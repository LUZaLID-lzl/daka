package com.luzalid.daka.ui.debug

import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.size
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.key
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalInspectionMode
import androidx.compose.ui.unit.dp
import app.rive.Fit
import app.rive.Result
import app.rive.Rive
import app.rive.RiveFileSource
import app.rive.rememberRiveFile
import app.rive.rememberRiveWorkerOrNull
import com.luzalid.daka.R
import com.luzalid.daka.ui.home.FoodMotionIllustration

/**
 * Renders the UI Lab Rive asset and keeps the Compose illustration as a safe fallback.
 */
@Composable
internal fun RiveMotionPreview(
    contentDescription: String,
    playing: Boolean,
    replayKey: Int,
    modifier: Modifier = Modifier,
) {
    if (LocalInspectionMode.current) {
        FoodMotionIllustration(
            contentDescription = contentDescription,
            modifier = modifier,
        )
    } else {
        key(replayKey) {
            val workerError = remember { mutableStateOf<Throwable?>(null) }
            val riveWorker = rememberRiveWorkerOrNull(workerError)
            if (riveWorker == null) {
                FoodMotionIllustration(
                    contentDescription = contentDescription,
                    modifier = modifier,
                )
            } else {
                val riveFileResult = rememberRiveFile(
                    source = RiveFileSource.RawRes.from(R.raw.ui_lab_motion),
                    riveWorker = riveWorker,
                )
                when (riveFileResult) {
                    Result.Loading -> Box(
                        modifier = modifier,
                        contentAlignment = Alignment.Center,
                    ) {
                        CircularProgressIndicator(modifier = Modifier.size(28.dp))
                    }

                    is Result.Error -> FoodMotionIllustration(
                        contentDescription = contentDescription,
                        modifier = modifier,
                    )

                    is Result.Success -> Rive(
                        file = riveFileResult.value,
                        playing = playing,
                        fit = Fit.Contain(),
                        modifier = modifier.fillMaxSize(),
                    )
                }
            }
        }
    }
}
