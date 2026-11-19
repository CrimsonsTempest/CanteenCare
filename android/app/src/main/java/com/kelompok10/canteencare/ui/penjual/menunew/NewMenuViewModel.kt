package com.kelompok10.canteencare.ui.penjual.menunew

import android.net.Uri
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.kelompok10.canteencare.core.network.ApiResult
import com.kelompok10.canteencare.data.repository.ImageRepository
import com.kelompok10.canteencare.data.repository.SellerRepository
import com.kelompok10.canteencare.domain.model.MenuCategory
import com.kelompok10.canteencare.domain.model.MenuDraft
import com.kelompok10.canteencare.domain.model.Nutrient
import com.kelompok10.canteencare.domain.validator.MenuField
import com.kelompok10.canteencare.domain.model.Nutrition
import com.kelompok10.canteencare.domain.validator.MenuFormValidator
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class NewMenuViewModel @Inject constructor(
    private val sellerRepository: SellerRepository,
    private val imageRepository: ImageRepository,
) : ViewModel() {

    private val _uiState = MutableStateFlow(NewMenuUiState())
    val uiState: StateFlow<NewMenuUiState> = _uiState.asStateFlow()

    fun onNameChange(value: String) = _uiState.update { it.copy(name = value, invalidFields = it.invalidFields - MenuField.NAME) }

    fun onDescriptionChange(value: String) = _uiState.update { it.copy(description = value) }

    fun onCategoryChange(value: MenuCategory) = _uiState.update { it.copy(category = value) }

    fun onPriceChange(value: String) = _uiState.update { it.copy(price = value, invalidFields = it.invalidFields - MenuField.PRICE) }

    fun onStockChange(value: String) = _uiState.update { it.copy(stock = value, invalidFields = it.invalidFields - MenuField.STOCK) }

    /** Foto dipilih dari galeri: langsung di-upload, lalu path dari server disimpan di state. */
    fun onImagePicked(uri: Uri) {
        viewModelScope.launch {
            _uiState.update { it.copy(isUploadingImage = true, imageError = null) }
            when (val result = imageRepository.uploadMenuImage(uri)) {
                is ApiResult.Success -> _uiState.update { it.copy(isUploadingImage = false, imageUrl = result.data) }
                is ApiResult.Error -> _uiState.update { it.copy(isUploadingImage = false, imageError = result.message) }
            }
        }
    }

    fun onImageRemoved() = _uiState.update { it.copy(imageUrl = "", imageError = null) }

    fun onNutritionChange(nutrient: Nutrient, value: String) = _uiState.update {
        it.copy(nutritionInputs = it.nutritionInputs + (nutrient to value), invalidNutrients = it.invalidNutrients - nutrient)
    }

    fun submit() {
        val state = _uiState.value
        // Tunggu upload gambar selesai supaya menu tidak tersimpan tanpa gambarnya
        if (state.isSaving || state.isUploadingImage) return

        // Validasi semua kolom sekaligus, supaya penjual melihat semua yang salah dalam satu kali tekan
        val price = MenuFormValidator.parseNonNegativeInt(state.price)
        val stock = MenuFormValidator.parseNonNegativeInt(state.stock)
        val nutrition = Nutrient.entries.associateWith {
            MenuFormValidator.parseNonNegativeDecimal(state.nutritionInputs[it].orEmpty())
        }
        val invalidFields = buildSet {
            if (state.name.isBlank()) add(MenuField.NAME)
            if (price == null) add(MenuField.PRICE)
            if (stock == null) add(MenuField.STOCK)
        }
        val invalidNutrients = nutrition.filterValues { it == null }.keys
        if (invalidFields.isNotEmpty() || invalidNutrients.isNotEmpty()) {
            _uiState.update { it.copy(invalidFields = invalidFields, invalidNutrients = invalidNutrients) }
            return
        }

        val draft = MenuDraft(
            name = state.name.trim(),
            description = state.description.trim().takeIf { it.isNotEmpty() },
            category = state.category,
            price = price!!,
            stock = stock!!,
            imageUrl = state.imageUrl.takeIf { it.isNotEmpty() },
            nutrition = Nutrition(
                calories = nutrition.getValue(Nutrient.CALORIES)!!,
                carbs = nutrition.getValue(Nutrient.CARBS)!!,
                protein = nutrition.getValue(Nutrient.PROTEIN)!!,
                fat = nutrition.getValue(Nutrient.FAT)!!,
                salt = nutrition.getValue(Nutrient.SALT)!!,
                sugar = nutrition.getValue(Nutrient.SUGAR)!!,
            ),
        )
        viewModelScope.launch {
            _uiState.update { it.copy(isSaving = true, saveError = null) }
            when (val result = sellerRepository.createMenu(draft)) {
                is ApiResult.Success -> _uiState.update { it.copy(isSaving = false, isSaved = true) }
                is ApiResult.Error -> _uiState.update { it.copy(isSaving = false, saveError = result.message) }
            }
        }
    }
}
