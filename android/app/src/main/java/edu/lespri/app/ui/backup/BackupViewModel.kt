package edu.lespri.app.ui.backup

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import dagger.hilt.android.lifecycle.HiltViewModel
import edu.lespri.app.domain.repository.BackupRepository
import edu.lespri.app.util.BackupManager
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch
import javax.inject.Inject
import kotlin.Result

data class BackupUiState(
    val isLoading: Boolean = false,
    val errorMessage: String? = null,
    val successMessage: String? = null,
    val backupFiles: List<BackupFileInfo> = emptyList(),
    val lastExportFileName: String? = null
)

data class BackupFileInfo(
    val name: String,
    val path: String,
    val sizeBytes: Long,
    val lastModified: Long
)

@HiltViewModel
class BackupViewModel @Inject constructor(
    private val backupRepository: BackupRepository,
    private val backupManager: BackupManager
) : ViewModel() {

    private val _uiState = MutableStateFlow(BackupUiState())
    val uiState: StateFlow<BackupUiState> = _uiState.asStateFlow()

    init {
        refreshBackupList()
    }

    fun refreshBackupList() {
        val files = backupManager.listBackups().map { f ->
            BackupFileInfo(
                name = f.name,
                path = f.absolutePath,
                sizeBytes = f.length(),
                lastModified = f.lastModified()
            )
        }
        _uiState.update { it.copy(backupFiles = files) }
    }

    fun exportBackup() {
        viewModelScope.launch {
            _uiState.update { it.copy(isLoading = true, errorMessage = null, successMessage = null) }
            val result = backupRepository.exportJsonBackup()
            result.onSuccess { jsonPayload ->
                val fileName = backupManager.backupFileName()
                try {
                    val payloadMap = mapOf("backup" to jsonPayload)
                    backupManager.exportToFile(payloadMap, fileName)
                    _uiState.update {
                        it.copy(
                            isLoading = false,
                            successMessage = "Backup berhasil diekspor: $fileName",
                            lastExportFileName = fileName
                        )
                    }
                    refreshBackupList()
                } catch (e: Exception) {
                    _uiState.update { it.copy(isLoading = false, errorMessage = e.message) }
                }
            }.onFailure { error ->
                _uiState.update { it.copy(isLoading = false, errorMessage = error.message) }
            }
        }
    }

    fun restoreFromFile(filePath: String) {
        val file = java.io.File(filePath)
        if (!file.exists()) {
            _uiState.update { it.copy(errorMessage = "Berkas tidak ditemukan.") }
            return
        }
        viewModelScope.launch {
            _uiState.update { it.copy(isLoading = true, errorMessage = null, successMessage = null) }
            try {
                val jsonString = backupManager.importFromFile(file)
                val result = backupRepository.restoreJsonBackup(jsonString)
                result.onSuccess {
                    _uiState.update {
                        it.copy(
                            isLoading = false,
                            successMessage = "Backup berhasil dipulihkan."
                        )
                    }
                    refreshBackupList()
                }.onFailure { error ->
                    _uiState.update { it.copy(isLoading = false, errorMessage = error.message) }
                }
            } catch (e: Exception) {
                _uiState.update { it.copy(isLoading = false, errorMessage = e.message) }
            }
        }
    }

    fun deleteBackup(fileName: String) {
        val file = java.io.File(backupManager.backupDir, fileName)
        if (backupManager.deleteBackup(file)) {
            refreshBackupList()
            _uiState.update { it.copy(successMessage = "Berkas backup dihapus.") }
        } else {
            _uiState.update { it.copy(errorMessage = "Gagal menghapus berkas backup.") }
        }
    }
}