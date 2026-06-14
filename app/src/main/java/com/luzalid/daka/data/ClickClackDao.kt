package com.luzalid.daka.data

import androidx.room.Dao
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import androidx.room.Update
import kotlinx.coroutines.flow.Flow

@Dao
interface ClickClackDao {
    @Query("SELECT COUNT(*) FROM recommendations")
    suspend fun countRecommendations(): Int

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertRecommendations(recommendations: List<RecommendationEntity>)

    @Query("SELECT * FROM recommendations WHERE enabled = 1 ORDER BY id")
    suspend fun getEnabledRecommendations(): List<RecommendationEntity>

    @Query("SELECT * FROM recommendations WHERE id = :id LIMIT 1")
    suspend fun getRecommendation(id: String): RecommendationEntity?

    @Query("SELECT DISTINCT recommendationId FROM check_in_records WHERE deletedAt IS NULL")
    suspend fun getRecordedRecommendationIds(): List<String>

    @Query(
        """
        SELECT
            r.id,
            r.recommendationId,
            r.title,
            r.dateKey,
            r.category,
            r.updatedAt,
            (SELECT contentSnapshot FROM check_in_versions WHERE id = r.currentVersionId) AS content,
            (SELECT uri FROM media_attachments WHERE versionId = r.currentVersionId ORDER BY sortOrder LIMIT 1) AS thumbnailUri,
            (SELECT type FROM media_attachments WHERE versionId = r.currentVersionId ORDER BY sortOrder LIMIT 1) AS mediaType
        FROM check_in_records r
        WHERE r.deletedAt IS NULL
        ORDER BY r.dateKey DESC, r.updatedAt DESC
        """
    )
    fun observeRecordSummaries(): Flow<List<RecordSummaryProjection>>

    @Query(
        """
        SELECT
            r.id,
            r.recommendationId,
            r.title,
            r.dateKey,
            r.category,
            r.updatedAt,
            (SELECT contentSnapshot FROM check_in_versions WHERE id = r.currentVersionId) AS content,
            (SELECT uri FROM media_attachments WHERE versionId = r.currentVersionId ORDER BY sortOrder LIMIT 1) AS thumbnailUri,
            (SELECT type FROM media_attachments WHERE versionId = r.currentVersionId ORDER BY sortOrder LIMIT 1) AS mediaType
        FROM check_in_records r
        WHERE r.deletedAt IS NULL AND r.dateKey = :dateKey
        ORDER BY r.updatedAt DESC
        LIMIT 1
        """
    )
    fun observeLatestRecordForDate(dateKey: String): Flow<RecordSummaryProjection?>

    @Query("SELECT * FROM check_in_records WHERE id = :recordId AND deletedAt IS NULL LIMIT 1")
    suspend fun getRecord(recordId: String): CheckInRecordEntity?

    @Query(
        """
        SELECT
            r.id,
            r.recommendationId,
            v.titleSnapshot AS title,
            r.dateKey,
            v.categorySnapshot AS category,
            v.contentSnapshot AS content,
            v.moodSnapshot AS mood,
            v.locationSnapshot AS location,
            v.tagsSnapshot AS tags,
            v.versionNumber AS versionNumber,
            r.currentVersionId,
            r.createdAt,
            r.updatedAt
        FROM check_in_records r
        LEFT JOIN check_in_versions v ON v.id = r.currentVersionId
        WHERE r.id = :recordId AND r.deletedAt IS NULL
        LIMIT 1
        """
    )
    fun observeRecordDetail(recordId: String): Flow<RecordDetailProjection?>

    @Query(
        """
        SELECT v.id, v.versionNumber, v.titleSnapshot AS title, v.contentSnapshot AS content,
            v.editedAt, r.currentVersionId
        FROM check_in_versions v
        INNER JOIN check_in_records r ON r.id = v.recordId
        WHERE v.recordId = :recordId
        ORDER BY v.versionNumber DESC
        """
    )
    fun observeVersions(recordId: String): Flow<List<RecordVersionProjection>>

    @Query("SELECT COALESCE(MAX(versionNumber), 0) FROM check_in_versions WHERE recordId = :recordId")
    suspend fun latestVersionNumber(recordId: String): Int

    @Query("SELECT * FROM media_attachments WHERE versionId = :versionId ORDER BY sortOrder")
    fun observeMediaForVersion(versionId: String): Flow<List<MediaAttachmentEntity>>

    @Query("SELECT * FROM media_attachments WHERE versionId = :versionId ORDER BY sortOrder")
    suspend fun getMediaForVersion(versionId: String): List<MediaAttachmentEntity>

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertRecord(record: CheckInRecordEntity)

    @Update
    suspend fun updateRecord(record: CheckInRecordEntity)

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertVersion(version: CheckInVersionEntity)

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertMedia(media: List<MediaAttachmentEntity>)

    @Query("SELECT * FROM user_preferences ORDER BY key")
    fun observePreferences(): Flow<List<UserPreferenceEntity>>

    @Query("SELECT * FROM user_preferences WHERE `key` = :key LIMIT 1")
    suspend fun getPreference(key: String): UserPreferenceEntity?

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun upsertPreference(preference: UserPreferenceEntity)
}
