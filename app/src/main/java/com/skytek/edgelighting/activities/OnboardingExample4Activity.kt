package com.thecode.onboardingviewagerexamples.activities

import android.annotation.SuppressLint
import android.content.Intent
import android.os.Bundle
import android.provider.Settings
import android.util.Log
import android.widget.TextView
import androidx.appcompat.app.AppCompatActivity
import androidx.viewpager2.widget.ViewPager2
import com.google.android.material.tabs.TabLayoutMediator
import com.mobi.pixels.adBannerOnDemand.loadOnDemandBannerAd
import com.mobi.pixels.adNativeOnDemand.AdNativeOnDemandListeners
import com.mobi.pixels.adNativeOnDemand.loadOnDemandNativeAd
import com.mobi.pixels.enums.BannerAdType
import com.mobi.pixels.enums.NativeAdIcon
import com.mobi.pixels.enums.NativeAdType
import com.mobi.pixels.enums.ShimmerColor
import com.skytek.edgelighting.App
import com.skytek.edgelighting.R
import com.skytek.edgelighting.activities.MainActivity
import com.skytek.edgelighting.activities.SliderActivity
import com.skytek.edgelighting.activities.onboarding.EdgeAndWallpaperOnboarding
import com.skytek.edgelighting.databinding.ActivityOnboardingExample4Binding
import com.skytek.edgelighting.utils.AdResources.NativeToBanner
import com.skytek.edgelighting.utils.AdResources.bannerAdId
import com.skytek.edgelighting.utils.AdResources.onboardingShow
import com.skytek.edgelighting.utils.AdResources.permissionOnBoardingAdId
import com.skytek.edgelighting.utils.AdResources.permissionOnboardingAdShow
import com.skytek.edgelighting.utils.AdResources.wholeScreenAdShow
import com.skytek.edgelighting.utils.MySharePreferencesEdge
import com.skytek.edgelighting.utils.adTimeTraker.updateLastAdShownTime
import com.thecode.onboardingviewagerexamples.adapters.OnboardingViewPagerAdapter4


class OnboardingExample4Activity : AppCompatActivity() {

    private lateinit var mViewPager: ViewPager2
    private lateinit var btnBack: TextView
    private lateinit var btnNext: TextView

    private lateinit var binding: ActivityOnboardingExample4Binding

    @SuppressLint("ResourceType")
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityOnboardingExample4Binding.inflate(layoutInflater)
        val view = binding.root
        setContentView(view)
        if (wholeScreenAdShow && permissionOnboardingAdShow) {
            val adContainer = binding.adContainer

            if (adContainer != null) {
                if (NativeToBanner) {
                    loadOnDemandNativeAd(
                        this, adContainer, permissionOnBoardingAdId, NativeAdType.NativeSmall
                    ).setBackgroundColor(resources.getString(R.color.round_background))
                        .setTextColorButton("#ffffff").setTextColorTitle("#ffffff")
                        .setTextColorDescription("#ffffff").setButtonColor("#0071EC")
                        .setButtonRoundness(15).setAdIcon(NativeAdIcon.White)
                        .enableShimmerEffect(true).setShimmerBackgroundColor("#000000")
                        .setShimmerColor(ShimmerColor.White)
                        .adListeners(object : AdNativeOnDemandListeners {
                            override fun onAdFailedToLoad(error: String) {
                                Log.d("error", "onAdFailedToLoad:$error ")
                            }

                            override fun onAdLoaded() {
                                Log.d("error", "onAdLoaded: ")
                            }

                        }).load()
                } else {
                    loadOnDemandBannerAd(
                        this, adContainer, bannerAdId, BannerAdType.Banner
                    ).enableShimmerEffect(true).load()
                }
            } else {
                Log.e("OnboardingExample4Activity", "adContainer is null")
            }
        }

        mViewPager = binding.viewPager
        mViewPager.adapter = OnboardingViewPagerAdapter4(this, this)
        mViewPager.offscreenPageLimit = 1
        btnBack = binding.btnPreviousStep
        btnNext = binding.btnNextStep
        mViewPager.registerOnPageChangeCallback(object : ViewPager2.OnPageChangeCallback() {
            override fun onPageSelected(position: Int) {
                if (position == 1) {
                    btnNext.text = getText(R.string.allow)
                } else {
                    btnNext.text = getText(R.string.next)
                }
            }

            override fun onPageScrolled(arg0: Int, arg1: Float, arg2: Int) {}
            override fun onPageScrollStateChanged(arg0: Int) {}
        })
        TabLayoutMediator(binding.pageIndicator, mViewPager) { _, _ -> }.attach()
        btnNext.setOnClickListener {
            updateLastAdShownTime()
            if (getItem() > mViewPager.childCount - 1) {
                val intent = Intent(Settings.ACTION_ACCESSIBILITY_SETTINGS)
                intent.flags = Intent.FLAG_ACTIVITY_NEW_TASK
                startActivity(intent)
            } else {
                mViewPager.setCurrentItem(getItem() + 1, true)
            }
        }

        btnBack.setOnClickListener {
            Log.d("onboardingShowkjnodesoivbivbu", "markOnboardingAsCompleted:${isFirstTime()} ")
            Log.d(
                "onboardingShowkjnodesoivbivbu", "markOnboardingAsCompleted:${!firstTimeInstall()} "
            )
            updateLastAdShownTime()
            if (isFirstTime() && !firstTimeInstall()) {
                markOnboardingAsCompleted()
                val intent = Intent(this, EdgeAndWallpaperOnboarding::class.java)


                startActivity(intent)
                finish()
            } else {
                markFirstOnboardingAsCompleted()
                startActivity(Intent(this@OnboardingExample4Activity, MainActivity::class.java))
                finish()
            }
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

    override fun onResume() {
        super.onResume()

        if (MySharePreferencesEdge.getAccessibilityEnabled(
                MySharePreferencesEdge.ACCESSIBILITY_BROADCAST, App.context
            )
        ) {
            startActivity(Intent(this@OnboardingExample4Activity, SliderActivity::class.java))
            finish()
        }
    }

    override fun onBackPressed() {
        super.onBackPressed()
        if (isFirstTime() && !firstTimeInstall()) {
            markOnboardingAsCompleted()
            val intent = Intent(this, EdgeAndWallpaperOnboarding::class.java)

            intent.putExtra("onBoardingScreen", true)
            startActivity(intent)
            finish()
        } else {
            markFirstOnboardingAsCompleted()
            startActivity(Intent(this@OnboardingExample4Activity, MainActivity::class.java))
            finish()
        }
    }

    private fun getItem(): Int {
        return mViewPager.currentItem
    }
}
