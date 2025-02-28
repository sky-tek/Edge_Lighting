package com.mobi.pixels.adBannerOnDemand

import android.app.Activity
import android.os.Bundle
import android.util.DisplayMetrics
import android.view.View
import android.widget.FrameLayout
import com.mobi.pixels.enums.BannerAdType
import com.mobi.pixels.enums.ShimmerColor
import com.google.ads.mediation.admob.AdMobAdapter
import com.google.android.gms.ads.AdListener
import com.google.android.gms.ads.AdRequest
import com.google.android.gms.ads.AdSize
import com.google.android.gms.ads.AdView
import com.google.android.gms.ads.LoadAdError
import com.mobi.pixels.initialize.Ads
import com.mobi.pixels.isOnline
import com.mobi.pixels.shimmerBanner

class AdBannerOnDemand(
    private val context: Activity,
    private val bannerAdContainer: FrameLayout,
    private val adUnitId: String,
    private val bannerAdType: BannerAdType
)
{

    private var shimmerEffectEnabled: Boolean = false
    private var shimmerColor: ShimmerColor? = null
    private var shimmerBackgroundColor: String? = null
    private var adLoadListener: AdBannerOnDemandListeners? = null

    fun enableShimmerEffect(enable: Boolean) = apply {
        shimmerEffectEnabled = enable
    }

    fun setShimmerBackgroundColor(color: String) = apply {
        shimmerBackgroundColor = color
    }
    fun setShimmerColor(color: ShimmerColor) = apply {
        shimmerColor = color
    }

    fun adListeners(listener: AdBannerOnDemandListeners?) = apply {
        adLoadListener = listener
    }

    fun load():AdView? {

        if (!isOnline(context)) {
            adLoadListener?.onAdFailedToLoad("Internet connection not detected. Please check your connection and try again.")
            bannerAdContainer.visibility = View.GONE
            return null
        }

        if (!Ads.IsAdsAllowed) {
            adLoadListener?.onAdFailedToLoad("Ads disabled by developer.")
            bannerAdContainer.visibility = View.GONE
            return null
        }

        setupBannerContainer()

        if (shimmerEffectEnabled) {
            shimmerBanner(context, bannerAdContainer, shimmerColor, shimmerBackgroundColor)
        }

        val adView = AdView(context).apply {
            adUnitId = this@AdBannerOnDemand.adUnitId
            setAdSize(getAdaptiveAdSize(context as Activity))
            adListener = createAdListener(this)
        }

         adView.loadAd(createAdRequest())
   return adView
    }

    private fun setupBannerContainer() {
        bannerAdContainer.apply {
            visibility = View.VISIBLE
            minimumHeight =  FrameLayout.LayoutParams.WRAP_CONTENT
            layoutParams.width = FrameLayout.LayoutParams.MATCH_PARENT
            removeAllViews()
        }
    }

    private fun createAdRequest(): AdRequest {
        return AdRequest.Builder().apply {
            if (bannerAdType == BannerAdType.CollapsibleBanner) {
                val extras = Bundle().apply {
                    putString("collapsible", "bottom")
                }
                addNetworkExtrasBundle(AdMobAdapter::class.java, extras)
            }
        }.build()
    }

    private fun createAdListener(adView: AdView) = object : AdListener() {
        override fun onAdFailedToLoad(adError: LoadAdError) {
            bannerAdContainer.visibility = View.GONE
            adLoadListener?.onAdFailedToLoad(adError.message)
        }

        override fun onAdLoaded() {
            bannerAdContainer.apply {
                removeAllViews()
                addView(adView)
            }
            adLoadListener?.onAdLoaded()
        }
    }

    private fun getAdaptiveAdSize(activity: Activity): AdSize {
        val displayMetrics = DisplayMetrics().also {
            activity.windowManager.defaultDisplay.getMetrics(it)
        }
        val adWidth = (displayMetrics.widthPixels / displayMetrics.density).toInt()
        return AdSize.getCurrentOrientationAnchoredAdaptiveBannerAdSize(activity, adWidth)
    }
}

fun loadOnDemandBannerAd(
    context: Activity,
    bannerAdContainer: FrameLayout,
    adUnitId: String,
    bannerAdType: BannerAdType
): AdBannerOnDemand {
    return AdBannerOnDemand(context, bannerAdContainer, adUnitId, bannerAdType)
}