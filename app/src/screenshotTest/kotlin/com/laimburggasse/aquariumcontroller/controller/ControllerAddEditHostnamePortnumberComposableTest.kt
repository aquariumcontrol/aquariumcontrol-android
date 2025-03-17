package com.laimburggasse.aquariumcontroller.controller

import android.content.res.Configuration
import androidx.compose.runtime.Composable
import androidx.compose.ui.tooling.preview.Preview

@Suppress("unused")
class ControllerAddEditHostnamePortnumberComposableTest {
    @Preview(showBackground = true, uiMode = Configuration.UI_MODE_NIGHT_YES)
    @Preview(showBackground = true, uiMode = Configuration.UI_MODE_NIGHT_NO)
    @Composable
    fun ControllerAddEditHostnamePortnumberComposablePortraitTest() {
        ControllerAddEditHostnamePortnumberComposablePortraitPreview()
    }

    @Preview(
        showBackground = true,
        uiMode = Configuration.UI_MODE_NIGHT_YES,
        device = "spec:width=600dp,height=400dp,orientation=landscape"
    )
    @Preview(
        showBackground = true,
        uiMode = Configuration.UI_MODE_NIGHT_NO,
        device = "spec:width=600dp,height=400dp,orientation=landscape"
    )
    @Composable
    fun ControllerAddEditHostnamePortnumberComposableLandscapeTest() {
        ControllerAddEditHostnamePortnumberComposableLandscapePreview()
    }
}