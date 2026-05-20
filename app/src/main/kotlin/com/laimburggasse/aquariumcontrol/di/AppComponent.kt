package com.laimburggasse.aquariumcontrol.di

import android.content.Context
import com.laimburggasse.aquariumcontrol.balling.data.di.BallingDataModule
import com.laimburggasse.aquariumcontrol.balling.data.di.BallingDatabaseModule
import com.laimburggasse.aquariumcontrol.balling.data.di.BallingNetwork
import com.laimburggasse.aquariumcontrol.balling.presentation.edit.BallingProfileEditViewModel
import com.laimburggasse.aquariumcontrol.balling.presentation.entry.BallingProfilesViewModel
import com.laimburggasse.aquariumcontrol.balling.presentation.history.BallingHistoryViewModel
import com.laimburggasse.aquariumcontrol.common.di.AppModule
import com.laimburggasse.aquariumcontrol.common.di.ApplicationScope
import com.laimburggasse.aquariumcontrol.common.di.CommonNetwork
import com.laimburggasse.aquariumcontrol.common.di.DispatchersModule
import com.laimburggasse.aquariumcontrol.common.di.Network
import com.laimburggasse.aquariumcontrol.common.domain.SessionManager
import com.laimburggasse.aquariumcontrol.controller.presentation.add.ControllerAddViewModel
import com.laimburggasse.aquariumcontrol.controller.presentation.edit.ControllerEditViewModel
import com.laimburggasse.aquariumcontrol.controller.presentation.selection.ControllerSelectionViewModel
import com.laimburggasse.aquariumcontrol.feed.data.di.FeedDataModule
import com.laimburggasse.aquariumcontrol.feed.data.di.FeedDatabaseModule
import com.laimburggasse.aquariumcontrol.feed.data.di.FeedNetwork
import com.laimburggasse.aquariumcontrol.feed.presentation.add.FeedProfileAddViewModel
import com.laimburggasse.aquariumcontrol.feed.presentation.edit.FeedProfileEditViewModel
import com.laimburggasse.aquariumcontrol.feed.presentation.entry.FeedProfilesViewModel
import com.laimburggasse.aquariumcontrol.feed.presentation.entry.FeedScheduleViewModel
import com.laimburggasse.aquariumcontrol.feed.presentation.history.FeedHistoryViewModel
import com.laimburggasse.aquariumcontrol.heating.data.di.HeatingDataModule
import com.laimburggasse.aquariumcontrol.heating.data.di.HeatingNetwork
import com.laimburggasse.aquariumcontrol.heating.presentation.HeatingSetValsViewModel
import com.laimburggasse.aquariumcontrol.heating.presentation.HeatingStatsViewModel
import com.laimburggasse.aquariumcontrol.refill.data.di.RefillDataModule
import com.laimburggasse.aquariumcontrol.refill.data.di.RefillDatabaseModule
import com.laimburggasse.aquariumcontrol.refill.data.di.RefillNetwork
import com.laimburggasse.aquariumcontrol.refill.presentation.entry.RefillViewModel
import com.laimburggasse.aquariumcontrol.refill.presentation.history.RefillHistoryViewModel
import com.laimburggasse.aquariumcontrol.schedule.data.di.ScheduleDataModule
import com.laimburggasse.aquariumcontrol.schedule.data.di.ScheduleNetwork
import com.laimburggasse.aquariumcontrol.schedule.presentation.ScheduleViewModel
import com.laimburggasse.aquariumcontrol.summary.data.di.SummaryDataModule
import com.laimburggasse.aquariumcontrol.summary.data.di.SummaryNetwork
import com.laimburggasse.aquariumcontrol.summary.presentation.SummaryViewModel
import com.laimburggasse.aquariumcontrol.timedata.data.di.TimeDataDataModule
import com.laimburggasse.aquariumcontrol.timedata.data.di.TimeDataNetwork
import com.laimburggasse.aquariumcontrol.timedata.presentation.TimeDataViewModel
import com.laimburggasse.aquariumcontrol.ventilation.data.di.VentilationDataModule
import com.laimburggasse.aquariumcontrol.ventilation.data.di.VentilationNetwork
import com.laimburggasse.aquariumcontrol.ventilation.presentation.VentilationSetValsViewModel
import com.laimburggasse.aquariumcontrol.summary.domain.repository.SummaryRepository
import me.tatarka.inject.annotations.Component
import me.tatarka.inject.annotations.Provides

@Component
@ApplicationScope
abstract class AppComponent(
    @get:Provides val context: Context
) : AppModule, CommonNetwork, DispatchersModule, Network,
    BallingDataModule, BallingDatabaseModule, BallingNetwork,
    FeedDataModule, FeedDatabaseModule, FeedNetwork,
    HeatingDataModule, HeatingNetwork,
    RefillDataModule, RefillDatabaseModule, RefillNetwork,
    ScheduleDataModule, ScheduleNetwork,
    SummaryDataModule, SummaryNetwork,
    TimeDataDataModule, TimeDataNetwork,
    VentilationDataModule, VentilationNetwork {

    // ViewModels
    abstract val ballingProfilesViewModel: BallingProfilesViewModel
    abstract val ballingProfileEditViewModel: BallingProfileEditViewModel
    abstract val ballingHistoryViewModel: BallingHistoryViewModel

    abstract val controllerAddViewModel: ControllerAddViewModel
    abstract val controllerEditViewModel: ControllerEditViewModel
    abstract val controllerSelectionViewModel: ControllerSelectionViewModel

    abstract val feedProfileAddViewModel: FeedProfileAddViewModel
    abstract val feedProfileEditViewModel: FeedProfileEditViewModel
    abstract val feedProfilesViewModel: FeedProfilesViewModel
    abstract val feedScheduleViewModel: FeedScheduleViewModel
    abstract val feedHistoryViewModel: FeedHistoryViewModel

    abstract val heatingSetValsViewModel: HeatingSetValsViewModel
    abstract val heatingStatsViewModel: HeatingStatsViewModel

    abstract val refillViewModel: RefillViewModel
    abstract val refillHistoryViewModel: RefillHistoryViewModel

    abstract val scheduleViewModel: ScheduleViewModel

    abstract val summaryViewModel: SummaryViewModel

    abstract val timeDataViewModel: TimeDataViewModel

    abstract val ventilationSetValsViewModel: VentilationSetValsViewModel

    abstract val sessionManager: SessionManager

    abstract val summaryRepository: SummaryRepository
}
