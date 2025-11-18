/*
 * Copyright 2025 Uwe Martin
 *
 * Permission is hereby granted, free of charge, to any person obtaining a copy
 * of this software and associated documentation files (the "Software"), to deal
 * in the Software without restriction, including without limitation the rights
 * to use, copy, modify, merge, publish, distribute, sublicense, and/or sell
 * copies of the Software, and to permit persons to whom the Software is
 * furnished to do so, subject to the following conditions:
 *
 * The above copyright notice and this permission notice shall be included in all
 * copies or substantial portions of the Software.
 *
 * THE SOFTWARE IS PROVIDED "AS IS", WITHOUT WARRANTY OF ANY KIND, EXPRESS OR
 * IMPLIED, INCLUDING BUT NOT LIMITED TO THE WARRANTIES OF MERCHANTABILITY,
 * FITNESS FOR A PARTICULAR PURPOSE AND NONINFRINGEMENT. IN NO EVENT SHALL THE
 * AUTHORS OR COPYRIGHT HOLDERS BE LIABLE FOR ANY CLAIM, DAMAGES OR OTHER
 * LIABILITY, WHETHER IN AN ACTION OF CONTRACT, TORT OR OTHERWISE, ARISING FROM,
 * OUT OF OR IN CONNECTION WITH THE SOFTWARE OR THE USE OR OTHER DEALINGS IN THE
 * SOFTWARE.
 */

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
