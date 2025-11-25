package com.example.altitudechangescalculator

import android.hardware.Sensor
import android.hardware.SensorEvent
import android.hardware.SensorEventListener
import android.hardware.SensorManager
import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.unit.dp
import com.example.altitudechangescalculator.ui.theme.AltitudeChangesCalculatorTheme
import kotlin.math.pow

class MainActivity : ComponentActivity(), SensorEventListener {

    // Handles sensor interactions
    private lateinit var sensorManager: SensorManager
    private var pressureSensor: Sensor? = null

    // Holds current pressure value (mutable state for Compose updates)
    private var _pressure by mutableFloatStateOf(1013.25f) // default sea-level pressure
    private var simulatePressure by mutableStateOf(false)

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        // Initialize sensor manager and get pressure sensor if available
        sensorManager = getSystemService(SENSOR_SERVICE) as SensorManager
        pressureSensor = sensorManager.getDefaultSensor(Sensor.TYPE_PRESSURE)

        // Set Compose UI, pass pressure and toggles to screen
        setContent {
            AltitudeChangesCalculatorTheme {
                AltitudeScreen(
                    pressure = _pressure,
                    onSimulatedPressureChange = { _pressure = it },
                    simulateMode = simulatePressure,
                    toggleSim = { simulatePressure = !simulatePressure }
                )
            }
        }
    }

    override fun onResume() {
        super.onResume()
        // Register for sensor updates if not simulating
        if (!simulatePressure) {
            pressureSensor?.let {
                sensorManager.registerListener(this, it, SensorManager.SENSOR_DELAY_NORMAL)
            }
        }
    }

    override fun onPause() {
        super.onPause()
        // Always unregister to avoid sensor leaks
        sensorManager.unregisterListener(this)
    }

    override fun onSensorChanged(event: SensorEvent?) {
        // Update pressure state whenever a new value arrives (if not simulating)
        if (!simulatePressure) {
            event?.let {
                _pressure = it.values[0]
            }
        }
    }

    // Unused, required method for SensorEventListener
    override fun onAccuracyChanged(sensor: Sensor?, accuracy: Int) {}
}

/* Composable function to show altitude and controls.
* 1) pressure Current barometric pressure in hPa.
* 2) onSimulatedPressureChange Callback when simulation slider moves.
* 3) simulateMode Whether simulation UI is active.
* 4) toggleSim Toggles simulation mode.
*/
@Composable
fun AltitudeScreen(
    pressure: Float,
    onSimulatedPressureChange: (Float) -> Unit,
    simulateMode: Boolean,
    toggleSim: () -> Unit
) {
    // Altitude formula
    val P0 = 1013.25f
    val altitude = 44330f * (1f - (pressure / P0).pow(1f / 5.255f))

    // Dynamically darken background based on altitude for visual effect
    val backgroundDarkness = (altitude / 5000f).coerceIn(0f, 1f)
    val bgColor = Color(
        red = (1f - backgroundDarkness),
        green = (1f - backgroundDarkness),
        blue = 1f
    )

    Column(
        modifier = Modifier
            .fillMaxSize()
            .background(bgColor) // Set background color based on calculated altitude
            .padding(20.dp),
        horizontalAlignment = Alignment.CenterHorizontally,
        verticalArrangement = Arrangement.Center
    ) {
        Text("Altitude Calculator", style = MaterialTheme.typography.headlineMedium)
        Spacer(modifier = Modifier.height(20.dp))

        // Show current pressure and calculated altitude
        Text("Pressure: ${"%.2f".format(pressure)} hPa", style = MaterialTheme.typography.bodyLarge)
        Text("Altitude: ${"%.2f".format(altitude)} meters", style = MaterialTheme.typography.bodyLarge)

        Spacer(modifier = Modifier.height(20.dp))

        // Button toggles between real and simulated pressure modes
        Button(onClick = toggleSim) {
            Text(if (simulateMode) "Disable Simulation" else "Enable Simulation")
        }

        // Show slider to adjust simulated pressure if in simulation mode
        if (simulateMode) {
            Spacer(modifier = Modifier.height(20.dp))
            Text("Simulate Pressure")
            Slider(
                value = pressure,
                valueRange = 800f..1050f,
                onValueChange = { onSimulatedPressureChange(it) },
                modifier = Modifier.fillMaxWidth()
            )
        }
    }
}