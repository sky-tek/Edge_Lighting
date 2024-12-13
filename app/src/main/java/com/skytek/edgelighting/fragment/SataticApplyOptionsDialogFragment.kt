package com.skytek.edgelighting.fragment

import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import com.skytek.edgelighting.R


class SataticApplyOptionsDialogFragment : Fragment() {



        override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View? {
            return inflater.inflate(R.layout.bottom_sheet_dialog_option_layout, container, false)
        }

        override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
            super.onViewCreated(view, savedInstanceState)

            // Implement logic for applying wallpaper to home screen, lock screen, or both
            // Add onClickListeners to the buttons inside the bottom sheet dialog layout
            // For example:
            /*
            val applyToHomeButton = view.findViewById<View>(R.id.applyToHomeButton)
            applyToHomeButton.setOnClickListener {
                // Apply wallpaper to home screen
            }
            */
        }

}