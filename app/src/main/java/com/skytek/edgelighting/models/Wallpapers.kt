package com.skytek.edgelighting.models

import androidx.annotation.Keep
import com.google.gson.annotations.SerializedName

//@Keep
//data class Wallpapers(
//    val response: List<Response>
//)

@Keep
class Wallpapers{

    @SerializedName("response")
    val response = listOf<Response>()
}

