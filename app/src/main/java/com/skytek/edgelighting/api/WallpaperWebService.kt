package com.skytek.edgelighting.api

import com.skytek.edgelighting.model2.MagicalWallpapers
import com.skytek.edgelighting.models.Wallpapers
import retrofit2.Call
import retrofit2.Response
import retrofit2.http.GET

interface WallpaperWebService {

    @GET("Backgrounds.php")
    suspend fun getWallpapers() : Response<Wallpapers>

    @GET("newt.php")
    suspend fun getMagicalWallpapers() : Response<MagicalWallpapers>
}

