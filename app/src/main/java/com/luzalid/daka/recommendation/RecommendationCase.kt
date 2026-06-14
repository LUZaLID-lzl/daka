package com.luzalid.daka.recommendation

import android.content.res.Resources
import androidx.annotation.StringRes
import com.luzalid.daka.data.RecommendationEntity
import java.util.Locale

internal data class LocalizedText(
    val zh: String,
    val en: String,
)

internal data class RecommendationCase(
    val id: String,
    val title: LocalizedText,
    val description: LocalizedText,
    @StringRes val categoryRes: Int,
    val imageAsset: String,
) {
    fun toEntity(resources: Resources, now: Long): RecommendationEntity {
        val language = resources.configuration.locales[0]?.language
            ?: Locale.getDefault().language
        val useChinese = language.equals("zh", ignoreCase = true)
        return RecommendationEntity(
            id = id,
            title = if (useChinese) title.zh else title.en,
            description = if (useChinese) description.zh else description.en,
            category = resources.getString(categoryRes),
            imageAsset = imageAsset,
            createdAt = now,
            updatedAt = now,
        )
    }
}

internal fun text(zh: String, en: String) = LocalizedText(zh, en)

internal fun buildRecommendationCases(
    idPrefix: String,
    @StringRes categoryRes: Int,
    imageAsset: String,
    activities: List<LocalizedText>,
    contexts: List<LocalizedText>,
): List<RecommendationCase> {
    require(activities.size == 20) { "$idPrefix must define 20 activities" }
    require(contexts.size == 5) { "$idPrefix must define 5 contexts" }

    return activities.flatMapIndexed { activityIndex, activity ->
        contexts.mapIndexed { contextIndex, context ->
            val title = LocalizedText(
                zh = listOf(activity.zh, context.zh).filter(String::isNotBlank).joinToString(" · "),
                en = listOf(activity.en, context.en).filter(String::isNotBlank).joinToString(" - "),
            )
            RecommendationCase(
                id = "local-$idPrefix-${((activityIndex * contexts.size) + contextIndex + 1).toString().padStart(3, '0')}",
                title = title,
                description = LocalizedText(
                    zh = "完成“${title.zh}”，用一句话或一张照片记录过程和感受。",
                    en = "Complete \"${title.en}\", then capture the moment with one sentence or a photo.",
                ),
                categoryRes = categoryRes,
                imageAsset = imageAsset,
            )
        }
    }
}
