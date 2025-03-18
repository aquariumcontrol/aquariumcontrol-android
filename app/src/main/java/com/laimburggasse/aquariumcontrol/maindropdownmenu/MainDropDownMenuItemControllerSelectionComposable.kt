package com.laimburggasse.aquariumcontrol.maindropdownmenu

import android.content.res.Configuration
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.outlined.Settings
import androidx.compose.material3.DropdownMenuItem
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.tooling.preview.Preview
import androidx.navigation.NavHostController
import com.laimburggasse.aquariumcontrol.R
import com.laimburggasse.aquariumcontrol.ViewNavigationRoutes
import com.laimburggasse.aquariumcontrol.ui.theme.AquariumControllerTheme

@Composable
fun MainDropDownMenuItemControllerSelectionComposable(
    navHostController: NavHostController? = null,
    navigationRouteSource: String? = null
) {
    DropdownMenuItem(
        text = { Text(stringResource(R.string.select_controller)) },
        onClick = {
            navHostController?.navigate(ViewNavigationRoutes.controllerSelection)
        },
        leadingIcon = {
            Icon(
                imageVector = Icons.Outlined.Settings,
                contentDescription = stringResource(R.string.select_controller)
            )
        },
        enabled = navigationRouteSource != ViewNavigationRoutes.controllerSelection
    )
}

@Preview(showBackground = false, uiMode = Configuration.UI_MODE_NIGHT_YES)
@Preview(showBackground = true, uiMode = Configuration.UI_MODE_NIGHT_NO)
@Composable
fun MainDropdownMenuItemControllerSelectionPreview() {
    AquariumControllerTheme {
        Surface(
            color = MaterialTheme.colorScheme.background,
        ) {
            MainDropDownMenuItemControllerSelectionComposable()
        }
    }
}