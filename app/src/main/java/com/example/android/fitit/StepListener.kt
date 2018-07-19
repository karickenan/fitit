package com.example.android.fitit

// Will listen to step alerts
interface StepListener {
    fun step(timeNs: Long)
}
