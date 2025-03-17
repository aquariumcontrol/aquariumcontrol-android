package com.laimburggasse.aquariumcontroller.balling

import android.content.res.Configuration
import androidx.compose.runtime.Composable
import androidx.compose.ui.tooling.preview.Preview
import com.laimburggasse.aquariumcontroller.balling.history.BallingHistoryEventComposablePreview

@Suppress("unused")
class BallingHistoryEventComposableTest {
    @Preview(showBackground = true, uiMode = Configuration.UI_MODE_NIGHT_YES)
    @Preview(showBackground = true, uiMode = Configuration.UI_MODE_NIGHT_NO)
    @Composable
    fun BallingHistoryEventComposableScreenshotTest() {
        BallingHistoryEventComposablePreview()
    }
}