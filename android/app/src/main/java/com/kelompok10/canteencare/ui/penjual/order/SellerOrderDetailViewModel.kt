package com.kelompok10.canteencare.ui.penjual.order

import androidx.lifecycle.SavedStateHandle
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.kelompok10.canteencare.core.network.ApiResult
import com.kelompok10.canteencare.data.repository.SellerRepository
import com.kelompok10.canteencare.domain.calculator.NutritionCalculator
import com.kelompok10.canteencare.domain.model.Order
import com.kelompok10.canteencare.ui.navigation.Routes
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class SellerOrderDetailViewModel @Inject constructor(
    savedStateHandle: SavedStateHandle,
    private val sellerRepository: SellerRepository,
) : ViewModel() {

    private val orderId: Int = checkNotNull(savedStateHandle[Routes.ARG_ORDER_ID])

    private val _uiState = MutableStateFlow(SellerOrderDetailUiState())
    val uiState: StateFlow<SellerOrderDetailUiState> = _uiState.asStateFlow()

    init {
        load()
    }

    fun load() {
        viewModelScope.launch {
            _uiState.update { it.copy(isLoading = true, errorMessage = null) }
            when (val result = sellerRepository.getOrder(orderId)) {
                is ApiResult.Success -> showOrder(result.data)
                is ApiResult.Error -> _uiState.update { it.copy(isLoading = false, errorMessage = result.message) }
            }
        }
    }

    /** Konfirmasi pesanan: server mengecek ulang dan mengurangi stok, lalu mengirim notifikasi ke pembeli. */
    fun confirm() {
        if (_uiState.value.isProcessing) return
        viewModelScope.launch {
            _uiState.update { it.copy(isProcessing = true) }
            when (val result = sellerRepository.confirmOrder(orderId)) {
                is ApiResult.Success -> {
                    showOrder(result.data)
                    _uiState.update { it.copy(message = "Pesanan dikonfirmasi. Stok sudah dikurangi.") }
                }
                // Contoh: "Stok Mie Ayam Solo tidak mencukupi, tersisa 2."
                is ApiResult.Error -> _uiState.update { it.copy(isProcessing = false, message = result.message) }
            }
        }
    }

    fun onRejectClick() = _uiState.update { it.copy(showRejectDialog = true) }

    fun onRejectReasonChange(reason: String) = _uiState.update { it.copy(rejectReason = reason) }

    fun onDismissRejectDialog() = _uiState.update { it.copy(showRejectDialog = false) }

    fun reject() {
        val state = _uiState.value
        val reason = state.rejectReason.trim()
        if (reason.isEmpty() || state.isProcessing) return
        viewModelScope.launch {
            _uiState.update { it.copy(isProcessing = true, showRejectDialog = false) }
            when (val result = sellerRepository.rejectOrder(orderId, reason)) {
                is ApiResult.Success -> {
                    showOrder(result.data)
                    _uiState.update { it.copy(message = "Pesanan ditolak. Pembeli akan menerima notifikasi.") }
                }
                is ApiResult.Error -> _uiState.update { it.copy(isProcessing = false, message = result.message) }
            }
        }
    }

    fun onMessageShown() = _uiState.update { it.copy(message = null) }

    private fun showOrder(order: Order) = _uiState.update {
        it.copy(
            isLoading = false,
            isProcessing = false,
            order = order,
            totalPrice = NutritionCalculator.calculateTotalPrice(order.items),
        )
    }
}
