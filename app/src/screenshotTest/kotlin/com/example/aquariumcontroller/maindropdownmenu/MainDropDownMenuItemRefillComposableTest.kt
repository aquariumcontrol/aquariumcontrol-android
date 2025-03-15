package com.example.aquariumcontroller.maindropdownmenu

import android.content.res.Configuration
import androidx.compose.runtime.Composable
import androidx.compose.ui.tooling.preview.Preview

class MainDropDownMenuItemRefillComposableTest {
    @Preview(showBackground = false, uiMode = Configuration.UI_MODE_NIGHT_YES)
    @Preview(showBackground = true, uiMode = Configuration.UI_MODE_NIGHT_NO)
    @Composable
    fun MainDropdownMenuItemRefillEnabledTest() {
        MainDropdownMenuItemRefillEnabledPreview()
    }

    @Preview(showBackground = false, uiMode = Configuration.UI_MODE_NIGHT_YES)
    @Preview(showBackground = true, uiMode = Configuration.UI_MODE_NIGHT_NO)
    @Composable
    fun MainDropdownMenuItemRefillDisabledTest() {
        MainDropdownMenuItemRefillDisabledPreview()
    }
}