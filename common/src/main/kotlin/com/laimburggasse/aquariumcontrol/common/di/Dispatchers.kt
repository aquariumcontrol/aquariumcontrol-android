package com.laimburggasse.aquariumcontrol.common.di

import kotlinx.coroutines.CoroutineDispatcher

data class DefaultDispatcher(val dispatcher: CoroutineDispatcher)
data class IoDispatcher(val dispatcher: CoroutineDispatcher)
data class MainDispatcher(val dispatcher: CoroutineDispatcher)
