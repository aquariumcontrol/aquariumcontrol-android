/*
 * Copyright 2025 Uwe Martin
 *
 * Permission is hereby granted, free of charge, to any person obtaining a copy
 * of this software and associated documentation files (the "Software"), to deal
 * in the Software without restriction, including without limitation the rights
 * to use, copy, modify, merge, publish, distribute, sublicense, and/or sell
 * copies of the Software, and to permit persons to whom the Software is
 * furnished to do so, subject to the following conditions:
 *
 * The above copyright notice and this permission notice shall be included in all
 * copies or substantial portions of the Software.
 *
 * THE SOFTWARE IS PROVIDED "AS IS", WITHOUT WARRANTY OF ANY KIND, EXPRESS OR
 * IMPLIED, INCLUDING BUT NOT LIMITED TO THE WARRANTIES OF MERCHANTABILITY,
 * FITNESS FOR A PARTICULAR PURPOSE AND NONINFRINGEMENT. IN NO EVENT SHALL THE
 * AUTHORS OR COPYRIGHT HOLDERS BE LIABLE FOR ANY CLAIM, DAMAGES OR OTHER
 * LIABILITY, WHETHER IN AN ACTION OF CONTRACT, TORT OR OTHERWISE, ARISING FROM,
 * OUT OF OR IN CONNECTION WITH THE SOFTWARE OR THE USE OR OTHER DEALINGS IN THE
 * SOFTWARE.
 */

package com.laimburggasse.aquariumcontrol.common.presentation.maindropdownmenu.presentation

import androidx.navigation.NavHostController
import com.laimburggasse.aquariumcontrol.common.domain.SessionManager
import com.laimburggasse.aquariumcontrol.common.presentation.ViewNavigationRoutes
import kotlinx.coroutines.flow.StateFlow

fun NavHostController.handleMainDropDownMenuNavigation(
    event: MainDropDownMenuNavigationEvent,
    sessionManager: SessionManager
) {
    when (event) {
        MainDropDownMenuNavigationEvent.ToOverview -> {
            val currentProfileId: StateFlow<Long?> = sessionManager.defaultControllerProfileId
            if (currentProfileId.value != null) {
                this.navigate(route = ViewNavigationRoutes.overView(controllerProfileId = currentProfileId.value!!))
            }
        }
        MainDropDownMenuNavigationEvent.ToSchedule -> this.navigate(route = ViewNavigationRoutes.schedule)
        MainDropDownMenuNavigationEvent.ToControllerSelection -> this.navigate(route = ViewNavigationRoutes.controllerSelection)
        MainDropDownMenuNavigationEvent.ToDisplayData -> this.navigate(route = ViewNavigationRoutes.timeData)
        MainDropDownMenuNavigationEvent.ToBalling -> this.navigate(route = ViewNavigationRoutes.balling)
        MainDropDownMenuNavigationEvent.ToRefill -> this.navigate(route = ViewNavigationRoutes.refill)
        MainDropDownMenuNavigationEvent.ToFeed -> this.navigate(route = ViewNavigationRoutes.feed)
        MainDropDownMenuNavigationEvent.ToHeating -> this.navigate(route = ViewNavigationRoutes.heating)
        MainDropDownMenuNavigationEvent.ToVentilation -> this.navigate(route = ViewNavigationRoutes.ventilation)
        MainDropDownMenuNavigationEvent.ToInfo -> this.navigate(route = ViewNavigationRoutes.info)
    }
}