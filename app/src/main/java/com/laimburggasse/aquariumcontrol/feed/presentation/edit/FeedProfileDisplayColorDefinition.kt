package com.laimburggasse.aquariumcontrol.feed.presentation.edit

import androidx.compose.ui.graphics.Color

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

fun feedProfileLineColorDefinition(signal: FeedProfileSignal): Int {
    return when (signal) {
        FeedProfileSignal.SKIMMER -> android.graphics.Color.GRAY
        FeedProfileSignal.MAIN_PUMP1 -> android.graphics.Color.GREEN
        FeedProfileSignal.MAIN_PUMP2 -> android.graphics.Color.RED
        FeedProfileSignal.AUX_PUMP1 -> android.graphics.Color.BLUE
        FeedProfileSignal.AUX_PUMP2 -> android.graphics.Color.YELLOW
        FeedProfileSignal.FEEDER -> android.graphics.Color.MAGENTA
    }
}
