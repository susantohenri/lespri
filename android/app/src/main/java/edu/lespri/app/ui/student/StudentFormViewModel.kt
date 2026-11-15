package edu.lespri.app.ui.student

import androidx.lifecycle.SavedStateHandle
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import dagger.hilt.android.lifecycle.HiltViewModel
import edu.lespri.app.data.local.dao.ClassDao
import edu.lespri.app.domain.model.BillingType
import edu.lespri.app.domain.model.ClassGroup
import edu.lespri.app.domain.model.Enrollment
import edu.lespri.app.domain.model.EnrollmentStatus
import edu.lespri.app.domain.model.LespriConstants
import edu.lespri.app.domain.model.Student
import edu.lespri.app.domain.model.StudentStatus
import edu.lespri.app.domain.repository.ClassRepository
import edu.lespri.app.domain.repository.StudentRepository
import edu.lespri.app.util.CurrencyFormatter
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.firstOrNull
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch
import java.util.UUID
import javax.inject.Inject

/**
 * State UI untuk form tambah/edit siswa (P04).
 */
data class StudentFormUiState(
    val isLoading: Boolean = false,
    val isEditing: Boolean = false,
    val id: String = UUID.randomUUID().toString(),
    val name: String = "",
    val guardianName: String = "",
    val guardianPhone: String = "",
    val schoolGrade: String = "",
    val notes: String = "",
    val isActive: Boolean = true,
    val joinedAt: Long = System.currentTimeMillis(),
    val availableClasses: List<ClassGroup> = emptyList(),
    val enrollToClass: Boolean = false,
    val selectedClassId: String? = null,
    val billingType: BillingType = BillingType.MONTHLY,
    val feeAmountInput: String = "",
    val dueDayInput: String = "",
    val packageSessionCountInput: String = "",
    val isSaving: Boolean = false,
    val savedSuccess: Boolean = false,
    val errorMessage: String? = null
)

/**
 * ViewModel form siswa (P04).
 * Menangani validasi data siswa dan opsional pendaftaran ke kelas sekaligus.
 */
@HiltViewModel
class StudentFormViewModel @Inject constructor(
    savedStateHandle: SavedStateHandle,
    private val studentRepository: StudentRepository,
    private val classRepository: ClassRepository,
    private val classDao: ClassDao
) : ViewModel() {

    private val studentId: String? = savedStateHandle.get<String>("studentId")?.takeIf { it.isNotBlank() }

    private val _uiState = MutableStateFlow(StudentFormUiState(isLoading = true))
    val uiState: StateFlow<StudentFormUiState> = _uiState.asStateFlow()

    init {
        loadInitialData()
    }

    private fun loadInitialData() {
        viewModelScope.launch {
            val existing = studentId?.let { studentRepository.getStudent(it).firstOrNull() }
            val enrolledClassIds = studentId?.let { id ->
                classDao.getActiveEnrollmentsForStudent(id)
                    .firstOrNull()
                    ?.map { it.classId }
                    ?.toSet()
                    .orEmpty()
            }.orEmpty()

            val availableClasses = classRepository.getClasses()
                .firstOrNull()
                .orEmpty()
                .filter { it.id !in enrolledClassIds }

            _uiState.update { current ->
                if (existing != null) {
                    current.copy(
                        isLoading = false,
                        isEditing = true,
                        id = existing.id,
                        name = existing.name,
                        guardianName = existing.guardianName.orEmpty(),
                        guardianPhone = existing.guardianPhone.orEmpty(),
                        schoolGrade = existing.schoolGrade.orEmpty(),
                        notes = existing.notes.orEmpty(),
                        isActive = existing.status == StudentStatus.ACTIVE,
                        joinedAt = existing.joinedAt,
                        availableClasses = availableClasses
                    )
                } else {
                    current.copy(
                        isLoading = false,
                        isEditing = false,
                        availableClasses = availableClasses,
                        errorMessage = if (studentId != null) "Data siswa tidak ditemukan" else null
                    )
                }
            }
        }
    }

    fun onNameChanged(value: String) = _uiState.update { it.copy(name = value, errorMessage = null) }

    fun onGuardianNameChanged(value: String) = _uiState.update { it.copy(guardianName = value) }

    fun onGuardianPhoneChanged(value: String) = _uiState.update { it.copy(guardianPhone = value) }

    fun onSchoolGradeChanged(value: String) = _uiState.update { it.copy(schoolGrade = value) }

    fun onNotesChanged(value: String) = _uiState.update { it.copy(notes = value) }

    fun onActiveChanged(isActive: Boolean) = _uiState.update { it.copy(isActive = isActive) }

    fun onEnrollToClassChanged(enabled: Boolean) {
        _uiState.update {
            it.copy(
                enrollToClass = enabled,
                selectedClassId = if (enabled) it.selectedClassId else null,
                errorMessage = null
            )
        }
    }

    fun onClassSelected(classId: String) {
        val selectedClass = _uiState.value.availableClasses.firstOrNull { it.id == classId }
        _uiState.update {
            it.copy(
                selectedClassId = classId,
                billingType = BillingType.MONTHLY,
                feeAmountInput = "",
                dueDayInput = "",
                packageSessionCountInput = "",
                errorMessage = if (selectedClass == null) {
                    "Kelas tidak ditemukan"
                } else {
                    null
                }
            )
        }
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

    fun save() {
        val state = _uiState.value
        val trimmedName = state.name.trim()
        if (trimmedName.isEmpty()) {
            _uiState.update { it.copy(errorMessage = "Nama siswa wajib diisi") }
            return
        }

        val dueDay = state.dueDayInput.toIntOrNull()
        val packageSessions = state.packageSessionCountInput.toIntOrNull()
        val feeAmount = CurrencyFormatter.parseRupiah(state.feeAmountInput)

        if (state.enrollToClass) {
            if (state.selectedClassId.isNullOrBlank()) {
                _uiState.update { it.copy(errorMessage = "Pilih kelas yang akan diikuti siswa") }
                return
            }
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
        }

        val student = Student(
            id = state.id,
            name = trimmedName,
            guardianName = state.guardianName.trim().takeIf { it.isNotEmpty() },
            guardianPhone = state.guardianPhone.trim().takeIf { it.isNotEmpty() },
            schoolGrade = state.schoolGrade.trim().takeIf { it.isNotEmpty() },
            notes = state.notes.trim().takeIf { it.isNotEmpty() },
            status = if (state.isActive) StudentStatus.ACTIVE else StudentStatus.ARCHIVED,
            joinedAt = state.joinedAt
        )

        viewModelScope.launch {
            _uiState.update { it.copy(isSaving = true, errorMessage = null) }
            studentRepository.saveStudent(student)
                .onFailure { error ->
                    _uiState.update {
                        it.copy(
                            isSaving = false,
                            errorMessage = error.message ?: "Gagal menyimpan siswa"
                        )
                    }
                }
                .onSuccess {
                    if (!state.enrollToClass) {
                        _uiState.update { it.copy(isSaving = false, savedSuccess = true) }
                        return@onSuccess
                    }

                    val enrollment = Enrollment(
                        id = UUID.randomUUID().toString(),
                        studentId = student.id,
                        classId = requireNotNull(state.selectedClassId),
                        billingType = state.billingType,
                        feeAmount = feeAmount,
                        dueDay = if (state.billingType == BillingType.MONTHLY) dueDay else null,
                        packageSessionCount = if (state.billingType == BillingType.PACKAGE) packageSessions else null,
                        status = EnrollmentStatus.ACTIVE
                    )

                    classRepository.enrollStudent(enrollment)
                        .onSuccess {
                            _uiState.update { it.copy(isSaving = false, savedSuccess = true) }
                        }
                        .onFailure { error ->
                            _uiState.update {
                                it.copy(
                                    isSaving = false,
                                    errorMessage = "Data siswa tersimpan, tetapi pendaftaran kelas gagal: " +
                                        (error.message ?: "kesalahan tidak diketahui")
                                )
                            }
                        }
                }
        }
    }
}
