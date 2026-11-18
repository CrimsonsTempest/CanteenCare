package com.kelompok10.canteencare.domain.model

/**
 * Enam nilai gizi. Bisa berarti gizi per porsi, total satu pesanan, atau total satu hari.
 * Satuan: kalori kkal, garam mg natrium, sisanya gram.
 */
data class Nutrition(
    val calories: Double,
    val carbs: Double,
    val protein: Double,
    val fat: Double,
    val salt: Double,
    val sugar: Double,
) {
    /** Ambil nilai untuk satu jenis gizi, berguna saat menampilkan keenamnya dalam perulangan. */
    fun valueOf(nutrient: Nutrient): Double = when (nutrient) {
        Nutrient.CALORIES -> calories
        Nutrient.CARBS -> carbs
        Nutrient.PROTEIN -> protein
        Nutrient.FAT -> fat
        Nutrient.SALT -> salt
        Nutrient.SUGAR -> sugar
    }

    companion object {
        val ZERO = Nutrition(0.0, 0.0, 0.0, 0.0, 0.0, 0.0)
    }
}

/** Jenis gizi beserta label dan satuannya untuk ditampilkan. */
enum class Nutrient(val label: String, val unit: String) {
    CALORIES("Kalori", "kkal"),
    CARBS("Karbohidrat", "g"),
    PROTEIN("Protein", "g"),
    FAT("Lemak", "g"),
    SALT("Garam (natrium)", "mg"),
    SUGAR("Gula", "g"),
}
