package com.laimburggasse.aquariumcontroller.maindropdownmenu

import android.content.res.Configuration
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.outlined.RotateRight
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
fun MainDropDownMenuItemFeedComposable(
    navHostController: NavHostController? = null,
    isControllerLoaded: Boolean = false,
    navigationRouteSource: String? = null
) {
    DropdownMenuItem(
        text = { Text(stringResource(R.string.feed)) },
        onClick = {
            navHostController?.navigate(ViewNavigationRoutes.feed)
        },
        leadingIcon = {
            Icon(
                imageVector = Icons.AutoMirrored.Outlined.RotateRight,
                contentDescription = stringResource(R.string.feed)
            )
        },
        enabled = isControllerLoaded && navigationRouteSource != ViewNavigationRoutes.feed
    )
}

@Preview(showBackground = false, uiMode = Configuration.UI_MODE_NIGHT_YES)
@Preview(showBackground = true, uiMode = Configuration.UI_MODE_NIGHT_NO)
@Composable
fun MainDropdownMenuItemFeedEnabledPreview() {
    AquariumControllerTheme {
        Surface(
            color = MaterialTheme.colorScheme.background,
        ) {
            MainDropDownMenuItemFeedComposable(isControllerLoaded = true)
        }
    }
}

@Preview(showBackground = false, uiMode = Configuration.UI_MODE_NIGHT_YES)
@Preview(showBackground = true, uiMode = Configuration.UI_MODE_NIGHT_NO)
@Composable
fun MainDropdownMenuItemFeedDisabledPreview() {
    AquariumControllerTheme {
        Surface(
            color = MaterialTheme.colorScheme.background,
        ) {
            MainDropDownMenuItemFeedComposable(isControllerLoaded = false)
        }
    }
}
