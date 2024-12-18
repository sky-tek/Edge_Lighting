package com.skytek.edgelighting.activities.onboarding

import android.content.Context
import android.content.Intent
import android.os.Bundle
import android.util.Log
import android.view.View
import androidx.appcompat.app.AppCompatActivity
import androidx.appcompat.app.AppCompatDelegate
import androidx.core.os.LocaleListCompat
import androidx.lifecycle.lifecycleScope
import androidx.recyclerview.widget.LinearLayoutManager
import com.mobi.pixels.adNativeOnDemand.loadOnDemandNativeAd
import com.mobi.pixels.enums.NativeAdIcon
import com.mobi.pixels.enums.NativeAdType
import com.mobi.pixels.enums.NativeLayoutType
import com.mobi.pixels.enums.ShimmerColor
import com.mobi.pixels.firebase.fireEvent
import com.skytek.edgelighting.R
import com.skytek.edgelighting.activities.MainActivity
import com.skytek.edgelighting.activities.RequestPermissionActivity
import com.skytek.edgelighting.adapter.InAppLanOneTimeRvAdapter
import com.skytek.edgelighting.databinding.ActivityOnBoardingLanguageScreenBinding
import com.skytek.edgelighting.utils.AdResources
import com.skytek.edgelighting.utils.AdResources.InAppOnBoardingAdId
import com.skytek.edgelighting.utils.AdResources.nativeAdId
import com.skytek.edgelighting.utils.AdResources.onboardingShow
import com.skytek.edgelighting.utils.ConversationLanguage
import com.skytek.edgelighting.utils.getLanguages_for_translate_fragment
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext

class OnBoardingLanguageScreen : AppCompatActivity() {
    private lateinit var languageList: ArrayList<ConversationLanguage>
    var selected = false
    var lanName = ""
    var lanCode = ""
    var lanFlag = -1
    val binding: ActivityOnBoardingLanguageScreenBinding by lazy {
        ActivityOnBoardingLanguageScreenBinding.inflate(layoutInflater)
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(binding.root)
        val packageInfo = packageManager?.getPackageInfo(packageName, 0)
        fireEvent("RV_${packageInfo?.versionCode}_App_Language_change_Screen_Opened")

        Log.d("yh3yr8gfedhfvbjds vbufy", "${intent.getBooleanExtra("onBoardingScreen", false)}: ")
        if (AdResources.wholeScreenAdShow && AdResources.inAppOnboardingShow) {
            if (intent.getBooleanExtra("onBoardingScreen", false)) {
                loadOnDemandNativeAd(
                    this,
                    binding.adViewContainer,
                    InAppOnBoardingAdId,
                    NativeAdType.NativeSmall,
                    NativeLayoutType.Layout2
                ).setBackgroundColor(resources.getString(R.color.round_background))
                    .setTextColorButton("#ffffff").setTextColorTitle("#ffffff")
                    .setTextColorDescription("#ffffff")

                    .setButtonColor("#0071EC").setButtonRoundness(15).setAdIcon(NativeAdIcon.White)
                    .enableShimmerEffect(true).setShimmerBackgroundColor("#000000")
                    .setShimmerColor(ShimmerColor.White).load()
            } else {
                loadOnDemandNativeAd(
                    this,
                    binding.adViewContainer,
                    nativeAdId,
                    NativeAdType.NativeSmall,
                    NativeLayoutType.Layout2
                ).setBackgroundColor(resources.getString(R.color.round_background))
                    .setTextColorButton("#ffffff").setTextColorTitle("#ffffff")
                    .setTextColorDescription("#ffffff")

                    .setButtonColor("#0071EC").setButtonRoundness(15).setAdIcon(NativeAdIcon.White)
                    .enableShimmerEffect(true).setShimmerBackgroundColor("#000000")
                    .setShimmerColor(ShimmerColor.White).load()
            }
        }


        languageList = ArrayList()
        getLanguages_for_translate_fragment(languageList)
        val recyclerView = binding.languagesRecyclerView
        recyclerView.layoutManager = LinearLayoutManager(this)
        recyclerView.setHasFixedSize(true)
        val languagePreferences = getSharedPreferences(LANGUAGE_PREFERENCE_KEY, MODE_PRIVATE)
        val languageToFind =
            languagePreferences.getString(LANGUAGE_PREFERENCE_NAME_KEY, "English")!!
        Log.d("whatIsLAnguange", "onCreate:$languageToFind ")
        val index = getLanguageIndex(languageList, languageToFind)
        val adapter = InAppLanOneTimeRvAdapter(
            this, languageList, index

        ) { click, lanName, lanFlag, lanCode ->
            if (click) {
                selected = true
                this.lanCode = lanCode
                this.lanFlag = lanFlag
                this.lanName = lanName
                binding.dialogSearchBtn.visibility = View.VISIBLE
            }
        }

        recyclerView.scrollToPosition(index)
//        adapter.setScrollPosition(scrollPosition)

        recyclerView.adapter = adapter
        binding.dialogSearchBtn.setOnClickListener {
            markOnboardingAsCompleted()
            /*    setLanguage()*/
            if (selected) {
                val languagePreferences =
                    getSharedPreferences(
                        OnBoardingLanguageScreen.LANGUAGE_PREFERENCE_KEY,
                        AppCompatActivity.MODE_PRIVATE
                    )
                val languagePreferenceEditor = languagePreferences.edit()

                languagePreferenceEditor.putString(
                    OnBoardingLanguageScreen.LANGUAGE_PREFERENCE_VALUE_KEY,
                    lanCode
                )
                Log.d("whatIsLAnguange", "onBindViewHolder: ${lanCode}")
                languagePreferenceEditor.putString(
                    OnBoardingLanguageScreen.LANGUAGE_PREFERENCE_NAME_KEY,
                    lanName
                )
                languagePreferenceEditor.putInt(
                    "LANGUAGE_PREFERENCE_Flag_KEY",
                    lanFlag
                )
                languagePreferenceEditor.apply()
                if (intent.getBooleanExtra("onboardinglan", false)) {

                    startActivity(
                        Intent(
                            this@OnBoardingLanguageScreen, RequestPermissionActivity::class.java
                        )
                    )
                    finish()

                } else {
                    startActivity(Intent(this@OnBoardingLanguageScreen, MainActivity::class.java))
                    finish()
                }

                setLanguage()
                val languageSelectDialog =
                    getSharedPreferences("languageSelectDialog", Context.MODE_PRIVATE)
                val editor = languageSelectDialog.edit().apply {
                    putBoolean("languageSelectDialog", true)
                    apply()
                }
            } else {
                if (intent.getBooleanExtra("onboardinglan", false)) {

                    startActivity(
                        Intent(
                            this@OnBoardingLanguageScreen, RequestPermissionActivity::class.java
                        )
                    )
                    finish()

                } else {
                    startActivity(Intent(this@OnBoardingLanguageScreen, MainActivity::class.java))
                    finish()
                }
                val languageSelectDialog =
                    getSharedPreferences("languageSelectDialog", Context.MODE_PRIVATE)
                val editor = languageSelectDialog.edit().apply {
                    putBoolean("languageSelectDialog", true)
                    apply()
                }
            }


        }
    }

    private fun markOnboardingAsCompleted() {

        Log.d("onboardingShowkjnodesoivbivbu", "markOnboardingAsCompleted:$onboardingShow ")
        val sharedPreferences = getSharedPreferences("onboardinglan", MODE_PRIVATE)
        val editor = sharedPreferences.edit()
        editor.putBoolean("isFirstTimelan", false)
        editor.apply()
    }

    override fun onBackPressed() {
        markOnboardingAsCompleted()
        Log.d(
            "intentValuefshgdfu",
            "onBackPressed: ${intent.getBooleanExtra("onBoardingScreen", false)}"
        )
        if (intent.getBooleanExtra("onboardinglan", false)) {

            startActivity(Intent(this, RequestPermissionActivity::class.java))
            finish()

        } else {
            super.onBackPressed()
        }

    }

    fun getLanguageIndex(
        conversationLanguageList: ArrayList<ConversationLanguage>, languageName: String
    ): Int {
        for (i in conversationLanguageList.indices) {
            if (conversationLanguageList[i].languageName == languageName) {
                return i
            }
        }
        return -1
    }


    private fun setLanguage() {
        try {
//            val languagePreferences = getSharedPreferences(LANGUAGE_PREFERENCE_KEY, MODE_PRIVATE)
//            val languageCode = languagePreferences.getString(LANGUAGE_PREFERENCE_VALUE_KEY, "en")
            Log.d("nekdjhbfiicngib8     8", "setLanguage:${lanCode} ")
            lifecycleScope.launch {
                withContext(Dispatchers.IO) {
                    val appLocale = LocaleListCompat.forLanguageTags(lanCode)
                    withContext(Dispatchers.Main) {
                        AppCompatDelegate.setApplicationLocales(appLocale)
                    }
                }
            }
        } catch (e: Exception) {
            Log.e("setLanguage", e.message.toString())
        }
    }

//    override fun onDestroy() {
//        super.onDestroy()
//        Log.d("whatIsLAnguange", "onDestroy:$selected ")
//        if (!selected) {
//            val languagePreferences = getSharedPreferences(
//                LANGUAGE_PREFERENCE_KEY, MODE_PRIVATE
//            )
//            val languagePreferenceEditor = languagePreferences.edit()
//
//            languagePreferenceEditor.putString(
//                LANGUAGE_PREFERENCE_VALUE_KEY, languageList[12].languageCode
//            )
//            languagePreferenceEditor.putString(
//                LANGUAGE_PREFERENCE_NAME_KEY, languageList[12].languageName
//            )
//            languagePreferenceEditor.putInt(
//                "LANGUAGE_PREFERENCE_Flag_KEY", languageList[12].languageFlag
//            )
//            languagePreferenceEditor.apply()
//            val languageSelectDialog =
//                getSharedPreferences("languageSelectDialog", Context.MODE_PRIVATE)
//            val editor = languageSelectDialog.edit().apply {
//                putBoolean("languageSelectDialog", true)
//                apply()
//            }
//        }
//    }

    companion object {
        const val LANGUAGE_PREFERENCE_KEY = "APP_LANGUAGE_PREFERENCE"
        const val LANGUAGE_PREFERENCE_VALUE_KEY = "LANGUAGE_PREFERENCE_VALUE"
        const val LANGUAGE_PREFERENCE_NAME_KEY = "LANGUAGE_PREFERENCE_NAME"

    }
}