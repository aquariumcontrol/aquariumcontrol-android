package com.example.aquariumcontroller.feed

import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.material3.Button
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.navigation.NavHostController
import com.example.aquariumcontroller.ApiUrls
import com.example.aquariumcontroller.MainDropdownMenuScreen
import com.example.aquariumcontroller.MainViewModel
import com.example.aquariumcontroller.R
import com.example.aquariumcontroller.ViewNavigationRoutes
import com.example.aquariumcontroller.balling.BallingProfileReader
import com.example.aquariumcontroller.balling.BallingProfileSummaryScreen
import com.example.aquariumcontroller.ui.theme.AquariumControllerTheme
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext

@Composable
fun FeedScreen(
    navHostController: NavHostController? = null,
    mockFeedProfiles: List<FeedProfile>? = null,
    mainViewModel: MainViewModel? = null
) {
    // This screen displays
    // - a button for visualizing all feed events of the last 24h
    // - a button for visualizing all feed events of the last 7 days
    // - feed profiles
    // - floating action button for adding feed profile

    var feedProfiles by remember { mutableStateOf(mockFeedProfiles) }

    if (mainViewModel != null) {
        val url = "http://" + mainViewModel.getControllerAddress() + ApiUrls.FEED_GET_PROFILES
        val reader = FeedProfileReader(url)

        LaunchedEffect(Unit) {
            withContext(Dispatchers.IO) {
                feedProfiles = reader.readFeedProfiles()
            }
        }
    }

    AquariumControllerTheme {
        // A surface container using the 'background' color from the theme
        Surface(
            modifier = Modifier.fillMaxSize(),
            color = MaterialTheme.colorScheme.background
        ) {
            Column {
                MainDropdownMenuScreen(
                    navHostController = navHostController,
                    navigationRouteSource = ViewNavigationRoutes.feed,
                    mainViewModel = mainViewModel
                )
                Text(
                    text = stringResource(id = R.string.FeedControl),
                    style = MaterialTheme.typography.headlineSmall,
                    modifier = Modifier.padding(16.dp)
                )
                Button(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(horizontal = 8.dp),
                    onClick = {
                        navHostController?.navigate(ViewNavigationRoutes.feedHistory24h)
                    },
                ) {
                    Text(
                        text = stringResource(id = R.string.ViewFeedEvents24h),
                        fontSize = 16.sp
                    )
                }
                Button(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(horizontal = 8.dp),
                    onClick = {
                        navHostController?.navigate(ViewNavigationRoutes.feedHistory7d)
                    },
                ) {
                    Text(
                        text = stringResource(id = R.string.ViewFeedEvents7d),
                        fontSize = 16.sp
                    )
                }
                if (feedProfiles != null) {
                    LazyColumn(modifier = Modifier
                        .fillMaxSize()
                        .padding(16.dp)) {
                        items(feedProfiles!!) { feedProfile ->
                            BallingProfileSummaryScreen(
                                navHostController = navHostController,
                                mainViewModel = mainViewModel,
                                ballingProfile = feedProfile
                            )
                        }
                    }
                }
            }
        }
    }
}

@Preview(showBackground = true)
@Composable
fun FeedViewPreview() {
    AquariumControllerTheme {
        FeedScreen()
    }
}