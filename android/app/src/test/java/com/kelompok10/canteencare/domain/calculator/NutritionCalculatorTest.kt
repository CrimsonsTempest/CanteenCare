package com.kelompok10.canteencare.domain.calculator

import com.kelompok10.canteencare.domain.model.CartItem
import com.kelompok10.canteencare.domain.model.Nutrition
import org.junit.Assert.assertEquals
import org.junit.Test

class NutritionCalculatorTest {

    // Data sama dengan seed backend
    private val mieAyamSolo = Nutrition(650.0, 85.0, 30.0, 25.0, 1800.0, 6.0)
    private val esTehManis = Nutrition(120.0, 30.0, 0.0, 0.0, 10.0, 30.0)

    private fun cartItem(menuId: Int, price: Int, quantity: Int, nutrition: Nutrition) = CartItem(
        menuId = menuId, vendorId = 1, vendorName = "Dapur Gapura", name = "Menu $menuId",
        price = price, stock = 50, quantity = quantity, nutrition = nutrition,
    )

    private val pesananContoh = listOf(
        cartItem(menuId = 1, price = 20000, quantity = 1, nutrition = mieAyamSolo),
        cartItem(menuId = 4, price = 4000, quantity = 2, nutrition = esTehManis),
    )

    // ---------- Subtotal ----------

    @Test
    fun `subtotal adalah harga dikali jumlah`() {
        assertEquals(40000, NutritionCalculator.calculateSubtotal(price = 20000, quantity = 2))
        assertEquals(0, NutritionCalculator.calculateSubtotal(price = 20000, quantity = 0))
    }

    // ---------- Fungsi 1: total harga ----------

    @Test
    fun `total harga menjumlahkan subtotal semua item`() {
        // 1 × 20.000 + 2 × 4.000 = 28.000
        assertEquals(28000, NutritionCalculator.calculateTotalPrice(pesananContoh))
    }

    @Test
    fun `total harga keranjang kosong adalah nol`() {
        assertEquals(0, NutritionCalculator.calculateTotalPrice(emptyList()))
    }

    @Test
    fun `item dengan jumlah nol tidak menambah total harga`() {
        val items = pesananContoh + cartItem(menuId = 2, price = 12000, quantity = 0, nutrition = mieAyamSolo)
        assertEquals(28000, NutritionCalculator.calculateTotalPrice(items))
    }

    // ---------- Fungsi 2: total gizi ----------

    @Test
    fun `total gizi adalah gizi per porsi dikali jumlah lalu dijumlahkan`() {
        val total = NutritionCalculator.calculateOrderNutrition(pesananContoh)

        assertEquals(650.0 + 2 * 120.0, total.calories, DELTA) // 890 kkal
        assertEquals(85.0 + 2 * 30.0, total.carbs, DELTA) // 145 g
        assertEquals(30.0, total.protein, DELTA)
        assertEquals(25.0, total.fat, DELTA)
        assertEquals(1800.0 + 2 * 10.0, total.salt, DELTA) // 1820 mg
        assertEquals(6.0 + 2 * 30.0, total.sugar, DELTA) // 66 g
    }

    @Test
    fun `total gizi berubah saat jumlah porsi berubah`() {
        val satuPorsi = NutritionCalculator.calculateOrderNutrition(listOf(cartItem(1, 20000, 1, mieAyamSolo)))
        val tigaPorsi = NutritionCalculator.calculateOrderNutrition(listOf(cartItem(1, 20000, 3, mieAyamSolo)))

        assertEquals(650.0, satuPorsi.calories, DELTA)
        assertEquals(1950.0, tigaPorsi.calories, DELTA)
    }

    @Test
    fun `total gizi keranjang kosong adalah nol semua`() {
        assertEquals(Nutrition.ZERO, NutritionCalculator.calculateOrderNutrition(emptyList()))
    }

    @Test
    fun `item dengan jumlah nol tidak menambah total gizi`() {
        val total = NutritionCalculator.calculateOrderNutrition(listOf(cartItem(1, 20000, 0, mieAyamSolo)))
        assertEquals(Nutrition.ZERO, total)
    }

    private companion object {
        const val DELTA = 0.001
    }
}
