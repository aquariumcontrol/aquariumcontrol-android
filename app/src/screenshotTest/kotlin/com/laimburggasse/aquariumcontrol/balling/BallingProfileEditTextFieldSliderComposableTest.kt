package com.laimburggasse.aquariumcontrol.balling

import android.content.res.Configuration
import androidx.compose.runtime.Composable
import androidx.compose.ui.tooling.preview.Preview
import com.android.tools.screenshot.PreviewTest
import com.laimburggasse.aquariumcontrol.balling.presentation.edit.BallingProfileEditTextFieldSliderComposableLandscapePreview
import com.laimburggasse.aquariumcontrol.balling.presentation.edit.BallingProfileEditTextFieldSliderComposablePortraitPreview

@Suppress("unused")
class BallingProfileEditTextFieldSliderComposableTest {
    @PreviewTest
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
    fun BallingProfileEditTextFieldSliderComposableLandscapeTest() {
        BallingProfileEditTextFieldSliderComposableLandscapePreview()
    }

    @PreviewTest
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
    fun BallingProfileEditTextFieldSliderComposablePortraitTest() {
        BallingProfileEditTextFieldSliderComposablePortraitPreview()
    }
}
