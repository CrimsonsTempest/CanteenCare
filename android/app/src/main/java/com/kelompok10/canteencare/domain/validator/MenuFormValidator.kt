package com.kelompok10.canteencare.domain.validator

/** Aturan isian form menu (penjual dan admin) serta edit stok. */
object MenuFormValidator {

    private val INT_PATTERN = Regex("^\\d+$")
    private val DECIMAL_PATTERN = Regex("^\\d+([.,]\\d+)?$")
    private const val MAX_INT_DIGITS = 9 // cegah angka terlalu besar untuk tipe Int

    /** Harga atau stok: bilangan bulat 0 atau lebih ("20000" → 20000). Selain itu null. */
    fun parseNonNegativeInt(text: String): Int? {
        val trimmed = text.trim()
        if (!INT_PATTERN.matches(trimmed) || trimmed.length > MAX_INT_DIGITS) return null
        return trimmed.toInt()
    }

    /** Nilai gizi per porsi: angka 0 atau lebih, koma/titik sebagai desimal ("12,5" → 12.5). */
    fun parseNonNegativeDecimal(text: String): Double? {
        val trimmed = text.trim()
        if (!DECIMAL_PATTERN.matches(trimmed)) return null
        return trimmed.replace(',', '.').toDoubleOrNull()
    }

    /** URL gambar boleh kosong; jika diisi harus diawali http:// atau https://. */
    fun isValidImageUrl(text: String): Boolean {
        val trimmed = text.trim()
        return trimmed.isEmpty() || trimmed.startsWith("http://") || trimmed.startsWith("https://")
    }
}
