package com.example.android.fitit.view

import android.content.Context
import android.hardware.Sensor
import android.hardware.SensorEvent
import android.hardware.SensorEventListener
import android.hardware.SensorManager
import android.os.Bundle
import android.support.v7.app.AppCompatActivity
import com.example.android.fitit.StepDetector
import com.example.android.fitit.StepListener
import com.github.thunder413.datetimeutils.DateTimeUtils
import kotlinx.android.synthetic.main.activity_navigation.*
import org.jetbrains.anko.sdk25.coroutines.onClick
import org.jetbrains.anko.startActivity

class NavigationActivity : AppCompatActivity(), SensorEventListener, StepListener {

    private var fitnessList = ArrayList<String>()
    private lateinit var simpleStepDetector: StepDetector
    private lateinit var sensorManager: SensorManager
    private var accel: Sensor? = null
    private val TEXT_NUM_STEPS = "Number of Steps: "
    private var numSteps: Int = 0

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_navigation)

        fitnessList = intent.getStringArrayListExtra("fitnessType")

        // Get an instance of the SensorManager
        sensorManager = getSystemService(Context.SENSOR_SERVICE) as SensorManager
        accel = sensorManager.getDefaultSensor(Sensor.TYPE_ACCELEROMETER)
        simpleStepDetector = StepDetector()
        simpleStepDetector.registerListener(this)
        counter_current_steps.text = "0"

        counter_begin_button.setOnClickListener {
            if (counter_begin_button.isChecked) {
                numSteps = 0
                sensorManager.registerListener(this@NavigationActivity, accel, SensorManager.SENSOR_DELAY_FASTEST)
            } else
                sensorManager.unregisterListener(this@NavigationActivity)
        }

        profileIntent_ImageView.onClick {
            startActivity<ProfileActivity>(
                    "fitnessDataProfile" to fitnessList
            )
        }
    }

    override fun onAccuracyChanged(p0: Sensor?, p1: Int) {}

    override fun onSensorChanged(sensorEvent: SensorEvent?) {
        if (sensorEvent?.sensor?.type == Sensor.TYPE_ACCELEROMETER) simpleStepDetector.updateAccel(
                sensorEvent.timestamp, sensorEvent.values[0], sensorEvent.values[1], sensorEvent.values[2])
    }

    override fun step(timeNs: Long) {
        numSteps++

        counter_current_steps.text = "$numSteps"
        counter_distance_textView.text = "${numSteps * 0.71}"
        counter_calories_textView.text = "${numSteps * 0.03}"

        val date = DateTimeUtils.formatDate(timeNs)
        val styledDate = DateTimeUtils.formatWithPattern(date, "ss")
        counter_time_textView.text = styledDate
    }
}
