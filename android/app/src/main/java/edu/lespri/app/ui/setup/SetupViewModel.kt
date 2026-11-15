package edu.lespri.app.ui.setup

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import dagger.hilt.android.lifecycle.HiltViewModel
import edu.lespri.app.data.local.dao.SettingDao
import edu.lespri.app.data.local.entity.AppSettingEntity
import edu.lespri.app.data.local.entity.OwnerProfileEntity
import edu.lespri.app.domain.model.AppSettingKey
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch
import java.util.UUID
import javax.inject.Inject

data class SetupUiState(
    val bimbelName: String = "",
    val ownerName: String = "",
    val whatsappNumber: String = "",
    val selectedLanguage: String = "id", // "id" or "en"
    val isSaving: Boolean = false,
    val isCompleted: Boolean = false,
    val errorMessage: String? = null
)

@HiltViewModel
class SetupViewModel @Inject constructor(
    private val settingDao: SettingDao
) : ViewModel() {

    private val _uiState = MutableStateFlow(SetupUiState())
    val uiState: StateFlow<SetupUiState> = _uiState.asStateFlow()

    fun onBimbelNameChanged(name: String) {
        _uiState.update { it.copy(bimbelName = name, errorMessage = null) }
    }

    fun onOwnerNameChanged(name: String) {
        _uiState.update { it.copy(ownerName = name) }
    }

    fun onWhatsappChanged(number: String) {
        _uiState.update { it.copy(whatsappNumber = number) }
    }

    fun onLanguageSelected(lang: String) {
        _uiState.update { it.copy(selectedLanguage = lang) }
    }

    fun completeSetup(onSuccess: () -> Unit) {
        val name = _uiState.value.bimbelName.trim()
        if (name.isBlank()) {
            _uiState.update { it.copy(errorMessage = "Nama bimbingan belajar wajib diisi") }
            return
        }

        viewModelScope.launch {
            _uiState.update { it.copy(isSaving = true) }
            val now = System.currentTimeMillis()

            val profile = OwnerProfileEntity(
                id = UUID.randomUUID().toString(),
                businessName = name,
                createdAt = now,
                updatedAt = now
            )
            settingDao.upsertOwnerProfile(profile)

            settingDao.setSetting(AppSettingEntity(AppSettingKey.BIMBEL_NAME.name, name))
            if (_uiState.value.ownerName.isNotBlank()) {
                settingDao.setSetting(AppSettingEntity(AppSettingKey.OWNER_NAME.name, _uiState.value.ownerName.trim()))
            }
            if (_uiState.value.whatsappNumber.isNotBlank()) {
                settingDao.setSetting(AppSettingEntity(AppSettingKey.WHATSAPP_NUMBER.name, _uiState.value.whatsappNumber.trim()))
            }

            _uiState.update { it.copy(isSaving = false, isCompleted = true) }
            onSuccess()
        }
    }
}
