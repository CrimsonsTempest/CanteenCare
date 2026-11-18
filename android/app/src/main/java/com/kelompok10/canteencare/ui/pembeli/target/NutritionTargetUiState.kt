package com.kelompok10.canteencare.ui.pembeli.target

import com.kelompok10.canteencare.domain.model.Nutrient

/**
 * State form Target Gizi. Isian disimpan sebagai teks (apa adanya yang diketik user),
 * baru diubah menjadi angka saat tombol Simpan ditekan.
 */
data class NutritionTargetUiState(
    val isLoading: Boolean = true,
    val loadError: String? = null,
    val inputs: Map<Nutrient, String> = emptyMap(),
    /** Gizi yang isiannya tidak valid (kosong, bukan angka, atau ≤ 0). */
    val invalidFields: Set<Nutrient> = emptySet(),
    val isSaving: Boolean = false,
    val saveError: String? = null,
    val isSaved: Boolean = false,
)
