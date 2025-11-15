package com.laimburggasse.aquariumcontrol.info.presentation

import android.content.res.Configuration
import androidx.compose.foundation.isSystemInDarkTheme
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import com.laimburggasse.aquariumcontrol.MainScaffold
import com.laimburggasse.aquariumcontrol.R
import com.laimburggasse.aquariumcontrol.ViewNavigationRoutes
import com.laimburggasse.aquariumcontrol.lib_composables.presentation.HyperlinkTextComposable

@Composable
fun InfoScreen() {
    // This screen displays information about the app and the developer.
    Column(
        modifier = Modifier
            .fillMaxSize()
            .padding(all = 16.dp)
            .verticalScroll(state = rememberScrollState()),
        horizontalAlignment = Alignment.CenterHorizontally,
    ) {
        Spacer(Modifier.size(size = 10.dp))
        Text(
            text = "Info",
            style = MaterialTheme.typography.headlineLarge,
        )
        Spacer(Modifier.size(size = 10.dp))
        Text(
            text = stringResource(id = R.string.aquarium_control_is_an_open_source_control_system_for_marine_fish_tanks),
            textAlign = TextAlign.Center
        )
        HyperlinkTextComposable(
            text = stringResource(id = R.string.developer_documentation),
            url = stringResource(id = R.string.http_217_79_180_177_mediawiki_index_php_main_page),
            textAlign = TextAlign.Center
        )
        HyperlinkTextComposable(
            text = stringResource(id = R.string.developer_information),
            url = stringResource(id = R.string.mailto_uwe_martin_gmail_com),
            textAlign = TextAlign.Center
        )
        Text(
            text = stringResource(id = R.string.this_software_comes_with_no_warranties_usage_at_your_own_risk),
            textAlign = TextAlign.Center
        )
    }
}

@Preview(showBackground = true, uiMode = Configuration.UI_MODE_NIGHT_YES)
@Preview(showBackground = true, uiMode = Configuration.UI_MODE_NIGHT_NO)
@Composable
internal fun InfoScreenPreview() {
    MainScaffold(
        isControllerLoaded = true,
        navigationRouteSource = ViewNavigationRoutes.heating,
        screenContent = { InfoScreen() },
        onNavigate = {},
        darkTheme = isSystemInDarkTheme()
    )
}