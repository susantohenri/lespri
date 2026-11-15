package edu.lespri.app.ui.classroom

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import dagger.hilt.android.lifecycle.HiltViewModel
import edu.lespri.app.domain.model.ClassGroup
import edu.lespri.app.domain.model.ClassListItem
import edu.lespri.app.domain.model.ClassType
import edu.lespri.app.domain.model.LespriConstants
import edu.lespri.app.domain.repository.ClassRepository
import edu.lespri.app.domain.repository.ScheduleRepository
import edu.lespri.app.domain.repository.TutorRepository
import edu.lespri.app.util.DateTimeUtil
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.FlowPreview
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.catch
import kotlinx.coroutines.flow.combine
import kotlinx.coroutines.flow.debounce
import kotlinx.coroutines.flow.distinctUntilChanged
import kotlinx.coroutines.flow.firstOrNull
import kotlinx.coroutines.flow.flatMapLatest
import kotlinx.coroutines.flow.map
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch
import javax.inject.Inject

/**
 * State UI untuk daftar kelas (P06).
 */
data class ClassListUiState(
    val isLoading: Boolean = false,
    val searchQuery: String = "",
    val typeFilter: ClassType? = null,
    val statusFilter: String = "ACTIVE",
    val classes: List<ClassListItem> = emptyList(),
    val errorMessage: String? = null
)

/**
 * ViewModel untuk daftar kelas (P06).
 */
@OptIn(ExperimentalCoroutinesApi::class, FlowPreview::class)
@HiltViewModel
class ClassListViewModel @Inject constructor(
    private val classRepository: ClassRepository,
    private val tutorRepository: TutorRepository,
    private val scheduleRepository: ScheduleRepository
) : ViewModel() {

    private val _uiState = MutableStateFlow(ClassListUiState(isLoading = true))
    val uiState: StateFlow<ClassListUiState> = _uiState.asStateFlow()

    private val queryFlow = MutableStateFlow("")
    private val typeFilterFlow = MutableStateFlow<ClassType?>(null)
    private val statusFilterFlow = MutableStateFlow("ACTIVE")

    init {
        observeClasses()
    }

    private fun observeClasses() {
        val today = DateTimeUtil.getTodayIsoDate()
        val endDate = DateTimeUtil.getIsoDatePlusDays(LespriConstants.SCHEDULE_GENERATION_WINDOW_DAYS)

        viewModelScope.launch {
            combine(
                queryFlow.debounce(LespriConstants.SEARCH_DEBOUNCE_MS).distinctUntilChanged(),
                typeFilterFlow,
                statusFilterFlow
            ) { query, type, status -> Triple(query, type, status) }
                .flatMapLatest { (query, type, status) ->
                    classRepository.getClassesByStatus(status).map { classes ->
                        classes
                            .filter { type == null || it.classType == type }
                            .map { it.toListItem(today, endDate) }
                            .filter { it.matchesQuery(query) }
                    }
                }
                .catch { error ->
                    _uiState.update {
                        it.copy(
                            isLoading = false,
                            classes = emptyList(),
                            errorMessage = error.message ?: "Gagal memuat daftar kelas"
                        )
                    }
                }
                .collect { classes ->
                    _uiState.update {
                        it.copy(isLoading = false, classes = classes, errorMessage = null)
                    }
                }
        }
    }

    fun onQueryChanged(query: String) {
        _uiState.update { it.copy(searchQuery = query) }
        queryFlow.value = query
    }

    fun onTypeFilterChanged(classType: ClassType?) {
        _uiState.update { it.copy(typeFilter = classType) }
        typeFilterFlow.value = classType
    }

    fun onStatusFilterChanged(status: String) {
        _uiState.update { it.copy(statusFilter = status) }
        statusFilterFlow.value = status
    }

    private fun ClassListItem.matchesQuery(query: String): Boolean {
        val normalized = query.trim()
        if (normalized.isEmpty()) return true
        return name.contains(normalized, ignoreCase = true) ||
            subject.orEmpty().contains(normalized, ignoreCase = true) ||
            tutorName.orEmpty().contains(normalized, ignoreCase = true)
    }

    private suspend fun ClassGroup.toListItem(today: String, endDate: String): ClassListItem {
        val tutorName = tutorId?.let { id ->
            tutorRepository.getTutor(id).firstOrNull()?.name
        }
        val nextSession = scheduleRepository.getSessionsForClassInRange(id, today, endDate)
            .firstOrNull()
            ?.firstOrNull()

        return ClassListItem(
            id = id,
            name = name,
            classType = classType,
            subject = subject,
            tutorName = tutorName,
            enrolledStudentsCount = classRepository.countActiveEnrollmentsForClass(id),
            nextSessionDate = nextSession?.sessionDate,
            nextSessionTime = nextSession?.startTime,
            status = status
        )
    }
}
