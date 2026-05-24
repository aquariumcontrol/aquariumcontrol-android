package com.laimburggasse.aquariumcontrol.di

import com.laimburggasse.aquariumcontrol.balling.data.di.BallingDataModule
import com.laimburggasse.aquariumcontrol.common.di.AppModule
import com.laimburggasse.aquariumcontrol.common.di.CommonNetwork
import com.laimburggasse.aquariumcontrol.common.di.DispatchersModule
import com.laimburggasse.aquariumcontrol.common.di.Network
import com.laimburggasse.aquariumcontrol.common.di.PlatformModule
import com.laimburggasse.aquariumcontrol.feed.data.di.FeedDataModule
import com.laimburggasse.aquariumcontrol.heating.data.di.HeatingDataModule
import com.laimburggasse.aquariumcontrol.refill.data.di.RefillDataModule
import com.laimburggasse.aquariumcontrol.schedule.data.di.ScheduleDataModule
import com.laimburggasse.aquariumcontrol.summary.data.di.SummaryDataModule
import com.laimburggasse.aquariumcontrol.timedata.data.di.TimeDataDataModule
import com.laimburggasse.aquariumcontrol.ventilation.data.di.VentilationDataModule

interface SharedModule : AppModule, CommonNetwork, Network, DispatchersModule,
    BallingDataModule, FeedDataModule, HeatingDataModule, RefillDataModule,
    ScheduleDataModule, SummaryDataModule, TimeDataDataModule, VentilationDataModule
