package com.skytek.edgelighting.activities

import android.app.WallpaperManager
import android.content.ComponentName
import android.content.Context
import android.content.Intent
import android.os.Build
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.util.Log
import android.view.View
import android.widget.Button
import android.widget.ProgressBar
import android.widget.VideoView
import androidx.annotation.RequiresApi
import androidx.core.content.ContextCompat
import androidx.swiperefreshlayout.widget.SwipeRefreshLayout
import com.google.android.exoplayer2.ExoPlayer
import com.google.android.exoplayer2.MediaItem


import com.google.android.exoplayer2.Player

import com.google.android.exoplayer2.SimpleExoPlayer

import com.google.android.exoplayer2.source.ProgressiveMediaSource
import com.google.android.exoplayer2.ui.PlayerView
import com.google.android.exoplayer2.upstream.DefaultHttpDataSource
import com.google.android.material.bottomsheet.BottomSheetDialog
import com.skytek.edgelighting.R
import com.skytek.edgelighting.databinding.ActivityLiveWallpaperPreviewBinding
import com.skytek.edgelighting.service.GLWallpaperService


class LiveWallpaperPreviewActivity : AppCompatActivity() {
    private lateinit var binding:ActivityLiveWallpaperPreviewBinding
    lateinit var btnShowBottomSheet: Button
    lateinit var dialog : BottomSheetDialog
    private lateinit var progressBar: ProgressBar
    private lateinit var swipeRefreshLayout: SwipeRefreshLayout
    private lateinit var fullScreenWallpaperImageView: VideoView
    private lateinit var playerView: PlayerView
    private lateinit var player: SimpleExoPlayer
    var wallpaperPath=""
    @RequiresApi(Build.VERSION_CODES.N)
    override fun onCreate(savedInstanceState: Bundle?) {

        super.onCreate(savedInstanceState)
        binding=ActivityLiveWallpaperPreviewBinding.inflate(layoutInflater)
        setContentView(binding.root)

        window.statusBarColor = ContextCompat.getColor(this, R.color.background)


        progressBar = findViewById(R.id.progressBar) // Initialize progressBar
        showProgressBar()


         wallpaperPath = intent.getStringExtra("wallpaperPath")!!
        Log.d("abcd",wallpaperPath!!)
        val sharedPreferences = this.getSharedPreferences("MyPreferences", Context.MODE_PRIVATE)
        val editor = sharedPreferences.edit()
        editor.putString("wallpaperPath", wallpaperPath)
        editor.commit()
        editor.apply()

        Log.d("dsdsdhksdhjskd", "onCreate: " + wallpaperPath)
        // ExoPlayer setup
        var exoPlayer = ExoPlayer.Builder(this).build()
        // Initialize your PlayerView
        val playerView = findViewById<PlayerView>(R.id.fullScreenWallpaperImageView)
        playerView.player = exoPlayer

        val dataSourceFactory = DefaultHttpDataSource.Factory()
        val audioSource = ProgressiveMediaSource.Factory(dataSourceFactory)
            .createMediaSource(MediaItem.fromUri(wallpaperPath))
        exoPlayer.setMediaSource(audioSource)
        exoPlayer.prepare(audioSource)
        exoPlayer.seekToDefaultPosition()
        exoPlayer.play()



        exoPlayer.volume=0f

        exoPlayer.addListener(object : Player.Listener {
            override fun onPlayerStateChanged(playWhenReady: Boolean, playbackState: Int) {
                if (playbackState == Player.STATE_ENDED) {
                    Log.d("abcd", "exoplayer: ")
                    // Restart playback from the beginning when the video reaches the end
                    exoPlayer.seekToDefaultPosition()
                    exoPlayer.play()

                } else if (playbackState == Player.STATE_READY) {
                    hideProgressBar()
                }
            }
        })

        btnShowBottomSheet = findViewById(R.id.applyButton)
        btnShowBottomSheet.setOnClickListener {

            val intent = Intent(WallpaperManager.ACTION_CHANGE_LIVE_WALLPAPER)
            intent.putExtra(
                WallpaperManager.EXTRA_LIVE_WALLPAPER_COMPONENT,
                ComponentName(this, GLWallpaperService::class.java)
            )
            startActivity(intent)
          //  intent.putExtra("path",wallpaperPath)

    }

}
    override fun onBackPressed() {

        super.onBackPressed()

    }



    private fun showProgressBar() {
        progressBar.visibility = View.VISIBLE // Show ProgressBar
    }

    private fun hideProgressBar() {
        progressBar.visibility = View.GONE // Hide ProgressBar
    }

}