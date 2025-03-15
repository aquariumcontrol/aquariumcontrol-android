package com.example.aquariumcontroller.balling

import android.content.res.Configuration
import androidx.compose.runtime.Composable
import androidx.compose.ui.tooling.preview.Preview
import com.example.aquariumcontroller.balling.edit.BallingProfileEditTextFieldSliderComposableLandscapePreview
import com.example.aquariumcontroller.balling.edit.BallingProfileEditTextFieldSliderComposablePortraitPreview

@Suppress("unused")
class BallingProfileEditTextFieldSliderComposableTest {
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
