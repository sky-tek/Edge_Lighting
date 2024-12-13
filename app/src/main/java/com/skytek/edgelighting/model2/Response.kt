package com.skytek.edgelighting.model2

import androidx.annotation.Keep
import com.google.gson.annotations.SerializedName

//@Keep
//data class Response(
//    val Category: String,
//    val id: String,
//    val wallpapers: List<Wallpaper>
//)

@Keep
class Response(
    @SerializedName("Category")
    val Category: String,
    @SerializedName("id")
    val id: String,
    @SerializedName("wallpapers")
    val wallpapers: List<Wallpaper>
)