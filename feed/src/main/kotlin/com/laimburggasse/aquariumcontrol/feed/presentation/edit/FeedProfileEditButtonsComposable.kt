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

    Button(
        modifier = modifier?.then(other = Modifier
            .fillMaxWidth()
            .padding(horizontal = 8.dp)) ?: Modifier,
        onClick = {
            onFeedProfileEditUiEvent(FeedProfileEditUiEvent.Update)
            Toast.makeText(
                context,
                context.getString(R.string.sent_request_to_controller_for_updating_feed_profile),
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
@Preview(showBackground = true, uiMode = Configuration.UI_MODE_NIGHT_NO)
@Composable
internal fun FeedProfileEditButtonsComposablePortraitPreview() {
    AquariumControllerTheme {
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
@Preview(
    showBackground = true,
    uiMode = Configuration.UI_MODE_NIGHT_NO,
    device = "spec:width=600dp,height=400dp,orientation=landscape"
)
@Composable
internal fun FeedProfileEditButtonsComposableLandscapePreview() {
    AquariumControllerTheme {
        Surface {
            Row {
                FeedProfileEditButtonsComposable(onFeedProfileEditUiEvent = {})
            }
        }
    }
}
