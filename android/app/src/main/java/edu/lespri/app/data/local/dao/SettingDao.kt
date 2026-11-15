package edu.lespri.app.data.local.dao

import androidx.room.Dao
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import edu.lespri.app.data.local.entity.AppSettingEntity
import edu.lespri.app.data.local.entity.OwnerProfileEntity
import kotlinx.coroutines.flow.Flow

/**
 * DAO untuk manajemen profil pemilik bimbel dan pengaturan aplikasi key-value.
 */
@Dao
interface SettingDao {

    @Query("SELECT * FROM owner_profile LIMIT 1")
    fun getOwnerProfile(): Flow<OwnerProfileEntity?>

    @Query("SELECT * FROM owner_profile LIMIT 1")
    suspend fun getOwnerProfileSync(): OwnerProfileEntity?

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun upsertOwnerProfile(profile: OwnerProfileEntity)

    @Query("SELECT setting_value FROM app_settings WHERE setting_key = :key")
    fun getSetting(key: String): Flow<String?>

    @Query("SELECT setting_value FROM app_settings WHERE setting_key = :key")
    suspend fun getSettingSync(key: String): String?

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun setSetting(setting: AppSettingEntity)

    /** Ambil semua pengaturan untuk backup JSON. */
    @Query("SELECT * FROM app_settings ORDER BY setting_key ASC")
    suspend fun getAllSettingsForBackup(): List<AppSettingEntity>
}
