package com.example.aquariumcontroller

import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.Text
import androidx.compose.runtime.*
import androidx.compose.runtime.getValue
import androidx.compose.runtime.setValue
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.text.input.TextFieldValue
import androidx.compose.ui.Modifier

@Composable
fun NumericOutlinedTextField(
    value: String,
    onValueChange: (String) -> Unit,
    label: String,
    modifier: Modifier = Modifier
) {
    var textFieldValueState by remember {
        mutableStateOf(TextFieldValue(value))
    }

    OutlinedTextField(
        value = textFieldValueState,
        onValueChange = { newTextFieldValue ->
            val numericValue = newTextFieldValue.text.filter { it.isDigit() }
            textFieldValueState = newTextFieldValue.copy(text = numericValue)
            onValueChange(numericValue)
        },
        label = { Text(label) },
        keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Number),
        modifier = modifier
    )
}