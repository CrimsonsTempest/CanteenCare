package com.kelompok10.canteencare.ui.penjual.menus

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.kelompok10.canteencare.core.network.ApiResult
import com.kelompok10.canteencare.data.repository.SellerRepository
import com.kelompok10.canteencare.domain.model.Menu
import com.kelompok10.canteencare.domain.model.MenuStatus
import com.kelompok10.canteencare.domain.validator.MenuFormValidator
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class SellerMenusViewModel @Inject constructor(
    private val sellerRepository: SellerRepository,
) : ViewModel() {

    private val _uiState = MutableStateFlow(SellerMenusUiState())
    val uiState: StateFlow<SellerMenusUiState> = _uiState.asStateFlow()

    /** Dipanggil setiap layar tampil, misalnya setelah menambah menu baru. */
    fun load() {
        viewModelScope.launch {
            _uiState.update { it.copy(isLoading = it.menus.isEmpty(), errorMessage = null) }
            when (val result = sellerRepository.getMenus()) {
                is ApiResult.Success -> _uiState.update { it.copy(isLoading = false, menus = sortForDisplay(result.data)) }
                is ApiResult.Error -> _uiState.update { it.copy(isLoading = false, errorMessage = result.message) }
            }
        }
    }

    // ---------- Edit stok langsung di daftar ----------

    fun onEditStock(menu: Menu) = _uiState.update {
        it.copy(editingMenuId = menu.id, stockInput = menu.stock.toString(), isStockInputInvalid = false)
    }

    fun onStockInputChange(text: String) = _uiState.update { it.copy(stockInput = text, isStockInputInvalid = false) }

    fun onCancelEditStock() = _uiState.update { it.copy(editingMenuId = null) }

    fun onSaveStock() {
        val state = _uiState.value
        val menuId = state.editingMenuId ?: return
        val stock = MenuFormValidator.parseNonNegativeInt(state.stockInput)
        if (stock == null) {
            _uiState.update { it.copy(isStockInputInvalid = true) }
            return
        }
        viewModelScope.launch {
            _uiState.update { it.copy(isProcessing = true) }
            when (val result = sellerRepository.updateStock(menuId, stock)) {
                is ApiResult.Success -> _uiState.update {
                    it.copy(
                        isProcessing = false,
                        editingMenuId = null,
                        menus = it.menus.replace(result.data),
                        message = "Stok ${result.data.name} menjadi ${result.data.stock}",
                    )
                }
                is ApiResult.Error -> _uiState.update { it.copy(isProcessing = false, message = result.message) }
            }
        }
    }

    // ---------- Arsipkan menu ----------

    fun onArchiveClick(menu: Menu) = _uiState.update { it.copy(archiveCandidate = menu) }

    fun onDismissArchive() = _uiState.update { it.copy(archiveCandidate = null) }

    fun onConfirmArchive() {
        val menu = _uiState.value.archiveCandidate ?: return
        viewModelScope.launch {
            _uiState.update { it.copy(isProcessing = true, archiveCandidate = null) }
            when (val result = sellerRepository.archiveMenu(menu.id)) {
                is ApiResult.Success -> _uiState.update {
                    it.copy(
                        isProcessing = false,
                        menus = sortForDisplay(it.menus.replace(result.data)),
                        message = "${menu.name} diarsipkan",
                    )
                }
                is ApiResult.Error -> _uiState.update { it.copy(isProcessing = false, message = result.message) }
            }
        }
    }

    fun onMessageShown() = _uiState.update { it.copy(message = null) }

    /** Menu yang diarsipkan ditaruh paling bawah. */
    private fun sortForDisplay(menus: List<Menu>) = menus.sortedBy { it.status == MenuStatus.ARCHIVED }

    private fun List<Menu>.replace(updated: Menu) = map { if (it.id == updated.id) updated else it }
}
