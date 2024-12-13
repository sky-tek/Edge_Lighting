package com.skytek.edgelighting.activities.onboarding.fragments

import android.content.Intent
import android.graphics.Paint
import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import androidx.core.content.ContextCompat
import androidx.fragment.app.Fragment
import androidx.navigation.fragment.findNavController
import com.skytek.edgelighting.R
import com.skytek.edgelighting.activities.EdgeOverlaySettingsActivity
import com.skytek.edgelighting.activities.MainActivity
import com.skytek.edgelighting.databinding.FragmentEdgeBinding
import com.skytek.edgelighting.utils.Const
import com.skytek.edgelighting.utils.adTimeTraker.updateLastAdShownTime


class EdgeFragment : Fragment() {
    private var _binding: FragmentEdgeBinding? = null
    private val binding get() = _binding!!

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?
    ): View {
        _binding = FragmentEdgeBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        // Underline and set color for buttons
//        underlineAndSetColor(binding.tvHomeBtn)
        underlineAndSetColor(binding.tryMore, R.color.color_orange)

        // Set click listeners for edge overlay containers
        setContainerClickListeners()

        // Set click listeners for other buttons
        binding.tvHomeBtn.setOnClickListener {
            updateLastAdShownTime()
            navigateToActivity(MainActivity::class.java)
        }
        binding.tryMore.setOnClickListener {
            navigateToActivity(
                EdgeOverlaySettingsActivity::class.java, "onBoardingScreen" to true
            )
        }
        binding.elNext.setOnClickListener { findNavController().navigate(R.id.wallpaperFragment) }
    }

    /**
     * Underlines the text and sets the color (if provided).
     */
    private fun underlineAndSetColor(textView: TextView, colorResId: Int? = null) {
        textView.apply {
            paintFlags = paintFlags or Paint.UNDERLINE_TEXT_FLAG
            colorResId?.let {
                setTextColor(ContextCompat.getColor(requireContext(), it))
            }
        }
    }

    /**
     * Sets click listeners for all edge overlay containers.
     */
    private fun setContainerClickListeners() {
        val containerMap = mapOf(
            binding.el1Container to Const.SNOW,
            binding.el2Container to Const.CHRISMISTREE,
            binding.el3Container to Const.MOON,
            binding.el4Container to Const.SUN,
            binding.el5Container to Const.HEART,
            binding.el6Container to Const.STAR
        )
        containerMap.forEach { (t, u) ->
            Log.d("45646456", "$t:$u ")
        }

        containerMap.forEach { (container, borderType) ->
            container.setOnClickListener {
                navigateToActivity(
                    EdgeOverlaySettingsActivity::class.java,
                    "onBoardingScreen" to true,
                    "borderType" to borderType
                )
            }
        }
    }

    /**
     * Navigates to the specified activity with optional extras.
     */
    private fun navigateToActivity(activityClass: Class<*>, vararg extras: Pair<String, Any>) {
        Log.d("45646456", "${activityClass.simpleName} ")
        extras.forEach { (key, value) ->
            Log.d("45646456", "$key ; $value")
        }
        Intent(requireActivity(), activityClass).apply {
            extras.forEach { (key, value) ->
                when (value) {
                    is Boolean -> putExtra(key, value)
                    is String -> putExtra(key, value)
                }
            }
            startActivity(this)
            requireActivity().finish()
        }
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }
}
