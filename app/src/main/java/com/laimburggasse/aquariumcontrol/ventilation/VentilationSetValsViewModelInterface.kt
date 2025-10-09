package com.laimburggasse.aquariumcontrol.ventilation

import androidx.lifecycle.LiveData
import com.laimburggasse.aquariumcontrol.MainViewModel

interface VentilationSetValsViewModelInterface {
    var ventilationSetValsUiState: VentilationSetValsUiState
    val sliderRange: LiveData<ClosedFloatingPointRange<Float>>
    val lowerBoundValue: LiveData<Double>
    val upperBoundValue: LiveData<Double>
    val onRangeChangedBySlider: (Float, Float) -> Unit
    val onLowerValueChanged: (Float) -> Unit
    val onUpperValueChanged: (Float) -> Unit
    fun loadData(mainViewModel: MainViewModel)
    fun updateValues(mainViewModel: MainViewModel)
    fun resetValues()
}