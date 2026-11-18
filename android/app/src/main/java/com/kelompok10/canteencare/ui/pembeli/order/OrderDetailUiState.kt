package com.kelompok10.canteencare.ui.pembeli.order

import com.kelompok10.canteencare.domain.model.Nutrition
import com.kelompok10.canteencare.domain.model.Order

/** State layar Detail Pesanan. Total dihitung NutritionCalculator di ViewModel, bukan diambil dari server. */
data class OrderDetailUiState(
    val isLoading: Boolean = true,
    val errorMessage: String? = null,
    val order: Order? = null,
    val totalPrice: Int = 0,
    val totalNutrition: Nutrition = Nutrition.ZERO,
)
