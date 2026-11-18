package com.kelompok10.canteencare.data.repository

import com.kelompok10.canteencare.core.network.ApiResult
import com.kelompok10.canteencare.core.network.ApiService
import com.kelompok10.canteencare.core.network.safeApiCall
import com.kelompok10.canteencare.data.remote.dto.CreateOrderRequestDto
import com.kelompok10.canteencare.data.remote.dto.OrderItemRequestDto
import com.kelompok10.canteencare.data.remote.dto.toDomain
import com.kelompok10.canteencare.domain.model.CartItem
import com.kelompok10.canteencare.domain.model.Order
import com.kelompok10.canteencare.domain.model.OrderStatus
import com.kelompok10.canteencare.domain.model.PaymentMethod
import javax.inject.Inject
import javax.inject.Singleton

@Singleton
class DefaultOrderRepository @Inject constructor(
    private val api: ApiService,
) : OrderRepository {

    override suspend fun placeOrder(
        vendorId: Int,
        paymentMethod: PaymentMethod,
        note: String?,
        items: List<CartItem>,
    ): ApiResult<Order> = safeApiCall {
        val body = CreateOrderRequestDto(
            vendorId = vendorId,
            paymentMethod = paymentMethod.name,
            note = note?.trim()?.takeIf { it.isNotEmpty() },
            // Hanya id menu dan jumlah yang dikirim; harga dan gizi diambil server dari database
            items = items.map { OrderItemRequestDto(menuId = it.menuId, quantity = it.quantity) },
        )
        api.createOrder(body).toDomain()
    }

    override suspend fun getOrder(orderId: Int): ApiResult<Order> =
        safeApiCall { api.getOrder(orderId).toDomain() }

    override suspend fun getMyOrders(status: OrderStatus?): ApiResult<List<Order>> =
        safeApiCall { api.getMyOrders(status?.name).map { it.toDomain() } }
}
