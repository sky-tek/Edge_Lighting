package com.skytek.edgelighting

import android.content.Context
import android.os.AsyncTask
import android.graphics.Bitmap
import android.graphics.BitmapFactory
import android.widget.Toast
import com.skytek.edgelighting.utils.Const

class DecodeEdgeResource(private val context: Context, private val shape: String, private val callBack: CallBack) : AsyncTask<Void?, Void?, Bitmap?>() {
    interface CallBack {
        fun decodeDone(bitmap: Bitmap?)
    }

    override fun doInBackground(vararg p0: Void?): Bitmap? {
        return getBitMap(shape)
    }

    public override fun onPostExecute(bitmap: Bitmap?) {
        callBack.decodeDone(bitmap)
    }

    private fun getBitMap(str: String): Bitmap? {
        var bitmap:Bitmap
        if (str == "") {
            return null
        }
        if (str == Const.LINE) {
            return null
        }
        try {
            if (str == Const.HEART) {
                return BitmapFactory.decodeResource(context.resources, R.drawable.heart_100px)
            }
            if (str == Const.SNOW) {
                return BitmapFactory.decodeResource(context.resources, R.drawable.ic_snowflake)
            }
            if (str == Const.DOT) {
                return BitmapFactory.decodeResource(context.resources, R.drawable.dots)
            }
            if (str == Const.SUN) {
                return BitmapFactory.decodeResource(context.resources, R.drawable.sun_100px)
            }
            if (str == Const.MOON) {
                return BitmapFactory.decodeResource(context.resources, R.drawable.moon_100px)
            }
            if (str == Const.FLOWERART) {
                return BitmapFactory.decodeResource(context.resources, R.drawable.ic_7)
            }
            if (str == Const.MOON1) {
                return BitmapFactory.decodeResource(context.resources, R.drawable.ic_3)
            }
            if (str == Const.EMOJI) {
                return BitmapFactory.decodeResource(context.resources, R.drawable.ic_1)
            }
            if (str == Const.CHRISMISTREE) {
                return BitmapFactory.decodeResource(context.resources, R.drawable.ic_14)
            }
            if (str == Const.FOOT) {
                return BitmapFactory.decodeResource(context.resources, R.drawable.ic_6)
            }
            if (str == Const.SPACESHIP) {
                return BitmapFactory.decodeResource(context.resources, R.drawable.ic_15)
            }
            if (str == Const.STAR) {
                return BitmapFactory.decodeResource(context.resources, R.drawable.ic_18)
            }
            if(str == Const.ART1){
                return BitmapFactory.decodeResource(context.resources, R.drawable.ic_21)
            }
        } catch (e: OutOfMemoryError) {}
        return null
    }
}

