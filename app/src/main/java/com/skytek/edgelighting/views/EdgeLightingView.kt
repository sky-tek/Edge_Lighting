package com.skytek.edgelighting.views

import android.content.Context
import android.content.res.Configuration
import android.graphics.Bitmap
import android.graphics.Canvas
import android.util.AttributeSet
import android.util.Log
import android.view.View
import com.skytek.edgelighting.ChangeWallpaperListener
import com.skytek.edgelighting.DecodeEdgeResource
import com.skytek.edgelighting.EdgeLightingAnimate

class EdgeLightingView : View {
    private var animate: EdgeLightingAnimate? = null
    private var changeWallpaper: ChangeWallpaperListener? = null

    constructor(context: Context?) : super(context) {
        init()
    }

    constructor(context: Context?, attributeSet: AttributeSet?) : super(context, attributeSet) {
        init()
    }

    constructor(context: Context?, attributeSet: AttributeSet?, i: Int) : super(
        context,
        attributeSet,
        i
    ) {
        init()
    }

    public override fun onLayout(z: Boolean, i: Int, i2: Int, i3: Int, i4: Int) {
        super.onLayout(z, i, i2, i3, i4)


        val edgeBorderLightAnimate = animate
        if (edgeBorderLightAnimate != null) {
            edgeBorderLightAnimate.onLayout(width, height)
            invalidate()
        }
    }

    public override fun onDraw(canvas: Canvas) {


        val edgeBorderLightAnimate = animate
        if (edgeBorderLightAnimate != null) {
            edgeBorderLightAnimate.onDraw(canvas)
            postInvalidateDelayed(30)
        }
    }

    private fun init() {

        val edgeBorderLightAnimate = EdgeLightingAnimate(context)
        animate = edgeBorderLightAnimate
        changeWallpaper = ChangeWallpaperListener(
            edgeBorderLightAnimate,
            context,
            context.resources.displayMetrics.widthPixels,
            context.resources.displayMetrics.heightPixels
        )
    }

    fun changeColor(str: String?) {
        changeWallpaper!!.lisenerChangeColor(str!!)
    }

    fun changeSpeed(str: String?) {
        changeWallpaper!!.lisenerChangeBorder(str!!)
    }

    fun changeSize(str: String?) {
        changeWallpaper!!.lisenerChangeBorder(str!!)
    }

    fun changeRadius(str: String?) {
        changeWallpaper!!.lisenerChangeBorder(str!!)
    }

    fun changeNotch(str: String?) {
        changeWallpaper!!.lisenerChangeNotch(str!!)
    }

    fun setShape(str: String?) {
        changeWallpaper!!.lisenerChangeType(str!!)
    }


    fun changeColor(iArr: IntArray?) {
        animate!!.changeColor(iArr)
    }

    fun changeSize(i: Int) {
        animate!!.changeSize(i)
    }

    fun changeSpeed(i: Int) {
        Log.d("checkanimatespeed", "animate speed called")
        animate!!.changeSpeed(i.toFloat())
    }

    fun changeBorder(i: Int, i2: Int) {
        Log.d("checkanimatespeed", "animate speed called")
        animate!!.changeRadius(i, i2)
    }

    fun changeNotch(z: Boolean, i: Int, i2: Int, i3: Int, i4: Int, i5: Int) {
        animate!!.changeNotch(z, i, i2, i3, i4, i5)
    }

    fun changeType(str: String?) {
        if (str != null) {
            try {
                DecodeEdgeResource(context, str, object : DecodeEdgeResource.CallBack {
                    override fun decodeDone(bitmap: Bitmap?) {
                        animate?.changeShape(str, bitmap)
                    }
                }).execute(*arrayOfNulls<Void>(0))
            } catch (e: NullPointerException) {
                // Handle exception
            }
        } else {
            Log.e("changeType", "String is null")
        }
    }


    fun changeRotate(z: Boolean) {
        animate!!.changeRotate(z)
    }

    companion object {
        private const val TAG = "EdgeLightView"
    }
}
