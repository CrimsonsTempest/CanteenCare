package com.kelompok10.canteencare.ui.navigation

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.kelompok10.canteencare.core.network.SessionEvents
import com.kelompok10.canteencare.data.repository.AuthRepository
import com.kelompok10.canteencare.domain.model.Role
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.map
import kotlinx.coroutines.flow.onEach
import kotlinx.coroutines.flow.stateIn
import javax.inject.Inject

/**
 * ViewModel tingkat aplikasi untuk navigasi:
 * - [role] peran user dari sesi, menentukan isi bottom bar.
 * - [sessionExpired] terpancar saat server membalas 401; sesi dan keranjang dibersihkan dulu.
 */
@HiltViewModel
class AppViewModel @Inject constructor(
    private val authRepository: AuthRepository,
    sessionEvents: SessionEvents,
) : ViewModel() {

    val role: StateFlow<Role?> = authRepository.observeSession()
        .map { it?.role }
        .stateIn(viewModelScope, SharingStarted.WhileSubscribed(5_000), null)

    val sessionExpired: Flow<Unit> = sessionEvents.expired.onEach { authRepository.logout() }
}
