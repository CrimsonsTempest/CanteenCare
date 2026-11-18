package com.kelompok10.canteencare.data.repository

import com.kelompok10.canteencare.core.network.ApiResult
import com.kelompok10.canteencare.domain.model.CartItem
import com.kelompok10.canteencare.domain.model.Order
import com.kelompok10.canteencare.domain.model.PaymentMethod

/** Pesanan milik pembeli. */
interface OrderRepository {
    /** Kirim isi keranjang sebagai pesanan baru (status MENUNGGU). */
    suspend fun placeOrder(
        vendorId: Int,
        paymentMethod: PaymentMethod,
        note: String?,
        items: List<CartItem>,
    ): ApiResult<Order>

    suspend fun getOrder(orderId: Int): ApiResult<Order>
}
