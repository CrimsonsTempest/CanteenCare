package com.kelompok10.canteencare.core.database

import androidx.room.Entity
import androidx.room.PrimaryKey

/**
 * Satu baris keranjang di database Room.
 * Gizi disimpan per SATU porsi; total dihitung oleh NutritionCalculator (Phase 7).
 */
@Entity(tableName = "cart_items")
data class CartItemEntity(
    @PrimaryKey val menuId: Int,
    val vendorId: Int,
    val vendorName: String,
    val name: String,
    val price: Int,
    val stock: Int,
    val quantity: Int,
    val calories: Double,
    val carbs: Double,
    val protein: Double,
    val fat: Double,
    val salt: Double,
    val sugar: Double,
)
