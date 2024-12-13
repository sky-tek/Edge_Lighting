package com.skytek.edgelighting.Listeners

import android.graphics.Color
import android.view.View
import android.widget.AdapterView
import com.skytek.edgelighting.activities.EdgeOverlaySettingsActivity
import com.skytek.edgelighting.activities.EdgeOverlaySettingsActivity.Companion._color1
import com.skytek.edgelighting.activities.EdgeOverlaySettingsActivity.Companion._color2
import com.skytek.edgelighting.activities.EdgeOverlaySettingsActivity.Companion._color3
import com.skytek.edgelighting.activities.EdgeOverlaySettingsActivity.Companion.activity
import com.skytek.edgelighting.utils.MySharePreferencesEdge

class SpinnerItemSelectedListener : AdapterView.OnItemSelectedListener {

    override fun onItemSelected(adapterView: AdapterView<*>?, view: View?, position: Int, l: Long) {
        try {
            EdgeOverlaySettingsActivity._color1 = MySharePreferencesEdge.getColor1Value(MySharePreferencesEdge.COLOR1_VALUE, activity,Color.RED)
            EdgeOverlaySettingsActivity._color2 = MySharePreferencesEdge.getColor2Value(MySharePreferencesEdge.COLOR2_VALUE, activity,Color.BLUE)
            EdgeOverlaySettingsActivity._color3 = MySharePreferencesEdge.getColor3Value(MySharePreferencesEdge.COLOR3_VALUE, activity,Color.MAGENTA)
            EdgeOverlaySettingsActivity._color4 = MySharePreferencesEdge.getColor4Value(MySharePreferencesEdge.COLOR4_VALUE, activity, _color1)
            EdgeOverlaySettingsActivity._color5 = MySharePreferencesEdge.getColor5Value(MySharePreferencesEdge.COLOR5_VALUE, activity,_color2)
            EdgeOverlaySettingsActivity._color6 = MySharePreferencesEdge.getColor6Value(MySharePreferencesEdge.COLOR6_VALUE, activity,_color3)
        } catch (e: Exception) {}

//        when (5) {
//
//            0 -> {
//
//
//                try
//                {
//                    Log.d("abcd",position.toString())
//                    MySharePreferencesEdge.setSpinnerInt(MySharePreferencesEdge.SPINNER, 0, activity)
//                    EdgeOverlaySettingsActivity.colors_list!!.prompt = "1"
//                    binding!!.apply {
//                        c1!!.visibility = View.VISIBLE
//                        c2!!.visibility = View.GONE
//                        c3!!.visibility = View.GONE
//                        c4!!.visibility = View.GONE
//                        c5!!.visibility = View.GONE
//                        c6!!.visibility = View.GONE
//                        imageColor1!!.setBackgroundColor(if (EdgeOverlaySettingsActivity._color1 == 0) EdgeOverlaySettingsActivity.img_c1 else Color.parseColor(BorderColorClickListners.convertIntToString(EdgeOverlaySettingsActivity._color1)))
//                    }
//                }
//                catch (e:Exception)
//                {
//                    e.printStackTrace()
//                }
//
////                        Log.d("imgColor", (image_color1!!.background as ColorDrawable).color.toString())
//
//                EdgeOverlaySettingsActivity.color[0] = if (EdgeOverlaySettingsActivity._color1 == 0) EdgeOverlaySettingsActivity.img_c1 else Color.parseColor(
//                    BorderColorClickListners.convertIntToString(EdgeOverlaySettingsActivity._color1)
//                )
//                EdgeOverlaySettingsActivity.color[1] = if (EdgeOverlaySettingsActivity._color1 == 0) EdgeOverlaySettingsActivity.img_c1 else Color.parseColor(
//                    BorderColorClickListners.convertIntToString(EdgeOverlaySettingsActivity._color1)
//                )
//                EdgeOverlaySettingsActivity.color[2] = if (EdgeOverlaySettingsActivity._color1 == 0) EdgeOverlaySettingsActivity.img_c1 else Color.parseColor(
//                    BorderColorClickListners.convertIntToString(EdgeOverlaySettingsActivity._color1)
//                )
//                EdgeOverlaySettingsActivity.color[3] = if (EdgeOverlaySettingsActivity._color1 == 0) EdgeOverlaySettingsActivity.img_c1 else Color.parseColor(
//                    BorderColorClickListners.convertIntToString(EdgeOverlaySettingsActivity._color1)
//                )
//                EdgeOverlaySettingsActivity.color[4] = if (EdgeOverlaySettingsActivity._color1 == 0) EdgeOverlaySettingsActivity.img_c1 else Color.parseColor(
//                    BorderColorClickListners.convertIntToString(EdgeOverlaySettingsActivity._color1)
//                )
//                EdgeOverlaySettingsActivity.color[5] = if (EdgeOverlaySettingsActivity._color1 == 0) EdgeOverlaySettingsActivity.img_c1 else Color.parseColor(
//                    BorderColorClickListners.convertIntToString(EdgeOverlaySettingsActivity._color1)
//                )
//                EdgeOverlaySettingsActivity.color[6] = if (EdgeOverlaySettingsActivity._color1 == 0) EdgeOverlaySettingsActivity.img_c1 else Color.parseColor(
//                    BorderColorClickListners.convertIntToString(EdgeOverlaySettingsActivity._color1)
//                )
////                if(!(binding!!.switchCharging!!.isChecked)){
//                if(timerReceiver == null){
//                    if(MyAccessibilityService.edgeLightingView !=null && MySharePreferencesEdge.getAccessibilityEnabled(MySharePreferencesEdge.ACCESSIBILITY_BROADCAST, activity)){
//                        MyAccessibilityService.edgeLightingView!!.changeColor(EdgeOverlaySettingsActivity.color)
//                    }else{
//                        binding!!.edgeLightView!!.changeColor(EdgeOverlaySettingsActivity.color)
//                    }
//                    if (MySharePreferencesEdge.getBooleanValue(MySharePreferencesEdge.ControlWindowManager, activity)) {
//                        val intent = Intent(Const.Action_ChangeWindownManager)
//                        intent.putExtra(Const.CONTROLWINDOW, Const.COLOR)
//                        intent.setPackage(activity!!.packageName)
//                        activity!!.sendBroadcast(intent)
//                    }
//                }
//            }
//            1 -> {
//                Log.d("abcd",position.toString())
//                MySharePreferencesEdge.setSpinnerInt(MySharePreferencesEdge.SPINNER, 1, activity)
//                EdgeOverlaySettingsActivity.colors_list!!.prompt = "2"
//                binding!!.apply {
//                    c1!!.visibility = View.VISIBLE
//                    c2!!.visibility = View.GONE
//                    c3!!.visibility = View.GONE
//                    c4!!.visibility = View.GONE
//                    c5!!.visibility = View.GONE
//                    c6!!.visibility = View.GONE
//                    imageColor1!!.setBackgroundColor(if (EdgeOverlaySettingsActivity._color1 == 0) EdgeOverlaySettingsActivity.img_c1 else Color.parseColor(BorderColorClickListners.convertIntToString(EdgeOverlaySettingsActivity._color1)))
//                    imageColor2!!.setBackgroundColor(if (EdgeOverlaySettingsActivity._color2 == 0) EdgeOverlaySettingsActivity.img_c2 else Color.parseColor(BorderColorClickListners.convertIntToString(EdgeOverlaySettingsActivity._color2)))
//                }
//
//
//                EdgeOverlaySettingsActivity.color[0] = if (EdgeOverlaySettingsActivity._color1 == 0) EdgeOverlaySettingsActivity.img_c1 else Color.parseColor(
//                    BorderColorClickListners.convertIntToString(EdgeOverlaySettingsActivity._color1)
//                )
//                EdgeOverlaySettingsActivity.color[1] = if (EdgeOverlaySettingsActivity._color2 == 0) EdgeOverlaySettingsActivity.img_c2 else Color.parseColor(
//                    BorderColorClickListners.convertIntToString(EdgeOverlaySettingsActivity._color2)
//                )
//                EdgeOverlaySettingsActivity.color[2] = if (EdgeOverlaySettingsActivity._color1 == 0) EdgeOverlaySettingsActivity.img_c1 else Color.parseColor(
//                    BorderColorClickListners.convertIntToString(EdgeOverlaySettingsActivity._color1)
//                )
//                EdgeOverlaySettingsActivity.color[3] = if (EdgeOverlaySettingsActivity._color2 == 0) EdgeOverlaySettingsActivity.img_c2 else Color.parseColor(
//                    BorderColorClickListners.convertIntToString(EdgeOverlaySettingsActivity._color2)
//                )
//                EdgeOverlaySettingsActivity.color[4] = if (EdgeOverlaySettingsActivity._color1 == 0) EdgeOverlaySettingsActivity.img_c1 else Color.parseColor(
//                    BorderColorClickListners.convertIntToString(EdgeOverlaySettingsActivity._color1)
//                )
//                EdgeOverlaySettingsActivity.color[5] = if (EdgeOverlaySettingsActivity._color2 == 0) EdgeOverlaySettingsActivity.img_c2 else Color.parseColor(
//                    BorderColorClickListners.convertIntToString(EdgeOverlaySettingsActivity._color2)
//                )
//                EdgeOverlaySettingsActivity.color[6] = if (EdgeOverlaySettingsActivity._color1 == 0) EdgeOverlaySettingsActivity.img_c1 else Color.parseColor(
//                    BorderColorClickListners.convertIntToString(EdgeOverlaySettingsActivity._color1)
//                )
////                if(!(binding!!.switchCharging!!.isChecked)){
//                if(timerReceiver == null){
//                    if(MyAccessibilityService.edgeLightingView !=null && MySharePreferencesEdge.getAccessibilityEnabled(MySharePreferencesEdge.ACCESSIBILITY_BROADCAST, activity)){
//                        MyAccessibilityService.edgeLightingView!!.changeColor(EdgeOverlaySettingsActivity.color)
//                    }else{
//                        binding!!.edgeLightView!!.changeColor(EdgeOverlaySettingsActivity.color)
//                    }
//                    if (MySharePreferencesEdge.getBooleanValue(MySharePreferencesEdge.ControlWindowManager, activity)) {
//                        val intent = Intent(Const.Action_ChangeWindownManager)
//                        intent.putExtra(Const.CONTROLWINDOW, Const.COLOR)
//                        intent.setPackage(activity!!.packageName)
//                        activity!!.sendBroadcast(intent)
//                    }
//                }
//            }
//            2 -> {
//                try {
//                    MySharePreferencesEdge.setSpinnerInt(MySharePreferencesEdge.SPINNER, 2, activity)
//                    EdgeOverlaySettingsActivity.colors_list!!.prompt = "3"
//                    binding!!.apply {
//                        c1!!.visibility = View.VISIBLE
//                        c2!!.visibility = View.VISIBLE
//                        c3!!.visibility = View.VISIBLE
//                        c4!!.visibility = View.GONE
//                        c5!!.visibility = View.GONE
//                        c6!!.visibility = View.GONE
//                        imageColor1!!.setBackgroundColor(if (EdgeOverlaySettingsActivity._color1 == 0) EdgeOverlaySettingsActivity.img_c1 else Color.parseColor(BorderColorClickListners.convertIntToString(EdgeOverlaySettingsActivity._color1)))
//                        imageColor2!!.setBackgroundColor(if (EdgeOverlaySettingsActivity._color2 == 0) EdgeOverlaySettingsActivity.img_c2 else Color.parseColor(BorderColorClickListners.convertIntToString(EdgeOverlaySettingsActivity._color2)))
//                        imageColor3!!.setBackgroundColor(if (EdgeOverlaySettingsActivity._color3 == 0) EdgeOverlaySettingsActivity.img_c3 else Color.parseColor(BorderColorClickListners.convertIntToString(EdgeOverlaySettingsActivity._color3)))
//                    }
//
//                    EdgeOverlaySettingsActivity.color[0] = if (EdgeOverlaySettingsActivity._color1 == 0) EdgeOverlaySettingsActivity.img_c1 else Color.parseColor(
//                        BorderColorClickListners.convertIntToString(EdgeOverlaySettingsActivity._color1)
//                    )
//                    EdgeOverlaySettingsActivity.color[1] = if (EdgeOverlaySettingsActivity._color2 == 0) EdgeOverlaySettingsActivity.img_c2 else Color.parseColor(
//                        BorderColorClickListners.convertIntToString(EdgeOverlaySettingsActivity._color2)
//                    )
//                    EdgeOverlaySettingsActivity.color[2] = if (EdgeOverlaySettingsActivity._color3 == 0) EdgeOverlaySettingsActivity.img_c3 else Color.parseColor(
//                        BorderColorClickListners.convertIntToString(EdgeOverlaySettingsActivity._color3)
//                    )
//                    EdgeOverlaySettingsActivity.color[3] = if (EdgeOverlaySettingsActivity._color1 == 0) EdgeOverlaySettingsActivity.img_c1 else Color.parseColor(
//                        BorderColorClickListners.convertIntToString(EdgeOverlaySettingsActivity._color1)
//                    )
//                    EdgeOverlaySettingsActivity.color[4] = if (EdgeOverlaySettingsActivity._color2 == 0) EdgeOverlaySettingsActivity.img_c2 else Color.parseColor(
//                        BorderColorClickListners.convertIntToString(EdgeOverlaySettingsActivity._color2)
//                    )
//                    EdgeOverlaySettingsActivity.color[5] = if (EdgeOverlaySettingsActivity._color3 == 0) EdgeOverlaySettingsActivity.img_c3 else Color.parseColor(
//                        BorderColorClickListners.convertIntToString(EdgeOverlaySettingsActivity._color3)
//                    )
//                    EdgeOverlaySettingsActivity.color[6] = if (EdgeOverlaySettingsActivity._color1 == 0) EdgeOverlaySettingsActivity.img_c1 else Color.parseColor(
//                        BorderColorClickListners.convertIntToString(EdgeOverlaySettingsActivity._color1)
//                    )
//                    if(timerReceiver == null){
//                        if(MyAccessibilityService.edgeLightingView !=null && MySharePreferencesEdge.getAccessibilityEnabled(MySharePreferencesEdge.ACCESSIBILITY_BROADCAST, activity)){
//                            MyAccessibilityService.edgeLightingView!!.changeColor(EdgeOverlaySettingsActivity.color)
//                        }else{
//                            binding!!.edgeLightView!!.changeColor(EdgeOverlaySettingsActivity.color)
//                        }
//                        if (MySharePreferencesEdge.getBooleanValue(MySharePreferencesEdge.ControlWindowManager, activity)) {
//                            val intent = Intent(Const.Action_ChangeWindownManager)
//                            intent.putExtra(Const.CONTROLWINDOW, Const.COLOR)
//                            intent.setPackage(activity!!.packageName)
//                            activity!!.sendBroadcast(intent)
//                        }
//                    }
//                } catch (e: NullPointerException) {}
//            }
//            3 -> {
//                try {
//                    MySharePreferencesEdge.setSpinnerInt(MySharePreferencesEdge.SPINNER, 3, activity)
//                    EdgeOverlaySettingsActivity.colors_list!!.prompt = "4"
//                    binding!!.apply {
//                        c1!!.visibility = View.VISIBLE
//                        c2!!.visibility = View.VISIBLE
//                        c3!!.visibility = View.VISIBLE
//                        c4!!.visibility = View.GONE
//                        c5!!.visibility = View.GONE
//                        c6!!.visibility = View.GONE
//                        imageColor1!!.setBackgroundColor(if (EdgeOverlaySettingsActivity._color1 == 0) EdgeOverlaySettingsActivity.img_c1 else Color.parseColor(BorderColorClickListners.convertIntToString(EdgeOverlaySettingsActivity._color1)))
//                        imageColor2!!.setBackgroundColor(if (EdgeOverlaySettingsActivity._color2 == 0) EdgeOverlaySettingsActivity.img_c2 else Color.parseColor(BorderColorClickListners.convertIntToString(EdgeOverlaySettingsActivity._color2)))
//                        imageColor3!!.setBackgroundColor(if (EdgeOverlaySettingsActivity._color3 == 0) EdgeOverlaySettingsActivity.img_c3 else Color.parseColor(BorderColorClickListners.convertIntToString(EdgeOverlaySettingsActivity._color3)))
//                        imageColor4!!.setBackgroundColor(if (EdgeOverlaySettingsActivity._color4 == 0) EdgeOverlaySettingsActivity.img_c4 else Color.parseColor(BorderColorClickListners.convertIntToString(EdgeOverlaySettingsActivity._color4)))
//                    }
//
//                    EdgeOverlaySettingsActivity.color[0] = if (EdgeOverlaySettingsActivity._color1 == 0) EdgeOverlaySettingsActivity.img_c1 else Color.parseColor(
//                        BorderColorClickListners.convertIntToString(EdgeOverlaySettingsActivity._color1)
//                    )
//                    EdgeOverlaySettingsActivity.color[1] = if (EdgeOverlaySettingsActivity._color2 == 0) EdgeOverlaySettingsActivity.img_c2 else Color.parseColor(
//                        BorderColorClickListners.convertIntToString(EdgeOverlaySettingsActivity._color2)
//                    )
//                    EdgeOverlaySettingsActivity.color[2] = if (EdgeOverlaySettingsActivity._color3 == 0) EdgeOverlaySettingsActivity.img_c3 else Color.parseColor(
//                        BorderColorClickListners.convertIntToString(EdgeOverlaySettingsActivity._color3)
//                    )
//                    EdgeOverlaySettingsActivity.color[3] = if (EdgeOverlaySettingsActivity._color4 == 0) EdgeOverlaySettingsActivity.img_c4 else Color.parseColor(
//                        BorderColorClickListners.convertIntToString(EdgeOverlaySettingsActivity._color4)
//                    )
//                    EdgeOverlaySettingsActivity.color[4] = if (EdgeOverlaySettingsActivity._color1 == 0) EdgeOverlaySettingsActivity.img_c1 else Color.parseColor(
//                        BorderColorClickListners.convertIntToString(EdgeOverlaySettingsActivity._color1)
//                    )
//                    EdgeOverlaySettingsActivity.color[5] = if (EdgeOverlaySettingsActivity._color2 == 0) EdgeOverlaySettingsActivity.img_c2 else Color.parseColor(
//                        BorderColorClickListners.convertIntToString(EdgeOverlaySettingsActivity._color2)
//                    )
//                    EdgeOverlaySettingsActivity.color[6] = if (EdgeOverlaySettingsActivity._color3 == 0) EdgeOverlaySettingsActivity.img_c3 else Color.parseColor(
//                        BorderColorClickListners.convertIntToString(EdgeOverlaySettingsActivity._color3)
//                    )
//                    if(timerReceiver == null){
//                        if(MyAccessibilityService.edgeLightingView !=null && MySharePreferencesEdge.getAccessibilityEnabled(MySharePreferencesEdge.ACCESSIBILITY_BROADCAST, activity)){
//                            MyAccessibilityService.edgeLightingView!!.changeColor(EdgeOverlaySettingsActivity.color)
//                        }else{
//                            binding!!.edgeLightView!!.changeColor(EdgeOverlaySettingsActivity.color)
//                        }
//                        if (MySharePreferencesEdge.getBooleanValue(MySharePreferencesEdge.ControlWindowManager, activity)) {
//                            val intent = Intent(Const.Action_ChangeWindownManager)
//                            intent.putExtra(Const.CONTROLWINDOW, Const.COLOR)
//                            intent.setPackage(activity!!.packageName)
//                            activity!!.sendBroadcast(intent)
//                        }
//                    }
//                } catch (e: NullPointerException) {}
//            }
//            4 -> {
//                MySharePreferencesEdge.setSpinnerInt(MySharePreferencesEdge.SPINNER, 4, activity)
//                EdgeOverlaySettingsActivity.colors_list!!.prompt = "5"
//                binding!!.apply {
//                    c1!!.visibility = View.VISIBLE
//                    c2!!.visibility = View.VISIBLE
//                    c3!!.visibility = View.VISIBLE
//                    c4!!.visibility = View.VISIBLE
//                    c5!!.visibility = View.VISIBLE
//                    c6!!.visibility = View.GONE
//                    imageColor1!!.setBackgroundColor(if (EdgeOverlaySettingsActivity._color1 == 0) EdgeOverlaySettingsActivity.img_c1 else Color.parseColor(BorderColorClickListners.convertIntToString(EdgeOverlaySettingsActivity._color1)))
//                    imageColor2!!.setBackgroundColor(if (EdgeOverlaySettingsActivity._color2 == 0) EdgeOverlaySettingsActivity.img_c2 else Color.parseColor(BorderColorClickListners.convertIntToString(EdgeOverlaySettingsActivity._color2)))
//                    imageColor3!!.setBackgroundColor(if (EdgeOverlaySettingsActivity._color3 == 0) EdgeOverlaySettingsActivity.img_c3 else Color.parseColor(BorderColorClickListners.convertIntToString(EdgeOverlaySettingsActivity._color3)))
//                    imageColor4!!.setBackgroundColor(if (EdgeOverlaySettingsActivity._color4 == 0) EdgeOverlaySettingsActivity.img_c4 else Color.parseColor(BorderColorClickListners.convertIntToString(EdgeOverlaySettingsActivity._color4)))
//                    imageColor5!!.setBackgroundColor(if (EdgeOverlaySettingsActivity._color5 == 0) EdgeOverlaySettingsActivity.img_c5 else Color.parseColor(BorderColorClickListners.convertIntToString(EdgeOverlaySettingsActivity._color5)))
//                }
//
//                EdgeOverlaySettingsActivity.color[0] = if (EdgeOverlaySettingsActivity._color1 == 0) EdgeOverlaySettingsActivity.img_c1 else Color.parseColor(
//                    BorderColorClickListners.convertIntToString(EdgeOverlaySettingsActivity._color1)
//                )
//                EdgeOverlaySettingsActivity.color[1] = if (EdgeOverlaySettingsActivity._color2 == 0) EdgeOverlaySettingsActivity.img_c2 else Color.parseColor(
//                    BorderColorClickListners.convertIntToString(EdgeOverlaySettingsActivity._color2)
//                )
//                EdgeOverlaySettingsActivity.color[2] = if (EdgeOverlaySettingsActivity._color3 == 0) EdgeOverlaySettingsActivity.img_c3 else Color.parseColor(
//                    BorderColorClickListners.convertIntToString(EdgeOverlaySettingsActivity._color3)
//                )
//                EdgeOverlaySettingsActivity.color[3] = if (EdgeOverlaySettingsActivity._color4 == 0) EdgeOverlaySettingsActivity.img_c4 else Color.parseColor(
//                    BorderColorClickListners.convertIntToString(EdgeOverlaySettingsActivity._color4)
//                )
//                EdgeOverlaySettingsActivity.color[4] = if (EdgeOverlaySettingsActivity._color5 == 0) EdgeOverlaySettingsActivity.img_c5 else Color.parseColor(
//                    BorderColorClickListners.convertIntToString(EdgeOverlaySettingsActivity._color5)
//                )
//                EdgeOverlaySettingsActivity.color[5] = if (EdgeOverlaySettingsActivity._color1 == 0) EdgeOverlaySettingsActivity.img_c1 else Color.parseColor(
//                    BorderColorClickListners.convertIntToString(EdgeOverlaySettingsActivity._color1)
//                )
//                EdgeOverlaySettingsActivity.color[6] = if (EdgeOverlaySettingsActivity._color2 == 0) EdgeOverlaySettingsActivity.img_c2 else Color.parseColor(
//                    BorderColorClickListners.convertIntToString(EdgeOverlaySettingsActivity._color2)
//                )
////                if(!(binding!!.switchCharging!!.isChecked)){
//                if(timerReceiver == null){
//                    if(MyAccessibilityService.edgeLightingView !=null && MySharePreferencesEdge.getAccessibilityEnabled(MySharePreferencesEdge.ACCESSIBILITY_BROADCAST, activity)){
//                        MyAccessibilityService.edgeLightingView!!.changeColor(EdgeOverlaySettingsActivity.color)
//                    }else{
//                        binding!!.edgeLightView!!.changeColor(EdgeOverlaySettingsActivity.color)
//                    }
//                    if (MySharePreferencesEdge.getBooleanValue(MySharePreferencesEdge.ControlWindowManager, activity)) {
//                        val intent = Intent(Const.Action_ChangeWindownManager)
//                        intent.putExtra(Const.CONTROLWINDOW, Const.COLOR)
//                        intent.setPackage(activity!!.packageName)
//                        activity!!.sendBroadcast(intent)
//                    }
//                }
//            }
//            5 -> {
//                try {
//                    MySharePreferencesEdge.setSpinnerInt(MySharePreferencesEdge.SPINNER, 5, activity)
//                    EdgeOverlaySettingsActivity.colors_list!!.prompt = "6"
//                    binding!!.apply {
//                        c1!!.visibility = View.VISIBLE
//                        c2!!.visibility = View.VISIBLE
//                        c3!!.visibility = View.VISIBLE
//                        c4!!.visibility = View.VISIBLE
//                        c5!!.visibility = View.VISIBLE
//                        c6!!.visibility = View.VISIBLE
//                        imageColor1!!.setBackgroundColor(if (EdgeOverlaySettingsActivity._color1 == 0) EdgeOverlaySettingsActivity.img_c1 else Color.parseColor(BorderColorClickListners.convertIntToString(EdgeOverlaySettingsActivity._color1)))
//                        imageColor2!!.setBackgroundColor(if (EdgeOverlaySettingsActivity._color2 == 0) EdgeOverlaySettingsActivity.img_c2 else Color.parseColor(BorderColorClickListners.convertIntToString(EdgeOverlaySettingsActivity._color2)))
//                        imageColor3!!.setBackgroundColor(if (EdgeOverlaySettingsActivity._color3 == 0) EdgeOverlaySettingsActivity.img_c3 else Color.parseColor(BorderColorClickListners.convertIntToString(EdgeOverlaySettingsActivity._color3)))
//                        imageColor4!!.setBackgroundColor(if (EdgeOverlaySettingsActivity._color4 == 0) EdgeOverlaySettingsActivity.img_c4 else Color.parseColor(BorderColorClickListners.convertIntToString(EdgeOverlaySettingsActivity._color4)))
//                        imageColor5!!.setBackgroundColor(if (EdgeOverlaySettingsActivity._color5 == 0) EdgeOverlaySettingsActivity.img_c5 else Color.parseColor(BorderColorClickListners.convertIntToString(EdgeOverlaySettingsActivity._color5)))
//                        imageColor6!!.setBackgroundColor(if (EdgeOverlaySettingsActivity._color6 == 0) EdgeOverlaySettingsActivity.img_c6 else Color.parseColor(BorderColorClickListners.convertIntToString(EdgeOverlaySettingsActivity._color6)))
//                    }
//
//                    EdgeOverlaySettingsActivity.color[0] = if (EdgeOverlaySettingsActivity._color1 == 0) EdgeOverlaySettingsActivity.img_c1 else Color.parseColor(
//                        BorderColorClickListners.convertIntToString(EdgeOverlaySettingsActivity._color1)
//                    )
//                    EdgeOverlaySettingsActivity.color[1] = if (EdgeOverlaySettingsActivity._color2 == 0) EdgeOverlaySettingsActivity.img_c2 else Color.parseColor(
//                        BorderColorClickListners.convertIntToString(EdgeOverlaySettingsActivity._color2)
//                    )
//                    EdgeOverlaySettingsActivity.color[2] = if (EdgeOverlaySettingsActivity._color3 == 0) EdgeOverlaySettingsActivity.img_c3 else Color.parseColor(
//                        BorderColorClickListners.convertIntToString(EdgeOverlaySettingsActivity._color3)
//                    )
//                    EdgeOverlaySettingsActivity.color[3] = if (EdgeOverlaySettingsActivity._color4 == 0) EdgeOverlaySettingsActivity.img_c4 else Color.parseColor(
//                        BorderColorClickListners.convertIntToString(EdgeOverlaySettingsActivity._color4)
//                    )
//                    EdgeOverlaySettingsActivity.color[4] = if (EdgeOverlaySettingsActivity._color5 == 0) EdgeOverlaySettingsActivity.img_c5 else Color.parseColor(
//                        BorderColorClickListners.convertIntToString(EdgeOverlaySettingsActivity._color5)
//                    )
//                    EdgeOverlaySettingsActivity.color[5] = if (EdgeOverlaySettingsActivity._color6 == 0) EdgeOverlaySettingsActivity.img_c6 else Color.parseColor(
//                        BorderColorClickListners.convertIntToString(EdgeOverlaySettingsActivity._color6)
//                    )
//                    EdgeOverlaySettingsActivity.color[6] = if (EdgeOverlaySettingsActivity._color1 == 0) EdgeOverlaySettingsActivity.img_c1 else Color.parseColor(
//                        BorderColorClickListners.convertIntToString(EdgeOverlaySettingsActivity._color1)
//                    )
//                    if(timerReceiver == null){
//                        if(MyAccessibilityService.edgeLightingView !=null && MySharePreferencesEdge.getAccessibilityEnabled(MySharePreferencesEdge.ACCESSIBILITY_BROADCAST, activity)){
//                            MyAccessibilityService.edgeLightingView!!.changeColor(EdgeOverlaySettingsActivity.color)
//                        }else{
//                            binding!!.edgeLightView!!.changeColor(EdgeOverlaySettingsActivity.color)
//                        }
//                        if (MySharePreferencesEdge.getBooleanValue(MySharePreferencesEdge.ControlWindowManager, activity)) {
//                            val intent = Intent(Const.Action_ChangeWindownManager)
//                            intent.putExtra(Const.CONTROLWINDOW, Const.COLOR)
//                            intent.setPackage(activity!!.packageName)
//                            activity!!.sendBroadcast(intent)
//                        }
//                    }
//                } catch (e: NullPointerException) {}
//            }
//        }
    }

    override fun onNothingSelected(p0: AdapterView<*>?) {
        TODO("Not yet implemented")
    }
}

