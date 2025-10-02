package com.laimburggasse.aquariumcontrol.heating

import android.content.res.Configuration
import androidx.compose.runtime.Composable
import androidx.compose.ui.tooling.preview.Preview
import com.android.tools.screenshot.PreviewTest

@Suppress("unused")
class HeatingButtonsComposableTest {
    // @PreviewTest
    @Preview(showBackground = true, uiMode = Configuration.UI_MODE_NIGHT_YES)
    @Preview(showBackground = true, uiMode = Configuration.UI_MODE_NIGHT_NO)
    @Composable
    fun HeatingButtonsComposablePortraitTest() {
        HeatingButtonsComposablePortraitPreview()
    }

    // @PreviewTest
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
    fun HeatingButtonsComposableLandscapeTest() {
        HeatingButtonsComposableLandscapePreview()
    }
}