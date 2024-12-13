package com.skytek.edgelighting.Listeners

import android.content.Intent
import android.view.View
import com.skytek.edgelighting.MyAccessibilityService
import com.skytek.edgelighting.R
import com.skytek.edgelighting.activities.EdgeOverlaySettingsActivity
import com.skytek.edgelighting.activities.EdgeOverlaySettingsActivity.Companion.activity
import com.skytek.edgelighting.activities.EdgeOverlaySettingsActivity.Companion.binding
import com.skytek.edgelighting.activities.EdgeOverlaySettingsActivity.Companion.themeArrayList
import com.skytek.edgelighting.models.Theme
import com.skytek.edgelighting.utils.Const
import com.skytek.edgelighting.utils.MySharePreferencesEdge

class BorderTypeClickListeners : View.OnClickListener {

    override fun onClick(view: View?) {
        when (view!!.id) {
            R.id.type_line -> {
                EdgeOverlaySettingsActivity._theme = themeArrayList!![0] as Theme?
                EdgeOverlaySettingsActivity.type = Const.LINE
                initType()
            }
            R.id.type_moon -> {
                binding?.typeMoon?.setBackgroundResource(R.drawable.ic_icon_rectangles)
                EdgeOverlaySettingsActivity.type = Const.MOON
                initType()
            }
            R.id.type_snow -> {
                binding?.typeSnow?.setBackgroundResource(R.drawable.ic_icon_rectangles)
                EdgeOverlaySettingsActivity.type = Const.SNOW
                initType()
            }
            R.id.type_sun -> {
                binding?.typeSun?.setBackgroundResource(R.drawable.ic_icon_rectangles)
                EdgeOverlaySettingsActivity.type = Const.SUN
                initType()
            }
            R.id.type_dot -> {
                try {
                    binding?.typeDot?.setBackgroundResource(R.drawable.ic_icon_rectangles)
                    EdgeOverlaySettingsActivity.type = Const.DOT
                    initType()
                } catch (e: NullPointerException) {}
            }
            R.id.type_heart -> {
                binding?.typeHeart?.setBackgroundResource(R.drawable.ic_icon_rectangles)
                EdgeOverlaySettingsActivity.type = Const.HEART
                initType()
            }
            R.id.type_art1 -> {
                binding?.typeArt1?.setBackgroundResource(R.drawable.ic_icon_rectangles)
                EdgeOverlaySettingsActivity.type = Const.ART1
                initType()
            }
            R.id.type_star -> {
                binding?.typeStar?.setBackgroundResource(R.drawable.ic_icon_rectangles)
                EdgeOverlaySettingsActivity.type = Const.STAR
                initType()
            }
            R.id.type_spaceship -> {
                binding?.typeSpaceship?.setBackgroundResource(R.drawable.ic_icon_rectangles)
                EdgeOverlaySettingsActivity.type = Const.SPACESHIP
                initType()
            }
            R.id.type_foot -> {
                binding?.typeFoot?.setBackgroundResource(R.drawable.ic_icon_rectangles)
                EdgeOverlaySettingsActivity.type = Const.FOOT
                initType()
            }
            R.id.type_chrismis -> {
                try {
                    binding?.typeChrismis?.setBackgroundResource(R.drawable.ic_icon_rectangles)
                    EdgeOverlaySettingsActivity.type = Const.CHRISMISTREE
                    initType()
                } catch (e: NullPointerException) {}
            }
            R.id.type_moon1 -> {
                binding?.typeMoon1?.setBackgroundResource(R.drawable.ic_icon_rectangles)
                EdgeOverlaySettingsActivity.type = Const.MOON1
                initType()
            }
            R.id.type_emoji -> {
                try
                {
                    binding?.typeEmoji?.setBackgroundResource(R.drawable.ic_icon_rectangles)
                    EdgeOverlaySettingsActivity.type = Const.EMOJI
                    initType()
                }
                catch (e:java.lang.Exception)
                {
                   e.printStackTrace()
                }

            }
            R.id.type_flower -> {
                binding?.typeFlower?.setBackgroundResource(R.drawable.ic_icon_rectangles)
                EdgeOverlaySettingsActivity.type = Const.FLOWERART
                initType()
            }
        }
    }

    companion object {
        fun initType() {
            try {
                chooseType(EdgeOverlaySettingsActivity.type)
                try {
                    if(MyAccessibilityService.edgeLightingView !=null && MySharePreferencesEdge.getAccessibilityEnabled(MySharePreferencesEdge.ACCESSIBILITY_BROADCAST, activity)){
                        MyAccessibilityService.edgeLightingView!!.changeType(EdgeOverlaySettingsActivity.type)
                    }else{
                        binding!!.apply {
                            edgeLightView!!.visibility = View.VISIBLE
                            edgeLightView!!.changeType(EdgeOverlaySettingsActivity.type)
                        }
                    }
                } catch (e: Exception) {}
                MySharePreferencesEdge.setString(activity, MySharePreferencesEdge.SHAPE, EdgeOverlaySettingsActivity.type)
                if (MySharePreferencesEdge.getBooleanValue(MySharePreferencesEdge.ControlWindowManager, activity)) {
                    val intent = Intent(Const.Action_ChangeWindownManager)
                    intent.putExtra(Const.CONTROLWINDOW, Const.BORDER)
                    intent.setPackage(activity!!.packageName)
                    activity!!.sendBroadcast(intent)
                }
//                if(binding.swWallpaper.isChecked){
//                    try {
//                        activity.functionApply()
//                        val intent = Intent(Const.Action_SetLiveWallpaper)
//                        intent.putExtra(Const.Action_StopLiveWallpaper, Const.RUN)
//                        intent.setPackage(activity.packageName)
//                        activity.sendBroadcast(intent)
//                    } catch (e: Exception) {}
//                }
            } catch (e: Exception) {}
        }

        fun chooseType(str: String?) {
            binding!!.apply {
                typeLine!!.setBackgroundResource(R.drawable.ic_icon_rectangles)
                typeDot!!.setBackgroundResource(R.drawable.ic_icon_rectangles)
                typeHeart!!.setBackgroundResource(R.drawable.ic_icon_rectangles)
                typeSun!!.setBackgroundResource(R.drawable.ic_icon_rectangles)
                typeMoon!!.setBackgroundResource(R.drawable.ic_icon_rectangles)
                typeSnow!!.setBackgroundResource(R.drawable.ic_icon_rectangles)
                typeFlower!!.setBackgroundResource(R.drawable.ic_icon_rectangles)
                typeMoon1!!.setBackgroundResource(R.drawable.ic_icon_rectangles)
                typeEmoji!!.setBackgroundResource(R.drawable.ic_icon_rectangles)
                typeChrismis!!.setBackgroundResource(R.drawable.ic_icon_rectangles)
                typeFoot!!.setBackgroundResource(R.drawable.ic_icon_rectangles)
                typeSpaceship!!.setBackgroundResource(R.drawable.ic_icon_rectangles)
                typeStar!!.setBackgroundResource(R.drawable.ic_icon_rectangles)
                typeArt1!!.setBackgroundResource(R.drawable.ic_icon_rectangles)
            }

            if (str == Const.LINE) {
                binding!!.typeLine!!.setBackgroundResource(R.drawable.ic_selected)
            } else if (str == Const.HEART) {
                binding!!.typeHeart!!.setBackgroundResource(R.drawable.ic_selected)
            } else if (str == Const.DOT) {
                binding!!.typeDot!!.setBackgroundResource(R.drawable.ic_selected)
            } else if (str == Const.SUN) {
                binding!!.typeSun!!.setBackgroundResource(R.drawable.ic_selected)
            } else if (str == Const.MOON) {
                binding!!.typeMoon!!.setBackgroundResource(R.drawable.ic_selected)
            } else if (str == Const.SNOW) {
                binding!!.typeSnow!!.setBackgroundResource(R.drawable.ic_selected)
            } else if (str == Const.FLOWERART) {
                binding!!.typeFlower!!.setBackgroundResource(R.drawable.ic_selected)
            } else if (str == Const.MOON1) {
                binding!!.typeMoon1!!.setBackgroundResource(R.drawable.ic_selected)
            } else if (str == Const.EMOJI) {
                binding!!.typeEmoji!!.setBackgroundResource(R.drawable.ic_selected)
            } else if (str == Const.CHRISMISTREE) {
                binding!!.typeChrismis!!.setBackgroundResource(R.drawable.ic_selected)
            } else if (str == Const.FOOT) {
                binding!!.typeFoot!!.setBackgroundResource(R.drawable.ic_selected)
            } else if (str == Const.SPACESHIP) {
                binding!!.typeSpaceship!!.setBackgroundResource(R.drawable.ic_selected)
            } else if (str == Const.STAR) {
                binding!!.typeStar!!.setBackgroundResource(R.drawable.ic_selected)
            }else if (str == Const.ART1) {
                binding!!.typeArt1!!.setBackgroundResource(R.drawable.ic_selected)
            }
        }
    }

}
