package com.kelompok10.canteencare.data.repository

import com.kelompok10.canteencare.core.network.ApiResult
import com.kelompok10.canteencare.domain.model.User
import com.kelompok10.canteencare.domain.model.UserSession
import kotlinx.coroutines.flow.Flow

/** Semua urusan akun: login, daftar, sesi, dan logout. */
interface AuthRepository {
    /** Sesi tersimpan; memancarkan null setelah logout atau sesi berakhir. */
    fun observeSession(): Flow<UserSession?>

    suspend fun login(email: String, password: String): ApiResult<User>

    suspend fun register(fullName: String, email: String, password: String): ApiResult<User>

    /** Data akun terbaru dari server (dipakai layar Profil). */
    suspend fun getProfile(): ApiResult<User>

    /** Hapus sesi dan keranjang, karena keranjang milik user yang sedang login. */
    suspend fun logout()
}
