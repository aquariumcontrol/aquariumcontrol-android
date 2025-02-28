package com.example.aquariumcontroller.feed.schedule

import android.content.res.Configuration
import android.util.Log
import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Delete
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.navigation.NavHostController
import com.example.aquariumcontroller.webapi.WebApiUrls
import com.example.aquariumcontroller.MainViewModel
import com.example.aquariumcontroller.webapi.WebApiPostRequest
// import com.example.aquariumcontroller.ViewNavigationRoutes will be used in future implementation
import com.example.aquariumcontroller.ui.theme.AquariumControllerTheme
import com.example.aquariumcontroller.webapi.webApiPostRequestExecution

@Composable
fun FeedScheduleEntryComposable(
    navHostController: NavHostController? = null,   // for future implementation
    mainViewModel: MainViewModel? = null,
    feedScheduleEntry: FeedScheduleEntry,
    feedScheduleViewModel: FeedScheduleViewModel? = null
) {
    // This composable displays one feed schedule entry:
    // - Name of feed profile
    // - timestamp of schedule
    // - information if the feed profile execution should be repeated daily

    val coroutineScope = rememberCoroutineScope()
    val webApiPostRequest = WebApiPostRequest()
    val context = LocalContext.current // context for web API request and Toasts

    Row(
        modifier = Modifier
            .fillMaxWidth()
            .border(
                border = BorderStroke(
                    1.dp,
                    MaterialTheme.colorScheme.onSurface
                )
            )
            .padding(8.dp)
    ) {
        Column(
            modifier = Modifier
                .weight(5f)
        ) {
            Text(
                text = "Profile name: ${feedScheduleEntry.profileName}",
                fontSize = 12.sp
            )
            Text(
                text = "Scheduled for ${feedScheduleEntry.getTimestampString()}",
                fontSize = 12.sp
            )
            Text(
                text = "Repeat daily: ${if (feedScheduleEntry.isDaily) "Yes" else "No"}",
                fontSize = 12.sp
            )
        }
        Column(
            Modifier
                .align(Alignment.CenterVertically)
        ) {
            Row {
                /* deactivated because API is currently (Feb, 2025) not available
                Icon(
                    imageVector = Icons.Filled.Edit,
                    contentDescription = "Edit",
                    Modifier
                        .size(48.dp)
                        .clickable {
                            Log.d(
                                "FeedScheduleEntryComposable",
                                "Clicked on Edit icon for schedule entry at {${feedScheduleEntry.getTimestampString()}}"
                            )
                            mainViewModel?.updateSelectedFeedScheduleEntry(feedScheduleEntry)
                            navHostController?.navigate(ViewNavigationRoutes.feedSchedule)
                        }
                )
                Spacer(modifier = Modifier.width(16.dp))

                 */
                Icon(
                    imageVector = Icons.Filled.Delete,
                    contentDescription = "Delete",
                    Modifier
                        .size(48.dp)
                        .clickable {
                            Log.d(
                                "FeedScheduleEntryComposable",
                                "Clicked on Delete icon for schedule entry at {${feedScheduleEntry.getTimestampString()}}"
                            )
                            if (mainViewModel != null) {
                                mainViewModel.updateSelectedFeedScheduleEntry(null)
                                feedScheduleViewModel?.deleteFeedScheduleEntry(feedScheduleEntry)
                                webApiPostRequestExecution(
                                    apiUrl = WebApiUrls.FEED_POST_SCHEDULE_REMOVE_ENTRY,
                                    mainViewModel = mainViewModel,
                                    coroutineScope = coroutineScope,
                                    webApiPostRequest = webApiPostRequest,
                                    context = context,
                                    optionalGetParams = feedScheduleEntry.getApiParameters()
                                )
                            }
                        }
                )
            }
        }
    }
}

@Preview(showBackground = true, uiMode = Configuration.UI_MODE_NIGHT_YES)
@Composable
fun FeedScheduleEntryComposablePreview() {
    val mockFeedScheduleEntryImport = FeedScheduleEntryImport(
        timestampString = "2023-09-01 12:00:00",
        profileName = "MockProfile",
        isDaily = 1
    )

    val mockFeedScheduleEntry = mockFeedScheduleEntryImport.transformToFeedScheduleEntry()

    AquariumControllerTheme {
        FeedScheduleEntryComposable(
            feedScheduleEntry = mockFeedScheduleEntry
        )
    }
}