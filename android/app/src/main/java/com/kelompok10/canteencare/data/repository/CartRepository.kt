package com.kelompok10.canteencare.data.repository

import com.kelompok10.canteencare.domain.model.CartItem
import com.kelompok10.canteencare.domain.model.Menu
import kotlinx.coroutines.flow.Flow

/** Hasil menambah menu ke keranjang. */
sealed interface AddToCartResult {
    /** Berhasil; [quantity] adalah jumlah akhir menu itu di keranjang. */
    data class Added(val quantity: Int) : AddToCartResult

    /** Keranjang masih berisi menu dari stan lain, jadi perlu konfirmasi untuk dikosongkan. */
    data class DifferentVendor(val currentVendorName: String) : AddToCartResult
}

/** Keranjang belanja yang disimpan di Room, sehingga tidak hilang saat aplikasi ditutup. */
interface CartRepository {
    /** Isi keranjang; otomatis memancarkan data baru setiap kali keranjang berubah. */
    fun observeCart(): Flow<List<CartItem>>

    /** Tambah menu ke keranjang. Aturan: satu keranjang hanya untuk satu stan. */
    suspend fun addItem(menu: Menu, quantity: Int): AddToCartResult

    /** Kosongkan keranjang lalu isi dengan menu ini (dipakai setelah user setuju ganti stan). */
    suspend fun replaceCartWith(menu: Menu, quantity: Int)
}
