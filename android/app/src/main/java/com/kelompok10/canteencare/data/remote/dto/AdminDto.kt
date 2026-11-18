package com.kelompok10.canteencare.data.remote.dto

import com.kelompok10.canteencare.domain.model.MenuEdit
import com.kelompok10.canteencare.domain.model.Nutrition
import com.kelompok10.canteencare.domain.model.Seller
import com.kelompok10.canteencare.domain.model.SellerDraft

data class SellerDto(
    val id: Int,
    val fullName: String,
    val email: String,
    val vendor: VendorDto,
)

/** Body POST /admin/sellers: akun penjual dan stannya dibuat dalam satu request. */
data class CreateSellerRequestDto(
    val fullName: String,
    val email: String,
    val password: String,
    val vendorName: String,
    val vendorDescription: String?,
    val openHours: String?,
)

/** Body PUT /admin/menus/{id}. */
data class UpdateMenuRequestDto(
    val name: String,
    val description: String?,
    val category: String,
    val price: Int,
    val imageUrl: String?,
)

fun SellerDto.toDomain() = Seller(id = id, fullName = fullName, email = email, vendor = vendor.toDomain())

fun SellerDraft.toRequest() = CreateSellerRequestDto(
    fullName = fullName,
    email = email,
    password = password,
    vendorName = vendorName,
    vendorDescription = vendorDescription,
    openHours = openHours,
)

fun MenuEdit.toRequest() = UpdateMenuRequestDto(
    name = name,
    description = description,
    category = category.name,
    price = price,
    imageUrl = imageUrl,
)

fun Nutrition.toRequest() = NutritionRequestDto(
    calories = calories,
    carbs = carbs,
    protein = protein,
    fat = fat,
    salt = salt,
    sugar = sugar,
)
