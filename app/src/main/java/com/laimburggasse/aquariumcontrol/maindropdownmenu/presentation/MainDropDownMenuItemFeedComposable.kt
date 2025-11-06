package com.laimburggasse.aquariumcontrol.maindropdownmenu.presentation

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
import com.laimburggasse.aquariumcontrol.R
import com.laimburggasse.aquariumcontrol.ViewNavigationRoutes
import com.laimburggasse.aquariumcontrol.ui.theme.AquariumControllerTheme

@Composable
fun MainDropDownMenuItemFeedComposable(
    isControllerLoaded: Boolean = false,
    navigationRouteSource: String? = null,
    onNavigate: (MainDropDownMenuNavigationEvent) -> Unit
) {
    DropdownMenuItem(
        text = { Text(stringResource(R.string.feed)) },
        onClick = { onNavigate(MainDropDownMenuNavigationEvent.ToFeed) },
        leadingIcon = {
            Icon(
                imageVector = Icons.AutoMirrored.Outlined.RotateRight,
                contentDescription = stringResource(R.string.feed)
            )
        },
        enabled = isControllerLoaded &&
                    navigationRouteSource != ViewNavigationRoutes.feedHistory &&
                    navigationRouteSource != ViewNavigationRoutes.feedHistory24h &&
                    navigationRouteSource != ViewNavigationRoutes.feedHistory7d &&
                    navigationRouteSource != ViewNavigationRoutes.feedEdit &&
                    navigationRouteSource != ViewNavigationRoutes.feedAddProfile &&
                    navigationRouteSource != ViewNavigationRoutes.feedSchedule
    )
}

@Preview(showBackground = false, uiMode = Configuration.UI_MODE_NIGHT_YES)
@Preview(showBackground = true, uiMode = Configuration.UI_MODE_NIGHT_NO)
@Composable
internal fun MainDropdownMenuItemFeedEnabledPreview() {
    AquariumControllerTheme {
        Surface(
            color = MaterialTheme.colorScheme.background,
        ) {
            MainDropDownMenuItemFeedComposable(
                isControllerLoaded = true,
                onNavigate = {}
            )
        }
    }
}

@Preview(showBackground = false, uiMode = Configuration.UI_MODE_NIGHT_YES)
@Preview(showBackground = true, uiMode = Configuration.UI_MODE_NIGHT_NO)
@Composable
internal fun MainDropdownMenuItemFeedDisabledPreview() {
    AquariumControllerTheme {
        Surface(
            color = MaterialTheme.colorScheme.background,
        ) {
            MainDropDownMenuItemFeedComposable(
                isControllerLoaded = false,
                onNavigate = {}
            )
        }
    }
}
