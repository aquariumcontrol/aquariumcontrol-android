package com.laimburggasse.aquariumcontrol.controller

import android.content.res.Configuration
import androidx.compose.runtime.Composable
import androidx.compose.ui.tooling.preview.Preview
import com.android.tools.screenshot.PreviewTest
import com.laimburggasse.aquariumcontrol.controller.presentation.selection.ControllerProfileComposablePreview

@Suppress("unused")
class ControllerProfileComposableTest {
    @PreviewTest
    @Preview(showBackground = true, uiMode = Configuration.UI_MODE_NIGHT_YES)
    @Preview(showBackground = true, uiMode = Configuration.UI_MODE_NIGHT_NO)
    @Composable
    fun ControllerProfileComposableSnapshotTest() {
        ControllerProfileComposablePreview()
    }
}