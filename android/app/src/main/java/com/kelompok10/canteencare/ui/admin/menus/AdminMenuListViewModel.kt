package com.kelompok10.canteencare.ui.admin.menus

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.kelompok10.canteencare.core.network.ApiResult
import com.kelompok10.canteencare.data.repository.AdminRepository
import com.kelompok10.canteencare.domain.model.Menu
import com.kelompok10.canteencare.domain.model.MenuStatus
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.Job
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch
import javax.inject.Inject

/** State daftar menu admin. [filter] null berarti semua status. */
data class AdminMenuListUiState(
    val filter: MenuStatus? = null,
    val menus: List<Menu> = emptyList(),
    val isLoading: Boolean = true,
    val isRefreshing: Boolean = false,
    val errorMessage: String? = null,
)

/**
 * Logika bersama untuk daftar menu admin. Dua layar memakainya dengan filter awal berbeda:
 * Verifikasi (selalu PENDING) dan Semua Menu (bisa difilter).
 */
abstract class AdminMenuListViewModel(
    private val adminRepository: AdminRepository,
    initialFilter: MenuStatus?,
) : ViewModel() {

    private val _uiState = MutableStateFlow(AdminMenuListUiState(filter = initialFilter))
    val uiState: StateFlow<AdminMenuListUiState> = _uiState.asStateFlow()

    private var loadJob: Job? = null

    /** Dipanggil setiap layar tampil, misalnya setelah menyetujui menu di layar detail. */
    fun onScreenResumed() = fetch(pullToRefresh = false)

    fun onRefresh() = fetch(pullToRefresh = true)

    fun onFilterSelected(status: MenuStatus?) {
        if (status == _uiState.value.filter) return
        _uiState.update { it.copy(filter = status, menus = emptyList()) }
        fetch(pullToRefresh = false)
    }

    private fun fetch(pullToRefresh: Boolean) {
        loadJob?.cancel()
        loadJob = viewModelScope.launch {
            _uiState.update {
                it.copy(isLoading = !pullToRefresh && it.menus.isEmpty(), isRefreshing = pullToRefresh, errorMessage = null)
            }
            when (val result = adminRepository.getMenus(_uiState.value.filter)) {
                is ApiResult.Success -> _uiState.update {
                    it.copy(isLoading = false, isRefreshing = false, menus = result.data)
                }
                is ApiResult.Error -> _uiState.update {
                    it.copy(isLoading = false, isRefreshing = false, errorMessage = result.message)
                }
            }
        }
    }
}

/** Tab Verifikasi: hanya menu yang menunggu verifikasi. */
@HiltViewModel
class AdminPendingViewModel @Inject constructor(adminRepository: AdminRepository) :
    AdminMenuListViewModel(adminRepository, initialFilter = MenuStatus.PENDING)

/** Tab Semua Menu: semua status, bisa difilter. */
@HiltViewModel
class AdminMenusViewModel @Inject constructor(adminRepository: AdminRepository) :
    AdminMenuListViewModel(adminRepository, initialFilter = null)
