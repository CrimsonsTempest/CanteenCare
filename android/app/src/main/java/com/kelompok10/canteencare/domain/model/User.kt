package com.kelompok10.canteencare.domain.model

/** Data akun user. vendorId dan vendorName hanya terisi untuk PENJUAL. */
data class User(
    val id: Int,
    val fullName: String,
    val email: String,
    val role: Role,
    val vendorId: Int?,
    val vendorName: String?,
)
