package com.kelompok10.canteencare.core.network

import com.kelompok10.canteencare.data.remote.dto.AuthResponseDto
import com.kelompok10.canteencare.data.remote.dto.FcmTokenRequestDto
import com.kelompok10.canteencare.data.remote.dto.LoginRequestDto
import com.kelompok10.canteencare.data.remote.dto.MenuDto
import com.kelompok10.canteencare.data.remote.dto.MessageDto
import com.kelompok10.canteencare.data.remote.dto.RegisterRequestDto
import com.kelompok10.canteencare.data.remote.dto.UserDto
import com.kelompok10.canteencare.data.remote.dto.VendorDto
import retrofit2.http.Body
import retrofit2.http.GET
import retrofit2.http.POST
import retrofit2.http.PUT
import retrofit2.http.Path
import retrofit2.http.Query

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

    // ---------- Katalog ----------
    @GET("vendors")
    suspend fun getVendors(): List<VendorDto>

    @GET("vendors/{id}")
    suspend fun getVendor(@Path("id") vendorId: Int): VendorDto

    @GET("vendors/{id}/menus")
    suspend fun getVendorMenus(@Path("id") vendorId: Int): List<MenuDto>

    @GET("menus")
    suspend fun searchMenus(@Query("search") search: String): List<MenuDto>
}
