package com.kelompok10.canteencare.domain.model

import java.time.OffsetDateTime

enum class OrderStatus(val label: String) {
    MENUNGGU("Menunggu"),
    DIKONFIRMASI("Dikonfirmasi"),
    DITOLAK("Ditolak"),
}

enum class PaymentMethod(val label: String) {
    TUNAI("Tunai"),
    QRIS("QRIS"),
}

/** Item pesanan: snapshot nama, harga, dan gizi per porsi saat pesanan dibuat. */
data class OrderItem(
    val menuId: Int,
    val menuName: String,
    override val price: Int,
    override val quantity: Int,
    override val nutrition: Nutrition,
) : LineItem

/**
 * Pesanan. Sengaja TIDAK menyimpan total harga dan total gizi dari server:
 * total selalu dihitung ulang di aplikasi oleh NutritionCalculator dari [items].
 */
data class Order(
    val id: Int,
    val orderNumber: String,
    val buyerName: String,
    val vendorId: Int,
    val vendorName: String,
    val status: OrderStatus,
    val paymentMethod: PaymentMethod,
    val note: String?,
    val rejectionReason: String?,
    val createdAt: OffsetDateTime,
    val resolvedAt: OffsetDateTime?,
    val items: List<OrderItem>,
)
