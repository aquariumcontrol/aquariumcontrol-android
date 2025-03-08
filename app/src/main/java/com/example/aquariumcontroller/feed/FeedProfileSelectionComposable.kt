package com.example.aquariumcontroller.feed

import android.content.res.Configuration
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Add
import androidx.compose.material3.FloatingActionButton
import androidx.compose.material3.Icon
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.lifecycle.viewmodel.compose.viewModel
import androidx.navigation.NavHostController
import com.example.aquariumcontroller.MainViewModel
import com.example.aquariumcontroller.ViewNavigationRoutes
import com.example.aquariumcontroller.ui.theme.AquariumControllerTheme

@Composable
fun FeedProfileSelectionComposable(
    mockFeedProfiles: List<FeedProfile>? = null,
    myFeedProfileViewModel: FeedProfileViewModel? = null,
    mainViewModel: MainViewModel? = null,
    navHostController: NavHostController? = null,
    modifier: Modifier? = null
) {
    // This composable displays a label "Feed Profiles"
    // and a list of feed profiles.

    // instantiate a new view model if there is no view model provided as parameter
    val feedProfileViewModel: FeedProfileViewModel = myFeedProfileViewModel ?: viewModel(
        factory = FeedProfileViewModelFactory(feedProfiles = mockFeedProfiles)
    )

    Text(text = "Feed Profiles")
    LazyColumn(
        modifier = Modifier
            .fillMaxWidth()
            .padding(8.dp)
    ) {
        items(feedProfileViewModel.getFeedProfiles()) { feedProfile ->
            FeedProfileSummaryComposable(
                navHostController = navHostController,
                mainViewModel = mainViewModel,
                feedProfile = feedProfile,
                feedProfileViewModel = feedProfileViewModel
            )
        }
    }
    FloatingActionButton(
        onClick = {
            navHostController?.navigate(ViewNavigationRoutes.feedAddProfile)
        },
        modifier = modifier?.then(
            Modifier
                .padding(horizontal = 8.dp)
        ) ?: Modifier
    ) {
        Icon(
            imageVector = Icons.Filled.Add,
            contentDescription = "Create New Profile"
        )
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
)
@Composable
fun FeedProfileSelectionComposablePreview() {
    AquariumControllerTheme {
        Surface {
            Column(horizontalAlignment = Alignment.CenterHorizontally) {
                FeedProfileSelectionComposable(
                    mockFeedProfiles = createMockFeedProfiles(),
                    //modifier = Modifier.align(Alignment.End)
                )
            }
        }
    }
}