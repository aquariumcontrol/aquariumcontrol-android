package com.laimburggasse.aquariumcontrol.lib

import android.content.res.Configuration
import androidx.compose.runtime.Composable
import androidx.compose.ui.tooling.preview.Preview
import com.android.tools.screenshot.PreviewTest
import com.laimburggasse.aquariumcontrol.lib.presentation.ScreenHeadlineComposablePreview

@Suppress("unused")
class ScreenHeadlineComposableTest {
    @PreviewTest
    @Preview(showBackground = true, uiMode = Configuration.UI_MODE_NIGHT_YES)
    @Preview(showBackground = true, uiMode = Configuration.UI_MODE_NIGHT_NO)
    @Composable
    fun ScreenHeadlineComposableSnapshotTest() {
        ScreenHeadlineComposablePreview()
    }
}