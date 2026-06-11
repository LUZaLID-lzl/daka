package com.luzalid.daka.ui.screens

import androidx.annotation.DrawableRes
import com.luzalid.daka.R
import java.text.DateFormat
import java.util.Date
import java.util.Locale

internal fun todayDateLabel(): String =
    DateFormat.getDateInstance(DateFormat.MEDIUM, Locale.getDefault()).format(Date())

@DrawableRes
internal fun categoryMotionImageRes(asset: String): Int = when (asset.lowercase(Locale.getDefault())) {
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
    else -> R.drawable.motion_food
}
