package com.kelompok10.canteencare.ui.auth

import com.kelompok10.canteencare.domain.model.Role

/**
 * Semua state layar Login. Disimpan di ViewModel, jadi isian form tidak hilang saat layar diputar.
 * [loggedInRole] terisi setelah login berhasil, menjadi tanda untuk pindah ke home sesuai peran.
 */
data class LoginUiState(
    val email: String = "",
    val password: String = "",
    val isLoading: Boolean = false,
    val errorMessage: String? = null,
    val loggedInRole: Role? = null,
)
