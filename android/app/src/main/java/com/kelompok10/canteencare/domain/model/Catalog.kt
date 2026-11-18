package com.kelompok10.canteencare.domain.model

enum class MenuCategory(val label: String) {
    MAKANAN("Makanan"),
    MINUMAN("Minuman"),
}

/** Stan kantin. Satu penjual memiliki satu stan. */
data class Vendor(
    val id: Int,
    val name: String,
    val description: String?,
    val imageUrl: String?,
    val openHours: String?,
)

/** Menu di sebuah stan. [nutrition] adalah gizi untuk SATU porsi. */
data class Menu(
    val id: Int,
    val vendorId: Int,
    val vendorName: String,
    val name: String,
    val description: String?,
    val category: MenuCategory,
    val price: Int,
    val stock: Int,
    val imageUrl: String?,
    val status: String,
    val nutrition: Nutrition?,
    val isNutritionVerified: Boolean,
) {
    val isAvailable: Boolean get() = stock > 0
}

/** Satu baris keranjang: menu yang dipilih beserta jumlahnya. [nutrition] per SATU porsi. */
data class CartItem(
    val menuId: Int,
    val vendorId: Int,
    val vendorName: String,
    val name: String,
    override val price: Int,
    val stock: Int,
    override val quantity: Int,
    override val nutrition: Nutrition,
) : LineItem
