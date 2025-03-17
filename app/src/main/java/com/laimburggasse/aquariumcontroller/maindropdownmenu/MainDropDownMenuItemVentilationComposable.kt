package com.laimburggasse.aquariumcontroller.maindropdownmenu

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
import androidx.navigation.NavHostController
import com.laimburggasse.aquariumcontroller.R
import com.laimburggasse.aquariumcontroller.ViewNavigationRoutes
import com.laimburggasse.aquariumcontroller.ui.theme.AquariumControllerTheme

@Composable
fun MainDropDownMenuItemVentilationComposable(
    navHostController: NavHostController? = null,
    isControllerLoaded: Boolean = false,
    navigationRouteSource: String? = null
) {
    DropdownMenuItem(
        text = { Text(stringResource(R.string.ventilation)) },
        onClick = {
            navHostController?.navigate(ViewNavigationRoutes.ventilation)
        },
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
fun MainDropdownMenuItemVentilationEnabledPreview() {
    AquariumControllerTheme {
        Surface(
            color = MaterialTheme.colorScheme.background,
        ) {
            MainDropDownMenuItemVentilationComposable(isControllerLoaded = true)
        }
    }
}

@Preview(showBackground = false, uiMode = Configuration.UI_MODE_NIGHT_YES)
@Preview(showBackground = true, uiMode = Configuration.UI_MODE_NIGHT_NO)
@Composable
fun MainDropdownMenuItemVentilationDisabledPreview() {
    AquariumControllerTheme {
        Surface(
            color = MaterialTheme.colorScheme.background,
        ) {
            MainDropDownMenuItemVentilationComposable(isControllerLoaded = false)
        }
    }
}
