package com.example.aquariumcontroller.feed.edit

import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.padding
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.MoreVert
import androidx.compose.material3.Icon
import androidx.compose.material3.Switch
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import com.example.aquariumcontroller.ui.theme.AquariumControllerTheme

@Composable
fun FeedProfileEditMenuItemComposable(item: FeedProfileEditMenuItem) {
    Row(
        modifier = Modifier
            .clickable {
                if (item.children.isNotEmpty()) {
                    item.isExpanded.value = !item.isExpanded.value
                }
            }
            .padding(start = (item.level * 16).dp, top = 8.dp, bottom = 8.dp), // Indentation        verticalAlignment = Alignment.CenterVertically
    ) {
        Text(item.text)
        Spacer(Modifier.weight(1f))
        if (item.children.isNotEmpty()) {
            // Icon to indicate expandability
            Icon(imageVector = Icons.Default.MoreVert, contentDescription = "More options")
        }
        if (item.isToggleButton) {
            Switch(checked = item.isChecked.value, onCheckedChange = { item.isChecked.value = it })
        }
    }
    if (item.isExpanded.value) {
        FeedProfileEditMenuComposable(
            feedProfileEditMenuItems = item.children
        )
    }
}

@Preview(showBackground = true)
@Composable
fun FeedProfileEditMenuItemPreviewComposable() {

    val mockFeedProfileEditMenuItem1 = FeedProfileEditMenuItem(
        id = 1,
        level = 1,
        text = "Preview",
        isToggleButton = true,
        children = emptyList(),
    )

    val mockFeedProfileEditMenuItem2 = FeedProfileEditMenuItem(
        id = 1,
        level = 2,
        text = "Preview",
        isToggleButton = true,
        children = listOf(mockFeedProfileEditMenuItem1),
    )

    AquariumControllerTheme {
        FeedProfileEditMenuItemComposable(mockFeedProfileEditMenuItem2)
    }
}