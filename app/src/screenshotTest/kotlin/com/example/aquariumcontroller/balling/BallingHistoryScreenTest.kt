package com.example.aquariumcontroller.balling

import android.content.res.Configuration
import androidx.compose.runtime.Composable
import androidx.compose.ui.tooling.preview.Preview
import com.example.aquariumcontroller.balling.history.BallingHistoryScreenEmptyPreview
import com.example.aquariumcontroller.balling.history.BallingHistoryScreenPreview

@Suppress("unused")
class BallingHistoryScreenTest {
    @Preview(showBackground = true, uiMode = Configuration.UI_MODE_NIGHT_YES)
    @Preview(showBackground = true, uiMode = Configuration.UI_MODE_NIGHT_NO)
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
    fun BallingHistoryScreenSnapshotTest() {
        BallingHistoryScreenPreview()
    }

    @Preview(showBackground = true, uiMode = Configuration.UI_MODE_NIGHT_YES)
    @Composable
    fun BallingHistoryScreenEmptySnapshotTest() {
        BallingHistoryScreenEmptyPreview()
    }
}