package com.laimburggasse.aquariumcontrol.feed.presentation.entry

import android.content.res.Configuration
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.width
import androidx.compose.material3.Button
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.laimburggasse.aquariumcontrol.feed.R
import com.laimburggasse.aquariumcontrol.feed.presentation.navigation.FeedNavigationEvent
import com.laimburggasse.aquariumcontrol.common.presentation.theme.AquariumControllerTheme

@Composable
fun FeedHistorySelectionButtonsComposable(
    modifier: Modifier? = Modifier,
    onNavigateFromFeed: (FeedNavigationEvent, Int?, Boolean?, Long?) -> Unit
) {
    // This composable displays the feed history selection buttons.
    // The arrangement of the buttons follows the device orientation.

    Button(
        modifier = modifier?.then(other = Modifier.padding(horizontal = 8.dp)) ?: Modifier,
        onClick = { onNavigateFromFeed(FeedNavigationEvent.ToFeedHistory24h, null, null, null) },
    ) {
        Text(
            text = stringResource(id = R.string.view_feed_events_24h),
            fontSize = 16.sp
        )
    }
    Spacer(modifier = Modifier.width(width = 10.dp))
    Button(
        modifier = modifier?.then(Modifier.padding(horizontal = 8.dp)) ?: Modifier,
        onClick = { onNavigateFromFeed(FeedNavigationEvent.ToFeedHistory7d, null, null, null) },
    ) {
        Text(
            text = stringResource(id = R.string.view_feed_events_7d),
            fontSize = 16.sp
        )
    }
}

@Preview(showBackground = true, uiMode = Configuration.UI_MODE_NIGHT_YES)
@Preview(showBackground = true, uiMode = Configuration.UI_MODE_NIGHT_NO)
@Composable
internal fun FeedHistorySelectionButtonsComposablePortraitPreview() {
    AquariumControllerTheme {
        Surface {
            Column {
                FeedHistorySelectionButtonsComposable( onNavigateFromFeed = { event, id, isNew, millis -> })
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
internal fun FeedHistorySelectionButtonsComposableLandscapePreview() {
    AquariumControllerTheme {
        Surface {
            Row {
                FeedHistorySelectionButtonsComposable( onNavigateFromFeed = { event, id, isNew, millis -> })
            }
        }
    }
}