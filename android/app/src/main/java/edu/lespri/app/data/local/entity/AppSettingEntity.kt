package edu.lespri.app.data.local.entity

import androidx.room.ColumnInfo
import androidx.room.Entity
import androidx.room.PrimaryKey

/**
 * Entitas penyimpanan pengaturan aplikasi berbasis key-value.
 */
@Entity(tableName = "app_settings")
data class AppSettingEntity(
    @PrimaryKey
    @ColumnInfo(name = "setting_key")
    val settingKey: String,

    @ColumnInfo(name = "setting_value")
    val settingValue: String
)
