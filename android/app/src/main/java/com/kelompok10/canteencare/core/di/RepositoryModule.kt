package com.kelompok10.canteencare.core.di

import com.kelompok10.canteencare.data.repository.AuthRepository
import com.kelompok10.canteencare.data.repository.CartRepository
import com.kelompok10.canteencare.data.repository.CatalogRepository
import com.kelompok10.canteencare.data.repository.DefaultAuthRepository
import com.kelompok10.canteencare.data.repository.DefaultCartRepository
import com.kelompok10.canteencare.data.repository.DefaultCatalogRepository
import com.kelompok10.canteencare.data.repository.DefaultNutritionRepository
import com.kelompok10.canteencare.data.repository.DefaultOrderRepository
import com.kelompok10.canteencare.data.repository.NutritionRepository
import com.kelompok10.canteencare.data.repository.OrderRepository
import dagger.Binds
import dagger.Module
import dagger.hilt.InstallIn
import dagger.hilt.components.SingletonComponent

/** Menghubungkan interface repository dengan implementasinya. ViewModel cukup meminta interface-nya. */
@Module
@InstallIn(SingletonComponent::class)
abstract class RepositoryModule {

    @Binds
    abstract fun bindAuthRepository(impl: DefaultAuthRepository): AuthRepository

    @Binds
    abstract fun bindCatalogRepository(impl: DefaultCatalogRepository): CatalogRepository

    @Binds
    abstract fun bindCartRepository(impl: DefaultCartRepository): CartRepository

    @Binds
    abstract fun bindOrderRepository(impl: DefaultOrderRepository): OrderRepository

    @Binds
    abstract fun bindNutritionRepository(impl: DefaultNutritionRepository): NutritionRepository
}
