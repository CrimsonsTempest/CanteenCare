package com.kelompok10.canteencare.ui.penjual.orders

import com.kelompok10.canteencare.domain.model.Order
import com.kelompok10.canteencare.domain.model.OrderStatus

/**
 * State layar Pesanan Masuk. Semua pesanan stan diambil sekaligus, lalu dibagi per tab,
 * sehingga badge jumlah pesanan menunggu selalu akurat di tab mana pun.
 */
data class SellerOrdersUiState(
    val selectedTab: OrderStatus = OrderStatus.MENUNGGU,
    val orders: List<Order> = emptyList(),
    val isLoading: Boolean = true,
    val isRefreshing: Boolean = false,
    val errorMessage: String? = null,
) {
    val visibleOrders: List<Order> get() = orders.filter { it.status == selectedTab }
    val pendingCount: Int get() = orders.count { it.status == OrderStatus.MENUNGGU }
}
