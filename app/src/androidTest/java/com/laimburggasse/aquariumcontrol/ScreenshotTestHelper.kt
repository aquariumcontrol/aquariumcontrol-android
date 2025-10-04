package com.laimburggasse.aquariumcontrol

import android.graphics.Bitmap
import androidx.compose.runtime.Composable
import androidx.compose.ui.graphics.ImageBitmap
import androidx.compose.ui.graphics.asAndroidBitmap
import androidx.compose.ui.test.captureToImage
import androidx.compose.ui.test.junit4.createComposeRule
import androidx.compose.ui.test.onRoot
import androidx.test.platform.app.InstrumentationRegistry
import org.junit.Rule
import java.io.File
import java.io.FileOutputStream

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

    fun recordBitmap(bitmap: androidx.compose.ui.graphics.ImageBitmap, goldenName: String) {
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

    fun verifyBitmap(
        capturedBitmap: androidx.compose.ui.graphics.ImageBitmap,
        subfolder: String,
        goldenName: String
    ): Boolean {
        val context = InstrumentationRegistry.getInstrumentation().context
        val goldenFileRelativePath = "screenshots/$subfolder/$goldenName.png"

        try {
            val apkPath = context.packageResourcePath
            println("▶️ verifyBitmap: Attempting to open asset from APK: $apkPath")
            println("▶️ verifyBitmap: Using relative asset path: $goldenFileRelativePath")
            context.assets.open(goldenFileRelativePath).use { inputStream ->
                val goldenBitmap = android.graphics.BitmapFactory.decodeStream(inputStream)
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
        } catch (_: java.io.FileNotFoundException) {
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
            FileOutputStream(File(debugDir,"${goldenName}_captured.png")).use { out ->
                captured.compress(Bitmap.CompressFormat.PNG, 100, out)
            }
            golden?.let {
                FileOutputStream(File(debugDir,"${goldenName}_golden.png")).use { out ->
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
            ScreenshotTestHelper.recordBitmap(imageBitmap, goldenName)
            // After recording, we might want to fail the test to remind the user to copy the file
            // and switch back to verify mode, or handle this in a more sophisticated way.
            // For this example, we'll let it pass but print a strong reminder.
            println("RECORD MODE: Screenshot for '$goldenName' captured. Ensure it's copied to assets and switch recordMode to false.")
        } else {

            val matches = ScreenshotTestHelper.verifyBitmap(
                imageBitmap,
                subfolder,
                goldenName
            )

            assert(matches) {
                "Screenshot '$goldenName' does not match the golden image. " +
                        "If this is an intentional change, re-record the screenshot by setting recordMode=true. " +
                        "Debug images might be in: ${File(ScreenshotTestHelper.getDeviceOutputDirectory(), "debug_diffs").absolutePath}"
            }
        }
    }
}