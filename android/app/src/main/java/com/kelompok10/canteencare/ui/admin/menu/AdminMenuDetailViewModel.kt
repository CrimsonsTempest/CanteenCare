package com.kelompok10.canteencare.ui.admin.menu

import android.net.Uri
import androidx.lifecycle.SavedStateHandle
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.kelompok10.canteencare.core.network.ApiResult
import com.kelompok10.canteencare.data.repository.AdminRepository
import com.kelompok10.canteencare.data.repository.ImageRepository
import com.kelompok10.canteencare.domain.model.Menu
import com.kelompok10.canteencare.domain.model.MenuCategory
import com.kelompok10.canteencare.domain.model.MenuEdit
import com.kelompok10.canteencare.domain.model.Nutrient
import com.kelompok10.canteencare.domain.model.Nutrition
import com.kelompok10.canteencare.domain.validator.MenuField
import com.kelompok10.canteencare.domain.validator.MenuFormValidator
import com.kelompok10.canteencare.domain.validator.TargetValidator
import com.kelompok10.canteencare.ui.navigation.Routes
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class AdminMenuDetailViewModel @Inject constructor(
    savedStateHandle: SavedStateHandle,
    private val adminRepository: AdminRepository,
    private val imageRepository: ImageRepository,
) : ViewModel() {

    private val menuId: Int = checkNotNull(savedStateHandle[Routes.ARG_MENU_ID])

    private val _uiState = MutableStateFlow(AdminMenuDetailUiState())
    val uiState: StateFlow<AdminMenuDetailUiState> = _uiState.asStateFlow()

    init {
        load()
    }

    fun load() {
        viewModelScope.launch {
            _uiState.update { it.copy(isLoading = true, loadError = null) }
            when (val result = adminRepository.getMenu(menuId)) {
                is ApiResult.Success -> fillForms(result.data)
                is ApiResult.Error -> _uiState.update { it.copy(isLoading = false, loadError = result.message) }
            }
        }
    }

    // ---------- Form data menu ----------

    fun onNameChange(v: String) = _uiState.update { it.copy(name = v, invalidFields = it.invalidFields - MenuField.NAME) }
    fun onDescriptionChange(v: String) = _uiState.update { it.copy(description = v) }
    fun onCategoryChange(v: MenuCategory) = _uiState.update { it.copy(category = v) }
    fun onPriceChange(v: String) = _uiState.update { it.copy(price = v, invalidFields = it.invalidFields - MenuField.PRICE) }

    /** Gambar baru langsung di-upload; baru tersimpan ke menu setelah "Simpan Data Menu" ditekan. */
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

    fun saveMenuData() {
        val state = _uiState.value
        if (state.isSavingMenu || state.isUploadingImage) return
        val price = MenuFormValidator.parseNonNegativeInt(state.price)
        val invalid = buildSet {
            if (state.name.isBlank()) add(MenuField.NAME)
            if (price == null) add(MenuField.PRICE)
        }
        if (invalid.isNotEmpty()) {
            _uiState.update { it.copy(invalidFields = invalid) }
            return
        }
        val edit = MenuEdit(
            name = state.name.trim(),
            description = state.description.trim().takeIf { it.isNotEmpty() },
            category = state.category,
            price = price!!,
            imageUrl = state.imageUrl.takeIf { it.isNotEmpty() },
        )
        viewModelScope.launch {
            _uiState.update { it.copy(isSavingMenu = true) }
            when (val result = adminRepository.updateMenu(menuId, edit)) {
                is ApiResult.Success -> {
                    fillForms(result.data)
                    _uiState.update { it.copy(isSavingMenu = false, message = "Data menu disimpan") }
                }
                is ApiResult.Error -> _uiState.update { it.copy(isSavingMenu = false, message = result.message) }
            }
        }
    }

    // ---------- Form gizi (simpan = verifikasi) ----------

    fun onNutritionChange(nutrient: Nutrient, value: String) = _uiState.update {
        it.copy(nutritionInputs = it.nutritionInputs + (nutrient to value), invalidNutrients = it.invalidNutrients - nutrient)
    }

    /** Admin mengecek estimasi penjual, memperbaiki jika perlu, lalu menyimpannya sebagai gizi terverifikasi. */
    fun verifyNutrition() {
        val state = _uiState.value
        if (state.isSavingNutrition) return
        val values = Nutrient.entries.associateWith {
            MenuFormValidator.parseNonNegativeDecimal(state.nutritionInputs[it].orEmpty())
        }
        val invalid = values.filterValues { it == null }.keys
        if (invalid.isNotEmpty()) {
            _uiState.update { it.copy(invalidNutrients = invalid) }
            return
        }
        val nutrition = Nutrition(
            calories = values.getValue(Nutrient.CALORIES)!!,
            carbs = values.getValue(Nutrient.CARBS)!!,
            protein = values.getValue(Nutrient.PROTEIN)!!,
            fat = values.getValue(Nutrient.FAT)!!,
            salt = values.getValue(Nutrient.SALT)!!,
            sugar = values.getValue(Nutrient.SUGAR)!!,
        )
        viewModelScope.launch {
            _uiState.update { it.copy(isSavingNutrition = true) }
            when (val result = adminRepository.verifyNutrition(menuId, nutrition)) {
                is ApiResult.Success -> {
                    fillForms(result.data)
                    _uiState.update { it.copy(isSavingNutrition = false, message = "Gizi disimpan dan terverifikasi") }
                }
                is ApiResult.Error -> _uiState.update { it.copy(isSavingNutrition = false, message = result.message) }
            }
        }
    }

    // ---------- Setujui / tolak ----------

    fun approve() = changeStatus("Menu disetujui dan sekarang tampil untuk pembeli") { adminRepository.approveMenu(menuId) }

    fun onRejectClick() = _uiState.update { it.copy(showRejectDialog = true) }

    fun onDismissRejectDialog() = _uiState.update { it.copy(showRejectDialog = false) }

    fun reject() {
        _uiState.update { it.copy(showRejectDialog = false) }
        changeStatus("Menu ditolak") { adminRepository.rejectMenu(menuId) }
    }

    fun onMessageShown() = _uiState.update { it.copy(message = null) }

    private fun changeStatus(successMessage: String, call: suspend () -> ApiResult<Menu>) {
        if (_uiState.value.isChangingStatus) return
        viewModelScope.launch {
            _uiState.update { it.copy(isChangingStatus = true) }
            when (val result = call()) {
                is ApiResult.Success -> {
                    fillForms(result.data)
                    _uiState.update { it.copy(isChangingStatus = false, message = successMessage) }
                }
                // Contoh: "Gizi menu ... belum diverifikasi. Verifikasi gizi terlebih dahulu."
                is ApiResult.Error -> _uiState.update { it.copy(isChangingStatus = false, message = result.message) }
            }
        }
    }

    /** Isi kedua form dengan data menu terbaru dari server. */
    private fun fillForms(menu: Menu) = _uiState.update {
        it.copy(
            isLoading = false,
            menu = menu,
            name = menu.name,
            description = menu.description.orEmpty(),
            category = menu.category,
            price = menu.price.toString(),
            imageUrl = menu.imageUrl.orEmpty(),
            imageError = null,
            invalidFields = emptySet(),
            nutritionInputs = Nutrient.entries.associateWith { nutrient ->
                menu.nutrition?.let { n -> TargetValidator.toInput(n.valueOf(nutrient)) }.orEmpty()
            },
            invalidNutrients = emptySet(),
        )
    }
}
