package com.example.aquariumcontroller.balling

import android.os.Build
import android.util.Log
import androidx.annotation.RequiresApi
import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.navigation.NavHostController
import com.example.aquariumcontroller.MainViewModel
import com.example.aquariumcontroller.ViewNavigationRoutes
import com.example.aquariumcontroller.ui.theme.AquariumControllerTheme

@Composable
fun BallingProfileSummaryScreen(
    navHostController: NavHostController? = null,
    mainViewModel: MainViewModel? = null,
    ballingProfile: BallingProfile
) {
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
                Log.d("BallingProfileScreen", "Clicked on {${ballingProfile.label}}")
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

@RequiresApi(Build.VERSION_CODES.O)
@Preview(showBackground = true)
@Composable
fun BallingProfileSummaryPreviewScreen() {
    val mockBallingProfile = BallingProfile(
        pumpId = 1,
        volume = 1.5,
        label = "MockDosingUnit 1",
    )

    AquariumControllerTheme {
        BallingProfileSummaryScreen(
            navHostController = null,
            mainViewModel = null,
            ballingProfile = mockBallingProfile
        )
    }
}