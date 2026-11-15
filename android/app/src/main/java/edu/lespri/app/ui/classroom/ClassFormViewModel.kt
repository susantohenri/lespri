package edu.lespri.app.ui.classroom

import androidx.lifecycle.SavedStateHandle
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import dagger.hilt.android.lifecycle.HiltViewModel
import edu.lespri.app.domain.model.ClassGroup
import edu.lespri.app.domain.model.ClassType
import edu.lespri.app.domain.model.Tutor
import edu.lespri.app.domain.repository.ClassRepository
import edu.lespri.app.domain.repository.TutorRepository
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.firstOrNull
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch
import java.util.UUID
import javax.inject.Inject

/**
 * State UI untuk form tambah/edit kelas (P07).
 */
data class ClassFormUiState(
    val isLoading: Boolean = false,
    val isEditing: Boolean = false,
    val id: String = UUID.randomUUID().toString(),
    val name: String = "",
    val classType: ClassType = ClassType.GROUP,
    val subject: String = "",
    val tutorId: String? = null,
    val locationNote: String = "",
    val isActive: Boolean = true,
    val createdAt: Long = System.currentTimeMillis(),
    val tutors: List<Tutor> = emptyList(),
    val isSaving: Boolean = false,
    val savedSuccess: Boolean = false,
    val errorMessage: String? = null
)

/**
 * ViewModel form kelas (P07).
 */
@HiltViewModel
class ClassFormViewModel @Inject constructor(
    savedStateHandle: SavedStateHandle,
    private val classRepository: ClassRepository,
    private val tutorRepository: TutorRepository
) : ViewModel() {

    private val classId: String? = savedStateHandle.get<String>("classId")?.takeIf { it.isNotBlank() }

    private val _uiState = MutableStateFlow(ClassFormUiState(isLoading = true))
    val uiState: StateFlow<ClassFormUiState> = _uiState.asStateFlow()

    init {
        loadInitialData()
    }

    private fun loadInitialData() {
        viewModelScope.launch {
            val existing = classId?.let { classRepository.getClassDetail(it).firstOrNull() }
            val tutors = tutorRepository.getActiveTutors()
                .firstOrNull()
                .orEmpty()
                .toMutableList()

            // Tutor lama yang sudah diarsipkan tetap ditampilkan saat mengedit kelas.
            val assignedTutor = existing?.assignedTutor
            if (assignedTutor != null && tutors.none { it.id == assignedTutor.id }) {
                tutors.add(assignedTutor)
            }

            _uiState.update { current ->
                if (existing != null) {
                    current.copy(
                        isLoading = false,
                        isEditing = true,
                        id = existing.classGroup.id,
                        name = existing.classGroup.name,
                        classType = existing.classGroup.classType,
                        subject = existing.classGroup.subject.orEmpty(),
                        tutorId = existing.classGroup.tutorId,
                        locationNote = existing.classGroup.locationNote.orEmpty(),
                        isActive = existing.classGroup.status == "ACTIVE",
                        createdAt = existing.classGroup.createdAt,
                        tutors = tutors
                    )
                } else {
                    current.copy(
                        isLoading = false,
                        isEditing = false,
                        tutors = tutors,
                        errorMessage = if (classId != null) "Data kelas tidak ditemukan" else null
                    )
                }
            }
        }
    }

    fun onNameChanged(value: String) = _uiState.update { it.copy(name = value, errorMessage = null) }

    fun onClassTypeChanged(classType: ClassType) = _uiState.update { it.copy(classType = classType) }

    fun onSubjectChanged(value: String) = _uiState.update { it.copy(subject = value) }

    fun onTutorSelected(tutorId: String?) = _uiState.update { it.copy(tutorId = tutorId) }

    fun onLocationNoteChanged(value: String) = _uiState.update { it.copy(locationNote = value) }

    fun onActiveChanged(isActive: Boolean) = _uiState.update { it.copy(isActive = isActive) }

    fun save() {
        val state = _uiState.value
        val trimmedName = state.name.trim()
        if (trimmedName.isEmpty()) {
            _uiState.update { it.copy(errorMessage = "Nama kelas wajib diisi") }
            return
        }

        val classGroup = ClassGroup(
            id = state.id,
            name = trimmedName,
            classType = state.classType,
            subject = state.subject.trim().takeIf { it.isNotEmpty() },
            tutorId = state.tutorId,
            locationNote = state.locationNote.trim().takeIf { it.isNotEmpty() },
            status = if (state.isActive) "ACTIVE" else "ARCHIVED",
            createdAt = state.createdAt
        )

        viewModelScope.launch {
            _uiState.update { it.copy(isSaving = true, errorMessage = null) }
            classRepository.saveClass(classGroup)
                .onSuccess {
                    _uiState.update { it.copy(isSaving = false, savedSuccess = true) }
                }
                .onFailure { error ->
                    _uiState.update {
                        it.copy(
                            isSaving = false,
                            errorMessage = error.message ?: "Gagal menyimpan kelas"
                        )
                    }
                }
        }
    }
}
