package com.skytek.edgelighting.thread

import android.content.Intent
import android.graphics.Color
import android.graphics.drawable.ColorDrawable
import android.util.DisplayMetrics
import com.skytek.edgelighting.WallpaperWindowEdgeService
import com.skytek.edgelighting.activities.EdgeOverlaySettingsActivity
import com.skytek.edgelighting.activities.EdgeOverlaySettingsActivity.Companion.themeArrayList
import com.skytek.edgelighting.models.Theme
import com.skytek.edgelighting.utils.Const
import com.skytek.edgelighting.utils.MySharePreferencesEdge
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.async
import kotlinx.coroutines.launch

class CoroutineAsync {

    fun doCoroutine(task: String, activity: EdgeOverlaySettingsActivity) {

        when (task) {
            "removeWallpaperAndEdge" -> {

                CoroutineScope(Dispatchers.IO).launch {
                    activity.turnOffWallpaper()
                    if (activity.isMyServiceRunning(WallpaperWindowEdgeService::class.java)) {
                        try {
                            val intent = Intent(activity, WallpaperWindowEdgeService::class.java)
                            intent.action = Const.ACTION.STOP_ACTION
                            activity.stopService(intent)
                        } catch (e: Exception) {
                        }
                        MySharePreferencesEdge.putBoolean(
                            MySharePreferencesEdge.ControlWindowManager,
                            false,
                            activity
                        )
                    }
                    MySharePreferencesEdge.setInt(MySharePreferencesEdge.ID_THEME, -1, activity)
                }
            }

            "getThemeData" -> {
                val coroutine = CoroutineScope(Dispatchers.IO).async {

                    val arrayList: ArrayList<Theme> = ArrayList<Theme>()
                    val displayMetrics = DisplayMetrics()
                    activity.windowManager.defaultDisplay.getMetrics(displayMetrics)
                    var width = displayMetrics.widthPixels
                    val i = width / 2
                    val notch = i - 300

                    val iArr = intArrayOf(
                        Color.parseColor("#EB1111"), Color.parseColor("#1A11EB"),
                        Color.parseColor("#EB11DA"), Color.parseColor("#11D6EB"),
                        Color.parseColor("#EBDA11"), Color.parseColor("#EBDA11"),
                        Color.parseColor("#EB1111")
                    )

                    val z = 0 == 1
                    val id = 1
                    val string = "Color sample 0"
                    val i2 = 7
                    val i3 = 20
                    val i4 = 50
                    val i5 = 50
                    val string2 = "line"
                    val i6 = 0
                    arrayList.add(
                        Theme(
                            id,
                            string,
                            i2,
                            i3,
                            i4,
                            i5,
                            iArr,
                            string2,
                            i6,
                            "#000000",
                            "link",
                            notch,
                            notch,
                            60,
                            50,
                            50,
                            z
                        )
                    )
                    return@async arrayList
                }
                CoroutineScope(Dispatchers.Main).launch {
                    val list = coroutine.await()
                    themeArrayList = ArrayList()
                    themeArrayList!!.addAll(list!!)
                    if (activity.progressDialog != null && activity.progressDialog!!.isShowing) {
                        activity.progressDialog!!.dismiss()
                    }
                    activity.data
                    activity.init()
//                    activity.setNotchSeekbarListner()
                    activity.setBorderSeekbarListner()
                    activity.setBorderColorsListener()
                    activity.setBorderTypeListener()
                }
            }

            "setEdgeBorderColor" -> {

                CoroutineScope(Dispatchers.IO).launch {
                    when (MySharePreferencesEdge.getSpinnerInt(
                        MySharePreferencesEdge.SPINNER,
                        activity
                    )) {
                        0 -> {
                            activity.setEdgeBorderColoring(
                                (EdgeOverlaySettingsActivity.binding!!.imageColor1!!.background as ColorDrawable).color,
                                (EdgeOverlaySettingsActivity.binding!!.imageColor1!!.background as ColorDrawable).color,
                                (EdgeOverlaySettingsActivity.binding!!.imageColor1!!.background as ColorDrawable).color,
                                (EdgeOverlaySettingsActivity.binding!!.imageColor1!!.background as ColorDrawable).color,
                                (EdgeOverlaySettingsActivity.binding!!.imageColor1!!.background as ColorDrawable).color,
                                (EdgeOverlaySettingsActivity.binding!!.imageColor1!!.background as ColorDrawable).color,
                                (EdgeOverlaySettingsActivity.binding!!.imageColor1!!.background as ColorDrawable).color
                            )
                        }

                        1 -> {
                            activity.setEdgeBorderColoring(
                                (EdgeOverlaySettingsActivity.binding!!.imageColor1!!.background as ColorDrawable).color,
                                (EdgeOverlaySettingsActivity.binding!!.imageColor2!!.background as ColorDrawable).color,
                                (EdgeOverlaySettingsActivity.binding!!.imageColor1!!.background as ColorDrawable).color,
                                (EdgeOverlaySettingsActivity.binding!!.imageColor2!!.background as ColorDrawable).color,
                                (EdgeOverlaySettingsActivity.binding!!.imageColor1!!.background as ColorDrawable).color,
                                (EdgeOverlaySettingsActivity.binding!!.imageColor2!!.background as ColorDrawable).color,
                                (EdgeOverlaySettingsActivity.binding!!.imageColor1!!.background as ColorDrawable).color
                            )
                        }

                        2 -> {
                            activity.setEdgeBorderColoring(
                                (EdgeOverlaySettingsActivity.binding!!.imageColor1!!.background as ColorDrawable).color,
                                (EdgeOverlaySettingsActivity.binding!!.imageColor2!!.background as ColorDrawable).color,
                                (EdgeOverlaySettingsActivity.binding!!.imageColor3!!.background as ColorDrawable).color,
                                (EdgeOverlaySettingsActivity.binding!!.imageColor1!!.background as ColorDrawable).color,
                                (EdgeOverlaySettingsActivity.binding!!.imageColor2!!.background as ColorDrawable).color,
                                (EdgeOverlaySettingsActivity.binding!!.imageColor3!!.background as ColorDrawable).color,
                                (EdgeOverlaySettingsActivity.binding!!.imageColor1!!.background as ColorDrawable).color
                            )
                        }

                        3 -> {
                            activity.setEdgeBorderColoring(
                                (EdgeOverlaySettingsActivity.binding!!.imageColor1!!.background as ColorDrawable).color,
                                (EdgeOverlaySettingsActivity.binding!!.imageColor2!!.background as ColorDrawable).color,
                                (EdgeOverlaySettingsActivity.binding!!.imageColor3!!.background as ColorDrawable).color,
                                (EdgeOverlaySettingsActivity.binding!!.imageColor4!!.background as ColorDrawable).color,
                                (EdgeOverlaySettingsActivity.binding!!.imageColor1!!.background as ColorDrawable).color,
                                (EdgeOverlaySettingsActivity.binding!!.imageColor2!!.background as ColorDrawable).color,
                                (EdgeOverlaySettingsActivity.binding!!.imageColor3!!.background as ColorDrawable).color
                            )
                        }

                        4 -> {
                            activity.setEdgeBorderColoring(
                                (EdgeOverlaySettingsActivity.binding!!.imageColor1!!.background as ColorDrawable).color,
                                (EdgeOverlaySettingsActivity.binding!!.imageColor2!!.background as ColorDrawable).color,
                                (EdgeOverlaySettingsActivity.binding!!.imageColor3!!.background as ColorDrawable).color,
                                (EdgeOverlaySettingsActivity.binding!!.imageColor4!!.background as ColorDrawable).color,
                                (EdgeOverlaySettingsActivity.binding!!.imageColor5!!.background as ColorDrawable).color,
                                (EdgeOverlaySettingsActivity.binding!!.imageColor1!!.background as ColorDrawable).color,
                                (EdgeOverlaySettingsActivity.binding!!.imageColor2!!.background as ColorDrawable).color
                            )
                        }

                        5 -> {
                            activity.setEdgeBorderColoring(
                                (EdgeOverlaySettingsActivity.binding!!.imageColor1!!.background as ColorDrawable).color,
                                (EdgeOverlaySettingsActivity.binding!!.imageColor2!!.background as ColorDrawable).color,
                                (EdgeOverlaySettingsActivity.binding!!.imageColor3!!.background as ColorDrawable).color,
                                (EdgeOverlaySettingsActivity.binding!!.imageColor4!!.background as ColorDrawable).color,
                                (EdgeOverlaySettingsActivity.binding!!.imageColor5!!.background as ColorDrawable).color,
                                (EdgeOverlaySettingsActivity.binding!!.imageColor6!!.background as ColorDrawable).color,
                                (EdgeOverlaySettingsActivity.binding!!.imageColor1!!.background as ColorDrawable).color
                            )
                        }

                    }
                }

            }
        }
    }

}

