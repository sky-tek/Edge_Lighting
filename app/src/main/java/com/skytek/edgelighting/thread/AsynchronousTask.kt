package com.skytek.edgelighting.thread

import android.content.Intent
import android.graphics.Color
import android.graphics.drawable.ColorDrawable
import android.util.DisplayMetrics
import android.util.Log
import com.skytek.edgelighting.CoroutinesAsyncTask
import com.skytek.edgelighting.MyAccessibilityService.Companion.edgeLightingView
import com.skytek.edgelighting.WallpaperWindowEdgeService
import com.skytek.edgelighting.activities.EdgeOverlaySettingsActivity
import com.skytek.edgelighting.activities.EdgeOverlaySettingsActivity.Companion.binding
import com.skytek.edgelighting.activities.EdgeOverlaySettingsActivity.Companion.themeArrayList
import com.skytek.edgelighting.models.Theme
import com.skytek.edgelighting.utils.Const
import com.skytek.edgelighting.utils.MySharePreferencesEdge

class AsynchronousTask(val task:String,val activity: EdgeOverlaySettingsActivity) : CoroutinesAsyncTask<String, Void, List<Any>>("MysAsyncTask"){

    override fun onPreExecute() {
        super.onPreExecute()
    }

    override fun doInBackground(vararg params: String?): List<Any?>? {
        when(task){
            "removeWallpaperAndEdge" -> {
                if (activity.isMyServiceRunning(WallpaperWindowEdgeService::class.java)) {
                    try {
                        val intent = Intent(activity, WallpaperWindowEdgeService::class.java)
                        intent.action = Const.ACTION.STOP_ACTION
                        activity.stopService(intent)
                    } catch (e: Exception) {
                    }
                    MySharePreferencesEdge.putBoolean(MySharePreferencesEdge.ControlWindowManager, false, activity)
                }
                MySharePreferencesEdge.setInt(MySharePreferencesEdge.ID_THEME, -1, activity)
                return null
            }
            "getThemeData" -> {
                val arrayList: ArrayList<Theme> = ArrayList<Theme>()
                val displayMetrics = DisplayMetrics()
                activity.windowManager.defaultDisplay.getMetrics(displayMetrics)
                var width = displayMetrics.widthPixels
                val i = width / 2
                val notch = i - 300

                val color = intArrayOf(
                    Color.parseColor("#EB1111"), Color.parseColor("#1A11EB"),
                    Color.parseColor("#EB11DA"), Color.parseColor("#11D6EB"),
                    Color.parseColor("#EBDA11"), Color.parseColor("#EBDA11"),
                    Color.parseColor("#EB1111"))

                val id = 1

                val title = "Color sample 0"

                val speed = 7
                val size = 20
                val cornerTop = 50
                val cornerBottom = 50
                val shape = "line"
                val checkBackground = 0
                val colorBg = "#000000"
                val linkBg = "link"
                val notchHeight = 60
                val notchRadiusBottom = 50
                val notchRadiusTop = 50
                val isNotchCheck = 0 == 1

                arrayList.add(Theme(id, title, speed, size, cornerTop, cornerBottom, color, shape, checkBackground, colorBg, linkBg, notch, notch,
                    notchHeight, notchRadiusBottom, notchRadiusTop, isNotchCheck))
                return arrayList
            }
            "setEdgeBorderColor" -> {
                try {
                    when(MySharePreferencesEdge.getSpinnerInt(MySharePreferencesEdge.SPINNER, activity)){
                        0 -> {
                            activity.setEdgeBorderColoring((binding!!.imageColor1!!.background as ColorDrawable).color, (binding!!.imageColor1!!.background as ColorDrawable).color,
                                (binding!!.imageColor1!!.background as ColorDrawable).color, (binding!!.imageColor1!!.background as ColorDrawable).color,
                                (binding!!.imageColor1!!.background as ColorDrawable).color, (binding!!.imageColor1!!.background as ColorDrawable).color,
                                (binding!!.imageColor1!!.background as ColorDrawable).color)
                        }
                        1 -> {
                            activity.setEdgeBorderColoring((binding!!.imageColor1!!.background as ColorDrawable).color, (binding!!.imageColor2!!.background as ColorDrawable).color,
                                (binding!!.imageColor1!!.background as ColorDrawable).color, (binding!!.imageColor2!!.background as ColorDrawable).color,
                                (binding!!.imageColor1!!.background as ColorDrawable).color, (binding!!.imageColor2!!.background as ColorDrawable).color,
                                (binding!!.imageColor1!!.background as ColorDrawable).color)
                        }
                        2 -> {
                            activity.setEdgeBorderColoring((binding!!.imageColor1!!.background as ColorDrawable).color, (binding!!.imageColor2!!.background as ColorDrawable).color,
                                (binding!!.imageColor3!!.background as ColorDrawable).color, (binding!!.imageColor1!!.background as ColorDrawable).color,
                                (binding!!.imageColor2!!.background as ColorDrawable).color, (binding!!.imageColor3!!.background as ColorDrawable).color,
                                (binding!!.imageColor1!!.background as ColorDrawable).color)
                        }
                        3 -> {
                            activity.setEdgeBorderColoring((binding!!.imageColor1!!.background as ColorDrawable).color, (binding!!.imageColor2!!.background as ColorDrawable).color,
                                (binding!!.imageColor3!!.background as ColorDrawable).color, (binding!!.imageColor4!!.background as ColorDrawable).color,
                                (binding!!.imageColor1!!.background as ColorDrawable).color, (binding!!.imageColor2!!.background as ColorDrawable).color,
                                (binding!!.imageColor3!!.background as ColorDrawable).color)
                        }
                        4 -> {
                            activity.setEdgeBorderColoring((binding!!.imageColor1!!.background as ColorDrawable).color, (binding!!.imageColor2!!.background as ColorDrawable).color,
                                (binding!!.imageColor3!!.background as ColorDrawable).color, (binding!!.imageColor4!!.background as ColorDrawable).color,
                                (binding!!.imageColor5!!.background as ColorDrawable).color, (binding!!.imageColor1!!.background as ColorDrawable).color,
                                (binding!!.imageColor2!!.background as ColorDrawable).color)
                        }
                        5 -> {
                            activity.setEdgeBorderColoring((binding!!.imageColor1!!.background as ColorDrawable).color, (binding!!.imageColor2!!.background as ColorDrawable).color,
                                (binding!!.imageColor3!!.background as ColorDrawable).color, (binding!!.imageColor4!!.background as ColorDrawable).color,
                                (binding!!.imageColor5!!.background as ColorDrawable).color, (binding!!.imageColor6!!.background as ColorDrawable).color,
                                (binding!!.imageColor1!!.background as ColorDrawable).color)
                        }
                    }
                } catch (e: NullPointerException) {}
                return null
            }
        }
        return null
    }

    override fun onPostExecute(list: List<Any?>?) {
        super.onPostExecute(list)
        when(task){
            "getThemeData" -> {
                themeArrayList = ArrayList()
                themeArrayList!!.addAll(list!!)
                Log.d("kjsacvkgsyacv", "onPostExecute: $list")
                if (activity.progressDialog != null && activity.progressDialog!!.isShowing) {
                    activity.progressDialog!!.dismiss()
                }
                activity.data
                activity.init()

                try
                {
//                    activity.setNotchSeekbarListner()
                    activity.setBorderSeekbarListner()
                    activity.setBorderColorsListener()
                    activity.setBorderTypeListener()
                }
                catch (e:java.lang.Exception)
                {
                    e.printStackTrace()
                }
            }
            "setEdgeBorderColor" -> {
                try {
                    if(edgeLightingView !=null && MySharePreferencesEdge.getAccessibilityEnabled(MySharePreferencesEdge.ACCESSIBILITY_BROADCAST, activity)){
                        edgeLightingView!!.changeColor(EdgeOverlaySettingsActivity.color)
                    }else{
                        binding?.edgeLightView?.changeColor(EdgeOverlaySettingsActivity.color)
                    }
                    if (MySharePreferencesEdge.getBooleanValue(MySharePreferencesEdge.ControlWindowManager, activity)) {
                        val intent = Intent(Const.Action_ChangeWindownManager)
                        intent.putExtra(Const.CONTROLWINDOW, Const.COLOR)
                        intent.setPackage(activity!!.packageName)
                        activity.sendBroadcast(intent)
                    }
                } catch (e: NullPointerException) {}
            }
        }
    }
}

