package com.example.sensors

import android.content.Context
import android.hardware.Sensor
import android.hardware.SensorEvent
import android.hardware.SensorEventListener
import android.hardware.SensorManager
import androidx.compose.runtime.Composable
import androidx.compose.runtime.DisposableEffect
import androidx.compose.runtime.Stable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableFloatStateOf
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.runtime.setValue
import androidx.compose.ui.platform.LocalContext
import kotlinx.coroutines.Job
import kotlinx.coroutines.delay
import kotlinx.coroutines.isActive
import kotlinx.coroutines.launch
import kotlin.math.sin

/**
 * Manages device orientation tilt telemetry via Accelerometer / Gyroscope / Rotation Vector
 * with exponential smoothing, plus interactive touch drag and gentle orbit simulation mode.
 */
@Stable
class TiltState(
    initialPitch: Float = 0f,
    initialRoll: Float = 0f
) {
    // Pitch: vertical tilt (-1f = tilted down, +1f = tilted up)
    var pitch by mutableFloatStateOf(initialPitch)
        internal set

    // Roll: horizontal tilt (-1f = tilted left, +1f = tilted right)
    var roll by mutableFloatStateOf(initialRoll)
        internal set

    var isSensorAvailable by mutableStateOf(false)
        internal set

    var isSimulatedMode by mutableStateOf(false)
        internal set

    fun setManualTilt(targetPitch: Float, targetRoll: Float) {
        pitch = targetPitch.coerceIn(-1.5f, 1.5f)
        roll = targetRoll.coerceIn(-1.5f, 1.5f)
    }

    fun resetTilt() {
        pitch = 0f
        roll = 0f
    }
}

@Composable
fun rememberTiltState(): TiltState {
    val context = LocalContext.current
    val tiltState = remember { TiltState() }
    val coroutineScope = rememberCoroutineScope()

    DisposableEffect(context) {
        val sensorManager = context.getSystemService(Context.SENSOR_SERVICE) as? SensorManager
        var simulationJob: Job? = null

        // Try rotation vector first (most stable), then fallback to accelerometer
        val rotationSensor = sensorManager?.getDefaultSensor(Sensor.TYPE_ROTATION_VECTOR)
        val accelSensor = sensorManager?.getDefaultSensor(Sensor.TYPE_ACCELEROMETER)
        val activeSensor = rotationSensor ?: accelSensor

        tiltState.isSensorAvailable = (activeSensor != null)

        val rotationMatrix = FloatArray(9)
        val orientationValues = FloatArray(3)

        // Smoothing factor for sensor values (exponential moving average)
        val alpha = 0.12f
        var smoothedPitch = 0f
        var smoothedRoll = 0f

        val listener = object : SensorEventListener {
            override fun onSensorChanged(event: SensorEvent) {
                if (tiltState.isSimulatedMode) return

                if (event.sensor.type == Sensor.TYPE_ROTATION_VECTOR) {
                    SensorManager.getRotationMatrixFromVector(rotationMatrix, event.values)
                    SensorManager.getOrientation(rotationMatrix, orientationValues)

                    // orientationValues[1] is pitch (-pi to pi), [2] is roll (-pi/2 to pi/2)
                    // Normalize standard holding angle (approx 35-45 degrees tilt towards user)
                    val rawPitch = ((orientationValues[1] + 0.65f) * 1.6f).coerceIn(-1f, 1f)
                    val rawRoll = (orientationValues[2] * 1.8f).coerceIn(-1f, 1f)

                    smoothedPitch += alpha * (rawPitch - smoothedPitch)
                    smoothedRoll += alpha * (rawRoll - smoothedRoll)

                    tiltState.pitch = smoothedPitch
                    tiltState.roll = smoothedRoll
                } else if (event.sensor.type == Sensor.TYPE_ACCELEROMETER) {
                    // event.values[0] is X axis (-9.8 to +9.8), [1] is Y axis, [2] is Z axis
                    val rawRoll = (-event.values[0] / 6.5f).coerceIn(-1f, 1f)
                    val rawPitch = ((event.values[1] - 4.5f) / 6.5f).coerceIn(-1f, 1f)

                    smoothedPitch += alpha * (rawPitch - smoothedPitch)
                    smoothedRoll += alpha * (rawRoll - smoothedRoll)

                    tiltState.pitch = smoothedPitch
                    tiltState.roll = smoothedRoll
                }
            }

            override fun onAccuracyChanged(sensor: Sensor?, accuracy: Int) {}
        }

        if (activeSensor != null) {
            sensorManager?.registerListener(
                listener,
                activeSensor,
                SensorManager.SENSOR_DELAY_GAME
            )
        } else {
            // No hardware sensor found (e.g. standard desktop emulator without sensor injection)
            // Start gentle ambient orbital wave so the 3D parallax effects are naturally demonstrated!
            tiltState.isSimulatedMode = true
        }

        // Ambient simulation coroutine if hardware sensor is absent or simulation is enabled
        fun startAmbientSimulation() {
            simulationJob?.cancel()
            simulationJob = coroutineScope.launch {
                var elapsed = 0f
                while (isActive) {
                    if (tiltState.isSimulatedMode) {
                        elapsed += 0.035f
                        val simPitch = (sin(elapsed * 0.8) * 0.45f).toFloat()
                        val simRoll = (sin(elapsed * 1.1) * 0.55f).toFloat()
                        tiltState.pitch = simPitch
                        tiltState.roll = simRoll
                    }
                    delay(16)
                }
            }
        }

        startAmbientSimulation()

        onDispose {
            sensorManager?.unregisterListener(listener)
            simulationJob?.cancel()
        }
    }

    return tiltState
}
