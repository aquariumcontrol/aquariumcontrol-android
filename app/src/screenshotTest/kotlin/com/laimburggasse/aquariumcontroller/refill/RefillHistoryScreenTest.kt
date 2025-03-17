package com.laimburggasse.aquariumcontroller.refill

import android.content.res.Configuration
import androidx.compose.runtime.Composable
import androidx.compose.ui.tooling.preview.Preview
import com.laimburggasse.aquariumcontroller.refill.history.RefillHistoryScreenPreview

@Suppress("unused")
class RefillHistoryScreenTest {
    @Preview(showBackground = true, uiMode = Configuration.UI_MODE_NIGHT_YES)
    @Preview(showBackground = true, uiMode = Configuration.UI_MODE_NIGHT_NO)
    @Preview(
        showBackground = true,
        uiMode = Configuration.UI_MODE_NIGHT_YES,
        device = "spec:width=800dp,height=400dp,orientation=landscape"
    )
    @Preview(
        showBackground = true,
        uiMode = Configuration.UI_MODE_NIGHT_NO,
        device = "spec:width=800dp,height=400dp,orientation=landscape"
    )
    @Composable
    fun RefillHistoryScreenSnapshotTest() {
        RefillHistoryScreenPreview()
    }
}