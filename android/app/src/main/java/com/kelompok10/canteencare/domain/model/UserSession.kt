package com.kelompok10.canteencare.domain.model

/** Data user yang sedang login, disimpan di DataStore agar tetap login setelah aplikasi ditutup. */
data class UserSession(
    val token: String,
    val userId: Int,
    val fullName: String,
    val role: Role,
)
