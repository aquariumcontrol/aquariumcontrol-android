package com.laimburggasse.aquariumcontrol.ventilation

import com.laimburggasse.aquariumcontrol.MainViewModel

interface VentilationSetValsViewModelInterface {
    fun updateValues(mainViewModel: MainViewModel)
    fun resetValues()
}