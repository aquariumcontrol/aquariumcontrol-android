package com.laimburggasse.aquariumcontrol.overview.presentation

import android.content.res.Configuration
import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.border
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import com.laimburggasse.aquariumcontrol.R
import com.laimburggasse.aquariumcontrol.common.presentation.theme.AquariumControllerTheme

@Composable
fun OverviewControllerComposable(
    controllerProfileName: String,
    controllerAddress: String,
    controllerPort: Int,
    controllerUsername: String,
) {
    Column(
        modifier = Modifier.border(
            border = BorderStroke(
                width = 1.dp,
                color = MaterialTheme.colorScheme.onSurface
            )
        ).padding(all = 8.dp)
    ) {
        Row(Modifier.fillMaxWidth()) {
            Column(modifier = Modifier.weight(weight = 1f)) {
                Row(modifier = Modifier.align(Alignment.Start)) {
                    Text(text = stringResource(R.string.controller_name))
                }
            }
            Column(modifier = Modifier.weight(weight = 1f)) {
                Row(modifier = Modifier.align(Alignment.End)) {
                    Text(text = controllerProfileName)
                }
            }
        }
        Row(Modifier.fillMaxWidth()) {
            Column(modifier = Modifier.weight(weight = 1f)) {
                Row(modifier = Modifier.align(Alignment.Start)) {
                    Text(text = stringResource(R.string.controller_address))
                }
            }
            Column(modifier = Modifier.weight(weight = 1f)) {
                Row(modifier = Modifier.align(Alignment.End)) {
                    Text(text = controllerAddress)
                }
            }
        }
        Row(Modifier.fillMaxWidth()) {
            Column(modifier = Modifier.weight(weight = 1f)) {
                Row(modifier = Modifier.align(Alignment.Start)) {
                    Text(text = stringResource(R.string.controller_port))
                }
            }
            Column(modifier = Modifier.weight(weight = 1f)) {
                Row(modifier = Modifier.align(Alignment.End)) {
                    Text(text = controllerPort.toString())
                }
            }
        }
        Row(Modifier.fillMaxWidth()) {
            Column(modifier = Modifier.weight(weight = 1f)) {
                Row(modifier = Modifier.align(Alignment.Start)) {
                    Text(text = stringResource(R.string.user_name))
                }
            }
            Column(modifier = Modifier.weight(weight = 1f)) {
                Row(modifier = Modifier.align(Alignment.End)) {
                    Text(text = controllerUsername)
                }
            }
        }
    }
}

@Preview(showBackground = true, uiMode = Configuration.UI_MODE_NIGHT_YES)
@Preview(showBackground = true, uiMode = Configuration.UI_MODE_NIGHT_NO)
@Composable
internal fun OverviewControllerComposablePreview() {
    AquariumControllerTheme {
        Surface {
            OverviewControllerComposable(
                controllerProfileName = "mockProfileName",
                controllerAddress = "mock.controller.address",
                controllerPort = 80,
                controllerUsername = "mock_user",
            )
        }
    }
}