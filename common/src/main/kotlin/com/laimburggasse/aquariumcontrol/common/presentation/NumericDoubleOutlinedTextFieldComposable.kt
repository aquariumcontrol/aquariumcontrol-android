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

package com.laimburggasse.aquariumcontrol.common.presentation

import android.content.res.Configuration
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.runtime.snapshotFlow
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.text.input.TextFieldValue
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import com.laimburggasse.aquariumcontrol.common.presentation.theme.AquariumControllerTheme

fun isParsableToDouble(input: String): Boolean {
    return try {
        input.toDouble()
        true // Parsing successful
    } catch (_: NumberFormatException) {
        false // Parsing failed
    }
}

@Composable
fun NumericDoubleOutlinedTextFieldComposable(
    value: Double,
    onValueChange: (String) -> Unit,
    label: String,
    modifier: Modifier = Modifier,
    precision: Int = 2, // Add precision parameter with default value
) {
    var textFieldValueState: TextFieldValue by remember {
        mutableStateOf(value = TextFieldValue(text = value.toString()))
    }

    LaunchedEffect(key1 = value) { // Trigger when 'value' changes
        snapshotFlow { value }
            .collect { newValue ->
                val formattedValue: String =
                    String.format("%.${precision}f", newValue) // Format incoming value
                textFieldValueState = textFieldValueState.copy(text = formattedValue)
            }
    }

    OutlinedTextField(
        value = textFieldValueState,
        onValueChange = { newTextFieldValue ->
            val newValue: String =
                newTextFieldValue.text
                    .filter { it.isDigit() || it == '.' || it == ',' }
                    .replace(oldValue = ",", newValue = ".")
            if (isParsableToDouble(input = newValue)) {
                val doubleValue: Double = newValue.toDoubleOrNull() ?: 0.0
                val formattedValue: String =
                    String.format("%.${precision}f", doubleValue) // Format with precision
                textFieldValueState = newTextFieldValue.copy(text = formattedValue)
                onValueChange(newValue)
            }
        },
        label = {
            Box(modifier = Modifier.fillMaxWidth(), contentAlignment = Alignment.Center) {
                Text(text = label)
            }
        },
        keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Decimal),
        modifier = modifier.width(width = 100.dp),
        singleLine = true,
        textStyle = MaterialTheme.typography.bodyLarge.copy(textAlign = TextAlign.Center)
    )
}

@Preview(showBackground = true, uiMode = Configuration.UI_MODE_NIGHT_YES)
@Composable
internal fun NumericDoubleOutlinedTextFieldComposableDarkPreview() {
    val testValue = 0.5
    AquariumControllerTheme(darkTheme = true) {
        Surface(color = MaterialTheme.colorScheme.background) {
            NumericDoubleOutlinedTextFieldComposable(
                value = testValue,
                onValueChange = {},
                label = "testLabel",
                modifier = Modifier,
            )
        }
    }
}

@Preview(showBackground = true, uiMode = Configuration.UI_MODE_NIGHT_NO)
@Composable
internal fun NumericDoubleOutlinedTextFieldComposableLightPreview() {
    val testValue = 0.5
    AquariumControllerTheme(darkTheme = false) {
        Surface(color = MaterialTheme.colorScheme.background) {
            NumericDoubleOutlinedTextFieldComposable(
                value = testValue,
                onValueChange = {},
                label = "testLabel",
                modifier = Modifier,
            )
        }
    }
}