package com.laimburggasse.aquariumcontrol.ventilation

import android.content.res.Configuration
import androidx.compose.runtime.Composable
import androidx.compose.ui.tooling.preview.Preview
import com.android.tools.screenshot.PreviewTest
import com.laimburggasse.aquariumcontrol.ventilation.presentation.VentilationRangeSliderTextFieldsLandscapeComposablePreview

@Suppress("unused")
class VentilationRangeSliderTextFieldsLandscapeComposableTest {
    @PreviewTest
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
    fun VentilationRangeSliderTextFieldsLandscapeComposableSnapshotTest() {
        VentilationRangeSliderTextFieldsLandscapeComposablePreview()
    }
}