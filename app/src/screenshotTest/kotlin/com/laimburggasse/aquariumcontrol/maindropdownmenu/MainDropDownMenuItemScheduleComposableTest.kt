package com.laimburggasse.aquariumcontrol.maindropdownmenu

import android.content.res.Configuration
import androidx.compose.runtime.Composable
import androidx.compose.ui.tooling.preview.Preview
import com.android.tools.screenshot.PreviewTest

@Suppress("unused")
class MainDropDownMenuItemScheduleComposableTest {
    // @PreviewTest
    @Preview(showBackground = false, uiMode = Configuration.UI_MODE_NIGHT_YES)
    @Preview(showBackground = true, uiMode = Configuration.UI_MODE_NIGHT_NO)
    @Composable
    fun MainDropdownMenuItemScheduleEnabledTest() {
        MainDropdownMenuItemScheduleEnabledPreview()
    }

    // @PreviewTest
    @Preview(showBackground = false, uiMode = Configuration.UI_MODE_NIGHT_YES)
    @Preview(showBackground = true, uiMode = Configuration.UI_MODE_NIGHT_NO)
    @Composable
    fun MainDropdownMenuItemScheduleDisabledTest() {
        MainDropdownMenuItemScheduleDisabledPreview()
    }
}