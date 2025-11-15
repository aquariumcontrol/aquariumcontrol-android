package com.laimburggasse.aquariumcontrol.lib_composables.presentation

import android.content.res.Configuration
import androidx.compose.foundation.clickable
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalUriHandler
import androidx.compose.ui.platform.UriHandler
import androidx.compose.ui.text.SpanStyle
import androidx.compose.ui.text.buildAnnotatedString
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.text.style.TextDecoration
import androidx.compose.ui.text.withStyle
import androidx.compose.ui.tooling.preview.Preview
import com.laimburggasse.aquariumcontrol.ui.theme.AquariumControllerTheme

@Composable
fun HyperlinkTextComposable(text: String, url: String, textAlign: TextAlign? = null) {
    val uriHandler: UriHandler = LocalUriHandler.current
    Text(
        text = buildAnnotatedString {
            withStyle(style = SpanStyle(color = Color.Blue, textDecoration = TextDecoration.Underline)) {
                append(text)
            }
        },
        textAlign = textAlign,
        modifier = Modifier.clickable { uriHandler.openUri(uri = url) }
    )
}

@Preview(showBackground = true, uiMode = Configuration.UI_MODE_NIGHT_YES)
@Preview(showBackground = true, uiMode = Configuration.UI_MODE_NIGHT_NO)
@Composable
internal fun HyperlinkTextComposablePreview() {
    AquariumControllerTheme {
        Surface {
            HyperlinkTextComposable(text = "Click here", url = "https://www.google.com")
        }
    }
}
