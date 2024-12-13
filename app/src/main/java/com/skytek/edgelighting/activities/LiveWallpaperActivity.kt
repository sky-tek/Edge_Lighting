package com.skytek.edgelighting.activities

import android.annotation.SuppressLint
import android.app.Dialog
import android.app.WallpaperManager
import android.content.Context
import android.content.SharedPreferences
import android.graphics.Color
import android.graphics.drawable.ColorDrawable
import android.graphics.drawable.InsetDrawable
import android.os.Build
import android.os.Bundle
import android.os.Handler
import android.os.Looper
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.Window
import android.view.animation.AccelerateInterpolator
import android.widget.Toast
import androidx.activity.enableEdgeToEdge
import androidx.annotation.RequiresApi
import androidx.appcompat.app.AppCompatActivity
import androidx.constraintlayout.widget.ConstraintSet
import androidx.preference.PreferenceManager
import com.google.android.gms.ads.AdView

import com.mobi.pixels.adBannerOnDemand.loadOnDemandBannerAd
import com.mobi.pixels.enums.BannerAdType
import com.mobi.pixels.isOnline

import com.skytek.edgelighting.App.Companion.openAd
import com.skytek.edgelighting.R
import com.skytek.edgelighting.adapter.LiveWallpaperPagerAdapter
import com.skytek.edgelighting.databinding.ActivityLiveWallpaperBinding
import com.skytek.edgelighting.utils.AdResources.bannerAdId


class LiveWallpaperActivity : AppCompatActivity() {
    private lateinit var binding: ActivityLiveWallpaperBinding

    lateinit var adView: AdView
    var i = 0

    lateinit var sharedPref: SharedPreferences
    lateinit var editor: SharedPreferences.Editor
    override fun onCreate(savedInstanceState: Bundle?) {
        Log.d("whyNorbfhgbf", "onCreate: ")
        super.onCreate(savedInstanceState)
enableEdgeToEdge()
        binding = ActivityLiveWallpaperBinding.inflate(layoutInflater)
        setContentView(binding.root)

        sharedPref = PreferenceManager.getDefaultSharedPreferences(this@LiveWallpaperActivity)
        editor = sharedPref.edit()
//        loadInterstitialAd(activitiesAdId)
//        if (liveWallpaperScreenAdShow) {
//
//        }

        binding.wallpaperEnabledSwitch.setOnClickListener(object : View.OnClickListener {

            @SuppressLint("CommitPrefEdits")
            override fun onClick(v: View?) {
                Log.d("checkclicklistener", "asdsadasdsadas else")
                i = 1
                editor.putBoolean("check_wallpaper_set", false)
                editor.apply()
                Handler(Looper.getMainLooper()).postDelayed(Runnable {
                    WallpaperManager.getInstance(this@LiveWallpaperActivity).clear()
                    Toast.makeText(
                        this@LiveWallpaperActivity, "Wallpaper Removed", Toast.LENGTH_SHORT
                    ).show()
                }, 500)
                binding.tabLayout.elevation = 4f
                binding.parentLayoutofDisableWallpaper.elevation = 1f
                binding.parentLayoutofDisableWallpaper.animate().translationY(-100f)
                    .setDuration(500).setInterpolator(AccelerateInterpolator()).start()
                binding.viewPager.animate().translationY(0f).setDuration(500)
                    .setInterpolator(AccelerateInterpolator()).start()
                Log.d("checkwallpaperlocation", "wallpaper location is first")

                //    Get the ConstraintLayout that contains the views
                val constraintLayout = binding.root
                val constraintSet = ConstraintSet()
                constraintSet.clone(constraintLayout)
                constraintSet.connect(
                    binding.viewPager.id,
                    ConstraintSet.TOP,
                    binding.tabLayout.id,
                    ConstraintSet.BOTTOM,
                    0
                )
                // Get the ConstraintLayout that contains the views
                constraintSet.applyTo(constraintLayout)
            }
        })

        if (isOnline(applicationContext)) {

            loadOnDemandBannerAd(
                this@LiveWallpaperActivity,
                findViewById(R.id.frameLayout),
                bannerAdId,
                BannerAdType.Banner
            )

            Log.d("checkthingsasd", "things are here")
        }

        Log.d("checkvalueofi", "i is " + i)
        binding.tabLayout.setupWithViewPager(binding.viewPager)
        binding.viewPager.offscreenPageLimit = 2
        binding.viewPager.adapter = LiveWallpaperPagerAdapter(this, supportFragmentManager)

    }

    class LoadingDialog(context: Context) : Dialog(context, R.style.LoadingDialogTheme) {
        private val mContext = context

        override fun onCreate(savedInstanceState: Bundle?) {
            super.onCreate(savedInstanceState)
            requestWindowFeature(Window.FEATURE_NO_TITLE)
            val inflater =
                mContext.getSystemService(Context.LAYOUT_INFLATER_SERVICE) as LayoutInflater
            val inflateView =
                inflater.inflate(R.layout.progress_layout, findViewById(R.id.loading_container))
            val inset =
                InsetDrawable(ColorDrawable(Color.TRANSPARENT), 0, 0, 0, 185) // for margin bottom
            inflateView.setBackgroundDrawable(inset)
            setCancelable(true)
            setCanceledOnTouchOutside(true)
            setContentView(inflateView)
        }
    }


    override fun onDestroy() {
        if (loader != null && loader!!.isShowing) {
            loader!!.dismiss()
            loader = null
        }
        Log.d("checkrecreate", "actuvuty recreated")
        super.onDestroy()
    }

    @RequiresApi(Build.VERSION_CODES.O_MR1)
    override fun onResume() {
        super.onResume()
//        Log.d("ujh", "onResume: ")
        openAd = false
        pause = false
        Log.d("checkonresume", "onresume called")
        val wallpaperManager = WallpaperManager.getInstance(this@LiveWallpaperActivity)
        val currentWallpaper = wallpaperManager.wallpaperInfo


        var getBoolean: Boolean = sharedPref.getBoolean("check_wallpaper_set", false)


        if (Build.VERSION.SDK_INT > Build.VERSION_CODES.R) {
            Log.d("checkverison", "inside first ")
            // Code for devices running Android higher
            //this code is for android 12 and 13 and above
            if (!getBoolean) {
                // Wallpaper of your app is not currently set
                binding.parentLayoutofDisableWallpaper.visibility = View.GONE
            } else {
                // Wallpaper of your app is currently set
                binding.parentLayoutofDisableWallpaper.visibility = View.VISIBLE
                binding.tabLayout.elevation = 4f
                binding.parentLayoutofDisableWallpaper.elevation = 1f
                binding.parentLayoutofDisableWallpaper.animate()?.translationY(100f)
                    ?.setDuration(1000)?.setInterpolator(AccelerateInterpolator())?.start()
                binding.viewPager.animate().translationY(100f).setDuration(1000)
                    .setInterpolator(AccelerateInterpolator()).start()
                // Get the ConstraintLayout that contains the views
                val constraintLayout = binding.root

                val constraintSet = ConstraintSet()

                constraintSet.clone(constraintLayout)

                constraintSet.connect(
                    binding.viewPager.id,
                    ConstraintSet.TOP,
                    binding.parentLayoutofDisableWallpaper.id,
                    ConstraintSet.BOTTOM,
                    0
                )

                constraintSet.applyTo(constraintLayout)

                binding.wallpaperEnabledSwitch.isChecked = true

            }
        } else {
            Log.d("checkverison", "inside second ")
            // Code for devices running Android versions higher than 11
            if (currentWallpaper != null && currentWallpaper.component.packageName == this@LiveWallpaperActivity.packageName) {
                binding.parentLayoutofDisableWallpaper.visibility = View.VISIBLE
                binding.tabLayout.elevation = 4f
                binding.parentLayoutofDisableWallpaper.elevation = 1f
                binding.parentLayoutofDisableWallpaper.animate()?.translationY(100f)
                    ?.setDuration(1000)?.setInterpolator(AccelerateInterpolator())?.start()
                binding.viewPager.animate()?.translationY(100f)?.setDuration(1000)
                    ?.setInterpolator(AccelerateInterpolator())?.start()
                // Get the ConstraintLayout that contains the views
                val constraintLayout = binding.root

                val constraintSet = ConstraintSet()

                constraintSet.clone(constraintLayout)

                constraintSet.connect(
                    binding.viewPager.id,
                    ConstraintSet.TOP,
                    binding.parentLayoutofDisableWallpaper.id,
                    ConstraintSet.BOTTOM,
                    0
                )

                constraintSet.applyTo(constraintLayout)


                binding.wallpaperEnabledSwitch.isChecked = true
            } else {
                binding.parentLayoutofDisableWallpaper.visibility = View.GONE
            }
        }
        if (sharedPref.getBoolean("check_wallpaper_set", false)) {
            // Get the ConstraintLayout that contains the views
            val constraintLayout = binding.root

            val constraintSet = ConstraintSet()

            constraintSet.clone(constraintLayout)

            constraintSet.connect(
                binding.viewPager.id,
                ConstraintSet.TOP,
                binding.parentLayoutofDisableWallpaper.id,
                ConstraintSet.BOTTOM,
                0
            )
            constraintSet.applyTo(constraintLayout)
        }
    }

    override fun onStop() {
        super.onStop()
        if (!wallpaperClicked) openAd = true
        pause = true
        if (loader != null && loader!!.isShowing) {
            loader!!.dismiss()
            loader = null
        }
    }

    override fun onBackPressed() {
        if (loader != null && loader!!.isShowing) {
            pause = true
        }
        Log.d("jhjhjkhjkhkjhkjh", "onBackPressed: jhjkhjhkj")

        super.onBackPressed()
    }


    companion object {
        var loader: LoadingDialog? = null
        var pause = false
        var wallpaperClicked = false
    }


}

