package com.laimburggasse.aquariumcontrol.heating

import android.content.res.Configuration
import androidx.compose.runtime.Composable
import androidx.compose.ui.tooling.preview.Preview
import com.android.tools.screenshot.PreviewTest
import com.laimburggasse.aquariumcontrol.heating.presentation.HeatingRangeSliderTextFieldsPortraitComposablePreview

@Suppress("unused")
class HeatingRangeSliderTextFieldsPortraitComposableTest {
    @PreviewTest
    @Preview(showBackground = true, uiMode = Configuration.UI_MODE_NIGHT_YES)
    @Preview(showBackground = true, uiMode = Configuration.UI_MODE_NIGHT_NO)
    @Composable
    fun HeatingRangeSliderTextFieldsPortraitComposableSnapshotTest() {
        HeatingRangeSliderTextFieldsPortraitComposablePreview()
    }
}