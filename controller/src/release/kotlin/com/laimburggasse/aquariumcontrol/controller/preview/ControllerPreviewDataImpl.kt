/*
 *  Copyright 2025 Uwe Martin
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

package com.laimburggasse.aquariumcontrol.controller.preview

import com.laimburggasse.aquariumcontrol.common.domain.ControllerProfile
import com.laimburggasse.aquariumcontrol.controller.presentation.components.ControllerAddEditUiState
import com.laimburggasse.aquariumcontrol.controller.presentation.components.ControllerPreviewData

object ControllerPreviewDataImpl: ControllerPreviewData {
    override val mockControllerState = ControllerAddEditUiState(
        controllerProfileAddress = "Release build",
        controllerProfilePort = "0",
    )

    override val mockControllerEditState = ControllerAddEditUiState(
        controllerProfileName = "Release build",
        controllerProfileAddress = "0.0.0.0",
        controllerProfilePort = "0",
        controllerProfileUser = "dummy",
        controllerProfilePassword = "dummy",
    )

    override val mockControllerProfile1 = ControllerProfile(
        id = 0,
        name = "Release build",
        address = "0.0.0.0",
        port = 0,
        user = "dummy",
        password = "dummy",
    )
    override val mockControllerProfile2 = mockControllerProfile1

    override val mockControllerProfileList: List<ControllerProfile> = emptyList()
}