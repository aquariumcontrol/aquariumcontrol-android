package com.laimburggasse.aquariumcontrol.maindropdownmenu

import android.content.res.Configuration
import androidx.compose.runtime.Composable
import androidx.compose.ui.tooling.preview.Preview
import com.android.tools.screenshot.PreviewTest
import com.laimburggasse.aquariumcontrol.maindropdownmenu.presentation.MainDropdownMenuItemRefillDisabledPreview
import com.laimburggasse.aquariumcontrol.maindropdownmenu.presentation.MainDropdownMenuItemRefillEnabledPreview

@Suppress("unused")
class MainDropDownMenuItemRefillComposableTest {
    @PreviewTest
    @Preview(showBackground = false, uiMode = Configuration.UI_MODE_NIGHT_YES)
    @Preview(showBackground = true, uiMode = Configuration.UI_MODE_NIGHT_NO)
    @Composable
    fun MainDropdownMenuItemRefillEnabledTest() {
        MainDropdownMenuItemRefillEnabledPreview()
    }

    @PreviewTest
    @Preview(showBackground = false, uiMode = Configuration.UI_MODE_NIGHT_YES)
    @Preview(showBackground = true, uiMode = Configuration.UI_MODE_NIGHT_NO)
    @Composable
    fun MainDropdownMenuItemRefillDisabledTest() {
        MainDropdownMenuItemRefillDisabledPreview()
    }
}