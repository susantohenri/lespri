package edu.lespri.app.ui.settings

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import dagger.hilt.android.lifecycle.HiltViewModel
import edu.lespri.app.data.local.dao.SettingDao
import edu.lespri.app.data.local.entity.AppSettingEntity
import edu.lespri.app.domain.model.AppSettingKey
import edu.lespri.app.util.AdManager
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch
import javax.inject.Inject

/**
 * State UI untuk layar Pengaturan (P24).
 *
 * Mengelola pengaturan aplikasi: nama bimbel, nomor WhatsApp, toggle iklan,
 * dan threshold saldo paket rendah. Data disimpan di [AppSettingEntity] melalui [SettingDao].
 */
data class SettingsUiState(
    val bimbelsName: String = "",
    val ownerName: String = "",
    val whatsappNumber: String = "",
    val adsEnabled: Boolean = false,
    val lowSessionThreshold: String = "2",
    val isLoading: Boolean = false,
    val errorMessage: String? = null,
    val successMessage: String? = null
)

@HiltViewModel
class SettingsViewModel @Inject constructor(
    private val settingDao: SettingDao,
    private val adManager: AdManager
) : ViewModel() {

    private val _uiState = MutableStateFlow(SettingsUiState())
    val uiState: StateFlow<SettingsUiState> = _uiState.asStateFlow()

    init {
        loadSettings()
    }

    fun loadSettings() {
        viewModelScope.launch {
            val bimbelsName = settingDao.getSettingSync(AppSettingKey.BIMBEL_NAME.name) ?: ""
            val ownerName = settingDao.getSettingSync(AppSettingKey.OWNER_NAME.name) ?: ""
            val whatsappNumber = settingDao.getSettingSync(AppSettingKey.WHATSAPP_NUMBER.name) ?: ""
            val adsEnabled = settingDao.getSettingSync(AppSettingKey.ADS_ENABLED.name) == "true"
            val threshold = settingDao.getSettingSync(AppSettingKey.LOW_SESSION_THRESHOLD.name) ?: "2"

            _uiState.value = SettingsUiState(
                bimbelsName = bimbelsName,
                ownerName = ownerName,
                whatsappNumber = whatsappNumber,
                adsEnabled = adsEnabled,
                lowSessionThreshold = threshold
            )
        }
    }

    fun saveSettings() {
        val state = _uiState.value
        viewModelScope.launch {
            _uiState.update { it.copy(isLoading = true, errorMessage = null) }
            try {
                settingDao.setSetting(AppSettingEntity(AppSettingKey.BIMBEL_NAME.name, state.bimbelsName))
                settingDao.setSetting(AppSettingEntity(AppSettingKey.OWNER_NAME.name, state.ownerName))
                settingDao.setSetting(AppSettingEntity(AppSettingKey.WHATSAPP_NUMBER.name, state.whatsappNumber))
                settingDao.setSetting(AppSettingEntity(AppSettingKey.ADS_ENABLED.name, if (state.adsEnabled) "true" else "false"))
                settingDao.setSetting(AppSettingEntity(AppSettingKey.LOW_SESSION_THRESHOLD.name, state.lowSessionThreshold))

                adManager.updateAdsEnabled(state.adsEnabled)

                _uiState.update {
                    it.copy(isLoading = false, successMessage = "Pengaturan berhasil disimpan.")
                }
            } catch (e: Exception) {
                _uiState.update { it.copy(isLoading = false, errorMessage = e.message) }
            }
        }
    }

    fun updateBimbelsName(value: String) {
        _uiState.update { it.copy(bimbelsName = value, successMessage = null) }
    }

    fun updateOwnerName(value: String) {
        _uiState.update { it.copy(ownerName = value, successMessage = null) }
    }

    fun updateWhatsAppNumber(value: String) {
        _uiState.update { it.copy(whatsappNumber = value, successMessage = null) }
    }

    fun toggleAdsEnabled(enabled: Boolean) {
        _uiState.update { it.copy(adsEnabled = enabled, successMessage = null) }
    }

    fun updateLowSessionThreshold(value: String) {
        _uiState.update { it.copy(lowSessionThreshold = value, successMessage = null) }
    }
}
