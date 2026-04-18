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

package com.laimburggasse.aquariumcontrol.feed.presentation.edit

import android.content.Context
import android.content.res.Configuration
import android.widget.Toast
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.width
import androidx.compose.material3.Button
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.laimburggasse.aquariumcontrol.feed.R
import com.laimburggasse.aquariumcontrol.common.presentation.theme.AquariumControllerTheme

@Composable
fun FeedProfileEditButtonsComposable(
    modifier: Modifier? = Modifier,
    onFeedProfileEditUiEvent: (FeedProfileEditUiEvent) -> Unit
) {
    // This composable displays the buttons for updating the feed profile.
    // The arrangement of the buttons follows the device orientation.

    val context: Context = LocalContext.current // context for Toasts
    val messageSentFeedProfileUpdateRequest = stringResource(
        R.string.sent_request_to_controller_for_updating_feed_profile
    )

    Button(
        modifier = modifier?.then(other = Modifier
            .fillMaxWidth()
            .testTag("updateFeedProfileButton")
            .padding(horizontal = 8.dp)) ?: Modifier,
        onClick = {
            onFeedProfileEditUiEvent(FeedProfileEditUiEvent.Update)
            Toast.makeText(
                context,
                messageSentFeedProfileUpdateRequest,
                Toast.LENGTH_SHORT
            ).show()
        },
    ) {
        Text(
            text = stringResource(id = R.string.update_feed_profile),
            fontSize = 16.sp
        )
    }
    Spacer(modifier = Modifier.width(width = 10.dp))
    Button(
        modifier = modifier?.then(other = Modifier
            .fillMaxWidth()
            .testTag("resetFeedProfileButton")
            .padding(horizontal = 8.dp)) ?: Modifier,
        onClick = { onFeedProfileEditUiEvent(FeedProfileEditUiEvent.Reset) },
    ) {
        Text(
            text = stringResource(id = R.string.reset_changes_to_feed_profile),
            fontSize = 16.sp
        )
    }
}

@Preview(showBackground = true, uiMode = Configuration.UI_MODE_NIGHT_YES)
@Composable
internal fun FeedProfileEditButtonsComposablePortraitDarkPreview() {
    AquariumControllerTheme(darkTheme = true) {
        Surface {
            Column {
                FeedProfileEditButtonsComposable(modifier = Modifier.fillMaxWidth(), onFeedProfileEditUiEvent = {})
            }
        }
    }
}

@Preview(showBackground = true, uiMode = Configuration.UI_MODE_NIGHT_NO)
@Composable
internal fun FeedProfileEditButtonsComposablePortraitLightPreview() {
    AquariumControllerTheme(darkTheme = false) {
        Surface {
            Column {
                FeedProfileEditButtonsComposable(modifier = Modifier.fillMaxWidth(), onFeedProfileEditUiEvent = {})
            }
        }
    }
}

@Preview(
    showBackground = true,
    uiMode = Configuration.UI_MODE_NIGHT_YES,
    device = "spec:width=600dp,height=400dp,orientation=landscape"
)
@Composable
internal fun FeedProfileEditButtonsComposableLandscapeDarkPreview() {
    AquariumControllerTheme(darkTheme = true) {
        Surface {
            Row {
                FeedProfileEditButtonsComposable(onFeedProfileEditUiEvent = {})
            }
        }
    }
}

@Preview(
    showBackground = true,
    uiMode = Configuration.UI_MODE_NIGHT_NO,
    device = "spec:width=600dp,height=400dp,orientation=landscape"
)
@Composable
internal fun FeedProfileEditButtonsComposableLandscapeLightPreview() {
    AquariumControllerTheme(darkTheme = false) {
        Surface {
            Row {
                FeedProfileEditButtonsComposable(onFeedProfileEditUiEvent = {})
            }
        }
    }
}
