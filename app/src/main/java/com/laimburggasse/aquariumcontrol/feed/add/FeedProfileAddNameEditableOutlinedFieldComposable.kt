package com.laimburggasse.aquariumcontrol.feed.add

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
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.tooling.preview.Preview
import com.laimburggasse.aquariumcontrol.ui.theme.AquariumControllerTheme

@Composable
fun FeedProfileAddNameEditableOutlinedFieldComposable(
    onFeedProfileNameChange: (String) -> Unit,
    modifier: Modifier = Modifier,
    textAlign: TextAlign? = TextAlign.Center
) {
    val initialValue = ""
    var feedProfileName by remember { mutableStateOf(initialValue)}

    val myTextAlign = textAlign ?: TextAlign.Center
    OutlinedTextField(
        value = feedProfileName, // start with empty value
        onValueChange = { newValue ->
            val filteredValue = newValue.filter { it.isLetterOrDigit() || it == '_' }
            feedProfileName = filteredValue // Update the state
            onFeedProfileNameChange(filteredValue)
        },
        label = { Text("Feed Profile Name") },
        keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Text),
        modifier = modifier,
        textStyle = MaterialTheme.typography.bodyLarge.copy(
            textAlign = myTextAlign
        )
    )
}

@Preview(showBackground = true, uiMode = Configuration.UI_MODE_NIGHT_YES)
@Preview(showBackground = true, uiMode = Configuration.UI_MODE_NIGHT_NO)
@Composable
fun FeedProfileAddNameEditableOutlinedFieldComposablePreview() {
    AquariumControllerTheme {
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