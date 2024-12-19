package com.skytek.edgelighting.activities.onboarding

import android.os.Bundle
import android.view.View
import androidx.appcompat.app.AppCompatActivity
import com.mobi.pixels.adBannerOnDemand.loadOnDemandBannerAd
import com.mobi.pixels.adNativeOnDemand.AdNativeOnDemandListeners
import com.mobi.pixels.adNativeOnDemand.loadOnDemandNativeAd
import com.mobi.pixels.enums.BannerAdType
import com.mobi.pixels.enums.NativeAdIcon
import com.mobi.pixels.enums.NativeAdType
import com.mobi.pixels.enums.NativeLayoutType
import com.mobi.pixels.enums.ShimmerColor
import com.mobi.pixels.firebase.fireEvent
import com.skytek.edgelighting.BuildConfig
import com.skytek.edgelighting.activities.EdgeOverlaySettingsActivity
import com.skytek.edgelighting.databinding.ActivityEdgeAndWallpaperOnboardingBinding
import com.skytek.edgelighting.utils.AdResources.INTROOnBoardingAdId
import com.skytek.edgelighting.utils.AdResources.NativeToBanner
import com.skytek.edgelighting.utils.AdResources.bannerAdId
import com.skytek.edgelighting.utils.AdResources.wallpaperOnboardingAdShow
import com.skytek.edgelighting.utils.AdResources.wholeScreenAdShow

class EdgeAndWallpaperOnboarding : AppCompatActivity() {
    private var _binding: ActivityEdgeAndWallpaperOnboardingBinding? = null
    private val binding get() = _binding!!
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        _binding = ActivityEdgeAndWallpaperOnboardingBinding.inflate(layoutInflater)
        setContentView(binding.root)
        fireEvent("RV_${BuildConfig.VERSION_CODE}_EDGE_AND_WallPaper_Boarding")
        if (wholeScreenAdShow && wallpaperOnboardingAdShow) {
            if (NativeToBanner) {
                loadOnDemandNativeAd(
                    this, binding.adContainer, INTROOnBoardingAdId, NativeAdType.NativeSmall,NativeLayoutType.Layout2
                ).setBackgroundColor("#1C1C1C").setTextColorButton("#000000")
                    .setTextColorTitle("#ffffff").setTextColorDescription("#ffffff")
                    .setButtonColor("#FFBE00").setButtonRoundness(15).setAdIcon(NativeAdIcon.White)
                    .enableShimmerEffect(true).setShimmerBackgroundColor("#000000")
                    .setShimmerColor(ShimmerColor.White)
                .load()
            } else {
                loadOnDemandBannerAd(
                    this, binding.adContainer, bannerAdId, BannerAdType.Banner
                ).enableShimmerEffect(true).load()
            }
        }


    }
}