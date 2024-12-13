package com.skytek.edgelighting.model2

import androidx.annotation.Keep
import com.google.gson.annotations.SerializedName

//@Keep
//data class MagicalWallpapers(
//    val response: List<Response>
//)

@Keep
class MagicalWallpapers(
    @SerializedName("response")
    val response: List<Response>
)