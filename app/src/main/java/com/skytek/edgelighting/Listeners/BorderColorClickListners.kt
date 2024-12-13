package com.skytek.edgelighting.Listeners

import android.annotation.SuppressLint
import android.content.Context
import android.content.Intent
import android.content.SharedPreferences
import android.graphics.Color
import android.graphics.drawable.ColorDrawable
import android.util.Log
import android.view.View
import androidx.core.content.edit
import com.skytek.WindowService.Companion.timerReceiver
import com.skytek.edgelighting.MyAccessibilityService
import com.skytek.edgelighting.R
import com.skytek.edgelighting.activities.EdgeOverlaySettingsActivity
import com.skytek.edgelighting.activities.EdgeOverlaySettingsActivity.Companion.activity
import com.skytek.edgelighting.activities.EdgeOverlaySettingsActivity.Companion.binding
import com.skytek.edgelighting.utils.Const
import com.skytek.edgelighting.utils.MySharePreferencesEdge
import com.skytek.edgelighting.views.AmbilWarnaDialog
import com.skytek.edgelighting.views.CircleImageView


class BorderColorClickListners : View.OnClickListener {



    @SuppressLint("UseCompatLoadingForDrawables")
    override fun onClick(view: View?) {
        Log.d("abcd", view!!.id.toString())
        val binding = binding ?: return
        Log.d("ssjhdskjdshkjd", "onClick: ")
        when (view.id) {
            R.id.image_color1 -> handleColorImageClick(binding.imageColor1, 0)
            R.id.image_color2 -> handleColorImageClick(binding.imageColor2, 1)
            R.id.image_color3 -> handleColorImageClick(binding.imageColor3, 2)
            R.id.image_color4 -> handleColorImageClick(binding.imageColor4, 3)
            R.id.image_color5 -> handleColorImageClick(binding.imageColor5, 4)
            R.id.image_color6 -> handleColorImageClick(binding.imageColor6, 5)

        }
    }
    private fun handleColorImageClick(imageView: CircleImageView, colorIndex: Int) {
        val MainActivity = activity ?: return

        if (imageView.foreground != null) {
            clearImageView(imageView, colorIndex)
        } else {
            showColorPickerDialog(imageView, colorIndex)
        }
    }

    private fun clearImageView(imageView: CircleImageView, colorIndex: Int) {
        val MainActivity = activity ?: return

        val sharedPref = MainActivity.getSharedPreferences("mySharedPreferences", Context.MODE_PRIVATE)

        sharedPref?.edit {
            when (colorIndex) {
                0 -> putBoolean(MySharePreferencesEdge.COLOR1_VALUE, false)
                1 -> putBoolean(MySharePreferencesEdge.COLOR2_VALUE, false)
                2 -> putBoolean(MySharePreferencesEdge.COLOR3_VALUE, false)
                3 -> putBoolean(MySharePreferencesEdge.COLOR4_VALUE, false)
                4 -> putBoolean(MySharePreferencesEdge.COLOR5_VALUE, false)
                5 -> putBoolean(MySharePreferencesEdge.COLOR6_VALUE, false)
            }
            apply()
        }

        imageView.setBackgroundColor(Color.GRAY)
        imageView.foreground = null
        checkAllImageViews()

        if (allOtherImageViewsAreEmpty(imageView)) {

                        Log.d("gfgdfgdfgdf", "onClick: 3")
                        EdgeOverlaySettingsActivity.color[0] = Color.RED
                        EdgeOverlaySettingsActivity.color[1] =
                            MainActivity!!.resources.getColor(R.color.color_EB11DA)
                        EdgeOverlaySettingsActivity.color[2] = Color.BLUE
                        EdgeOverlaySettingsActivity.color[3] = Color.RED
                        EdgeOverlaySettingsActivity.color[4] =
                            MainActivity.resources.getColor(R.color.color_EB11DA)
                        EdgeOverlaySettingsActivity.color[5] = Color.BLUE
                        EdgeOverlaySettingsActivity.color[6] = Color.TRANSPARENT
                        val MainActivity = activity

                        MySharePreferencesEdge.setString(
                            MainActivity, MySharePreferencesEdge.COLOR1, "${Color.RED}"
                        )
                        MySharePreferencesEdge.setString(
                            MainActivity,
                            MySharePreferencesEdge.COLOR2,
                            "${MainActivity!!.resources.getColor(R.color.color_EB11DA)}"
                        )
                        MySharePreferencesEdge.setString(
                            MainActivity, MySharePreferencesEdge.COLOR3, "${Color.BLUE}"
                        )
                        MySharePreferencesEdge.setString(
                            MainActivity, MySharePreferencesEdge.COLOR4, "${Color.RED}"
                        )
                        MySharePreferencesEdge.setString(
                            MainActivity,
                            MySharePreferencesEdge.COLOR5,
                            "${MainActivity.resources.getColor(R.color.color_EB11DA)}"
                        )
                        MySharePreferencesEdge.setString(
                            MainActivity, MySharePreferencesEdge.COLOR6, "${Color.BLUE}"
                        )
                        binding?.imageColor1?.setBackgroundColor(Color.RED)
                        binding?.imageColor2?.setBackgroundColor(MainActivity.resources.getColor(R.color.color_EB11DA))
                        binding?.imageColor3?.setBackgroundColor(Color.BLUE)
                        binding?.imageColor4?.setBackgroundColor(Color.GRAY)
                        binding?.imageColor1?.foreground =
                            activity!!.resources.getDrawable(R.drawable.group_445)
                        binding?.imageColor2?.foreground =
                            activity!!.resources.getDrawable(R.drawable.group_445)
                        binding?.imageColor3?.foreground =
                            activity!!.resources.getDrawable(R.drawable.group_445)
                        val sharedPref = MainActivity.getSharedPreferences(
                            "mySharedPreferences", Context.MODE_PRIVATE
                        )
                        with(sharedPref?.edit()) {
                            this?.putBoolean(MySharePreferencesEdge.COLOR1_VALUE, true)
                            this?.putBoolean(MySharePreferencesEdge.COLOR2_VALUE, true)
                            this?.putBoolean(MySharePreferencesEdge.COLOR3_VALUE, true)
                            this?.putBoolean(MySharePreferencesEdge.COLOR4_VALUE, false)
                            this?.putBoolean(MySharePreferencesEdge.COLOR5_VALUE, false)
                            this?.putBoolean(MySharePreferencesEdge.COLOR6_VALUE, false)
                            this?.apply()
                        }



        }


            updateEdgeLightingColor()
            updateWindowManagerColor()
        
    }

    private fun showColorPickerDialog(imageView: CircleImageView, colorIndex: Int) {
        val MainActivity = activity ?: return

        EdgeOverlaySettingsActivity.checkColor = Const.COLOR1
        val initialColor = EdgeOverlaySettingsActivity.color[colorIndex]

        AmbilWarnaDialog(
            MainActivity,
            initialColor,
            true,
            object : AmbilWarnaDialog.OnAmbilWarnaListener {
                override fun onCancel(dialog: AmbilWarnaDialog) {}

                override fun onOk(dialog: AmbilWarnaDialog, color: Int) {
                    val sharedPref = MainActivity.getSharedPreferences("mySharedPreferences", Context.MODE_PRIVATE)
                    val colorState1Value = sharedPref.getBoolean(MySharePreferencesEdge.COLOR1_VALUE, true)
                    val colorState2Value = sharedPref.getBoolean(MySharePreferencesEdge.COLOR2_VALUE, true)
                    val colorState3Value = sharedPref.getBoolean(MySharePreferencesEdge.COLOR3_VALUE, true)
                    val colorState4Value = sharedPref.getBoolean(MySharePreferencesEdge.COLOR4_VALUE, true)
                    val colorState5Value = sharedPref.getBoolean(MySharePreferencesEdge.COLOR5_VALUE, true)
                    val colorState6Value = sharedPref.getBoolean(MySharePreferencesEdge.COLOR6_VALUE, true)

                    if (!colorState1Value && !colorState2Value && !colorState3Value && colorState4Value && colorState5Value && colorState6Value) {
                        EdgeOverlaySettingsActivity.color.fill(color)
                        MySharePreferencesEdge.setString(
                            MainActivity, MySharePreferencesEdge.COLOR1, "$color"
                        )
                        MySharePreferencesEdge.setString(
                            MainActivity, MySharePreferencesEdge.COLOR2, "$color"
                        )
                        MySharePreferencesEdge.setString(
                            MainActivity, MySharePreferencesEdge.COLOR3, "$color"
                        )


                        MySharePreferencesEdge.setString(
                            MainActivity, MySharePreferencesEdge.COLOR4, "$color"
                        )
                        MySharePreferencesEdge.setString(
                            MainActivity, MySharePreferencesEdge.COLOR5, "$color"
                        )
                        MySharePreferencesEdge.setString(
                            MainActivity, MySharePreferencesEdge.COLOR6, "$color"
                        )

                    }
                    sharedPref?.edit {
                        when (colorIndex) {
                            0 -> {
                                putBoolean(MySharePreferencesEdge.COLOR1_VALUE, true)
                                MySharePreferencesEdge.putIntValue(MySharePreferencesEdge.COLOR1_VALUE, color, MainActivity)
                            }
                            1 -> {
                                putBoolean(MySharePreferencesEdge.COLOR2_VALUE, true)
                                MySharePreferencesEdge.putIntValue(MySharePreferencesEdge.COLOR2_VALUE, color, MainActivity)
                            }
                            2 -> {
                                putBoolean(MySharePreferencesEdge.COLOR3_VALUE, true)
                                MySharePreferencesEdge.putIntValue(MySharePreferencesEdge.COLOR3_VALUE, color, MainActivity)
                            }
                            3 -> {
                                putBoolean(MySharePreferencesEdge.COLOR4_VALUE, true)
                                MySharePreferencesEdge.putIntValue(MySharePreferencesEdge.COLOR4_VALUE, color, MainActivity)
                            }
                            4 -> {
                                putBoolean(MySharePreferencesEdge.COLOR5_VALUE, true)
                                MySharePreferencesEdge.putIntValue(MySharePreferencesEdge.COLOR5_VALUE, color, MainActivity)
                            }
                            5 -> {
                                putBoolean(MySharePreferencesEdge.COLOR6_VALUE, true)
                                MySharePreferencesEdge.putIntValue(MySharePreferencesEdge.COLOR6_VALUE, color, MainActivity)
                            }
                        }
                        apply()
                    }
                    imageView.foreground = activity!!.resources.getDrawable(R.drawable.group_445)

                    imageView.setBackgroundColor(color)


                        updateEdgeLightingColor()
                        updateWindowManagerColor()


                    checkAllImageViews()
                }
            }).show()
    }

    private fun allOtherImageViewsAreEmpty(clickedImageView: CircleImageView): Boolean {
        val imageViews = listOf(
            binding!!.imageColor1,
            binding!!.imageColor2,
            binding!!.imageColor3,
            binding!!.imageColor4,
            binding!!.imageColor5,
            binding!!.imageColor6
        )

        return imageViews.filter { it != clickedImageView }.all { it.foreground == null }
    }

    private fun updateEdgeLightingColor() {
        if (MyAccessibilityService.edgeLightingView != null &&
            MySharePreferencesEdge.getAccessibilityEnabled(MySharePreferencesEdge.ACCESSIBILITY_BROADCAST, activity)
        ) {
            MyAccessibilityService.edgeLightingView!!.changeColor(EdgeOverlaySettingsActivity.color)
        } else {
            binding?.edgeLightView?.changeColor(EdgeOverlaySettingsActivity.color)
        }
    }

    private fun updateWindowManagerColor() {
        val intent = Intent(Const.Action_ChangeWindownManager)
        intent.putExtra(Const.CONTROLWINDOW, Const.COLOR)
        intent.setPackage(activity!!.packageName)
        activity!!.sendBroadcast(intent)
    }

// Define other helper functions here0


    private fun  setColorsAndPreferences(
        color1: Int, color2: Int, color3: Int, color4: Int, color5: Int, color6: Int
    ) {
        val MainActivity = activity
        EdgeOverlaySettingsActivity.color[0] = color1
        EdgeOverlaySettingsActivity.color[1] = color2
        EdgeOverlaySettingsActivity.color[2] = color3
        EdgeOverlaySettingsActivity.color[3] = color4
        EdgeOverlaySettingsActivity.color[4] = color5
        EdgeOverlaySettingsActivity.color[5] = color6
        EdgeOverlaySettingsActivity.color[6] = color6 // Assuming this is intentional

        val colorStrings = listOf(
            MySharePreferencesEdge.COLOR1,
            MySharePreferencesEdge.COLOR2,
            MySharePreferencesEdge.COLOR3,
            MySharePreferencesEdge.COLOR4,
            MySharePreferencesEdge.COLOR5,
            MySharePreferencesEdge.COLOR6
        )

        val colors = listOf(color1, color2, color3, color4, color5, color6)

        colorStrings.forEachIndexed { index, str ->
            MySharePreferencesEdge.setString(MainActivity, str, "${colors[index]}")
            Log.d("abcd","${colors[index]}")
        }
    }
//    fun saveData(context: Context, key: String, value: String) {
//        val sharedPref: SharedPreferences = context.getSharedPreferences("mySharedPreferencesRememberColors", Context.MODE_PRIVATE)
//        val editor: SharedPreferences.Editor = sharedPref.edit()
//        editor.putString(key, value)
//        editor.apply()
//    }
fun checkAllImageViews() {
    val mainActivity = activity ?: return
    val binding = binding ?: return

    val imageViews = listOf(
        binding.imageColor1,
        binding.imageColor2,
        binding.imageColor3,
        binding.imageColor4,
        binding.imageColor5,
        binding.imageColor6
    )

    val selectedColors = imageViews
        .filter { it.foreground != null }
        .map { (it.background as? ColorDrawable)?.color !!}


    if (selectedColors.isEmpty()) {

        return
    }

    when (selectedColors.size) {

        1 -> setColorsAndPreferences(selectedColors[0], selectedColors[0], selectedColors[0], selectedColors[0], selectedColors[0], selectedColors[0])
        2 -> setColorsAndPreferences(selectedColors[0], selectedColors[0], selectedColors[0], selectedColors[1], selectedColors[1], selectedColors[1])
        3 -> setColorsAndPreferences(selectedColors[0], selectedColors[0], selectedColors[1], selectedColors[1], selectedColors[2], selectedColors[2])
        4 -> setColorsAndPreferences(selectedColors[0], selectedColors[1], selectedColors[2], selectedColors[3], selectedColors[1], selectedColors[2])
        5 -> setColorsAndPreferences(selectedColors[0], selectedColors[0], selectedColors[1], selectedColors[2], selectedColors[3], selectedColors[4])
        6 -> setColorsAndPreferences(selectedColors[0], selectedColors[1], selectedColors[2], selectedColors[3], selectedColors[4], selectedColors[5])
        else -> {

        }
    }
}


    companion object {

        fun convertIntToString(i: Int): String {
            return if (i == -1) {
                "#00000000" // Return black color if -1
            } else {
                val colorHex = String.format(
                    "%06X", 16777215 and i
                ) // Get hexadecimal representation without leading '#'
                "#$colorHex" // Add leading '#' and return
            }
        }


    }

}

