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

package com.laimburggasse.aquariumcontrol.common

import android.content.Context
import android.graphics.Bitmap
import android.graphics.BitmapFactory
import androidx.compose.ui.graphics.ImageBitmap
import androidx.compose.ui.graphics.asAndroidBitmap
import androidx.test.platform.app.InstrumentationRegistry
import java.io.File
import java.io.FileNotFoundException
import java.io.FileOutputStream
import java.io.IOException

object ScreenshotTestHelper {
    private const val GOLDEN_IMAGE_ASSETS_PATH = "assets"

    fun getDeviceOutputDirectory(): File {
        val context = InstrumentationRegistry.getInstrumentation().targetContext
        val dir = File(context.filesDir, "screenshots_output")

        if (!dir.exists()) {
            dir.mkdirs()
        }
        println("***** Recording to $dir *****")
        return dir
    }

    fun recordBitmap(bitmap: ImageBitmap, goldenName: String) {
        val outputFile = File(getDeviceOutputDirectory(), "$goldenName.png")
        try {
            FileOutputStream(outputFile).use { out ->
                bitmap.asAndroidBitmap().compress(Bitmap.CompressFormat.PNG, 100, out)
                println("📸 Screenshot recorded: ${outputFile.absolutePath}")
                println("❗ IMPORTANT: Manually copy this to src/androidTest/assets/$GOLDEN_IMAGE_ASSETS_PATH/$goldenName.png for future verification runs.")
            }
        } catch (e: Exception) {
            throw RuntimeException("Failed to save bitmap for $goldenName", e)
        }
    }

    /**
     * Checks if a given golden image asset exists within the test APK's assets.
     * @return True if the file exists, false otherwise.
     */
    private fun goldenImageExists(context: Context, goldenFileRelativePath: String): Boolean {
        return try {
            // The most reliable way to check for existence is to try to open it.
            // We must close it immediately to free up resources.
            context.assets.open(goldenFileRelativePath).close()
            println("✅ Golden image found at: $goldenFileRelativePath")
            true
        } catch (e: IOException) {
            // IOException (including FileNotFoundException) means the asset is not there.
            println("🚨 Golden image NOT FOUND at: $goldenFileRelativePath ({${e.message}})")
            false
        }
    }

    fun verifyBitmap(
        capturedBitmap: ImageBitmap,
        subfolder: String,
        goldenName: String
    ): Boolean {
        val context: Context = InstrumentationRegistry.getInstrumentation().context
        val goldenFileRelativePath = "screenshots/$subfolder/$goldenName.png"

        // Check  for the golden image's existence first.
        if (!goldenImageExists(context, goldenFileRelativePath)) {
            println("VERIFICATION FAILED: Golden image is missing. If this is a new test, set recordMode=true to create the reference image.")
            // Save the captured bitmap for easy debugging and copying.
            recordBitmap(capturedBitmap, goldenName)
            return false // Fail the test immediately.
        }

        try {
            val apkPath = context.packageResourcePath
            println("▶️ verifyBitmap: Attempting to open asset from APK: $apkPath")
            println("▶️ verifyBitmap: Using relative asset path: $goldenFileRelativePath")
            context.assets.open(goldenFileRelativePath).use { inputStream ->
                val goldenBitmap = BitmapFactory.decodeStream(inputStream)
                val capturedAndroidBitmap = capturedBitmap.asAndroidBitmap()

                if (goldenBitmap.width != capturedAndroidBitmap.width || goldenBitmap.height != capturedAndroidBitmap.height) {
                    println("▶️ verifyBitmap: Bitmap dimensions differ for $goldenName.")
                    saveDebugBitmaps(capturedAndroidBitmap, goldenBitmap, goldenName)
                    return false
                }
                for (x in 0 until goldenBitmap.width) {
                    for (y in 0 until goldenBitmap.height) {
                        if (goldenBitmap.getPixel(x, y) != capturedAndroidBitmap.getPixel(x, y)) {
                            println("▶️ verifyBitmap: Pixel mismatch at ($x, $y) for $goldenName.")
                            saveDebugBitmaps(capturedAndroidBitmap, goldenBitmap, goldenName)
                            return false
                        }
                    }
                }
                println("✅ verifyBitmap: Golden image matches for $goldenName")
                return true
            }
        } catch (_: FileNotFoundException) {
            println("🚨 verifyBitmap: Golden image not found: $goldenFileRelativePath. If this is the first run or a new test, set recordMode=true to create it.")
            return false // Fail the test if golden is missing in verify mode
        } catch (e: Exception) {
            println("verifyBitmap: Could not load or compare golden image $goldenName from assets. ${e.message}")
            return false
        }
    }

    private fun saveDebugBitmaps(captured: Bitmap, golden: Bitmap?, goldenName: String) {
        val debugDir = File(getDeviceOutputDirectory(), "debug_diffs")
        if (!debugDir.exists()) debugDir.mkdirs()
        try {
            FileOutputStream(File(debugDir, "${goldenName}_captured.png")).use { out ->
                captured.compress(Bitmap.CompressFormat.PNG, 100, out)
            }
            golden?.let {
                FileOutputStream(File(debugDir, "${goldenName}_golden.png")).use { out ->
                    it.compress(Bitmap.CompressFormat.PNG, 100, out)
                }
            }
            println("Saved debug bitmaps for $goldenName to ${debugDir.absolutePath}")
        } catch (e: Exception) {
            println("Error saving debug bitmaps: ${e.message}")
        }
    }

    fun performScreenshotTest(
        goldenName: String,
        recordMode: Boolean,
        subfolder: String,
        imageBitmap: ImageBitmap
    ) {
        if (recordMode) {
            recordBitmap(imageBitmap, goldenName)
            // After recording, we might want to fail the test to remind the user to copy the file
            // and switch back to verify mode, or handle this in a more sophisticated way.
            // For this example, we'll let it pass but print a strong reminder.
            println("RECORD MODE: Screenshot for '$goldenName' captured. Ensure it's copied to assets and switch recordMode to false.")
        } else {

            val matches: Boolean = verifyBitmap(
                capturedBitmap = imageBitmap,
                subfolder,
                goldenName
            )

            assert(value = matches) {
                "Screenshot '$goldenName' does not match the golden image. " +
                "If this is an intentional change, re-record the screenshot by setting recordMode=true."
            }
        }
    }
}