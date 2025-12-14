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

package com.laimburggasse.aquariumcontrol.feed.presentation.add

import android.content.res.Configuration
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.tooling.preview.Preview
import com.laimburggasse.aquariumcontrol.feed.R
import com.laimburggasse.aquariumcontrol.common.presentation.theme.AquariumControllerTheme

@Composable
fun FeedProfileAddNameEditableOutlinedFieldComposable(
    onFeedProfileNameChange: (String) -> Unit,
    modifier: Modifier = Modifier,
    textAlign: TextAlign? = TextAlign.Center
) {
    val initialValue = ""
    var feedProfileName: String by remember { mutableStateOf(initialValue)}

    val myTextAlign: TextAlign = textAlign ?: TextAlign.Center
    OutlinedTextField(
        value = feedProfileName, // start with empty value
        onValueChange = { newValue ->
            val filteredValue: String = newValue.filter { it.isLetterOrDigit() || it == '_' }
            feedProfileName = filteredValue // Update the state
            onFeedProfileNameChange(filteredValue)
        },
        label = { Text(text = stringResource(id = R.string.feed_profile_name)) },
        keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Text),
        modifier = modifier,
        textStyle = MaterialTheme.typography.bodyLarge.copy(
            textAlign = myTextAlign
        )
    )
}

@Preview(showBackground = true, uiMode = Configuration.UI_MODE_NIGHT_YES)
@Composable
internal fun FeedProfileAddNameEditableOutlinedFieldComposableDarkPreview() {
    AquariumControllerTheme(darkTheme = true) {
        Surface(
            color = MaterialTheme.colorScheme.background
        ) {
            FeedProfileAddNameEditableOutlinedFieldComposable(
                onFeedProfileNameChange = {},
                modifier = Modifier
            )
        }
    }
}

@Preview(showBackground = true, uiMode = Configuration.UI_MODE_NIGHT_NO)
@Composable
internal fun FeedProfileAddNameEditableOutlinedFieldComposableLightPreview() {
    AquariumControllerTheme(darkTheme = false) {
        Surface(
            color = MaterialTheme.colorScheme.background
        ) {
            FeedProfileAddNameEditableOutlinedFieldComposable(
                onFeedProfileNameChange = {},
                modifier = Modifier
            )
        }
    }
}