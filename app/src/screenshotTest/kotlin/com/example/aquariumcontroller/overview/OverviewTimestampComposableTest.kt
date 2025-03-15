package com.example.aquariumcontroller.overview

import android.content.res.Configuration
import androidx.compose.runtime.Composable
import androidx.compose.ui.tooling.preview.Preview

class OverviewTimestampComposableTest {
    @Preview(showBackground = true, uiMode = Configuration.UI_MODE_NIGHT_YES)
    @Preview(showBackground = true, uiMode = Configuration.UI_MODE_NIGHT_NO)
    @Composable
    fun OverviewTimestampComposableSnapshotTest() {
        OverviewTimestampComposablePreview()
    }
}