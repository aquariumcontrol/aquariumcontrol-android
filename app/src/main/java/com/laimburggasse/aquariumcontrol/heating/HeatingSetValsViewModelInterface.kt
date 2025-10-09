package com.laimburggasse.aquariumcontrol.heating

import android.util.Log
import androidx.lifecycle.LiveData
import com.laimburggasse.aquariumcontrol.MainViewModel

interface HeatingSetValsViewModelInterface {
    var heatingSetValsUiState: HeatingSetValsUiState
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