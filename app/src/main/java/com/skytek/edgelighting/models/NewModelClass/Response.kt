package com.skytek.edgelighting.models.NewModelClass

data class Response(
    val category: String,
    val viewType: String,
    val wallpapers: List<Wallpaper>
)