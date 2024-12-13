package com.skytek.edgelighting.utils;

import android.content.Context;
import android.content.SharedPreferences;

public class MySharePreferencesEdge {
    public static String BACKGROUND = "background";
    public static String BACKGROUNDCOLOR = "backgroundcolor";
    public static String BACKGROUNDLINK = "backgroundlink";
    public static String CHECKNOTCH = "checknotch";
    public static String COLOR1 = Const.COLOR1;
    public static String COLOR2 = Const.COLOR2;
    public static String COLOR3 = Const.COLOR3;
    public static String COLOR4 = Const.COLOR5;
    public static String COLOR5 = Const.COLOR6;
    public static String COLOR6 = Const.COLOR4;
    public static String ControlWindowManager = "ChangeWindowManager";
    public static String FINISH_BACKGROUND = "finish_background";
    public static String FINISH_BACKGROUNDCOLOR = "finish_backgroundcolor";
    public static String FINISH_BACKGROUNDLINK = "finish_backgroundlink";
    public static String FINISH_CHECKNOTCH = "finish_checknotch";
    public static String FINISH_COLOR1 = "finish_color1";
    public static String FINISH_COLOR2 = "finish_color2";
    public static String FINISH_COLOR3 = "finish_color3";
    public static String FINISH_COLOR4 = "finish_color5";
    public static String FINISH_COLOR5 = "finish_color6";
    public static String FINISH_COLOR6 = "finish_color4";

    public static String FINISH_NOTCHBOTTOM = "finish_notchbottom";
    public static String FINISH_NOTCHHEIGHT = "finish_notchheight";
    public static String FINISH_NOTCHRADIUSBOTTOM = "finish_notchradiusbottom";
    public static String FINISH_NOTCHRADIUSTOP = "finish_notchradiustop";
    public static String FINISH_NOTCHTOP = "finish_notchtop";
    public static String FINISH_RADIUSBOTTOM = "finish_bottom";
    public static String FINISH_RADIUSTOP = "finish_top";
    public static String FINISH_SHAPE = "finish_shape";
    public static String FINISH_SIZE = "finish_size";
    public static String FINISH_SPEED = "finish_speed";
    public static String HEIGHT = Const.HEIGHT;

    public static String ID_THEME = "id_theme";

    private static final String NEWS_ADS_PREFERENCES = "DUONGCV";
    public static String NOTCHBOTTOM = "notchbottom";
    public static String NOTCHHEIGHT = "notchheight";
    public static String NOTCHRADIUSBOTTOM = "notchradiusbottom";
    public static String NOTCHRADIUSTOP = "notchradiustop";
    public static String NOTCHTOP = "notchtop";
    public static String RADIUSBOTTOM = "bottom";
    public static String RADIUSTOP = "top";
    public static String SHAPE = "shape";
    public static String SIZE = "size";
    public static String SPEED = "speed";
    public static String TIME_COUNTER_RATE = "time_counter_rate";
    public static String WIDTH = Const.WIDTH;

    public static String ALWAYS_ON_DISPLAY = "always_on_display";
    public static String WALL_PAPER = "wall_paper";
    public static String DISPLAY_OVERLAY = "display_overLay";
    public static String SWITCH_CHARGING = "SWITCH_CHARGING";
    public static String SPINNER = "spinner";
    public static String SHOW_WHILE_CHARGING = "charging";

    public static String COLOR1_VALUE = "c1";
    public static String COLOR2_VALUE = "c2";
    public static String COLOR3_VALUE = "c3";
    public static String COLOR4_VALUE = "c4";
    public static String COLOR5_VALUE = "c5";
    public static String COLOR6_VALUE = "c6";
    public static String COLOR_WHILE_CHARGING = "color_charge";


    public static String ACCESSIBILITY_BROADCAST = "accessibility_broadcast";

    public static int getInt(String str, Context context) {
        return getIntValue(str, context);
    }

    public static void setInt(String str, int i, Context context) {
        putIntValue(str, i, context);
    }

    public static String getString(String str, Context context) {
        return getStringValue(str, context);
    }

    public static void setString(Context context, String str, String str2) {
        putStringValue(str, str2, context);
    }

    public static void putStringValue(String str, String str2, Context context) {
        SharedPreferences.Editor edit = context.getSharedPreferences(NEWS_ADS_PREFERENCES, 0).edit();
        edit.putString(str, str2);
        edit.apply();
    }

    public static void putBoolean(String str, boolean z, Context context) {
        SharedPreferences.Editor edit = context.getSharedPreferences(NEWS_ADS_PREFERENCES, 0).edit();
        edit.putBoolean(str, z);
        edit.apply();
    }

    public static void putIntValue(String str, int i, Context context) {
        SharedPreferences.Editor edit = context.getSharedPreferences(NEWS_ADS_PREFERENCES, 0).edit();
        edit.putInt(str, i);
        edit.apply();
    }

    public static boolean getBooleanValue(String str, Context context) {
        return context.getSharedPreferences(NEWS_ADS_PREFERENCES, 0).getBoolean(str, false);
    }

    public static String getStringValue(String str, Context context) {
        return context.getSharedPreferences(NEWS_ADS_PREFERENCES, 0).getString(str, null);
    }

    public static int getIntValue(String str, Context context) {
        return context.getSharedPreferences(NEWS_ADS_PREFERENCES, 0).getInt(str, -1);
    }

    public static void putWallpaperBooleanValue(String str, Boolean bool, Context context) {
        SharedPreferences.Editor edit = context.getSharedPreferences(NEWS_ADS_PREFERENCES, 0).edit();
        edit.putBoolean(str, bool);
        edit.apply();
    }

    public static boolean getWallpaperBooleanValue(String str, Context context) {
        return context.getSharedPreferences(NEWS_ADS_PREFERENCES, 0).getBoolean(str, false);
    }

    public static void putDisplayOverLayBooleanValue(String str, Boolean bool, Context context) {
        SharedPreferences.Editor edit = context.getSharedPreferences(NEWS_ADS_PREFERENCES, 0).edit();
        edit.putBoolean(str, bool);
        edit.apply();
    }

    public static boolean getDisplayOverLayBooleanValue(String str, Context context) {
        return context.getSharedPreferences(NEWS_ADS_PREFERENCES, 0).getBoolean(str, false);
    }

    public static void putAlwaysOnDisplayBooleanValue(String str, Boolean bool, Context context) {
        SharedPreferences.Editor edit = context.getSharedPreferences(NEWS_ADS_PREFERENCES, 0).edit();
        edit.putBoolean(str, bool);
        edit.apply();
    }

    public static boolean getAlwaysOnDisplayBooleanValue(String str, Context context) {
        return context.getSharedPreferences(NEWS_ADS_PREFERENCES, 0).getBoolean(str, false);
    }


    public static boolean getShowChargingBooleanValue(String str, Context context) {
        return context.getSharedPreferences(NEWS_ADS_PREFERENCES, 0).getBoolean(str, false);
    }



    public static int getSpinnerInt(String str, Context context) {
        return context.getSharedPreferences(NEWS_ADS_PREFERENCES, 0).getInt(str, 2);
    }

    public static void putChargedColorValue(String str, int str2, Context context) {
        SharedPreferences.Editor edit = context.getSharedPreferences(NEWS_ADS_PREFERENCES, 0).edit();
        edit.putInt(str,str2);
        edit.apply();
    }


    public static int getColor1Value(String str, Context context,int color) {
        return context.getSharedPreferences(NEWS_ADS_PREFERENCES, color).getInt(str, color);
    }

    public static int getColor2Value(String str, Context context,int color) {
        return context.getSharedPreferences(NEWS_ADS_PREFERENCES, color).getInt(str, color);
    }

    public static int getColor3Value(String str, Context context,int color) {
        return context.getSharedPreferences(NEWS_ADS_PREFERENCES, color).getInt(str, color);
    }

    public static int getColor4Value(String str, Context context,int color) {
        return context.getSharedPreferences(NEWS_ADS_PREFERENCES, color).getInt(str, color);
    }

    public static int getColor5Value(String str, Context context,int color) {
        return context.getSharedPreferences(NEWS_ADS_PREFERENCES, color).getInt(str, color);
    }


    public static int getColor6Value(String str, Context context,int color) {
        return context.getSharedPreferences(NEWS_ADS_PREFERENCES, color).getInt(str, color);
    }



    public static void setAccessibilityEnabled(String str, Boolean bool, Context context) {
        SharedPreferences.Editor edit = context.getSharedPreferences(NEWS_ADS_PREFERENCES, 0).edit();
        edit.putBoolean(str, bool);
        edit.apply();
    }

    public static boolean getAccessibilityEnabled(String str, Context context) {
        return context.getSharedPreferences(NEWS_ADS_PREFERENCES, 0).getBoolean(str, false);
    }

}









