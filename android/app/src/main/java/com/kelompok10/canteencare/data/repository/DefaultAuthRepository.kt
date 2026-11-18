package com.kelompok10.canteencare.data.repository

import com.kelompok10.canteencare.core.database.CartDao
import com.kelompok10.canteencare.core.datastore.SessionDataStore
import com.kelompok10.canteencare.core.network.ApiResult
import com.kelompok10.canteencare.core.network.ApiService
import com.kelompok10.canteencare.core.network.safeApiCall
import com.kelompok10.canteencare.core.notification.FcmTokenManager
import com.kelompok10.canteencare.data.remote.dto.AuthResponseDto
import com.kelompok10.canteencare.data.remote.dto.LoginRequestDto
import com.kelompok10.canteencare.data.remote.dto.RegisterRequestDto
import com.kelompok10.canteencare.data.remote.dto.toDomain
import com.kelompok10.canteencare.domain.model.User
import com.kelompok10.canteencare.domain.model.UserSession
import kotlinx.coroutines.flow.Flow
import javax.inject.Inject
import javax.inject.Singleton

@Singleton
class DefaultAuthRepository @Inject constructor(
    private val api: ApiService,
    private val sessionDataStore: SessionDataStore,
    private val cartDao: CartDao,
    private val fcmTokenManager: FcmTokenManager,
) : AuthRepository {

    override fun observeSession(): Flow<UserSession?> = sessionDataStore.session

    override suspend fun login(email: String, password: String): ApiResult<User> =
        saveSessionIfSuccess(safeApiCall { api.login(LoginRequestDto(email, password)) })

    override suspend fun register(fullName: String, email: String, password: String): ApiResult<User> =
        saveSessionIfSuccess(safeApiCall { api.register(RegisterRequestDto(fullName, email, password)) })

    override suspend fun getProfile(): ApiResult<User> =
        when (val result = safeApiCall { api.getMe() }) {
            is ApiResult.Success -> ApiResult.Success(result.data.toDomain())
            is ApiResult.Error -> result
        }

    override suspend fun logout() {
        cartDao.clear()
        sessionDataStore.clear()
    }

    /** Jika login/daftar berhasil: simpan sesi ke DataStore lalu kirim token FCM ke server. */
    private suspend fun saveSessionIfSuccess(result: ApiResult<AuthResponseDto>): ApiResult<User> =
        when (result) {
            is ApiResult.Success -> {
                val user = result.data.user.toDomain()
                // Keranjang user sebelumnya (jika ada) tidak boleh terbawa ke akun baru
                cartDao.clear()
                sessionDataStore.save(
                    UserSession(
                        token = result.data.accessToken,
                        userId = user.id,
                        fullName = user.fullName,
                        role = user.role,
                    ),
                )
                fcmTokenManager.syncToken()
                ApiResult.Success(user)
            }
            is ApiResult.Error -> result
        }
}
