package com.skytek.edgelighting.activities

import android.annotation.SuppressLint
import android.app.WallpaperManager
import android.content.Context
import android.content.Intent
import android.content.res.Configuration
import android.graphics.Bitmap
import android.graphics.BitmapFactory
import android.graphics.drawable.BitmapDrawable
import android.os.Build
import android.os.Bundle
import android.os.Handler
import android.os.Looper
import android.util.Log
import android.view.View
import android.widget.ImageView
import android.widget.LinearLayout
import android.widget.ProgressBar
import android.widget.TextView
import android.widget.Toast
import androidx.annotation.RequiresApi
import androidx.appcompat.app.AppCompatActivity
import androidx.core.content.ContextCompat
import coil.Coil
import coil.load
import coil.request.ImageRequest
import coil.size.Scale
import com.google.android.material.bottomsheet.BottomSheetDialog
import com.mobi.pixels.adInterstitial.AdInterstitialShowListeners
import com.mobi.pixels.adInterstitial.Interstitial
import com.mobi.pixels.firebase.fireEvent
import com.skytek.edgelighting.GenericFunctions.imgBitmap
import com.skytek.edgelighting.R
import com.skytek.edgelighting.ads.IsShowingOpenAd.isinterstitialvisible
import com.skytek.edgelighting.databinding.ActivityStaticCategoryWallpaperPreviewBinding
import com.skytek.edgelighting.utils.AdResources
import com.skytek.edgelighting.utils.AdResources.activitiesAdId
import com.skytek.edgelighting.utils.AdResources.clicks
import com.skytek.edgelighting.utils.AdResources.wholeInterAdShow
import com.skytek.edgelighting.utils.AdResources.wholeScreenAdShow
import com.skytek.edgelighting.utils.adTimeTraker.isIntervalElapsed
import com.skytek.edgelighting.utils.adTimeTraker.updateLastAdShownTime
import com.skytek.edgelighting.utils.checkContext
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.async
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext
import java.io.ByteArrayInputStream
import java.io.ByteArrayOutputStream
import java.io.EOFException
import java.io.FileNotFoundException
import java.io.IOException
import java.net.HttpURLConnection
import java.net.SocketException
import java.net.URL
import java.net.UnknownHostException
import javax.net.ssl.SSLHandshakeException

class StaticCategoryWallpaperPreviewActivity : AppCompatActivity() {
    private lateinit var binding: ActivityStaticCategoryWallpaperPreviewBinding
    lateinit var btnShowBottomSheet: TextView
    lateinit var dialog: BottomSheetDialog
    private lateinit var progressBarBottom: ProgressBar
    private lateinit var layout: LinearLayout
    private lateinit var progressBar: ProgressBar

    @SuppressLint("NewApi")
    override fun onCreate(savedInstanceState: Bundle?) {

        super.onCreate(savedInstanceState)
        binding = ActivityStaticCategoryWallpaperPreviewBinding.inflate(layoutInflater)
        setContentView(binding.root)
        progressBar = binding.progressBar
        // Interstitial AD


// Change the status bar color
        window.statusBarColor = ContextCompat.getColor(this, R.color.background)

        // Initialize the ProgressBar
        showProgressBar()

        val wallpaperPath = intent.getStringExtra("wallpaperPath")


        Log.d("abcd1234", wallpaperPath!!)
        val fullScreenWallpaperImageView: ImageView =
            findViewById(R.id.fullScreenWallpaperImageView)

        fullScreenWallpaperImageView.load(wallpaperPath) {
            scale(Scale.FILL)

            listener(onSuccess = { _, _ ->
                hideProgressBar()
            }, onError = { _, v ->
                hideProgressBar()
                Toast.makeText(
                    this@StaticCategoryWallpaperPreviewActivity,
                    "Failed to load wallpaper",
                    Toast.LENGTH_SHORT
                ).show()
            })
        }


        btnShowBottomSheet = findViewById(R.id.applyButton)
        btnShowBottomSheet.setOnClickListener {

            dialog = BottomSheetDialog(this)
            val view = layoutInflater.inflate(R.layout.bottom_sheet_dialog_option_layout, null)
            progressBarBottom = view.findViewById<ProgressBar>(R.id.progressBarBottom)
            layout = view.findViewById<LinearLayout>(R.id.layout12)
            dialog.setCancelable(true)
            dialog.setContentView(view)
            dialog.show()
            //  hideBottomProgressBar()
            val applyToHomeButton = view.findViewById<View>(R.id.layoutSetHomeScreen)
            val applyToLockButton = view.findViewById<View>(R.id.layoutSetLockScreen)
            val applyToBothButton = view.findViewById<View>(R.id.layoutSetBothScreens)
            val adjustscreen = view.findViewById<View>(R.id.adjustscreen)

            val result = CoroutineScope(Dispatchers.IO).async {
                withContext(Dispatchers.Main) {
                    showBottomProgressBar()
                }
                try {
                    val url = URL(wallpaperPath)

                    withContext(Dispatchers.IO) {
                        val connection = url.openConnection() as HttpURLConnection
                        connection.doInput = true
                        connection.connect()
                        val inputStream = connection.inputStream
                        BitmapFactory.decodeStream(inputStream)
                    }

                } catch (e: UnknownHostException) {
                    handleNetworkError("Unable to load wallpaper due to network issues")
                    null
                } catch (e: FileNotFoundException) {
                    handleNetworkError("Wallpaper not found")
                    null
                } catch (e: SocketException) {
                    handleNetworkError("Connection error. Please try again later.")
                    null
                } catch (e: EOFException) {
                    handleNetworkError("Unexpected error. Please try again later.")
                    null
                } catch (e: SSLHandshakeException) {
                    handleNetworkError("SSL handshake failed. Please check your network connection and try again.")
                    null
                } catch (e: Exception) {
                    handleNetworkError("An unexpected error occurred. Please try again later.")
                    null
                }
            }

            CoroutineScope(Dispatchers.Main).launch {
                imgBitmap = result.await()
                if (imgBitmap != null) {
                    hideBottomProgressBar()
                    Log.d("dlksdlsjdskjd", "onCreate:  2")
                    applyToHomeButton?.setOnClickListener {
clicks++
                        Log.d("abcd6666666", "asdasdasdasd")
                        showToast(getString(R.string.applying_wallpaper))

                        Handler(Looper.getMainLooper()).postDelayed({
                            applyWallpaperToHomeScreen(wallpaperPath, dialog, view)
                            if (wholeInterAdShow && wholeScreenAdShow && (isIntervalElapsed() || AdResources.clicks >= AdResources.ElBtnClickCount)) {
                                if (checkContext(context = this@StaticCategoryWallpaperPreviewActivity)) {
                                    Interstitial.show(this@StaticCategoryWallpaperPreviewActivity,
                                        object : AdInterstitialShowListeners {
                                            override fun onShowed() {
                                                isinterstitialvisible = true
                                                fireEvent("SHOW_EL_static_home_Wall_click")
                                                Interstitial.load(
                                                    this@StaticCategoryWallpaperPreviewActivity,
                                                    activitiesAdId,
                                                )
                                            }

                                            override fun onError(error: String) {
                                                Interstitial.load(
                                                    this@StaticCategoryWallpaperPreviewActivity,
                                                    activitiesAdId,
                                                )
                                                showToast(getString(R.string.wallpaper_applied))
                                            }

                                            override fun onDismissed() {
                                                isinterstitialvisible = false
                                                updateLastAdShownTime()
                                                clicks=0
                                                showToast(getString(R.string.wallpaper_applied))
                                            }

                                        })
                                }
                            } else {
                                showToast(getString(R.string.wallpaper_applied))
                            }

                        }, 600)

                    }
                    applyToLockButton?.setOnClickListener {
                        Log.d("abcd", "asda23423sdasdasd")
clicks++
                        showToast(getString(R.string.applying_wallpaper))
                        Handler(Looper.getMainLooper()).postDelayed({
                            applyWallpaperToLockScreen(wallpaperPath, dialog, view)

                            if (wholeInterAdShow && wholeScreenAdShow && (isIntervalElapsed() || clicks>= AdResources.ElBtnClickCount)) {
                                if (checkContext(context = this@StaticCategoryWallpaperPreviewActivity)) {
                                    Interstitial.show(this@StaticCategoryWallpaperPreviewActivity,
                                        object : AdInterstitialShowListeners {
                                            override fun onShowed() {
                                                isinterstitialvisible = true
                                                fireEvent("SHOW_EL_static_Lock_Wall_click")
                                                Interstitial.load(
                                                    this@StaticCategoryWallpaperPreviewActivity,
                                                    activitiesAdId
                                                )
                                            }

                                            override fun onError(error: String) {
                                                showToast(getString(R.string.wallpaper_applied))
                                                Interstitial.load(
                                                    this@StaticCategoryWallpaperPreviewActivity,
                                                    activitiesAdId
                                                )
                                            }

                                            override fun onDismissed() {
                                                isinterstitialvisible = false
                                                updateLastAdShownTime()
                                                clicks=0
                                                showToast(getString(R.string.wallpaper_applied))
                                            }

                                        })
                                }
                            } else {
                                showToast(getString(R.string.wallpaper_applied))
                            }

                        }, 500)
                    }
                    applyToBothButton?.setOnClickListener {
clicks++
                        Log.d("abcd", "asdas345345dasdasd")

                        showToast(getString(R.string.applying_wallpaper))
                        Handler(Looper.getMainLooper()).postDelayed({
                            applyWallpaperToHomeAndLockScreen(wallpaperPath, dialog, view)
                            if (wholeInterAdShow && wholeScreenAdShow && (isIntervalElapsed() || clicks>= AdResources.ElBtnClickCount)) {
                                if (checkContext(context = this@StaticCategoryWallpaperPreviewActivity)) {
                                    Interstitial.show(this@StaticCategoryWallpaperPreviewActivity,
                                        object : AdInterstitialShowListeners {
                                            override fun onShowed() {
                                                isinterstitialvisible = true
                                                fireEvent("SHOW_EL_static_both_Wall_click")
                                                Interstitial.load(

                                                    this@StaticCategoryWallpaperPreviewActivity,
                                                    activitiesAdId
                                                )
                                            }

                                            override fun onError(error: String) {
                                                showToast(getString(R.string.wallpaper_applied))
                                                Interstitial.load(
                                                    this@StaticCategoryWallpaperPreviewActivity,
                                                    activitiesAdId
                                                )
                                            }

                                            override fun onDismissed() {
                                                isinterstitialvisible = false
                                                updateLastAdShownTime()
                                                clicks=0
                                                Log.d("abcd", "onDismissed")
                                                showToast(getString(R.string.wallpaper_applied))
                                            }

                                        })
                                }
                            } else {
                                Log.d("abcd", "else")
                                showToast(getString(R.string.wallpaper_applied))
                            }

                        }, 700)
                    }
                    adjustscreen?.setOnClickListener {
                        clicks++
                        Log.d("abcd", "onCreate:  adjustscreen")
                        adjustscreen(wallpaperPath, dialog, view)
                    }
                }


            }

        }


    }

    override fun onBackPressed() {
        Log.d(
            "fishuiofhiwegficvue",
            "onBackPressed:${intent.getBooleanExtra("onBoardingScreen", false)} "
        )
        if (intent.getBooleanExtra("onBoardingScreen", false)) {
            startActivity(Intent(this, StaticWallpaperActivity::class.java))
            finish()

        } else {
            super.onBackPressed()
        }
    }

    private suspend fun handleNetworkError(message: String) {
        withContext(Dispatchers.Main) {
            hideBottomProgressBar()
            dialog.dismiss()
            Toast.makeText(
                this@StaticCategoryWallpaperPreviewActivity, message, Toast.LENGTH_SHORT
            ).show()
        }
    }

    private fun showToast(message: String) {
        Toast.makeText(this@StaticCategoryWallpaperPreviewActivity, message, Toast.LENGTH_SHORT)
            .show()
    }

    private suspend fun delay(milliseconds: Long) {
        delay(milliseconds)
    }

    private fun setBitmapAsWallpaper(context: Context, bitmap: Bitmap) {
        val wallpaperManager = WallpaperManager.getInstance(context)

        try {
            val stream = ByteArrayOutputStream()
            bitmap.compress(Bitmap.CompressFormat.JPEG, 100, stream)
            val inputStream = ByteArrayInputStream(stream.toByteArray())

            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.N) {
                wallpaperManager.setStream(inputStream, null, true, WallpaperManager.FLAG_SYSTEM)
            } else {
                wallpaperManager.setStream(inputStream)
            }

            stream.close()
            inputStream.close()
        } catch (e: Exception) {
            e.printStackTrace()
            // Handle exceptions (e.g., SecurityException)
        }
    }


    private fun applyWallpaperToHomeScreen(
        wallpaperPath: String?, dialog: BottomSheetDialog, bottomSheetView: View
    ) {
        val wallpaperManager = WallpaperManager.getInstance(this)
        val request = ImageRequest.Builder(this)
            .data(wallpaperPath)
            .target { drawable ->
                val bitmap = (drawable as? BitmapDrawable)?.bitmap
                bitmap?.let {
                    try {
                        // Apply wallpaper to home screen only
                        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.N) {
                            wallpaperManager.setBitmap(
                                bitmap,
                                null,
                                true,
                                WallpaperManager.FLAG_SYSTEM
                            )
                        } else {
                            // Fallback for older versions (applies to both screens)
                            wallpaperManager.setBitmap(bitmap)
                        }

                        runOnUiThread {
                            dialog.dismiss()
                        }
                    } catch (e: IOException) {
                        e.printStackTrace()
                    }
                }
            }.build()

        Coil.imageLoader(this).enqueue(request)
    }


    private fun adjustscreen(
        wallpaperPath: String?, dialog: BottomSheetDialog, bottomSheetView: View
    ) {


        // val wallpaperManager = WallpaperManager.getInstance(this)
        val request = ImageRequest.Builder(this).data(wallpaperPath).target { drawable ->
            val bitmap = (drawable as? BitmapDrawable)?.bitmap
            // imgBitmap = bitmap
            bitmap?.let {
                try {

                    //   wallpaperManager.setBitmap(bitmap)
                    runOnUiThread {

                        dialog.dismiss() // Dismiss the dialog after setting wallpaper


                        val intentnext = Intent(
                            this@StaticCategoryWallpaperPreviewActivity,
                            AdjustImageActivity::class.java
                        )
                        intentnext.putExtra("adjustpath", wallpaperPath)
                        startActivity(intentnext)
                    }
                } catch (e: IOException) {
                    e.printStackTrace()

                }
            }
        }.build()

        Coil.imageLoader(this).enqueue(request)
    }

    override fun onConfigurationChanged(newConfig: Configuration) {
        super.onConfigurationChanged(newConfig)
        Log.d("sdfaasdf", "onConfigurationChanged: ")
    }

    @RequiresApi(Build.VERSION_CODES.N)
    private fun applyWallpaperToLockScreen(
        wallpaperPath: String?, dialog: BottomSheetDialog, bottomSheetView: View
    ) {


        val wallpaperManager = WallpaperManager.getInstance(this)
        val request = ImageRequest.Builder(this).data(wallpaperPath).target { drawable ->
            val bitmap = (drawable as? BitmapDrawable)?.bitmap
            //imgBitmap = bitmap
            bitmap?.let {
                try {
                    // Apply wallpaper to home screen only
                    if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.N) {
                        wallpaperManager.setBitmap(
                            bitmap,
                            null,
                            true,
                            WallpaperManager.FLAG_LOCK
                        )
                    } else {
                        // Fallback for older versions (applies to both screens)
                        wallpaperManager.setBitmap(bitmap)
                    }

                    runOnUiThread {

                        dialog.dismiss() // Dismiss the BottomSheetDialog after setting wallpaper

                    }
                } catch (e: IOException) {
                    e.printStackTrace()

                }
            }
        }.build()

        Coil.imageLoader(this).enqueue(request)
    }

    private fun applyWallpaperToHomeAndLockScreen(
        wallpaperPath: String?, dialog: BottomSheetDialog, bottomSheetView: View
    ) {
        val wallpaperManager = WallpaperManager.getInstance(this)
        val request = ImageRequest.Builder(this)
            .data(wallpaperPath)
            .target { drawable ->
                val bitmap = (drawable as? BitmapDrawable)?.bitmap
                bitmap?.let {
                    try {
                        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.N) {
                            // Set wallpaper for both home and lock screens
                            wallpaperManager.setBitmap(
                                bitmap,
                                null,
                                true,
                                WallpaperManager.FLAG_SYSTEM
                            )
                            wallpaperManager.setBitmap(
                                bitmap,
                                null,
                                true,
                                WallpaperManager.FLAG_LOCK
                            )
                        } else {
                            // Fallback for older versions, applies to both screens by default
                            wallpaperManager.setBitmap(bitmap)
                        }

                        runOnUiThread {
                            dialog.dismiss()
                        }
                    } catch (e: IOException) {
                        e.printStackTrace()
                    }
                }
            }.build()

        Coil.imageLoader(this).enqueue(request)
    }


    private fun showProgressBar() {
        binding.progressBar.visibility = View.VISIBLE // Show ProgressBar
    }

    private fun hideProgressBar() {
        binding.progressBar.visibility = View.GONE // Hide ProgressBar
    }

    private fun showBottomProgressBar() {
        // val progressBarBottom = bottomSheetView.findViewById<ProgressBar>(R.id.progressBarBottom)
        layout.visibility = View.INVISIBLE
        progressBarBottom.visibility = View.VISIBLE
    }

    private fun hideBottomProgressBar() {

        progressBarBottom.visibility = View.GONE
        layout.visibility = View.VISIBLE
    }


}



