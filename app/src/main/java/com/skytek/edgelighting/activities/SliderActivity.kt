package com.skytek.edgelighting.activities

import android.content.Context
import android.content.Intent
import android.os.Bundle
import android.util.Log
import android.view.View
import android.view.ViewGroup
import androidx.appcompat.app.AppCompatActivity
import androidx.core.content.ContextCompat
import com.google.android.gms.ads.formats.UnifiedNativeAd
import com.google.android.gms.ads.nativead.NativeAd
import com.mobi.pixels.adBannerOnDemand.loadOnDemandBannerAd
import com.mobi.pixels.enums.BannerAdType
import com.skytek.edgelighting.R
import com.skytek.edgelighting.activities.onboarding.EdgeAndWallpaperOnboarding
import com.skytek.edgelighting.adapter.SliderAdapter
import com.skytek.edgelighting.databinding.ActivitySliderBinding
import com.skytek.edgelighting.utils.AdResources.bannerAdId
import com.skytek.edgelighting.utils.AdResources.onboardingShow
import com.skytek.edgelighting.utils.AdResources.permissionOnboardingAdShow
import com.skytek.edgelighting.utils.AdResources.wholeScreenAdShow
import com.smarteist.autoimageslider.SliderView

class SliderActivity : AppCompatActivity() {

    private lateinit var binding: ActivitySliderBinding
    lateinit var sliderView: SliderView
    private lateinit var sliderAdapter: SliderAdapter
    private lateinit var nativeAd: UnifiedNativeAd
    private var mNativeAd: NativeAd? = null


    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivitySliderBinding.inflate(layoutInflater)
        setContentView(binding.root)

        window.statusBarColor = ContextCompat.getColor(this, R.color.background)

        sliderView = findViewById(R.id.slider)
        val sliderDataArrayList = ArrayList<SliderData>()
        sliderDataArrayList.add(
            SliderData(
                getString(R.string.slider_Edge),
                getString(R.string.slider_Edge1),
                R.drawable.slider_edge_lighting
            )
        )

        sliderDataArrayList.add(
            SliderData(
                getString(R.string.alert_Edge),
                getString(R.string.alert_Edge1),
                R.drawable.slideralert
            )
        )

        sliderDataArrayList.add(
            SliderData(
                getString(R.string.warning_Edge),
                getString(R.string.warning_Edge1),
                R.drawable.sliderwarning
            )
        )

        sliderDataArrayList.add(
            SliderData(
                getString(R.string.notch_Edge),
                getString(R.string.notch_Edge1),
                R.drawable.slidernotch
            )
        )

        sliderDataArrayList.add(
            SliderData(
                getString(R.string.wallpaper_Edge),
                getString(R.string.wallpaper_Edge1),
                R.drawable.sliderwallpaper
            )
        )

        // Pass the array of drawable resource IDs to the SliderAdapter.
        sliderAdapter = SliderAdapter(sliderDataArrayList.toList())

        // Set the adapter to the slider view.
        sliderView.setSliderAdapter(sliderAdapter)

        // Set other properties for the slider (auto cycle direction, scroll time, etc.) as needed.
        sliderView.autoCycleDirection = SliderView.LAYOUT_DIRECTION_LTR
        sliderView.scrollTimeInSec = 3
        sliderView.isAutoCycle = true
        sliderView.startAutoCycle()
        Log.d("abcd", "asdasd")


        binding.btn.setOnClickListener {
            if (isFirstTime() && !firstTimeInstall()) {
                markOnboardingAsCompleted()
                startActivity(Intent(this@SliderActivity, EdgeAndWallpaperOnboarding::class.java))

                finish()
            } else {
                markFirstOnboardingAsCompleted()
                startActivity(Intent(this@SliderActivity, MainActivity::class.java))
                finish()
            }
        }
//        loadNativeAd(this@SliderActivity)

        /*   loadNativeAd(this, binding.sliderNativeAdContainer, getString(R.string.EL_NativeBannerAD), NativeAdType.NativeAdvance)
               .backgroundColor("#1C2754")
               .textColor("#ffffff")
               .buttonColor("#141D3C")
               .adIcon(NativeAdIcon.White)
               .shimmerEffect(true)
               .shimmerBackgroundColor("#000000")
               .shimmerColor(ShimmerColor.White)
               .callback { loaded, failed ->
                   // Callback logic here
               }.load()*/

//        setBottomMargin(binding.sliderNativeAdContainer, getNavigationBarHeight(this))
        if (wholeScreenAdShow && permissionOnboardingAdShow) {
            loadOnDemandBannerAd(
                this, binding.sliderNativeAdContainer, bannerAdId, BannerAdType.Banner
            ).enableShimmerEffect(true).load()
        }


    }

    private fun markOnboardingAsCompleted() {
        if (onboardingShow) {
            return
        }
        Log.d("onboardingShowkjnodesoivbivbu", "markOnboardingAsCompleted:${onboardingShow} ")
        val sharedPreferences = getSharedPreferences("onboarding", MODE_PRIVATE)
        val editor = sharedPreferences.edit()
        editor.putBoolean("isFirstTime", false)
        editor.apply()
    }

    private fun markFirstOnboardingAsCompleted() {

        Log.d("onboardingShowkjnodesoivbivbu", "markOnboardingAsCompleted:${onboardingShow} ")
        val sharedPreferences = getSharedPreferences("firstTimeInstall", MODE_PRIVATE)
        val editor = sharedPreferences.edit()
        editor.putBoolean("isFirstTimeInstall", false)
        editor.apply()
    }

    private fun firstTimeInstall(): Boolean {
        val sharedPreferences = getSharedPreferences("firstTimeInstall", MODE_PRIVATE)
        return sharedPreferences.getBoolean("isFirstTimeInstall", true)
    }

    private fun isFirstTime(): Boolean {
        val sharedPreferences = getSharedPreferences("onboarding", MODE_PRIVATE)
        return sharedPreferences.getBoolean("isFirstTime", true)
    }

    private fun getNavigationBarHeight(context: Context): Int {
        var result = 0
        val resourceId =
            context.resources.getIdentifier("navigation_bar_height", "dimen", "android")
        if (resourceId > 0) {
            result = context.resources.getDimensionPixelSize(resourceId)
        }
        return result
    }

    private fun setBottomMargin(view: View, bottomMargin: Int) {
        if (view.layoutParams is ViewGroup.MarginLayoutParams) {
            val layoutParams = view.layoutParams as ViewGroup.MarginLayoutParams
            layoutParams.bottomMargin = bottomMargin
            view.requestLayout()
        }
    }
}


data class SliderData(val title: String, val description: String, val image: Int)