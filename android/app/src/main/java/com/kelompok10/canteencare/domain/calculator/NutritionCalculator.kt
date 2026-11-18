package com.kelompok10.canteencare.domain.calculator

import com.kelompok10.canteencare.domain.model.LineItem
import com.kelompok10.canteencare.domain.model.Nutrition

/**
 * Inti logika CanteenCare. Semua perhitungan Fungsi 1, 2, dan 3 di aplikasi HANYA ada di sini.
 *
 * Kelas ini murni Kotlin tanpa ketergantungan Android, sehingga mudah diuji dengan JUnit
 * dan dipakai oleh ViewModel mana pun (keranjang, detail pesanan, gizi harian).
 */
object NutritionCalculator {

    /** Subtotal satu baris: harga satuan × jumlah porsi. */
    fun calculateSubtotal(price: Int, quantity: Int): Int = price * quantity

    /**
     * Fungsi 1 — Total harga pesanan.
     * Rumus: Σ (harga × jumlah) untuk setiap item.
     */
    fun calculateTotalPrice(items: List<LineItem>): Int =
        items.sumOf { item -> calculateSubtotal(item.price, item.quantity) }

    /**
     * Fungsi 2 — Total gizi pesanan.
     * Untuk setiap jenis gizi: Σ (gizi per porsi × jumlah porsi).
     * Contoh: 1 Mie Ayam (650 kkal) + 2 Es Teh (120 kkal) = 650 + 2 × 120 = 890 kkal.
     */
    fun calculateOrderNutrition(items: List<LineItem>): Nutrition = Nutrition(
        calories = items.sumOf { it.nutrition.calories * it.quantity },
        carbs = items.sumOf { it.nutrition.carbs * it.quantity },
        protein = items.sumOf { it.nutrition.protein * it.quantity },
        fat = items.sumOf { it.nutrition.fat * it.quantity },
        salt = items.sumOf { it.nutrition.salt * it.quantity },
        sugar = items.sumOf { it.nutrition.sugar * it.quantity },
    )
}
