package edu.lespri.app.ui.csv

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import dagger.hilt.android.lifecycle.HiltViewModel
import edu.lespri.app.domain.repository.BackupRepository
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch
import javax.inject.Inject
import kotlin.Result

/**
 * State UI untuk layar Ekspor CSV (P22).
 *
 * Menyediakan daftar dataset yang bisa diekspor (siswa, tutor, kelas) dan
 * memanggil [BackupRepository.exportCsvDataset] untuk menghasilkan berkas CSV.
 */
data class CsvExportUiState(
    val selectedDataset: String = "students",
    val availableDatasets: List<String> = listOf("students", "tutors", "classes"),
    val isLoading: Boolean = false,
    val errorMessage: String? = null,
    val successMessage: String? = null,
    val exportedFileName: String? = null
)

@HiltViewModel
class CsvExportViewModel @Inject constructor(
    private val backupRepository: BackupRepository
) : ViewModel() {

    private val _uiState = MutableStateFlow(CsvExportUiState())
    val uiState: StateFlow<CsvExportUiState> = _uiState.asStateFlow()

    fun selectDataset(dataset: String) {
        _uiState.update { it.copy(selectedDataset = dataset, errorMessage = null, successMessage = null, exportedFileName = null) }
    }

    fun exportDataset() {
        val dataset = _uiState.value.selectedDataset
        viewModelScope.launch {
            _uiState.update { it.copy(isLoading = true, errorMessage = null, successMessage = null) }
            val result = backupRepository.exportCsvDataset(dataset)
            result.onSuccess { fileName ->
                _uiState.update {
                    it.copy(
                        isLoading = false,
                        successMessage = "Berhasil mengekspor $dataset ke berkas CSV.",
                        exportedFileName = fileName
                    )
                }
            }.onFailure { error ->
                _uiState.update { it.copy(isLoading = false, errorMessage = error.message) }
            }
        }
    }
}
