package com.skytek.edgelighting.activities.onboarding


import android.content.Intent
import android.net.Uri
import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.widget.FrameLayout
import android.widget.ImageView
import android.widget.LinearLayout
import android.widget.TextView
import androidx.appcompat.app.AppCompatActivity
import androidx.cardview.widget.CardView
import androidx.core.view.isVisible
import androidx.viewpager2.widget.ViewPager2
import com.google.android.gms.ads.nativead.NativeAd
import com.google.android.gms.ads.nativead.NativeAdView
import com.skytek.edgelighting.R
import com.skytek.edgelighting.activities.MainActivity
import com.skytek.edgelighting.utils.AdResources
import com.skytek.edgelighting.utils.AdResources.preloadedAd
import com.skytek.edgelighting.utils.AdResources.preloadedAd2

import com.tbuonomo.viewpagerdotsindicator.DotsIndicator

class MainActivityOnBoarding : AppCompatActivity() {

    private lateinit var viewPager2: ViewPager2
    private lateinit var tvDesc: TextView
    private lateinit var getStarted: TextView
    private lateinit var adSection: CardView
    private lateinit var dots: Array<TextView>
    private var adapter: ViewPagerAdapter? = null
    private val onBoardingPosition = 1L
    private lateinit var videoPathsSaving: MutableList<Uri>

    private lateinit var layouts: MutableList<Int>



    // Default or passed value for current position

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main_on_boarding)


        viewPager2 = findViewById(R.id.viewPager)


//        tvNext = findViewById(R.id.tvNext)
        // dotsLayout = findViewById(R.id.layoutDots)

        tvDesc = findViewById(R.id.tv_decs)
        getStarted = findViewById(R.id.getStarted)
//        adSection = findViewById(R.id.adSection)
        val adContainer = findViewById<FrameLayout>(R.id.AdContainer)

        val dotsIndicator = findViewById<DotsIndicator>(R.id.dots_indicator)
        val bottomSheet = findViewById<LinearLayout>(R.id.bottomSheet)


        // Initialize layouts

        layouts = rearrangeLayouts(onBoardingPosition)

        val adView = LayoutInflater.from(this).inflate(
            R.layout.native_ad_layout,
            adContainer,
            false
        ) as NativeAdView
        // Set up ViewPager Adapter
        adapter = ViewPagerAdapter(layouts, this@MainActivityOnBoarding)
        viewPager2.adapter = adapter
        dotsIndicator.attachTo(viewPager2)

        if (preloadedAd2 != null) {
            populateNativeAdView(preloadedAd2!!, adView)
            adContainer.removeAllViews()
            adContainer.addView(adView)
        }
        viewPager2.post {
            viewPager2.currentItem = 0

        }


        // Listen to page change events in ViewPager2
        viewPager2.registerOnPageChangeCallback(object : ViewPager2.OnPageChangeCallback() {
            override fun onPageSelected(position: Int) {
                super.onPageSelected(position)

                val currentLayout = layouts[position]

                when (currentLayout) {
                    R.layout.activity_on_boarding_one -> {

                        tvDesc.isVisible = false
                        getStarted.isVisible = false
                        adContainer.isVisible = true
                        Log.d("bsjvcjvjsvs", "activity_on_boarding_one: ")
                        // Custom event for layout "one"
//                        AdHelper.Event(this@MainActivityOnBoarding, "_CRA_ONBOARDING_ONE_SHOWN")
                        // Any additional logic for layout "one"
                    }

                    R.layout.activity_on_boarding_two -> {

                        adapter?.releaseMediaPlayer()
                        tvDesc.text="Edge Lightning\nBorder Effects"
                        getStarted.isVisible = false
                        adContainer.isVisible = true
                        Log.d("bsjvcjvjsvs", "activity_on_boarding_two: ")
                        // Custom event for layout "two"
//                        AdHelper.Event(this@MainActivityOnBoarding, "_CRA_ONBOARDING_TWO_SHOWN")
                        // Any additional logic for layout "two"
                    }

                    R.layout.activity_on_boarding_three -> {


                        getStarted.isVisible = true
                        tvDesc.text="Stunning Live/Static\nWallpapers"

                        getStarted.setOnClickListener {
                            startActivity(
                                Intent(
                                    this@MainActivityOnBoarding,
                                    MainActivity::class.java
                                )
                            )
                            finish()
                        }
                        Log.d("bsjvcjvjsvs", "activity_on_boarding_three: ")
                        // Custom event for layout "three"
//                        AdHelper.Event(this@MainActivityOnBoarding, "_CRA_ONBOARDING_THREE_SHOWN")
                        // Any additional logic for layout "three"
                    }
                }

                if (currentLayout == R.layout.onboarding_add) {
                    // Hide dotsIndicator and continue button when on onboarding_add
                    dotsIndicator.visibility = View.INVISIBLE
                    bottomSheet.isVisible = false
//                    continue_btn.visibility = View.GONE
                } else {
                    // Show dotsIndicator and continue button for other layouts
                    dotsIndicator.visibility = View.VISIBLE
                    bottomSheet.isVisible = true
//                    continue_btn.visibility = View.VISIBLE

                    // Update the button text and behavior based on the position
//                    if (position == layouts.size - 1) {
//                        continue_btn.text = resources.getString(R.string.continue_btn)
//                        continue_btn.setOnClickListener {
//                            // Navigate to FrontActivity when onboarding finishes
//                            startActivity(Intent(this@MainActivityOnBoarding, MainActivity::class.java))
//                            finish()
//
//                            // Show the interstitial ad after onboarding
////                            if (isSplashInterstitialloaded) {
////                                showInterstitialAd()
////                            }
//                        }
//                    } else
//                    {
//                        continue_btn.text = resources.getString(R.string.next)
//                        continue_btn.setOnClickListener {
//                            // Move to the next item in ViewPager2
//                            viewPager2.currentItem = position + 1
//                        }
//                    }
                }
            }
        })

    }


    private fun getItem(i: Int): Int {
        return viewPager2.currentItem + i
    }

    private fun rearrangeLayouts(onBoardingPosition: Long): MutableList<Int> {
        val originalLayouts = mutableListOf<Int>()

        // Check RemoteConfig values and add the corresponding layouts
        if (false) {
            originalLayouts.add(R.layout.activity_on_boarding_one)
        }
        if (true) {
            originalLayouts.add(R.layout.activity_on_boarding_two)
        }
        if (true) {
            originalLayouts.add(R.layout.activity_on_boarding_three)
        }

        // Ensure the position is valid (1-based index for placement)
        val validPosition = when (onBoardingPosition) {
            1L -> 1
            2L -> 2
            else -> 2// Default to the end if out of range
        }

        // Add the onboarding_add layout if `wholeAdShown` is true and add is loaded
        if (AdResources.areNativeAdsEnabled && preloadedAd != null) {
            if (validPosition <= originalLayouts.size) {
                originalLayouts.add(validPosition, R.layout.onboarding_add)
            } else {
                originalLayouts.add(R.layout.onboarding_add) // Append to the end
            }
        }

        return originalLayouts
    }


    private fun showInterstitialAd() {
        /*   Interstitial.show(this@MainActivityOnBoarding, object : AdInterstitialShowListeners {
               override fun onShowed() {
                   if (SharedPreferenceUtils.getBooleanValue(this@MainActivityOnBoarding, "isFirstTimeADShow") == false) {
                       AdHelper.Event(this@MainActivityOnBoarding, "_CRA_SA_FT_AD_SHOW")
                       SharedPreferenceUtils.saveBoolean(this@MainActivityOnBoarding, "isFirstTimeADShow", true)
                   }
                   Log.d("SplashScreen", "Ad showed successfully")
               }

               override fun onDismissed() {
                   Log.d("SplashScreen", "Ad dismissed")
                   SplashScreen.isAdDissmissed = true
               }

               override fun onError(error: String) {
                   if (SharedPreferenceUtils.getBooleanValue(this@MainActivityOnBoarding, "isFirstTimeADShow") == false) {
                       AdHelper.Event(this@MainActivityOnBoarding, "_CRA_SA_FT_AD_ERROR")
                       SharedPreferenceUtils.saveBoolean(this@MainActivityOnBoarding, "isFirstTimeADShow", true)
                   }
                   Log.e("dasdsad", "Error showing ad")
                   SplashScreen.isAdDissmissed = true
               }
           })*/
    }

    private fun populateNativeAdView(nativeAd: NativeAd, adView: NativeAdView) {
        Log.d("bsacbcjjs", "populateNativeAdView: $nativeAd")

        // Initialize the views in the ad view layout
        adView.mediaView = adView.findViewById(R.id.ad_media)
        adView.headlineView = adView.findViewById(R.id.ad_headline)
        adView.bodyView = adView.findViewById(R.id.ad_body)
        adView.callToActionView = adView.findViewById(R.id.ad_call_to_action)
        adView.iconView = adView.findViewById(R.id.ad_app_icon)

        // Set the headline
        (adView.headlineView as TextView).text = nativeAd.headline

        // Set the media content
        adView.mediaView?.mediaContent = nativeAd.mediaContent

        // Handle visibility and content of body text
        adView.bodyView?.visibility = if (nativeAd.body == null) View.INVISIBLE else View.VISIBLE
        (adView.bodyView as? TextView)?.text = nativeAd.body

        // Handle visibility and content of call to action
        adView.callToActionView?.visibility =
            if (nativeAd.callToAction == null) View.INVISIBLE else View.VISIBLE
        (adView.callToActionView as? TextView)?.text = nativeAd.callToAction

        // Handle visibility and content of ad icon
        adView.iconView?.visibility =
            if (nativeAd.icon == null) View.GONE else View.VISIBLE
        (adView.iconView as? ImageView)?.setImageDrawable(nativeAd.icon?.drawable)

        // Set visibility of media (video or image)
        if (nativeAd.mediaContent != null) {
            // If media content (video) is available, show the MediaView and hide the ImageView
            adView.mediaView?.visibility = View.VISIBLE
            (adView.iconView as? ImageView)?.visibility = View.GONE
        } else {
            // If no media content (video) is available, hide the MediaView and show the ImageView
            adView.mediaView?.visibility = View.GONE
            (adView.iconView as? ImageView)?.visibility = View.VISIBLE
        }

        // Uncomment these lines to make the views visible when their data is available
        adView.priceView?.visibility =
            if (nativeAd.price == null) View.INVISIBLE else View.VISIBLE
        (adView.priceView as? TextView)?.text = nativeAd.price

        adView.storeView?.visibility =
            if (nativeAd.store == null) View.INVISIBLE else View.VISIBLE
        (adView.storeView as? TextView)?.text = nativeAd.store

        // Rating will not be displayed, so leaving this line commented as requested
        // adView.starRatingView?.visibility =
        //     if (nativeAd.starRating == null) View.INVISIBLE else View.VISIBLE
        // (adView.starRatingView as? RatingBar)?.rating = nativeAd.starRating?.toFloat() ?: 0F

        adView.advertiserView?.visibility =
            if (nativeAd.advertiser == null) View.INVISIBLE else View.VISIBLE
        (adView.advertiserView as? TextView)?.text = nativeAd.advertiser

        // Finally, set the native ad to the ad view
        adView.setNativeAd(nativeAd)
    }

    override fun onPause() {
        super.onPause()
        adapter?.releaseMediaPlayer()
    }

}