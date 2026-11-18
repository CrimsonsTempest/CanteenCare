package com.kelompok10.canteencare.data.remote.dto

import com.kelompok10.canteencare.domain.model.NutritionTarget

/** JSON target gizi: target_calories, target_carbs, ... (snake_case diatur Gson). */
data class NutritionTargetDto(
    val targetCalories: Double,
    val targetCarbs: Double,
    val targetProtein: Double,
    val targetFat: Double,
    val targetSalt: Double,
    val targetSugar: Double,
)

fun NutritionTargetDto.toDomain() = NutritionTarget(
    calories = targetCalories,
    carbs = targetCarbs,
    protein = targetProtein,
    fat = targetFat,
    salt = targetSalt,
    sugar = targetSugar,
)

fun NutritionTarget.toDto() = NutritionTargetDto(
    targetCalories = calories,
    targetCarbs = carbs,
    targetProtein = protein,
    targetFat = fat,
    targetSalt = salt,
    targetSugar = sugar,
)
