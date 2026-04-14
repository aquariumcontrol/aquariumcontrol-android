/*
 * Copyright 2025 Uwe Martin
 *
 * Permission is hereby granted, free of charge, to any person obtaining a copy
 * of this software and associated documentation files (the "Software"), to deal
 * in the Software without restriction, including without limitation the rights
 * to use, copy, modify, merge, publish, distribute, sublicense, and/or sell
 * copies of the Software, and to permit persons to whom the Software is
 * furnished to do so, subject to the following conditions:
 *
 * The above copyright notice and this permission notice shall be included in all
 * copies or substantial portions of the Software.
 *
 * THE SOFTWARE IS PROVIDED "AS IS", WITHOUT WARRANTY OF ANY KIND, EXPRESS OR
 * IMPLIED, INCLUDING BUT NOT LIMITED TO THE WARRANTIES OF MERCHANTABILITY,
 * FITNESS FOR A PARTICULAR PURPOSE AND NONINFRINGEMENT. IN NO EVENT SHALL THE
 * AUTHORS OR COPYRIGHT HOLDERS BE LIABLE FOR ANY CLAIM, DAMAGES OR OTHER
 * LIABILITY, WHETHER IN AN ACTION OF CONTRACT, TORT OR OTHERWISE, ARISING FROM,
 * OUT OF OR IN CONNECTION WITH THE SOFTWARE OR THE USE OR OTHER DEALINGS IN THE
 * SOFTWARE.
 */

package com.laimburggasse.aquariumcontrol.info.presentation

import android.content.res.Configuration
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.material3.Button
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import com.laimburggasse.aquariumcontrol.common.presentation.HyperlinkTextComposable
import com.laimburggasse.aquariumcontrol.common.presentation.theme.AquariumControllerTheme
import com.laimburggasse.aquariumcontrol.info.BuildConfig
import com.laimburggasse.aquariumcontrol.info.R

@Composable
fun InfoHeaderComposable() {
    Column(
        modifier = Modifier
            .fillMaxWidth()
            .padding(all = 16.dp),
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
        Text(
            text = stringResource(id = R.string.aquarium_control_license_statement),
            style = MaterialTheme.typography.titleMedium,
            textAlign = TextAlign.Center
        )
        HyperlinkTextComposable(
            text = stringResource(id = R.string.aquarium_control_repo),
            url = stringResource(id = R.string.repo_url),
            textAlign = TextAlign.Center
        )
        Text(
            text = stringResource(id = R.string.aquarium_control_version),
            textAlign = TextAlign.Center,
            style = MaterialTheme.typography.bodySmall
        )
        Spacer(Modifier.size(size = 10.dp))
        if (BuildConfig.DEBUG) {
            Button(
                onClick = {
                    throw RuntimeException("Test Crash for Firebase Crashlytics")
                }
            ) {
                Text("DEBUG: Force Crash")
            }
            Spacer(Modifier.size(size = 10.dp))
        }
        Text(
            text = stringResource(id = R.string.this_app_is_built_using_the_following_libraries),
            textAlign = TextAlign.Center
        )
    }
}

@Preview(showBackground = true, uiMode = Configuration.UI_MODE_NIGHT_YES)
@Composable
internal fun InfoHeaderComposableDarkPreview() {
    AquariumControllerTheme(darkTheme = true) {
        Surface(color = MaterialTheme.colorScheme.background) {
            InfoHeaderComposable()
        }
    }
}

@Preview(showBackground = true, uiMode = Configuration.UI_MODE_NIGHT_NO)
@Composable
internal fun InfoHeaderComposableLightPreview() {
    AquariumControllerTheme(darkTheme = false) {
        Surface(color = MaterialTheme.colorScheme.background) {
            InfoHeaderComposable()
        }
    }
}