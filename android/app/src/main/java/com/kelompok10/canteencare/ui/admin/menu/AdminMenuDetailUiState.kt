package com.kelompok10.canteencare.ui.admin.menu

import com.kelompok10.canteencare.domain.model.Menu
import com.kelompok10.canteencare.domain.model.MenuCategory
import com.kelompok10.canteencare.domain.model.MenuStatus
import com.kelompok10.canteencare.domain.model.Nutrient
import com.kelompok10.canteencare.domain.validator.MenuField

/**
 * State layar detail menu untuk admin. Ada dua form terpisah:
 * data menu (nama, harga, dll.) dan gizi per porsi (disimpan sekaligus diverifikasi).
 */
data class AdminMenuDetailUiState(
    val isLoading: Boolean = true,
    val loadError: String? = null,
    val menu: Menu? = null,
    // Form data menu
    val name: String = "",
    val description: String = "",
    val category: MenuCategory = MenuCategory.MAKANAN,
    val price: String = "",
    val imageUrl: String = "",
    val isUploadingImage: Boolean = false,
    val imageError: String? = null,
    val invalidFields: Set<MenuField> = emptySet(),
    // Form gizi
    val nutritionInputs: Map<Nutrient, String> = emptyMap(),
    val invalidNutrients: Set<Nutrient> = emptySet(),
    // Proses
    val isSavingMenu: Boolean = false,
    val isSavingNutrition: Boolean = false,
    val isChangingStatus: Boolean = false,
    val showRejectDialog: Boolean = false,
    val message: String? = null,
) {
    /** Setujui hanya boleh jika gizi sudah diverifikasi admin (aturan yang sama dicek server). */
    val canApprove: Boolean
        get() = menu != null && menu.isNutritionVerified &&
            menu.status in setOf(MenuStatus.PENDING, MenuStatus.REJECTED) && !isChangingStatus

    val canReject: Boolean
        get() = menu != null && menu.status in setOf(MenuStatus.PENDING, MenuStatus.APPROVED) && !isChangingStatus
}
