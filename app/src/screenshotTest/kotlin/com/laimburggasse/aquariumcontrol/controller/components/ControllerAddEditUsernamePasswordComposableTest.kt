package com.laimburggasse.aquariumcontrol.controller.components

import android.content.res.Configuration
import androidx.compose.runtime.Composable
import androidx.compose.ui.tooling.preview.Preview
import com.android.tools.screenshot.PreviewTest
import com.laimburggasse.aquariumcontrol.controller.presentation.components.ControllerAddEditUsernamePasswordComposableLandscapePreview
import com.laimburggasse.aquariumcontrol.controller.presentation.components.ControllerAddEditUsernamePasswordComposablePortraitPreview

@Suppress("unused")
class ControllerAddEditUsernamePasswordComposableTest {
    @PreviewTest
    @Preview(showBackground = true, uiMode = Configuration.UI_MODE_NIGHT_YES)
    @Preview(showBackground = true, uiMode = Configuration.UI_MODE_NIGHT_NO)
    @Composable
    fun ControllerAddEditUsernamePasswordComposablePortraitTest() {
        ControllerAddEditUsernamePasswordComposablePortraitPreview()
    }

    @PreviewTest
    @Preview(showBackground = true, uiMode = Configuration.UI_MODE_NIGHT_YES)
    @Preview(showBackground = true, uiMode = Configuration.UI_MODE_NIGHT_NO)
    @Composable
    fun ControllerAddEditUsernamePasswordComposableLandscapeTest() {
        ControllerAddEditUsernamePasswordComposableLandscapePreview()

    }
}