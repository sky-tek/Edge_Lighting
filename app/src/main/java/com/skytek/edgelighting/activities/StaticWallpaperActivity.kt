package com.skytek.edgelighting.activities


import android.app.Activity
import android.app.WallpaperManager
import android.content.ActivityNotFoundException
import android.content.ComponentName
import android.content.Context
import android.content.Intent
import android.os.Bundle
import android.util.Log
import android.widget.TextView
import androidx.activity.result.ActivityResultLauncher
import androidx.activity.result.contract.ActivityResultContracts
import androidx.appcompat.app.AppCompatActivity
import androidx.core.app.ActivityCompat
import androidx.core.content.ContextCompat
import androidx.fragment.app.Fragment
import androidx.viewpager.widget.ViewPager
import com.google.android.material.tabs.TabLayout
import com.mobi.pixels.firebase.fireEvent
import com.skytek.edgelighting.BuildConfig
import com.skytek.edgelighting.R
import com.skytek.edgelighting.adapter.ViewPagerAdapter
import com.skytek.edgelighting.databinding.ActivityStaticWallpaperBinding
import com.skytek.edgelighting.fragment.LiveWallpaperFragment
import com.skytek.edgelighting.fragment.StaticWallpaperFragment
import com.skytek.edgelighting.service.GLWallpaperService


class StaticWallpaperActivity : AppCompatActivity(), LiveWallpaperFragment.FragmentCallback {
    private lateinit var binding: ActivityStaticWallpaperBinding

    //    private lateinit var viewPager: ViewPager
    private lateinit var viewPager: ViewPager
    private lateinit var tabLayout: TabLayout
    private var mProgressIsShowing = false
    private lateinit var liveWallpaperLauncher: ActivityResultLauncher<Intent>

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        Log.d("dskjdskdshds", "onCreate: 1")
        Log.d("abcdddd", "onCreated of static activity : ")

        binding = ActivityStaticWallpaperBinding.inflate(layoutInflater)
        setContentView(binding.root)

        liveWallpaperLauncher = registerForActivityResult(
            ActivityResultContracts.StartActivityForResult()
        ) { result ->
            handleActivityResult(result.resultCode, result.data)
        }
        // Change the status bar color
        window.statusBarColor = ContextCompat.getColor(this, R.color.background)

        viewPager = findViewById(R.id.viewPager)
        tabLayout = findViewById(R.id.tabLayout)

        val packageInfo = packageManager.getPackageInfo(packageName, 0)
        fireEvent("RV_${BuildConfig.VERSION_CODE}_Wallpaper_Activity ")
        val adapter = ViewPagerAdapter(supportFragmentManager)
        adapter.addFragment(StaticWallpaperFragment(), getString(R.string.wallpapers))
        adapter.addFragment(LiveWallpaperFragment(), getString(R.string.magical_wallpaper))

        viewPager.adapter = adapter
        viewPager.offscreenPageLimit = 1
        tabLayout.setupWithViewPager(viewPager)


// Set the custom tab indicator
        // Set the custom tab indicator color
        tabLayout.setSelectedTabIndicatorColor(resources.getColor(R.color.white))

        // Set custom tab layout
        for (i in 0 until tabLayout.tabCount) {
            val tab = tabLayout.getTabAt(i)
            tab?.customView = layoutInflater.inflate(R.layout.custom_tab_layout, null)
            // Update the text for each tab based on your adapter's titles
            tab?.customView?.findViewById<TextView>(R.id.tabText)?.text = adapter.getPageTitle(i)

            // Set default background resource for the first tab (you can adjust this based on your requirement)
            if (i == 0) {
                tab?.view?.setBackgroundResource(R.drawable.selected_tab)
            }
        }


        // Adding tab selection listener to update tab backgrounds
        tabLayout.addOnTabSelectedListener(object : TabLayout.OnTabSelectedListener {
            override fun onTabSelected(tab: TabLayout.Tab) {
                tab.view.setBackgroundResource(R.drawable.selected_tab)
            }

            override fun onTabUnselected(tab: TabLayout.Tab) {
                tab.view.setBackgroundResource(R.drawable.unselected_tab)
            }

            override fun onTabReselected(tab: TabLayout.Tab) {
                // Optional: Handle tab reselection if needed
            }
        })

    }

    override fun onBackPressed() {
        Log.d(
            "fishuiofhiwegficvue",
            "onBackPressed:${intent.getBooleanExtra("onBoardingScreen", false)} "
        )
        if (intent.getBooleanExtra("onBoardingScreen", false)) {
            startActivity(Intent(this, MainActivity::class.java))
            finish()

        } else {
            super.onBackPressed()
        }
    }

    private fun handleActivityResult(resultCode: Int, data: Intent?) {
        Log.d("asdfdasf12332", "resultCode == RESULT_OK:${resultCode == RESULT_OK} ")
        Log.d("asdfdasf12332", "oldID: $oldID")
        Log.d("asdfdasf12332", "newID: $newID")

        if (resultCode == RESULT_OK) {
            oldID = newID
            Log.d("asdfdasf", "onActivityResult sending id to gl: $newID")
            val sharedPreferences = getSharedPreferences("MyPreferences", Context.MODE_PRIVATE)
            sharedPreferences.edit()
                .putString("wallpaperPath", oldID)
                .apply()
        } else if (resultCode == RESULT_CANCELED) {
            Log.d("djhfdkhfdkjhf", "onActivityResult: result canceled")
            currentId = oldID
            Log.d("fdsfsd", "stati148 ${oldID.toString()}")
        }
    }



    private fun loadFragment(fragment: Fragment) {
        supportFragmentManager.beginTransaction().apply {
            replace(R.id.viewPager, fragment)
            commit()
        }
    }


    override fun onNameAdded(wallpaperPath: Boolean, outputFile: String) {
        if (!wallpaperPath) {
            Log.d("abcd4455454", "onBindViewHolder: $outputFile")
            val sharedPreferences = this.getSharedPreferences("MyPreferences", Context.MODE_PRIVATE)
            oldID = sharedPreferences.getString("wallpaperPath", null)
            Log.d("fdsfsd", "17999 $oldID")
            currentId = outputFile
            newID = currentId
            Log.d("fdsfsd", "182 $newID")
        }

        try {
            Log.d("callbackTAG", "onNameAdded call back: $outputFile")
            Log.d("callbackTAG", "onNameAdded call back: $wallpaperPath")

            val intent = Intent(WallpaperManager.ACTION_CHANGE_LIVE_WALLPAPER).apply {
                putExtra(
                    WallpaperManager.EXTRA_LIVE_WALLPAPER_COMPONENT,
                    ComponentName(this@StaticWallpaperActivity, GLWallpaperService::class.java)
                )
            }

            Log.d("checkwallpaerscond", "before starting activity launcher")
            liveWallpaperLauncher.launch(intent)

        } catch (e: ActivityNotFoundException) {
            Log.e("Error", "Live wallpaper picker not found", e)
        }
    }



}
















