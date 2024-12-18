package com.skytek.edgelighting.activities

import android.app.WallpaperManager
import android.content.Context
import android.graphics.Bitmap
import android.graphics.Color
import android.graphics.drawable.BitmapDrawable
import android.os.Build
import android.os.Bundle
import android.renderscript.Allocation
import android.renderscript.Element
import android.renderscript.RenderScript
import android.renderscript.ScriptIntrinsicBlur
import android.util.Log
import android.view.View
import android.widget.LinearLayout
import android.widget.ProgressBar
import android.widget.RelativeLayout
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import androidx.core.content.ContextCompat
import com.bumptech.glide.Glide
import com.bumptech.glide.request.target.SimpleTarget
import com.bumptech.glide.request.transition.Transition
import com.davemorrissey.labs.subscaleview.ImageSource
import com.davemorrissey.labs.subscaleview.SubsamplingScaleImageView
import com.google.android.material.bottomsheet.BottomSheetDialog
import com.mobi.pixels.adInterstitial.AdInterstitialShowListeners
import com.mobi.pixels.adInterstitial.Interstitial
import com.mobi.pixels.firebase.fireEvent
import com.skytek.edgelighting.GenericFunctions.imgBitmap
import com.skytek.edgelighting.R
import com.skytek.edgelighting.ads.IsShowingOpenAd.isinterstitialvisible
import com.skytek.edgelighting.databinding.ActivityAdjustImageBinding
import com.skytek.edgelighting.utils.AdResources
import com.skytek.edgelighting.utils.AdResources.activitiesAdId
import com.skytek.edgelighting.utils.AdResources.clicks
import com.skytek.edgelighting.utils.AdResources.wholeInterAdShow
import com.skytek.edgelighting.utils.AdResources.wholeScreenAdShow
import com.skytek.edgelighting.utils.adTimeTraker.isIntervalElapsed
import com.skytek.edgelighting.utils.adTimeTraker.updateLastAdShownTime
import com.skytek.edgelighting.utils.checkContext
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.GlobalScope
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext

class AdjustImageActivity : AppCompatActivity() {

    private lateinit var binding: ActivityAdjustImageBinding
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityAdjustImageBinding.inflate(layoutInflater)
        setContentView(binding.root)
        window?.decorView?.systemUiVisibility =
            (View.SYSTEM_UI_FLAG_LAYOUT_STABLE or View.SYSTEM_UI_FLAG_LAYOUT_FULLSCREEN)
        window.statusBarColor = Color.TRANSPARENT

        // Check if imgBitmap is null before accessing it
        val wallpaperPath = intent.getStringExtra("adjustpath")
        Log.d("dhsajdhsjds", "onCreate: $wallpaperPath")
        imgBitmap?.let { bitmap ->
            binding.resize.setImage(ImageSource.bitmap(bitmap))
            adjustImageScale()
            blurEffectOnBackground()
        } ?: run {
            // Handle the case where imgBitmap is null
            Log.e("dhsajdhsjds", "imgBitmap is null")

        }

        binding.apply.setOnClickListener {
            showBottomSheetDialog()
        }
    }


    //


    //blur effect on selected image background
    private fun blurEffectOnBackground() {
        Glide.with(this).asBitmap().load(imgBitmap).into(object : SimpleTarget<Bitmap>() {
            override fun onResourceReady(resource: Bitmap, transition: Transition<in Bitmap>?) {
                GlobalScope.launch(Dispatchers.Main) {
                    val blurredBitmap = resource.applyBlur(this@AdjustImageActivity, 25f)
                    withContext(Dispatchers.Main) {
                        binding.background.background = BitmapDrawable(resources, blurredBitmap)
                    }
                }
            }
        })
    }

    fun Bitmap.applyBlur(context: Context, radius: Float): Bitmap {
        val inputBitmap = this.copy(Bitmap.Config.ARGB_8888, true)
//        val inputBitmap = this.copy(this.config, true)

        val renderScript = RenderScript.create(context)

        val input = Allocation.createFromBitmap(renderScript, inputBitmap)

        val output = Allocation.createTyped(renderScript, input.type)

        val script = ScriptIntrinsicBlur.create(renderScript, Element.U8_4(renderScript))

        script.setRadius(radius)

        script.setInput(input)

        script.forEach(output)

        output.copyTo(inputBitmap)

        renderScript.destroy()

        return inputBitmap
    }

    private fun adjustImageScale() {
        val imageWidth = binding.resize.sWidth.toFloat()
        val imageHeight = binding.resize.sHeight.toFloat()
        binding.resize.post {
            try {
                val viewWidth = binding.resize.width.toFloat()
                val viewHeight = binding.resize.height.toFloat()
                // Calculate the required scale factors
                val scaleWidth = viewWidth / imageWidth
                val scaleHeight = viewHeight / imageHeight
                val scaleFactor = Math.max(scaleWidth, scaleHeight)
                // Enable zoom gestures
                binding.resize.setDoubleTapZoomStyle(SubsamplingScaleImageView.ZOOM_FOCUS_CENTER)
                binding.resize.isZoomEnabled = true
                binding.resize.setMinimumScaleType(SubsamplingScaleImageView.SCALE_TYPE_CUSTOM)
                binding.resize.minScale = scaleFactor
                binding.resize.maxScale = 20f
                // Set the minimum scale
                // Set the scale and center of the image view
                binding.resize.setScaleAndCenter(scaleFactor, binding.resize.center)
            } catch (e: Exception) {
                e.printStackTrace()
            }

        }
    }

    private fun showBottomSheetDialog() {
        // Assuming you have a data structure named 'wallpaperData' with the provided JSON data

        fireEvent("static_adjust_screen_apply_clicked")
        val bottomSheetDialog = BottomSheetDialog(this)
        bottomSheetDialog.setContentView(R.layout.download_bottom_sheet_dialog)
        val bottomSheetDialogColor =
            bottomSheetDialog.findViewById<RelativeLayout>(R.id.bottomsheetdialogbgcolor)
        val progressBar = bottomSheetDialog.findViewById<ProgressBar>(R.id.progress)
        val content = bottomSheetDialog.findViewById<LinearLayout>(R.id.content)
        val home = bottomSheetDialog.findViewById<LinearLayout>(R.id.home)
        val lock = bottomSheetDialog.findViewById<LinearLayout>(R.id.lock)
        val both = bottomSheetDialog.findViewById<LinearLayout>(R.id.both)
        val adjust = bottomSheetDialog.findViewById<LinearLayout>(R.id.adjust)
        bottomSheetDialogColor?.setBackgroundColor(
            ContextCompat.getColor(
                this, R.color.background_app
            )
        )
        adjust?.visibility = View.GONE
        progressBar?.visibility = View.GONE
        content?.visibility = View.VISIBLE

        if (Build.VERSION.SDK_INT < Build.VERSION_CODES.N) {
            lock?.visibility = View.GONE
            both?.visibility = View.GONE
        }
        home?.setOnClickListener {
            setHomeScreen()
            bottomSheetDialog.dismiss()
        }
        lock?.setOnClickListener {
            setLockScreen()
            bottomSheetDialog.dismiss()
        }
        both?.setOnClickListener {
            setBothScreen()
            bottomSheetDialog.dismiss()
        }
        bottomSheetDialog.show()
    }

    private fun setBothScreen() {
        clicks++
        fireEvent("static_adjust_screen_apply_set_both_clicked")
        val bottomSheetDialogWatcher = BottomSheetDialog(this)
        bottomSheetDialogWatcher.setContentView(R.layout.download_bottom_sheet_dialog)
        val bgColor =
            bottomSheetDialogWatcher.findViewById<RelativeLayout>(R.id.bottomsheetdialogbgcolor)
        val progressBar = bottomSheetDialogWatcher.findViewById<ProgressBar>(R.id.progress)
        progressBar?.visibility = View.VISIBLE
        bgColor?.setBackgroundColor(
            ContextCompat.getColor(
                this, R.color.background_app
            )
        )
        bottomSheetDialogWatcher.show()
        if (wholeInterAdShow && wholeScreenAdShow && (isIntervalElapsed() || AdResources.clicks <= AdResources.ElBtnClickCount)) {

            if (checkContext(context = this)) {
                Interstitial.show(this@AdjustImageActivity, object : AdInterstitialShowListeners {
                    override fun onShowed() {
                        //showAdForBackPressed = 0
                        isinterstitialvisible = true
                        Interstitial.load(this@AdjustImageActivity, activitiesAdId)
                        fireEvent("SHOW_EL_static_AD_both_Wall_click")
                        Log.d("adadsadqw", "ad is showed ")
                        binding.resize.isDrawingCacheEnabled = true
                        val wallpaperManager =
                            WallpaperManager.getInstance(this@AdjustImageActivity)
                        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.N) {
                            wallpaperManager.setBitmap(
                                binding.resize.getDrawingCache(true),
                                null,
                                true,
                                WallpaperManager.FLAG_SYSTEM
                            )
                        } else {
                            wallpaperManager.setBitmap(binding.resize.getDrawingCache(true))
                        }
                        //set for lock screen


                        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.N) {
                            wallpaperManager.setBitmap(
                                binding.resize.getDrawingCache(true),
                                null,
                                true,
                                WallpaperManager.FLAG_LOCK
                            )
                        }
                        bottomSheetDialogWatcher.dismiss()
                    }

                    override fun onError(error: String) {
                        Log.d("sdfjkhdsf", "mainonFailedToLoad")
                        binding.resize.isDrawingCacheEnabled = true
                        val wallpaperManager =
                            WallpaperManager.getInstance(this@AdjustImageActivity)
                        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.N) {
                            wallpaperManager.setBitmap(
                                binding.resize.getDrawingCache(true),
                                null,
                                true,
                                WallpaperManager.FLAG_SYSTEM
                            )
                        } else {
                            wallpaperManager.setBitmap(binding.resize.getDrawingCache(true))
                        }
                        //set for lock screen


                        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.N) {
                            wallpaperManager.setBitmap(
                                binding.resize.getDrawingCache(true),
                                null,
                                true,
                                WallpaperManager.FLAG_LOCK
                            )
                        }
                        Toast.makeText(
                            this@AdjustImageActivity,
                            getString(R.string.applied),
                            Toast.LENGTH_SHORT
                        ).show()

                        bottomSheetDialogWatcher.dismiss()
                        Interstitial.load(this@AdjustImageActivity, activitiesAdId)
                    }

                    override fun onDismissed() {
                        isinterstitialvisible = false
                        updateLastAdShownTime()
                        clicks=0
                        Toast.makeText(
                            this@AdjustImageActivity,
                            getString(R.string.applied),
                            Toast.LENGTH_SHORT
                        ).show()
                    }

                })
            }
            Log.d("sdfjkhdsf", "mainonLoaded")
        } else {
            Log.d("sdfjkhdsf", "mainonFailedToLoad")
            binding.resize.isDrawingCacheEnabled = true
            val wallpaperManager = WallpaperManager.getInstance(this@AdjustImageActivity)
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.N) {
                wallpaperManager.setBitmap(
                    binding.resize.getDrawingCache(true), null, true, WallpaperManager.FLAG_SYSTEM
                )
            } else {
                wallpaperManager.setBitmap(binding.resize.getDrawingCache(true))
            }
            //set for lock screen


            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.N) {
                wallpaperManager.setBitmap(
                    binding.resize.getDrawingCache(true), null, true, WallpaperManager.FLAG_LOCK
                )
            }
            Toast.makeText(
                this@AdjustImageActivity, getString(R.string.applied), Toast.LENGTH_SHORT
            ).show()

            bottomSheetDialogWatcher.dismiss()
        }


    }

    private fun setHomeScreen() {
        clicks++
        fireEvent("static_adjust_screen_apply_set_home_clicked")
        val bottomSheetDialogWatcher = BottomSheetDialog(this)
        bottomSheetDialogWatcher.setContentView(R.layout.download_bottom_sheet_dialog)
        val bgColor =
            bottomSheetDialogWatcher.findViewById<RelativeLayout>(R.id.bottomsheetdialogbgcolor)

        val progressBar = bottomSheetDialogWatcher.findViewById<ProgressBar>(R.id.progress)
        progressBar?.visibility = View.VISIBLE
        bgColor?.setBackgroundColor(
            ContextCompat.getColor(
                this, R.color.background_app
            )
        )
        bottomSheetDialogWatcher.show()
        if (wholeInterAdShow && wholeScreenAdShow && (isIntervalElapsed() || clicks<= AdResources.ElBtnClickCount)) {

            if (checkContext(context = this)) {
                Interstitial.show(this@AdjustImageActivity, object : AdInterstitialShowListeners {
                    override fun onShowed() {
                        isinterstitialvisible = true
                        fireEvent("SHOW_EL_static_AD_home_Wall_click")
                        Interstitial.load(this@AdjustImageActivity, activitiesAdId)
                    }

                    override fun onError(error: String) {
                        Log.d("sdfjkhdsf", "mainonFailedToLoad")
                        binding.resize.isDrawingCacheEnabled = true
                        val wallpaperManager =
                            WallpaperManager.getInstance(this@AdjustImageActivity)
                        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.N) {
                            wallpaperManager.setBitmap(
                                binding.resize.getDrawingCache(true),
                                null,
                                true,
                                WallpaperManager.FLAG_SYSTEM
                            )
                        } else {
                            wallpaperManager.setBitmap(binding.resize.getDrawingCache(true))
                        }

                        binding.resize.isDrawingCacheEnabled = false

                        Toast.makeText(
                            this@AdjustImageActivity,
                            getString(R.string.applied),
                            Toast.LENGTH_SHORT
                        ).show()

                        bottomSheetDialogWatcher.dismiss()
                        Interstitial.load(this@AdjustImageActivity, activitiesAdId)
                    }

                    override fun onDismissed() {
                        isinterstitialvisible = false
                        updateLastAdShownTime()
                        clicks=0
                        binding.resize.isDrawingCacheEnabled = true
                        val wallpaperManager =
                            WallpaperManager.getInstance(this@AdjustImageActivity)
                        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.N) {
                            Log.d(
                                "dfgfdgd",
                                "setHomeScreen:  if " + binding.resize.getDrawingCache(true)
                            )
                            wallpaperManager.setBitmap(
                                binding.resize.getDrawingCache(true),
                                null,
                                true,
                                WallpaperManager.FLAG_SYSTEM
                            )
                        } else {
                            Log.d("dfgfdgd", "setHomeScreen: else " + wallpaperManager)
                            wallpaperManager.setBitmap(binding.resize.getDrawingCache(true))
                        }
                        binding.resize.isDrawingCacheEnabled = false
                        Toast.makeText(
                            this@AdjustImageActivity,
                            getString(R.string.applied),
                            Toast.LENGTH_SHORT
                        ).show()

                        bottomSheetDialogWatcher.dismiss()
                    }

                })
            }


        } else {
            Log.d("sdfjkhdsf", "mainonFailedToLoad")
            binding.resize.isDrawingCacheEnabled = true
            val wallpaperManager = WallpaperManager.getInstance(this@AdjustImageActivity)
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.N) {
                wallpaperManager.setBitmap(
                    binding.resize.getDrawingCache(true), null, true, WallpaperManager.FLAG_SYSTEM
                )
            } else {
                wallpaperManager.setBitmap(binding.resize.getDrawingCache(true))
            }

            binding.resize.isDrawingCacheEnabled = false

            Toast.makeText(
                this@AdjustImageActivity, getString(R.string.applied), Toast.LENGTH_SHORT
            ).show()

            bottomSheetDialogWatcher.dismiss()
        }


    }

    private fun setLockScreen() {
        clicks++
        fireEvent("static_adjust_screen_apply_set_lock_clicked")
        val bottomSheetDialogWatcher = BottomSheetDialog(this)
        bottomSheetDialogWatcher.setContentView(R.layout.download_bottom_sheet_dialog)
        val bgColor =
            bottomSheetDialogWatcher.findViewById<RelativeLayout>(R.id.bottomsheetdialogbgcolor)

        val progressBar = bottomSheetDialogWatcher.findViewById<ProgressBar>(R.id.progress)
        progressBar?.visibility = View.VISIBLE
        bgColor?.setBackgroundColor(
            ContextCompat.getColor(
                this, R.color.background_app
            )
        )
        bottomSheetDialogWatcher.show()
        if (wholeInterAdShow && wholeScreenAdShow && (isIntervalElapsed() || clicks<= AdResources.ElBtnClickCount)) {

            if (checkContext(context = this)) {
                Interstitial.show(this@AdjustImageActivity, object : AdInterstitialShowListeners {
                    override fun onShowed() {
                        isinterstitialvisible = true
                        // showAdForBackPressed = 0
                        fireEvent("SHOW_EL_static_AD_lock_Wall_click")
                        Interstitial.load(this@AdjustImageActivity, activitiesAdId)
                        updateLastAdShownTime()
                        Log.d("adadsadqw", "ad is showed ")
                        binding.resize.isDrawingCacheEnabled = true
                        val wallpaperManager =
                            WallpaperManager.getInstance(this@AdjustImageActivity)
                        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.N) {
                            wallpaperManager.setBitmap(
                                binding.resize.getDrawingCache(true),
                                null,
                                true,
                                WallpaperManager.FLAG_LOCK
                            )
                        }

                        binding.resize.isDrawingCacheEnabled = false
                        bottomSheetDialogWatcher.dismiss()
                    }

                    override fun onError(error: String) {
                        binding.resize.isDrawingCacheEnabled = true
                        val wallpaperManager =
                            WallpaperManager.getInstance(this@AdjustImageActivity)
                        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.N) {
                            wallpaperManager.setBitmap(
                                binding.resize.getDrawingCache(true),
                                null,
                                true,
                                WallpaperManager.FLAG_LOCK
                            )
                        }

                        binding.resize.isDrawingCacheEnabled = false
                        Toast.makeText(
                            this@AdjustImageActivity,
                            getString(R.string.applied),
                            Toast.LENGTH_SHORT
                        ).show()

                        bottomSheetDialogWatcher.dismiss()
                        Log.d("sdfjkhdsf", "mainonFailedToLoad")
                        Interstitial.load(this@AdjustImageActivity, activitiesAdId)
                    }

                    override fun onDismissed() {
                        isinterstitialvisible = false
                        updateLastAdShownTime()
                        clicks=0
                        Toast.makeText(
                            this@AdjustImageActivity,
                            getString(R.string.applied),
                            Toast.LENGTH_SHORT
                        ).show()
                    }

                })
            }
        } else {
            binding.resize.isDrawingCacheEnabled = true
            val wallpaperManager = WallpaperManager.getInstance(this@AdjustImageActivity)
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.N) {
                wallpaperManager.setBitmap(
                    binding.resize.getDrawingCache(true), null, true, WallpaperManager.FLAG_LOCK
                )
            }

            binding.resize.isDrawingCacheEnabled = false
            Toast.makeText(
                this@AdjustImageActivity, getString(R.string.applied), Toast.LENGTH_SHORT
            ).show()

            bottomSheetDialogWatcher.dismiss()
            Log.d("sdfjkhdsf", "mainonFailedToLoad")
        }


    }


}