package com.skytek.edgelighting.utils

import com.google.android.gms.ads.nativead.NativeAd

object AdResources {
    var splashScreenAdId:String="ca-app-pub-2011235963710101/3094932004"
    var openAppAdId:String="ca-app-pub-2011235963710101/3758020874"
    var activitiesAdId:String="ca-app-pub-2011235963710101/3191756428"
    var nativeAdId:String="ca-app-pub-2011235963710101/6789462608"
    var bannerAdId:String="ca-app-pub-2011235963710101/9757164774"
    var splashBannerAdId:String="ca-app-pub-2011235963710101/5091209105"
    var permissionOnBoardingAdId:String="ca-app-pub-2011235963710101/7069623306"
    var INTROOnBoardingAdId:String="ca-app-pub-2011235963710101/8586455779"
    var InAppOnBoardingAdId:String="ca-app-pub-2011235963710101/2069459948"
    var splashScreenAdShow:Boolean=true
var ads_clicks_on_inter=0
//    var liveWallpaperScreenAdShow:Boolean=true
//    var settingScreenAdShow:Boolean=true
var preloadedAd: NativeAd? = null // Store the single preloaded ad
    var preloadedAd2: NativeAd? = null // Store the single preloaded ad
    var settingScreenNativeAdShow:Boolean=true
    var splashBannerAdShow:Boolean=true
    var mainScreenAdShow:Boolean=true
    var wholeScreenAdShow:Boolean=true
    var wholeInterAdShow:Boolean=true
    var bannerToNative:Boolean=true
    var NativeToBanner:Boolean=true
    var onBoardingNativeAd:Boolean=true
    var areNativeAdsEnabled:Boolean=true
    var openAppAdShow:Boolean=true
    var permissionOnboardingAdShow:Boolean=true
    var wallpaperOnboardingAdShow:Boolean=true
    var onboardingShow:Boolean=true
    var inAppOnboardingShow:Boolean=true
var clicks=0
    var version = ""
    var ElBtnClickCount=1
    var ElWallpaperTimeCount=45000

}