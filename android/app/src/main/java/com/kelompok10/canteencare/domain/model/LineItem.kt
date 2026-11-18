package com.kelompok10.canteencare.domain.model

/**
 * Satu baris belanja: harga satuan, jumlah porsi, dan gizi per porsi.
 * Dipakai bersama oleh item keranjang ([CartItem]) dan item pesanan ([OrderItem]),
 * sehingga NutritionCalculator cukup punya satu rumus untuk keduanya.
 */
interface LineItem {
    val price: Int
    val quantity: Int
    val nutrition: Nutrition
}
