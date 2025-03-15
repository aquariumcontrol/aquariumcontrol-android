package com.example.aquariumcontroller.controller

import android.content.res.Configuration
import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.outlined.Delete
import androidx.compose.material.icons.outlined.Edit
import androidx.compose.material.icons.outlined.Star
import androidx.compose.material3.Button
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.navigation.NavHostController
import com.example.aquariumcontroller.MainViewModel
import com.example.aquariumcontroller.ViewNavigationRoutes
import com.example.aquariumcontroller.ui.theme.AquariumControllerTheme

@Composable
fun ControllerProfileComposable(
    navHostController: NavHostController? = null,
    onEvent: (ControllerEvent) -> Unit,
    controllerProfile: ControllerProfile,
    mainViewModel: MainViewModel? = null,
    mockIsDefaultProfile: Boolean = false
) {
    // This composable displays:
    // - text with the name of the controller profile
    // - text with the address of the controller profile
    // - text with the port number of the controller profile
    // - text with user name of the of the controller profile
    // - text with password of the controller profile
    // - button to edit the controller profile
    // - button to delete the controller profile
    // - star icon if the controller profile is the default profile
    
    // use either stored profile id to check if profile is default profile or use mock data for preview
    val isDefaultProfile: Boolean = if (mainViewModel != null) {
        mainViewModel.getDefaultControllerProfileId() == controllerProfile.id
    } else {
        mockIsDefaultProfile
    }

    // This composable displays a single controller profile.
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
                .clickable {
                    if (mainViewModel != null) {
                        mainViewModel.setFirstLoad()
                        mainViewModel.updateSelectedControllerProfile(controllerProfile)
                        navHostController?.navigate(ViewNavigationRoutes.overView)
                    }
                }
        ) {
            Row(
                modifier = Modifier
                    .fillMaxWidth(),
            ) {
                Column(
                    modifier = Modifier
                        .weight(1f)
                ) {
                    Row(
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(top = 8.dp),
                        horizontalArrangement = Arrangement.Start
                    ) {
                        Column(
                            horizontalAlignment = Alignment.Start) {
                            Text(
                                text = controllerProfile.name,
                                fontSize = 24.sp,
                                style = TextStyle(fontWeight = FontWeight.Bold)
                            )
                        }
                        Column(
                            modifier = Modifier.weight(1f),
                            horizontalAlignment = Alignment.End) {
                            if (isDefaultProfile) {
                                //Spacer(modifier = Modifier.width(10.dp))
                                Icon(
                                    imageVector = Icons.Outlined.Star,
                                    contentDescription = "DefaultProfile",
                                )
                            }
                        }
                    }
                }
            }
            Row(
                modifier = Modifier.fillMaxWidth(),
            ) {
                Text(
                    text = "Address: " + controllerProfile.address,
                    fontSize = 12.sp
                )

            }
            Row(
                modifier = Modifier.fillMaxWidth(),
            ) {
                Column {
                    Text(
                        text = "Port: " + controllerProfile.port,
                        fontSize = 12.sp
                    )
                    Text(
                        text = "ID: ${controllerProfile.id}",
                        fontSize = 12.sp,
                    )
                }
                Column(horizontalAlignment = Alignment.End) {
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.Start
                    ) {
                    }
                    Text(
                        text = "User: " + controllerProfile.user,
                        fontSize = 12.sp,
                    )
                    Text(
                        text = "Password: " + controllerProfile.password,
                        fontSize = 12.sp,
                    )
                }
            }
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.End,
            ) {
                Button(
                    modifier = Modifier.padding(end = 4.dp),
                    onClick = {
                        onEvent(
                            ControllerEvent.SelectControllerProfile(
                                controllerProfile
                            )
                        )
                        navHostController?.navigate(
                            ViewNavigationRoutes.controllerEdit
                        )
                    },
                ) {
                    Icon(
                        imageVector = Icons.Outlined.Edit,
                        contentDescription = "Edit"
                    )
                }
                Button(
                    onClick = {
                        onEvent(
                            ControllerEvent.DeleteControllerProfile(
                                controllerProfile
                            )
                        )
                    }) {
                    Icon(
                        imageVector = Icons.Outlined.Delete,
                        contentDescription = "Delete"
                    )
                }
            }
        }
    }
}

@Preview(showBackground = true, uiMode = Configuration.UI_MODE_NIGHT_YES)
@Preview(showBackground = true, uiMode = Configuration.UI_MODE_NIGHT_NO)
@Composable
fun ControllerProfileComposablePreview() {
    val mockControllerProfile1 = ControllerProfile(
        id = 1,
        name = "Controller 1",
        address = "192.168.1.1",
        port = 80,
        user = "user",
        password = "password",
    )

    AquariumControllerTheme {
        Surface(
            color = MaterialTheme.colorScheme.background
        ) {
            ControllerProfileComposable(
                onEvent = {},
                controllerProfile = mockControllerProfile1,
                mockIsDefaultProfile = true
            )
        }
    }
}