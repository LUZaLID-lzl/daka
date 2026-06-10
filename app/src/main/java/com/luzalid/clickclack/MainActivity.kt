package com.luzalid.clickclack

import android.graphics.Color
import android.os.Bundle
import androidx.activity.SystemBarStyle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import com.luzalid.clickclack.data.ClickClackRepository
import com.luzalid.clickclack.ui.screens.ClickClackApp
import com.luzalid.clickclack.ui.theme.ClickclackTheme

class MainActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge(
            statusBarStyle = SystemBarStyle.light(Color.TRANSPARENT, Color.TRANSPARENT),
            navigationBarStyle = SystemBarStyle.light(Color.TRANSPARENT, Color.TRANSPARENT),
        )
        setContent {
            ClickclackTheme(dynamicColor = false) {
                ClickClackApp(repository = ClickClackRepository(this))
            }
        }
    }
}
