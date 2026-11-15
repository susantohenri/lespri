package edu.lespri.app.ui.schedule

import androidx.lifecycle.SavedStateHandle
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import dagger.hilt.android.lifecycle.HiltViewModel
import edu.lespri.app.domain.model.LessonSessionDetail
import edu.lespri.app.domain.repository.ScheduleRepository
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.catch
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch
import javax.inject.Inject

/**
 * State UI untuk Detail Sesi (P14).
 */
data class SessionDetailUiState(
    val isLoading: Boolean = true,
    val detail: LessonSessionDetail? = null,
    val isCancelling: Boolean = false,
    val isCancelled: Boolean = false,
    val errorMessage: String? = null
)

/**
 * ViewModel untuk Detail Sesi (P14).
 * Menampilkan ringkasan sesi, peserta, status presensi, serta aksi pembatalan sesi.
 */
@HiltViewModel
class SessionDetailViewModel @Inject constructor(
    private val scheduleRepository: ScheduleRepository,
    savedStateHandle: SavedStateHandle
) : ViewModel() {

    val sessionId: String = savedStateHandle.get<String>("sessionId").orEmpty()

    private val _uiState = MutableStateFlow(SessionDetailUiState())
    val uiState: StateFlow<SessionDetailUiState> = _uiState.asStateFlow()

    init {
        observeSession()
    }

    private fun observeSession() {
        if (sessionId.isBlank()) {
            _uiState.update {
                it.copy(isLoading = false, errorMessage = "ID sesi tidak valid")
            }
            return
        }

        viewModelScope.launch {
            scheduleRepository.getSessionDetail(sessionId)
                .catch { throwable ->
                    _uiState.update {
                        it.copy(isLoading = false, errorMessage = throwable.message)
                    }
                }
                .collect { detail ->
                    _uiState.update {
                        it.copy(
                            isLoading = false,
                            detail = detail,
                            errorMessage = if (detail == null) "Sesi tidak ditemukan" else null
                        )
                    }
                }
        }
    }

    fun cancelSession() {
        if (sessionId.isBlank()) return

        viewModelScope.launch {
            _uiState.update { it.copy(isCancelling = true, errorMessage = null) }
            scheduleRepository.cancelSession(sessionId)
                .onSuccess {
                    _uiState.update { it.copy(isCancelling = false, isCancelled = true) }
                }
                .onFailure { throwable ->
                    _uiState.update {
                        it.copy(
                            isCancelling = false,
                            errorMessage = throwable.message ?: "Gagal membatalkan sesi"
                        )
                    }
                }
        }
    }
}
