/*
 *  Copyright 2025 Uwe Martin
 *
 *  Permission is hereby granted, free of charge, to any person obtaining a copy
 *  of this software and associated documentation files (the "Software"), to deal
 *  in the Software without restriction, including without limitation the rights
 *  to use, copy, modify, merge, publish, distribute, sublicense, and/or sell
 *  copies of the Software, and to permit persons to whom the Software is
 *  furnished to do so, subject to the following conditions:
 *
 *  The above copyright notice and this permission notice shall be included in all
 *  copies or substantial portions of the Software.
 *
 * THE SOFTWARE IS PROVIDED "AS IS", WITHOUT WARRANTY OF ANY KIND, EXPRESS OR
 * IMPLIED, INCLUDING BUT NOT LIMITED TO THE WARRANTIES OF MERCHANTABILITY,
 * FITNESS FOR A PARTICULAR PURPOSE AND NONINFRINGEMENT. IN NO EVENT SHALL THE
 * AUTHORS OR COPYRIGHT HOLDERS BE LIABLE FOR ANY CLAIM, DAMAGES OR OTHER
 * LIABILITY, WHETHER IN AN ACTION OF CONTRACT, TORT OR OTHERWISE, ARISING FROM,
 * OUT OF OR IN CONNECTION WITH THE SOFTWARE OR THE USE OR OTHER DEALINGS IN THE
 * SOFTWARE.
 */

package com.laimburggasse.aquariumcontrol.balling.history

import android.content.res.Configuration
import androidx.compose.runtime.Composable
import androidx.compose.ui.tooling.preview.Preview
import com.android.tools.screenshot.PreviewTest
import com.laimburggasse.aquariumcontrol.balling.presentation.history.BallingHistoryScreenEmptyDarkPreview
import com.laimburggasse.aquariumcontrol.balling.presentation.history.BallingHistoryScreenEmptyLightPreview
import com.laimburggasse.aquariumcontrol.balling.presentation.history.BallingHistoryScreenErrorDarkPreview
import com.laimburggasse.aquariumcontrol.balling.presentation.history.BallingHistoryScreenErrorLightPreview
import com.laimburggasse.aquariumcontrol.balling.presentation.history.BallingHistoryScreenLoadingDarkPreview
import com.laimburggasse.aquariumcontrol.balling.presentation.history.BallingHistoryScreenLoadingLightPreview
import com.laimburggasse.aquariumcontrol.balling.presentation.history.BallingHistoryScreenSuccessDarkPreview
import com.laimburggasse.aquariumcontrol.balling.presentation.history.BallingHistoryScreenSuccessLightPreview

class BallingHistoryScreenTest {
    @PreviewTest
    @Preview(showBackground = true, uiMode = Configuration.UI_MODE_NIGHT_YES)
    @Preview(
        showBackground = true,
        uiMode = Configuration.UI_MODE_NIGHT_YES,
        device = "spec:width=600dp,height=400dp,orientation=landscape"
    )
    @Composable
    fun BallingHistoryScreenSuccessDarkTest() {
        BallingHistoryScreenSuccessDarkPreview()
    }

    @PreviewTest
    @Preview(showBackground = true, uiMode = Configuration.UI_MODE_NIGHT_NO)
    @Preview(
        showBackground = true,
        uiMode = Configuration.UI_MODE_NIGHT_NO,
        device = "spec:width=600dp,height=400dp,orientation=landscape"
    )
    @Composable
    fun BallingHistoryScreenSuccessLightTest() {
        BallingHistoryScreenSuccessLightPreview()
    }

    @PreviewTest
    @Preview(showBackground = true, uiMode = Configuration.UI_MODE_NIGHT_YES)
    @Composable
    fun BallingHistoryScreenEmptyDarkTest() {
        BallingHistoryScreenEmptyDarkPreview()
    }

    @PreviewTest
    @Preview(showBackground = true, uiMode = Configuration.UI_MODE_NIGHT_NO)
    @Composable
    fun BallingHistoryScreenEmptyLightTest() {
        BallingHistoryScreenEmptyLightPreview()
    }

    @PreviewTest
    @Preview(showBackground = true, uiMode = Configuration.UI_MODE_NIGHT_YES)
    @Composable
    fun BallingHistoryScreenErrorTest() {
        BallingHistoryScreenErrorDarkPreview()
    }

    @PreviewTest
    @Preview(showBackground = true, uiMode = Configuration.UI_MODE_NIGHT_NO)
    @Composable
    fun BallingHistoryScreenErrorLightTest() {
        BallingHistoryScreenErrorLightPreview()
    }

    @PreviewTest
    @Preview(showBackground = true, uiMode = Configuration.UI_MODE_NIGHT_YES)
    @Composable
    fun BallingHistoryScreenLoadingTest() {
        BallingHistoryScreenLoadingDarkPreview()
    }

    @PreviewTest
    @Preview(showBackground = true, uiMode = Configuration.UI_MODE_NIGHT_NO)
    @Composable
    fun BallingHistoryScreenLoadingLightTest() {
        BallingHistoryScreenLoadingLightPreview()
    }
}