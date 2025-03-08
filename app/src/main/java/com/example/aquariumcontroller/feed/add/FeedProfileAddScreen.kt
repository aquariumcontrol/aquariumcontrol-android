package com.example.aquariumcontroller.feed.add

import android.content.res.Configuration
import android.util.Log
import androidx.compose.foundation.layout.Box
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
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
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
import com.example.aquariumcontroller.MainViewModel
import com.example.aquariumcontroller.R
import com.example.aquariumcontroller.ViewNavigationRoutes
import com.example.aquariumcontroller.feed.FeedProfile
import com.example.aquariumcontroller.feed.FeedProfileViewModel
import com.example.aquariumcontroller.maindropdownmenu.MainDropdownMenuComposable
import com.example.aquariumcontroller.lib_composables.ScreenHeadlineComposable
import com.example.aquariumcontroller.ui.theme.AquariumControllerTheme
import com.example.aquariumcontroller.webapi.WebApiPostRequest
import com.example.aquariumcontroller.webapi.WebApiUrls
import com.example.aquariumcontroller.webapi.webApiPostRequestExecution
import kotlinx.coroutines.delay
import kotlinx.coroutines.launch

@Composable
fun FeedProfileAddScreen(
    navHostController: NavHostController? = null,
    mainViewModel: MainViewModel? = null,
    myFeedProfileViewModel: FeedProfileViewModel? = null
) {
    // This screen displays
    // - a graphic visualizing the feed profile
    // - button for triggering creation of feed profile
    // - a hierarchical menu for editing the feed profile

    val feedProfileAddViewModel: FeedProfileAddViewModel = viewModel(
        factory = FeedProfileAddViewModelFactory(feedProfile = FeedProfile())
    )

    val webApiPostRequest = WebApiPostRequest()

    val context = LocalContext.current // context for web API request and Toasts

    val coroutineScope = rememberCoroutineScope()

    val feedProfileName by feedProfileAddViewModel.feedProfileName.collectAsState()

    AquariumControllerTheme {
        Surface(
            modifier = Modifier.fillMaxWidth(),
            color = MaterialTheme.colorScheme.background
        ) {
            Box {
                Column(
                    horizontalAlignment = Alignment.CenterHorizontally,
                    modifier = Modifier
                        .fillMaxSize()
                        .verticalScroll(rememberScrollState())
                ) {
                    ScreenHeadlineComposable(
                        text = stringResource(id = R.string.feed_profile_add)
                    )

                    FeedProfileAddNameEditableOutlinedFieldComposable(
                        onFeedProfileNameChange = {
                            Log.d("FeedProfileAddScreen", "Feed profile name changed to $it")
                            feedProfileAddViewModel.setFeedProfileName(it)
                        },
                        modifier = Modifier.padding(16.dp)
                    )

                    Button(
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(horizontal = 8.dp),
                        enabled = feedProfileName.isNotEmpty(),
                        onClick = {
                            if (mainViewModel != null) {
                                webApiPostRequestExecution(
                                    mainViewModel = mainViewModel,
                                    apiUrl = WebApiUrls.FEED_POST_PROFILE_CREATE,
                                    coroutineScope = coroutineScope,
                                    webApiPostRequest = webApiPostRequest,
                                    context = context,
                                    optionalGetParams = feedProfileAddViewModel.getApiCallParameters()
                                )

                                myFeedProfileViewModel?.addFeedProfile(
                                    feedProfile = feedProfileAddViewModel.getFeedProfile()
                                )

                                coroutineScope.launch {
                                    // give the API some time and navigate back to main feed screen
                                    delay(500)
                                    navHostController?.navigate(ViewNavigationRoutes.feed)
                                }
                            }
                        },
                    ) {
                        Text(
                            text = stringResource(R.string.add_feed_profile),
                            fontSize = 16.sp
                        )
                    }
                }
                MainDropdownMenuComposable(
                    navHostController = navHostController,
                    navigationRouteSource = ViewNavigationRoutes.feed,
                    mainViewModel = mainViewModel
                )
            }
        }
    }
}

@Preview(showBackground = true, uiMode = Configuration.UI_MODE_NIGHT_YES)
@Preview(showBackground = true, uiMode = Configuration.UI_MODE_NIGHT_NO)
@Preview(
    showBackground = true,
    uiMode = Configuration.UI_MODE_NIGHT_YES,
    device = "spec:width=600dp,height=400dp,orientation=landscape"
)
@Preview(
    showBackground = true,
    uiMode = Configuration.UI_MODE_NIGHT_NO,
    device = "spec:width=600dp,height=400dp,orientation=landscape"
)@Composable
fun FeedProfileAddScreenPreview() {
    FeedProfileAddScreen()
}