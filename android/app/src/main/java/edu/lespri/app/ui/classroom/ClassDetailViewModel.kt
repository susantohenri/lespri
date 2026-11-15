package edu.lespri.app.ui.classroom

import androidx.lifecycle.SavedStateHandle
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import dagger.hilt.android.lifecycle.HiltViewModel
import edu.lespri.app.domain.model.BillingType
import edu.lespri.app.domain.model.ClassGroup
import edu.lespri.app.domain.model.ClassType
import edu.lespri.app.domain.model.EnrolledStudentItem
import edu.lespri.app.domain.model.Enrollment
import edu.lespri.app.domain.model.EnrollmentStatus
import edu.lespri.app.domain.model.LessonSession
import edu.lespri.app.domain.model.LespriConstants
import edu.lespri.app.domain.model.Student
import edu.lespri.app.domain.model.Tutor
import edu.lespri.app.domain.repository.ClassRepository
import edu.lespri.app.domain.repository.StudentRepository
import edu.lespri.app.util.CurrencyFormatter
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.catch
import kotlinx.coroutines.flow.combine
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch
import java.util.UUID
import javax.inject.Inject

/**
 * State UI untuk detail kelas (P08), termasuk dialog pendaftaran siswa.
 */
data class ClassDetailUiState(
    val isLoading: Boolean = false,
    val classGroup: ClassGroup? = null,
    val assignedTutor: Tutor? = null,
    val enrolledStudents: List<EnrolledStudentItem> = emptyList(),
    val upcomingSessions: List<LessonSession> = emptyList(),
    val availableStudents: List<Student> = emptyList(),
    val showEnrollDialog: Boolean = false,
    val selectedStudentId: String? = null,
    val billingType: BillingType = BillingType.MONTHLY,
    val feeAmountInput: String = "",
    val dueDayInput: String = "",
    val packageSessionCountInput: String = "",
    val isSavingEnrollment: Boolean = false,
    val isRemovingEnrollment: Boolean = false,
    val isArchiving: Boolean = false,
    val actionMessage: String? = null,
    val errorMessage: String? = null
)

/**
 * ViewModel untuk detail kelas (P08).
 * Mengelola daftar siswa terdaftar, preview sesi, serta enrollment engine
 * dengan validasi kelas privat, duplikasi, dan skema pembayaran.
 */
@HiltViewModel
class ClassDetailViewModel @Inject constructor(
    savedStateHandle: SavedStateHandle,
    private val classRepository: ClassRepository,
    private val studentRepository: StudentRepository
) : ViewModel() {

    private val classId: String = checkNotNull(savedStateHandle["classId"])

    private val _uiState = MutableStateFlow(ClassDetailUiState(isLoading = true))
    val uiState: StateFlow<ClassDetailUiState> = _uiState.asStateFlow()

    init {
        observeClassDetail()
    }

    private fun observeClassDetail() {
        viewModelScope.launch {
            combine(
                classRepository.getClassDetail(classId),
                studentRepository.getActiveStudents()
            ) { detail, students -> detail to students }
                .catch { error ->
                    _uiState.update {
                        it.copy(
                            isLoading = false,
                            errorMessage = error.message ?: "Gagal memuat detail kelas"
                        )
                    }
                }
                .collect { (detail, students) ->
                    if (detail == null) {
                        _uiState.update {
                            it.copy(
                                isLoading = false,
                                classGroup = null,
                                errorMessage = "Data kelas tidak ditemukan"
                            )
                        }
                        return@collect
                    }

                    val enrolledIds = detail.enrolledStudents.map { it.studentId }.toSet()
                    _uiState.update { current ->
                        current.copy(
                            isLoading = false,
                            classGroup = detail.classGroup,
                            assignedTutor = detail.assignedTutor,
                            enrolledStudents = detail.enrolledStudents,
                            upcomingSessions = detail.upcomingSessions,
                            availableStudents = students.filter { it.id !in enrolledIds },
                            errorMessage = null
                        )
                    }
                }
        }
    }

    fun openEnrollDialog() {
        _uiState.update {
            it.copy(
                showEnrollDialog = true,
                selectedStudentId = null,
                billingType = BillingType.MONTHLY,
                feeAmountInput = "",
                dueDayInput = "",
                packageSessionCountInput = "",
                errorMessage = null
            )
        }
    }

    fun dismissEnrollDialog() {
        if (_uiState.value.isSavingEnrollment) return
        _uiState.update { it.copy(showEnrollDialog = false, errorMessage = null) }
    }

    fun onStudentSelected(studentId: String) {
        _uiState.update { it.copy(selectedStudentId = studentId, errorMessage = null) }
    }

    fun onBillingTypeChanged(billingType: BillingType) {
        _uiState.update {
            it.copy(
                billingType = billingType,
                dueDayInput = if (billingType == BillingType.MONTHLY) it.dueDayInput else "",
                packageSessionCountInput = if (billingType == BillingType.PACKAGE) {
                    it.packageSessionCountInput
                } else {
                    ""
                }
            )
        }
    }

    fun onFeeAmountChanged(value: String) {
        _uiState.update { it.copy(feeAmountInput = value.filter { char -> char.isDigit() }) }
    }

    fun onDueDayChanged(value: String) {
        _uiState.update { it.copy(dueDayInput = value.filter { char -> char.isDigit() }.take(2)) }
    }

    fun onPackageSessionCountChanged(value: String) {
        _uiState.update { it.copy(packageSessionCountInput = value.filter { char -> char.isDigit() }.take(3)) }
    }

    fun enrollStudent() {
        val state = _uiState.value
        val selectedStudentId = state.selectedStudentId
        if (selectedStudentId.isNullOrBlank()) {
            _uiState.update { it.copy(errorMessage = "Pilih siswa yang akan didaftarkan") }
            return
        }

        val classGroup = state.classGroup
        if (classGroup == null) {
            _uiState.update { it.copy(errorMessage = "Data kelas belum siap") }
            return
        }

        if (classGroup.classType == ClassType.PRIVATE && state.enrolledStudents.isNotEmpty()) {
            _uiState.update { it.copy(errorMessage = "Kelas privat hanya boleh memiliki satu siswa aktif") }
            return
        }

        val dueDay = state.dueDayInput.toIntOrNull()
        val packageSessions = state.packageSessionCountInput.toIntOrNull()

        when (state.billingType) {
            BillingType.MONTHLY -> {
                if (state.dueDayInput.isNotBlank() && (dueDay == null || dueDay !in 1..28)) {
                    _uiState.update { it.copy(errorMessage = "Tanggal jatuh tempo harus 1–28") }
                    return
                }
            }
            BillingType.PACKAGE -> {
                if (packageSessions == null ||
                    packageSessions !in LespriConstants.MIN_SESSIONS_PER_PACKAGE..LespriConstants.MAX_SESSIONS_PER_PACKAGE
                ) {
                    _uiState.update { it.copy(errorMessage = "Jumlah sesi paket harus 1–100") }
                    return
                }
            }
            BillingType.PER_SESSION -> Unit
        }

        val enrollment = Enrollment(
            id = UUID.randomUUID().toString(),
            studentId = selectedStudentId,
            classId = classId,
            billingType = state.billingType,
            feeAmount = CurrencyFormatter.parseRupiah(state.feeAmountInput),
            dueDay = if (state.billingType == BillingType.MONTHLY) dueDay else null,
            packageSessionCount = if (state.billingType == BillingType.PACKAGE) packageSessions else null,
            status = EnrollmentStatus.ACTIVE
        )

        viewModelScope.launch {
            _uiState.update { it.copy(isSavingEnrollment = true, errorMessage = null) }
            classRepository.enrollStudent(enrollment)
                .onSuccess {
                    _uiState.update {
                        it.copy(
                            isSavingEnrollment = false,
                            showEnrollDialog = false,
                            selectedStudentId = null,
                            feeAmountInput = "",
                            dueDayInput = "",
                            packageSessionCountInput = "",
                            actionMessage = "Siswa berhasil didaftarkan ke kelas"
                        )
                    }
                }
                .onFailure { error ->
                    _uiState.update {
                        it.copy(
                            isSavingEnrollment = false,
                            errorMessage = error.message ?: "Gagal mendaftarkan siswa"
                        )
                    }
                }
        }
    }

    fun removeEnrollment(enrollmentId: String) {
        viewModelScope.launch {
            _uiState.update { it.copy(isRemovingEnrollment = true, errorMessage = null) }
            classRepository.removeEnrollment(enrollmentId)
                .onSuccess {
                    _uiState.update {
                        it.copy(
                            isRemovingEnrollment = false,
                            actionMessage = "Siswa dikeluarkan dari kelas"
                        )
                    }
                }
                .onFailure { error ->
                    _uiState.update {
                        it.copy(
                            isRemovingEnrollment = false,
                            errorMessage = error.message ?: "Gagal mengeluarkan siswa"
                        )
                    }
                }
        }
    }

    fun archiveClass(onArchived: () -> Unit) {
        viewModelScope.launch {
            _uiState.update { it.copy(isArchiving = true, errorMessage = null) }
            classRepository.archiveClass(classId)
                .onSuccess {
                    _uiState.update { it.copy(isArchiving = false) }
                    onArchived()
                }
                .onFailure { error ->
                    _uiState.update {
                        it.copy(
                            isArchiving = false,
                            errorMessage = error.message ?: "Gagal menonaktifkan kelas"
                        )
                    }
                }
        }
    }

    fun dismissActionMessage() {
        _uiState.update { it.copy(actionMessage = null) }
    }
}
