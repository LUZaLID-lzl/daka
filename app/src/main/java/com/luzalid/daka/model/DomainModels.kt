package com.luzalid.daka.model

enum class MediaType(val dbValue: String) {
    Image("image"),
    Video("video");

    companion object {
        fun fromDbValue(value: String): MediaType =
            entries.firstOrNull { it.dbValue == value } ?: Image
    }
}

data class Recommendation(
    val id: String,
    val title: String,
    val description: String,
    val category: String,
    val imageAsset: String,
)

data class MediaAttachmentDraft(
    val id: String,
    val type: MediaType,
    val uri: String,
    val thumbnailUri: String? = null,
)

data class RecordSummary(
    val id: String,
    val title: String,
    val dateKey: String,
    val category: String,
    val updatedAt: Long,
    val content: String,
    val thumbnailUri: String?,
    val mediaType: MediaType?,
)

data class RecordDetail(
    val id: String,
    val recommendationId: String,
    val title: String,
    val dateKey: String,
    val category: String,
    val content: String,
    val mood: String,
    val location: String,
    val tags: String,
    val versionNumber: Int,
    val currentVersionId: String,
    val createdAt: Long,
    val updatedAt: Long,
)

data class RecordVersion(
    val id: String,
    val versionNumber: Int,
    val title: String,
    val content: String,
    val editedAt: Long,
    val isCurrent: Boolean,
)

data class MediaAttachment(
    val id: String,
    val type: MediaType,
    val uri: String,
    val thumbnailUri: String?,
    val sortOrder: Int,
)

data class PreferenceItem(
    val key: String,
    val value: String,
)
