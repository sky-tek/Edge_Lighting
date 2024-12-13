package com.skytek.edgelighting.adapter
import android.app.AlertDialog
import android.app.Dialog
import android.content.DialogInterface
import android.os.Bundle
import androidx.fragment.app.DialogFragment

class WallpaperOptionsDialogClass : DialogFragment() {

    override fun onCreateDialog(savedInstanceState: Bundle?): Dialog {
        val options = arrayOf("Apply to Homescreen", "Lockscreen", "Both")
        val builder = AlertDialog.Builder(activity)
        builder.setTitle("Choose Wallpaper Options")
            .setItems(options) { _: DialogInterface?, which: Int ->
                when (which) {
                    0 -> {
                        // Apply to Homescreen selected
                        // Implement your logic here
                    }
                    1 -> {
                        // Lockscreen selected
                        // Implement your logic here
                    }
                    2 -> {
                        // Both selected
                        // Implement your logic here
                    }
                }
            }
        return builder.create()
    }
}
