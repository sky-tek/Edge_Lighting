package com.skytek.edgelighting.views

import android.app.Activity
import android.widget.RelativeLayout
import android.widget.TextClock
import android.widget.TextView
import com.skytek.edgelighting.R

class AlwaysOnViewHolder(activity: Activity) {

    @JvmField
    val edgeLightingView: EdgeLightingView = activity.findViewById(R.id.edge_light_view)

    @JvmField
    val rl: RelativeLayout = activity.findViewById(R.id.rl)

//    @JvmField
//    val time: TextView = activity.findViewById(R.id.time)

    @JvmField
    val date: TextView = activity.findViewById(R.id.date)

    @JvmField
    val time: TextClock = activity.findViewById(R.id.time)

//    @JvmField
//    val frame: CustomFrameLayout = activity.findViewById(R.id.frame)
//
//    @JvmField
//    val customView: AlwaysOnCustomView = activity.findViewById(R.id.customView)
//
//    @JvmField
//    val fingerprintIcn: FingerprintView = activity.findViewById(R.id.fingerprintIcn)
}
