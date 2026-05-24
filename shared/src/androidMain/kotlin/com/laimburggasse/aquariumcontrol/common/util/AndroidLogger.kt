package com.laimburggasse.aquariumcontrol.common.util

import me.tatarka.inject.annotations.Inject

@Inject
class AndroidLogger : Logger {
    override fun recordException(e: Exception) {
        // Firebase removed as per requirement
        println("Android Exception: ${e.message}")
        e.printStackTrace()
    }
}
