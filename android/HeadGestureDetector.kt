package org.librepods.android.sensors

import kotlin.math.abs

class HeadGestureDetector(
    private var pitchThreshold: Float = 1.5f, // Nod threshold (up/down)
    private var rollThreshold: Float = 1.8f  // Shake threshold (left/right)
) {
    private var lastPitch: Float = 0f
    private var lastRoll: Float = 0f
    private var nodCount = 0
    private var shakeCount = 0
    private var lastGestureTimestamp: Long = 0

    enum class GestureType {
        NONE, NOD_YES, SHAKE_NO
    }

    fun processSensorData(pitch: Float, roll: Float): GestureType {
        val currentTime = System.currentTimeMillis()
        
        // Reset counters if too much time has passed (timeout 1.5 seconds)
        if (currentTime - lastGestureTimestamp > 1500) {
            nodCount = 0
            shakeCount = 0
        }

        val pitchDelta = pitch - lastPitch
        val rollDelta = roll - lastRoll

        // Detect Nod (Pitch Axis - Vertical movement)
        if (abs(pitchDelta) > pitchThreshold) {
            nodCount++
            lastGestureTimestamp = currentTime
            lastPitch = pitch
        }

        // Detect Shake (Roll/Yaw Axis - Horizontal movement)
        if (abs(rollDelta) > rollThreshold) {
            shakeCount++
            lastGestureTimestamp = currentTime
            lastRoll = roll
        }

        // Evaluate triggers
        return when {
            nodCount >= 2 -> {
                nodCount = 0
                GestureType.NOD_YES
            }
            shakeCount >= 2 -> {
                shakeCount = 0
                GestureType.SHAKE_NO
            }
            else -> GestureType.NONE
        }
    }
}
