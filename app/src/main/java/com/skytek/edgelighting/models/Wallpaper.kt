package com.skytek.edgelighting.models

import androidx.annotation.Keep
import com.google.gson.annotations.SerializedName

//@Keep
//data class Wallpaper(
//    val Background: String,
//    val Thumbnail: String,
//    val id: String
//)

@Keep
class Wallpaper(

    @SerializedName("Background")
    val Background: String,

    @SerializedName("Thumbnail")
    val Thumbnail: String,

    @SerializedName("id")
    val id: String
)
