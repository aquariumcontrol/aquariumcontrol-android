package com.example.aquariumcontroller.feed

import android.content.res.Configuration
import android.os.Build
import android.util.Log
import androidx.annotation.RequiresApi
import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Delete
import androidx.compose.material.icons.filled.Edit
import androidx.compose.material.icons.filled.Schedule
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
import com.example.aquariumcontroller.ApiUrls
import com.example.aquariumcontroller.MainViewModel
import com.example.aquariumcontroller.PostRequest
import com.example.aquariumcontroller.ViewNavigationRoutes
import com.example.aquariumcontroller.ui.theme.AquariumControllerTheme
import com.example.aquariumcontroller.webApiPostRequest

@Composable
fun FeedProfileSummaryComposable(
    navHostController: NavHostController? = null,
    mainViewModel: MainViewModel? = null,
    feedProfile: FeedProfile,
    feedProfileViewModel: FeedProfileViewModel? = null
) {
    val coroutineScope = rememberCoroutineScope()
    val postRequest = PostRequest()
    val context = LocalContext.current // context for web API request and Toasts

    // This composable displays one Feed dosing profile summary with two icons for editing or scheduling.
    // When the user clicks on one of the icons, the corresponding action is triggered.
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
                .weight(1f)
        ) {
            Row(verticalAlignment = Alignment.CenterVertically) {
                Text(
                    text = "ID: ${feedProfile.profileID}",
                    fontSize = 12.sp
                )
            }
        }

        Column(
            modifier = Modifier.weight(3f)
        ) {
            Row(verticalAlignment = Alignment.CenterVertically) {
                Text(
                    text = "Name: ${feedProfile.profileName}",
                    fontSize = 12.sp
                )
            }
        }
        Column(
            horizontalAlignment = Alignment.End
        ) {
            Row(verticalAlignment = Alignment.CenterVertically) {
                Icon(
                    imageVector = Icons.Filled.Edit,
                    contentDescription = "Edit",
                    modifier = Modifier
                        .size(48.dp)
                        .clickable {
                            Log.d(
                                "FeedProfileSummaryScreen",
                                "Clicked on Edit icon for {${feedProfile.profileName}}"
                            )
                            mainViewModel?.updateSelectedFeedProfile(feedProfile)
                            navHostController?.navigate(ViewNavigationRoutes.feedEdit)
                        }
                )
                Spacer(modifier = Modifier.width(16.dp))
                Icon(
                    imageVector = Icons.Filled.Schedule,
                    contentDescription = "Schedule",
                    modifier = Modifier
                        .size(48.dp)
                        .clickable {
                            Log.d(
                                "FeedProfileSummaryScreen",
                                "Clicked on Schedule icon for {${feedProfile.profileName}}"
                            )
                            mainViewModel?.updateSelectedFeedProfile(feedProfile)
                            mainViewModel?.updateSelectedFeedScheduleEntry(null)
                            navHostController?.navigate(ViewNavigationRoutes.feedSchedule)
                        }
                )
                Spacer(modifier = Modifier.width(16.dp))
                Icon(
                    imageVector = Icons.Filled.Delete,
                    contentDescription = "Schedule",
                    modifier = Modifier
                        .size(48.dp)
                        .clickable {
                            Log.d(
                                "FeedProfileSummaryScreen",
                                "Clicked on Delete icon for {${feedProfile.profileName}}"
                            )
                            feedProfileViewModel?.deleteFeedProfile(feedProfile)
                            if (mainViewModel != null) {
                                webApiPostRequest(
                                    apiUrl = ApiUrls.FEED_POST_REMOVE_PROFILE,
                                    mainViewModel = mainViewModel,
                                    coroutineScope = coroutineScope,
                                    postRequest = postRequest,
                                    context = context,
                                    optionalGetParams = feedProfile.getFeedProfileApiCallParameters()
                                )
                            }
                        }
                )
            }
        }
    }
}

@RequiresApi(Build.VERSION_CODES.O)
@Preview(showBackground = true, uiMode = Configuration.UI_MODE_NIGHT_YES)
@Composable
fun FeedProfileSummaryComposablePreview() {
    val mockFeedProfileImport = FeedProfileImport()
    mockFeedProfileImport.profileId = 1
    mockFeedProfileImport.profileName = "Test Profile"

    val mockFeedProfile = FeedProfile(mockFeedProfileImport)

    AquariumControllerTheme {
        FeedProfileSummaryComposable(
            feedProfile = mockFeedProfile,
            feedProfileViewModel = null,
        )
    }
}