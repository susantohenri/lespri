package edu.lespri.app.ui.student

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import dagger.hilt.android.lifecycle.HiltViewModel
import edu.lespri.app.domain.model.LespriConstants
import edu.lespri.app.domain.model.Student
import edu.lespri.app.domain.model.StudentListItem
import edu.lespri.app.domain.model.StudentStatus
import edu.lespri.app.domain.repository.ClassRepository
import edu.lespri.app.domain.repository.FinanceRepository
import edu.lespri.app.domain.repository.StudentRepository
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
 * State UI untuk daftar siswa (P03).
 */
data class StudentListUiState(
    val isLoading: Boolean = false,
    val searchQuery: String = "",
    val statusFilter: StudentStatus = StudentStatus.ACTIVE,
    val onlyWithDebt: Boolean = false,
    val students: List<StudentListItem> = emptyList(),
    val errorMessage: String? = null
)

/**
 * ViewModel untuk daftar siswa (P03).
 * Mendukung pencarian nama/wali/telepon, filter status, dan filter tunggakan.
 */
@OptIn(ExperimentalCoroutinesApi::class, FlowPreview::class)
@HiltViewModel
class StudentListViewModel @Inject constructor(
    private val studentRepository: StudentRepository,
    private val classRepository: ClassRepository,
    private val financeRepository: FinanceRepository
) : ViewModel() {

    private val _uiState = MutableStateFlow(StudentListUiState(isLoading = true))
    val uiState: StateFlow<StudentListUiState> = _uiState.asStateFlow()

    private val queryFlow = MutableStateFlow("")
    private val statusFilterFlow = MutableStateFlow(StudentStatus.ACTIVE)
    private val onlyWithDebtFlow = MutableStateFlow(false)

    init {
        observeStudents()
    }

    private fun observeStudents() {
        viewModelScope.launch {
            combine(
                queryFlow.debounce(LespriConstants.SEARCH_DEBOUNCE_MS).distinctUntilChanged(),
                statusFilterFlow,
                onlyWithDebtFlow
            ) { query, status, onlyWithDebt ->
                Triple(query, status, onlyWithDebt)
            }
                .flatMapLatest { (query, status, onlyWithDebt) ->
                    studentRepository.getStudentsByStatus(status).map { students ->
                        students
                            .filter { it.matchesQuery(query) }
                            .map { it.toListItem() }
                            .filter { !onlyWithDebt || it.totalDebt > 0L }
                    }
                }
                .catch { error ->
                    _uiState.update {
                        it.copy(
                            isLoading = false,
                            students = emptyList(),
                            errorMessage = error.message ?: "Gagal memuat daftar siswa"
                        )
                    }
                }
                .collect { students ->
                    _uiState.update {
                        it.copy(isLoading = false, students = students, errorMessage = null)
                    }
                }
        }
    }

    fun onQueryChanged(query: String) {
        _uiState.update { it.copy(searchQuery = query) }
        queryFlow.value = query
    }

    fun onStatusFilterChanged(status: StudentStatus) {
        _uiState.update { it.copy(statusFilter = status) }
        statusFilterFlow.value = status
    }

    fun onOnlyWithDebtChanged(enabled: Boolean) {
        _uiState.update { it.copy(onlyWithDebt = enabled) }
        onlyWithDebtFlow.value = enabled
    }

    private fun Student.matchesQuery(query: String): Boolean {
        val normalized = query.trim()
        if (normalized.isEmpty()) return true
        return name.contains(normalized, ignoreCase = true) ||
            guardianName.orEmpty().contains(normalized, ignoreCase = true) ||
            guardianPhone.orEmpty().contains(normalized, ignoreCase = true)
    }

    private suspend fun Student.toListItem(): StudentListItem {
        return StudentListItem(
            id = id,
            name = name,
            guardianPhone = guardianPhone,
            schoolGrade = schoolGrade,
            activeClassesCount = classRepository.countActiveEnrollmentsForStudent(id),
            totalDebt = financeRepository.getOutstandingForStudent(id),
            status = status
        )
    }
}
