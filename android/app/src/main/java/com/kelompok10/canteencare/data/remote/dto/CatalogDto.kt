package com.kelompok10.canteencare.data.remote.dto

import com.kelompok10.canteencare.domain.model.Menu
import com.kelompok10.canteencare.domain.model.MenuCategory
import com.kelompok10.canteencare.domain.model.MenuStatus
import com.kelompok10.canteencare.domain.model.Nutrition
import com.kelompok10.canteencare.domain.model.Vendor

data class VendorDto(
    val id: Int,
    val name: String,
    val description: String?,
    val imageUrl: String?,
    val openHours: String?,
    val isActive: Boolean,
)

data class NutritionDto(
    val calories: Double,
    val carbs: Double,
    val protein: Double,
    val fat: Double,
    val salt: Double,
    val sugar: Double,
    val isVerified: Boolean?,
)

data class MenuDto(
    val id: Int,
    val vendorId: Int,
    val vendorName: String,
    val name: String,
    val description: String?,
    val category: String,
    val price: Int,
    val stock: Int,
    val imageUrl: String?,
    val status: String,
    val nutrition: NutritionDto?,
)

fun VendorDto.toDomain() = Vendor(
    id = id,
    name = name,
    description = description,
    imageUrl = imageUrl,
    openHours = openHours,
)

fun NutritionDto.toDomain() = Nutrition(
    calories = calories,
    carbs = carbs,
    protein = protein,
    fat = fat,
    salt = salt,
    sugar = sugar,
)

fun MenuDto.toDomain() = Menu(
    id = id,
    vendorId = vendorId,
    vendorName = vendorName,
    name = name,
    description = description,
    category = MenuCategory.valueOf(category),
    price = price,
    stock = stock,
    imageUrl = imageUrl,
    status = MenuStatus.valueOf(status),
    nutrition = nutrition?.toDomain(),
    isNutritionVerified = nutrition?.isVerified == true,
)
