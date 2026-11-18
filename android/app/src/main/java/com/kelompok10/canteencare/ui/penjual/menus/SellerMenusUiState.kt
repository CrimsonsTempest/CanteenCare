package com.kelompok10.canteencare.ui.penjual.menus

import com.kelompok10.canteencare.domain.model.Menu

/**
 * State layar Menu & Stok.
 * - [editingMenuId] dan [stockInput]: menu yang stoknya sedang diedit langsung di daftar.
 * - [archiveCandidate]: menu yang menunggu konfirmasi untuk diarsipkan.
 */
data class SellerMenusUiState(
    val isLoading: Boolean = true,
    val errorMessage: String? = null,
    val menus: List<Menu> = emptyList(),
    val editingMenuId: Int? = null,
    val stockInput: String = "",
    val isStockInputInvalid: Boolean = false,
    val archiveCandidate: Menu? = null,
    val isProcessing: Boolean = false,
    val message: String? = null,
)
