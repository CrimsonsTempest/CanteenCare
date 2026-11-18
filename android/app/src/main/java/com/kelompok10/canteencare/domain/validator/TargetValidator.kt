package com.kelompok10.canteencare.domain.validator

/** Validasi isian form target gizi. */
object TargetValidator {

    private val NUMBER_PATTERN = Regex("^\\d+([.,]\\d+)?$")

    /**
     * Ubah teks isian menjadi angka target.
     * Koma atau titik boleh dipakai sebagai desimal ("62,5" atau "62.5").
     * Mengembalikan null jika kosong, bukan angka, atau tidak lebih dari 0.
     */
    fun parse(text: String): Double? {
        val trimmed = text.trim()
        if (!NUMBER_PATTERN.matches(trimmed)) return null
        val value = trimmed.replace(',', '.').toDoubleOrNull() ?: return null
        return value.takeIf { it > 0 }
    }

    /** Angka untuk diisi ke form: 2150.0 → "2150", 62.5 → "62.5" (tanpa pemisah ribuan). */
    fun toInput(value: Double): String =
        if (value % 1.0 == 0.0) value.toLong().toString() else value.toString()
}
