package com.kelompok10.canteencare.core.di

import android.content.Context
import androidx.room.Room
import com.kelompok10.canteencare.core.database.AppDatabase
import com.kelompok10.canteencare.core.database.CartDao
import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.android.qualifiers.ApplicationContext
import dagger.hilt.components.SingletonComponent
import javax.inject.Singleton

/** Menyediakan database Room dan DAO-nya. */
@Module
@InstallIn(SingletonComponent::class)
object DatabaseModule {

    @Provides
    @Singleton
    fun provideDatabase(@ApplicationContext context: Context): AppDatabase =
        Room.databaseBuilder(context, AppDatabase::class.java, AppDatabase.NAME).build()

    @Provides
    fun provideCartDao(database: AppDatabase): CartDao = database.cartDao()
}
