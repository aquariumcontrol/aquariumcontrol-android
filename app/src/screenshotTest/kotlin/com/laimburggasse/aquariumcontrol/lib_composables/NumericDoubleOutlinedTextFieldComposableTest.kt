package com.laimburggasse.aquariumcontrol.lib_composables

import android.content.res.Configuration
import androidx.compose.runtime.Composable
import androidx.compose.ui.tooling.preview.Preview
import com.android.tools.screenshot.PreviewTest

@Suppress("unused")
class NumericDoubleOutlinedTextFieldComposableTest {
    // @PreviewTest
    @Preview(showBackground = true, uiMode = Configuration.UI_MODE_NIGHT_YES)
    @Preview(showBackground = true, uiMode = Configuration.UI_MODE_NIGHT_NO)
    @Composable
    fun NumericDoubleOutlinedTextFieldComposableSnapshotTest() {
        NumericDoubleOutlinedTextFieldComposablePreview()
    }
}