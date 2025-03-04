package com.example.cs501compassdigitallevel

import android.hardware.Sensor
import android.hardware.SensorEvent
import android.hardware.SensorEventListener
import android.hardware.SensorManager
import android.os.Bundle
import androidx.compose.ui.Modifier
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.compose.foundation.layout.Column
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.setValue
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.foundation.Image
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.ui.Alignment
import androidx.compose.ui.draw.rotate
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.unit.dp
import com.example.cs501compassdigitallevel.ui.theme.CS501CompassDigitalLevelTheme

class MainActivity : ComponentActivity(), SensorEventListener {

    private lateinit var sensorManager: SensorManager
    private var accelerometer: Sensor? = null
    private var magnetometer: Sensor? = null
    private var gyroscope: Sensor? = null

    // for accelerometer
    private var _x by mutableStateOf(0f)
    private var _y by mutableStateOf(0f)
    private var _z by mutableStateOf(0f)

    // for magnetometer
    private var _a by mutableStateOf(0f)
    private var _b by mutableStateOf(0f)
    private var _c by mutableStateOf(0f)

    // for gyroscope
    private var _r by mutableStateOf(0f)
    private var _s by mutableStateOf(0f)
    private var _t by mutableStateOf(0f)

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        // initialize sensor manager
        sensorManager = getSystemService(SENSOR_SERVICE) as SensorManager
        magnetometer = sensorManager.getDefaultSensor(Sensor.TYPE_MAGNETIC_FIELD)
        accelerometer = sensorManager.getDefaultSensor(Sensor.TYPE_ACCELEROMETER)
        gyroscope = sensorManager.getDefaultSensor(Sensor.TYPE_GYROSCOPE)

        setContent {
            CS501CompassDigitalLevelTheme {
                CompassScreen(x = _x, y = _y, z = _z, a = _a, b = _b, c = _c, r = _r, s = _s, t = _t)
            }
        }
    }

    // register the accelerometer sensor when the app starts (onResume).
    override fun onResume() {
        super.onResume()
        accelerometer?.let {
            sensorManager.registerListener(this, it, SensorManager.SENSOR_DELAY_UI)
        }
        magnetometer?.let {
            sensorManager.registerListener(this, it, SensorManager.SENSOR_DELAY_UI)
        }
        gyroscope?.let {
            sensorManager.registerListener(this, it, SensorManager.SENSOR_DELAY_UI)
        }
    }

    // unregister the sensor when the app is paused (onPause) to save battery
    override fun onPause() {
        super.onPause()
        sensorManager.unregisterListener(this)
    }

    // update accelerometer and magnetometer values when they change
    override fun onSensorChanged(event: SensorEvent?) {
        if (event != null) {
            when (event.sensor.type) {
                // if the event is magnetometer changing
                Sensor.TYPE_MAGNETIC_FIELD -> {
                    event.let {
                        _a = it.values[0]
                        _b = it.values[1]
                        _c = it.values[2]
                    }
                }
                // if the event is accelerometer changing
                Sensor.TYPE_ACCELEROMETER -> {
                    event.let {
                        _x = it.values[0]
                        _y = it.values[1]
                        _z = it.values[2]
                    }
                }
                // if the event is gyroscope changing
                Sensor.TYPE_GYROSCOPE -> {
                    event.let {
                        _r = it.values[0]
                        _s = it.values[1]
                        _t = it.values[2]
                    }
                }
            }
        }
    }

    override fun onAccuracyChanged(sensor: Sensor?, accuracy: Int) {
        // not needed for this example but needed for implementing members
    }
}

@Composable
fun CompassScreen(x: Float, y: Float, z: Float, a: Float, b: Float, c: Float, r: Float, s: Float, t: Float) {
    val identityMatrix = FloatArray(9)
    val rotationMatrix = FloatArray(9)
    val accelerometerValues: FloatArray = floatArrayOf(x, y, z)
    val magnetometerValues: FloatArray = floatArrayOf(a, b, c)
    var rotationInDegrees = 0.0

    val success = SensorManager.getRotationMatrix(rotationMatrix, identityMatrix, accelerometerValues, magnetometerValues)

    if (success) {
        // get rotation in degrees
        val orientationMatrix = FloatArray(3)
        SensorManager.getOrientation(rotationMatrix, orientationMatrix)
        val rotationInRadians = orientationMatrix[0]
        rotationInDegrees = Math.toDegrees(rotationInRadians.toDouble())
    }

    Column(
        modifier = Modifier
            .fillMaxSize()
            .padding(16.dp),
        horizontalAlignment = Alignment.CenterHorizontally,
        verticalArrangement = Arrangement.Center
    ) {
        Text(text = rotationInDegrees.toString())
        Text(text = "gyroscope values") // need to use these for the rotation matrix
        Text(text = r.toString())
        Text(text = s.toString())
        Text(text = t.toString())

        // representing the compass using an arrow (arrow head will be considered north)
        Image(
            painter = painterResource(id = R.drawable.arrow),
            contentDescription = null,
            modifier = Modifier
                .rotate(rotationInDegrees.toFloat())
        )
    }
}

@Preview(showBackground = true)
@Composable
fun GreetingPreview() {
    CS501CompassDigitalLevelTheme {
        CompassScreen(x = 0f, y = 0f, z = 0f, a = 0f, b = 0f, c = 0f, r = 0f, s = 0f, t = 0f)
    }
}