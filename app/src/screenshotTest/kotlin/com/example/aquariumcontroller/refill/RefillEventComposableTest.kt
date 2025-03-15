package com.example.aquariumcontroller.refill

import android.content.res.Configuration
import androidx.compose.runtime.Composable
import androidx.compose.ui.tooling.preview.Preview
import com.example.aquariumcontroller.refill.history.RefillEventComposablePreview

@Suppress("unused")
class RefillEventComposableTest {
    @Preview(showBackground = true, uiMode = Configuration.UI_MODE_NIGHT_YES)
    @Preview(showBackground = true, uiMode = Configuration.UI_MODE_NIGHT_NO)
    @Composable
    fun RefillEventComposableSnapshotTest() {
        RefillEventComposablePreview()
    }
}