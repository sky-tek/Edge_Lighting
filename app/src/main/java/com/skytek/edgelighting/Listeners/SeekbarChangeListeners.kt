package com.skytek.edgelighting.Listeners

import android.content.Intent
import android.util.Log
import android.view.View
import android.widget.SeekBar
import com.skytek.edgelighting.MyAccessibilityService
import com.skytek.edgelighting.R
import com.skytek.edgelighting.activities.EdgeOverlaySettingsActivity.Companion.activity
import com.skytek.edgelighting.activities.EdgeOverlaySettingsActivity.Companion.binding
import com.skytek.edgelighting.activities.EdgeOverlaySettingsActivity.Companion.checkBottom
import com.skytek.edgelighting.activities.EdgeOverlaySettingsActivity.Companion.checkNotch
import com.skytek.edgelighting.activities.EdgeOverlaySettingsActivity.Companion.cornerBottom
import com.skytek.edgelighting.activities.EdgeOverlaySettingsActivity.Companion.cornerTop
import com.skytek.edgelighting.activities.EdgeOverlaySettingsActivity.Companion.notchBottom
import com.skytek.edgelighting.activities.EdgeOverlaySettingsActivity.Companion.notchHeight
import com.skytek.edgelighting.activities.EdgeOverlaySettingsActivity.Companion.notchRadiusBottom
import com.skytek.edgelighting.activities.EdgeOverlaySettingsActivity.Companion.notchRadiusTop
import com.skytek.edgelighting.activities.EdgeOverlaySettingsActivity.Companion.notchTop
import com.skytek.edgelighting.activities.EdgeOverlaySettingsActivity.Companion.size
import com.skytek.edgelighting.activities.EdgeOverlaySettingsActivity.Companion.speed
import com.skytek.edgelighting.utils.Const
import com.skytek.edgelighting.utils.MySharePreferencesEdge

class SeekbarChangeListeners : SeekBar.OnSeekBarChangeListener {

    var tag: String? = null

    constructor(tag: String) {
        this.tag = tag
    }

    override fun onProgressChanged(seekBar: SeekBar?, i: Int, p2: Boolean) {
        when (seekBar?.id) {

            R.id.animation_speed -> {
                try {
                    val i2 = i / 2
                    MySharePreferencesEdge.setInt(MySharePreferencesEdge.SPEED, i2, activity)
                    speed = i2
                    if (MyAccessibilityService.edgeLightingView != null &&
                        MySharePreferencesEdge.getAccessibilityEnabled(MySharePreferencesEdge.ACCESSIBILITY_BROADCAST, activity)) {
                        MyAccessibilityService.edgeLightingView?.changeSpeed(speed)
                    } else {
                        binding?.edgeLightView?.changeSpeed(speed)
                    }
                } catch (e: NullPointerException) {
                    Log.e("SeekbarChangeListeners", "NullPointerException in animation_speed", e)
                } catch (e: Exception) {
                    Log.e("SeekbarChangeListeners", "Exception in animation_speed", e)
                }
            }

            R.id.border_size -> {
                try {
                    MySharePreferencesEdge.setInt(MySharePreferencesEdge.SIZE, i, activity)
                    size = i
                    if (MyAccessibilityService.edgeLightingView != null &&
                        MySharePreferencesEdge.getAccessibilityEnabled(MySharePreferencesEdge.ACCESSIBILITY_BROADCAST, activity)) {
                        MyAccessibilityService.edgeLightingView?.changeSize(size)
                    } else {
                        binding?.edgeLightView?.changeSize(size)
                    }
                } catch (e: NullPointerException) {
                    Log.e("SeekbarChangeListeners", "NullPointerException in border_size", e)
                } catch (e: Exception) {
                    Log.e("SeekbarChangeListeners", "Exception in border_size", e)
                }
            }

            R.id.radius_top -> {
                try {
                    MySharePreferencesEdge.setInt(MySharePreferencesEdge.RADIUSTOP, i, activity)
                    cornerTop = i
                    if (MyAccessibilityService.edgeLightingView != null &&
                        MySharePreferencesEdge.getAccessibilityEnabled(MySharePreferencesEdge.ACCESSIBILITY_BROADCAST, activity)) {
                        MyAccessibilityService.edgeLightingView?.changeBorder(cornerTop, cornerBottom)
                    } else {
                        binding?.edgeLightView?.changeBorder(cornerTop, cornerBottom)
                    }
                } catch (e: Exception) {
                    Log.e("SeekbarChangeListeners", "Exception in radius_top", e)
                }
            }

            R.id.radius_bottom -> {
                try {
                    MySharePreferencesEdge.setInt(MySharePreferencesEdge.RADIUSBOTTOM, i, activity)
                    cornerBottom = i
                    if (MyAccessibilityService.edgeLightingView != null &&
                        MySharePreferencesEdge.getAccessibilityEnabled(MySharePreferencesEdge.ACCESSIBILITY_BROADCAST, activity)) {
                        MyAccessibilityService.edgeLightingView?.changeBorder(cornerTop, cornerBottom)
                    } else {
                        binding?.edgeLightView?.changeBorder(cornerTop, cornerBottom)
                    }
                } catch (e: Exception) {
                    Log.e("SeekbarChangeListeners", "Exception in radius_bottom", e)
                }
            }

            R.id.notch_width_top -> {
                try {
                    val i2 = MySharePreferencesEdge.getInt(MySharePreferencesEdge.NOTCHTOP, activity)

                    if (i <= notchBottom) {
                        checkBottom = true
                        MySharePreferencesEdge.setInt(MySharePreferencesEdge.NOTCHBOTTOM, i, activity)
                        notchBottom = i
                    } else {
                        val i3 = i - (i2 - notchBottom)
                        if (i3 >= 0) {
                            checkBottom = true
                            MySharePreferencesEdge.setInt(MySharePreferencesEdge.NOTCHBOTTOM, i3, activity)
                            notchBottom = i3
                        }
                    }

                    if (i <= notchRadiusBottom) {
                        MySharePreferencesEdge.setInt(MySharePreferencesEdge.NOTCHRADIUSBOTTOM, i, activity)
                        binding?.notchRadiusBottom?.progress = 0
                        notchRadiusBottom = i
                    } else {
                        val i4 = i - (i2 - notchRadiusBottom)
                        if (i4 >= 0) {
                            MySharePreferencesEdge.setInt(MySharePreferencesEdge.NOTCHRADIUSBOTTOM, i4, activity)
                            notchRadiusBottom = i4
                        }
                    }

                    MySharePreferencesEdge.setInt(MySharePreferencesEdge.NOTCHTOP, i, activity)
                    notchTop = i

                    Log.d("notch", "$i   $notchTop")

                    if (MyAccessibilityService.edgeLightingView != null &&
                        MySharePreferencesEdge.getAccessibilityEnabled(MySharePreferencesEdge.ACCESSIBILITY_BROADCAST, activity)) {
                        MyAccessibilityService.edgeLightingView?.changeNotch(checkNotch, notchTop, notchBottom, notchHeight, notchRadiusTop, notchRadiusBottom)
                    } else {
                        binding?.edgeLightView?.changeNotch(checkNotch, notchTop, notchBottom, notchHeight, notchRadiusTop, notchRadiusBottom)
                    }
                } catch (e: Exception) {
                    Log.e("SeekbarChangeListeners", "Exception in notch_width_top", e)
                }
            }

            R.id.notch_width_bottom -> {
                try {
                    if (i >= notchBottom) {
                        checkBottom = false
                    }

                    if (!checkBottom && i >= 50 && i <= notchTop) {
                        if (i <= notchRadiusBottom) {
                            binding?.notchRadiusBottom?.progress = 0
                            MySharePreferencesEdge.setInt(MySharePreferencesEdge.NOTCHRADIUSBOTTOM, i, activity)
                            notchRadiusBottom = i
                        } else {
                            val i2 = i - (notchBottom - notchRadiusBottom)
                            if (i2 >= 0) {
                                MySharePreferencesEdge.setInt(MySharePreferencesEdge.NOTCHRADIUSBOTTOM, i2, activity)
                                notchRadiusBottom = i2
                            }
                        }
                        MySharePreferencesEdge.setInt(MySharePreferencesEdge.NOTCHBOTTOM, i, activity)
                        notchBottom = i
                        if (MyAccessibilityService.edgeLightingView != null &&
                            MySharePreferencesEdge.getAccessibilityEnabled(MySharePreferencesEdge.ACCESSIBILITY_BROADCAST, activity)) {
                            MyAccessibilityService.edgeLightingView?.changeNotch(checkNotch, notchTop, notchBottom, notchHeight, notchRadiusTop, notchRadiusBottom)
                        } else {
                            binding?.edgeLightView?.changeNotch(checkNotch, notchTop, notchBottom, notchHeight, notchRadiusTop, notchRadiusBottom)
                        }
                    }
                } catch (e: Exception) {
                    Log.e("SeekbarChangeListeners", "Exception in notch_width_bottom", e)
                }
            }

            R.id.notch_height -> {
                try {
                    MySharePreferencesEdge.setInt(MySharePreferencesEdge.NOTCHHEIGHT, i, activity)
                    notchHeight = i
                    if (MyAccessibilityService.edgeLightingView != null &&
                        MySharePreferencesEdge.getAccessibilityEnabled(MySharePreferencesEdge.ACCESSIBILITY_BROADCAST, activity)) {
                        MyAccessibilityService.edgeLightingView?.changeNotch(checkNotch, notchTop, notchBottom, notchHeight, notchRadiusTop, notchRadiusBottom)
                    } else {
                        binding?.edgeLightView?.changeNotch(checkNotch, notchTop, notchBottom, notchHeight, notchRadiusTop, notchRadiusBottom)
                    }
                } catch (e: Exception) {
                    Log.e("SeekbarChangeListeners", "Exception in notch_height", e)
                }
            }

            R.id.notch_radius_top -> {
                try {
                    MySharePreferencesEdge.setInt(MySharePreferencesEdge.NOTCHRADIUSTOP, i, activity)
                    notchRadiusTop = i
                    if (MyAccessibilityService.edgeLightingView != null &&
                        MySharePreferencesEdge.getAccessibilityEnabled(MySharePreferencesEdge.ACCESSIBILITY_BROADCAST, activity)) {
                        MyAccessibilityService.edgeLightingView?.changeNotch(checkNotch, notchTop, notchBottom, notchHeight, notchRadiusTop, notchRadiusBottom)
                    } else {
                        binding?.edgeLightView?.changeNotch(checkNotch, notchTop, notchBottom, notchHeight, notchRadiusTop, notchRadiusBottom)
                    }
                } catch (e: Exception) {
                    Log.e("SeekbarChangeListeners", "Exception in notch_radius_top", e)
                }
            }

            R.id.notch_radius_bottom -> {
                try {
                    val i2 = notchBottom - i
                    if (i <= notchBottom && i2 >= 0) {
                        MySharePreferencesEdge.setInt(MySharePreferencesEdge.NOTCHRADIUSBOTTOM, i2, activity)
                        notchRadiusBottom = i2
                        if (MyAccessibilityService.edgeLightingView != null &&
                            MySharePreferencesEdge.getAccessibilityEnabled(MySharePreferencesEdge.ACCESSIBILITY_BROADCAST, activity)) {
                            MyAccessibilityService.edgeLightingView?.changeNotch(checkNotch, notchTop, notchBottom, notchHeight, notchRadiusTop, notchRadiusBottom)
                        } else {
                            binding?.edgeLightView?.changeNotch(checkNotch, notchTop, notchBottom, notchHeight, notchRadiusTop, notchRadiusBottom)
                        }
                    }
                } catch (e: Exception) {
                    Log.e("SeekbarChangeListeners", "Exception in notch_radius_bottom", e)
                }
            }

            else -> Log.w("SeekbarChangeListeners", "Unknown seekBar ID: ${seekBar?.id}")
        }
    }

    override fun onStartTrackingTouch(seekBar: SeekBar?) {
        // No specific handling needed here
    }

    override fun onStopTrackingTouch(seekBar: SeekBar?) {
        try {
            if (MySharePreferencesEdge.getBooleanValue(MySharePreferencesEdge.ControlWindowManager, activity)) {
                val intent = Intent(Const.Action_ChangeWindownManager).apply {
                    when (tag) {
                        "border" -> putExtra(Const.CONTROLWINDOW, Const.BORDER)
                        "notch" -> putExtra(Const.CONTROLWINDOW, Const.NOTCH)
                    }
                    setPackage(activity?.packageName)
                }
                activity?.sendBroadcast(intent)
            }
        } catch (e: Exception) {
            Log.e("SeekbarChangeListeners", "Exception in onStopTrackingTouch", e)
        }
    }
}

