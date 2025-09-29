package com.laimburggasse.aquariumcontrol

import android.graphics.Bitmap
import androidx.compose.ui.graphics.asAndroidBitmap
import androidx.test.platform.app.InstrumentationRegistry
import java.io.File
import java.io.FileOutputStream

object ScreenshotTestHelper {
    private const val GOLDEN_IMAGE_ASSETS_PATH = "screenshots"

    fun getDeviceOutputDirectory(): File {
        val context = InstrumentationRegistry.getInstrumentation().targetContext
        val dir = File(context.filesDir, "screenshots_output")
        if (!dir.exists()) {
            dir.mkdirs()
        }
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
        goldenName: String
    ): Boolean {
        val context = InstrumentationRegistry.getInstrumentation().targetContext
        val goldenFileInAssets = "$GOLDEN_IMAGE_ASSETS_PATH/$goldenName.png"

        try {
            println("Using reference file $goldenFileInAssets for $goldenName")
            context.assets.open(goldenFileInAssets).use { inputStream ->
                val goldenBitmap = android.graphics.BitmapFactory.decodeStream(inputStream)
                val capturedAndroidBitmap = capturedBitmap.asAndroidBitmap()

                if (goldenBitmap.width != capturedAndroidBitmap.width || goldenBitmap.height != capturedAndroidBitmap.height) {
                    println("Bitmap dimensions differ for $goldenName.")
                    saveDebugBitmaps(capturedAndroidBitmap, goldenBitmap, goldenName)
                    return false
                }
                for (x in 0 until goldenBitmap.width) {
                    for (y in 0 until goldenBitmap.height) {
                        if (goldenBitmap.getPixel(x, y) != capturedAndroidBitmap.getPixel(x, y)) {
                            println("Pixel mismatch at ($x, $y) for $goldenName.")
                            saveDebugBitmaps(capturedAndroidBitmap, goldenBitmap, goldenName)
                            return false
                        }
                    }
                }
                return true
            }
        } catch (_: java.io.FileNotFoundException) {
            println("🚨 Golden image not found: $goldenFileInAssets. If this is the first run or a new test, set recordMode=true to create it.")
            return false // Fail the test if golden is missing in verify mode
        } catch (e: Exception) {
            println("Could not load or compare golden image $goldenName from assets. ${e.message}")
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
}