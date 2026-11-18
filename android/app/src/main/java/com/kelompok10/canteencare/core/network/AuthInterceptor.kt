package com.kelompok10.canteencare.core.network

import com.kelompok10.canteencare.core.datastore.SessionDataStore
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.runBlocking
import okhttp3.Interceptor
import okhttp3.Response
import javax.inject.Inject

/**
 * Menempelkan header "Authorization: Bearer <token>" ke setiap request jika user sudah login.
 * OkHttp menjalankan interceptor di thread background, jadi runBlocking aman dipakai di sini.
 */
class AuthInterceptor @Inject constructor(
    private val sessionDataStore: SessionDataStore,
) : Interceptor {
    override fun intercept(chain: Interceptor.Chain): Response {
        val token = runBlocking { sessionDataStore.token.first() }
        val request = chain.request().newBuilder().apply {
            if (token != null) header("Authorization", "Bearer $token")
        }.build()
        return chain.proceed(request)
    }
}
