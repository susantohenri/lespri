package edu.lespri.app.ui.settings

import android.app.Activity
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import dagger.hilt.android.lifecycle.HiltViewModel
import edu.lespri.app.domain.model.AppLanguage
import edu.lespri.app.domain.model.AppSettingKey
import edu.lespri.app.domain.model.AppTheme
import edu.lespri.app.domain.repository.SettingRepository
import edu.lespri.app.util.AdManager
import edu.lespri.app.util.AnalyticsLogger
import edu.lespri.app.util.ConsentManager
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
 * preferensi bahasa (i18n), mode tema (Light/Dark/System), dan threshold sesi.
 */
data class SettingsUiState(
    val bimbelsName: String = "",
    val ownerName: String = "",
    val whatsappNumber: String = "",
    val adsEnabled: Boolean = false,
    val lowSessionThreshold: String = "2",
    val language: AppLanguage = AppLanguage.SYSTEM,
    val theme: AppTheme = AppTheme.SYSTEM,
    val isPrivacyOptionsAvailable: Boolean = false,
    val isLoading: Boolean = false,
    val errorMessage: String? = null,
    val successMessage: String? = null
)

@HiltViewModel
class SettingsViewModel @Inject constructor(
    private val settingRepository: SettingRepository,
    private val adManager: AdManager,
    private val consentManager: ConsentManager,
    private val analyticsLogger: AnalyticsLogger
) : ViewModel() {

    private val _uiState = MutableStateFlow(SettingsUiState())
    val uiState: StateFlow<SettingsUiState> = _uiState.asStateFlow()

    init {
        loadSettings()
    }

    fun loadSettings() {
        viewModelScope.launch {
            val bimbelsName = settingRepository.getSettingSync(AppSettingKey.BIMBEL_NAME) ?: ""
            val ownerName = settingRepository.getSettingSync(AppSettingKey.OWNER_NAME) ?: ""
            val whatsappNumber = settingRepository.getSettingSync(AppSettingKey.WHATSAPP_NUMBER) ?: ""
            val adsEnabled = settingRepository.getSettingSync(AppSettingKey.ADS_ENABLED) == "true"
            val threshold = settingRepository.getSettingSync(AppSettingKey.LOW_SESSION_THRESHOLD) ?: "2"
            val languageStr = settingRepository.getSettingSync(AppSettingKey.LANGUAGE)
            val themeStr = settingRepository.getSettingSync(AppSettingKey.THEME)

            val language = AppLanguage.fromStorageValue(languageStr)
            val theme = AppTheme.fromStorageValue(themeStr)
            val privacyAvailable = consentManager.isPrivacyOptionsRequired()

            _uiState.value = SettingsUiState(
                bimbelsName = bimbelsName,
                ownerName = ownerName,
                whatsappNumber = whatsappNumber,
                adsEnabled = adsEnabled,
                lowSessionThreshold = threshold,
                language = language,
                theme = theme,
                isPrivacyOptionsAvailable = privacyAvailable
            )
        }
    }

    fun saveSettings() {
        val state = _uiState.value
        viewModelScope.launch {
            _uiState.update { it.copy(isLoading = true, errorMessage = null) }
            try {
                settingRepository.setSetting(AppSettingKey.BIMBEL_NAME, state.bimbelsName)
                settingRepository.setSetting(AppSettingKey.OWNER_NAME, state.ownerName)
                settingRepository.setSetting(AppSettingKey.WHATSAPP_NUMBER, state.whatsappNumber)
                settingRepository.setSetting(AppSettingKey.ADS_ENABLED, if (state.adsEnabled) "true" else "false")
                settingRepository.setSetting(AppSettingKey.LOW_SESSION_THRESHOLD, state.lowSessionThreshold)
                settingRepository.setSetting(AppSettingKey.LANGUAGE, state.language.storageValue)
                settingRepository.setSetting(AppSettingKey.THEME, state.theme.storageValue)

                adManager.updateAdsEnabled(state.adsEnabled)

                // Update User Properties & Catat Event ke Firebase Analytics
                analyticsLogger.setUserProperty("theme_preference", state.theme.storageValue)
                analyticsLogger.setUserProperty("language_preference", state.language.storageValue)
                analyticsLogger.setUserProperty("ads_enabled", if (state.adsEnabled) "true" else "false")

                analyticsLogger.logEvent(
                    "change_settings",
                    mapOf(
                        "theme" to state.theme.storageValue,
                        "language" to state.language.storageValue,
                        "ads_enabled" to state.adsEnabled
                    )
                )

                _uiState.update {
                    it.copy(isLoading = false, successMessage = "Pengaturan berhasil disimpan.")
                }
            } catch (e: Exception) {
                analyticsLogger.logException(e, "Gagal menyimpan pengaturan aplikasi")
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

    fun updateLanguage(language: AppLanguage) {
        _uiState.update { it.copy(language = language, successMessage = null) }
    }

    fun updateTheme(theme: AppTheme) {
        _uiState.update { it.copy(theme = theme, successMessage = null) }
    }

    fun showPrivacyOptions(activity: Activity) {
        consentManager.showPrivacyOptionsForm(activity) { error ->
            if (error != null) {
                analyticsLogger.logException(
                    Exception(error.message),
                    "Error displaying UMP privacy options"
                )
            }
        }
    }
}
