package edu.lespri.app.ui.tutor

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import dagger.hilt.android.lifecycle.HiltViewModel
import edu.lespri.app.domain.model.LespriConstants
import edu.lespri.app.domain.model.Tutor
import edu.lespri.app.domain.model.TutorListItem
import edu.lespri.app.domain.model.TutorStatus
import edu.lespri.app.domain.repository.ClassRepository
import edu.lespri.app.domain.repository.TutorRepository
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.FlowPreview
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.catch
import kotlinx.coroutines.flow.combine
import kotlinx.coroutines.flow.debounce
import kotlinx.coroutines.flow.distinctUntilChanged
import kotlinx.coroutines.flow.flatMapLatest
import kotlinx.coroutines.flow.map
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch
import javax.inject.Inject

/**
 * State UI untuk daftar tutor (P09).
 */
data class TutorListUiState(
    val isLoading: Boolean = false,
    val searchQuery: String = "",
    val statusFilter: TutorStatus = TutorStatus.ACTIVE,
    val tutors: List<TutorListItem> = emptyList(),
    val errorMessage: String? = null
)

/**
 * ViewModel untuk daftar tutor (P09).
 */
@OptIn(ExperimentalCoroutinesApi::class, FlowPreview::class)
@HiltViewModel
class TutorListViewModel @Inject constructor(
    private val tutorRepository: TutorRepository,
    private val classRepository: ClassRepository
) : ViewModel() {

    private val _uiState = MutableStateFlow(TutorListUiState(isLoading = true))
    val uiState: StateFlow<TutorListUiState> = _uiState.asStateFlow()

    private val queryFlow = MutableStateFlow("")
    private val statusFilterFlow = MutableStateFlow(TutorStatus.ACTIVE)

    init {
        observeTutors()
    }

    private fun observeTutors() {
        viewModelScope.launch {
            combine(
                queryFlow.debounce(LespriConstants.SEARCH_DEBOUNCE_MS).distinctUntilChanged(),
                statusFilterFlow
            ) { query, status -> query to status }
                .flatMapLatest { (query, status) ->
                    tutorRepository.getTutorsByStatus(status).map { tutors ->
                        tutors
                            .filter { it.matchesQuery(query) }
                            .map { it.toListItem() }
                    }
                }
                .catch { error ->
                    _uiState.update {
                        it.copy(
                            isLoading = false,
                            tutors = emptyList(),
                            errorMessage = error.message ?: "Gagal memuat daftar tutor"
                        )
                    }
                }
                .collect { tutors ->
                    _uiState.update {
                        it.copy(isLoading = false, tutors = tutors, errorMessage = null)
                    }
                }
        }
    }

    fun onQueryChanged(query: String) {
        _uiState.update { it.copy(searchQuery = query) }
        queryFlow.value = query
    }

    fun onStatusFilterChanged(status: TutorStatus) {
        _uiState.update { it.copy(statusFilter = status) }
        statusFilterFlow.value = status
    }

    private fun Tutor.matchesQuery(query: String): Boolean {
        val normalized = query.trim()
        if (normalized.isEmpty()) return true
        return name.contains(normalized, ignoreCase = true) ||
            phone.orEmpty().contains(normalized, ignoreCase = true) ||
            expertise.orEmpty().contains(normalized, ignoreCase = true)
    }

    private suspend fun Tutor.toListItem(): TutorListItem {
        return TutorListItem(
            id = id,
            name = name,
            phone = phone,
            expertise = expertise,
            activeClassesCount = classRepository.countActiveClassesForTutor(id),
            status = status
        )
    }
}
