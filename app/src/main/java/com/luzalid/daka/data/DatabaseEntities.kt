package com.luzalid.daka.data

import androidx.room.Entity
import androidx.room.Index
import androidx.room.PrimaryKey

@Entity(tableName = "recommendations")
data class RecommendationEntity(
    @PrimaryKey val id: String,
    val title: String,
    val description: String,
    val category: String,
    val imageAsset: String,
    val source: String = "local",
    val enabled: Boolean = true,
    val remoteId: String? = null,
    val syncStatus: String = "synced",
    val createdAt: Long,
    val updatedAt: Long,
)

@Entity(
    tableName = "check_in_records",
    indices = [
        Index("recommendationId"),
        Index("dateKey"),
        Index("currentVersionId"),
        Index("remoteId"),
    ],
)
data class CheckInRecordEntity(
    @PrimaryKey val id: String,
    val recommendationId: String,
    val title: String,
    val dateKey: String,
    val category: String,
    val currentVersionId: String,
    val remoteId: String? = null,
    val syncStatus: String = "pending_local",
    val createdAt: Long,
    val updatedAt: Long,
    val deletedAt: Long? = null,
)

@Entity(
    tableName = "check_in_versions",
    indices = [
        Index("recordId"),
        Index("remoteId"),
    ],
)
data class CheckInVersionEntity(
    @PrimaryKey val id: String,
    val recordId: String,
    val versionNumber: Int,
    val titleSnapshot: String,
    val contentSnapshot: String,
    val categorySnapshot: String,
    val moodSnapshot: String,
    val locationSnapshot: String,
    val tagsSnapshot: String,
    val remoteId: String? = null,
    val syncStatus: String = "pending_local",
    val editedAt: Long,
)

@Entity(
    tableName = "media_attachments",
    indices = [
        Index("recordId"),
        Index("versionId"),
        Index("remoteId"),
    ],
)
data class MediaAttachmentEntity(
    @PrimaryKey val id: String,
    val recordId: String,
    val versionId: String,
    val type: String,
    val uri: String,
    val thumbnailUri: String?,
    val sortOrder: Int,
    val remoteId: String? = null,
    val syncStatus: String = "pending_local",
    val createdAt: Long,
)

@Entity(tableName = "user_preferences")
data class UserPreferenceEntity(
    @PrimaryKey val key: String,
    val value: String,
    val remoteId: String? = null,
    val syncStatus: String = "pending_local",
    val updatedAt: Long,
)

data class RecordSummaryProjection(
    val id: String,
    val title: String,
    val dateKey: String,
    val category: String,
    val updatedAt: Long,
    val content: String?,
    val thumbnailUri: String?,
    val mediaType: String?,
)

data class RecordDetailProjection(
    val id: String,
    val recommendationId: String,
    val title: String,
    val dateKey: String,
    val category: String,
    val content: String?,
    val mood: String?,
    val location: String?,
    val tags: String?,
    val versionNumber: Int?,
    val currentVersionId: String,
    val createdAt: Long,
    val updatedAt: Long,
)

data class RecordVersionProjection(
    val id: String,
    val versionNumber: Int,
    val title: String,
    val content: String,
    val editedAt: Long,
    val currentVersionId: String?,
)
