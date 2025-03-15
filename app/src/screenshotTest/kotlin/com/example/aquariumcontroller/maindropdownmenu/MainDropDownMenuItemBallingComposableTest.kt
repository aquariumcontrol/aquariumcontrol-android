package com.example.aquariumcontroller.maindropdownmenu

import android.content.res.Configuration
import androidx.compose.runtime.Composable
import androidx.compose.ui.tooling.preview.Preview

class MainDropDownMenuItemBallingComposableTest {
    @Preview(showBackground = false, uiMode = Configuration.UI_MODE_NIGHT_YES)
    @Preview(showBackground = true, uiMode = Configuration.UI_MODE_NIGHT_NO)
    @Composable
    fun MainDropdownMenuItemBallingEnabledTest() {
        MainDropdownMenuItemBallingEnabledPreview()
    }

    @Preview(showBackground = false, uiMode = Configuration.UI_MODE_NIGHT_YES)
    @Preview(showBackground = true, uiMode = Configuration.UI_MODE_NIGHT_NO)
    @Composable
    fun MainDropdownMenuItemBallingDataDisabledTest() {
        MainDropdownMenuItemBallingDataDisabledPreview()
    }
}