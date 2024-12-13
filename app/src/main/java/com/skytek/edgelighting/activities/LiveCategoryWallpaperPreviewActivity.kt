package com.skytek.edgelighting.activities

import android.net.Uri
import android.os.Build
import android.os.Bundle
import android.util.Log
import android.view.View
import android.widget.Button
import android.widget.ProgressBar
import android.widget.VideoView
import androidx.annotation.RequiresApi
import androidx.appcompat.app.AppCompatActivity
import androidx.core.content.ContextCompat
import com.google.android.material.bottomsheet.BottomSheetDialog
import com.skytek.edgelighting.R
import com.skytek.edgelighting.databinding.ActivityLiveCategoryWallpaperPreviewBinding

class LiveCategoryWallpaperPreviewActivity : AppCompatActivity() {
    private lateinit var binding: ActivityLiveCategoryWallpaperPreviewBinding
    lateinit var btnShowBottomSheet: Button
    lateinit var dialog: BottomSheetDialog
    private lateinit var progressBar: ProgressBar


    @RequiresApi(Build.VERSION_CODES.N)
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityLiveCategoryWallpaperPreviewBinding.inflate(layoutInflater)
        setContentView(binding.root)

        // Change the status bar color
        window.statusBarColor = ContextCompat.getColor(this, R.color.background)


        progressBar = findViewById(R.id.progressBar) // Initialize progressBar
        showProgressBar()

        val wallpaperPath = intent.getStringExtra("wallpaperPath")

        val fullScreenWallpaperVideoView: VideoView =
            findViewById(R.id.fullScreenWallpaperVideoView)

        // Set the video URI and start playing after preparing
        fullScreenWallpaperVideoView.setVideoURI(Uri.parse(wallpaperPath))
        fullScreenWallpaperVideoView.setOnPreparedListener { mp ->
            // Start playback once prepared
            mp.start()


            progressBar.visibility = View.GONE

        }
        fullScreenWallpaperVideoView.setOnCompletionListener { mp ->
            // Handle completion if needed
        }


        fullScreenWallpaperVideoView.setOnClickListener {
            fullScreenWallpaperVideoView.seekTo(0)
            fullScreenWallpaperVideoView.start()
        }


    }


    private fun showProgressBar() {
        progressBar.visibility = View.VISIBLE // Show ProgressBar
    }

    private fun hideProgressBar() {
        progressBar.visibility = View.GONE // Hide ProgressBar
    }
}