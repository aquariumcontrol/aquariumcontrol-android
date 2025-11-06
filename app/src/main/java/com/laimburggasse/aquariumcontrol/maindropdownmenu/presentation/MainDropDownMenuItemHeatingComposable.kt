package com.laimburggasse.aquariumcontrol.maindropdownmenu.presentation

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
import com.laimburggasse.aquariumcontrol.R
import com.laimburggasse.aquariumcontrol.ViewNavigationRoutes
import com.laimburggasse.aquariumcontrol.ui.theme.AquariumControllerTheme

@Composable
fun MainDropDownMenuItemHeatingComposable(
    isControllerLoaded: Boolean = false,
    navigationRouteSource: String? = null,
    onNavigate: (MainDropDownMenuNavigationEvent) -> Unit
) {
    DropdownMenuItem(
        text = { Text(text = stringResource(R.string.heating)) },
        onClick = { onNavigate(MainDropDownMenuNavigationEvent.ToHeating) },
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
internal fun MainDropdownMenuItemHeatingEnabledPreview() {
    AquariumControllerTheme {
        Surface(
            color = MaterialTheme.colorScheme.background,
        ) {
            MainDropDownMenuItemHeatingComposable(
                isControllerLoaded = true,
                onNavigate = {}
            )
        }
    }
}

@Preview(showBackground = false, uiMode = Configuration.UI_MODE_NIGHT_YES)
@Preview(showBackground = true, uiMode = Configuration.UI_MODE_NIGHT_NO)
@Composable
internal fun MainDropdownMenuItemHeatingDisabledPreview() {
    AquariumControllerTheme {
        Surface(
            color = MaterialTheme.colorScheme.background,
        ) {
            MainDropDownMenuItemHeatingComposable(
                isControllerLoaded = false,
                onNavigate = {}
            )
        }
    }
}
