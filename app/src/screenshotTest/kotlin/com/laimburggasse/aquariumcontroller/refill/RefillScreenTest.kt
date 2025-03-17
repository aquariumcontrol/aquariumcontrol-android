package com.laimburggasse.aquariumcontroller.refill

import android.content.res.Configuration
import androidx.compose.runtime.Composable
import androidx.compose.ui.tooling.preview.Preview

@Suppress("unused")
class RefillScreenTest {
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
    @Preview(showBackground = true, uiMode = Configuration.UI_MODE_NIGHT_YES)
    @Composable
    fun RefillScreenSnapshotTest() {
        RefillScreenPreview()
    }
}