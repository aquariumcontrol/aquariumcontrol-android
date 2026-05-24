package com.laimburggasse.aquariumcontrol.di

import com.laimburggasse.aquariumcontrol.common.di.ApplicationScope
import com.laimburggasse.aquariumcontrol.common.di.IosPlatformModule
import com.laimburggasse.aquariumcontrol.common.domain.SessionManager
import com.laimburggasse.aquariumcontrol.summary.domain.repository.SummaryRepository
import com.laimburggasse.aquariumcontrol.refill.domain.repository.RefillRepository
import com.laimburggasse.aquariumcontrol.refill.domain.repository.RefillHistoryRepository
import com.laimburggasse.aquariumcontrol.balling.domain.repository.BallingProfilesRepository
import com.laimburggasse.aquariumcontrol.balling.domain.repository.BallingHistoryRepository
import com.laimburggasse.aquariumcontrol.feed.domain.repository.FeedProfilesRepository
import com.laimburggasse.aquariumcontrol.feed.domain.repository.FeedScheduleRepository
import com.laimburggasse.aquariumcontrol.feed.domain.repository.FeedHistoryRepository
import com.laimburggasse.aquariumcontrol.heating.domain.repository.HeatingSetValsRepository
import com.laimburggasse.aquariumcontrol.heating.domain.repository.HeatingStatsRepository
import com.laimburggasse.aquariumcontrol.schedule.domain.repository.ScheduleRepository
import com.laimburggasse.aquariumcontrol.timedata.domain.repository.TimeDataRepository
import com.laimburggasse.aquariumcontrol.ventilation.domain.repository.VentilationSetValsRepository
import me.tatarka.inject.annotations.Component

@Component
@ApplicationScope
abstract class IosComponent : SharedModule, IosPlatformModule {
    abstract val sessionManager: SessionManager
    abstract val summaryRepository: SummaryRepository
    abstract val refillRepository: RefillRepository
    abstract val refillHistoryRepository: RefillHistoryRepository
    abstract val ballingProfilesRepository: BallingProfilesRepository
    abstract val ballingHistoryRepository: BallingHistoryRepository
    abstract val feedProfilesRepository: FeedProfilesRepository
    abstract val feedScheduleRepository: FeedScheduleRepository
    abstract val feedHistoryRepository: FeedHistoryRepository
    abstract val heatingSetValsRepository: HeatingSetValsRepository
    abstract val heatingStatsRepository: HeatingStatsRepository
    abstract val scheduleRepository: ScheduleRepository
    abstract val timeDataRepository: TimeDataRepository
    abstract val ventilationSetValsRepository: VentilationSetValsRepository

    companion object
}
