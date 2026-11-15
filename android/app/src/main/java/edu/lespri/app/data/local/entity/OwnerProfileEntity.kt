package edu.lespri.app.data.local.entity

import androidx.room.ColumnInfo
import androidx.room.Entity
import androidx.room.PrimaryKey

/**
 * Entitas profil pemilik bimbel.
 * Menyimpan data dasar bimbel untuk kustomisasi aplikasi dan laporan.
 */
@Entity(tableName = "owner_profile")
data class OwnerProfileEntity(
    @PrimaryKey
    @ColumnInfo(name = "id")
    val id: String,

    @ColumnInfo(name = "business_name")
    val businessName: String,

    @ColumnInfo(name = "created_at")
    val createdAt: Long,

    @ColumnInfo(name = "updated_at")
    val updatedAt: Long
)
