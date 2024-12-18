package com.skytek.edgelighting.service

import android.app.ActivityManager
import android.content.Context
import android.graphics.Canvas
import android.net.Uri
import android.opengl.GLSurfaceView
import android.os.Handler
import android.os.Looper
import android.service.wallpaper.WallpaperService
import android.util.Log
import android.view.SurfaceHolder
import com.google.android.exoplayer2.C
import com.google.android.exoplayer2.DefaultRenderersFactory
import com.google.android.exoplayer2.ExoPlayer
import com.google.android.exoplayer2.MediaItem
import com.google.android.exoplayer2.Player
import com.google.android.exoplayer2.SimpleExoPlayer
import com.google.android.exoplayer2.source.MediaSource
import com.google.android.exoplayer2.source.ProgressiveMediaSource
import com.google.android.exoplayer2.trackselection.DefaultTrackSelector
import com.google.android.exoplayer2.upstream.DataSource
import com.google.android.exoplayer2.upstream.DefaultDataSourceFactory
import com.google.android.exoplayer2.upstream.FileDataSource
import com.google.android.exoplayer2.util.Util
import com.skytek.edgelighting.GLES20WallpaperRenderer
import com.skytek.edgelighting.GLES30WallpaperRenderer
import com.skytek.edgelighting.GLWallpaperRenderer
import com.skytek.edgelighting.activities.currentId
import com.skytek.edgelighting.activities.newID
import com.skytek.edgelighting.activities.oldID
import com.skytek.edgelighting.utils.Utills
import java.io.File

class GLWallpaperService : WallpaperService() {

    companion object {
        val TAG = "GLWallpaperEngine"
    }


    private inner class GLWallpaperEngine(c: Context?) : Engine() {

        private var context: Context? = null
        private var glSurfaceView: GLWallpaperSurfaceView? = null
        private var exoPlayer: ExoPlayer? = null
        private var videoSource: MediaSource? = null
        private var videopath = ""
        private var trackSelector: DefaultTrackSelector? = null
        private var renderer: GLWallpaperRenderer? = null
        private var allowSlide = false
        private val videoRotation = 0
        private val videoWidth = 1080
        private val videoHeight = 1920

        override fun isPreview(): Boolean {
            return super.isPreview()
            Log.d("asdfdasf", "receiving isPreview: ")
        }
        private inner class GLWallpaperSurfaceView(context: Context?) : GLSurfaceView(context) {
            /**
             * This is a hack. Because Android Live Wallpaper only has a Surface.
             * So we create a GLSurfaceView, and when drawing to its Surface,
             * we replace it with WallpaperEngine's Surface.
             */


            // Override onCreate and call startForegroundService


            override fun getHolder(): SurfaceHolder {
                return surfaceHolder
            }

            fun onDestroy() {
                super.onDetachedFromWindow()
            }
        }

        init {
            Log.d("asdfdasf", "jhoihi isPreview: ")
            this.context = c
            setTouchEventsEnabled(false)
        }

        override fun onCreate(surfaceHolder: SurfaceHolder?) {
            super.onCreate(surfaceHolder)
            Log.d("asdfdasf", "receiving Glvideopath: ")

        }

        override fun onSurfaceCreated(holder: SurfaceHolder?) {
            super.onSurfaceCreated(holder)
            createGLSurfaceView()
            val sharedPreferences =
                context!!.getSharedPreferences("MyPreferences", Context.MODE_PRIVATE)
            val editor = sharedPreferences.edit()

            videopath = newID?:""
            Log.d("asdfdasf", "receiving Glvideopath: " + videopath)
            startPlayer()


        }

        override fun onVisibilityChanged(visible: Boolean) {
            super.onVisibilityChanged(visible)
            if (renderer != null) {
                if (visible) {
                    Log.d("abcd", "visible")
                    glSurfaceView!!.onResume()
                    startPlayer()
                } else {
                    Log.d("abcd", "pause")
                    stopPlayer()
                    glSurfaceView!!.onPause()
                    // Prevent useless renderer calculating.
                    allowSlide = false
                }
            }
        }

        override fun onOffsetsChanged(
            xOffset: Float,
            yOffset: Float,
            xOffsetStep: Float,
            yOffsetStep: Float,
            xPixelOffset: Int,
            yPixelOffset: Int
        ) {
            super.onOffsetsChanged(
                xOffset, yOffset, xOffsetStep, yOffsetStep, xPixelOffset, yPixelOffset
            )
            renderer!!.setOffset(0.5f - xOffset, 0.5f - yOffset)
        }

        override fun onSurfaceChanged(
            holder: SurfaceHolder?, format: Int, width: Int, height: Int
        ) {
            super.onSurfaceChanged(holder, format, width, height)
            renderer!!.setScreenSize(width, height)
        }

        override fun onSurfaceDestroyed(holder: SurfaceHolder?) {
            super.onSurfaceDestroyed(holder)
            stopPlayer()
            Log.d("abcd", "destry")

            glSurfaceView!!.onDestroy()
        }

        override fun onDestroy() {
            super.onDestroy()
            Log.d("abcd", "destry1")
        }

        private fun createGLSurfaceView() {
            if (glSurfaceView != null) {
                glSurfaceView!!.onDestroy()
                glSurfaceView = null
            }
            glSurfaceView = GLWallpaperSurfaceView(context)
            val activityManager = getSystemService(
                ACTIVITY_SERVICE
            ) as ActivityManager
            val configInfo = activityManager.deviceConfigurationInfo
            if (configInfo.reqGlEsVersion >= 0x30000) {
                Utills.debug(TAG, "Support GLESv3")
                glSurfaceView!!.setEGLContextClientVersion(3)
                renderer = GLES30WallpaperRenderer(context!!)
            } else if (configInfo.reqGlEsVersion >= 0x20000) {
                Utills.debug(TAG, "Fallback to GLESv2")
                glSurfaceView!!.setEGLContextClientVersion(2)
                renderer = GLES20WallpaperRenderer(context!!)
            } else {
                throw RuntimeException("Needs GLESv2 or higher")
            }
            glSurfaceView!!.preserveEGLContextOnPause = true
            glSurfaceView!!.setRenderer(renderer)
            // On demand render will lead to black screen.
            glSurfaceView!!.renderMode = GLSurfaceView.RENDERMODE_CONTINUOUSLY
        }

        private fun startPlayer() {
            stopPlayer()

            // Determine the ID
            currentId = if (isPreview) newID else oldID
            currentId?.let {
                Log.d("startPlayer", "Using ID: $currentId")
                val sharedPreferences = context!!.getSharedPreferences("MyPreferences", Context.MODE_PRIVATE)
                sharedPreferences.edit().putString("wallpaperPath", currentId).apply()
            }

            // Fetch video path from preferences
            videopath = context!!.getSharedPreferences("MyPreferences", Context.MODE_PRIVATE)
                .getString("wallpaperPath", "") ?: return

            if (videopath.isNullOrEmpty() || !File(videopath).exists()) {
                Log.e("startPlayer", "Invalid video path: $videopath")
                return
            }

            Log.d("startPlayercurrentId", "Initializing player with path: $videopath")
            Log.d("startPlayercurrentId", "Initializing player with currentIdpath: $currentId")

            // Initialize ExoPlayer
            trackSelector = DefaultTrackSelector(context!!)
            trackSelector!!.parameters = trackSelector!!.buildUponParameters()
                .setForceHighestSupportedBitrate(true)
                .build()

            exoPlayer = SimpleExoPlayer.Builder(context!!)
                .setTrackSelector(trackSelector!!)
                .build()

            // Prepare MediaSource
            val mediaItem = MediaItem.fromUri(Uri.parse(videopath))
            val appName = context?.applicationInfo?.let {
                context!!.packageManager.getApplicationLabel(it).toString()
            } ?: "com.skytek.edgelighting"
            Log.d("startPlayer", "startPlayer: $appName")
            val dataSourceFactory = DefaultDataSourceFactory(
                context!!, Util.getUserAgent(context!!, appName)
            )

            videoSource = ProgressiveMediaSource.Factory(dataSourceFactory).createMediaSource(mediaItem)

            renderer?.apply {
                setVideoSizeAndRotation(videoWidth, videoHeight, videoRotation)
                setSourcePlayer(exoPlayer!!)
            }

            exoPlayer?.apply {
                setMediaSource(videoSource!!)
                prepare()
                repeatMode = Player.REPEAT_MODE_ALL
                playWhenReady = true
                volume = 0f
            }

            Log.d("startPlayer", "Player started successfully with path: $videopath")
        }




        private val handler = Handler(Looper.getMainLooper())

        private fun stopPlayer(){
            try {
                Log.d("checkthevisibility" , "stopPlayer")
                if (exoPlayer != null) {
                    if (exoPlayer!!.playWhenReady) {
                        exoPlayer?.playWhenReady = false
                        exoPlayer?.volume = 0f
                        exoPlayer?.stop()
                    }
                    exoPlayer?.release()
                    exoPlayer = null
                }
                videoSource = null
                trackSelector = null
            } catch (e: Exception) {
                //IGNORE
            }
        }


        var lockCanvas: Canvas? = null

    }


    override fun onCreateEngine(): Engine {
        return GLWallpaperEngine(applicationContext)
    }

}

