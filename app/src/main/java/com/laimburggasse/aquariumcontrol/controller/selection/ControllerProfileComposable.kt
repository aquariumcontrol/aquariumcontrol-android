package com.laimburggasse.aquariumcontrol.controller.selection

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
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.laimburggasse.aquariumcontrol.R
import com.laimburggasse.aquariumcontrol.controller.common.ControllerNavigationEvent
import com.laimburggasse.aquariumcontrol.controller.common.ControllerProfile
import com.laimburggasse.aquariumcontrol.ui.theme.AquariumControllerTheme

@Composable
fun ControllerProfileComposable(
    onEvent: (ControllerSelectionEvent) -> Unit,
    controllerProfile: ControllerProfile,
    isDefaultProfile: Boolean = false,
    onNavigateFromController: (ControllerNavigationEvent, Long) -> Unit
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
    
    // This composable displays a single controller profile.
    Row(
        modifier = Modifier
            .fillMaxWidth()
            .border(border = BorderStroke(width = 1.dp,color = MaterialTheme.colorScheme.onSurface))
            .padding(all = 8.dp)
    ) {
        Column(
            modifier = Modifier.weight(weight = 1f).clickable {
                onEvent(ControllerSelectionEvent.SelectControllerProfile(controllerProfile))
                onNavigateFromController(ControllerNavigationEvent.ToOverview, controllerProfile.id)
            }
        ) {
            Row(
                modifier = Modifier.fillMaxWidth(),
            ) {
                Column(modifier = Modifier.weight(weight = 1f)) {
                    Row(
                        modifier = Modifier.fillMaxWidth().padding(top = 8.dp),
                        horizontalArrangement = Arrangement.Start
                    ) {
                        Column(horizontalAlignment = Alignment.Start) {
                            Text(
                                text = controllerProfile.name,
                                fontSize = 24.sp,
                                style = TextStyle(fontWeight = FontWeight.Bold)
                            )
                        }
                        Column(
                            modifier = Modifier.weight(weight = 1f),
                            horizontalAlignment = Alignment.End) {
                            if (isDefaultProfile) {
                                Icon(
                                    imageVector = Icons.Outlined.Star,
                                    contentDescription = "DefaultProfile",
                                )
                            }
                        }
                    }
                }
            }
            Row(modifier = Modifier.fillMaxWidth()) {
                Text(
                    text = "Address: " + controllerProfile.address,
                    fontSize = 12.sp
                )

            }
            Row(modifier = Modifier.fillMaxWidth()) {
                Column {
                    Text(
                        text = stringResource(R.string.port) + controllerProfile.port,
                        fontSize = 12.sp
                    )
                    Text(
                        text = stringResource(R.string.id) + "${controllerProfile.id}",
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
                        text = stringResource(R.string.user) + controllerProfile.user,
                        fontSize = 12.sp,
                    )
                    Text(
                        text = stringResource(R.string.password_label) + controllerProfile.password,
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
                        onEvent(ControllerSelectionEvent.SelectControllerProfile(controllerProfile))
                        onNavigateFromController(
                            ControllerNavigationEvent.ToControllerEdit,
                            controllerProfile.id
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
                        onEvent(ControllerSelectionEvent.DeleteControllerSelectionProfile(controllerProfile))
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
internal fun ControllerProfileComposablePreview() {
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
                isDefaultProfile = true,
                onNavigateFromController = { event, profileId -> }
            )
        }
    }
}