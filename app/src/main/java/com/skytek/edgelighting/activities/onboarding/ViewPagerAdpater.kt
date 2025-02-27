package com.skytek.edgelighting.activities.onboarding

import android.app.Activity
import android.content.Context
import android.graphics.Color
import android.media.AudioAttributes
import android.media.MediaPlayer
import android.net.Uri
import android.os.Handler
import android.os.Looper
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.FrameLayout
import android.widget.ImageView
import android.widget.TextView
import android.widget.Toast
import androidx.core.content.ContextCompat
import androidx.recyclerview.widget.RecyclerView
import com.google.android.gms.ads.nativead.NativeAd
import com.google.android.gms.ads.nativead.NativeAdView
import com.skytek.edgelighting.R
import com.skytek.edgelighting.utils.AdResources.preloadedAd
//import com.masoudss.lib.WaveformSeekBar
//import com.masoudss.lib.utils.WaveGravity

import java.io.File
import java.io.FileOutputStream


class ViewPagerAdapter(private var layouts: MutableList<Int>, private val activity: Activity) :
    RecyclerView.Adapter<ViewPagerAdapter.ViewHolder>() {
    private var mediaPlayer: MediaPlayer? = null
    private var updateRunnable: Runnable? = null
    private var currentlyPlayingResId: Int? = null
    private val handler = Handler(Looper.getMainLooper())
    private var currentlyPlayingPlayBtn: ImageView? = null
    private var currentlyPlayingPauseBtn: ImageView? = null

    init {
//        if(AdHelper.wholeAdShown){
//
//            if (preloadedAd == null){
//                preloadAd()
//            }
//
//        }
    }

    inner class ViewHolder(view: View) : RecyclerView.ViewHolder(view)

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
        val view = LayoutInflater.from(activity).inflate(layouts[viewType], parent, false)
        return ViewHolder(view)
    }

    override fun onBindViewHolder(holder: ViewHolder, position: Int) {
        Log.d(
            "nsfaafv",
            "layouts[position]:  ${layouts[position] == R.layout.activity_on_boarding_one}"
        )



//        holder.binding.wave.setSampleFrom(audioFile)
        if (true) {
            if (layouts[position] == R.layout.onboarding_add) {
                Log.d("onboardingdebug", "Attempting to show OnBoarding Ad: ")
                val nativeAdContainer =
                    holder.itemView.findViewById<FrameLayout>(R.id.native_ad_container)

                if (preloadedAd != null) {
                    // Inflate and populate the native ad view
                    Log.d("onboardingdebug", "Displaying preloaded ad.")
                    val adView = LayoutInflater.from(activity).inflate(
                        R.layout.onboarding_native,
                        nativeAdContainer,
                        false
                    ) as NativeAdView


                    populateNativeAdView(preloadedAd!!, adView)

                    nativeAdContainer.removeAllViews()
                    nativeAdContainer.addView(adView)
                    nativeAdContainer.visibility = View.VISIBLE
                } else {

                    Toast.makeText(activity, "OnBoardingAdd not loaded", Toast.LENGTH_SHORT).show()
                }
            }
        }
    }



 fun releaseMediaPlayer() {
        mediaPlayer?.release()
        mediaPlayer = null
        currentlyPlayingResId = null

        // Reset UI
        currentlyPlayingPlayBtn?.visibility = View.VISIBLE
        currentlyPlayingPauseBtn?.visibility = View.GONE
        currentlyPlayingPlayBtn = null
        currentlyPlayingPauseBtn = null
    }

    override fun getItemCount(): Int = layouts.size

    override fun getItemViewType(position: Int): Int = position

    private fun populateNativeAdView(nativeAd: NativeAd, adView: NativeAdView) {
        adView.mediaView = adView.findViewById(R.id.ad_media)
        adView.headlineView = adView.findViewById(R.id.ad_headline)
        adView.bodyView = adView.findViewById(R.id.ad_body)
        adView.callToActionView = adView.findViewById(R.id.ad_call_to_action)
        adView.iconView = adView.findViewById(R.id.ad_app_icon)



        (adView.headlineView as TextView).text = nativeAd.headline
        adView.mediaView?.mediaContent = nativeAd.mediaContent

        adView.bodyView?.visibility = if (nativeAd.body == null) View.INVISIBLE else View.VISIBLE
        (adView.bodyView as? TextView)?.text = nativeAd.body

        adView.callToActionView?.visibility =
            if (nativeAd.callToAction == null) View.INVISIBLE else View.VISIBLE
        (adView.callToActionView as? TextView)?.text = nativeAd.callToAction

        adView.iconView?.visibility =
            if (nativeAd.icon == null) View.GONE else View.VISIBLE
        (adView.iconView as? ImageView)?.setImageDrawable(nativeAd.icon?.drawable)

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

        adView.setNativeAd(nativeAd)
    }


//    private fun preloadAd() {
//
//        Log.d("onboardingdebug", "preloading OnBoarding Ad: ")
//        val adLoader = AdLoader.Builder(activity, activity.resources.getString(R.string.admob_native_ad_id_boarding))
//            .forNativeAd { nativeAd ->
//                preloadedAd = nativeAd // Store the preloaded ad
//            }
//            .withAdListener(object : AdListener() {
//                override fun onAdFailedToLoad(adError: LoadAdError) {
//                    // Log or handle the ad loading failure
//                    preloadedAd = null
//
//                }
//            })
//            .build()
//
//        adLoader.loadAd(AdRequest.Builder().build())
//    }
}


