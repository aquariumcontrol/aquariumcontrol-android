package com.example.aquariumcontroller.heating

import com.example.aquariumcontroller.heating.HeatingStatsSignal.*

fun heatingStatsTextColorDefinition(signal: HeatingStatsSignal): androidx.compose.ui.graphics.Color {
    return when (signal) {
        AMBIENT_TEMPERATURE -> androidx.compose.ui.graphics.Color.Green
        WATER_TEMPERATURE -> androidx.compose.ui.graphics.Color.Blue
        ENERGY -> androidx.compose.ui.graphics.Color.Red
    }
}

fun heatingStatsLineColorDefinition(signal: HeatingStatsSignal): Int {
    return when (signal) {
        AMBIENT_TEMPERATURE -> android.graphics.Color.GREEN
        WATER_TEMPERATURE -> android.graphics.Color.BLUE
        ENERGY -> android.graphics.Color.RED
    }
}