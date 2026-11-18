package com.kelompok10.canteencare.core.network

import com.kelompok10.canteencare.data.remote.dto.AuthResponseDto
import com.kelompok10.canteencare.data.remote.dto.FcmTokenRequestDto
import com.kelompok10.canteencare.data.remote.dto.LoginRequestDto
import com.kelompok10.canteencare.data.remote.dto.MessageDto
import com.kelompok10.canteencare.data.remote.dto.RegisterRequestDto
import com.kelompok10.canteencare.data.remote.dto.UserDto
import retrofit2.http.Body
import retrofit2.http.GET
import retrofit2.http.POST
import retrofit2.http.PUT

/**
 * Daftar endpoint backend untuk Retrofit (base URL: BuildConfig.BASE_URL).
 * Setiap fungsi adalah `suspend fun` sehingga dipanggil dari coroutine.
 * Endpoint ditambahkan bertahap sesuai fase.
 */
interface ApiService {

    // ---------- Auth ----------
    @POST("auth/login")
    suspend fun login(@Body body: LoginRequestDto): AuthResponseDto

    @POST("auth/register")
    suspend fun register(@Body body: RegisterRequestDto): AuthResponseDto

    @GET("auth/me")
    suspend fun getMe(): UserDto

    @PUT("users/me/fcm-token")
    suspend fun updateFcmToken(@Body body: FcmTokenRequestDto): MessageDto
}
