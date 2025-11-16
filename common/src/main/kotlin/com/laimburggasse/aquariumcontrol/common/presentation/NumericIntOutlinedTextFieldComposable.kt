package com.laimburggasse.aquariumcontrol.common.presentation

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
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.text.input.TextFieldValue
import androidx.compose.ui.tooling.preview.Preview
import com.laimburggasse.aquariumcontrol.common.presentation.theme.AquariumControllerTheme

@Composable
fun NumericIntOutlinedTextFieldComposable(
    value: Int,
    onValueChange: (String) -> Unit,
    label: String,
    modifier: Modifier = Modifier
) {
    var textFieldValueState: TextFieldValue by remember {
        mutableStateOf(value = TextFieldValue(text = value.toString()))
    }

    OutlinedTextField(
        value = textFieldValueState,
        onValueChange = { newTextFieldValue ->
            val newValue: String = newTextFieldValue.text.filter { it.isDigit() }
            textFieldValueState = newTextFieldValue.copy(text = newValue)
            onValueChange(newValue)
        },
        label = { Text(text = label) },
        keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Number),
        modifier = modifier
    )
}

@Preview(showBackground = true, uiMode = Configuration.UI_MODE_NIGHT_YES)
@Preview(showBackground = true, uiMode = Configuration.UI_MODE_NIGHT_NO)
@Composable
internal fun NumericIntOutlinedTextFieldComposablePreview() {
    val testValue = 5
    AquariumControllerTheme {
        Surface(color = MaterialTheme.colorScheme.background) {
            NumericIntOutlinedTextFieldComposable(
                value = testValue,
                onValueChange = {},
                label = "testLabel",
                modifier = Modifier,
            )
        }
    }
}