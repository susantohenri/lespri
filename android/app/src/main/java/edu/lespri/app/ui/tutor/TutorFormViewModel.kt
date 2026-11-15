package edu.lespri.app.ui.tutor

import androidx.lifecycle.SavedStateHandle
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import dagger.hilt.android.lifecycle.HiltViewModel
import edu.lespri.app.domain.model.Tutor
import edu.lespri.app.domain.model.TutorStatus
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
 * State UI untuk form tambah/edit tutor (P10).
 */
data class TutorFormUiState(
    val isLoading: Boolean = false,
    val isEditing: Boolean = false,
    val id: String = UUID.randomUUID().toString(),
    val name: String = "",
    val phone: String = "",
    val expertise: String = "",
    val notes: String = "",
    val isActive: Boolean = true,
    val createdAt: Long = System.currentTimeMillis(),
    val isSaving: Boolean = false,
    val savedSuccess: Boolean = false,
    val errorMessage: String? = null
)

/**
 * ViewModel form tutor (P10).
 */
@HiltViewModel
class TutorFormViewModel @Inject constructor(
    savedStateHandle: SavedStateHandle,
    private val tutorRepository: TutorRepository
) : ViewModel() {

    private val tutorId: String? = savedStateHandle.get<String>("tutorId")?.takeIf { it.isNotBlank() }

    private val _uiState = MutableStateFlow(TutorFormUiState(isLoading = true))
    val uiState: StateFlow<TutorFormUiState> = _uiState.asStateFlow()

    init {
        loadInitialData()
    }

    private fun loadInitialData() {
        viewModelScope.launch {
            val existing = tutorId?.let { tutorRepository.getTutor(it).firstOrNull() }
            _uiState.update { current ->
                if (existing != null) {
                    current.copy(
                        isLoading = false,
                        isEditing = true,
                        id = existing.id,
                        name = existing.name,
                        phone = existing.phone.orEmpty(),
                        expertise = existing.expertise.orEmpty(),
                        notes = existing.notes.orEmpty(),
                        isActive = existing.status == TutorStatus.ACTIVE,
                        createdAt = existing.createdAt
                    )
                } else {
                    current.copy(
                        isLoading = false,
                        isEditing = false,
                        errorMessage = if (tutorId != null) "Data tutor tidak ditemukan" else null
                    )
                }
            }
        }
    }

    fun onNameChanged(value: String) = _uiState.update { it.copy(name = value, errorMessage = null) }

    fun onPhoneChanged(value: String) = _uiState.update { it.copy(phone = value) }

    fun onExpertiseChanged(value: String) = _uiState.update { it.copy(expertise = value) }

    fun onNotesChanged(value: String) = _uiState.update { it.copy(notes = value) }

    fun onActiveChanged(isActive: Boolean) = _uiState.update { it.copy(isActive = isActive) }

    fun save() {
        val state = _uiState.value
        val trimmedName = state.name.trim()
        if (trimmedName.isEmpty()) {
            _uiState.update { it.copy(errorMessage = "Nama tutor wajib diisi") }
            return
        }

        val tutor = Tutor(
            id = state.id,
            name = trimmedName,
            phone = state.phone.trim().takeIf { it.isNotEmpty() },
            expertise = state.expertise.trim().takeIf { it.isNotEmpty() },
            notes = state.notes.trim().takeIf { it.isNotEmpty() },
            status = if (state.isActive) TutorStatus.ACTIVE else TutorStatus.ARCHIVED,
            createdAt = state.createdAt
        )

        viewModelScope.launch {
            _uiState.update { it.copy(isSaving = true, errorMessage = null) }
            tutorRepository.saveTutor(tutor)
                .onSuccess {
                    _uiState.update { it.copy(isSaving = false, savedSuccess = true) }
                }
                .onFailure { error ->
                    _uiState.update {
                        it.copy(
                            isSaving = false,
                            errorMessage = error.message ?: "Gagal menyimpan tutor"
                        )
                    }
                }
        }
    }
}
