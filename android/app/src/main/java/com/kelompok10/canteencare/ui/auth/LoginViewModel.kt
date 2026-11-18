package com.kelompok10.canteencare.ui.auth

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.kelompok10.canteencare.core.network.ApiResult
import com.kelompok10.canteencare.data.repository.AuthRepository
import com.kelompok10.canteencare.domain.validator.AuthValidator
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class LoginViewModel @Inject constructor(
    private val authRepository: AuthRepository,
) : ViewModel() {

    // State hanya bisa diubah dari dalam ViewModel; layar hanya membaca StateFlow
    private val _uiState = MutableStateFlow(LoginUiState())
    val uiState: StateFlow<LoginUiState> = _uiState.asStateFlow()

    fun onEmailChange(value: String) = _uiState.update { it.copy(email = value, errorMessage = null) }

    fun onPasswordChange(value: String) = _uiState.update { it.copy(password = value, errorMessage = null) }

    fun login() {
        val state = _uiState.value
        if (state.isLoading) return

        // Validasi dulu di HP supaya tidak mengirim request yang pasti gagal
        AuthValidator.validateLogin(state.email, state.password)?.let { message ->
            _uiState.update { it.copy(errorMessage = message) }
            return
        }

        viewModelScope.launch {
            _uiState.update { it.copy(isLoading = true, errorMessage = null) }
            when (val result = authRepository.login(state.email.trim(), state.password)) {
                is ApiResult.Success -> _uiState.update { it.copy(isLoading = false, loggedInRole = result.data.role) }
                // Pesan dari server sudah berbahasa Indonesia, contoh: "Email atau password salah."
                is ApiResult.Error -> _uiState.update { it.copy(isLoading = false, errorMessage = result.message) }
            }
        }
    }

    /** Dipanggil setelah layar berpindah, supaya navigasi tidak terulang saat layar diputar. */
    fun onNavigated() = _uiState.update { it.copy(loggedInRole = null) }
}
