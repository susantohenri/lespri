package edu.lespri.app.ui.student

import androidx.lifecycle.SavedStateHandle
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import dagger.hilt.android.lifecycle.HiltViewModel
import edu.lespri.app.domain.model.BillingType
import edu.lespri.app.domain.model.ClassType
import edu.lespri.app.domain.model.SessionStatus
import edu.lespri.app.domain.model.Student
import edu.lespri.app.domain.model.StudentClassItem
import edu.lespri.app.domain.model.StudentUpcomingSessionItem
import edu.lespri.app.domain.repository.ClassRepository
import edu.lespri.app.domain.repository.FinanceRepository
import edu.lespri.app.domain.repository.ScheduleRepository
import edu.lespri.app.domain.repository.StudentRepository
import edu.lespri.app.domain.repository.TutorRepository
import edu.lespri.app.util.DateTimeUtil
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.catch
import kotlinx.coroutines.flow.combine
import kotlinx.coroutines.flow.firstOrNull
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch
import javax.inject.Inject

/**
 * State UI untuk detail siswa (P05).
 */
data class StudentDetailUiState(
    val isLoading: Boolean = false,
    val student: Student? = null,
    val classes: List<StudentClassItem> = emptyList(),
    val upcomingSessions: List<StudentUpcomingSessionItem> = emptyList(),
    val totalDebt: Long = 0L,
    val totalRemainingPackageSessions: Int = 0,
    val isArchiving: Boolean = false,
    val errorMessage: String? = null
)

/**
 * ViewModel untuk detail siswa (P05).
 * Menggabungkan profil, kelas aktif, saldo paket, tunggakan, dan sesi mendatang.
 */
@HiltViewModel
class StudentDetailViewModel @Inject constructor(
    savedStateHandle: SavedStateHandle,
    private val studentRepository: StudentRepository,
    private val scheduleRepository: ScheduleRepository,
    private val classRepository: ClassRepository,
    private val tutorRepository: TutorRepository,
    private val financeRepository: FinanceRepository
) : ViewModel() {

    private val studentId: String = checkNotNull(savedStateHandle["studentId"])

    private val _uiState = MutableStateFlow(StudentDetailUiState(isLoading = true))
    val uiState: StateFlow<StudentDetailUiState> = _uiState.asStateFlow()

    init {
        observeStudent()
    }

    private fun observeStudent() {
        val today = DateTimeUtil.getTodayIsoDate()
        val endDate = DateTimeUtil.getIsoDatePlusDays(60)

        viewModelScope.launch {
            combine(
                studentRepository.getStudent(studentId),
                classRepository.getActiveEnrollmentsForStudent(studentId),
                scheduleRepository.getAgendaSessions(today, endDate)
            ) { student, enrollments, sessions ->
                Triple(student, enrollments, sessions)
            }
                .catch { error ->
                    _uiState.update {
                        it.copy(
                            isLoading = false,
                            errorMessage = error.message ?: "Gagal memuat detail siswa"
                        )
                    }
                }
                .collect { (student, enrollments, sessions) ->
                    if (student == null) {
                        _uiState.update {
                            it.copy(
                                isLoading = false,
                                student = null,
                                errorMessage = "Data siswa tidak ditemukan"
                            )
                        }
                        return@collect
                    }

                    val classItems = enrollments.map { enrollment ->
                        val classGroup = classRepository.getClass(enrollment.classId).firstOrNull()
                        val tutorName = classGroup?.tutorId?.let { tutorId ->
                            tutorRepository.getTutor(tutorId).firstOrNull()?.name
                        }
                        val remainingSessions = if (enrollment.billingType == BillingType.PACKAGE) {
                            financeRepository.getRemainingPackageSessions(enrollment.id).firstOrNull()
                        } else {
                            null
                        }

                        StudentClassItem(
                            enrollmentId = enrollment.id,
                            classId = enrollment.classId,
                            className = classGroup?.name ?: "Kelas",
                            classType = classGroup?.classType ?: ClassType.GROUP,
                            subject = classGroup?.subject,
                            tutorName = tutorName,
                            billingType = enrollment.billingType,
                            feeAmount = enrollment.feeAmount,
                            remainingPackageSessions = remainingSessions,
                            currentDebt = financeRepository.getOutstandingForEnrollment(enrollment.id)
                        )
                    }

                    val classNamesById = classItems.associateBy { it.classId }
                    val upcomingSessions = sessions
                        .asSequence()
                        .filter { it.status != SessionStatus.CANCELLED && classNamesById.containsKey(it.classId) }
                        .map { session ->
                            StudentUpcomingSessionItem(
                                sessionId = session.id,
                                classId = session.classId,
                                className = classNamesById[session.classId]?.className ?: "Kelas",
                                sessionDate = session.sessionDate,
                                startTime = session.startTime,
                                status = session.status
                            )
                        }
                        .take(8)
                        .toList()

                    _uiState.update {
                        it.copy(
                            isLoading = false,
                            student = student,
                            classes = classItems,
                            upcomingSessions = upcomingSessions,
                            totalDebt = classItems.sumOf { item -> item.currentDebt },
                            totalRemainingPackageSessions = classItems.sumOf { item ->
                                item.remainingPackageSessions ?: 0
                            },
                            errorMessage = null
                        )
                    }
                }
        }
    }

    fun archiveStudent(onArchived: () -> Unit) {
        viewModelScope.launch {
            _uiState.update { it.copy(isArchiving = true, errorMessage = null) }
            studentRepository.archiveStudent(studentId)
                .onSuccess {
                    _uiState.update { it.copy(isArchiving = false) }
                    onArchived()
                }
                .onFailure { error ->
                    _uiState.update {
                        it.copy(
                            isArchiving = false,
                            errorMessage = error.message ?: "Gagal menonaktifkan siswa"
                        )
                    }
                }
        }
    }
}
