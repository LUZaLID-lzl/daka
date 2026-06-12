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
    "food" -> R.drawable.category_art_food
    "commute" -> R.drawable.category_art_commute
    "sport" -> R.drawable.category_art_sport
    "work" -> R.drawable.category_art_work
    "fun" -> R.drawable.category_art_fun
    "social" -> R.drawable.category_art_social
    "relax" -> R.drawable.category_art_relax
    "home" -> R.drawable.category_art_home
    "study" -> R.drawable.category_art_study
    "explore" -> R.drawable.category_art_explore
    else -> R.drawable.category_art_food
}
