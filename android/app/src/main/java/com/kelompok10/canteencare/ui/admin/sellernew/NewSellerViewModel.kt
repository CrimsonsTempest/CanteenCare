package com.kelompok10.canteencare.ui.admin.sellernew

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.kelompok10.canteencare.core.network.ApiResult
import com.kelompok10.canteencare.data.repository.AdminRepository
import com.kelompok10.canteencare.domain.model.SellerDraft
import com.kelompok10.canteencare.domain.validator.SellerField
import com.kelompok10.canteencare.domain.validator.SellerFormValidator
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch
import javax.inject.Inject

/** State form Penjual Baru: data akun penjual dan data stannya. */
data class NewSellerUiState(
    val fullName: String = "",
    val email: String = "",
    val password: String = "",
    val vendorName: String = "",
    val vendorDescription: String = "",
    val openHours: String = "",
    val invalidFields: Set<SellerField> = emptySet(),
    val isSaving: Boolean = false,
    val saveError: String? = null,
    val isSaved: Boolean = false,
)

@HiltViewModel
class NewSellerViewModel @Inject constructor(
    private val adminRepository: AdminRepository,
) : ViewModel() {

    private val _uiState = MutableStateFlow(NewSellerUiState())
    val uiState: StateFlow<NewSellerUiState> = _uiState.asStateFlow()

    fun onFullNameChange(v: String) = _uiState.update { it.copy(fullName = v, invalidFields = it.invalidFields - SellerField.FULL_NAME) }
    fun onEmailChange(v: String) = _uiState.update { it.copy(email = v, invalidFields = it.invalidFields - SellerField.EMAIL) }
    fun onPasswordChange(v: String) = _uiState.update { it.copy(password = v, invalidFields = it.invalidFields - SellerField.PASSWORD) }
    fun onVendorNameChange(v: String) =
        _uiState.update { it.copy(vendorName = v, invalidFields = it.invalidFields - SellerField.VENDOR_NAME) }
    fun onVendorDescriptionChange(v: String) = _uiState.update { it.copy(vendorDescription = v) }
    fun onOpenHoursChange(v: String) = _uiState.update { it.copy(openHours = v) }

    fun submit() {
        val state = _uiState.value
        if (state.isSaving) return
        val invalid = SellerFormValidator.invalidFields(state.fullName, state.email, state.password, state.vendorName)
        if (invalid.isNotEmpty()) {
            _uiState.update { it.copy(invalidFields = invalid) }
            return
        }
        val draft = SellerDraft(
            fullName = state.fullName.trim(),
            email = state.email.trim(),
            password = state.password,
            vendorName = state.vendorName.trim(),
            vendorDescription = state.vendorDescription.trim().takeIf { it.isNotEmpty() },
            openHours = state.openHours.trim().takeIf { it.isNotEmpty() },
        )
        viewModelScope.launch {
            _uiState.update { it.copy(isSaving = true, saveError = null) }
            when (val result = adminRepository.createSeller(draft)) {
                is ApiResult.Success -> _uiState.update { it.copy(isSaving = false, isSaved = true) }
                // Contoh: "Email sudah terdaftar."
                is ApiResult.Error -> _uiState.update { it.copy(isSaving = false, saveError = result.message) }
            }
        }
    }
}
