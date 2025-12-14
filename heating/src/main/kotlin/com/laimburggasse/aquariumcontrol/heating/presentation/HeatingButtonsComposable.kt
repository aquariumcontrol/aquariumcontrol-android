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

package com.laimburggasse.aquariumcontrol.heating.presentation

import android.content.Context
import android.content.res.Configuration
import android.widget.Toast
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.width
import androidx.compose.material3.Button
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.State
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import com.laimburggasse.aquariumcontrol.heating.R
import com.laimburggasse.aquariumcontrol.common.presentation.theme.AquariumControllerTheme

@Composable
fun HeatingButtonsComposable(
    onUpdateClicked: () -> Unit,
    onResetClicked: () -> Unit,
    heatingSetValsValid: State<Boolean?>,
) {
    // This composable displays two buttons for the heating control.
    // Arrangement of the buttons follows the device's orientation.
    
    val context: Context = LocalContext.current // context for Toasts

    Button(
        onClick = {
            onUpdateClicked()
            // Show the Toast after the API call
            Toast.makeText(
                context,
                context.getString(R.string.sent_request_to_controller_for_updating_heating_set_values),
                Toast.LENGTH_SHORT
            ).show()
        },
        enabled = heatingSetValsValid.value == true
    ) {
        Text(text = stringResource(id = R.string.update_heating_controller_values))
    }
    Spacer(modifier = Modifier.width(width = 10.dp))
    Button(
        onClick = { onResetClicked() }
    ) {
        Text(text = stringResource(id = R.string.reset_heating_controller_values))
    }

}

@Preview(showBackground = true, uiMode = Configuration.UI_MODE_NIGHT_YES)
@Composable
internal fun HeatingButtonsComposablePortraitDarkPreview() {
    AquariumControllerTheme(darkTheme = true) {
        Surface {
            Column(horizontalAlignment = Alignment.CenterHorizontally) {
                HeatingButtonsComposable(
                    onUpdateClicked = { },
                    onResetClicked = { },
                    heatingSetValsValid = remember { mutableStateOf(value = true) }
                )
            }
        }
    }
}

@Preview(showBackground = true, uiMode = Configuration.UI_MODE_NIGHT_NO)
@Composable
internal fun HeatingButtonsComposablePortraitLightPreview() {
    AquariumControllerTheme(darkTheme = false) {
        Surface {
            Column(horizontalAlignment = Alignment.CenterHorizontally) {
                HeatingButtonsComposable(
                    onUpdateClicked = { },
                    onResetClicked = { },
                    heatingSetValsValid = remember { mutableStateOf(value = true) }
                )
            }
        }
    }
}

@Preview(
    showBackground = true,
    uiMode = Configuration.UI_MODE_NIGHT_YES,
    device = "spec:width=800dp,height=400dp,orientation=landscape"
)
@Composable
internal fun HeatingButtonsComposableLandscapeDarkPreview() {
    AquariumControllerTheme(darkTheme = true) {
        Surface {
            Row {
                HeatingButtonsComposable(
                    onUpdateClicked = { },
                    onResetClicked = { },
                    heatingSetValsValid = remember { mutableStateOf(value = true) }
                )
            }
        }
    }
}
@Preview(
    showBackground = true,
    uiMode = Configuration.UI_MODE_NIGHT_NO,
    device = "spec:width=800dp,height=400dp,orientation=landscape"
)
@Composable
internal fun HeatingButtonsComposableLandscapeLightPreview() {
    AquariumControllerTheme(darkTheme = false) {
        Surface {
            Row {
                HeatingButtonsComposable(
                    onUpdateClicked = { },
                    onResetClicked = { },
                    heatingSetValsValid = remember { mutableStateOf(value = true) }
                )
            }
        }
    }
}