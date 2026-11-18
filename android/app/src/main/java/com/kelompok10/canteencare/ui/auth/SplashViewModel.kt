package com.kelompok10.canteencare.ui.auth

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.kelompok10.canteencare.core.notification.FcmTokenManager
import com.kelompok10.canteencare.data.repository.AuthRepository
import com.kelompok10.canteencare.ui.navigation.Routes
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.delay
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.launch
import javax.inject.Inject

/**
 * Mengecek apakah sudah ada sesi tersimpan.
 * Ada sesi → langsung ke home sesuai peran (tetap login setelah aplikasi ditutup). Tidak ada → Login.
 */
@HiltViewModel
class SplashViewModel @Inject constructor(
    private val authRepository: AuthRepository,
    private val fcmTokenManager: FcmTokenManager,
) : ViewModel() {

    /** Route tujuan setelah splash; null selama pengecekan masih berjalan. */
    private val _destination = MutableStateFlow<String?>(null)
    val destination: StateFlow<String?> = _destination.asStateFlow()

    init {
        viewModelScope.launch {
            delay(SPLASH_MIN_MILLIS) // biar logo sempat terlihat
            val session = authRepository.observeSession().first()
            if (session != null) {
                // Token FCM bisa berganti sejak login terakhir, jadi dikirim ulang
                fcmTokenManager.syncToken()
                _destination.value = Routes.homeFor(session.role)
            } else {
                _destination.value = Routes.LOGIN
            }
        }
    }

    private companion object {
        const val SPLASH_MIN_MILLIS = 700L
    }
}
