package com.example.aquariumcontroller.feed.edit

import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.Button
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.lifecycle.viewmodel.compose.viewModel
import androidx.navigation.NavHostController
import com.example.aquariumcontroller.MainDropdownMenuComposable
import com.example.aquariumcontroller.MainViewModel
import com.example.aquariumcontroller.R
import com.example.aquariumcontroller.ViewNavigationRoutes
import com.example.aquariumcontroller.feed.FeedProfile
import com.example.aquariumcontroller.feed.FeedProfileImport
import com.example.aquariumcontroller.ui.theme.AquariumControllerTheme

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

    AquariumControllerTheme {
        // A surface container using the 'background' color from the theme
        Surface(
            modifier = Modifier.fillMaxSize(),
            color = MaterialTheme.colorScheme.background
        ) {
            Column(
                horizontalAlignment = Alignment.CenterHorizontally
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
                Button(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(horizontal = 8.dp),
                    onClick = {
                        // todo: add update feed profile functionality
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

    val mockFeedProfile = FeedProfile(mockFeedProfileImport)

    AquariumControllerTheme {
        FeedProfileEditScreen(
            feedProfile = mockFeedProfile,
        )
    }
}