package com.example.aquariumcontroller.ventilation

import android.content.res.Configuration
import androidx.compose.runtime.Composable
import androidx.compose.ui.tooling.preview.Preview

class VentilationButtonsComposableTest {
    @Preview(showBackground = true, uiMode = Configuration.UI_MODE_NIGHT_YES)
    @Preview(showBackground = true, uiMode = Configuration.UI_MODE_NIGHT_NO)
    @Composable
    fun VentilationButtonsComposablePortraitTest() {
        VentilationButtonsComposablePortraitPreview()
    }

    @Preview(
        showBackground = true,
        uiMode = Configuration.UI_MODE_NIGHT_YES,
        device = "spec:width=800dp,height=400dp,orientation=landscape"
    )
    @Preview(
        showBackground = true,
        uiMode = Configuration.UI_MODE_NIGHT_NO,
        device = "spec:width=800dp,height=400dp,orientation=landscape"
    )
    @Composable
    fun VentilationButtonsComposableLandscapeTest() {
        VentilationButtonsComposableLandscapePreview()
    }
}