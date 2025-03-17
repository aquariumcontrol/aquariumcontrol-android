package com.laimburggasse.aquariumcontroller.maindropdownmenu

import android.content.res.Configuration
import androidx.compose.runtime.Composable
import androidx.compose.ui.tooling.preview.Preview

@Suppress("unused")
class MainDropDownMenuItemVentilationComposableTest {
    @Preview(showBackground = false, uiMode = Configuration.UI_MODE_NIGHT_YES)
    @Preview(showBackground = true, uiMode = Configuration.UI_MODE_NIGHT_NO)
    @Composable
    fun MainDropdownMenuItemVentilationEnabledTest() {
        MainDropdownMenuItemVentilationEnabledPreview()
    }

    @Preview(showBackground = false, uiMode = Configuration.UI_MODE_NIGHT_YES)
    @Preview(showBackground = true, uiMode = Configuration.UI_MODE_NIGHT_NO)
    @Composable
    fun MainDropdownMenuItemVentilationDisabledTest() {
        MainDropdownMenuItemVentilationDisabledPreview()
    }
}