package com.laimburggasse.aquariumcontrol.common.presentation.maindropdownmenu.presentation

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
import com.laimburggasse.aquariumcontrol.common.R
import com.laimburggasse.aquariumcontrol.common.presentation.ViewNavigationRoutes
import com.laimburggasse.aquariumcontrol.common.presentation.theme.AquariumControllerTheme

@Composable
fun MainDropDownMenuItemControllerSelectionComposable(
    navigationRouteSource: String? = null,
    onNavigate: (MainDropDownMenuNavigationEvent) -> Unit
) {
    DropdownMenuItem(
        text = { Text(text = stringResource(R.string.select_controller)) },
        onClick = { onNavigate(MainDropDownMenuNavigationEvent.ToControllerSelection) },
        leadingIcon = {
            Icon(
                imageVector = Icons.Outlined.Settings,
                contentDescription = stringResource(R.string.select_controller)
            )
        },
        enabled = navigationRouteSource != ViewNavigationRoutes.controllerSelection &&
                    navigationRouteSource != ViewNavigationRoutes.controllerAdd &&
                    navigationRouteSource != ViewNavigationRoutes.controllerEdit
    )
}

@Preview(showBackground = false, uiMode = Configuration.UI_MODE_NIGHT_YES)
@Preview(showBackground = true, uiMode = Configuration.UI_MODE_NIGHT_NO)
@Composable
internal fun MainDropdownMenuItemControllerSelectionPreview() {
    AquariumControllerTheme {
        Surface(
            color = MaterialTheme.colorScheme.background,
        ) {
            MainDropDownMenuItemControllerSelectionComposable(
                onNavigate = {}
            )
        }
    }
}