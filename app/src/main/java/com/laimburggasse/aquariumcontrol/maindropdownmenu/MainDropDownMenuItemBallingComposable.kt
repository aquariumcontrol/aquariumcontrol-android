package com.laimburggasse.aquariumcontrol.maindropdownmenu

import android.content.res.Configuration
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.outlined.Science
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
fun MainDropDownMenuItemBallingComposable(
    navHostController: NavHostController? = null,
    isControllerLoaded: Boolean = false,
    navigationRouteSource: String? = null
) {
    DropdownMenuItem(
        text = { Text(stringResource(R.string.balling_mineral_dosing)) },
        onClick = {
            navHostController?.navigate(ViewNavigationRoutes.balling)
        },
        leadingIcon = {
            Icon(
                imageVector = Icons.Outlined.Science,
                contentDescription = stringResource(R.string.balling_mineral_dosing)
            )
        },
        enabled = isControllerLoaded &&
                    navigationRouteSource != ViewNavigationRoutes.balling &&
                    navigationRouteSource != ViewNavigationRoutes.ballingHistory24h &&
                    navigationRouteSource != ViewNavigationRoutes.ballingHistory7d
    )
}

@Preview(showBackground = false, uiMode = Configuration.UI_MODE_NIGHT_YES)
@Preview(showBackground = true, uiMode = Configuration.UI_MODE_NIGHT_NO)
@Composable
internal fun MainDropdownMenuItemBallingEnabledPreview() {
    AquariumControllerTheme {
        Surface(
            color = MaterialTheme.colorScheme.background,
        ) {
            MainDropDownMenuItemBallingComposable(isControllerLoaded = true)
        }
    }
}

@Preview(showBackground = false, uiMode = Configuration.UI_MODE_NIGHT_YES)
@Preview(showBackground = true, uiMode = Configuration.UI_MODE_NIGHT_NO)
@Composable
internal fun MainDropdownMenuItemBallingDataDisabledPreview() {
    AquariumControllerTheme {
        Surface(
            color = MaterialTheme.colorScheme.background,
        ) {
            MainDropDownMenuItemBallingComposable(isControllerLoaded = false)
        }
    }
}
