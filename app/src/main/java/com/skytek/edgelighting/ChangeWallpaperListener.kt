package com.skytek.edgelighting

import android.content.Context
import android.graphics.Bitmap
import android.graphics.BitmapFactory
import android.graphics.Canvas
import android.graphics.drawable.BitmapDrawable
import android.graphics.drawable.Drawable
import android.util.Log
import com.skytek.edgelighting.activities.EdgeOverlaySettingsActivity
import com.skytek.edgelighting.fragment.WallpaperFragment
import com.skytek.edgelighting.fragment.WallpaperFragment.Companion.wallpaper
import com.skytek.edgelighting.utils.Const
import com.skytek.edgelighting.utils.MySharePreferencesEdge
import com.skytek.edgelighting.views.CustomEdgeImage

class ChangeWallpaperListener(private val animate: EdgeLightingAnimate, private val context: Context, private val width: Int, private val height: Int) {

    fun lisenerChangeColor(str: String) {
        var str3: String?
        var str4: String?
        var str5: String?
        var str6: String?
        var str7: String?
        var str2: String?
        if (str == Const.Action_DemoLiveWallpaper) {
            str2 = MySharePreferencesEdge.getString(MySharePreferencesEdge.COLOR1, context)
            str7 = MySharePreferencesEdge.getString(MySharePreferencesEdge.COLOR2, context)
            str6 = MySharePreferencesEdge.getString(MySharePreferencesEdge.COLOR3, context)
            str5 = MySharePreferencesEdge.getString(MySharePreferencesEdge.COLOR4, context)
            str4 = MySharePreferencesEdge.getString(MySharePreferencesEdge.COLOR5, context)
            str3 = MySharePreferencesEdge.getString(MySharePreferencesEdge.COLOR6, context)
        } else {
            str2 = MySharePreferencesEdge.getString(MySharePreferencesEdge.FINISH_COLOR1, context)
            str7 = MySharePreferencesEdge.getString(MySharePreferencesEdge.FINISH_COLOR2, context)
            str6 = MySharePreferencesEdge.getString(MySharePreferencesEdge.FINISH_COLOR3, context)
            str5 = MySharePreferencesEdge.getString(MySharePreferencesEdge.FINISH_COLOR4, context)
            str4 = MySharePreferencesEdge.getString(MySharePreferencesEdge.FINISH_COLOR5, context)
            str3 = MySharePreferencesEdge.getString(MySharePreferencesEdge.FINISH_COLOR6, context)
        }
        if (str2 == null) {
            str2 = "#EB1111"
        }
        if (str7 == null) {
            str7 = "#EBDA11"
        }
        if (str6 == null) {
            str6 = "#11EB37"
        }
        if (str5 == null) {
            str5 = "#EB1111"
        }
        if (str4 == null) {
            str4 = "#EBDA11"
        }
        if (str3 == null) {
            str3 = "#11EB37"
        }
        try {
            if(MySharePreferencesEdge.getWallpaperBooleanValue(MySharePreferencesEdge.WALL_PAPER, context)){
                animate.changeColor(intArrayOf(EdgeOverlaySettingsActivity.color[0], EdgeOverlaySettingsActivity.color[1], EdgeOverlaySettingsActivity.color[2], EdgeOverlaySettingsActivity.color[3], EdgeOverlaySettingsActivity.color[4], EdgeOverlaySettingsActivity.color[5],
                    EdgeOverlaySettingsActivity.color[6]))
            }else{
                animate.changeColor(intArrayOf(WallpaperFragment.color[0], WallpaperFragment.color[1], WallpaperFragment.color[2], WallpaperFragment.color[3], WallpaperFragment.color[4],
                    WallpaperFragment.color[5], WallpaperFragment.color[6]))
            }
        } catch (e: ArrayIndexOutOfBoundsException) {}
    }

    fun lisenerChangeBorder(str: String) {
        val i2: Int
        val i3: Int
        val i4: Int
        val i: Int
        if (str == Const.Action_DemoLiveWallpaper) {
            i = MySharePreferencesEdge.getInt(MySharePreferencesEdge.SPEED, context)
            i4 = MySharePreferencesEdge.getInt(MySharePreferencesEdge.SIZE, context)
            i3 = MySharePreferencesEdge.getInt(MySharePreferencesEdge.RADIUSTOP, context)
            i2 = MySharePreferencesEdge.getInt(MySharePreferencesEdge.RADIUSBOTTOM, context)
        } else {
            i = MySharePreferencesEdge.getInt(MySharePreferencesEdge.FINISH_SPEED, context)
            i4 = MySharePreferencesEdge.getInt(MySharePreferencesEdge.FINISH_SIZE, context)
            i3 = MySharePreferencesEdge.getInt(MySharePreferencesEdge.FINISH_RADIUSTOP, context)
            i2 = MySharePreferencesEdge.getInt(MySharePreferencesEdge.FINISH_RADIUSBOTTOM, context)
        }
        Log.d("vbwq", i.toFloat().toString() + " " + i4.toString() + " " + i3.toString() + " " + i2.toString())
        animate.changeSpeed(i.toFloat())
        animate.changeSize(i4)
        animate.changeRadius(i3, i2)
    }

    fun lisenerChangeBorder(str: String,tag:String) {
        val i2: Int
        val i3: Int
        val i4: Int
        val i: Int
        if (str == Const.Action_DemoLiveWallpaper) {
            i = MySharePreferencesEdge.getInt(MySharePreferencesEdge.SPEED, context)
            i4 = MySharePreferencesEdge.getInt(MySharePreferencesEdge.SIZE, context)
            i3 = MySharePreferencesEdge.getInt(MySharePreferencesEdge.RADIUSTOP, context)
            i2 = MySharePreferencesEdge.getInt(MySharePreferencesEdge.RADIUSBOTTOM, context)
        } else {
            i = MySharePreferencesEdge.getInt(MySharePreferencesEdge.FINISH_SPEED, context)
            i4 = MySharePreferencesEdge.getInt(MySharePreferencesEdge.FINISH_SIZE, context)
            i3 = MySharePreferencesEdge.getInt(MySharePreferencesEdge.FINISH_RADIUSTOP, context)
            i2 = MySharePreferencesEdge.getInt(MySharePreferencesEdge.FINISH_RADIUSBOTTOM, context)
        }
        Log.d("vbwq", i.toFloat().toString() + " " + i4.toString() + " " + i3.toString() + " " + i2.toString())
        animate.changeSpeed(0F)
        animate.changeSize(0)
        animate.changeRadius(0, 0)
    }

    fun lisenerChangeNotch(str: String) {
        val i: Int
        val i2: Int
        val i3: Int
        val i4: Int
        val i5: Int
        val z: Boolean
        if (str == Const.Action_DemoLiveWallpaper) {
            z = MySharePreferencesEdge.getBooleanValue(MySharePreferencesEdge.CHECKNOTCH, context)
            i5 = MySharePreferencesEdge.getInt(MySharePreferencesEdge.NOTCHTOP, context)
            i4 = MySharePreferencesEdge.getInt(MySharePreferencesEdge.NOTCHRADIUSTOP, context)
            i3 = MySharePreferencesEdge.getInt(MySharePreferencesEdge.NOTCHRADIUSBOTTOM, context)
            i2 = MySharePreferencesEdge.getInt(MySharePreferencesEdge.NOTCHBOTTOM, context)
            i = MySharePreferencesEdge.getInt(MySharePreferencesEdge.NOTCHHEIGHT, context)
        } else {
            z = MySharePreferencesEdge.getBooleanValue(MySharePreferencesEdge.FINISH_CHECKNOTCH, context)
            i5 = MySharePreferencesEdge.getInt(MySharePreferencesEdge.FINISH_NOTCHTOP, context)
            i4 = MySharePreferencesEdge.getInt(MySharePreferencesEdge.FINISH_NOTCHRADIUSTOP, context)
            i3 = MySharePreferencesEdge.getInt(MySharePreferencesEdge.FINISH_NOTCHRADIUSBOTTOM, context)
            i2 = MySharePreferencesEdge.getInt(MySharePreferencesEdge.FINISH_NOTCHBOTTOM, context)
            i = MySharePreferencesEdge.getInt(MySharePreferencesEdge.FINISH_NOTCHHEIGHT, context)
        }
        animate.changeNotch(z, i5, i2, i, i4, i3)
    }

    fun listnerChangeBackground_Edited() {
        try {
//                animate.setBitmap(Bitmap.createScaledBitmap(drawableToBitmap(binding!!.imageBackground!!.drawable), width, height, false))
            animate.setBitmap(Bitmap.createScaledBitmap(wallpaper!!, width, height, false))
        } catch (e: NullPointerException) {}
        catch (e: OutOfMemoryError) {}
    }

    fun lisenerChangeType(str: String) {
        val str2: String?
        str2 = if (str == Const.Action_DemoLiveWallpaper) {
            MySharePreferencesEdge.getString(MySharePreferencesEdge.SHAPE, context)
        } else {
            MySharePreferencesEdge.getString(MySharePreferencesEdge.FINISH_SHAPE, context)
        }
        if (str2 == null) {
            return
        }
        Log.d("vbwq", str2)
        if (str2 == Const.LINE) {
            animate.changeShape(str2, null)
        } else if (str2 == Const.HEART) {
            animate.changeShape(str2, BitmapFactory.decodeResource(context.resources, R.drawable.heart_100px))
        } else if (str2 == Const.DOT) {
            animate.changeShape(str2, BitmapFactory.decodeResource(context.resources, R.drawable.dots))
        } else if (str2 == Const.SUN) {
            animate.changeShape(str2, BitmapFactory.decodeResource(context.resources, R.drawable.sun_100px))
        } else if (str2 == Const.MOON) {
            animate.changeShape(str2, BitmapFactory.decodeResource(context.resources, R.drawable.moon_100px))
        } else if (str2 == Const.SNOW) {
            animate.changeShape(str2, BitmapFactory.decodeResource(context.resources, R.drawable.ic_snowflake))
        } else if (str2 == Const.FLOWERART) {
            animate.changeShape(str2, BitmapFactory.decodeResource(context.resources, R.drawable.ic_7))
        } else if (str2 == Const.MOON1) {
            animate.changeShape(str2, BitmapFactory.decodeResource(context.resources, R.drawable.ic_3))
        } else if (str2 == Const.EMOJI) {
            animate.changeShape(str2, BitmapFactory.decodeResource(context.resources, R.drawable.ic_1))
        } else if (str2 == Const.CHRISMISTREE) {
            animate.changeShape(str2, BitmapFactory.decodeResource(context.resources, R.drawable.ic_14))
        } else if (str2 == Const.FOOT) {
            animate.changeShape(str2, BitmapFactory.decodeResource(context.resources, R.drawable.ic_6))
        } else if (str2 == Const.SPACESHIP) {
            animate.changeShape(str2, BitmapFactory.decodeResource(context.resources, R.drawable.ic_15))
        } else if (str2 == Const.STAR) {
            animate.changeShape(str2, BitmapFactory.decodeResource(context.resources, R.drawable.ic_18))
        } else if (str2 == Const.ART1) {
            animate.changeShape(str2, BitmapFactory.decodeResource(context.resources, R.drawable.ic_21))
        }else {
            animate.changeShape(str2, null)
        }
    }

    companion object {
        private const val TAG = "LisenerChangeWallpaper"
        @JvmStatic
        fun drawableToBitmap(drawable: Drawable): Bitmap {
            val bitmap: Bitmap
            if (drawable is BitmapDrawable) {
                val bitmapDrawable = drawable
                if (bitmapDrawable.bitmap != null) {
                    return bitmapDrawable.bitmap
                }
            }
            bitmap = if (drawable.intrinsicWidth <= 0 || drawable.intrinsicHeight <= 0) {
                Bitmap.createBitmap(1, 1, Bitmap.Config.ARGB_8888)
            } else {
                Bitmap.createBitmap(
                    drawable.intrinsicWidth,
                    drawable.intrinsicHeight,
                    Bitmap.Config.ARGB_8888
                )
            }
            val canvas = Canvas(bitmap)
            drawable.setBounds(0, 0, canvas.width, canvas.height)
            drawable.draw(canvas)
            return bitmap
        }

        var edge_image_view : CustomEdgeImage? = null
    }
}