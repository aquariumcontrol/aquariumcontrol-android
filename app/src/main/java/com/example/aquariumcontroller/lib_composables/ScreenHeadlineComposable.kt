package com.example.aquariumcontroller.lib_composables

import android.content.res.Configuration
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import com.example.aquariumcontroller.ui.theme.AquariumControllerTheme

@Composable
fun ScreenHeadlineComposable(text: String, textModifier: Modifier? = null) {
    Spacer(modifier = Modifier.size(10.dp))
    Text(
        text = text,
        style = MaterialTheme.typography.headlineLarge,
        modifier = textModifier ?: Modifier.padding(16.dp),
    )
    Spacer(modifier = Modifier.size(10.dp))
}

@Preview(showBackground = true, uiMode = Configuration.UI_MODE_NIGHT_YES)
@Preview(showBackground = true, uiMode = Configuration.UI_MODE_NIGHT_NO)
@Composable
fun ScreenHeadlineComposablePreview() {
    AquariumControllerTheme {
        Surface(
            color = MaterialTheme.colorScheme.background
        ) {
            ScreenHeadlineComposable(
                text = "PreviewTest Headline"
            )
        }
    }
}