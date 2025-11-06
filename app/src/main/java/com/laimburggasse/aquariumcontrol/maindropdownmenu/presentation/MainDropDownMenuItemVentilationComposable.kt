package com.laimburggasse.aquariumcontrol.maindropdownmenu.presentation

import android.content.res.Configuration
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.outlined.AcUnit
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
fun MainDropDownMenuItemVentilationComposable(
    isControllerLoaded: Boolean = false,
    navigationRouteSource: String? = null,
    onNavigate: (MainDropDownMenuNavigationEvent) -> Unit
) {
    DropdownMenuItem(
        text = { Text(text = stringResource(R.string.ventilation)) },
        onClick = { onNavigate(MainDropDownMenuNavigationEvent.ToVentilation) },
        leadingIcon = {
            Icon(
                imageVector = Icons.Outlined.AcUnit,
                contentDescription = stringResource(R.string.ventilation)
            )
        },
        enabled = isControllerLoaded && navigationRouteSource != ViewNavigationRoutes.ventilation
    )
}

@Preview(showBackground = false, uiMode = Configuration.UI_MODE_NIGHT_YES)
@Preview(showBackground = true, uiMode = Configuration.UI_MODE_NIGHT_NO)
@Composable
internal fun MainDropdownMenuItemVentilationEnabledPreview() {
    AquariumControllerTheme {
        Surface(
            color = MaterialTheme.colorScheme.background,
        ) {
            MainDropDownMenuItemVentilationComposable(
                isControllerLoaded = true,
                onNavigate = {}
            )
        }
    }
}

@Preview(showBackground = false, uiMode = Configuration.UI_MODE_NIGHT_YES)
@Preview(showBackground = true, uiMode = Configuration.UI_MODE_NIGHT_NO)
@Composable
internal fun MainDropdownMenuItemVentilationDisabledPreview() {
    AquariumControllerTheme {
        Surface(
            color = MaterialTheme.colorScheme.background,
        ) {
            MainDropDownMenuItemVentilationComposable(
                isControllerLoaded = false,
                onNavigate = {}
            )
        }
    }
}
