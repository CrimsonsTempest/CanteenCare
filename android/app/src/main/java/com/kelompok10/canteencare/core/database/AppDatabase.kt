package com.kelompok10.canteencare.core.database

import androidx.room.Database
import androidx.room.RoomDatabase

/** Database lokal di HP. Saat ini hanya berisi keranjang belanja. */
@Database(entities = [CartItemEntity::class], version = 1, exportSchema = false)
abstract class AppDatabase : RoomDatabase() {
    abstract fun cartDao(): CartDao

    companion object {
        const val NAME = "canteencare.db"
    }
}
