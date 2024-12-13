package com.skytek.edgelighting

import android.annotation.SuppressLint
import android.content.BroadcastReceiver
import android.content.Context
import android.content.Intent
import android.content.IntentFilter
import android.graphics.Canvas
import android.os.Build
import android.os.Handler
import android.os.Looper
import android.service.wallpaper.WallpaperService
import android.util.Log
import android.view.SurfaceHolder
import com.skytek.edgelighting.activities.EdgeOverlaySettingsActivity
import com.skytek.edgelighting.utils.Const
import com.skytek.edgelighting.utils.MySharePreferencesEdge

class WallpaperService : WallpaperService() {

    override fun onCreateEngine(): Engine {
        return MyWallpaperEngine()
    }
    private inner class MyWallpaperEngine : Engine() {
        private val animate: EdgeLightingAnimate?
        private val changeWallpaper: ChangeWallpaperListener
        var checkCavas = true
        var checkRun = true
        lateinit var drawRunner: Runnable
        lateinit var handler: Handler
        private var height: Int
        private val liveWallpaperBroadcast: LisenerSetLiveWallpaper
        private var width: Int

        override fun onVisibilityChanged(z: Boolean) {
            handler.removeCallbacks(drawRunner)
            handler.post(drawRunner)
        }

        override fun onSurfaceDestroyed(surfaceHolder: SurfaceHolder) {
            super.onSurfaceDestroyed(surfaceHolder)
            handler.removeCallbacks(drawRunner)
        }

        override fun onSurfaceChanged(surfaceHolder: SurfaceHolder, i: Int, i2: Int, i3: Int) {
            val edgeBorderLightAnimate = animate
            edgeBorderLightAnimate?.onLayout(i2, i3)
            super.onSurfaceChanged(surfaceHolder, i, i2, i3)
        }

        override fun onCreate(surfaceHolder: SurfaceHolder) {
            Log.d("isuuueeeeeeeeeeeeeee", "onCreate:yyyyyyyyyyyyyyyyyyyyyyyyyyyyyy ")
            super.onCreate(surfaceHolder)
        }

        var lockCanvas:Canvas? = null
        private var locked = false

        @SuppressLint("WrongConstant")
        private fun draw() {
            val surfaceHolder = surfaceHolder
            try {
                lockCanvas = surfaceHolder.lockCanvas()
//                if(!locked){
//                    lockCanvas = surfaceHolder.lockCanvas()
//                    locked = true
//                }
                if (!(lockCanvas == null || animate == null)) {
                    if (checkCavas) {
                        if(MySharePreferencesEdge.getShowChargingBooleanValue(MySharePreferencesEdge.SHOW_WHILE_CHARGING, applicationContext)){
                            changeWallpaper.listnerChangeBackground_Edited()
                            changeWallpaper.lisenerChangeBorder(Const.Action_FinishLiveWallpaper,"wallpaper")
                        }else{
                            changeWallpaper.lisenerChangeType(Const.Action_FinishLiveWallpaper)
                            changeWallpaper.listnerChangeBackground_Edited()
                            changeWallpaper.lisenerChangeNotch(Const.Action_FinishLiveWallpaper)
                            changeWallpaper.lisenerChangeBorder(Const.Action_FinishLiveWallpaper)
                            changeWallpaper.lisenerChangeColor(Const.Action_FinishLiveWallpaper)
                        }
                        checkCavas = false
                    }
                    animate.onDraw(lockCanvas)
                }
                if (lockCanvas != null) {
                    try {
                        surfaceHolder.unlockCanvasAndPost(lockCanvas)
                    } catch (e: Exception) {}
                }
//                if (locked) {
//                    surfaceHolder.unlockCanvasAndPost(lockCanvas)
//                    locked = false
//                }
            } catch (th: IllegalArgumentException) {}
        }

        override fun onDestroy() {
            super.onDestroy()
            try {
                unregisterReceiver(liveWallpaperBroadcast)
            } catch (e: IllegalArgumentException) {}
        }

        inner class LisenerSetLiveWallpaper : BroadcastReceiver() {

            override fun onReceive(context: Context, intent: Intent) {
                if (intent.action != Const.Action_SetLiveWallpaper) {
                    return
                }
                if (intent.getStringExtra(Const.Action_StopLiveWallpaper) == Const.STOP) {
                    handler.removeCallbacks(drawRunner)
                    checkRun = false

                    return
                }
                checkCavas = true
                handler.removeCallbacks(drawRunner)
                handler.postDelayed(drawRunner, 1)
                checkRun = true
            }
        }

        init {
            val r0 = Runnable {
                if (checkRun) {
                    try {
                        draw()
                    } catch (e: IllegalArgumentException) {}
                    handler.removeCallbacks(drawRunner)
                    handler.postDelayed(drawRunner, 30)
                }
            }
            drawRunner = r0
            val handler2 = Handler(Looper.getMainLooper())
            handler = handler2
            height = 0
            width = 0
            handler2.removeCallbacks(r0)
            handler2.post(r0)
            val lisenerSetLiveWallpaper = LisenerSetLiveWallpaper()
            liveWallpaperBroadcast = lisenerSetLiveWallpaper
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.TIRAMISU) {
                this@WallpaperService.registerReceiver(lisenerSetLiveWallpaper, IntentFilter(Const.Action_SetLiveWallpaper),
                    RECEIVER_EXPORTED)
            }
            animate = EdgeLightingAnimate(this@WallpaperService.applicationContext)
            this@WallpaperService.resources.getDimensionPixelSize(this@WallpaperService.resources.getIdentifier("navigation_bar_height", "dimen", "android"))
            width = MySharePreferencesEdge.getInt(MySharePreferencesEdge.WIDTH, this@WallpaperService.baseContext)
            height = MySharePreferencesEdge.getInt(MySharePreferencesEdge.HEIGHT, this@WallpaperService.baseContext)
            changeWallpaper = ChangeWallpaperListener(animate, this@WallpaperService.applicationContext, width, height)
        }
    }
}

