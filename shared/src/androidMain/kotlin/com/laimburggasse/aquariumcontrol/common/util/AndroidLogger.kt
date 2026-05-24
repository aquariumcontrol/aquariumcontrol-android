package com.laimburggasse.aquariumcontrol.common.util

import com.google.firebase.Firebase
import com.google.firebase.crashlytics.crashlytics
import me.tatarka.inject.annotations.Inject

@Inject
class AndroidLogger : Logger {
    override fun recordException(e: Exception) {
        Firebase.crashlytics.recordException(e)
    }
}
