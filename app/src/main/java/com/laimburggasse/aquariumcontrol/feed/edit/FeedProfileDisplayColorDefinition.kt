package com.laimburggasse.aquariumcontrol.feed.edit

fun feedProfileTextColorDefinition(signal: FeedProfileSignal): androidx.compose.ui.graphics.Color {
    return when (signal) {
        FeedProfileSignal.SKIMMER -> androidx.compose.ui.graphics.Color.Gray
        FeedProfileSignal.MAIN_PUMP1 -> androidx.compose.ui.graphics.Color.Green
        FeedProfileSignal.MAIN_PUMP2 -> androidx.compose.ui.graphics.Color.Red
        FeedProfileSignal.AUX_PUMP1 -> androidx.compose.ui.graphics.Color.Blue
        FeedProfileSignal.AUX_PUMP2 -> androidx.compose.ui.graphics.Color.Yellow
        FeedProfileSignal.FEEDER -> androidx.compose.ui.graphics.Color.Magenta
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
