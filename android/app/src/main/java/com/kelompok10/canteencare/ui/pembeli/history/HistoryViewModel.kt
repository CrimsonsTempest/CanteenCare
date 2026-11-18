package com.kelompok10.canteencare.ui.pembeli.history

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.kelompok10.canteencare.core.network.ApiResult
import com.kelompok10.canteencare.data.repository.OrderRepository
import com.kelompok10.canteencare.domain.model.OrderStatus
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.Job
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class HistoryViewModel @Inject constructor(
    private val orderRepository: OrderRepository,
) : ViewModel() {

    private val _uiState = MutableStateFlow(HistoryUiState())
    val uiState: StateFlow<HistoryUiState> = _uiState.asStateFlow()

    private var loadJob: Job? = null

    /** Dipanggil setiap layar tampil, supaya status pesanan selalu yang terbaru. */
    fun onScreenResumed() = fetch(pullToRefresh = false)

    fun onRefresh() = fetch(pullToRefresh = true)

    fun onFilterSelected(status: OrderStatus?) {
        if (status == _uiState.value.selectedStatus) return
        _uiState.update { it.copy(selectedStatus = status, orders = emptyList()) }
        fetch(pullToRefresh = false)
    }

    private fun fetch(pullToRefresh: Boolean) {
        // Batalkan permintaan lama, misalnya saat user cepat berganti chip filter,
        // supaya hasil filter lama tidak menimpa hasil filter baru
        loadJob?.cancel()
        loadJob = viewModelScope.launch {
            _uiState.update {
                it.copy(
                    // Spinner layar penuh hanya jika belum ada data sama sekali
                    isLoading = !pullToRefresh && it.orders.isEmpty(),
                    isRefreshing = pullToRefresh,
                    errorMessage = null,
                )
            }
            when (val result = orderRepository.getMyOrders(_uiState.value.selectedStatus)) {
                is ApiResult.Success -> _uiState.update {
                    it.copy(isLoading = false, isRefreshing = false, orders = result.data)
                }
                is ApiResult.Error -> _uiState.update {
                    it.copy(isLoading = false, isRefreshing = false, errorMessage = result.message)
                }
            }
        }
    }
}
