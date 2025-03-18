package com.laimburggasse.aquariumcontrol.controller

import android.content.res.Configuration
import androidx.compose.runtime.Composable
import androidx.compose.ui.tooling.preview.Preview

@Suppress("unused")
class ControllerAddEditUsernamePasswordComposableTest {
    @Preview(showBackground = true, uiMode = Configuration.UI_MODE_NIGHT_YES)
    @Preview(showBackground = true, uiMode = Configuration.UI_MODE_NIGHT_NO)
    @Composable
    fun ControllerAddEditUsernamePasswordComposablePortraitTest() {
        ControllerAddEditUsernamePasswordComposablePortraitPreview()
    }

    @Preview(showBackground = true, uiMode = Configuration.UI_MODE_NIGHT_YES)
    @Preview(showBackground = true, uiMode = Configuration.UI_MODE_NIGHT_NO)
    @Composable
    fun ControllerAddEditUsernamePasswordComposableLandscapeTest() {
        ControllerAddEditUsernamePasswordComposableLandscapePreview()

    }
}