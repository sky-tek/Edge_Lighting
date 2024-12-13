package com.skytek.edgelighting.adapter

import android.content.Context
import androidx.fragment.app.Fragment
import androidx.fragment.app.FragmentManager
import androidx.fragment.app.FragmentPagerAdapter
import com.skytek.edgelighting.R

import com.skytek.edgelighting.fragment.WallpaperFragment

class LiveWallpaperPagerAdapter(val context: Context, fragmentManager: FragmentManager) : FragmentPagerAdapter(fragmentManager, BEHAVIOR_RESUME_ONLY_CURRENT_FRAGMENT) {
    override fun getItem(position: Int): Fragment {
        return when(position){
            0->{
                WallpaperFragment()
            }

            else -> {
                WallpaperFragment()
            }
        }
    }

    override fun getPageTitle(position: Int): CharSequence? {
        return when(position) {
            0->{
                context.getString(R.string.wallpaper).toUpperCase()
            }
            else -> { context.getString(R.string.magical_wallpaper).toUpperCase()
            }
        }
    }
    override fun getCount(): Int = 2
}

