/*
 *  Copyright 2026 Uwe Martin
 *
 *  Permission is hereby granted, free of charge, to any person obtaining a copy
 *  of this software and associated documentation files (the "Software"), to deal
 *  in the Software without restriction, including without limitation the rights
 *  to use, copy, modify, merge, publish, distribute, sublicense, and/or sell
 *  copies of the Software, and to permit persons to whom the Software is
 *  furnished to do so, subject to the following conditions:
 *
 *  The above copyright notice and this permission notice shall be included in all
 *  copies or substantial portions of the Software.
 *
 * THE SOFTWARE IS PROVIDED "AS IS", WITHOUT WARRANTY OF ANY KIND, EXPRESS OR
 * IMPLIED, INCLUDING BUT NOT LIMITED TO THE WARRANTIES OF MERCHANTABILITY,
 * FITNESS FOR A PARTICULAR PURPOSE AND NONINFRINGEMENT. IN NO EVENT SHALL THE
 * AUTHORS OR COPYRIGHT HOLDERS BE LIABLE FOR ANY CLAIM, DAMAGES OR OTHER
 * LIABILITY, WHETHER IN AN ACTION OF CONTRACT, TORT OR OTHERWISE, ARISING FROM,
 * OUT OF OR IN CONNECTION WITH THE SOFTWARE OR THE USE OR OTHER DEALINGS IN THE
 * SOFTWARE.
 */

package com.laimburggasse.aquariumcontrol

 import android.app.Application
 import com.laimburggasse.aquariumcontrol.di.AppComponent
 import com.laimburggasse.aquariumcontrol.di.create
 import com.laimburggasse.aquariumcontrol.summary.domain.repository.SummaryRepository
 import kotlinx.coroutines.CoroutineScope
 import kotlinx.coroutines.delay
 import kotlinx.coroutines.isActive
 import kotlinx.coroutines.launch

 class AquariumControlApplication : Application() {

     lateinit var appComponent: AppComponent

     private val applicationScope: CoroutineScope by lazy { appComponent.provideApplicationScope() }

     private val summaryRepository: SummaryRepository by lazy { appComponent.summaryRepository }

     override fun onCreate() {
         super.onCreate()
         appComponent = AppComponent::class.create(this)
         startSummarySyncTask()
     }

     private fun startSummarySyncTask() {
         applicationScope.launch {
             while (isActive) {
                 try {
                     summaryRepository.fetchSummaryData()
                 } catch (_: Exception) {
                     // Log error if necessary
                 }
                 delay(5 * 60 * 1000) // 5 minutes
             }
         }
     }
 }