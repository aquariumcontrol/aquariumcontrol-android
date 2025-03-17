package com.laimburggasse.aquariumcontroller.timedata

import android.content.res.Configuration
import androidx.compose.runtime.Composable
import androidx.compose.ui.tooling.preview.Preview

@Suppress("unused")
class TimeDataScreenTest {
    @Preview(showBackground = true, heightDp = 1250, uiMode = Configuration.UI_MODE_NIGHT_YES)
    @Preview(showBackground = true, heightDp = 1250, uiMode = Configuration.UI_MODE_NIGHT_NO)
    @Preview(
        showBackground = true,
        uiMode = Configuration.UI_MODE_NIGHT_YES,
        device = "spec:width=1600dp,height=1200dp,orientation=landscape"
    )
    @Preview(
        showBackground = true,
        uiMode = Configuration.UI_MODE_NIGHT_NO,
        device = "spec:width=1600dp,height=1200dp,orientation=landscape"
    )
    @Composable
    fun TimeDataScreenSnapshotTest() {
        TimeDataScreenPreview()
    }
}