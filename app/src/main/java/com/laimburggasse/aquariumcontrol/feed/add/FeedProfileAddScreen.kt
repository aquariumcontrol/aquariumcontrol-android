package com.laimburggasse.aquariumcontrol.feed.add

import android.content.Context
import android.content.res.Configuration
import android.widget.Toast
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
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.lifecycle.viewmodel.compose.viewModel
import androidx.navigation.NavHostController
import com.laimburggasse.aquariumcontrol.MainViewModel
import com.laimburggasse.aquariumcontrol.MockMainViewModel
import com.laimburggasse.aquariumcontrol.R
import com.laimburggasse.aquariumcontrol.ViewNavigationRoutes
import com.laimburggasse.aquariumcontrol.feed.MockFeedProfilesRepository
import com.laimburggasse.aquariumcontrol.feed.mockMainViewModel
import com.laimburggasse.aquariumcontrol.lib_composables.ScreenHeadlineComposable
import com.laimburggasse.aquariumcontrol.maindropdownmenu.MainDropDownMenuNavigationEvent
import com.laimburggasse.aquariumcontrol.maindropdownmenu.MainDropdownMenuComposable
import com.laimburggasse.aquariumcontrol.ui.theme.AquariumControllerTheme
import com.laimburggasse.aquariumcontrol.webapi.DataFetchResult

@Composable
fun FeedProfileAddScreen(
    navHostController: NavHostController? = null,
    mainViewModel: MainViewModel,
    darkTheme: Boolean? = null,
    feedProfileAddViewModelFactory: FeedProfileAddViewModelFactory,
    onNavigate: (MainDropDownMenuNavigationEvent) -> Unit
) {
    // This screen displays
    // - a graphic visualizing the feed profile
    // - button for triggering creation of feed profile
    // - a hierarchical menu for editing the feed profile

    val feedProfileAddViewModel: FeedProfileAddViewModel = viewModel(
        factory = feedProfileAddViewModelFactory
    )

    val context: Context = LocalContext.current // context for web API request and Toasts

    val feedProfileName by feedProfileAddViewModel.feedProfileName.collectAsState()

    LaunchedEffect(Unit) {
        feedProfileAddViewModel.eventFlow.collect { event ->
            when (event) {
                is FeedProfileAddEvent.Success -> {
                    navHostController?.navigate(route = ViewNavigationRoutes.feed)
                }

                is FeedProfileAddEvent.Failure -> {
                    Toast.makeText(
                        context,
                        context.getString(R.string.failed_to_add_feed_profile),
                        Toast.LENGTH_SHORT
                    ).show()
                }
            }
        }
    }

    AquariumControllerTheme(darkTheme) {
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
                            feedProfileAddViewModel.addFeedProfile()
                            // @todo: Add navigation to previous screen
                        },
                    ) {
                        Text(
                            text = stringResource(R.string.add_feed_profile),
                            fontSize = 16.sp
                        )
                    }
                }
                MainDropdownMenuComposable(
                    navigationRouteSource = ViewNavigationRoutes.feed,
                    mainViewModel = mainViewModel,
                    onNavigate
                )
            }
        }
    }
}
val mockFeedProfilesRepositoryEmpty = MockFeedProfilesRepository(
    mockFeedProfiles = DataFetchResult.Success(data =emptyList())
)

val mockMainViewModel = MockMainViewModel()

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
internal fun FeedProfileAddScreenPreview() {
    FeedProfileAddScreen(
        mainViewModel = mockMainViewModel,
        feedProfileAddViewModelFactory = FeedProfileAddViewModelFactory(
            feedProfilesRepository = mockFeedProfilesRepositoryEmpty,
        ),
        onNavigate = {}
    )
}