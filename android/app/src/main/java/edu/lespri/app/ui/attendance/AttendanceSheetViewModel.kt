package edu.lespri.app.ui.attendance

import androidx.lifecycle.SavedStateHandle
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import dagger.hilt.android.lifecycle.HiltViewModel
import edu.lespri.app.domain.model.AttendanceRecord
import edu.lespri.app.domain.model.AttendanceStatus
import edu.lespri.app.domain.model.BillingType
import edu.lespri.app.domain.model.LessonSession
import edu.lespri.app.domain.repository.ScheduleRepository
import edu.lespri.app.domain.usecase.RecordAttendanceUseCase
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.catch
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch
import javax.inject.Inject

/**
 * Draft presensi satu siswa pada lembar kehadiran (P15).
 * Menyimpan status awal (dari database) dan status pilihan admin untuk preview dampak saldo paket.
 */
data class AttendanceDraftItem(
    val enrollmentId: String,
    val studentId: String,
    val studentName: String,
    val initialStatus: AttendanceStatus,
    val status: AttendanceStatus,
    val initialNote: String? = null,
    val note: String? = null,
    val billingType: BillingType,
    val remainingPackageSessions: Int? = null
) {
    /** True jika aksi ini akan memotong satu sesi paket siswa. */
    val willDeductSession: Boolean
        get() = billingType == BillingType.PACKAGE &&
            initialStatus != AttendanceStatus.PRESENT &&
            status == AttendanceStatus.PRESENT

    val isModified: Boolean
        get() = status != initialStatus || note.orEmpty() != initialNote.orEmpty()
}

/**
 * Baris preview dampak pemotongan saldo paket sebelum presensi disimpan.
 */
data class DeductionPreviewItem(
    val enrollmentId: String,
    val studentName: String,
    val currentBalance: Int,
    val projectedBalance: Int
)

/**
 * State UI untuk Lembar Presensi (P15).
 */
data class AttendanceSheetUiState(
    val isLoading: Boolean = true,
    val isSaving: Boolean = false,
    val session: LessonSession? = null,
    val className: String = "",
    val tutorName: String? = null,
    val studentAttendanceList: List<AttendanceDraftItem> = emptyList(),
    val deductionPreview: List<DeductionPreviewItem> = emptyList(),
    val hasDeductionImpact: Boolean = false,
    val saveSuccess: Boolean = false,
    val errorMessage: String? = null
) {
    val modifiedCount: Int
        get() = studentAttendanceList.count { it.isModified }
}

/**
 * ViewModel untuk Lembar Presensi (P15).
 * Menangani batch status toggling, preview pemotongan saldo paket, catatan per siswa,
 * dan penyimpanan atomik via [RecordAttendanceUseCase].
 */
@HiltViewModel
class AttendanceSheetViewModel @Inject constructor(
    private val scheduleRepository: ScheduleRepository,
    private val recordAttendanceUseCase: RecordAttendanceUseCase,
    savedStateHandle: SavedStateHandle
) : ViewModel() {

    val sessionId: String = savedStateHandle.get<String>("sessionId").orEmpty()

    private val _uiState = MutableStateFlow(AttendanceSheetUiState())
    val uiState: StateFlow<AttendanceSheetUiState> = _uiState.asStateFlow()

    private var draftsInitialized = false

    init {
        observeSessionDetail()
    }

    private fun observeSessionDetail() {
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
                    if (detail == null) {
                        _uiState.update {
                            it.copy(isLoading = false, errorMessage = "Sesi tidak ditemukan")
                        }
                        return@collect
                    }

                    _uiState.update { state ->
                        if (!draftsInitialized) {
                            draftsInitialized = true
                            val drafts = detail.attendanceRecords.map { item ->
                                AttendanceDraftItem(
                                    enrollmentId = item.enrollmentId,
                                    studentId = item.studentId,
                                    studentName = item.studentName,
                                    initialStatus = item.status,
                                    status = item.status,
                                    initialNote = item.note,
                                    note = item.note,
                                    billingType = item.billingType,
                                    remainingPackageSessions = item.remainingPackageSessions
                                )
                            }
                            state.copy(
                                isLoading = false,
                                session = detail.session,
                                className = detail.className,
                                tutorName = detail.tutorName,
                                studentAttendanceList = drafts,
                                errorMessage = null
                            ).withDeductionPreview()
                        } else {
                            state.copy(
                                isLoading = false,
                                session = detail.session,
                                className = detail.className,
                                tutorName = detail.tutorName
                            )
                        }
                    }
                }
        }
    }

    fun onStatusSelected(enrollmentId: String, status: AttendanceStatus) {
        _uiState.update { state ->
            state.copy(
                studentAttendanceList = state.studentAttendanceList.map { draft ->
                    if (draft.enrollmentId == enrollmentId) draft.copy(status = status) else draft
                }
            ).withDeductionPreview()
        }
    }

    fun onNoteChanged(enrollmentId: String, note: String) {
        _uiState.update { state ->
            state.copy(
                studentAttendanceList = state.studentAttendanceList.map { draft ->
                    if (draft.enrollmentId == enrollmentId) {
                        draft.copy(note = note.ifBlank { null })
                    } else {
                        draft
                    }
                }
            )
        }
    }

    fun onMarkAllPresent() {
        _uiState.update { state ->
            state.copy(
                studentAttendanceList = state.studentAttendanceList.map {
                    it.copy(status = AttendanceStatus.PRESENT)
                }
            ).withDeductionPreview()
        }
    }

    fun onClearAll() {
        _uiState.update { state ->
            state.copy(
                studentAttendanceList = state.studentAttendanceList.map {
                    it.copy(status = AttendanceStatus.UNRECORDED)
                }
            ).withDeductionPreview()
        }
    }

    /**
     * Menyimpan hanya record yang berubah agar audit trail tidak dipenuhi write kosong.
     * Pemotongan/pembalikan saldo paket dijamin atomik oleh AttendanceRepositoryImpl.
     */
    fun saveAttendance() {
        val state = _uiState.value
        if (state.isSaving || state.saveSuccess) return

        val changedDrafts = state.studentAttendanceList.filter { it.isModified }
        val now = System.currentTimeMillis()

        val records = changedDrafts.map { draft ->
            AttendanceRecord(
                id = "${sessionId}_${draft.enrollmentId}",
                sessionId = sessionId,
                enrollmentId = draft.enrollmentId,
                status = draft.status,
                note = draft.note,
                updatedAt = now
            )
        }

        viewModelScope.launch {
            _uiState.update { it.copy(isSaving = true, errorMessage = null) }
            recordAttendanceUseCase(sessionId, records)
                .onSuccess {
                    _uiState.update {
                        it.copy(isSaving = false, saveSuccess = true, errorMessage = null)
                    }
                }
                .onFailure { throwable ->
                    _uiState.update {
                        it.copy(
                            isSaving = false,
                            errorMessage = throwable.message ?: "Gagal menyimpan kehadiran"
                        )
                    }
                }
        }
    }

    fun onErrorConsumed() {
        _uiState.update { it.copy(errorMessage = null) }
    }

    private fun AttendanceSheetUiState.withDeductionPreview(): AttendanceSheetUiState {
        val preview = studentAttendanceList
            .filter { it.willDeductSession }
            .map { draft ->
                val currentBalance = draft.remainingPackageSessions ?: 0
                DeductionPreviewItem(
                    enrollmentId = draft.enrollmentId,
                    studentName = draft.studentName,
                    currentBalance = currentBalance,
                    projectedBalance = currentBalance - 1
                )
            }

        return copy(
            deductionPreview = preview,
            hasDeductionImpact = preview.isNotEmpty()
        )
    }
}
