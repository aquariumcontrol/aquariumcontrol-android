package com.example.aquariumcontroller

import android.content.res.Configuration
import androidx.compose.runtime.Composable
import androidx.compose.ui.tooling.preview.Preview
import com.example.aquariumcontroller.balling.history.BallingHistoryEventComposablePreview

class BallingHistoryEventComposableTest {
    @Preview(showBackground = true, uiMode = Configuration.UI_MODE_NIGHT_YES)
    @Preview(showBackground = true, uiMode = Configuration.UI_MODE_NIGHT_NO)
    @Composable
    fun BallingHistoryEventComposableScreenshotTest() {
        BallingHistoryEventComposablePreview()
    }
}