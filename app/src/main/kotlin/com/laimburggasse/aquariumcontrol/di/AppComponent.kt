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

/**
 * The root dependency injection component for the Android application.
 * Integrates the shared business logic and platform-specific Android modules.
 *
 * @property context The application context provided for dependency resolution.
 */
@Component
@ApplicationScope
abstract class AppComponent(
    @get:Provides override val context: Context
) : SharedModule, AndroidPlatformModule {

    // ViewModels
        /** ViewModel for the Balling dosing profile list. */
    abstract val ballingProfilesViewModel: BallingProfilesViewModel
        /** ViewModel for editing a specific Balling dosing profile. */
    abstract val ballingProfileEditViewModel: BallingProfileEditViewModel
        /** ViewModel for the Balling dosing history log. */
    abstract val ballingHistoryViewModel: BallingHistoryViewModel

        /** ViewModel for adding a new controller profile. */
    abstract val controllerAddViewModel: ControllerAddViewModel
        /** ViewModel for editing an existing controller profile. */
    abstract val controllerEditViewModel: ControllerEditViewModel
        /** ViewModel for selecting the active controller profile. */
    abstract val controllerSelectionViewModel: ControllerSelectionViewModel

        /** ViewModel for adding a new feed profile. */
    abstract val feedProfileAddViewModel: FeedProfileAddViewModel
        /** ViewModel for editing a specific feed profile. */
    abstract val feedProfileEditViewModel: FeedProfileEditViewModel
        /** ViewModel for the feed profile list. */
    abstract val feedProfilesViewModel: FeedProfilesViewModel
        /** ViewModel for managing the feed schedule entries. */
    abstract val feedScheduleViewModel: FeedScheduleViewModel
        /** ViewModel for the feed history log. */
    abstract val feedHistoryViewModel: FeedHistoryViewModel

        /** ViewModel for managing heating temperature setpoints. */
    abstract val heatingSetValsViewModel: HeatingSetValsViewModel
        /** ViewModel for the heating statistical data and charts. */
    abstract val heatingStatsViewModel: HeatingStatsViewModel

        /** ViewModel for the refill control state and commands. */
    abstract val refillViewModel: RefillViewModel
        /** ViewModel for the refill history log. */
    abstract val refillHistoryViewModel: RefillHistoryViewModel

        /** ViewModel for the general actuator schedule. */
    abstract val scheduleViewModel: ScheduleViewModel

        /** ViewModel for the dashboard summary overview. */
    abstract val summaryViewModel: SummaryViewModel

        /** ViewModel for the real-time sensor telemetry charts. */
    abstract val timeDataViewModel: TimeDataViewModel

        /** ViewModel for managing ventilation temperature thresholds. */
    abstract val ventilationSetValsViewModel: VentilationSetValsViewModel

        /** The global manager for active session and controller selection. */
    abstract val sessionManager: SessionManager

        /** The repository responsible for background summary data synchronization. */
    abstract val summaryRepository: SummaryRepository
}
