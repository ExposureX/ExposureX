package com.example.exposurex

import android.graphics.Bitmap
import android.graphics.Color

object DoseCalculator {

    // Reference scale: color -> (label, dose%)
    // Placeholder values — replace with real calibrated strip colors later
    private val referenceScale = listOf(
        Triple(Color.rgb(255, 250, 240), "Safe", 0),
        Triple(Color.rgb(255, 230, 180), "Low", 25),
        Triple(Color.rgb(230, 190, 130), "Moderate", 50),
        Triple(Color.rgb(180, 130, 90), "High", 75),
        Triple(Color.rgb(100, 70, 50), "Critical", 100)
    )

    // Samples the average color from the center of the captured image
    fun getAverageColor(bitmap: Bitmap): Int {
        val centerX = bitmap.width / 2
        val centerY = bitmap.height / 2
        val sampleSize = 50

        var totalR = 0L; var totalG = 0L; var totalB = 0L
        var count = 0

        for (x in (centerX - sampleSize / 2) until (centerX + sampleSize / 2)) {
            for (y in (centerY - sampleSize / 2) until (centerY + sampleSize / 2)) {
                if (x in 0 until bitmap.width && y in 0 until bitmap.height) {
                    val pixel = bitmap.getPixel(x, y)
                    totalR += Color.red(pixel)
                    totalG += Color.green(pixel)
                    totalB += Color.blue(pixel)
                    count++
                }
            }
        }
        return Color.rgb((totalR / count).toInt(), (totalG / count).toInt(), (totalB / count).toInt())
    }

    // Finds closest matching dose level for the sampled color
    fun matchDose(sampledColor: Int): Pair<String, Int> {
        var closestLabel = "Unknown"
        var closestValue = 0
        var smallestDistance = Double.MAX_VALUE

        for ((refColor, label, value) in referenceScale) {
            val distance = colorDistance(sampledColor, refColor)
            if (distance < smallestDistance) {
                smallestDistance = distance
                closestLabel = label
                closestValue = value
            }
        }
        return Pair(closestLabel, closestValue)
    }

    private fun colorDistance(c1: Int, c2: Int): Double {
        val rDiff = Color.red(c1) - Color.red(c2)
        val gDiff = Color.green(c1) - Color.green(c2)
        val bDiff = Color.blue(c1) - Color.blue(c2)
        return Math.sqrt((rDiff * rDiff + gDiff * gDiff + bDiff * bDiff).toDouble())
    }
}