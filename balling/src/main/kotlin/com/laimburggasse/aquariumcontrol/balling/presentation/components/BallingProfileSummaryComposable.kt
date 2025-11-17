package com.laimburggasse.aquariumcontrol.balling.presentation.components

import android.content.res.Configuration
import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.laimburggasse.aquariumcontrol.balling.R
import com.laimburggasse.aquariumcontrol.balling.presentation.navigation.BallingNavigationEvent
import com.laimburggasse.aquariumcontrol.balling.domain.model.BallingProfile
import com.laimburggasse.aquariumcontrol.balling.testing.BallingMockData
import com.laimburggasse.aquariumcontrol.common.presentation.theme.AquariumControllerTheme

@Composable
fun BallingProfileSummaryComposable(
    ballingProfile: BallingProfile,
    onNavigateFromBalling: (BallingNavigationEvent, Int?) -> Unit
) {
    // This composable displays one Balling dosing profile.
    // When the user clicks on the profile, the BallingProfileEditScreen is displayed.
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
            .clickable {
                onNavigateFromBalling(BallingNavigationEvent.ToBallingEdit, ballingProfile.pumpId)
            }
    ) {
        Column(
            modifier = Modifier
                .weight(weight = 1f)
        ) {
            Text(
                text = stringResource(id = R.string.balling_dosing_profile),
                fontSize = 12.sp
            )
            Text(
                text = ballingProfile.label,
                fontSize = 12.sp
            )
        }
        Column(
            modifier = Modifier.weight(weight = 1f)
        ) {
            Text(
                text = stringResource(id = R.string.pump_id) + ballingProfile.pumpId,
                fontSize = 12.sp
            )
            Text(
                text = stringResource(id = R.string.dosing_volume) +
                        "%.2f".format(ballingProfile.volume) +
                        stringResource(id = R.string.ml),
                fontSize = 12.sp
            )
        }
    }
}



@Preview(showBackground = true, uiMode = Configuration.UI_MODE_NIGHT_YES)
@Preview(showBackground = true, uiMode = Configuration.UI_MODE_NIGHT_NO)
@Composable
internal fun BallingProfileSummaryPreview() {
    AquariumControllerTheme {
        Surface {
            BallingProfileSummaryComposable(
                ballingProfile = BallingMockData.mockBallingProfile,
                onNavigateFromBalling = { event, pumpId -> }
            )
        }
    }
}