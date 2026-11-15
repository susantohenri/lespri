package edu.lespri.app.data.local.entity

import androidx.room.ColumnInfo
import androidx.room.Entity
import androidx.room.Index
import androidx.room.PrimaryKey

/**
 * Entitas preferensi pengingat/notifikasi lokal (pembayaran jatuh tempo, sesi mendatang, dll.).
 */
@Entity(
    tableName = "notification_preferences",
    indices = [
        Index(value = ["notification_type"], unique = true)
    ]
)
data class NotificationPreferenceEntity(
    @PrimaryKey
    @ColumnInfo(name = "id")
    val id: String,

    @ColumnInfo(name = "notification_type")
    val notificationType: String,

    @ColumnInfo(name = "is_enabled", defaultValue = "1")
    val isEnabled: Boolean = true,

    @ColumnInfo(name = "lead_time_minutes", defaultValue = "0")
    val leadTimeMinutes: Int = 0
)
