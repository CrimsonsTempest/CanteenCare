package com.kelompok10.canteencare.core.notification

import android.content.Context
import android.util.Log
import com.google.firebase.FirebaseApp
import com.google.firebase.messaging.FirebaseMessaging
import com.kelompok10.canteencare.core.datastore.SessionDataStore
import com.kelompok10.canteencare.core.di.ApplicationScope
import com.kelompok10.canteencare.core.network.ApiResult
import com.kelompok10.canteencare.core.network.ApiService
import com.kelompok10.canteencare.core.network.safeApiCall
import com.kelompok10.canteencare.data.remote.dto.FcmTokenRequestDto
import dagger.hilt.android.qualifiers.ApplicationContext
import kotlinx.coroutines.CancellationException
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.launch
import kotlinx.coroutines.suspendCancellableCoroutine
import javax.inject.Inject
import javax.inject.Singleton
import kotlin.coroutines.resume
import kotlin.coroutines.resumeWithException

/**
 * Mengirim token FCM perangkat ini ke backend, supaya server tahu ke HP mana notifikasi dikirim.
 * Berjalan di background; kalau gagal hanya dicatat di log dan tidak mengganggu user.
 * Jika Firebase tidak dipasang (tanpa google-services.json), proses ini dilewati.
 */
@Singleton
class FcmTokenManager @Inject constructor(
    @ApplicationContext private val context: Context,
    private val api: ApiService,
    private val sessionDataStore: SessionDataStore,
    @ApplicationScope private val appScope: CoroutineScope,
) {
    /**
     * Kirim token ke backend. [newToken] diisi saat Firebase memberi token baru (onNewToken);
     * jika null, token saat ini diambil dari Firebase (setelah login atau saat aplikasi dibuka).
     */
    fun syncToken(newToken: String? = null) {
        // Firebase hanya aktif jika google-services.json ada saat build
        if (FirebaseApp.getApps(context).isEmpty()) {
            Log.i(TAG, "Firebase tidak dipasang, notifikasi dinonaktifkan")
            return
        }
        appScope.launch {
            try {
                // Token hanya bisa disimpan untuk user yang sedang login
                if (sessionDataStore.token.first() == null) return@launch
                val token = newToken ?: fetchToken()
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
