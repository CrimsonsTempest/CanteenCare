package com.kelompok10.canteencare.domain.model

/** Akun penjual beserta stannya (satu penjual = satu stan). */
data class Seller(
    val id: Int,
    val fullName: String,
    val email: String,
    val vendor: Vendor,
)

/** Isian form admin untuk membuat akun penjual baru sekaligus stannya. */
data class SellerDraft(
    val fullName: String,
    val email: String,
    val password: String,
    val vendorName: String,
    val vendorDescription: String?,
    val openHours: String?,
)

/** Data menu yang bisa diubah admin (selain gizi dan status). */
data class MenuEdit(
    val name: String,
    val description: String?,
    val category: MenuCategory,
    val price: Int,
    val imageUrl: String?,
)
