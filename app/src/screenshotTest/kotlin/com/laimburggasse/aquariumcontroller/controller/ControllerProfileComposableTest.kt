package com.laimburggasse.aquariumcontroller.controller

import android.content.res.Configuration
import androidx.compose.runtime.Composable
import androidx.compose.ui.tooling.preview.Preview

@Suppress("unused")
class ControllerProfileComposableTest {
    @Preview(showBackground = true, uiMode = Configuration.UI_MODE_NIGHT_YES)
    @Preview(showBackground = true, uiMode = Configuration.UI_MODE_NIGHT_NO)
    @Composable
    fun ControllerProfileComposableSnapshotTest() {
        ControllerProfileComposablePreview()
    }
}