package com.laimburggasse.aquariumcontrol.common.util

import me.tatarka.inject.annotations.Inject

@Inject
class IosLogger : Logger {
    override fun recordException(e: Exception) {
        println("Exception: ${e.message}")
        e.printStackTrace()
    }
}
