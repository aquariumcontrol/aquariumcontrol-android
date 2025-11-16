package com.laimburggasse.aquariumcontrol.common.maindropdownmenu

import android.content.res.Configuration
import androidx.compose.runtime.Composable
import androidx.compose.ui.tooling.preview.Preview
import com.android.tools.screenshot.PreviewTest
import com.laimburggasse.aquariumcontrol.common.presentation.maindropdownmenu.presentation.MainDropdownMenuItemHeatingDisabledPreview
import com.laimburggasse.aquariumcontrol.common.presentation.maindropdownmenu.presentation.MainDropdownMenuItemHeatingEnabledPreview

@Suppress("unused")
class MainDropDownMenuItemHeatingComposableTest {
    @PreviewTest
    @Preview(showBackground = false, uiMode = Configuration.UI_MODE_NIGHT_YES)
    @Preview(showBackground = true, uiMode = Configuration.UI_MODE_NIGHT_NO)
    @Composable
    fun MainDropdownMenuItemHeatingEnabledTest() {
        MainDropdownMenuItemHeatingEnabledPreview()
    }

    @PreviewTest
    @Preview(showBackground = false, uiMode = Configuration.UI_MODE_NIGHT_YES)
    @Preview(showBackground = true, uiMode = Configuration.UI_MODE_NIGHT_NO)
    @Composable
    fun MainDropdownMenuItemHeatingDisabledTest() {
        MainDropdownMenuItemHeatingDisabledPreview()
    }
}