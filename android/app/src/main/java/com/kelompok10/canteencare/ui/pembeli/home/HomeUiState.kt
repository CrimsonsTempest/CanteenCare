package com.kelompok10.canteencare.ui.pembeli.home

import com.kelompok10.canteencare.domain.model.Menu
import com.kelompok10.canteencare.domain.model.Vendor

/**
 * State Beranda.
 * - Saat [query] kosong: tampilkan daftar stan ([vendors]).
 * - Saat [query] terisi: tampilkan hasil pencarian menu ([searchResults]).
 */
data class HomeUiState(
    val userName: String = "",
    val cartCount: Int = 0,
    // Daftar stan
    val isLoadingVendors: Boolean = true,
    val vendors: List<Vendor> = emptyList(),
    val vendorsError: String? = null,
    // Pencarian menu
    val query: String = "",
    val isSearching: Boolean = false,
    val searchResults: List<Menu> = emptyList(),
    val searchError: String? = null,
    /** Kata kunci yang hasilnya sedang ditampilkan; berbeda dengan [query] selama jeda debounce. */
    val searchedQuery: String = "",
) {
    val isSearchMode: Boolean get() = query.isNotBlank()

    /** "Tidak ditemukan" hanya ditampilkan jika pencarian untuk kata kunci saat ini sudah selesai. */
    val showNoResults: Boolean
        get() = !isSearching && searchError == null && searchResults.isEmpty() && searchedQuery == query.trim()
}
