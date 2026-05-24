package com.laimburggasse.aquariumcontrol.common.util

import kotlinx.coroutines.CoroutineDispatcher
import kotlinx.coroutines.Dispatchers

actual val DispatcherIO: CoroutineDispatcher get() = Dispatchers.Default
