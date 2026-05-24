package com.laimburggasse.aquariumcontrol.di

import android.content.Context
import com.laimburggasse.aquariumcontrol.common.di.AndroidPlatformModule
import com.laimburggasse.aquariumcontrol.common.di.ApplicationScope
import com.laimburggasse.aquariumcontrol.di.SharedModule
import com.laimburggasse.aquariumcontrol.balling.presentation.edit.BallingProfileEditViewModel
import com.laimburggasse.aquariumcontrol.balling.presentation.entry.BallingProfilesViewModel
import com.laimburggasse.aquariumcontrol.balling.presentation.history.BallingHistoryViewModel
import com.laimburggasse.aquariumcontrol.common.domain.SessionManager
import com.laimburggasse.aquariumcontrol.controller.presentation.add.ControllerAddViewModel
import com.laimburggasse.aquariumcontrol.controller.presentation.edit.ControllerEditViewModel
import com.laimburggasse.aquariumcontrol.controller.presentation.selection.ControllerSelectionViewModel
import com.laimburggasse.aquariumcontrol.feed.presentation.add.FeedProfileAddViewModel
import com.laimburggasse.aquariumcontrol.feed.presentation.edit.FeedProfileEditViewModel
import com.laimburggasse.aquariumcontrol.feed.presentation.entry.FeedProfilesViewModel
import com.laimburggasse.aquariumcontrol.feed.presentation.entry.FeedScheduleViewModel
import com.laimburggasse.aquariumcontrol.feed.presentation.history.FeedHistoryViewModel
import com.laimburggasse.aquariumcontrol.heating.presentation.HeatingSetValsViewModel
import com.laimburggasse.aquariumcontrol.heating.presentation.HeatingStatsViewModel
import com.laimburggasse.aquariumcontrol.refill.presentation.entry.RefillViewModel
import com.laimburggasse.aquariumcontrol.refill.presentation.history.RefillHistoryViewModel
import com.laimburggasse.aquariumcontrol.schedule.presentation.ScheduleViewModel
import com.laimburggasse.aquariumcontrol.summary.presentation.SummaryViewModel
import com.laimburggasse.aquariumcontrol.timedata.presentation.TimeDataViewModel
import com.laimburggasse.aquariumcontrol.ventilation.presentation.VentilationSetValsViewModel
import com.laimburggasse.aquariumcontrol.summary.domain.repository.SummaryRepository
import me.tatarka.inject.annotations.Component
import me.tatarka.inject.annotations.Provides

@Component
@ApplicationScope
abstract class AppComponent(
    @get:Provides override val context: Context
) : SharedModule, AndroidPlatformModule {

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
