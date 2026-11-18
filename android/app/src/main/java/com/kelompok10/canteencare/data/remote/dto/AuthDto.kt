package com.kelompok10.canteencare.data.remote.dto

import com.kelompok10.canteencare.domain.model.Role
import com.kelompok10.canteencare.domain.model.User

// DTO = bentuk JSON persis seperti yang dikirim/diterima backend.
// Nama properti camelCase otomatis diubah Gson menjadi snake_case (fullName ↔ full_name).

data class LoginRequestDto(val email: String, val password: String)

data class RegisterRequestDto(val fullName: String, val email: String, val password: String)

data class FcmTokenRequestDto(val fcmToken: String)

data class UserDto(
    val id: Int,
    val fullName: String,
    val email: String,
    val role: String,
    val vendorId: Int?,
    val vendorName: String?,
)

data class AuthResponseDto(
    val accessToken: String,
    val tokenType: String,
    val user: UserDto,
)

data class MessageDto(val message: String)

/** Mapper DTO → model domain yang dipakai di aplikasi. */
fun UserDto.toDomain(): User = User(
    id = id,
    fullName = fullName,
    email = email,
    role = Role.valueOf(role),
    vendorId = vendorId,
    vendorName = vendorName,
)
