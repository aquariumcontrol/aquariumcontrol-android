package com.example.aquariumcontroller

import androidx.compose.foundation.layout.width
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material.TextField
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.runtime.snapshotFlow
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.text.input.TextFieldValue
import androidx.compose.ui.unit.dp

@Composable
fun NumericDoubleOutlinedTextFieldComposable(
    value: Double,
    onValueChange: (String) -> Unit,
    label: String,
    modifier: Modifier = Modifier
) {
    var textFieldValueState by remember {
        mutableStateOf(TextFieldValue(value.toString()))
    }

    LaunchedEffect(value) { // Trigger when 'value' changes
        snapshotFlow { value }
            .collect { newValue ->
                textFieldValueState = textFieldValueState.copy(text = newValue.toString())
            }
    }

    OutlinedTextField(
        value = textFieldValueState,
        onValueChange = { newTextFieldValue ->
            val newValue =
                newTextFieldValue.text
                    .filter { it.isDigit() || it == '.' || it == ',' }
                    .replace(",", ".")
            textFieldValueState = newTextFieldValue.copy(text = newValue)
            onValueChange(newValue)
        },
        label = { Text(label) },
        keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Decimal),
        modifier = modifier.width(100.dp)
    )
}