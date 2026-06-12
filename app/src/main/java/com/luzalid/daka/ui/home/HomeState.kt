package com.luzalid.daka.ui.home

import com.luzalid.daka.ui.app.LocalAppAppearance
import com.luzalid.daka.ui.app.LocalDebugUiOutline
import com.luzalid.daka.ui.app.AppAppearance
import com.luzalid.daka.ui.app.appAppearance
import com.luzalid.daka.ui.app.appColorScheme
import com.luzalid.daka.ui.app.debugOutline

import androidx.annotation.DrawableRes
import com.luzalid.daka.R
import java.util.Locale

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
