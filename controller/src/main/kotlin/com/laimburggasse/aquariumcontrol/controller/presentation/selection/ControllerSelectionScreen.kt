package com.laimburggasse.aquariumcontrol.controller.presentation.selection

import android.content.res.Configuration
import androidx.compose.foundation.isSystemInDarkTheme
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.wrapContentSize
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Add
import androidx.compose.material3.FloatingActionButton
import androidx.compose.material3.Icon
import androidx.compose.material3.LinearProgressIndicator
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.State
import androidx.compose.runtime.mutableFloatStateOf
import androidx.compose.runtime.mutableLongStateOf
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalInspectionMode
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import com.laimburggasse.aquariumcontrol.common.presentation.MainScaffold
import com.laimburggasse.aquariumcontrol.controller.R
import com.laimburggasse.aquariumcontrol.common.presentation.ViewNavigationRoutes
import com.laimburggasse.aquariumcontrol.common.domain.ControllerProfile
import com.laimburggasse.aquariumcontrol.controller.presentation.navigation.ControllerNavigationEvent
import com.laimburggasse.aquariumcontrol.common.presentation.ScreenHeadlineComposable
import com.laimburggasse.aquariumcontrol.controller.controller.ControllerMockData.mockControllerProfileList
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext

@Composable
fun ControllerSelectionScreen(
    controllerProfileList: List<ControllerProfile>,
    onEvent: (ControllerSelectionUiEvent) -> Unit,
    defaultProfileId: State<Long?>,
    onNavigateFromController: (ControllerNavigationEvent, Long?) -> Unit,
    canStartWaitForNavigation: State<Boolean>,
    progressToDefaultNavigation: State<Float>,
) {
    // This screen displays:
    // - headline
    // - list of controller profiles
    // - floating action button to add new controller profile

    val isWaitFinished: Boolean = progressToDefaultNavigation.value >= 1.0f

    // Countdown and navigation logic
    LaunchedEffect(key1 = isWaitFinished) {
        // Time has passed without user interaction, navigate to the default profile
        if (isWaitFinished && defaultProfileId.value != null) {
            withContext(context = Dispatchers.Main) { // Switch to the main thread
                onNavigateFromController(ControllerNavigationEvent.ToOverview, defaultProfileId.value)
            }
        }
    }

    Column(horizontalAlignment = Alignment.CenterHorizontally) {
        ScreenHeadlineComposable(text = stringResource(id = R.string.list_of_controllers))

        if (canStartWaitForNavigation.value && defaultProfileId.value != null) {
            Text(text = stringResource(R.string.loading_default_profile))
            LinearProgressIndicator(
                progress = { progressToDefaultNavigation.value },
                modifier = Modifier.fillMaxWidth().padding(16.dp),
            )
        }

        Scaffold(
            floatingActionButton = {
                FloatingActionButton(
                    onClick = {
                        onNavigateFromController(ControllerNavigationEvent.ToControllerAdd, null)
                    },
                    modifier = Modifier.padding(all = 16.dp)
                ) {
                    Icon(
                        imageVector = Icons.Filled.Add,
                        contentDescription = stringResource(R.string.create_new_profile)
                    )
                }
            }
        ) { innerPadding ->
            Column(
                modifier = Modifier.fillMaxSize().padding(paddingValues = innerPadding),
                horizontalAlignment = Alignment.CenterHorizontally
            ) {
                val modifier: Modifier = Modifier.padding(all = 16.dp).wrapContentSize()

                if (LocalInspectionMode.current) {
                    Column(modifier) {
                        val items: List<ControllerProfile> = controllerProfileList
                        items.forEach { item ->
                            ControllerProfileComposable(
                                onEvent = {},
                                controllerProfile = item,
                                onNavigateFromController = onNavigateFromController
                            )
                        }
                    }
                }
                else {
                    LazyColumn(modifier) {
                        items(items =controllerProfileList) { controllerProfile ->
                            ControllerProfileComposable(
                                onEvent = onEvent,
                                controllerProfile = controllerProfile,
                                onNavigateFromController = onNavigateFromController
                            )
                        }
                    }
                }
            }
        }
    }
}

@Preview(showBackground = true, uiMode = Configuration.UI_MODE_NIGHT_YES)
@Preview(showBackground = true, uiMode = Configuration.UI_MODE_NIGHT_NO)
@Preview(
    showBackground = true,
    uiMode = Configuration.UI_MODE_NIGHT_YES,
    device = "spec:width=600dp,height=600dp,orientation=landscape"
)
@Preview(
    showBackground = true,
    uiMode = Configuration.UI_MODE_NIGHT_NO,
    device = "spec:width=600dp,height=600dp,orientation=landscape"
)
@Composable
internal fun ControllerSelectionScreenPreview() {
    MainScaffold(
        isControllerLoaded = true,
        navigationRouteSource = ViewNavigationRoutes.controllerSelection,
        screenContent = {
            ControllerSelectionScreen(
                controllerProfileList = mockControllerProfileList,
                onEvent = {},
                progressToDefaultNavigation = remember { mutableFloatStateOf(value = 0.3f) },
                onNavigateFromController = { event, profileId -> },
                defaultProfileId = remember { mutableLongStateOf(value = 1L) },
                canStartWaitForNavigation = remember { mutableStateOf(value = true) },
            )
         },
        onNavigate = {},
        darkTheme = isSystemInDarkTheme()
    )
}

