package edu.lespri.app.ui.csv

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import dagger.hilt.android.lifecycle.HiltViewModel
import edu.lespri.app.domain.model.ImportSummary
import edu.lespri.app.domain.repository.BackupRepository
import edu.lespri.app.util.CsvEngine
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch
import javax.inject.Inject

/**
 * State UI untuk layar Impor CSV (P21).
 *
 * Alur:
 * 1. Layar membaca berkas CSV → [loadCsv] mem-parse dan menyimpan [CsvEngine.CsvResult]
 * 2. [suggestHeaderMapping] mencocokkan header CSV ke field domain secara otomatis
 * 3. Pengguna menyesuaikan pemetaan via [updateMapping]
 * 4. [confirmImport] menyimpan hasil impor melalui [BackupRepository.importCsvData]
 */
data class CsvImportUiState(
    val csvResult: CsvEngine.CsvResult? = null,
    val headerMapping: Map<String, String> = emptyMap(), // csvHeader → domainField
    val previewRows: List<Map<String, String>> = emptyList(),
    val isLoading: Boolean = false,
    val errorMessage: String? = null,
    val successMessage: String? = null,
    val importSummary: ImportSummary? = null
)

@HiltViewModel
class CsvImportViewModel @Inject constructor(
    private val backupRepository: BackupRepository
) : ViewModel() {

    private val _uiState = MutableStateFlow(CsvImportUiState())
    val uiState: StateFlow<CsvImportUiState> = _uiState.asStateFlow()

    /** Memuat dan mem-parsing teks CSV. Dipanggil setelah layar membaca berkas. */
    fun loadCsv(csvText: String) {
        val result = CsvEngine.parse(csvText)
        result.onSuccess { csv ->
            _uiState.update {
                it.copy(
                    csvResult = csv,
                    headerMapping = emptyMap(),
                    previewRows = emptyList(),
                    errorMessage = null,
                    successMessage = null,
                    importSummary = null
                )
            }
            if (csv.headers.isNotEmpty()) {
                suggestHeaderMapping(csv.headers)
            }
        }.onFailure { error ->
            _uiState.update { it.copy(errorMessage = "Gagal membaca CSV: ${error.message}") }
        }
    }

    /** Menyarankan pemetaan header otomatis berdasarkan kemiripan nama kolom. */
    fun suggestHeaderMapping(csvHeaders: List<String>) {
        val domainFields = listOf("name", "guardianName", "guardianPhone", "schoolGrade", "notes", "id", "status")
        val mapping = mutableMapOf<String, String>()
        for (header in csvHeaders) {
            val lower = header.lowercase()
            val match = domainFields.firstOrNull { df -> lower.contains(df) }
            if (match != null) mapping[header] = match
        }
        _uiState.update { it.copy(headerMapping = mapping.toMap()) }
        refreshPreview()
    }

    /** Memperbarui pemetaan satu kolom CSV ke field domain. */
    fun updateMapping(csvHeader: String, domainField: String?) {
        val newMapping = (_uiState.value.headerMapping.toMutableMap()).apply {
            if (domainField == null || domainField.isBlank()) {
                remove(csvHeader)
            } else {
                put(csvHeader, domainField)
            }
        }
        _uiState.update { it.copy(headerMapping = newMapping) }
        refreshPreview()
    }

    private fun refreshPreview() {
        val csv = _uiState.value.csvResult ?: return
        val mapping = _uiState.value.headerMapping
        val preview = csv.rows.map { row ->
            mapping.mapValues { (_, domainField) -> row[domainField] ?: "" }
        }
        _uiState.update { it.copy(previewRows = preview) }
    }

    /** Konfirmasi dan jalankan impor data ke database. */
    fun confirmImport() {
        val state = _uiState.value
        val csv = state.csvResult ?: return
        val mapping = state.headerMapping

        if (mapping.values.none { it == "name" }) {
            _uiState.update { it.copy(errorMessage = "Kolom nama siswa (name) harus di-mapping.") }
            return
        }

        viewModelScope.launch {
            _uiState.update { it.copy(isLoading = true, errorMessage = null) }
            val result = backupRepository.importCsvData(
                rawCsv = CsvEngine.generate(state.csvResult!!.headers, state.csvResult!!.rows),
                columnMapping = mapping
            )
            result.onSuccess { summary ->
                _uiState.update {
                    it.copy(
                        isLoading = false,
                        successMessage = "Impor selesai: ${summary.importedCount} siswa berhasil, ${summary.failedCount} gagal.",
                        importSummary = summary
                    )
                }
            }.onFailure { error ->
                _uiState.update { it.copy(isLoading = false, errorMessage = error.message) }
            }
        }
    }
}
