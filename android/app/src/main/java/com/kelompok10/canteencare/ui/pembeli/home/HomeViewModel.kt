package com.kelompok10.canteencare.ui.pembeli.home

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.kelompok10.canteencare.core.network.ApiResult
import com.kelompok10.canteencare.data.repository.AuthRepository
import com.kelompok10.canteencare.data.repository.CartRepository
import com.kelompok10.canteencare.data.repository.CatalogRepository
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.FlowPreview
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.collectLatest
import kotlinx.coroutines.flow.debounce
import kotlinx.coroutines.flow.distinctUntilChanged
import kotlinx.coroutines.flow.filterNotNull
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.flow.map
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class HomeViewModel @Inject constructor(
    private val catalogRepository: CatalogRepository,
    cartRepository: CartRepository,
    authRepository: AuthRepository,
) : ViewModel() {

    private val _uiState = MutableStateFlow(HomeUiState())
    val uiState: StateFlow<HomeUiState> = _uiState.asStateFlow()

    /** Teks pencarian mentah, berubah di setiap ketikan. */
    private val queryFlow = MutableStateFlow("")

    init {
        viewModelScope.launch {
            val session = authRepository.observeSession().filterNotNull().first()
            _uiState.update { it.copy(userName = session.fullName) }
        }
        // Badge keranjang: jumlah porsi di keranjang, otomatis terbarui saat keranjang berubah
        viewModelScope.launch {
            cartRepository.observeCart()
                .map { items -> items.sumOf { it.quantity } }
                .collect { count -> _uiState.update { it.copy(cartCount = count) } }
        }
        loadVendors()
        observeSearch()
    }

    fun loadVendors() {
        viewModelScope.launch {
            _uiState.update { it.copy(isLoadingVendors = true, vendorsError = null) }
            when (val result = catalogRepository.getVendors()) {
                is ApiResult.Success -> _uiState.update { it.copy(isLoadingVendors = false, vendors = result.data) }
                is ApiResult.Error -> _uiState.update { it.copy(isLoadingVendors = false, vendorsError = result.message) }
            }
        }
    }

    fun onQueryChange(query: String) {
        _uiState.update { it.copy(query = query) }
        queryFlow.value = query
    }

    fun retrySearch() {
        viewModelScope.launch { search(_uiState.value.query.trim()) }
    }

    /**
     * Pencarian reaktif:
     * - debounce(500): tunggu user berhenti mengetik 500 ms, jadi tidak request di setiap huruf.
     * - distinctUntilChanged(): abaikan jika kata kunci sama dengan sebelumnya.
     * - collectLatest: jika kata kunci baru datang saat pencarian lama belum selesai,
     *   pencarian lama dibatalkan dan hanya hasil terbaru yang dipakai.
     */
    @OptIn(FlowPreview::class)
    private fun observeSearch() {
        viewModelScope.launch {
            queryFlow
                .debounce(SEARCH_DEBOUNCE_MILLIS)
                .map { it.trim() }
                .distinctUntilChanged()
                .collectLatest { query -> search(query) }
        }
    }

    private suspend fun search(query: String) {
        if (query.isEmpty()) {
            _uiState.update {
                it.copy(isSearching = false, searchResults = emptyList(), searchError = null, searchedQuery = "")
            }
            return
        }
        _uiState.update { it.copy(isSearching = true, searchError = null) }
        when (val result = catalogRepository.searchMenus(query)) {
            is ApiResult.Success -> _uiState.update {
                it.copy(isSearching = false, searchResults = result.data, searchedQuery = query)
            }
            is ApiResult.Error -> _uiState.update { it.copy(isSearching = false, searchError = result.message) }
        }
    }

    private companion object {
        const val SEARCH_DEBOUNCE_MILLIS = 500L
    }
}
