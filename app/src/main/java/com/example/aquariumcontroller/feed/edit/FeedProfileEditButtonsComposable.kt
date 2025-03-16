package com.example.aquariumcontroller.feed.edit

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
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.aquariumcontroller.MainViewModel
import com.example.aquariumcontroller.R
import com.example.aquariumcontroller.ui.theme.AquariumControllerTheme
import com.example.aquariumcontroller.webapi.WebApiPostRequest
import com.example.aquariumcontroller.webapi.WebApiUrls
import com.example.aquariumcontroller.webapi.webApiPostRequestExecution

@Composable
fun FeedProfileEditButtonsComposable(
    modifier: Modifier? = Modifier,
    feedProfileEditViewModel: FeedProfileEditViewModel? = null,
    mainViewModel: MainViewModel? = null,
) {
    // This composable displays the buttons for updating the feed profile.
    // The arrangement of the buttons follows the device orientation.

    val webApiPostRequest = WebApiPostRequest()

    val context = LocalContext.current // context for web API request and Toasts

    val coroutineScope = rememberCoroutineScope()

    Button(
        modifier = modifier?.then(Modifier
            .fillMaxWidth()
            .padding(horizontal = 8.dp)) ?: Modifier,
        onClick = {
            if (mainViewModel != null) {
                webApiPostRequestExecution(
                    mainViewModel = mainViewModel,
                    apiUrl = WebApiUrls.FEED_POST_PROFILE_UPDATE,
                    coroutineScope = coroutineScope,
                    webApiPostRequest = webApiPostRequest,
                    context = context,
                    optionalGetParams = feedProfileEditViewModel?.getApiCallParameters() ?: emptyMap()
                )

                // Show the Toast after the API call
                Toast.makeText(
                    context,
                    context.getString(R.string.sent_request_to_controller_for_updating_feed_profile),
                    Toast.LENGTH_SHORT
                ).show()
                feedProfileEditViewModel?.updateFeedProfile()
            }
        },
    ) {
        Text(
            text = stringResource(id = R.string.update_feed_profile),
            fontSize = 16.sp
        )
    }
    Spacer(modifier = Modifier.width(10.dp))
    Button(
        modifier = modifier?.then(Modifier
            .fillMaxWidth()
            .padding(horizontal = 8.dp)) ?: Modifier,
        onClick = {
            feedProfileEditViewModel?.resetFeedProfile()
        },
    ) {
        Text(
            text = stringResource(id = R.string.reset_changes_to_feed_profile),
            fontSize = 16.sp
        )
    }
}

@Preview(showBackground = true, uiMode = Configuration.UI_MODE_NIGHT_YES)
@Preview(showBackground = true, uiMode = Configuration.UI_MODE_NIGHT_NO)
@Composable
fun FeedProfileEditButtonsComposablePortraitPreview() {
    AquariumControllerTheme {
        Surface {
            Column {
                FeedProfileEditButtonsComposable(modifier = Modifier.fillMaxWidth())
            }
        }
    }
}

@Preview(
    showBackground = true,
    uiMode = Configuration.UI_MODE_NIGHT_YES,
    device = "spec:width=600dp,height=400dp,orientation=landscape"
)
@Preview(
    showBackground = true,
    uiMode = Configuration.UI_MODE_NIGHT_NO,
    device = "spec:width=600dp,height=400dp,orientation=landscape"
)
@Composable
fun FeedProfileEditButtonsComposableLandscapePreview() {
    AquariumControllerTheme {
        Surface {
            Row {
                FeedProfileEditButtonsComposable()
            }
        }
    }
}