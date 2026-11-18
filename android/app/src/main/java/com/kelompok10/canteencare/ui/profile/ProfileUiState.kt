package com.kelompok10.canteencare.ui.profile

import com.kelompok10.canteencare.domain.model.User

/** State layar Profil: sedang memuat, gagal (dengan pesan), atau berhasil (dengan data user). */
data class ProfileUiState(
    val isLoading: Boolean = true,
    val user: User? = null,
    val errorMessage: String? = null,
    val isLoggedOut: Boolean = false,
)
