package com.thecode.onboardingviewagerexamples.adapters

import android.content.Context
import androidx.fragment.app.Fragment
import androidx.fragment.app.FragmentActivity
import androidx.viewpager2.adapter.FragmentStateAdapter
import com.skytek.edgelighting.R
import com.thecode.onboardingviewagerexamples.fragments.OnboardingFragment4


class OnboardingViewPagerAdapter4(
    fragmentActivity: FragmentActivity,
    private val context: Context
) :
    FragmentStateAdapter(fragmentActivity) {

    override fun createFragment(position: Int): Fragment {
        return when (position) {
            0 -> OnboardingFragment4.newInstance(
                context.resources.getString(R.string.title_onboarding_1),
                context.resources.getString(R.string.string_text_onboarding_complete_description),
                R.drawable.permissionhere
            )
            1 -> OnboardingFragment4.newInstance(
                context.resources.getString(R.string.title_onboarding_2),
                context.resources.getString(R.string.description_onboarding_2),
                R.drawable.servicelevels
            )
            else -> OnboardingFragment4.newInstance(
                context.resources.getString(R.string.title_onboarding_2),
                context.resources.getString(R.string.description_onboarding_2),
                R.raw.lottie_developer
            )

        }
    }

    override fun getItemCount(): Int {
        return 2
    }
}