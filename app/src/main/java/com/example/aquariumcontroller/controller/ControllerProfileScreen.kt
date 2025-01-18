package com.example.aquariumcontroller.controller

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
import androidx.compose.material3.Button
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.lifecycle.viewmodel.compose.viewModel
import androidx.navigation.NavHostController
import com.example.aquariumcontroller.MainViewModel
import com.example.aquariumcontroller.MainViewModelFactory
import com.example.aquariumcontroller.ViewNavigationRoutes
import com.example.aquariumcontroller.ui.theme.AquariumControllerTheme

@Composable
fun ControllerProfileScreen(
    navHostController: NavHostController?,
    onEvent: (ControllerEvent) -> Unit,
    controllerProfile: ControllerProfile
) {
    val mainViewModel: MainViewModel = viewModel(
        factory = MainViewModelFactory
    )

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
                    mainViewModel.updateSelectedControllerProfile(controllerProfile)
                    navHostController?.navigate(ViewNavigationRoutes.overView)
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
                        Text(
                            text = controllerProfile.name,
                            fontSize = 24.sp,
                            style = TextStyle(fontWeight = FontWeight.Bold)
                        )
                    }
                }
                Column(
                    modifier = Modifier.weight(1f)
                ) {
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
            Text(
                text = "Address: " + controllerProfile.address,
                fontSize = 12.sp
            )
            Text(
                text = "Port: " + controllerProfile.port,
                fontSize = 12.sp
            )
            Row(
                modifier = Modifier.fillMaxWidth(),
            )
            {
                Column(
                    modifier = Modifier.weight(1f)
                ) {
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.Start
                    ) {
                        Text(
                            text = "User: " + controllerProfile.user,
                            fontSize = 12.sp,
                        )
                    }
                }
                Column(
                    modifier = Modifier.weight(1f)
                ) {
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.End
                    ) {
                        Text(
                            text = "Password: " + controllerProfile.password,
                            fontSize = 12.sp,
                        )
                    }
                }

            }
        }
    }
}

@Preview(showBackground = true)
@Composable
fun ControllerProfileScreenPreview() {
    val mockControllerProfile1 = ControllerProfile(
        name = "Controller 1",
        address = "192.168.1.1",
        port = 80,
        user = "user",
        password = "password",
        isDefault = true
    )

    AquariumControllerTheme {
        ControllerProfileScreen(null, {}, mockControllerProfile1)
    }
}