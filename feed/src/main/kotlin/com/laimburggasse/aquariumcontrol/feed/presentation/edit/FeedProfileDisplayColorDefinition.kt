package com.laimburggasse.aquariumcontrol.feed.presentation.edit

import androidx.compose.ui.graphics.Color
import android.graphics.Color as AndroidColor


fun feedProfileTextColorDefinition(signal: FeedProfileSignal): Color {
    return when (signal) {
        FeedProfileSignal.SKIMMER -> Color.Gray
        FeedProfileSignal.MAIN_PUMP1 -> Color.Green
        FeedProfileSignal.MAIN_PUMP2 -> Color.Red
        FeedProfileSignal.AUX_PUMP1 -> Color.Blue
        FeedProfileSignal.AUX_PUMP2 -> Color.Yellow
        FeedProfileSignal.FEEDER -> Color.Magenta
    }
}

// The old color library is still used by MPAndroidChart
fun feedProfileLineColorDefinition(signal: FeedProfileSignal): Int {
    return when (signal) {
        FeedProfileSignal.SKIMMER -> AndroidColor.GRAY
        FeedProfileSignal.MAIN_PUMP1 -> AndroidColor.GREEN
        FeedProfileSignal.MAIN_PUMP2 -> AndroidColor.RED
        FeedProfileSignal.AUX_PUMP1 -> AndroidColor.BLUE
        FeedProfileSignal.AUX_PUMP2 -> AndroidColor.YELLOW
        FeedProfileSignal.FEEDER -> AndroidColor.MAGENTA
    }
}
