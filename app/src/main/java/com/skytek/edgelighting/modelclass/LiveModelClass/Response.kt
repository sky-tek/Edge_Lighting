package com.skytek.edgelighting.modelclass.LiveModelClass

import androidx.annotation.Keep

@Keep
data class Response(
    val category: String,
    val viewType: String,
    val wallpapers: List<Wallpaper>
)