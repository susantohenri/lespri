package edu.lespri.app.ui.schedule

import androidx.lifecycle.SavedStateHandle
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import dagger.hilt.android.lifecycle.HiltViewModel
import edu.lespri.app.domain.model.ClassGroup
import edu.lespri.app.domain.model.LespriConstants
import edu.lespri.app.domain.model.LessonSession
import edu.lespri.app.domain.model.ScheduleRule
import edu.lespri.app.domain.model.SessionStatus
import edu.lespri.app.domain.model.Tutor
import edu.lespri.app.domain.repository.ClassRepository
import edu.lespri.app.domain.repository.ScheduleRepository
import edu.lespri.app.domain.repository.TutorRepository
import edu.lespri.app.util.DateTimeUtil
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch
import java.util.UUID
import javax.inject.Inject

/**
 * State UI untuk Form Jadwal / Sesi (P13).
 */
data class SessionFormUiState(
    val isLoading: Boolean = true,
    val isEditMode: Boolean = false,
    val isSaving: Boolean = false,
    val isSaved: Boolean = false,
    val isCancelled: Boolean = false,
    val classes: List<ClassGroup> = emptyList(),
    val tutors: List<Tutor> = emptyList(),
    val selectedClassId: String = "",
    val tutorId: String? = null,
    val sessionDate: String = DateTimeUtil.getTodayIsoDate(),
    val startTime: String = "16:00",
    val durationMinutes: String = "90",
    val repeatWeekly: Boolean = false,
    val status: SessionStatus = SessionStatus.SCHEDULED,
    val sourceRuleId: String? = null,
    val errorMessage: String? = null
) {
    val selectedClassName: String?
        get() = classes.firstOrNull { it.id == selectedClassId }?.name

    val selectedTutorName: String?
        get() = tutors.firstOrNull { it.id == tutorId }?.name
}

/**
 * ViewModel untuk Form Jadwal / Sesi (P13).
 * Mendukung pembuatan sesi satu kali (ad-hoc), jadwal rutin mingguan (ScheduleRule),
 * pengubahan jadwal (reschedule), dan pembatalan sesi.
 */
@HiltViewModel
class SessionFormViewModel @Inject constructor(
    private val scheduleRepository: ScheduleRepository,
    private val classRepository: ClassRepository,
    private val tutorRepository: TutorRepository,
    savedStateHandle: SavedStateHandle
) : ViewModel() {

    private val requestedSessionId: String =
        savedStateHandle.get<String>("sessionId").orEmpty()

    private val requestedClassId: String =
        savedStateHandle.get<String>("classId").orEmpty()

    private val _uiState = MutableStateFlow(SessionFormUiState())
    val uiState: StateFlow<SessionFormUiState> = _uiState.asStateFlow()

    init {
        loadInitialData()
    }

    private fun loadInitialData() {
        viewModelScope.launch {
            val classes = classRepository.getClasses().first()
            val tutors = tutorRepository.getActiveTutors().first()

            var newState = _uiState.value.copy(
                isLoading = false,
                classes = classes,
                tutors = tutors,
                selectedClassId = requestedClassId
            )

            if (requestedSessionId.isNotBlank()) {
                val detail = scheduleRepository.getSessionDetail(requestedSessionId).first()
                if (detail == null) {
                    newState = newState.copy(errorMessage = "Sesi tidak ditemukan")
                } else {
                    val session = detail.session
                    newState = newState.copy(
                        isEditMode = true,
                        selectedClassId = session.classId,
                        tutorId = session.tutorId,
                        sessionDate = session.sessionDate,
                        startTime = session.startTime,
                        durationMinutes = session.durationMinutes.toString(),
                        status = session.status,
                        sourceRuleId = session.sourceRuleId
                    )
                }
            } else {
                val fallbackClassId = newState.selectedClassId.ifBlank {
                    classes.firstOrNull()?.id.orEmpty()
                }
                newState = newState.copy(
                    selectedClassId = fallbackClassId,
                    tutorId = newState.tutorId
                        ?: classes.firstOrNull { it.id == fallbackClassId }?.tutorId
                )
            }

            _uiState.value = newState
        }
    }

    fun onClassSelected(classId: String) {
        _uiState.update { state ->
            val classTutorId = state.classes.firstOrNull { it.id == classId }?.tutorId
            state.copy(
                selectedClassId = classId,
                tutorId = state.tutorId ?: classTutorId,
                errorMessage = null
            )
        }
    }

    fun onTutorSelected(tutorId: String?) {
        _uiState.update { it.copy(tutorId = tutorId, errorMessage = null) }
    }

    fun onDateSelected(isoDate: String) {
        _uiState.update { it.copy(sessionDate = isoDate, errorMessage = null) }
    }

    fun onTimeSelected(time24: String) {
        _uiState.update { it.copy(startTime = time24, errorMessage = null) }
    }

    fun onDurationChanged(duration: String) {
        _uiState.update {
            it.copy(durationMinutes = duration.filter { char -> char.isDigit() }.take(3), errorMessage = null)
        }
    }

    fun onRepeatWeeklyChanged(enabled: Boolean) {
        _uiState.update { it.copy(repeatWeekly = enabled, errorMessage = null) }
    }

    fun onErrorConsumed() {
        _uiState.update { it.copy(errorMessage = null) }
    }

    fun saveSession() {
        val state = _uiState.value

        if (state.selectedClassId.isBlank()) {
            _uiState.update { it.copy(errorMessage = "Pilih kelas terlebih dahulu") }
            return
        }

        val duration = state.durationMinutes.toIntOrNull()
        if (duration == null || duration < 15 || duration > 240) {
            _uiState.update { it.copy(errorMessage = "Durasi harus antara 15 sampai 240 menit") }
            return
        }

        if (!isValidTime(state.startTime)) {
            _uiState.update { it.copy(errorMessage = "Jam mulai tidak valid (format HH:mm)") }
            return
        }

        val session = LessonSession(
            id = requestedSessionId.ifBlank { UUID.randomUUID().toString() },
            classId = state.selectedClassId,
            tutorId = state.tutorId,
            sessionDate = state.sessionDate,
            startTime = state.startTime,
            durationMinutes = duration,
            status = state.status,
            sourceRuleId = state.sourceRuleId
        )

        viewModelScope.launch {
            _uiState.update { it.copy(isSaving = true, errorMessage = null) }

            val result: Result<Unit> = when {
                state.isEditMode -> scheduleRepository.updateSession(session)

                state.repeatWeekly -> {
                    val rule = ScheduleRule(
                        id = UUID.randomUUID().toString(),
                        classId = session.classId,
                        tutorId = session.tutorId,
                        weekday = DateTimeUtil.getDayOfWeek(session.sessionDate),
                        startTime = session.startTime,
                        durationMinutes = session.durationMinutes,
                        effectiveFrom = session.sessionDate,
                        status = "ACTIVE"
                    )
                    scheduleRepository.saveScheduleRule(rule).mapCatching {
                        scheduleRepository
                            .generateRecurringSessions(
                                DateTimeUtil.addDaysIso(
                                    session.sessionDate,
                                    LespriConstants.SCHEDULE_GENERATION_WINDOW_DAYS
                                )
                            )
                            .getOrThrow()
                    }.map { }
                }

                else -> scheduleRepository.createAdHocSession(session)
            }

            result
                .onSuccess {
                    _uiState.update {
                        it.copy(isSaving = false, isSaved = true, errorMessage = null)
                    }
                }
                .onFailure { throwable ->
                    _uiState.update {
                        it.copy(isSaving = false, errorMessage = mapErrorMessage(throwable))
                    }
                }
        }
    }

    fun cancelSession() {
        if (requestedSessionId.isBlank()) {
            _uiState.update { it.copy(errorMessage = "Sesi belum tersimpan") }
            return
        }

        viewModelScope.launch {
            _uiState.update { it.copy(isSaving = true, errorMessage = null) }
            scheduleRepository.cancelSession(requestedSessionId)
                .onSuccess {
                    _uiState.update { it.copy(isSaving = false, isCancelled = true) }
                }
                .onFailure { throwable ->
                    _uiState.update {
                        it.copy(isSaving = false, errorMessage = mapErrorMessage(throwable))
                    }
                }
        }
    }

    private fun mapErrorMessage(throwable: Throwable): String {
        return when (throwable) {
            is android.database.sqlite.SQLiteConstraintException ->
                "Slot jadwal bentrok: kelas ini sudah punya sesi pada tanggal dan jam tersebut."
            else -> throwable.message ?: "Gagal menyimpan sesi"
        }
    }

    private fun isValidTime(time24: String): Boolean {
        val parts = time24.split(":")
        if (parts.size != 2) return false
        val hour = parts[0].toIntOrNull() ?: return false
        val minute = parts[1].toIntOrNull() ?: return false
        return hour in 0..23 && minute in 0..59
    }
}
