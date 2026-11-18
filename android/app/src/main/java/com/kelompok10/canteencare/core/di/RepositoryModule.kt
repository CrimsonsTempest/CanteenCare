package com.kelompok10.canteencare.core.di

import com.kelompok10.canteencare.data.repository.AuthRepository
import com.kelompok10.canteencare.data.repository.DefaultAuthRepository
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
}
