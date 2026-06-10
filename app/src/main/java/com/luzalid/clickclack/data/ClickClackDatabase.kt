package com.luzalid.clickclack.data

import android.content.Context
import androidx.room.Database
import androidx.room.Room
import androidx.room.RoomDatabase

@Database(
    entities = [
        RecommendationEntity::class,
        CheckInRecordEntity::class,
        CheckInVersionEntity::class,
        MediaAttachmentEntity::class,
        UserPreferenceEntity::class,
    ],
    version = 1,
    exportSchema = true,
)
abstract class ClickClackDatabase : RoomDatabase() {
    abstract fun dao(): ClickClackDao

    companion object {
        @Volatile
        private var instance: ClickClackDatabase? = null

        fun get(context: Context): ClickClackDatabase =
            instance ?: synchronized(this) {
                instance ?: Room.databaseBuilder(
                    context.applicationContext,
                    ClickClackDatabase::class.java,
                    "click_clack.db",
                ).build().also { instance = it }
            }
    }
}
