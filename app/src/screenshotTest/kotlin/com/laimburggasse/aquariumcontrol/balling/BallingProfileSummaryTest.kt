package com.laimburggasse.aquariumcontrol.balling

import android.content.res.Configuration
import androidx.compose.runtime.Composable
import androidx.compose.ui.tooling.preview.Preview
import com.android.tools.screenshot.PreviewTest

@Suppress("unused")
class BallingProfileSummaryTest {
    // @PreviewTest
    @Preview(showBackground = true, uiMode = Configuration.UI_MODE_NIGHT_YES)
    @Preview(showBackground = true, uiMode = Configuration.UI_MODE_NIGHT_NO)
    @Composable
    fun BallingProfileSummarySnapshotTest() {
        BallingProfileSummaryPreview()
    }
}