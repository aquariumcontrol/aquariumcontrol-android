package com.laimburggasse.aquariumcontroller.maindropdownmenu

import android.content.res.Configuration
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.outlined.Info
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
fun MainDropDownMenuItemInfoComposable(
    navHostController: NavHostController? = null,
    navigationRouteSource: String? = null
) {
    DropdownMenuItem(
        text = { Text(stringResource(R.string.info)) },
        onClick = {
            navHostController?.navigate(ViewNavigationRoutes.info)
        },
        leadingIcon = {
            Icon(
                imageVector = Icons.Outlined.Info,
                contentDescription = stringResource(R.string.info)
            )
        },
        enabled = navigationRouteSource != ViewNavigationRoutes.info
    )
}

@Preview(showBackground = false, uiMode = Configuration.UI_MODE_NIGHT_YES)
@Preview(showBackground = true, uiMode = Configuration.UI_MODE_NIGHT_NO)
@Composable
fun MainDropdownMenuItemInfoPreview() {
    AquariumControllerTheme {
        Surface(
            color = MaterialTheme.colorScheme.background,
        ) {
            MainDropDownMenuItemInfoComposable()
        }
    }
}