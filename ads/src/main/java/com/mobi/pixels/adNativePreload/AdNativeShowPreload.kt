package com.mobi.pixels.adNativePreload

import android.app.Activity
import android.content.Context
import android.graphics.Color
import android.graphics.drawable.GradientDrawable
import android.view.View
import android.widget.Button
import android.widget.FrameLayout
import android.widget.ImageView
import android.widget.LinearLayout
import android.widget.TextView
import com.example.ads.R
import com.google.android.gms.ads.nativead.NativeAdView
import com.mobi.pixels.adNativePreload.AdNativePreload.preloadAd
import com.mobi.pixels.enums.NativeAdIcon
import com.mobi.pixels.enums.NativeAdType
import com.mobi.pixels.enums.NativeLayoutType
import com.mobi.pixels.enums.ShimmerColor
import com.mobi.pixels.initialize.Ads

class ShowAdNativePreload(
    private val ctx: Activity,
    private val nativeAdContainer: FrameLayout,
    private val nativeAdType: NativeAdType,
    private val nativeLayoutType: NativeLayoutType
)
{
    private var textColorTitle: String? = null
    private var textColorDescription: String? = null
    private var textColorButton: String? = null
    private var colorButton: String? = null
    private var backgroundColor: String? = null
    private var buttonRoundness: Int = 0
    private var buttonHeight: Int = 40
    private var adIcon: NativeAdIcon? = null
    private var showListener: AdNativePreloadShowListeners? = null

    fun setTextColorTitle(color: String) = apply { textColorTitle = color }
    fun setTextColorDescription(color: String) = apply { textColorDescription = color }
    fun setTextColorButton(color: String) = apply { textColorButton = color }
    fun setButtonRoundness(buttonRound: Int) = apply { buttonRoundness = buttonRound }
    fun setButtonHeight(buttonHt: Int) = apply { buttonHeight = buttonHt }
    fun setButtonColor(color: String) = apply { colorButton = color }
    fun setBackgroundColor(color: String) = apply { backgroundColor = color }
    fun setAdIcon(icon: NativeAdIcon) = apply { adIcon = icon }
    fun showListeners(listener: AdNativePreloadShowListeners?) = apply {
        showListener = listener
    }

    fun show() {
        if (!Ads.IsAdsAllowed) {
            showListener?.onError("Ads disabled by developer.")
            nativeAdContainer.visibility = View.GONE
            return
        }

        if (preloadAd == null) {
            showListener?.onError("Ad is not loaded yet.")
            nativeAdContainer.visibility = View.GONE
            return
        }

        val layoutId = when (nativeAdType) {
            NativeAdType.NativeSmall -> {
                when(nativeLayoutType){
                    NativeLayoutType.Layout1 -> R.layout.native_small1
                    NativeLayoutType.Layout2 -> R.layout.native_small2
                }

            }
            NativeAdType.NativeAdvance -> {
                when(nativeLayoutType){
                    NativeLayoutType.Layout1 -> R.layout.native_advance1
                    NativeLayoutType.Layout2 -> R.layout.native_advance2
                }
            }
        }
        val nativeView = ctx.layoutInflater.inflate(layoutId, null) as NativeAdView

        nativeAdView(ctx, nativeView)

        nativeAdContainer.apply {
            visibility = View.VISIBLE
            removeAllViews()
            layoutParams.height = FrameLayout.LayoutParams.WRAP_CONTENT
            layoutParams.width = FrameLayout.LayoutParams.MATCH_PARENT
            addView(nativeView)
        }
    }


    private fun nativeAdView(context: Activity, adView: NativeAdView) {
        adView.apply {
            headlineView = findViewById(R.id.ad_headline)
            bodyView = findViewById(R.id.ad_body)
            callToActionView = findViewById(R.id.ad_call_to_action)
            iconView = findViewById(R.id.ad_app_icon)
            if (nativeAdType == NativeAdType.NativeAdvance) {
                mediaView = findViewById(R.id.ad_media)
                mediaView?.mediaContent = preloadAd?.mediaContent
            }

            findViewById<LinearLayout>(R.id.nativeBackground)?.apply {
                backgroundColor?.let { setBackgroundColor(Color.parseColor(it)) }
            }

            findViewById<TextView>(R.id.icon_ad)?.apply {
                adIcon?.let { icon ->
                    val backgroundDrawableRes = if (icon == NativeAdIcon.White) {
                        R.drawable.ad_text_background_white
                    } else {
                        R.drawable.ad_text_background_black
                    }
                    background = context.getDrawable(backgroundDrawableRes)
                    setTextColor(if (icon == NativeAdIcon.White) Color.WHITE else Color.BLACK)
                }
            }

            (headlineView as TextView).apply {
                text = preloadAd?.headline
                isSelected = true
                textColorTitle?.let { setTextColor(Color.parseColor(it)) }
            }

            bodyView?.apply {
                visibility = if (preloadAd?.body == null) View.INVISIBLE else View.VISIBLE
                (this as TextView).apply {
                    text = preloadAd?.body
                    textColorDescription?.let { setTextColor(Color.parseColor(it)) }
                }
            }

            callToActionView?.apply {
                visibility = if (preloadAd?.callToAction == null) View.INVISIBLE else View.VISIBLE
                (this as Button).apply {
                    text = preloadAd?.callToAction
                    textColorButton?.let { setTextColor(Color.parseColor(it)) }
//                    colorButton?.let { backgroundTintList = ColorStateList.valueOf(Color.parseColor(it)) }
                    background = GradientDrawable().apply {
                        cornerRadius = buttonRoundness.dpToPx(context).toFloat()
                        colorButton?.let { setColor(Color.parseColor(it)) }
                    }
                    layoutParams.height = buttonHeight.dpToPx(context)
                }
            }

            iconView?.apply {
                visibility = if (preloadAd?.icon == null) View.GONE else View.VISIBLE
                (this as ImageView).apply {
                    setImageDrawable(preloadAd?.icon?.drawable)
                }
            }
            if (preloadAd != null) {
                setNativeAd(preloadAd!!)
                showListener?.onShowed()
            }
        }
    }

    private fun Int.dpToPx(context: Context): Int {
        val scale = context.resources.displayMetrics.density
        return (this * scale + 0.5f).toInt()
    }
}
fun showAdNativePreloaded(
    ctx: Activity,
    nativeAdContainer: FrameLayout,
    nativeAdType: NativeAdType,
    nativeLayoutType: NativeLayoutType
): ShowAdNativePreload {
    return ShowAdNativePreload(ctx, nativeAdContainer, nativeAdType,nativeLayoutType)
}
