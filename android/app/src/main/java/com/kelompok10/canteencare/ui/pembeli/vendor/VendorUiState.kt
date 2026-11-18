package com.kelompok10.canteencare.ui.pembeli.vendor

import com.kelompok10.canteencare.domain.model.Menu
import com.kelompok10.canteencare.domain.model.MenuCategory
import com.kelompok10.canteencare.domain.model.Vendor

/**
 * State layar Stan. Menu yang sedang dibuka di bottom sheet, jumlahnya, dan dialog konfirmasi
 * semuanya ada di sini, jadi tetap utuh saat layar diputar.
 */
data class VendorUiState(
    val isLoading: Boolean = true,
    val errorMessage: String? = null,
    val vendor: Vendor? = null,
    val menus: List<Menu> = emptyList(),
    /** null berarti chip "Semua". */
    val selectedCategory: MenuCategory? = null,
    val cartCount: Int = 0,
    // Bottom sheet detail menu
    val selectedMenuId: Int? = null,
    val quantity: Int = 1,
    /** Terisi jika keranjang berisi menu stan lain, untuk menampilkan dialog konfirmasi. */
    val replaceCartVendorName: String? = null,
    /** Pesan singkat untuk snackbar, misalnya "Mie Ayam Solo ditambahkan ke keranjang". */
    val message: String? = null,
) {
    val visibleMenus: List<Menu>
        get() = if (selectedCategory == null) menus else menus.filter { it.category == selectedCategory }

    val selectedMenu: Menu? get() = menus.firstOrNull { it.id == selectedMenuId }
}
