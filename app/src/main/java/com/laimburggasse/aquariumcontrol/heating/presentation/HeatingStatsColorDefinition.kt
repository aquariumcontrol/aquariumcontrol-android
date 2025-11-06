package com.laimburggasse.aquariumcontrol.heating.presentation

import androidx.compose.ui.graphics.Color
import com.laimburggasse.aquariumcontrol.heating.presentation.HeatingStatsSignal
import com.laimburggasse.aquariumcontrol.heating.presentation.HeatingStatsSignal.*

fun heatingStatsTextColorDefinition(signal: HeatingStatsSignal): Color {
    return when (signal) {
        AMBIENT_TEMPERATURE -> Color.Green
        WATER_TEMPERATURE -> Color.Blue
        ENERGY -> Color.Red
    }
}

fun heatingStatsLineColorDefinition(signal: HeatingStatsSignal): Int {
    return when (signal) {
        AMBIENT_TEMPERATURE -> android.graphics.Color.GREEN
        WATER_TEMPERATURE -> android.graphics.Color.BLUE
        ENERGY -> android.graphics.Color.RED
    }
}