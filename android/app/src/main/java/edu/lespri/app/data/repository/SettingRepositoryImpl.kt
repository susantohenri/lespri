package edu.lespri.app.data.repository

import edu.lespri.app.data.local.dao.SettingDao
import edu.lespri.app.data.local.entity.AppSettingEntity
import edu.lespri.app.data.local.entity.OwnerProfileEntity
import edu.lespri.app.domain.model.AppSettingKey
import edu.lespri.app.domain.model.OwnerProfile
import edu.lespri.app.domain.repository.SettingRepository
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.map
import javax.inject.Inject
import javax.inject.Singleton

/**
 * Implementasi repositori pengaturan dan profil bimbel menggunakan [SettingDao].
 */
@Singleton
class SettingRepositoryImpl @Inject constructor(
    private val settingDao: SettingDao
) : SettingRepository {

    override fun getOwnerProfile(): Flow<OwnerProfile?> {
        return settingDao.getOwnerProfile().map { entity ->
            entity?.let {
                OwnerProfile(
                    id = it.id,
                    businessName = it.businessName,
                    createdAt = it.createdAt,
                    updatedAt = it.updatedAt
                )
            }
        }
    }

    override suspend fun getOwnerProfileSync(): OwnerProfile? {
        return settingDao.getOwnerProfileSync()?.let {
            OwnerProfile(
                id = it.id,
                businessName = it.businessName,
                createdAt = it.createdAt,
                updatedAt = it.updatedAt
            )
        }
    }

    override suspend fun saveOwnerProfile(profile: OwnerProfile): Result<Unit> {
        return runCatching {
            settingDao.upsertOwnerProfile(
                OwnerProfileEntity(
                    id = profile.id,
                    businessName = profile.businessName,
                    createdAt = profile.createdAt,
                    updatedAt = profile.updatedAt
                )
            )
        }
    }

    override fun getSetting(key: AppSettingKey): Flow<String?> {
        return settingDao.getSetting(key.name)
    }

    override suspend fun getSettingSync(key: AppSettingKey): String? {
        return settingDao.getSettingSync(key.name)
    }

    override suspend fun setSetting(key: AppSettingKey, value: String): Result<Unit> {
        return runCatching {
            settingDao.setSetting(
                AppSettingEntity(
                    settingKey = key.name,
                    settingValue = value
                )
            )
        }
    }
}
