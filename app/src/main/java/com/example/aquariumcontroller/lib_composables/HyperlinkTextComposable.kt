import androidx.compose.foundation.clickable
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalUriHandler
import androidx.compose.ui.text.SpanStyle
import androidx.compose.ui.text.buildAnnotatedString
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.text.style.TextDecoration
import androidx.compose.ui.text.withStyle

@Composable
fun HyperlinkText(text: String, url: String, textAlign: TextAlign? = null) {
    val uriHandler = LocalUriHandler.current
    Text(
        text = buildAnnotatedString {
            withStyle(
                style = SpanStyle(
                    color = Color.Blue,
                    textDecoration = TextDecoration.Underline
                )
            ) {
                append(text)
            }
        },
        textAlign = textAlign,
        modifier = Modifier.clickable { uriHandler.openUri(url) }
    )
}