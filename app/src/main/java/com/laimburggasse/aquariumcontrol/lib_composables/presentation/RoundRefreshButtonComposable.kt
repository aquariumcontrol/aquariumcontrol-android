package com.laimburggasse.aquariumcontrol.lib_composables.presentation

import android.content.res.Configuration
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Sync
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Surface
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import com.laimburggasse.aquariumcontrol.R
import com.laimburggasse.aquariumcontrol.ui.theme.AquariumControllerTheme

@Composable
fun RoundRefreshButtonComposable(onClick: () -> Unit) {
    val icon: ImageVector = Icons.Filled.Sync
    Surface(
        onClick = onClick,
        modifier = Modifier.size(size = 32.dp),
        shape = CircleShape, // this is a round button
        color = MaterialTheme.colorScheme.primary
    ) {
        IconButton(
            onClick = onClick,
            modifier = Modifier.size(size = 48.dp),
        ) {
            Icon(
                imageVector = icon,
                contentDescription = stringResource(id = R.string.refresh)
            )
        }
    }
}

@Preview(showBackground = true, uiMode = Configuration.UI_MODE_NIGHT_YES)
@Preview(showBackground = true, uiMode = Configuration.UI_MODE_NIGHT_NO)
@Composable
internal fun RoundRefreshButtonComposablePreview() {
    AquariumControllerTheme {
        Surface(color = MaterialTheme.colorScheme.background) {
            RoundRefreshButtonComposable(onClick = {})
        }
    }
}