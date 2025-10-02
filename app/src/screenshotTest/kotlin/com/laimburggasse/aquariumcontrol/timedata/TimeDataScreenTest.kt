package com.laimburggasse.aquariumcontrol.timedata

import android.content.res.Configuration
import androidx.compose.runtime.Composable
import androidx.compose.ui.tooling.preview.Preview
import com.android.tools.screenshot.PreviewTest
import com.laimburggasse.aquariumcontrol.timedata.TimeDataUiState.Error
import com.laimburggasse.aquariumcontrol.timedata.TimeDataUiState.Loading
import com.laimburggasse.aquariumcontrol.timedata.TimeDataUiState.Success

@Suppress("unused")
class TimeDataScreenTest {
    @PreviewTest
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
    fun TimeDataScreenPreview_Success() {
        TimeDataScreen(mockTimeDataUiState = Success(TimeDataSets(generateMockTimeData())))
    }

    @PreviewTest
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
    fun TimeDataScreenPreview_Error() {
        TimeDataScreen(mockTimeDataUiState = Error("Error message"))
    }

    @PreviewTest
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
    fun TimeDataScreenPreview_Loading() {
        TimeDataScreen(mockTimeDataUiState = Loading)
    }
}