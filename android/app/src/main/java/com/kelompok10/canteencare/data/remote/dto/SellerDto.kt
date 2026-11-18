package com.kelompok10.canteencare.data.remote.dto

import com.kelompok10.canteencare.domain.model.MenuDraft

/** Enam nilai gizi untuk dikirim ke server (tanpa status verifikasi). */
data class NutritionRequestDto(
    val calories: Double,
    val carbs: Double,
    val protein: Double,
    val fat: Double,
    val salt: Double,
    val sugar: Double,
)

/** Body POST /seller/menus: menu baru dengan estimasi gizi dari penjual. */
data class CreateMenuRequestDto(
    val name: String,
    val description: String?,
    val category: String,
    val price: Int,
    val stock: Int,
    val imageUrl: String?,
    val nutrition: NutritionRequestDto,
)

data class StockRequestDto(val stock: Int)

data class RejectRequestDto(val reason: String)

fun MenuDraft.toRequest() = CreateMenuRequestDto(
    name = name,
    description = description,
    category = category.name,
    price = price,
    stock = stock,
    imageUrl = imageUrl,
    nutrition = nutrition.toRequest(),
)
