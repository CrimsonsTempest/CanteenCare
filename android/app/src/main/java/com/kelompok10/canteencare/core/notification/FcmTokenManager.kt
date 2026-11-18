package com.kelompok10.canteencare.core.notification

import android.util.Log
import com.google.firebase.messaging.FirebaseMessaging
import com.kelompok10.canteencare.core.di.ApplicationScope
import com.kelompok10.canteencare.core.network.ApiResult
import com.kelompok10.canteencare.core.network.ApiService
import com.kelompok10.canteencare.core.network.safeApiCall
import com.kelompok10.canteencare.data.remote.dto.FcmTokenRequestDto
import kotlinx.coroutines.CancellationException
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.launch
import kotlinx.coroutines.suspendCancellableCoroutine
import javax.inject.Inject
import javax.inject.Singleton
import kotlin.coroutines.resume
import kotlin.coroutines.resumeWithException

/**
 * Mengirim token FCM perangkat ini ke backend, supaya server tahu ke HP mana notifikasi dikirim.
 * Berjalan di background; kalau gagal hanya dicatat di log dan tidak mengganggu user.
 */
@Singleton
class FcmTokenManager @Inject constructor(
    private val api: ApiService,
    @ApplicationScope private val appScope: CoroutineScope,
) {
    fun syncToken() {
        appScope.launch {
            try {
                val token = fetchToken()
                val result = safeApiCall { api.updateFcmToken(FcmTokenRequestDto(token)) }
                if (result is ApiResult.Error) Log.w(TAG, "Gagal mengirim token FCM: ${result.message}")
            } catch (e: CancellationException) {
                throw e
            } catch (e: Exception) {
                // Contoh: emulator tanpa Google Play Services
                Log.w(TAG, "Token FCM tidak bisa diambil", e)
            }
        }
    }

    /**
     * Mengubah API callback Firebase menjadi suspend function.
     * Firebase Messaging 26 menandai `token` sebagai usang dan menyarankan alur baru `register()` + FID.
     * Token lama masih berfungsi dan backend kita mengirim notifikasi berdasarkan token ini,
     * jadi aplikasi dan backend tetap sama-sama memakai token.
     */
    @Suppress("DEPRECATION")
    private suspend fun fetchToken(): String = suspendCancellableCoroutine { continuation ->
        FirebaseMessaging.getInstance().token.addOnCompleteListener { task ->
            if (task.isSuccessful) {
                continuation.resume(task.result)
            } else {
                continuation.resumeWithException(task.exception ?: IllegalStateException("Token FCM kosong"))
            }
        }
    }

    private companion object {
        const val TAG = "FcmTokenManager"
    }
}
