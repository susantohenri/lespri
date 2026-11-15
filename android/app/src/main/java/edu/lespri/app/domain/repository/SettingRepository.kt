package edu.lespri.app.domain.repository

import edu.lespri.app.domain.model.AppSettingKey
import edu.lespri.app.domain.model.OwnerProfile
import kotlinx.coroutines.flow.Flow

/**
 * Kontrak repositori untuk profil pemilik bimbel dan pengaturan aplikasi key-value.
 */
interface SettingRepository {
    fun getOwnerProfile(): Flow<OwnerProfile?>
    suspend fun getOwnerProfileSync(): OwnerProfile?
    suspend fun saveOwnerProfile(profile: OwnerProfile): Result<Unit>
    fun getSetting(key: AppSettingKey): Flow<String?>
    suspend fun getSettingSync(key: AppSettingKey): String?
    suspend fun setSetting(key: AppSettingKey, value: String): Result<Unit>
}
