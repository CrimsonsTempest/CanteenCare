package com.kelompok10.canteencare.data.repository

import com.kelompok10.canteencare.core.database.CartDao
import com.kelompok10.canteencare.core.database.CartItemEntity
import com.kelompok10.canteencare.domain.model.CartItem
import com.kelompok10.canteencare.domain.model.Menu
import com.kelompok10.canteencare.domain.model.Nutrition
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.map
import javax.inject.Inject
import javax.inject.Singleton

@Singleton
class DefaultCartRepository @Inject constructor(
    private val cartDao: CartDao,
) : CartRepository {

    override fun observeCart(): Flow<List<CartItem>> =
        cartDao.observeAll().map { entities -> entities.map { it.toDomain() } }

    override suspend fun addItem(menu: Menu, quantity: Int): AddToCartResult {
        val current = cartDao.getAll()

        // Satu keranjang hanya boleh berisi menu dari satu stan
        current.firstOrNull { it.vendorId != menu.vendorId }?.let { other ->
            return AddToCartResult.DifferentVendor(other.vendorName)
        }

        // Jika menu sudah ada, jumlahnya ditambah, tapi tidak boleh melebihi stok
        val existingQuantity = current.firstOrNull { it.menuId == menu.id }?.quantity ?: 0
        val newQuantity = (existingQuantity + quantity).coerceAtMost(menu.stock)
        cartDao.upsert(menu.toEntity(newQuantity))
        return AddToCartResult.Added(newQuantity)
    }

    override suspend fun replaceCartWith(menu: Menu, quantity: Int) {
        cartDao.replaceAll(menu.toEntity(quantity.coerceAtMost(menu.stock)))
    }
}

private fun Menu.toEntity(quantity: Int): CartItemEntity {
    val n = nutrition ?: Nutrition.ZERO
    return CartItemEntity(
        menuId = id,
        vendorId = vendorId,
        vendorName = vendorName,
        name = name,
        price = price,
        stock = stock,
        quantity = quantity,
        calories = n.calories,
        carbs = n.carbs,
        protein = n.protein,
        fat = n.fat,
        salt = n.salt,
        sugar = n.sugar,
    )
}

private fun CartItemEntity.toDomain() = CartItem(
    menuId = menuId,
    vendorId = vendorId,
    vendorName = vendorName,
    name = name,
    price = price,
    stock = stock,
    quantity = quantity,
    nutrition = Nutrition(calories, carbs, protein, fat, salt, sugar),
)
