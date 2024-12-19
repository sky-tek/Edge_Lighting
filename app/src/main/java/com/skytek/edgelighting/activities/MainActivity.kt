package com.skytek.edgelighting.activities

import android.app.Activity
import android.content.ActivityNotFoundException
import android.content.Context
import android.content.Intent
import android.net.ConnectivityManager
import android.net.NetworkInfo
import android.net.Uri
import android.os.Bundle
import android.os.StrictMode
import android.os.StrictMode.ThreadPolicy
import android.os.StrictMode.VmPolicy
import android.util.DisplayMetrics
import android.util.Log
import android.view.MenuItem
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import androidx.activity.OnBackPressedCallback
import androidx.appcompat.app.ActionBarDrawerToggle
import androidx.appcompat.app.AppCompatActivity
import androidx.core.content.ContextCompat
import androidx.core.view.GravityCompat
import com.google.ads.mediation.admob.AdMobAdapter
import com.google.android.gms.ads.AdRequest
import com.google.android.gms.ads.AdSize
import com.google.android.gms.ads.AdView
import com.google.android.material.navigation.NavigationView
import com.google.android.play.core.review.ReviewManagerFactory
import com.google.firebase.ktx.Firebase

import com.mobi.pixels.adBannerOnDemand.loadOnDemandBannerAd
import com.mobi.pixels.adInterstitial.AdInterstitialLoadListeners
import com.mobi.pixels.adInterstitial.AdInterstitialShowListeners
import com.mobi.pixels.adInterstitial.Interstitial
import com.mobi.pixels.adNativeOnDemand.loadOnDemandNativeAd
import com.mobi.pixels.enums.BannerAdType
import com.mobi.pixels.enums.NativeAdIcon
import com.mobi.pixels.enums.NativeAdType
import com.mobi.pixels.enums.NativeLayoutType
import com.mobi.pixels.enums.ShimmerColor
import com.mobi.pixels.firebase.fireEvent
import com.mobi.pixels.isOnline
import com.skytek.edgelighting.App.Companion.openAd
import com.skytek.edgelighting.BuildConfig
import com.skytek.edgelighting.R
import com.skytek.edgelighting.activities.onboarding.OnBoardingLanguageScreen
import com.skytek.edgelighting.ads.IsShowingOpenAd.isinterstitialvisible
import com.skytek.edgelighting.databinding.ActivityMainBinding

import com.skytek.edgelighting.modelclass.Response
import com.skytek.edgelighting.utils.AdResources
import com.skytek.edgelighting.utils.AdResources.activitiesAdId
import com.skytek.edgelighting.utils.AdResources.bannerAdId
import com.skytek.edgelighting.utils.AdResources.bannerToNative
import com.skytek.edgelighting.utils.AdResources.clicks
import com.skytek.edgelighting.utils.AdResources.mainScreenAdShow
import com.skytek.edgelighting.utils.AdResources.nativeAdId
import com.skytek.edgelighting.utils.AdResources.wholeInterAdShow
import com.skytek.edgelighting.utils.AdResources.wholeScreenAdShow
import com.skytek.edgelighting.utils.Utills
import com.skytek.edgelighting.utils.adTimeTraker.getLastAdShownTime
import com.skytek.edgelighting.utils.adTimeTraker.isIntervalElapsed
import com.skytek.edgelighting.utils.adTimeTraker.updateLastAdShownTime
import com.skytek.edgelighting.utils.checkContext
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.GlobalScope
import kotlinx.coroutines.launch
import retrofit2.Call
import retrofit2.http.GET

class MainActivity : AppCompatActivity(), NavigationView.OnNavigationItemSelectedListener {


    lateinit var activity: Activity
    var binding: ActivityMainBinding? = null
    private var adView: AdView? = null
    private val onBackPressedCallback = object : OnBackPressedCallback(true) {
        override fun handleOnBackPressed() {
            Log.d("nvksdbsififebfiu", "handleOnBackPressed: ")

            if (binding!!.drawerLayout.isDrawerOpen(GravityCompat.START)) {
                binding!!.drawerLayout.closeDrawer(GravityCompat.START)
            } else {
                startActivity(Intent(this@MainActivity, ExitActivity::class.java))

            }
        }
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        binding = ActivityMainBinding.inflate(layoutInflater)
        if (BuildConfig.DEBUG) {
            StrictMode.setThreadPolicy(
                ThreadPolicy.Builder()
                    .detectDiskReads()
                    .detectDiskWrites()
                    .detectNetwork()
                    .penaltyLog()

                    .build()
            )
            StrictMode.setVmPolicy(
                VmPolicy.Builder()
                    .detectLeakedSqlLiteObjects()
                    .detectLeakedClosableObjects()
                    .penaltyLog()

                    .build()
            )
        }
        setContentView(binding!!.root)
        window.statusBarColor = ContextCompat.getColor(this, R.color.background)
        binding!!.navView.setNavigationItemSelectedListener(this)
        val toggle = ActionBarDrawerToggle(
            this, binding!!.drawerLayout, R.string.open_nav, R.string.close_nav
        )
        onBackPressedDispatcher.addCallback(this, onBackPressedCallback)
        binding!!.drawerLayout.addDrawerListener(toggle)
        toggle.syncState()
        activity = this@MainActivity
        binding!!.drawerBtn.setOnClickListener {
            binding!!.drawerLayout.openDrawer(GravityCompat.START)
        }/*Navigation drawer with transparent background*/
        binding!!.drawerLayout.setScrimColor(getResources().getColor(android.R.color.transparent))

        /*Remove navigation drawer shadow/fadding*/
        binding!!.drawerLayout.setDrawerElevation(0F)

        // Move this operation to a background thread using Coroutines
        GlobalScope.launch(Dispatchers.IO) {
            Utills.getHeightScreen(this@MainActivity)
        }
        openAd = true
        val packageInfo = packageManager.getPackageInfo(packageName, 0)
        val header = binding!!.navView.getHeaderView(0)
        val v = header.findViewById<TextView>(R.id.tv_version)
        v.text = "Version: ${packageInfo.versionName}"

        fireEvent("RV_${BuildConfig.VERSION_CODE}_Main_Activity ")
        Interstitial.load(this, activitiesAdId, object : AdInterstitialLoadListeners {


            override fun onFailedToLoad(error: String) {
                Log.d("adIsLoaded", "onFailedToLoad: ")
            }

            override fun onLoaded() {
                Log.d("adIsLoaded", "onLoaded: ")
            }

            override fun onPreviousAdLoading() {
                Log.d("adIsLoaded", "onPreviousAdLoading: ")
            }
        })
        binding!!.edgeOverlay.setOnClickListener {
            clicks++
            fireEvent("RV_${BuildConfig.VERSION_CODE}_Main_Activity_edge_click ")

            Log.d(
                "hfasagifegifgijfvgikefvgif",
                "${isOnline(this@MainActivity) && wholeScreenAdShow && wholeInterAdShow && (isIntervalElapsed() || clicks>= AdResources.ElBtnClickCount)}"
            )
            Log.d(
                "hfasagifegifgijfvgikefvgif", "${getLastAdShownTime()} && ${isIntervalElapsed()}"
            )
            if (isOnline(this@MainActivity) && wholeScreenAdShow && wholeInterAdShow && (isIntervalElapsed() || clicks>= AdResources.ElBtnClickCount)) {
                val i = Intent(
                    this@MainActivity, EdgeOverlaySettingsActivity::class.java
                )
                loadInterstitialAd(i, "edge")
            } else {
                val i = Intent(
                    this@MainActivity, EdgeOverlaySettingsActivity::class.java
                )
                startActivity(i)
            }

//            val bundle = Bundle()
//            bundle.putString("edge_lighting_btn_clicked", "1")
//            firebaseAnalytics.logEvent("edge_lighting_event", bundle)

        }
//        getNavigationBarHeight(this@MainActivity)
//        setBottomMargin(binding!!.adViewContainer, getNavigationBarHeight(this@MainActivity))
        if (checkInternetConbection(this@MainActivity) && wholeScreenAdShow && mainScreenAdShow && bannerToNative) {

            loadOnDemandBannerAd(
                this, binding!!.adViewContainer, bannerAdId, BannerAdType.Banner

            ).enableShimmerEffect(true).load()

        } else {
            if (mainScreenAdShow && wholeScreenAdShow) {
                loadOnDemandNativeAd(
                    this@MainActivity,
                    binding!!.adViewContainer,
                    nativeAdId,
                    NativeAdType.NativeSmall,
                    NativeLayoutType.Layout2
                ).setBackgroundColor("#61C6A2FF").setTextColorButton("#ffffff")

                    .setButtonColor("#FF5589F1").setButtonRoundness(15)
                    .setAdIcon(NativeAdIcon.White).enableShimmerEffect(true)
                    .setShimmerBackgroundColor("#000000").setShimmerColor(ShimmerColor.White).load()
            }
        }




        binding!!.liveWallpaper.setOnClickListener {
            clicks++
            fireEvent("RV_${BuildConfig.VERSION_CODE}_Main_Activity_LiveWall_click ")

            Log.d(
                "hfasagifegifgijfvgikefvgif",
                "${isOnline(this@MainActivity) && wholeScreenAdShow && wholeInterAdShow && (isIntervalElapsed() || clicks>= AdResources.ElBtnClickCount)}"
            )

            if (isOnline(this@MainActivity) && wholeScreenAdShow && wholeInterAdShow && (isIntervalElapsed() || clicks>= AdResources.ElBtnClickCount)) {
                val i = Intent(this@MainActivity, StaticWallpaperActivity::class.java)
                loadInterstitialAd(i, "live")
            } else {
                val i = Intent(this@MainActivity, StaticWallpaperActivity::class.java)
                startActivity(i)
            }


        }


    }

    private fun loadInterstitialAd(i: Intent, s: String) {

        Log.d(
            "hfasagifegifgijfvgikefvgif", "loadInterstitialAd"
        )
        if (checkContext(this)) {
            startActivity(
                i
            )
            Interstitial.show(this, object : AdInterstitialShowListeners {
                override fun onDismissed() {
                    isinterstitialvisible = false
                    Log.d(
                        "hfasagifegifgijfvgikefvgif", "onDismissed"
                    )
                    clicks=0
                    updateLastAdShownTime()
                }

                override fun onError(error: String) {
                    Log.d(
                        "hfasagifegifgijfvgikefvgif", "onError"
                    )
                    startActivity(
                        i
                    )
                    Interstitial.load(
                        this@MainActivity,
                        activitiesAdId,
                    )
                }

                override fun onShowed() {
                    isinterstitialvisible = true
                    Log.d(
                        "hfasagifegifgijfvgikefvgif", "onShowed"
                    )

                    Interstitial.load(
                        this@MainActivity,
                        activitiesAdId,
                    )




                }
            })
        } else {
            startActivity(i)
        }


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

    fun checkInternetConbection(context: Context): Boolean {
        try {
            val connec = context.getSystemService(CONNECTIVITY_SERVICE) as ConnectivityManager
            if (connec.getNetworkInfo(0)!!.state == NetworkInfo.State.CONNECTED || connec.getNetworkInfo(
                    0
                )!!.state == NetworkInfo.State.CONNECTING || connec.getNetworkInfo(1)!!.state == NetworkInfo.State.CONNECTING || connec.getNetworkInfo(
                    1
                )!!.state == NetworkInfo.State.CONNECTED
            ) {
                return true
            } else if (connec.getNetworkInfo(0)!!.state == NetworkInfo.State.DISCONNECTED || connec.getNetworkInfo(
                    1
                )!!.state == NetworkInfo.State.DISCONNECTED
            ) {
                return false
            }
        } catch (e: Exception) {
            e.printStackTrace()
        }
        return false
    }


    override fun onDestroy() {
        super.onDestroy()
        binding = null
    }

    fun showCollapsibleBannerAd() {
        adView = AdView(this@MainActivity)
        val extras = Bundle()
        extras.putString("collapsible", "bottom")
        getAdSize0().let { adView!!.setAdSize(it) }
        adView!!.adUnitId = resources.getString(R.string.collapisble_banner_ad)
        val adRequest =
            AdRequest.Builder().addNetworkExtrasBundle(AdMobAdapter::class.java, extras).build()
        adView!!.loadAd(adRequest)
        if (binding?.adViewContainer != null) {
            binding?.adViewContainer?.addView(adView)
        } else {
            Log.d("checkCollapseAds", "no ad found")
        }
    }

    private fun getAdSize0(): AdSize {
        // Step 2 - Determine the screen width (less decorations) to use for the ad width.
        val display = windowManager.defaultDisplay
        val outMetrics = DisplayMetrics()
        display.getMetrics(outMetrics)
        val widthPixels = outMetrics.widthPixels.toFloat()
        val density = outMetrics.density
        val adWidth = (widthPixels / density).toInt()
        // Step 3 - Get adaptive ad size and return for setting on the ad view.
        return AdSize.getCurrentOrientationAnchoredAdaptiveBannerAdSize(this, adWidth)
        //return AdSize.getPortraitAnchoredAdaptiveBannerAdSize(this,adWidth);
        //return AdSize.getLandscapeAnchoredAdaptiveBannerAdSize(this,adWidth);
    }

    private fun showRateApp() {
        try {
            val manager = ReviewManagerFactory.create(this)
            val request = manager.requestReviewFlow()
            request.addOnCompleteListener { task ->
                if (task.isSuccessful) {
                    val reviewInfo = task.result
                    val flow = manager.launchReviewFlow(this@MainActivity, reviewInfo)
                    flow.addOnCompleteListener { reviewFlowTask ->
                        if (reviewFlowTask.isSuccessful) {

                            // Review flow completed successfully
                            Log.d("valueOfTask", "showRateApp: Review flow completed successfully")
                        } else {
                            // Review flow encountered an error
                            Log.e(
                                "valueOfTask",
                                "showRateApp: Error launching review flow",
                                reviewFlowTask.exception
                            )
                        }
                    }
                } else {
                    // Review flow request encountered an error
                    Log.e(
                        "valueOfTask", "showRateApp: Error requesting review flow", task.exception
                    )
                }
            }
        } catch (e: Exception) {
            // Handle any exceptions
            e.printStackTrace()
        }
        onUserLeaveHint()
    }

    private fun privacyPolicy() {
        val url = "https://airnetinformation.blogspot.com/2022/04/Edgelighting.html"
        val i = Intent(Intent.ACTION_VIEW, Uri.parse(url))
        i.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK)
        i.setPackage("com.android.chrome")
        try {
            startActivity(i)
        } catch (e: ActivityNotFoundException) {
            i.setPackage(null)
            startActivity(i)
        }
    }

    private fun moreApp() {
        val intent =
            Intent(Intent.ACTION_VIEW).setData(Uri.parse("https://play.google.com/store/apps/dev?id=5960812647982088893"))
        try {
            startActivity(
                Intent(intent).setPackage("com.wonderapps.translator")
            )
        } catch (exception: ActivityNotFoundException) {
            startActivity(intent)
        }
    }

    override fun onNavigationItemSelected(item: MenuItem): Boolean {
        when (item.itemId) {
            R.id.nav_language -> {
                Log.d("menubarClicked", "onNavigationItemSelected: language clicked")
                startActivity(Intent(this@MainActivity, OnBoardingLanguageScreen::class.java))
                binding!!.drawerLayout.closeDrawer(GravityCompat.START)
            }

            R.id.nav_privacy -> {
                privacyPolicy()
                binding!!.drawerLayout.closeDrawer(GravityCompat.START)
                Log.d("menubarClicked", "onNavigationItemSelected: privacy clicked")
            }

            R.id.nav_rate -> {
                binding!!.drawerLayout.closeDrawer(GravityCompat.START)
                Log.d("menubarClicked", "onNavigationItemSelected: rate clicked")

                // Intent to open the Play Store link
                val intent = Intent(Intent.ACTION_VIEW).apply {
                    data = Uri.parse("https://play.google.com/store/apps/details?id=com.skytek.edgelighting&pcampaignid=web_share")
                }
                // Check if there is an app to handle the intent
                if (intent.resolveActivity(this.packageManager) != null) {
                    startActivity(intent)
                } else {
                    Log.e("RateApp", "No app can handle this intent.")
                }
            }


            R.id.nav_more_app -> {
                moreApp()
                binding!!.drawerLayout.closeDrawer(GravityCompat.START)
                Log.d("menubarClicked", "onNavigationItemSelected: more clicked")
            }

            R.id.nav_exit -> {
                startActivity(Intent(this, ExitActivity::class.java))
                binding!!.drawerLayout.closeDrawer(GravityCompat.START)
                Log.d("menubarClicked", "onNavigationItemSelected: exit clicked")
            }

            else -> {
                Log.d("menubarClicked", "onNavigationItemSelected: nothing clicked")
            }
        }
        return true
    }

}

interface AllDataOfApi {
    @GET("3d-live-wallpaper/apis/static_api_tt.php")
    fun getCategory(): Call<Response>
}


