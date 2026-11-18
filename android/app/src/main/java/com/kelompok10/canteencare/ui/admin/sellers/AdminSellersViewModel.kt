package com.kelompok10.canteencare.ui.admin.sellers

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.kelompok10.canteencare.core.network.ApiResult
import com.kelompok10.canteencare.data.repository.AdminRepository
import com.kelompok10.canteencare.domain.model.Seller
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch
import javax.inject.Inject

data class AdminSellersUiState(
    val isLoading: Boolean = true,
    val errorMessage: String? = null,
    val sellers: List<Seller> = emptyList(),
)

@HiltViewModel
class AdminSellersViewModel @Inject constructor(
    private val adminRepository: AdminRepository,
) : ViewModel() {

    private val _uiState = MutableStateFlow(AdminSellersUiState())
    val uiState: StateFlow<AdminSellersUiState> = _uiState.asStateFlow()

    /** Dipanggil setiap layar tampil, termasuk setelah membuat penjual baru. */
    fun load() {
        viewModelScope.launch {
            _uiState.update { it.copy(isLoading = it.sellers.isEmpty(), errorMessage = null) }
            when (val result = adminRepository.getSellers()) {
                is ApiResult.Success -> _uiState.update { it.copy(isLoading = false, sellers = result.data) }
                is ApiResult.Error -> _uiState.update { it.copy(isLoading = false, errorMessage = result.message) }
            }
        }
    }
}
