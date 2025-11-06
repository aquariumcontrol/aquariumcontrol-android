package com.laimburggasse.aquariumcontrol.timedata

import android.content.res.Configuration
import androidx.compose.runtime.Composable
import androidx.compose.ui.tooling.preview.Preview
import com.laimburggasse.aquariumcontrol.timedata.presentation.TimeDataScreenPreviewEmpty
import com.laimburggasse.aquariumcontrol.timedata.presentation.TimeDataScreenPreviewError
import com.laimburggasse.aquariumcontrol.timedata.presentation.TimeDataScreenPreviewSuccess

@Suppress("unused")
class TimeDataScreenTest {
    //@PreviewTest
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
    fun TimeDataScreenSuccessSnapshotTest() {
        TimeDataScreenPreviewSuccess()
    }

    //@PreviewTest
    @Preview(showBackground = true, heightDp = 250, uiMode = Configuration.UI_MODE_NIGHT_YES)
    @Preview(showBackground = true, heightDp = 250, uiMode = Configuration.UI_MODE_NIGHT_NO)
    @Preview(
        showBackground = true,
        uiMode = Configuration.UI_MODE_NIGHT_YES,
        device = "spec:width=250dp,height=350dp,orientation=landscape"
    )
    @Preview(
        showBackground = true,
        uiMode = Configuration.UI_MODE_NIGHT_NO,
        device = "spec:width=250dp,height=350dp,orientation=landscape"
    )
    @Composable
    fun TimeDataScreenErrorSnapshotTest() {
       TimeDataScreenPreviewError()
    }

    //@PreviewTest
    @Preview(showBackground = true, heightDp = 250, uiMode = Configuration.UI_MODE_NIGHT_YES)
    @Preview(showBackground = true, heightDp = 250, uiMode = Configuration.UI_MODE_NIGHT_NO)
    @Preview(
        showBackground = true,
        uiMode = Configuration.UI_MODE_NIGHT_YES,
        device = "spec:width=250dp,height=350dp,orientation=landscape"
    )
    @Preview(
        showBackground = true,
        uiMode = Configuration.UI_MODE_NIGHT_NO,
        device = "spec:width=250dp,height=350dp,orientation=landscape"
    )
    @Composable
    fun TimeDataScreenEmptySnapshotTest() {
        TimeDataScreenPreviewEmpty()
    }
}