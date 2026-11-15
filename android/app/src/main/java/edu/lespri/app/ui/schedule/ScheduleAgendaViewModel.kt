package edu.lespri.app.ui.schedule

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import dagger.hilt.android.lifecycle.HiltViewModel
import edu.lespri.app.domain.model.AgendaSessionItem
import edu.lespri.app.domain.model.ClassGroup
import edu.lespri.app.domain.model.LespriConstants
import edu.lespri.app.domain.model.Tutor
import edu.lespri.app.domain.repository.ClassRepository
import edu.lespri.app.domain.repository.ScheduleRepository
import edu.lespri.app.domain.repository.TutorRepository
import edu.lespri.app.util.DateTimeUtil
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.catch
import kotlinx.coroutines.flow.combine
import kotlinx.coroutines.flow.flatMapLatest
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch
import javax.inject.Inject

/**
 * Mode tampilan agenda jadwal (P12) sesuai SCREEN-MAP: Hari | Minggu | Agenda.
 */
enum class AgendaViewMode(val label: String) {
    DAY("Hari"),
    WEEK("Minggu"),
    AGENDA("Agenda")
}

/**
 * State UI untuk layar Jadwal / Agenda (P12).
 */
data class ScheduleAgendaUiState(
    val isLoading: Boolean = true,
    val viewMode: AgendaViewMode = AgendaViewMode.DAY,
    val selectedDate: String = DateTimeUtil.getTodayIsoDate(),
    val rangeStartDate: String = DateTimeUtil.getTodayIsoDate(),
    val rangeEndDate: String = DateTimeUtil.getTodayIsoDate(),
    val weekDates: List<String> = DateTimeUtil.getWeekDatesIso(DateTimeUtil.getTodayIsoDate()),
    val sessions: List<AgendaSessionItem> = emptyList(),
    val classes: List<ClassGroup> = emptyList(),
    val tutors: List<Tutor> = emptyList(),
    val selectedClassId: String? = null,
    val selectedTutorId: String? = null,
    val generatedSessionsCount: Int = 0,
    val errorMessage: String? = null
) {
    val isFiltered: Boolean
        get() = selectedClassId != null || selectedTutorId != null
}

/**
 * Parameter query agenda yang di-flatten menjadi Flow agar perubahan tanggal/filter
 * otomatis memicu ulang observasi data sesi.
 */
private data class AgendaQuery(
    val startDate: String,
    val endDate: String,
    val classId: String?,
    val tutorId: String?
)

/**
 * ViewModel untuk Agenda Jadwal (P12).
 * Menangani navigasi tanggal (hari/minggu/agenda), filter kelas & tutor, serta
 * memicu auto-generation sesi berulang dari ScheduleRule.
 */
@OptIn(ExperimentalCoroutinesApi::class)
@HiltViewModel
class ScheduleAgendaViewModel @Inject constructor(
    private val scheduleRepository: ScheduleRepository,
    private val classRepository: ClassRepository,
    private val tutorRepository: TutorRepository
) : ViewModel() {

    private val _uiState = MutableStateFlow(ScheduleAgendaUiState())
    val uiState: StateFlow<ScheduleAgendaUiState> = _uiState.asStateFlow()

    private val query = MutableStateFlow(
        AgendaQuery(
            startDate = DateTimeUtil.getTodayIsoDate(),
            endDate = DateTimeUtil.getTodayIsoDate(),
            classId = null,
            tutorId = null
        )
    )

    init {
        generateUpcomingSessions()
        observeAgenda()
    }

    /**
     * Trigger mesin jadwal berulang untuk [LespriConstants.SCHEDULE_GENERATION_WINDOW_DAYS] hari ke depan.
     * Idempoten: slot sesi yang sudah ada diabaikan oleh unique index (OnConflictStrategy.IGNORE).
     */
    fun generateUpcomingSessions() {
        viewModelScope.launch {
            val upToDate = DateTimeUtil.addDaysIso(
                DateTimeUtil.getTodayIsoDate(),
                LespriConstants.SCHEDULE_GENERATION_WINDOW_DAYS
            )
            scheduleRepository.generateRecurringSessions(upToDate)
                .onSuccess { inserted ->
                    if (inserted > 0) {
                        _uiState.update { it.copy(generatedSessionsCount = inserted) }
                    }
                }
        }
    }

    private fun observeAgenda() {
        viewModelScope.launch {
            query
                .flatMapLatest { activeQuery ->
                    combine(
                        scheduleRepository.getAgendaSessions(activeQuery.startDate, activeQuery.endDate),
                        classRepository.getClasses(),
                        tutorRepository.getActiveTutors()
                    ) { sessions, classes, tutors ->
                        val classNames = classes.associate { it.id to it.name }
                        val tutorNames = tutors.associate { it.id to it.name }

                        val agendaItems = sessions
                            .filter { activeQuery.classId == null || it.classId == activeQuery.classId }
                            .filter { activeQuery.tutorId == null || it.tutorId == activeQuery.tutorId }
                            .map { session ->
                                AgendaSessionItem(
                                    session = session,
                                    className = classNames[session.classId] ?: "Kelas",
                                    tutorName = session.tutorId?.let { tutorNames[it] }
                                )
                            }

                        Triple(agendaItems, classes, tutors)
                    }
                }
                .catch { throwable ->
                    _uiState.update {
                        it.copy(isLoading = false, errorMessage = throwable.message)
                    }
                }
                .collect { (agendaItems, classes, tutors) ->
                    _uiState.update {
                        it.copy(
                            isLoading = false,
                            sessions = agendaItems,
                            classes = classes,
                            tutors = tutors,
                            errorMessage = null
                        )
                    }
                }
        }
    }

    fun onViewModeSelected(mode: AgendaViewMode) {
        val currentDate = _uiState.value.selectedDate
        val (startDate, endDate) = computeRange(currentDate, mode)
        _uiState.update {
            it.copy(
                viewMode = mode,
                rangeStartDate = startDate,
                rangeEndDate = endDate
            )
        }
        updateQuery(startDate, endDate)
    }

    fun onPreviousPeriod() {
        shiftPeriod(-1)
    }

    fun onNextPeriod() {
        shiftPeriod(1)
    }

    fun onGoToToday() {
        val today = DateTimeUtil.getTodayIsoDate()
        val mode = _uiState.value.viewMode
        val (startDate, endDate) = computeRange(today, mode)
        _uiState.update {
            it.copy(
                selectedDate = today,
                rangeStartDate = startDate,
                rangeEndDate = endDate,
                weekDates = DateTimeUtil.getWeekDatesIso(today)
            )
        }
        updateQuery(startDate, endDate)
    }

    fun onDateSelected(isoDate: String) {
        val mode = _uiState.value.viewMode
        val (startDate, endDate) = computeRange(isoDate, mode)
        _uiState.update {
            it.copy(
                selectedDate = isoDate,
                rangeStartDate = startDate,
                rangeEndDate = endDate,
                weekDates = DateTimeUtil.getWeekDatesIso(isoDate)
            )
        }
        updateQuery(startDate, endDate)
    }

    fun onClassFilterSelected(classId: String?) {
        _uiState.update { it.copy(selectedClassId = classId) }
        updateFilters(classId = classId, tutorId = _uiState.value.selectedTutorId)
    }

    fun onTutorFilterSelected(tutorId: String?) {
        _uiState.update { it.copy(selectedTutorId = tutorId) }
        updateFilters(classId = _uiState.value.selectedClassId, tutorId = tutorId)
    }

    fun onClearFilters() {
        _uiState.update { it.copy(selectedClassId = null, selectedTutorId = null) }
        updateFilters(classId = null, tutorId = null)
    }

    private fun shiftPeriod(direction: Int) {
        val state = _uiState.value
        val delta = when (state.viewMode) {
            AgendaViewMode.DAY -> direction
            AgendaViewMode.WEEK -> direction * 7
            AgendaViewMode.AGENDA -> direction * LespriConstants.SCHEDULE_GENERATION_WINDOW_DAYS
        }
        onDateSelected(DateTimeUtil.addDaysIso(state.selectedDate, delta))
    }

    private fun updateQuery(startDate: String, endDate: String) {
        val state = _uiState.value
        query.value = AgendaQuery(
            startDate = startDate,
            endDate = endDate,
            classId = state.selectedClassId,
            tutorId = state.selectedTutorId
        )
    }

    private fun updateFilters(classId: String?, tutorId: String?) {
        val state = _uiState.value
        query.value = AgendaQuery(
            startDate = state.rangeStartDate,
            endDate = state.rangeEndDate,
            classId = classId,
            tutorId = tutorId
        )
    }

    private fun computeRange(isoDate: String, mode: AgendaViewMode): Pair<String, String> {
        return when (mode) {
            AgendaViewMode.DAY -> isoDate to isoDate
            AgendaViewMode.WEEK -> {
                val weekStart = DateTimeUtil.getWeekStartIso(isoDate)
                weekStart to DateTimeUtil.addDaysIso(weekStart, 6)
            }
            AgendaViewMode.AGENDA -> {
                val today = DateTimeUtil.getTodayIsoDate()
                today to DateTimeUtil.addDaysIso(
                    today,
                    LespriConstants.SCHEDULE_GENERATION_WINDOW_DAYS
                )
            }
        }
    }
}
