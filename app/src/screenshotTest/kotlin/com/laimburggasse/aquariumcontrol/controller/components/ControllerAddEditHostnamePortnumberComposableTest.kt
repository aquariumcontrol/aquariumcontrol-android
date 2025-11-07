package com.laimburggasse.aquariumcontrol.controller.components

import android.content.res.Configuration
import androidx.compose.runtime.Composable
import androidx.compose.ui.tooling.preview.Preview
import com.android.tools.screenshot.PreviewTest
import com.laimburggasse.aquariumcontrol.controller.presentation.components.ControllerAddEditHostnamePortnumberComposableLandscapePreview
import com.laimburggasse.aquariumcontrol.controller.presentation.components.ControllerAddEditHostnamePortnumberComposablePortraitPreview

@Suppress("unused")
class ControllerAddEditHostnamePortnumberComposableTest {
    @PreviewTest
    @Preview(showBackground = true, uiMode = Configuration.UI_MODE_NIGHT_YES)
    @Preview(showBackground = true, uiMode = Configuration.UI_MODE_NIGHT_NO)
    @Composable
    fun ControllerAddEditHostnamePortnumberComposablePortraitTest() {
        ControllerAddEditHostnamePortnumberComposablePortraitPreview()
    }

    @PreviewTest
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