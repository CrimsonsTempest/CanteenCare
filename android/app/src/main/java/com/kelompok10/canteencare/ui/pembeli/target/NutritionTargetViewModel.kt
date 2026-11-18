package com.kelompok10.canteencare.ui.pembeli.target

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.kelompok10.canteencare.core.network.ApiResult
import com.kelompok10.canteencare.data.repository.NutritionRepository
import com.kelompok10.canteencare.domain.model.Nutrient
import com.kelompok10.canteencare.domain.model.NutritionTarget
import com.kelompok10.canteencare.domain.validator.TargetValidator
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class NutritionTargetViewModel @Inject constructor(
    private val nutritionRepository: NutritionRepository,
) : ViewModel() {

    private val _uiState = MutableStateFlow(NutritionTargetUiState())
    val uiState: StateFlow<NutritionTargetUiState> = _uiState.asStateFlow()

    init {
        load()
    }

    fun load() {
        viewModelScope.launch {
            _uiState.update { it.copy(isLoading = true, loadError = null) }
            when (val result = nutritionRepository.getTarget()) {
                is ApiResult.Success -> _uiState.update {
                    it.copy(isLoading = false, inputs = toInputs(result.data))
                }
                is ApiResult.Error -> _uiState.update { it.copy(isLoading = false, loadError = result.message) }
            }
        }
    }

    fun onValueChange(nutrient: Nutrient, text: String) = _uiState.update {
        it.copy(inputs = it.inputs + (nutrient to text), invalidFields = it.invalidFields - nutrient, saveError = null)
    }

    /** Isi form dengan target default. Belum tersimpan sampai tombol Simpan ditekan. */
    fun onResetToDefault() = _uiState.update {
        it.copy(inputs = toInputs(NutritionTarget.DEFAULT), invalidFields = emptySet(), saveError = null)
    }

    fun save() {
        val state = _uiState.value
        if (state.isSaving) return

        // Semua nilai wajib angka lebih dari 0; tandai kolom yang salah
        val parsed = Nutrient.entries.associateWith { TargetValidator.parse(state.inputs[it].orEmpty()) }
        val invalid = parsed.filterValues { it == null }.keys
        if (invalid.isNotEmpty()) {
            _uiState.update { it.copy(invalidFields = invalid) }
            return
        }

        val target = NutritionTarget.fromValues(parsed.mapValues { it.value!! })
        viewModelScope.launch {
            _uiState.update { it.copy(isSaving = true, saveError = null) }
            when (val result = nutritionRepository.updateTarget(target)) {
                is ApiResult.Success -> _uiState.update { it.copy(isSaving = false, isSaved = true) }
                is ApiResult.Error -> _uiState.update { it.copy(isSaving = false, saveError = result.message) }
            }
        }
    }

    private fun toInputs(target: NutritionTarget): Map<Nutrient, String> =
        Nutrient.entries.associateWith { TargetValidator.toInput(target.valueOf(it)) }
}
