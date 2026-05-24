package com.laimburggasse.aquariumcontrol.common.util

import com.laimburggasse.aquariumcontrol.common.util.DispatcherIO

import kotlinx.coroutines.CoroutineDispatcher

expect val DispatcherIO: CoroutineDispatcher
