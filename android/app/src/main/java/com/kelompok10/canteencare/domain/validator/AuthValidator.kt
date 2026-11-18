package com.kelompok10.canteencare.domain.validator

/**
 * Validasi form Login dan Daftar di sisi aplikasi, sebelum request dikirim ke server.
 * Mengembalikan pesan error berbahasa Indonesia, atau null jika semua isian valid.
 */
object AuthValidator {
    const val MIN_PASSWORD_LENGTH = 6

    private val EMAIL_PATTERN = Regex("^[^@\\s]+@[^@\\s]+\\.[^@\\s]+$")

    fun validateLogin(email: String, password: String): String? = when {
        email.isBlank() -> "Email wajib diisi."
        !EMAIL_PATTERN.matches(email.trim()) -> "Format email tidak valid."
        password.isEmpty() -> "Password wajib diisi."
        else -> null
    }

    fun validateRegister(fullName: String, email: String, password: String, confirmPassword: String): String? =
        when {
            fullName.isBlank() -> "Nama lengkap wajib diisi."
            email.isBlank() -> "Email wajib diisi."
            !EMAIL_PATTERN.matches(email.trim()) -> "Format email tidak valid."
            password.length < MIN_PASSWORD_LENGTH -> "Password minimal $MIN_PASSWORD_LENGTH karakter."
            password != confirmPassword -> "Konfirmasi password tidak sama."
            else -> null
        }
}
