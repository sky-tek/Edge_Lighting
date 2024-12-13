package com.skytek.edgelighting

import android.accessibilityservice.AccessibilityService
import android.content.Context
import android.content.Intent
import android.provider.Settings
import android.view.accessibility.AccessibilityEvent

class JustAccessibilityService : AccessibilityService() {

    override fun onAccessibilityEvent(event: AccessibilityEvent?) {
        // Implement your logic to handle accessibility events
    }

    override fun onInterrupt() {
        // Handle interruption of the service
    }

    companion object {
        fun openAccessibilitySettings(context: Context) {

            val intent = Intent(Settings.ACTION_ACCESSIBILITY_SETTINGS)
            context.startActivity(intent)
        }
    }
}

