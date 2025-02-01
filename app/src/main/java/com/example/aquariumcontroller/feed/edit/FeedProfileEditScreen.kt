package com.example.aquariumcontroller.feed.edit

import android.widget.Toast
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.material3.Button
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.lifecycle.viewmodel.compose.viewModel
import androidx.navigation.NavHostController
import com.example.aquariumcontroller.ApiUrls
import com.example.aquariumcontroller.MainDropdownMenuComposable
import com.example.aquariumcontroller.MainViewModel
import com.example.aquariumcontroller.PostRequest
import com.example.aquariumcontroller.R
import com.example.aquariumcontroller.ViewNavigationRoutes
import com.example.aquariumcontroller.feed.FeedProfile
import com.example.aquariumcontroller.feed.FeedProfileImport
import com.example.aquariumcontroller.ui.theme.AquariumControllerTheme
import com.example.aquariumcontroller.webApiPostRequest

@Composable
fun FeedProfileEditScreen(
    navHostController: NavHostController? = null,
    feedProfile: FeedProfile,
    mainViewModel: MainViewModel? = null
) {
    // This screen displays
    // - a graphic visualizing the feed profile
    // - button for triggering update of feed profile
    // - button for triggering delete of feed profile
    // - button for scheduling the execution of the feed profile
    // - a hierarchical menu for editing the feed profile

    val feedProfileEditViewModel: FeedProfileEditViewModel = viewModel(
        factory = FeedProfileEditViewModelFactory(feedProfile)
    )

    val postRequest = PostRequest()

    val context = LocalContext.current // context for web API request and Toasts

    val coroutineScope = rememberCoroutineScope()

    AquariumControllerTheme {
        // A surface container using the 'background' color from the theme
        Surface(
            modifier = Modifier.fillMaxWidth(),
            color = MaterialTheme.colorScheme.background
        ) {
            Column(
                horizontalAlignment = Alignment.CenterHorizontally,
                modifier = Modifier.verticalScroll(rememberScrollState())
            ) {
                MainDropdownMenuComposable(
                    navHostController = navHostController,
                    navigationRouteSource = ViewNavigationRoutes.feed,
                    mainViewModel = mainViewModel
                )
                Text(
                    text = stringResource(id = R.string.FeedProfileEditing),
                    style = MaterialTheme.typography.headlineSmall,
                    modifier = Modifier.padding(16.dp),
                )
                FeedProfileDisplayComposable(feedProfile = feedProfile)
                FeedProfileDisplayLegend()
                Button(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(horizontal = 8.dp),
                    onClick = {
                        if (mainViewModel != null) {
                            webApiPostRequest(
                                mainViewModel = mainViewModel,
                                apiUrl = ApiUrls.FEED_POST_PROFILE_UPDATE,
                                coroutineScope = coroutineScope,
                                postRequest = postRequest,
                                context = context,
                                optionalGetParams = feedProfileEditViewModel.getApiCallParameters()
                            )

                            // Show the Toast after the API call
                            Toast.makeText(
                                context,
                                "Sent request to controller for updating feed profile",
                                Toast.LENGTH_SHORT
                            ).show()
                            feedProfileEditViewModel.updateFeedProfile()
                        }
                    },
                ) {
                    Text(
                        text = stringResource(id = R.string.UpdateFeedProfile),
                        fontSize = 16.sp
                    )
                }
                Button(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(horizontal = 8.dp),
                    onClick = {
                        feedProfileEditViewModel.resetFeedProfile()
                    },
                ) {
                    Text(
                        text = stringResource(id = R.string.ResetFeedProfile),
                        fontSize = 16.sp
                    )
                }
                Button(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(horizontal = 8.dp),
                    onClick = {
                        // todo: add delete feed profile functionality
                        navHostController?.navigate(ViewNavigationRoutes.feed)
                    },
                ) {
                    Text(
                        text = stringResource(id = R.string.DeleteFeedProfile),
                        fontSize = 16.sp
                    )
                }
                FeedProfileEditMenuComposable(
                    feedProfileEditMenuItems = feedProfileEditViewModel.phaseMenuItems,
                    feedProfileEditViewModel = feedProfileEditViewModel
                )
            }
        }
    }
}

@Preview(showBackground = true)
@Composable
fun FeedViewPreview() {
    val mockFeedProfileImport = FeedProfileImport()
    mockFeedProfileImport.profileId = 1
    mockFeedProfileImport.profileName = "Test Profile"
    mockFeedProfileImport.profileId = 1
    mockFeedProfileImport.profileName = "Test Profile"
    mockFeedProfileImport.pause01duration = 1000
    mockFeedProfileImport.pause02duration = 1000
    mockFeedProfileImport.pause03duration = 1000
    mockFeedProfileImport.pause04duration = 1000
    mockFeedProfileImport.pause05duration = 1000
    mockFeedProfileImport.pause06duration = 1000
    mockFeedProfileImport.pause07duration = 1000
    mockFeedProfileImport.pause08duration = 1000
    mockFeedProfileImport.pause09duration = 1000
    mockFeedProfileImport.pause10duration = 1000
    mockFeedProfileImport.feed01duration = 1000
    mockFeedProfileImport.feed02duration = 1000
    mockFeedProfileImport.feed03duration = 1000
    mockFeedProfileImport.feed04duration = 1000
    mockFeedProfileImport.feed05duration = 1000
    mockFeedProfileImport.feed06duration = 1000
    mockFeedProfileImport.feed07duration = 1000
    mockFeedProfileImport.feed08duration = 1000
    mockFeedProfileImport.feed09duration = 1000
    mockFeedProfileImport.feed10duration = 1000
    mockFeedProfileImport.pause01skimmer = 1
    mockFeedProfileImport.pause02skimmer = 1
    mockFeedProfileImport.pause03skimmer = 1
    mockFeedProfileImport.pause04skimmer = 1
    mockFeedProfileImport.pause05skimmer = 1
    mockFeedProfileImport.pause06skimmer = 1
    mockFeedProfileImport.pause07skimmer = 1
    mockFeedProfileImport.pause08skimmer = 1
    mockFeedProfileImport.pause09skimmer = 1
    mockFeedProfileImport.pause10skimmer = 0
    mockFeedProfileImport.feed01skimmer = 1
    mockFeedProfileImport.feed02skimmer = 1
    mockFeedProfileImport.feed03skimmer = 1
    mockFeedProfileImport.feed04skimmer = 1
    mockFeedProfileImport.feed05skimmer = 1
    mockFeedProfileImport.feed06skimmer = 1
    mockFeedProfileImport.feed07skimmer = 1
    mockFeedProfileImport.feed08skimmer = 1
    mockFeedProfileImport.feed09skimmer = 1
    mockFeedProfileImport.feed10skimmer = 0
    mockFeedProfileImport.pause01mainPump1 = 1
    mockFeedProfileImport.pause02mainPump1 = 1
    mockFeedProfileImport.pause03mainPump1 = 1
    mockFeedProfileImport.pause04mainPump1 = 1
    mockFeedProfileImport.pause05mainPump1 = 1
    mockFeedProfileImport.pause06mainPump1 = 1
    mockFeedProfileImport.pause07mainPump1 = 1
    mockFeedProfileImport.pause08mainPump1 = 1
    mockFeedProfileImport.pause09mainPump1 = 0
    mockFeedProfileImport.pause10mainPump1 = 0
    mockFeedProfileImport.feed01mainPump1 = 1
    mockFeedProfileImport.feed02mainPump1 = 1
    mockFeedProfileImport.feed03mainPump1 = 1
    mockFeedProfileImport.feed04mainPump1 = 1
    mockFeedProfileImport.feed05mainPump1 = 1
    mockFeedProfileImport.feed06mainPump1 = 1
    mockFeedProfileImport.feed07mainPump1 = 1
    mockFeedProfileImport.feed08mainPump1 = 1
    mockFeedProfileImport.feed09mainPump1 = 0
    mockFeedProfileImport.feed10mainPump1 = 0
    mockFeedProfileImport.pause01mainPump2 = 1
    mockFeedProfileImport.pause02mainPump2 = 1
    mockFeedProfileImport.pause03mainPump2 = 1
    mockFeedProfileImport.pause04mainPump2 = 1
    mockFeedProfileImport.pause05mainPump2 = 1
    mockFeedProfileImport.pause06mainPump2 = 1
    mockFeedProfileImport.pause07mainPump2 = 1
    mockFeedProfileImport.pause08mainPump2 = 0
    mockFeedProfileImport.pause09mainPump2 = 0
    mockFeedProfileImport.pause10mainPump2 = 0
    mockFeedProfileImport.feed01mainPump2 = 1
    mockFeedProfileImport.feed02mainPump2 = 1
    mockFeedProfileImport.feed03mainPump2 = 1
    mockFeedProfileImport.feed04mainPump2 = 1
    mockFeedProfileImport.feed05mainPump2 = 1
    mockFeedProfileImport.feed06mainPump2 = 1
    mockFeedProfileImport.feed07mainPump2 = 1
    mockFeedProfileImport.feed08mainPump2 = 0
    mockFeedProfileImport.feed09mainPump2 = 0
    mockFeedProfileImport.feed10mainPump2 = 0
    mockFeedProfileImport.pause01auxPump1 = 1
    mockFeedProfileImport.pause02auxPump1 = 1
    mockFeedProfileImport.pause03auxPump1 = 1
    mockFeedProfileImport.pause04auxPump1 = 1
    mockFeedProfileImport.pause05auxPump1 = 1
    mockFeedProfileImport.pause06auxPump1 = 1
    mockFeedProfileImport.pause07auxPump1 = 0
    mockFeedProfileImport.pause08auxPump1 = 0
    mockFeedProfileImport.pause09auxPump1 = 0
    mockFeedProfileImport.pause10auxPump1 = 0
    mockFeedProfileImport.feed01auxPump1 = 1
    mockFeedProfileImport.feed02auxPump1 = 1
    mockFeedProfileImport.feed03auxPump1 = 1
    mockFeedProfileImport.feed04auxPump1 = 1
    mockFeedProfileImport.feed05auxPump1 = 1
    mockFeedProfileImport.feed06auxPump1 = 1
    mockFeedProfileImport.feed07auxPump1 = 0
    mockFeedProfileImport.feed08auxPump1 = 0
    mockFeedProfileImport.feed09auxPump1 = 0
    mockFeedProfileImport.feed10auxPump1 = 0
    mockFeedProfileImport.pause01auxPump2 = 1
    mockFeedProfileImport.pause02auxPump2 = 1
    mockFeedProfileImport.pause03auxPump2 = 1
    mockFeedProfileImport.pause04auxPump2 = 1
    mockFeedProfileImport.pause05auxPump2 = 1
    mockFeedProfileImport.pause06auxPump2 = 1
    mockFeedProfileImport.pause07auxPump2 = 0
    mockFeedProfileImport.pause08auxPump2 = 0
    mockFeedProfileImport.pause09auxPump2 = 0
    mockFeedProfileImport.pause10auxPump2 = 0
    mockFeedProfileImport.feed01auxPump2 = 1
    mockFeedProfileImport.feed02auxPump2 = 1
    mockFeedProfileImport.feed03auxPump2 = 1
    mockFeedProfileImport.feed04auxPump2 = 1
    mockFeedProfileImport.feed05auxPump2 = 1
    mockFeedProfileImport.feed06auxPump2 = 1
    mockFeedProfileImport.feed07auxPump2 = 0
    mockFeedProfileImport.feed08auxPump2 = 0
    mockFeedProfileImport.feed09auxPump2 = 0
    mockFeedProfileImport.feed10auxPump2 = 0

    val mockFeedProfile = FeedProfile(mockFeedProfileImport)

    AquariumControllerTheme {
        FeedProfileEditScreen(
            feedProfile = mockFeedProfile,
        )
    }
}