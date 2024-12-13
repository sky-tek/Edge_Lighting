package com.skytek.edgelighting.activities

import android.app.WallpaperManager
import android.content.Intent
import android.graphics.drawable.BitmapDrawable
import android.os.Build
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.util.Log
import android.view.View
import android.widget.Button
import android.widget.ImageView
import android.widget.Toast
import androidx.activity.OnBackPressedCallback
import androidx.annotation.RequiresApi
import androidx.core.content.ContentProviderCompat.requireContext
import androidx.core.content.ContextCompat
import androidx.fragment.app.commit
import coil.Coil
import coil.load
import coil.request.ImageRequest
import com.google.android.material.bottomsheet.BottomSheetDialog
import com.skytek.edgelighting.R

import java.io.IOException

class WallpaperPreview : AppCompatActivity() {

    lateinit var btnShowBottomSheet: Button
    lateinit var dialog : BottomSheetDialog

    @RequiresApi(Build.VERSION_CODES.N)
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_wallpaper_preview_new)
        // Change the status bar color
        window.statusBarColor = ContextCompat.getColor(this, R.color.background)

        val wallpaperPath = intent.getStringExtra("wallpaperPath")
        Log.d("abcd",wallpaperPath!!)
        val fullScreenWallpaperImageView: ImageView = findViewById(R.id.fullScreenWallpaperImageView)
        fullScreenWallpaperImageView.load(wallpaperPath) {
            // handle loading or error states if needed
        }



        btnShowBottomSheet = findViewById(R.id.applyButton)
        btnShowBottomSheet.setOnClickListener {

            dialog = BottomSheetDialog(this)
            val view = layoutInflater.inflate(R.layout.bottom_sheet_dialog_option_layout, null)
            dialog.setCancelable(true)
            dialog.setContentView(view)
            dialog.show()

            val applyToHomeButton = view.findViewById<View>(R.id.layoutSetHomeScreen)
            val applyToLockButton = view.findViewById<View>(R.id.layoutSetLockScreen)
            val applyToBothButton = view.findViewById<View>(R.id.layoutSetBothScreens)

            applyToHomeButton.setOnClickListener {
                // Apply wallpaper to home screen logic here and pass the dialog
                applyWallpaperToHomeScreen(wallpaperPath, dialog)
            }


            applyToLockButton.setOnClickListener {
                // Apply wallpaper to lock screen logic here
                applyWallpaperToLockScreen(wallpaperPath, dialog)
            }


            applyToBothButton.setOnClickListener {
                // Apply wallpaper to both home and lock screens
                applyWallpaperToHomeAndLockScreen(wallpaperPath, dialog)
                Toast.makeText(this, getString(R.string.set_both_screens), Toast.LENGTH_SHORT).show() // Show toast
            }



        }








    }

    override fun onBackPressed() {

        super.onBackPressed()
    }

    private fun applyWallpaperToHomeScreen(wallpaperPath: String?, dialog: BottomSheetDialog) {
        Log.d("home", "applyWallpaperToHomeScreen: ")
        val wallpaperManager = WallpaperManager.getInstance(this)
        val request = ImageRequest.Builder(this)
            .data(wallpaperPath)
            .target { drawable ->
                val bitmap = (drawable as? BitmapDrawable)?.bitmap
                bitmap?.let {
                    try {
                        wallpaperManager.setBitmap(bitmap)

                        runOnUiThread {
                            Toast.makeText(this,  getString(R.string.applied), Toast.LENGTH_SHORT).show()
                            dialog.dismiss() // Dismiss the dialog after setting wallpaper
                        }
                    } catch (e: IOException) {
                        e.printStackTrace()
                    }
                }
            }
            .build()

        Coil.imageLoader(this).enqueue(request)
    }



    @RequiresApi(Build.VERSION_CODES.N)
    private fun applyWallpaperToLockScreen(wallpaperPath: String?, dialog: BottomSheetDialog) {
        Log.d("lock", "applyWallpaperToLockScreen: ")
        val wallpaperManager = WallpaperManager.getInstance(this)
        val request = ImageRequest.Builder(this)
            .data(wallpaperPath)
            .target { drawable ->
                val bitmap = (drawable as? BitmapDrawable)?.bitmap
                bitmap?.let {
                    try {
                        wallpaperManager.setBitmap(bitmap, null, true, WallpaperManager.FLAG_LOCK)
                        runOnUiThread {
                            Toast.makeText(this,  getString(R.string.applied), Toast.LENGTH_SHORT).show()
                            dialog.dismiss() // Dismiss the BottomSheetDialog after setting wallpaper
                        }
                    } catch (e: IOException) {
                        e.printStackTrace()
                    }
                }
            }
            .build()

        Coil.imageLoader(this).enqueue(request)
    }

    @RequiresApi(Build.VERSION_CODES.N)
    private fun applyWallpaperToHomeAndLockScreen(wallpaperPath: String?, dialog: BottomSheetDialog) {
        val wallpaperManager = WallpaperManager.getInstance(this)
        val request = ImageRequest.Builder(this)
            .data(wallpaperPath)
            .target { drawable ->
                val bitmap = (drawable as? BitmapDrawable)?.bitmap
                bitmap?.let {
                    try {
                        wallpaperManager.setBitmap(bitmap)
                        runOnUiThread {
                            dialog.dismiss() // Dismiss the dialog after setting wallpaper
                        }
                    } catch (e: IOException) {
                        e.printStackTrace()
                    }
                }
            }
            .build()

        Coil.imageLoader(this).enqueue(request)
    }

    }
