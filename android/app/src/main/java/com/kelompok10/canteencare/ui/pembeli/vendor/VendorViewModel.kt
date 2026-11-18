package com.kelompok10.canteencare.ui.pembeli.vendor

import androidx.lifecycle.SavedStateHandle
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.kelompok10.canteencare.core.network.ApiResult
import com.kelompok10.canteencare.data.repository.AddToCartResult
import com.kelompok10.canteencare.data.repository.CartRepository
import com.kelompok10.canteencare.data.repository.CatalogRepository
import com.kelompok10.canteencare.domain.model.MenuCategory
import com.kelompok10.canteencare.ui.navigation.Routes
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.async
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.map
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class VendorViewModel @Inject constructor(
    savedStateHandle: SavedStateHandle,
    private val catalogRepository: CatalogRepository,
    private val cartRepository: CartRepository,
) : ViewModel() {

    // Argumen navigasi dari route "vendor/{vendorId}?menuId={menuId}"
    private val vendorId: Int = checkNotNull(savedStateHandle[Routes.ARG_VENDOR_ID])
    private var menuIdToOpen: Int? = savedStateHandle.get<Int>(Routes.ARG_MENU_ID)?.takeIf { it > 0 }

    private val _uiState = MutableStateFlow(VendorUiState())
    val uiState: StateFlow<VendorUiState> = _uiState.asStateFlow()

    init {
        viewModelScope.launch {
            cartRepository.observeCart()
                .map { items -> items.sumOf { it.quantity } }
                .collect { count -> _uiState.update { it.copy(cartCount = count) } }
        }
        load()
    }

    fun load() {
        viewModelScope.launch {
            _uiState.update { it.copy(isLoading = true, errorMessage = null) }
            // Data stan dan daftar menu diambil bersamaan supaya lebih cepat
            val vendorCall = async { catalogRepository.getVendor(vendorId) }
            val menusCall = async { catalogRepository.getVendorMenus(vendorId) }
            val vendorResult = vendorCall.await()
            val menusResult = menusCall.await()

            when {
                vendorResult is ApiResult.Error -> showError(vendorResult.message)
                menusResult is ApiResult.Error -> showError(menusResult.message)
                vendorResult is ApiResult.Success && menusResult is ApiResult.Success -> {
                    val menus = menusResult.data
                    _uiState.update {
                        it.copy(
                            isLoading = false,
                            vendor = vendorResult.data,
                            menus = menus,
                            // Buka detail menu dari hasil pencarian, cukup sekali saja
                            selectedMenuId = menuIdToOpen?.takeIf { id -> menus.any { m -> m.id == id } }
                                ?: it.selectedMenuId,
                        )
                    }
                    menuIdToOpen = null
                }
            }
        }
    }

    fun onCategorySelected(category: MenuCategory?) = _uiState.update { it.copy(selectedCategory = category) }

    fun onMenuClick(menuId: Int) = _uiState.update { it.copy(selectedMenuId = menuId, quantity = 1) }

    fun onDismissMenu() = _uiState.update { it.copy(selectedMenuId = null) }

    /** Jumlah porsi dibatasi antara 1 dan stok yang tersedia. */
    fun onIncreaseQuantity() = _uiState.update { state ->
        val stock = state.selectedMenu?.stock ?: return@update state
        state.copy(quantity = (state.quantity + 1).coerceAtMost(stock))
    }

    fun onDecreaseQuantity() = _uiState.update { it.copy(quantity = (it.quantity - 1).coerceAtLeast(1)) }

    fun onAddToCart() {
        val state = _uiState.value
        val menu = state.selectedMenu ?: return
        viewModelScope.launch {
            when (val result = cartRepository.addItem(menu, state.quantity)) {
                is AddToCartResult.Added -> _uiState.update {
                    it.copy(selectedMenuId = null, message = "${menu.name} ditambahkan ke keranjang")
                }
                is AddToCartResult.DifferentVendor -> _uiState.update {
                    it.copy(replaceCartVendorName = result.currentVendorName)
                }
            }
        }
    }

    /** User setuju mengosongkan keranjang lama dan mengisinya dengan menu dari stan ini. */
    fun onConfirmReplaceCart() {
        val state = _uiState.value
        val menu = state.selectedMenu ?: return
        viewModelScope.launch {
            cartRepository.replaceCartWith(menu, state.quantity)
            _uiState.update {
                it.copy(
                    replaceCartVendorName = null,
                    selectedMenuId = null,
                    message = "Keranjang diganti. ${menu.name} ditambahkan",
                )
            }
        }
    }

    fun onCancelReplaceCart() = _uiState.update { it.copy(replaceCartVendorName = null) }

    fun onMessageShown() = _uiState.update { it.copy(message = null) }

    private fun showError(message: String) = _uiState.update { it.copy(isLoading = false, errorMessage = message) }
}
