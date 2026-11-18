package com.kelompok10.canteencare.ui.penjual.menunew

import com.kelompok10.canteencare.domain.model.MenuCategory
import com.kelompok10.canteencare.domain.model.Nutrient

/** Kolom form menu yang bisa salah isi (selain kolom gizi). */
enum class MenuField { NAME, PRICE, STOCK, IMAGE_URL }

/** State form Menu Baru. Semua isian disimpan sebagai teks dan baru divalidasi saat Ajukan ditekan. */
data class NewMenuUiState(
    val name: String = "",
    val description: String = "",
    val category: MenuCategory = MenuCategory.MAKANAN,
    val price: String = "",
    val stock: String = "",
    val imageUrl: String = "",
    val nutritionInputs: Map<Nutrient, String> = Nutrient.entries.associateWith { "" },
    val invalidFields: Set<MenuField> = emptySet(),
    val invalidNutrients: Set<Nutrient> = emptySet(),
    val isSaving: Boolean = false,
    val saveError: String? = null,
    val isSaved: Boolean = false,
)
