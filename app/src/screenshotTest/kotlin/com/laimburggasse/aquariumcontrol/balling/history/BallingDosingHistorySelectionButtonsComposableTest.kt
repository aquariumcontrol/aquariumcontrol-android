package com.laimburggasse.aquariumcontrol.balling.history

import android.content.res.Configuration
import androidx.compose.runtime.Composable
import androidx.compose.ui.tooling.preview.Preview
import com.android.tools.screenshot.PreviewTest
import com.laimburggasse.aquariumcontrol.balling.presentation.entry.BallingDosingHistorySelectionButtonsComposableLandscapePreview
import com.laimburggasse.aquariumcontrol.balling.presentation.entry.BallingDosingHistorySelectionButtonsComposablePortraitPreview

@Suppress("unused")
class BallingDosingHistorySelectionButtonsComposableTest {
    @PreviewTest
    @Preview(showBackground = true, uiMode = Configuration.UI_MODE_NIGHT_YES)
    @Preview(showBackground = true, uiMode = Configuration.UI_MODE_NIGHT_NO)
    @Composable
    fun BallingDosingHistorySelectionButtonsComposablePortraitTest() {
        BallingDosingHistorySelectionButtonsComposablePortraitPreview()
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
    fun BallingDosingHistorySelectionButtonsComposableLandscapeTest() {
        BallingDosingHistorySelectionButtonsComposableLandscapePreview()
    }
}