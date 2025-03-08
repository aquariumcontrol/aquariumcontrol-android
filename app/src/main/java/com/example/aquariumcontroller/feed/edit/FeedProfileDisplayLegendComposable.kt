package com.example.aquariumcontroller.feed.edit

import android.content.res.Configuration
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalConfiguration
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.aquariumcontroller.R
import com.example.aquariumcontroller.ui.theme.AquariumControllerTheme

@Composable
fun FeedProfileDisplayLegendComposable() {
    // This composable displays the legend for the feed profile with each signal being represented by:
    // - a rectangle bullet with the corresponding color
    // - the name of the signal

    val configuration = LocalConfiguration.current

    val rectBullet = stringResource(id = R.string.rect_bullet)

    when (configuration.orientation) {
        Configuration.ORIENTATION_LANDSCAPE -> {
            Row {
                Column(
                    modifier = Modifier.padding(
                        horizontal = 12.dp
                    )
                ) {
                    Row {
                        Text(
                            text = rectBullet,
                            fontSize = 12.sp,
                            color = feedProfileTextColorDefinition(FeedProfileSignal.FEEDER),
                            modifier = Modifier.padding(
                                horizontal = 4.dp
                            )
                        )
                        Text(text = "Feeder", fontSize = 12.sp)
                    }
                }
                Column(
                    modifier = Modifier.padding(
                        horizontal = 12.dp
                    )
                )
                {
                    Row {
                        Text(
                            text = rectBullet, fontSize = 12.sp,
                            color = feedProfileTextColorDefinition(FeedProfileSignal.MAIN_PUMP1),
                            modifier = Modifier.padding(
                                horizontal = 4.dp
                            )
                        )
                        Text(text = "Main Pump 1", fontSize = 12.sp)
                    }
                }
                Column(
                    modifier = Modifier.padding(
                        horizontal = 12.dp
                    )
                )
                {
                    Row {
                        Text(
                            text = rectBullet, fontSize = 12.sp,
                            color = feedProfileTextColorDefinition(FeedProfileSignal.MAIN_PUMP2),
                            modifier = Modifier.padding(
                                horizontal = 4.dp
                            )
                        )
                        Text(text = "Main Pump 2", fontSize = 12.sp)
                    }
                }
                Column(
                    modifier = Modifier.padding(
                        horizontal = 12.dp
                    )
                )
                {
                    Row {
                        Text(
                            text = rectBullet, fontSize = 12.sp,
                            color = feedProfileTextColorDefinition(FeedProfileSignal.SKIMMER),
                            modifier = Modifier.padding(
                                horizontal = 4.dp
                            )
                        )
                        Text(text = "Skimmer", fontSize = 12.sp)
                    }
                }
                Column(
                    modifier = Modifier.padding(
                        horizontal = 12.dp
                    )
                )
                {
                    Row {
                        Text(
                            text = rectBullet, fontSize = 12.sp,
                            color = feedProfileTextColorDefinition(FeedProfileSignal.AUX_PUMP1),
                            modifier = Modifier.padding(
                                horizontal = 4.dp
                            )
                        )
                        Text(text = "Aux. Pump 1", fontSize = 12.sp)
                    }
                }
                Column(
                    modifier = Modifier.padding(
                        horizontal = 12.dp
                    )
                )
                {
                    Row {
                        Text(
                            text = rectBullet, fontSize = 12.sp,
                            color = feedProfileTextColorDefinition(FeedProfileSignal.AUX_PUMP2),
                            modifier = Modifier.padding(
                                horizontal = 4.dp
                            )
                        )
                        Text(text = "Aux. Pump 2", fontSize = 12.sp)
                    }
                }
            }
        }
        else -> {
            Row {
                Column(
                    modifier = Modifier.padding(
                        horizontal = 12.dp
                    )
                ) {
                    Row {
                        Text(
                            text = rectBullet,
                            fontSize = 12.sp,
                            color = feedProfileTextColorDefinition(FeedProfileSignal.FEEDER),
                            modifier = Modifier.padding(
                                horizontal = 4.dp
                            )
                        )
                        Text(text = "Feeder", fontSize = 12.sp)
                    }
                    Row {
                        Text(
                            text = rectBullet, fontSize = 12.sp,
                            color = feedProfileTextColorDefinition(FeedProfileSignal.MAIN_PUMP1),
                            modifier = Modifier.padding(
                                horizontal = 4.dp
                            )
                        )
                        Text(text = "Main Pump 1", fontSize = 12.sp)
                    }
                    Row {
                        Text(
                            text = rectBullet, fontSize = 12.sp,
                            color = feedProfileTextColorDefinition(FeedProfileSignal.AUX_PUMP1),
                            modifier = Modifier.padding(
                                horizontal = 4.dp
                            )
                        )
                        Text(text = "Aux. Pump 1", fontSize = 12.sp)
                    }
                }
                Column(
                    modifier = Modifier.padding(
                        horizontal = 12.dp
                    )
                ) {
                    Row {
                        Text(
                            text = rectBullet, fontSize = 12.sp,
                            color = feedProfileTextColorDefinition(FeedProfileSignal.SKIMMER),
                            modifier = Modifier.padding(
                                horizontal = 4.dp
                            )
                        )
                        Text(text = "Skimmer", fontSize = 12.sp)
                    }
                    Row {
                        Text(
                            text = rectBullet, fontSize = 12.sp,
                            color = feedProfileTextColorDefinition(FeedProfileSignal.MAIN_PUMP2),
                            modifier = Modifier.padding(
                                horizontal = 4.dp
                            )
                        )
                        Text(text = "Main Pump 2", fontSize = 12.sp)
                    }
                    Row {
                        Text(
                            text = rectBullet, fontSize = 12.sp,
                            color = feedProfileTextColorDefinition(FeedProfileSignal.AUX_PUMP2),
                            modifier = Modifier.padding(
                                horizontal = 4.dp
                            )
                        )
                        Text(text = "Aux. Pump 2", fontSize = 12.sp)
                    }
                    Spacer(modifier = Modifier.height(16.dp))
                }
            }
        }
    }
}

@Preview(showBackground = true, uiMode = Configuration.UI_MODE_NIGHT_YES)
@Preview(showBackground = true, uiMode = Configuration.UI_MODE_NIGHT_NO)
@Preview(
    showBackground = true,
    uiMode = Configuration.UI_MODE_NIGHT_YES,
    device = "spec:width=800dp,height=600dp,orientation=landscape"
)
@Preview(
    showBackground = true,
    uiMode = Configuration.UI_MODE_NIGHT_NO,
    device = "spec:width=800dp,height=600dp,orientation=landscape"
)
@Composable
fun FeedProfileDisplayLegendPreview() {
    AquariumControllerTheme {
        Surface(
            color = MaterialTheme.colorScheme.background
        ) {
            FeedProfileDisplayLegendComposable()
        }
    }
}