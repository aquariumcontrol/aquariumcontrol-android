package com.laimburggasse.aquariumcontrol.heating

import com.laimburggasse.aquariumcontrol.MainViewModel

interface HeatingSetValsViewModelInterface {
    fun updateValues(mainViewModel: MainViewModel)
    fun resetValues()
}