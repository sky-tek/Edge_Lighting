package com.skytek.edgelighting.utils

import android.content.SharedPreferences


internal class Constants(val prefs: SharedPreferences) {

    fun get(key: String, default: Boolean): Boolean = prefs.getBoolean(key, default)
    fun get(key: String, default: String): String = prefs.getString(key, default) ?: default
    fun get(key: String, default: Int): Int = prefs.getInt(key, default)



    fun displayScale(): Float = (prefs.getInt("pref_aod_scale", 50) + 50) / 100F




    companion object {

        const val amoled = false
        const val amoledkey = "amoled"

        const val clock = true
        const val clockkey = "clock"

        var flag = 0
        const val flagkey = "flag"

        var flag2 = 0
        const val flag2key = "flag2"

        const val recycler_Panel = false
        const val recycler_Panelkey = "panel"


        var clockvlaue = false
        const val clockvlauekey = "clock"
        var backgroundvalue = false
        const val backgroundvaluekey = "clock"


        const val CLOCKS_PREFS = "clocks_prefs"

        const val valll = "valueee"
        const val xx =0.0
        const val yy =0.0


        var chkk = false
        var chkk2 = false
        var chkkey= "abcd"
        var chkkey2= "abcd"

        const val sizekey= "textsize"
        const val colorkey= "textcolor"
        const val fontkey= "textfont"
        const val datekey= "textdate"





        const val ssfchk = "first"
        const val show_ss = false

        const val firsttime2 = "time"




        const val DISPLAY_COLOR = "edge_colors"
        const val DISPLAY_COLOR_DEFAULT = -1

        const val SHOW_date = "ss_date"
        const val SHOW_day = "ss_day"
        const val SHOW_time = "ss_clock"
        const val SHOW_battery_icon = "ss_batteryIcn"
        const val SHOW_battery_percentage = "ss_battery"
        const val SHOW_battery_text = "ss_battery_text"
        const val SHOW_date_DEFAULT = true
        const val SHOW_day_DEFAULT = true
        const val SHOW_time_DEFAULT = true
        const val SHOW_battery_icon_DEFAULT = true
        const val SHOW_battery_percentage_DEFAULT = true
        const val SHOW_battery_text_DEFAULT = true
        const val DISPLAY_COLOR_time = "display_color_time"
        const val DISPLAY_COLOR_date = "display_color_date2"
        const val DISPLAY_COLOR_day = "display_color_day"
        const val DISPLAY_COLOR_battery = "display_color_battery2"
        const val DISPLAY_COLOR_time_DEFAULT = -1
        const val DISPLAY_COLOR_date_DEFAULT = -1
        const val DISPLAY_COLOR_day_DEFAULT = -1
        const val DISPLAY_COLOR_battery_DEFAULT = -1


        const val RULES_CHARGING_STATE = "rules_charging_state"
        const val RULES_BATTERY = "rules_battery_level"
        const val RULES_TIMEOUT = "rules_timeout_sec"

        const val ROOT_MODE = "root_mode"
        const val POWER_SAVING_MODE = "ao_power_saving"
        const val USER_THEME = "ao_style"
        const val SHOW_CLOCK = "ao_clock"
        const val SHOW_DATE = "ao_date"
        const val SHOW_BATTERY_ICON = "ao_batteryIcn"
        const val SHOW_BATTERY_PERCENTAGE = "ao_battery"
        const val SHOW_NOTIFICATION_COUNT = "ao_notifications"
        const val SHOW_NOTIFICATION_ICONS = "ao_notification_icons"
        const val SHOW_FINGERPRINT_ICON = "ao_fingerprint"
        const val FINGERPRINT_MARGIN = "ao_fingerprint_margin"
        const val BACKGROUND_IMAGE = "ao_background_image"
        const val EDGE_GLOW = "ao_edgeGlow"
        const val POCKET_MODE = "ao_pocket_mode"
        const val DO_NOT_DISTURB = "ao_dnd"
        const val DISABLE_HEADS_UP_NOTIFICATIONS = "heads_up"
        const val USE_12_HOUR_CLOCK = "hour"
        const val SHOW_AM_PM = "am_pm"
        const val DATE_FORMAT = "ao_date_format"
        const val FORCE_BRIGHTNESS = "ao_force_brightness"
        const val DISABLE_DOUBLE_TAP = "ao_double_tap_disabled"
        const val SHOW_MUSIC_CONTROLS = "ao_musicControls"
        const val MESSAGE = "ao_message"
        const val DISPLAY_COLOR_CLOCK = "display_color_clock"
        const val DISPLAY_COLOR_DATE = "display_color_date"
        const val DISPLAY_COLOR_BATTERY = "display_color_battery"
        const val DISPLAY_COLOR_MUSIC_CONTROLS = "display_color_music_controls"
        const val DISPLAY_COLOR_NOTIFICATION = "display_color_notification"
        const val DISPLAY_COLOR_MESSAGE = "display_color_message"
        const val DISPLAY_COLOR_FINGERPRINT = "display_color_fingerprint"
        const val DISPLAY_COLOR_EDGE_GLOW = "display_color_edge_glow"

        const val RULES_CHARGING_STATE_CHARGING = "charging"
        const val RULES_CHARGING_STATE_DISCHARGING = "discharging"

        const val USER_THEME_GOOGLE = "google"
        const val USER_THEME_ONEPLUS = "oneplus"
        const val USER_THEME_SAMSUNG = "samsung"
        const val USER_THEME_SAMSUNG2 = "samsung2"
        const val USER_THEME_SAMSUNG3 = "samsung3"
        const val USER_THEME_80S = "80s"
        const val USER_THEME_FAST = "fast"
        const val USER_THEME_FLOWER = "flower"
        const val USER_THEME_GAME = "game"
        const val USER_THEME_HANDWRITTEN = "handwritten"
        const val USER_THEME_JUNGLE = "jungle"
        const val USER_THEME_WESTERN = "western"
        const val USER_THEME_ANALOG = "analog"

        //        const val BACKGROUND_IMAGE_NONE = "none"
        const val BACKGROUND_IMAGE_NONE = "filip_baotic_1"
        const val BACKGROUND_2 = "daniel_olah_1"
        const val BACKGROUND_3 = "daniel_olah_2"
        const val BACKGROUND_4 = "daniel_olah_3"
        const val BACKGROUND_5 = "daniel_olah_4"
        const val BACKGROUND_6 = "daniel_olah_5"
        const val BACKGROUND_7 = "daniel_olah_6"
        const val BACKGROUND_8 = "daniel_olah_7"
        const val BACKGROUND_9 = "daniel_olah_8"
        const val BACKGROUND_1 = "filip_baotic_1"
        const val BACKGROUND_10 = "tyler_lastovich_1"
        const val BACKGROUND_11 = "tyler_lastovich_2"
        const val BACKGROUND_12 = "tyler_lastovich_3"
        const val BACKGROUND_13 = "tyler_lastovich_4"

        const val RULES_CHARGING_STATE_DEFAULT = "always"
        const val RULES_BATTERY_DEFAULT = 0
        const val RULES_TIMEOUT_DEFAULT = 0

        const val ROOT_MODE_DEFAULT = false
        const val POWER_SAVING_MODE_DEFAULT = false
        const val USER_THEME_DEFAULT = USER_THEME_GOOGLE
        const val SHOW_CLOCK_DEFAULT = true
        const val SHOW_DATE_DEFAULT = true
        const val SHOW_BATTERY_ICON_DEFAULT = true
        const val SHOW_BATTERY_PERCENTAGE_DEFAULT = true
        const val SHOW_NOTIFICATION_COUNT_DEFAULT = false
        const val SHOW_NOTIFICATION_ICONS_DEFAULT = true
        const val SHOW_FINGERPRINT_ICON_DEFAULT = false
        const val FINGERPRINT_MARGIN_DEFAULT = 200
        const val BACKGROUND_IMAGE_DEFAULT = BACKGROUND_IMAGE_NONE
        const val EDGE_GLOW_DEFAULT = false
        const val POCKET_MODE_DEFAULT = false
        const val DO_NOT_DISTURB_DEFAULT = false
        const val DISABLE_HEADS_UP_NOTIFICATIONS_DEFAULT = false
        const val USE_12_HOUR_CLOCK_DEFAULT = false
        const val SHOW_AM_PM_DEFAULT = false
        const val DATE_FORMAT_DEFAULT = "EEE, MMMM d"
        const val FORCE_BRIGHTNESS_DEFAULT = false
        const val DISABLE_DOUBLE_TAP_DEFAULT = false
        const val SHOW_MUSIC_CONTROLS_DEFAULT = false
        const val MESSAGE_DEFAULT = ""
        const val DISPLAY_COLOR_CLOCK_DEFAULT = -1
        const val DISPLAY_COLOR_DATE_DEFAULT = -1
        const val DISPLAY_COLOR_BATTERY_DEFAULT = -1
        const val DISPLAY_COLOR_MUSIC_CONTROLS_DEFAULT = -1
        const val DISPLAY_COLOR_NOTIFICATION_DEFAULT = -1
        const val DISPLAY_COLOR_MESSAGE_DEFAULT = -1
        const val DISPLAY_COLOR_FINGERPRINT_DEFAULT = -1
        const val DISPLAY_COLOR_EDGE_GLOW_DEFAULT = -1
    }
}