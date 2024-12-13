package com.skytek.edgelighting.models

import androidx.annotation.Keep
import com.google.gson.annotations.SerializedName

//@Keep
//data class Response(
//    val Category: String,
//    val Wallpapers: List<Wallpaper>,
//    val id: String
//)

@Keep
class Response{

    @SerializedName("Category")
    val Category: String? = null

    @SerializedName("Wallpapers")
    val Wallpapers: List<Wallpaper>? = null

    @SerializedName("id")
    val id: String? = null

}
