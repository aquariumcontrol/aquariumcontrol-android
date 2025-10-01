package com.laimburggasse.aquariumcontrol.balling

import android.content.res.Configuration
import android.util.Log
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
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.navigation.NavHostController
import com.laimburggasse.aquariumcontrol.MainViewModel
import com.laimburggasse.aquariumcontrol.ViewNavigationRoutes
import com.laimburggasse.aquariumcontrol.ui.theme.AquariumControllerTheme

@Composable
fun BallingProfileSummaryComposable(
    navHostController: NavHostController? = null,
    mainViewModel: MainViewModel? = null,
    ballingProfile: BallingProfile
) {
    // This composable displays one Balling dosing profile.
    // When the user clicks on the profile, the BallingProfileEditScreen is displayed.
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
            .clickable {
                Log.d("BallingProfileSummaryScreen", "Clicked on {${ballingProfile.label}}")
                mainViewModel?.updateSelectedBallingProfile(ballingProfile)
                navHostController?.navigate(ViewNavigationRoutes.ballingEdit)
            }
    ) {
        Column(
            modifier = Modifier
                .weight(1f)
        ) {
            Text(
                text = "Balling dosing profile:",
                fontSize = 12.sp
            )
            Text(
                text = ballingProfile.label,
                fontSize = 12.sp
            )
        }
        Column(
            modifier = Modifier.weight(1f)
        ) {
            Text(
                text = "Pump ID: ${ballingProfile.pumpId}",
                fontSize = 12.sp
            )
            Text(
                text = "Dosing volume: ${"%.2f".format(ballingProfile.volume)} ml",
                fontSize = 12.sp
            )
        }
    }
}

val mockBallingProfile = BallingProfile(
    pumpId = 1,
    volume = 1.5,
    label = "MockDosingUnit 1",
)

@Preview(showBackground = true, uiMode = Configuration.UI_MODE_NIGHT_YES)
@Preview(showBackground = true, uiMode = Configuration.UI_MODE_NIGHT_NO)
@Composable
internal fun BallingProfileSummaryPreview() {
    AquariumControllerTheme {
        Surface {
            BallingProfileSummaryComposable(
                navHostController = null,
                mainViewModel = null,
                ballingProfile = mockBallingProfile
            )
        }
    }
}