package com.laimburggasse.aquariumcontrol.feed.presentation.add

import android.content.Context
import android.content.res.Configuration
import android.widget.Toast
import androidx.compose.foundation.isSystemInDarkTheme
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.material3.Button
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.laimburggasse.aquariumcontrol.MainScaffold
import com.laimburggasse.aquariumcontrol.R
import com.laimburggasse.aquariumcontrol.ViewNavigationRoutes
import com.laimburggasse.aquariumcontrol.feed.presentation.navigation.FeedNavigationEvent
import com.laimburggasse.aquariumcontrol.lib_composables.presentation.ScreenHeadlineComposable

@Composable
fun FeedProfileAddScreen(
    feedProfileName: String,
    feedProfileNameExists: Boolean? = false,
    feedProfileAddUiState: FeedProfileAddUiState,
    onNavigateFromFeed: (FeedNavigationEvent, Int?, Boolean?, Long?) -> Unit,
    onFeedProfileAddEvent: (FeedProfileAddUiEvent) -> Unit,
) {
    // This screen displays
    // - a graphic visualizing the feed profile
    // - button for triggering creation of feed profile
    // - a hierarchical menu for editing the feed profile

    val context: Context = LocalContext.current // context Toasts

    LaunchedEffect(key1 = feedProfileAddUiState) {
        when (val state: FeedProfileAddUiState = feedProfileAddUiState) {
            is FeedProfileAddUiState.Success -> {
                Toast.makeText(
                    context,
                    context.getString(R.string.successfully_added_feed_profile),
                    Toast.LENGTH_SHORT
                ).show()
                onNavigateFromFeed(FeedNavigationEvent.ToFeedMain, null, null, null)
                onFeedProfileAddEvent(FeedProfileAddUiEvent.HandleState)
            }
            is FeedProfileAddUiState.Error -> {
                Toast.makeText(context, state.message, Toast.LENGTH_SHORT).show()
                onFeedProfileAddEvent(FeedProfileAddUiEvent.HandleState)
            }
            else -> {}
        }
    }

    Column(
        horizontalAlignment = Alignment.CenterHorizontally,
        modifier = Modifier
            .fillMaxSize()
            .verticalScroll(state = rememberScrollState())
    ) {
        ScreenHeadlineComposable(text = stringResource(id = R.string.feed_profile_add))

        when (feedProfileAddUiState) {
            is FeedProfileAddUiState.Idle -> {
                FeedProfileAddNameEditableOutlinedFieldComposable(
                    onFeedProfileNameChange = {
                        onFeedProfileAddEvent(FeedProfileAddUiEvent.SetFeedProfileName(name = it))
                    },
                    modifier = Modifier.padding(all = 16.dp)
                )

                Button(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(horizontal = 8.dp),
                    enabled = feedProfileName.isNotEmpty() && !feedProfileNameExists!!,
                    onClick = { onFeedProfileAddEvent(FeedProfileAddUiEvent.Add) },
                ) {
                    Text(
                        text = stringResource(R.string.add_feed_profile),
                        fontSize = 16.sp
                    )
                }

                if (feedProfileNameExists == true) {
                    Text(text = "Feed profile name already exists!")
                }
            }
            else -> {
                CircularProgressIndicator(
                    modifier = Modifier.size(size = 150.dp),
                    color = MaterialTheme.colorScheme.primary
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
internal fun FeedProfileAddScreenIdlePreview() {
    MainScaffold(
        isControllerLoaded = true,
        navigationRouteSource = ViewNavigationRoutes.feedAddProfile,
        screenContent = {
            FeedProfileAddScreen(
                feedProfileName = "test",
                feedProfileNameExists = true,
                feedProfileAddUiState = FeedProfileAddUiState.Idle,
                onNavigateFromFeed = {event, id, isNew, time ->},
                onFeedProfileAddEvent = {event ->}
            )
        },
        onNavigate = {},
        darkTheme = isSystemInDarkTheme()
    )
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
internal fun FeedProfileAddScreenLoadingPreview() {
    MainScaffold(
        isControllerLoaded = true,
        navigationRouteSource = ViewNavigationRoutes.feedAddProfile,
        screenContent = {
            FeedProfileAddScreen(
                feedProfileName = "test",
                feedProfileAddUiState = FeedProfileAddUiState.Loading,
                onNavigateFromFeed = {event, id, isNew, time ->},
                onFeedProfileAddEvent = {event ->}
            )
        },
        onNavigate = {},
        darkTheme = isSystemInDarkTheme()
    )
}