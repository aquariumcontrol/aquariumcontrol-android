package com.laimburggasse.aquariumcontrol.heating.presentation

import android.graphics.Color as AndroidColor
import androidx.compose.ui.graphics.Color
import com.laimburggasse.aquariumcontrol.heating.presentation.HeatingStatsSignal.*

fun heatingStatsTextColorDefinition(signal: HeatingStatsSignal): Color {
    return when (signal) {
        AMBIENT_TEMPERATURE -> Color.Green
        WATER_TEMPERATURE -> Color.Blue
        ENERGY -> Color.Red
    }
}

// MPAndroidChart still uses the old color library.
fun heatingStatsLineColorDefinition(signal: HeatingStatsSignal): Int {
    return when (signal) {
        AMBIENT_TEMPERATURE -> AndroidColor.GREEN
        WATER_TEMPERATURE -> AndroidColor.BLUE
        ENERGY -> AndroidColor.RED
    }
}