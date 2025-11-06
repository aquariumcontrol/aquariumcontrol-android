package com.laimburggasse.aquariumcontrol.maindropdownmenu

import android.content.res.Configuration
import androidx.compose.runtime.Composable
import androidx.compose.ui.tooling.preview.Preview
import com.android.tools.screenshot.PreviewTest
import com.laimburggasse.aquariumcontrol.maindropdownmenu.presentation.MainDropdownMenuItemOverviewDisabledPreview
import com.laimburggasse.aquariumcontrol.maindropdownmenu.presentation.MainDropdownMenuItemOverviewEnabledPreview

@Suppress("unused")
class MainDropDownMenuItemOverviewComposableTest {
    @PreviewTest
    @Preview(showBackground = false, uiMode = Configuration.UI_MODE_NIGHT_YES)
    @Preview(showBackground = true, uiMode = Configuration.UI_MODE_NIGHT_NO)
    @Composable
    fun MainDropdownMenuItemOverviewEnabledTest() {
        MainDropdownMenuItemOverviewEnabledPreview()
    }

    @PreviewTest
    @Preview(showBackground = false, uiMode = Configuration.UI_MODE_NIGHT_YES)
    @Preview(showBackground = true, uiMode = Configuration.UI_MODE_NIGHT_NO)
    @Composable
    fun MainDropdownMenuItemOverviewDisabledTest() {
        MainDropdownMenuItemOverviewDisabledPreview()
    }
}