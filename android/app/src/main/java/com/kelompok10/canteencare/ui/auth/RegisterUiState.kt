package com.kelompok10.canteencare.ui.auth

/** State layar Daftar. [isRegistered] menjadi tanda untuk pindah ke Beranda pembeli. */
data class RegisterUiState(
    val fullName: String = "",
    val email: String = "",
    val password: String = "",
    val confirmPassword: String = "",
    val isLoading: Boolean = false,
    val errorMessage: String? = null,
    val isRegistered: Boolean = false,
)
