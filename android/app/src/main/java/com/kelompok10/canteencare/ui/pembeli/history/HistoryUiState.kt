package com.kelompok10.canteencare.ui.pembeli.history

import com.kelompok10.canteencare.domain.model.Order
import com.kelompok10.canteencare.domain.model.OrderStatus

/**
 * State layar Riwayat.
 * [isLoading] untuk muat pertama (layar penuh), [isRefreshing] untuk tarik-untuk-refresh.
 */
data class HistoryUiState(
    /** null berarti chip "Semua". */
    val selectedStatus: OrderStatus? = null,
    val orders: List<Order> = emptyList(),
    val isLoading: Boolean = true,
    val isRefreshing: Boolean = false,
    val errorMessage: String? = null,
)
