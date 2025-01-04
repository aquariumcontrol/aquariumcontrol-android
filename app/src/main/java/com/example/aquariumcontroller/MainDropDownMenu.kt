package com.example.aquariumcontroller

import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.wrapContentSize
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.outlined.RotateRight
import androidx.compose.material.icons.filled.MoreVert
import androidx.compose.material.icons.outlined.AcUnit
import androidx.compose.material.icons.outlined.DeviceThermostat
import androidx.compose.material.icons.outlined.Info
import androidx.compose.material.icons.outlined.LineAxis
import androidx.compose.material.icons.outlined.Schedule
import androidx.compose.material.icons.outlined.Settings
import androidx.compose.material.icons.outlined.WaterDrop
import androidx.compose.material3.DropdownMenu
import androidx.compose.material3.DropdownMenuItem
import androidx.compose.material3.HorizontalDivider
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.navigation.NavHostController
import com.example.aquariumcontroller.ui.theme.AquariumControllerTheme

@Composable
fun MainDropdownMenu(
    navHostController: NavHostController?,
    controllerSelected: Boolean,
    navigationRouteSource: String
) {
    var expanded by remember { mutableStateOf(false) }
    Box(
        modifier = Modifier
            .padding(16.dp)
            .fillMaxWidth()
            .wrapContentSize(Alignment.TopStart)
    ) {
        IconButton(
            onClick = { expanded = !expanded },
        ) {
            Icon(imageVector = Icons.Default.MoreVert, contentDescription = "More options")
        }
        DropdownMenu(
            expanded = expanded,
            onDismissRequest = { expanded = false },
        ) {
            DropdownMenuItem(
                text = { Text("Display Data") },
                onClick = {
                    navHostController?.navigate(ViewNavigationRoutes.timeData)
                },
                leadingIcon = {
                    Icon(
                        imageVector = Icons.Outlined.LineAxis,
                        contentDescription = "Settings"
                    )
                },
                enabled = controllerSelected && navigationRouteSource != ViewNavigationRoutes.timeData
            )
            DropdownMenuItem(
                text = { Text("Fresh water refill") },
                onClick = {
                    navHostController?.navigate(ViewNavigationRoutes.refill)
                },
                leadingIcon = {
                    Icon(
                        imageVector = Icons.Outlined.WaterDrop,
                        contentDescription = "Settings"
                    )
                },
                enabled = controllerSelected && navigationRouteSource != ViewNavigationRoutes.refill
            )
            DropdownMenuItem(
                text = { Text("Feed") },
                onClick = {
                    navHostController?.navigate(ViewNavigationRoutes.feed)
                },
                leadingIcon = {
                    Icon(
                        imageVector = Icons.AutoMirrored.Outlined.RotateRight,
                        contentDescription = "Settings"
                    )
                },
                enabled = controllerSelected && navigationRouteSource != ViewNavigationRoutes.feed
            )
            DropdownMenuItem(
                text = { Text("Heating") },
                onClick = {
                    navHostController?.navigate(ViewNavigationRoutes.heating)
                },
                leadingIcon = {
                    Icon(
                        imageVector = Icons.Outlined.DeviceThermostat,
                        contentDescription = "Settings"
                    )
                },
                enabled = controllerSelected && navigationRouteSource != ViewNavigationRoutes.heating
            )
            DropdownMenuItem(
                text = { Text("Ventilation") },
                onClick = {
                    navHostController?.navigate(ViewNavigationRoutes.ventilation)
                },
                leadingIcon = {
                    Icon(
                        imageVector = Icons.Outlined.AcUnit,
                        contentDescription = "Settings"
                    )
                },
                enabled = controllerSelected && navigationRouteSource != ViewNavigationRoutes.ventilation

            )
            DropdownMenuItem(
                text = { Text("Schedule") },
                onClick = {
                    navHostController?.navigate(ViewNavigationRoutes.schedule)
                },
                leadingIcon = {
                    Icon(
                        imageVector = Icons.Outlined.Schedule,
                        contentDescription = "Settings"
                    )
                },
                enabled = controllerSelected && navigationRouteSource != ViewNavigationRoutes.schedule
            )
            HorizontalDivider()
            DropdownMenuItem(
                text = { Text("Select Controller") },
                onClick = {
                    navHostController?.navigate(ViewNavigationRoutes.controllerSelection)
                },
                leadingIcon = {
                    Icon(
                        imageVector = Icons.Outlined.Settings,
                        contentDescription = "Settings"
                    )
                },
                enabled = navigationRouteSource != ViewNavigationRoutes.controllerSelection
            )
            DropdownMenuItem(
                text = { Text("Info") },
                onClick = {
                    navHostController?.navigate(ViewNavigationRoutes.info)
                },
                leadingIcon = {
                    Icon(
                        imageVector = Icons.Outlined.Info,
                        contentDescription = "Account Balance"
                    )
                },
                enabled = navigationRouteSource != ViewNavigationRoutes.info
            )
        }
    }
}

@Preview(showBackground = true)
@Composable
fun MainDropdownMenuPreview() {
    AquariumControllerTheme {
        MainDropdownMenu(null, true, ViewNavigationRoutes.controllerSelection)
    }
}
