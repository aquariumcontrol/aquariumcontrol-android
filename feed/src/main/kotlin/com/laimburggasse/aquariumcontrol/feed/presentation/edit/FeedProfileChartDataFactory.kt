/*
 * Copyright 2025 Uwe Martin
 *
 * Permission is hereby granted, free of charge, to any person obtaining a copy
 * of this software and associated documentation files (the "Software"), to deal
 * in the Software without restriction, including without limitation the rights
 * to use, copy, modify, merge, publish, distribute, sublicense, and/or sell
 * copies of the Software, and to permit persons to whom the Software is
 * furnished to do so, subject to the following conditions:
 *
 * The above copyright notice and this permission notice shall be included in all
 * copies or substantial portions of the Software.
 *
 * THE SOFTWARE IS PROVIDED "AS IS", WITHOUT WARRANTY OF ANY KIND, EXPRESS OR
 * IMPLIED, INCLUDING BUT NOT LIMITED TO THE WARRANTIES OF MERCHANTABILITY,
 * FITNESS FOR A PARTICULAR PURPOSE AND NONINFRINGEMENT. IN NO EVENT SHALL THE
 * AUTHORS OR COPYRIGHT HOLDERS BE LIABLE FOR ANY CLAIM, DAMAGES OR OTHER
 * LIABILITY, WHETHER IN AN ACTION OF CONTRACT, TORT OR OTHERWISE, ARISING FROM,
 * OUT OF OR IN CONNECTION WITH THE SOFTWARE OR THE USE OR OTHER DEALINGS IN THE
 * SOFTWARE.
 */

package com.laimburggasse.aquariumcontrol.feed.presentation.edit

import com.github.mikephil.charting.components.YAxis
import com.github.mikephil.charting.data.Entry
import com.github.mikephil.charting.data.LineData
import com.github.mikephil.charting.data.LineDataSet
import com.laimburggasse.aquariumcontrol.common.domain.GlobalConstants
import com.laimburggasse.aquariumcontrol.feed.domain.model.FeedProfile
import com.laimburggasse.aquariumcontrol.feed.domain.model.FeedProfilePhase
import kotlin.math.abs

/**
 * Factory object responsible for generating chart data bundles from a feed profile.
 * It transforms the sequence of feed profile phases into renderable line data sets for the UI.
 */
object FeedProfileChartDataFactory {
    /**
     * Defines the configuration and extraction logic for a single signal line on the chart.
     *
     * @property name The display name of the line (e.g., "Skimmer", "Main Pump 1").
     * @property signal The enum identifier of the signal being plotted.
     * @property getValue A lambda function to extract the active state (true/false) from a given phase.
     * @property yAxisValueOn The Y-axis value to plot when the signal is ON.
     * @property yAxisValueOff The Y-axis value to plot when the signal is OFF.
     */
    private data class FeedProfileChartLineDefinition(
        val name: String,
        val signal: FeedProfileSignal,
        val getValue: (FeedProfilePhase) -> Boolean,
        val yAxisValueOn: Float,
        val yAxisValueOff: Float
    )

    /**
     * Creates a [FeedProfileChartDataBundle] containing LineData objects for each actuator signal
     * based on the phases defined in the provided [FeedProfile].
     *
     * @param feedProfile The feed profile containing the phases to be visualized.
     * @return A bundle containing the LineData for each signal and the total duration of the profile.
     */
    fun create(feedProfile: FeedProfile): FeedProfileChartDataBundle {
        val chartLines = listOf(
            FeedProfileChartLineDefinition(
                name = GlobalConstants.LABEL_FEEDER,
                signal = FeedProfileSignal.FEEDER,
                getValue = { phase -> !phase.isPause },
                yAxisValueOn = FeedProfileDisplayYAxisValueDefinition.FEEDER_ON,
                yAxisValueOff = FeedProfileDisplayYAxisValueDefinition.FEEDER_OFF
            ),
            FeedProfileChartLineDefinition(
                name = GlobalConstants.LABEL_SKIMMER,
                signal = FeedProfileSignal.SKIMMER,
                getValue = { phase -> phase.skimmer },
                yAxisValueOn = FeedProfileDisplayYAxisValueDefinition.SKIMMER_ON,
                yAxisValueOff = FeedProfileDisplayYAxisValueDefinition.SKIMMER_OFF
            ),
            FeedProfileChartLineDefinition(
                name = GlobalConstants.LABEL_MAIN_PUMP_1,
                signal = FeedProfileSignal.MAIN_PUMP1,
                getValue = { phase -> phase.mainPump1 },
                yAxisValueOn = FeedProfileDisplayYAxisValueDefinition.MAIN_PUMP1_ON,
                yAxisValueOff = FeedProfileDisplayYAxisValueDefinition.MAIN_PUMP1_OFF
            ),
            FeedProfileChartLineDefinition(
                name = GlobalConstants.LABEL_MAIN_PUMP_2,
                signal = FeedProfileSignal.MAIN_PUMP2,
                getValue = { phase -> phase.mainPump2 },
                yAxisValueOn = FeedProfileDisplayYAxisValueDefinition.MAIN_PUMP2_ON,
                yAxisValueOff = FeedProfileDisplayYAxisValueDefinition.MAIN_PUMP2_OFF
            ),
            FeedProfileChartLineDefinition(
                name = GlobalConstants.LABEL_AUX_PUMP_1,
                signal = FeedProfileSignal.AUX_PUMP1,
                getValue = { phase -> phase.auxPump1 },
                yAxisValueOn = FeedProfileDisplayYAxisValueDefinition.AUX_PUMP1_ON,
                yAxisValueOff = FeedProfileDisplayYAxisValueDefinition.AUX_PUMP1_OFF
            ),
            FeedProfileChartLineDefinition(
                name = GlobalConstants.LABEL_AUX_PUMP_2,
                signal = FeedProfileSignal.AUX_PUMP2,
                getValue = { phase -> phase.auxPump2 },
                yAxisValueOn = FeedProfileDisplayYAxisValueDefinition.AUX_PUMP2_ON,
                yAxisValueOff = FeedProfileDisplayYAxisValueDefinition.AUX_PUMP2_OFF
            )
        )

        val valuesMap: Map<FeedProfileChartLineDefinition, MutableList<Entry>> =
            chartLines.associateWith { mutableListOf() }
        var cumulatedTime = 0.0f
        val firstPhase: FeedProfilePhase = feedProfile.feedProfilePhases.firstOrNull() ?:
            return FeedProfileChartDataBundle(
                lineDataFeeder = null,
                lineDataSkimmer = null,
                lineDataMainPump1 = null,
                lineDataMainPump2 = null,
                lineDataAuxPump1 = null,
                lineDataAuxPump2 = null,
                totalDuration = 0f
            )

        // Initial state
        chartLines.forEach { line ->
            val initialValue: Float = if (line.getValue(firstPhase)) line.yAxisValueOn else line.yAxisValueOff
            valuesMap.getValue(key =line).add(Entry(0f, initialValue))
        }

        // Process transitions
        feedProfile.feedProfilePhases.forEach { phase ->
            val phaseDuration: Float = phase.duration.toFloat()
            if (phaseDuration <= 0f) return@forEach

            chartLines.forEach { line ->
                val entries: MutableList<Entry> = valuesMap.getValue(key = line)
                val lastValue: Float = entries.last().y
                val newValue: Float = if (line.getValue(phase)) line.yAxisValueOn else line.yAxisValueOff

                if (abs(x = newValue - lastValue) > 0.1f) { // chartValAllowedDelta
                    entries.add(Entry( cumulatedTime, lastValue))
                    entries.add(Entry( cumulatedTime, newValue))
                }
            }

            cumulatedTime += phaseDuration

            chartLines.forEach { line ->
                val entries: MutableList<Entry> = valuesMap.getValue(key = line)
                val newValue: Float = if (line.getValue(phase)) line.yAxisValueOn else line.yAxisValueOff
                entries.add(Entry(cumulatedTime, newValue))
            }
        }

        val lineDataMap: Map<FeedProfileSignal, LineData> = chartLines.associate { line ->
            val set: LineDataSet = LineDataSet(valuesMap.getValue(line), line.name).apply {
                color = feedProfileLineColorDefinition(signal = line.signal)
                lineWidth = 2.5f
                axisDependency = YAxis.AxisDependency.LEFT
                setDrawValues(false)
                setDrawCircles(false)
            }
            line.signal to LineData(set)
        }

        return FeedProfileChartDataBundle(
            lineDataFeeder = lineDataMap[FeedProfileSignal.FEEDER],
            lineDataSkimmer = lineDataMap[FeedProfileSignal.SKIMMER],
            lineDataMainPump1 = lineDataMap[FeedProfileSignal.MAIN_PUMP1],
            lineDataMainPump2 = lineDataMap[FeedProfileSignal.MAIN_PUMP2],
            lineDataAuxPump1 = lineDataMap[FeedProfileSignal.AUX_PUMP1],
            lineDataAuxPump2 = lineDataMap[FeedProfileSignal.AUX_PUMP2],
            totalDuration = cumulatedTime
        )
    }
}