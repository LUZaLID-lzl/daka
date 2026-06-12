package com.luzalid.daka.data

import android.content.Context
import android.content.res.Resources
import com.luzalid.daka.R
import com.luzalid.daka.model.MediaAttachment
import com.luzalid.daka.model.MediaAttachmentDraft
import com.luzalid.daka.model.MediaType
import com.luzalid.daka.model.PreferenceItem
import com.luzalid.daka.model.Recommendation
import com.luzalid.daka.model.RecordDetail
import com.luzalid.daka.model.RecordSummary
import com.luzalid.daka.model.RecordVersion
import com.luzalid.daka.recommendation.RecommendationCatalog
import java.text.SimpleDateFormat
import java.util.Date
import java.util.Locale
import java.util.UUID
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.map

class ClickClackRepository(context: Context) {
    private val appContext = context.applicationContext
    private val dao = ClickClackDatabase.get(appContext).dao()

    suspend fun initialize() {
        dao.insertRecommendations(RecommendationCatalog.builtIns(appContext.resources))
        seedPreference("show_daily_reminder", "false")
        seedPreference("reminder_time", appContext.getString(R.string.preference_default_reminder_time))
        seedPreference("preferred_categories", "all_categories")
        seedPreference("theme_mode", "system")
        seedPreference("app_language", "system")
        seedPreference("background_style", "mist")
        seedPreference("media_strategy", "local_uri")
        seedPreference("debug_ui_outline", "false")
    }

    suspend fun todayRecommendation(resources: Resources = appContext.resources): Recommendation {
        val recommendations = dao.getEnabledRecommendations().ifEmpty {
            RecommendationCatalog.builtIns(appContext.resources).also { dao.insertRecommendations(it) }
        }
        val index = stableTodayIndex(recommendations.size)
        return RecommendationCatalog.localized(resources, recommendations[index]).toDomain()
    }

    suspend fun homeRecommendations(resources: Resources = appContext.resources): List<Recommendation> {
        val recommendations = dao.getEnabledRecommendations().ifEmpty {
            RecommendationCatalog.builtIns(appContext.resources).also { dao.insertRecommendations(it) }
        }
        val index = stableTodayIndex(recommendations.size)
        return (recommendations.drop(index) + recommendations.take(index))
            .map { RecommendationCatalog.localized(resources, it).toDomain() }
    }

    suspend fun getRecommendation(
        id: String,
        resources: Resources = appContext.resources,
    ): Recommendation? = dao.getRecommendation(id)
        ?.let { RecommendationCatalog.localized(resources, it) }
        ?.toDomain()

    fun observeTodayRecord(): Flow<RecordSummary?> =
        dao.observeLatestRecordForDate(todayKey()).map { it?.toDomain() }

    fun observeRecordSummaries(): Flow<List<RecordSummary>> =
        dao.observeRecordSummaries().map { records -> records.map { it.toDomain() } }

    fun observeRecordDetail(recordId: String): Flow<RecordDetail?> =
        dao.observeRecordDetail(recordId).map { it?.toDomain() }

    fun observeVersions(recordId: String): Flow<List<RecordVersion>> =
        dao.observeVersions(recordId).map { versions -> versions.map { it.toDomain() } }

    fun observeMediaForVersion(versionId: String): Flow<List<MediaAttachment>> =
        dao.observeMediaForVersion(versionId).map { media -> media.map { it.toDomain() } }

    suspend fun getMediaDraftsForVersion(versionId: String): List<MediaAttachmentDraft> =
        dao.getMediaForVersion(versionId).map {
            MediaAttachmentDraft(
                id = it.id,
                type = MediaType.fromDbValue(it.type),
                uri = it.uri,
                thumbnailUri = it.thumbnailUri,
            )
        }

    fun observePreferences(): Flow<List<PreferenceItem>> =
        dao.observePreferences().map { preferences -> preferences.map { PreferenceItem(it.key, it.value) } }

    suspend fun updatePreference(key: String, value: String) {
        dao.upsertPreference(UserPreferenceEntity(key = key, value = value, updatedAt = System.currentTimeMillis()))
    }

    suspend fun saveRecord(
        recordId: String?,
        recommendation: Recommendation,
        title: String,
        content: String,
        mood: String,
        location: String,
        tags: String,
        media: List<MediaAttachmentDraft>,
    ): String {
        val now = System.currentTimeMillis()
        val resolvedRecordId = recordId ?: UUID.randomUUID().toString()
        val existing = recordId?.let { dao.getRecord(it) }
        val versionNumber = dao.latestVersionNumber(resolvedRecordId) + 1
        val versionId = UUID.randomUUID().toString()
        val titleSnapshot = title.ifBlank { recommendation.title }
        val categorySnapshot = recommendation.category

        val record = CheckInRecordEntity(
            id = resolvedRecordId,
            recommendationId = recommendation.id,
            title = titleSnapshot,
            dateKey = existing?.dateKey ?: todayKey(),
            category = categorySnapshot,
            currentVersionId = versionId,
            remoteId = existing?.remoteId,
            syncStatus = "pending_local",
            createdAt = existing?.createdAt ?: now,
            updatedAt = now,
            deletedAt = existing?.deletedAt,
        )
        if (existing == null) {
            dao.insertRecord(record)
        } else {
            dao.updateRecord(record)
        }
        dao.insertVersion(
            CheckInVersionEntity(
                id = versionId,
                recordId = resolvedRecordId,
                versionNumber = versionNumber,
                titleSnapshot = titleSnapshot,
                contentSnapshot = content,
                categorySnapshot = categorySnapshot,
                moodSnapshot = mood,
                locationSnapshot = location,
                tagsSnapshot = tags,
                editedAt = now,
            ),
        )
        dao.insertMedia(
            media.mapIndexed { index, attachment ->
                MediaAttachmentEntity(
                    id = attachment.id.ifBlank { UUID.randomUUID().toString() },
                    recordId = resolvedRecordId,
                    versionId = versionId,
                    type = attachment.type.dbValue,
                    uri = attachment.uri,
                    thumbnailUri = attachment.thumbnailUri,
                    sortOrder = index,
                    createdAt = now,
                )
            },
        )
        return resolvedRecordId
    }

    private suspend fun seedPreference(key: String, value: String) {
        if (dao.getPreference(key) == null) {
            dao.upsertPreference(UserPreferenceEntity(key = key, value = value, updatedAt = System.currentTimeMillis()))
        }
    }
}

private fun RecommendationEntity.toDomain(): Recommendation =
    Recommendation(
        id = id,
        title = title,
        description = description,
        category = category,
        imageAsset = imageAsset,
    )

private fun RecordSummaryProjection.toDomain(): RecordSummary =
    RecordSummary(
        id = id,
        title = title,
        dateKey = dateKey,
        category = category,
        updatedAt = updatedAt,
        content = content.orEmpty(),
        thumbnailUri = thumbnailUri,
        mediaType = mediaType?.let(MediaType::fromDbValue),
    )

private fun RecordDetailProjection.toDomain(): RecordDetail =
    RecordDetail(
        id = id,
        recommendationId = recommendationId,
        title = title,
        dateKey = dateKey,
        category = category,
        content = content.orEmpty(),
        mood = mood.orEmpty(),
        location = location.orEmpty(),
        tags = tags.orEmpty(),
        versionNumber = versionNumber ?: 1,
        currentVersionId = currentVersionId,
        createdAt = createdAt,
        updatedAt = updatedAt,
    )

private fun RecordVersionProjection.toDomain(): RecordVersion =
    RecordVersion(
        id = id,
        versionNumber = versionNumber,
        title = title,
        content = content,
        editedAt = editedAt,
        isCurrent = id == currentVersionId,
    )

private fun MediaAttachmentEntity.toDomain(): MediaAttachment =
    MediaAttachment(
        id = id,
        type = MediaType.fromDbValue(type),
        uri = uri,
        thumbnailUri = thumbnailUri,
        sortOrder = sortOrder,
    )

private fun todayKey(): String = SimpleDateFormat("yyyy-MM-dd", Locale.CHINA).format(Date())

private fun stableTodayIndex(size: Int): Int {
    if (size <= 1) return 0
    val digits = todayKey().filter(Char::isDigit).toIntOrNull() ?: 0
    return ((digits % size) + (digits / 3 % size)) % size
}
