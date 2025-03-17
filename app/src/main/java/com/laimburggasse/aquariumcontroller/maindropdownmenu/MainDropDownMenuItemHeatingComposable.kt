package com.laimburggasse.aquariumcontroller.maindropdownmenu

import android.content.res.Configuration
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.outlined.DeviceThermostat
import androidx.compose.material3.DropdownMenuItem
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.tooling.preview.Preview
import androidx.navigation.NavHostController
import com.laimburggasse.aquariumcontroller.R
import com.laimburggasse.aquariumcontroller.ViewNavigationRoutes
import com.laimburggasse.aquariumcontroller.ui.theme.AquariumControllerTheme

@Composable
fun MainDropDownMenuItemHeatingComposable(
    navHostController: NavHostController? = null,
    isControllerLoaded: Boolean = false,
    navigationRouteSource: String? = null
) {
    DropdownMenuItem(
        text = { Text(stringResource(R.string.heating)) },
        onClick = {
            navHostController?.navigate(ViewNavigationRoutes.heating)
        },
        leadingIcon = {
            Icon(
                imageVector = Icons.Outlined.DeviceThermostat,
                contentDescription = stringResource(R.string.heating)
            )
        },
        enabled = isControllerLoaded && navigationRouteSource != ViewNavigationRoutes.heating
    )
}

@Preview(showBackground = false, uiMode = Configuration.UI_MODE_NIGHT_YES)
@Preview(showBackground = true, uiMode = Configuration.UI_MODE_NIGHT_NO)
@Composable
fun MainDropdownMenuItemHeatingEnabledPreview() {
    AquariumControllerTheme {
        Surface(
            color = MaterialTheme.colorScheme.background,
        ) {
            MainDropDownMenuItemHeatingComposable(isControllerLoaded = true)
        }
    }
}

@Preview(showBackground = false, uiMode = Configuration.UI_MODE_NIGHT_YES)
@Preview(showBackground = true, uiMode = Configuration.UI_MODE_NIGHT_NO)
@Composable
fun MainDropdownMenuItemHeatingDisabledPreview() {
    AquariumControllerTheme {
        Surface(
            color = MaterialTheme.colorScheme.background,
        ) {
            MainDropDownMenuItemHeatingComposable(isControllerLoaded = false)
        }
    }
}
