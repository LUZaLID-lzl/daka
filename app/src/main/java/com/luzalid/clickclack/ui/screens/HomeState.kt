package com.luzalid.clickclack.ui.screens

import androidx.annotation.DrawableRes
import com.luzalid.clickclack.R
import java.text.SimpleDateFormat
import java.util.Date
import java.util.Locale

internal fun todayDateLabel(): String {
    val day = SimpleDateFormat("d", Locale.US).format(Date()).toIntOrNull() ?: 1
    val suffix = when {
        day in 11..13 -> "TH"
        day % 10 == 1 -> "ST"
        day % 10 == 2 -> "ND"
        day % 10 == 3 -> "RD"
        else -> "TH"
    }
    val month = SimpleDateFormat("MMM", Locale.US).format(Date()).uppercase(Locale.US)
    return "$month ${day}${suffix}"
}

@DrawableRes
internal fun categoryMotionImageRes(asset: String, category: String): Int = when (asset.lowercase(Locale.getDefault())) {
    "food" -> R.drawable.motion_food
    "commute" -> R.drawable.motion_commute
    "sport" -> R.drawable.motion_sport
    "work" -> R.drawable.motion_work
    "fun" -> R.drawable.motion_fun
    "social" -> R.drawable.motion_social
    "relax" -> R.drawable.motion_relax
    "home" -> R.drawable.motion_home
    "study" -> R.drawable.motion_study
    "explore" -> R.drawable.motion_explore
    else -> when (category) {
        "美食" -> R.drawable.motion_food
        "通勤" -> R.drawable.motion_commute
        "运动" -> R.drawable.motion_sport
        "工作" -> R.drawable.motion_work
        "娱乐" -> R.drawable.motion_fun
        "社交" -> R.drawable.motion_social
        "放松" -> R.drawable.motion_relax
        "家务" -> R.drawable.motion_home
        "学习" -> R.drawable.motion_study
        "探索" -> R.drawable.motion_explore
        else -> R.drawable.motion_food
    }
}
