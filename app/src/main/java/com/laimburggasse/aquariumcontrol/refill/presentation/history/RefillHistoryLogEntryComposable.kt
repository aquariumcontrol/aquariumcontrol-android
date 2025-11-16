package com.laimburggasse.aquariumcontrol.refill.presentation.history

import android.content.res.Configuration
import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.border
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.laimburggasse.aquariumcontrol.R
import com.laimburggasse.aquariumcontrol.refill.domain.model.RefillHistoryLogEntry
import com.laimburggasse.aquariumcontrol.common.presentation.theme.AquariumControllerTheme
import java.sql.Timestamp

@Composable
fun RefillEventComposable(
    refillHistoryLogEntry: RefillHistoryLogEntry
) {
    // This composable displays a refill event:
    // Text objects for name and value of Timestamp, Duration, Volume

    Row(
        modifier = Modifier
            .fillMaxWidth()
            .border(
                border = BorderStroke(
                    width = 1.dp,
                    color = MaterialTheme.colorScheme.onSurface
                )
            )
            .padding(all = 8.dp)
    ) {
        Column(
            modifier = Modifier.weight(weight = 1f)
        ) {
            Text(
                text = stringResource(id = R.string.timestamp),
                fontSize = 12.sp
            )
            Text(
                text = refillHistoryLogEntry.timestamp,
                fontSize = 12.sp
            )
        }
        Column(
            modifier = Modifier.weight(weight = 1f)
        ) {
            Text(
                text = stringResource(id = R.string.duration) +
                        "%.1f".format(refillHistoryLogEntry.duration) +
                        stringResource(id = R.string.seconds),
                fontSize = 12.sp
            )
            Text(
                text = stringResource(id = R.string.volume) +
                        "%.2f".format(refillHistoryLogEntry.volume) +
                        stringResource(id = R.string.liters),
                fontSize = 12.sp
            )
        }
    }
}

@Preview(showBackground = true, uiMode = Configuration.UI_MODE_NIGHT_YES)
@Preview(showBackground = true, uiMode = Configuration.UI_MODE_NIGHT_NO)
@Composable
internal fun RefillEventComposablePreview() {
    val fixedTimestamp = Timestamp(0L)

    val mockEvent = RefillHistoryLogEntry(
        timestamp = fixedTimestamp,
        duration = 60.0,
        volume = 10.0
    )

    AquariumControllerTheme {
        Surface(color = MaterialTheme.colorScheme.background) {
            RefillEventComposable(refillHistoryLogEntry = mockEvent)
        }
    }
}