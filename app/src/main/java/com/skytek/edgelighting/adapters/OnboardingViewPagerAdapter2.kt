package com.thecode.onboardingviewagerexamples.adapters

import android.content.Context
import androidx.fragment.app.Fragment
import androidx.fragment.app.FragmentActivity
import androidx.viewpager2.adapter.FragmentStateAdapter
import com.skytek.edgelighting.R

import com.thecode.onboardingviewagerexamples.fragments.OnboardingFragment2


class OnboardingViewPagerAdapter2(
    fragmentActivity: FragmentActivity,
    private val context: Context
) :
    FragmentStateAdapter(fragmentActivity) {

    override fun createFragment(position: Int): Fragment {
        return when (position) {
            0 -> OnboardingFragment2.newInstance(
                context.resources.getString(R.string.title_onboarding_1),
                context.resources.getString(R.string.description_onboarding_1),
                R.raw.lottie_delivery_boy_bumpy_ride
            )
            1 -> OnboardingFragment2.newInstance(
                context.resources.getString(R.string.title_onboarding_2),
                context.resources.getString(R.string.description_onboarding_2),
                R.raw.lottie_developer
            )
            else -> OnboardingFragment2.newInstance(
                context.resources.getString(R.string.title_onboarding_3),
                context.resources.getString(R.string.description_onboarding_3),
                R.raw.lottie_girl_with_a_notebook
            )
        }
    }

    override fun getItemCount(): Int {
        return 3
    }
}