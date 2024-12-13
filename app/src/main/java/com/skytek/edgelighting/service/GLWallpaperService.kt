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
import com.google.android.exoplayer2.ExoPlayer
import com.google.android.exoplayer2.MediaItem
import com.google.android.exoplayer2.Player
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

            videopath = sharedPreferences!!.getString("wallpaperPath", "")!!
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

            try {
                if (exoPlayer != null) {
                    stopPlayer()
                }
                if (isPreview) {
                    if (newID != null) {

                        currentId = newID
                        Log.d("fdsdbgv", "ispreview")
                        Log.d("fdsdbgv", currentId!!)
                    }
                } else {

                    if (oldID != null) {
                        currentId = oldID
                        Log.d("fdsdbgv", "isnotpreview")
                        Log.d("fdsdbgv", currentId!!)

                    }
                }
                if (context != null) {
                    trackSelector = DefaultTrackSelector(context!!)
                }

                exoPlayer = ExoPlayer.Builder(context!!).build()
                exoPlayer!!.volume = 0.0f
                // Disable audio decoder.

                exoPlayer!!.repeatMode = Player.REPEAT_MODE_ALL
                val dataSourceFactory: DataSource.Factory = DefaultDataSourceFactory(
                    context!!, Util.getUserAgent(context!!, "com.skytek.edgelighting")
                )

                // ExoPlayer can load file:///android_asset/ uri correctly.
//            /storage/emulated/0/Android/data/com.skytek.live.wallpapers/files/live/298

                Log.d("q12qz", getExternalFilesDir("live")!!.absolutePath + "/" + currentId)
                Log.d("45434", "current id in start " + currentId.toString())
                val sharedPreferences =
                    context!!.getSharedPreferences("MyPreferences", Context.MODE_PRIVATE)
                if (currentId!=null) {
                    sharedPreferences.edit().apply {
                        putString("wallpaperPath", currentId)
                        apply()
                    }
                }

                videopath = sharedPreferences!!.getString("wallpaperPath", "")!!

                Log.d("donttttttt", "current video id in start $videopath")
                // ExoPlayer can load file:///android_asset/ uri correctly.
                val audioSource: MediaSource =
                    ProgressiveMediaSource.Factory(FileDataSource.Factory()).createMediaSource(
                        MediaItem.fromUri(
                            Uri.parse(videopath)
                        )
                    )
                videoSource = audioSource

                // Let we assume video has correct info in metadata, or user should fix it.
                renderer!!.setVideoSizeAndRotation(videoWidth, videoHeight, videoRotation)
                // This must be set after getting video info.
                //      renderer!!.setSourcePlayer(exoPlayer!!)
                renderer?.setExoPlayer(exoPlayer!!)
                exoPlayer!!.prepare(videoSource!!)
                exoPlayer!!.play()

                // ExoPlayer's video size changed listener is buggy. Don't use it.
                // It give's width and height after rotation, but did not rotate frames.
                exoPlayer!!.playWhenReady = true

            } catch (e: NullPointerException) {
            }


        }

        private val handler = Handler(Looper.getMainLooper())

        fun stopPlayer() {
            handler.post {
                try {
                    if (exoPlayer != null) {
                        if (exoPlayer!!.playWhenReady) {
                            Utills.debug(TAG, "Player stopping")
                            exoPlayer!!.playWhenReady = false
                            exoPlayer!!.stop()
                        }
                        exoPlayer!!.release()
                        exoPlayer = null
                    }
                    videoSource = null
                    trackSelector = null
                } catch (e: NullPointerException) {
                    // Handle exception
                }
            }
        }


        var lockCanvas: Canvas? = null

    }


    override fun onCreateEngine(): Engine {
        return GLWallpaperEngine(applicationContext)
    }

}

