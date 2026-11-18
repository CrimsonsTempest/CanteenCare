package com.kelompok10.canteencare.domain.model

enum class MenuCategory(val label: String) {
    MAKANAN("Makanan"),
    MINUMAN("Minuman"),
}

/** Status menu. Hanya APPROVED yang tampil untuk pembeli. */
enum class MenuStatus(val label: String) {
    PENDING("Menunggu verifikasi"),
    APPROVED("Tampil"),
    REJECTED("Ditolak admin"),
    ARCHIVED("Diarsipkan"),
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
    val status: MenuStatus,
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

/** Isian form menu baru dari penjual, sebelum dikirim ke server. [nutrition] adalah estimasi per porsi. */
data class MenuDraft(
    val name: String,
    val description: String?,
    val category: MenuCategory,
    val price: Int,
    val stock: Int,
    val imageUrl: String?,
    val nutrition: Nutrition,
)
