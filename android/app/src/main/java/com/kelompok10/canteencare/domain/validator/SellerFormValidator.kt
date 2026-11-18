package com.kelompok10.canteencare.domain.validator

/** Kolom form penjual baru yang wajib benar. */
enum class SellerField { FULL_NAME, EMAIL, PASSWORD, VENDOR_NAME }

/** Validasi form admin untuk membuat akun penjual + stan. */
object SellerFormValidator {

    /** Mengembalikan kolom-kolom yang isiannya salah; kosong berarti semua valid. */
    fun invalidFields(fullName: String, email: String, password: String, vendorName: String): Set<SellerField> =
        buildSet {
            if (fullName.isBlank()) add(SellerField.FULL_NAME)
            if (!AuthValidator.isValidEmail(email)) add(SellerField.EMAIL)
            if (password.length < AuthValidator.MIN_PASSWORD_LENGTH) add(SellerField.PASSWORD)
            if (vendorName.isBlank()) add(SellerField.VENDOR_NAME)
        }
}
