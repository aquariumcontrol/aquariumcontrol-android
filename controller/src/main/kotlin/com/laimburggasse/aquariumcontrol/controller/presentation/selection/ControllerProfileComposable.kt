/*
 * Copyright 2025 Uwe Martin
 *
 * Permission is hereby granted, free of charge, to any person obtaining a copy
 * of this software and associated documentation files (the "Software"), to deal
 * in the Software without restriction, including without limitation the rights
 * to use, copy, modify, merge, publish, distribute, sublicense, and/or sell
 * copies of the Software, and to permit persons to whom the Software is
 * furnished to do so, subject to the following conditions:
 *
 * The above copyright notice and this permission notice shall be included in all
 * copies or substantial portions of the Software.
 *
 * THE SOFTWARE IS PROVIDED "AS IS", WITHOUT WARRANTY OF ANY KIND, EXPRESS OR
 * IMPLIED, INCLUDING BUT NOT LIMITED TO THE WARRANTIES OF MERCHANTABILITY,
 * FITNESS FOR A PARTICULAR PURPOSE AND NONINFRINGEMENT. IN NO EVENT SHALL THE
 * AUTHORS OR COPYRIGHT HOLDERS BE LIABLE FOR ANY CLAIM, DAMAGES OR OTHER
 * LIABILITY, WHETHER IN AN ACTION OF CONTRACT, TORT OR OTHERWISE, ARISING FROM,
 * OUT OF OR IN CONNECTION WITH THE SOFTWARE OR THE USE OR OTHER DEALINGS IN THE
 * SOFTWARE.
 */

package com.laimburggasse.aquariumcontrol.controller.presentation.selection

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
import com.laimburggasse.aquariumcontrol.controller.R
import com.laimburggasse.aquariumcontrol.controller.presentation.navigation.ControllerNavigationEvent
import com.laimburggasse.aquariumcontrol.common.domain.ControllerProfile
import com.laimburggasse.aquariumcontrol.common.presentation.theme.AquariumControllerTheme
import com.laimburggasse.aquariumcontrol.controller.preview.ControllerPreviewDataImpl.mockControllerProfile1

@Composable
fun ControllerProfileComposable(
    onEvent: (ControllerSelectionUiEvent) -> Unit,
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
            .border(
                border = BorderStroke(
                    width = 1.dp,
                    color = MaterialTheme.colorScheme.onSurface
                )
            )
            .padding(all = 8.dp)
    ) {
        Column(
            modifier = Modifier
                .weight(weight = 1f)
                .clickable {
                    onEvent(ControllerSelectionUiEvent.SelectControllerProfile(controllerProfile))
                    onNavigateFromController(
                        ControllerNavigationEvent.ToOverview,
                        controllerProfile.id
                    )
                }
        ) {
            Row(
                modifier = Modifier.fillMaxWidth(),
            ) {
                Column(modifier = Modifier.weight(weight = 1f)) {
                    Row(
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(top = 8.dp),
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
                                    contentDescription = stringResource(id = R.string.defaultprofile),
                                )
                            }
                        }
                    }
                }
            }
            Row(modifier = Modifier.fillMaxWidth()) {
                Text(
                    text = stringResource(id = R.string.address) + controllerProfile.address,
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
                        onEvent(ControllerSelectionUiEvent.SelectControllerProfile(controllerProfile))
                        onNavigateFromController(
                            ControllerNavigationEvent.ToControllerEdit,
                            controllerProfile.id
                        )
                    },
                ) {
                    Icon(
                        imageVector = Icons.Outlined.Edit,
                        contentDescription = stringResource(id = R.string.edit)
                    )
                }
                Button(
                    onClick = {
                        onEvent(ControllerSelectionUiEvent.DeleteControllerSelectionProfile(controllerProfile))
                    }) {
                    Icon(
                        imageVector = Icons.Outlined.Delete,
                        contentDescription = stringResource(id = R.string.delete)
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