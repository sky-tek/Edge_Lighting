package com.skytek.edgelighting.service

import android.content.Context
import android.media.MediaPlayer
import android.net.Uri
import android.service.wallpaper.WallpaperService
import android.util.Log
import android.view.SurfaceHolder
import com.skytek.edgelighting.R

class VideoWallpaperService : WallpaperService() {

    override fun onCreateEngine(): Engine {
        return VideoWallpaperEngine()
    }

    inner class VideoWallpaperEngine : Engine() {
        private var mediaPlayer: MediaPlayer? = null

        override fun onSurfaceCreated(holder: SurfaceHolder?) {
            super.onSurfaceCreated(holder)

            val sharedPreferences = applicationContext.getSharedPreferences("MyPreferences", Context.MODE_PRIVATE)
            val wallpaperPath = sharedPreferences.getString("wallpaperPath", "")
            Log.d("donttttttt", "onSurfaceCreated:${wallpaperPath} ")
            mediaPlayer = MediaPlayer()
            mediaPlayer?.setDataSource(applicationContext, Uri.parse(wallpaperPath))
            mediaPlayer?.setDisplay(holder)
            mediaPlayer?.setOnPreparedListener { mp ->
                mp.isLooping = true
                mp.start()
            }
            mediaPlayer?.prepareAsync()
        }

        override fun onSurfaceDestroyed(holder: SurfaceHolder?) {
            super.onSurfaceDestroyed(holder)
            mediaPlayer?.release()
            mediaPlayer = null
        }
    }
}
