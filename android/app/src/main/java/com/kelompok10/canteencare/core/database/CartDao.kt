package com.kelompok10.canteencare.core.database

import androidx.room.Dao
import androidx.room.Query
import androidx.room.Transaction
import androidx.room.Upsert
import kotlinx.coroutines.flow.Flow

/** Akses data keranjang. Fungsi yang mengembalikan Flow otomatis memancarkan data baru saat tabel berubah. */
@Dao
interface CartDao {
    @Query("SELECT * FROM cart_items ORDER BY name")
    fun observeAll(): Flow<List<CartItemEntity>>

    @Query("SELECT * FROM cart_items")
    suspend fun getAll(): List<CartItemEntity>

    @Upsert
    suspend fun upsert(item: CartItemEntity)

    @Query("UPDATE cart_items SET quantity = :quantity WHERE menuId = :menuId")
    suspend fun updateQuantity(menuId: Int, quantity: Int)

    @Query("DELETE FROM cart_items WHERE menuId = :menuId")
    suspend fun delete(menuId: Int)

    @Query("DELETE FROM cart_items")
    suspend fun clear()

    /** Kosongkan lalu isi ulang dalam satu transaksi, jadi keranjang tidak pernah setengah kosong. */
    @Transaction
    suspend fun replaceAll(item: CartItemEntity) {
        clear()
        upsert(item)
    }
}
