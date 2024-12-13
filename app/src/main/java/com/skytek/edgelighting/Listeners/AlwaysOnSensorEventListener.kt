package com.skytek.edgelighting.Listeners

import android.hardware.Sensor
import android.hardware.SensorEvent
import android.hardware.SensorEventListener
import com.skytek.edgelighting.views.AlwaysOnViewHolder

class AlwaysOnSensorEventListener(private val viewHolder: AlwaysOnViewHolder) :
    SensorEventListener {

    override fun onSensorChanged(event: SensorEvent) {
        if (event.sensor.type == Sensor.TYPE_PROXIMITY) {
            if (event.values[0] == event.sensor.maximumRange) {
                viewHolder.edgeLightingView.animate().alpha(1F).duration = 200L
            } else {
                viewHolder.edgeLightingView.animate().alpha(0F).duration = 200L
            }
        }
    }

    override fun onAccuracyChanged(sensor: Sensor?, accuracy: Int) {}
}
