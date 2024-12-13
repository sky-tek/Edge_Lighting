package com.skytek.edgelighting.repository

import com.skytek.edgelighting.api.WallpaperInterface
import com.skytek.edgelighting.modelclass.LiveModelClass.LiveResponsePrent
import com.skytek.edgelighting.modelclass.Response
import retrofit2.Call
import retrofit2.Callback

class WallpaperRepository1 {
    private val wallpaperService = WallpaperInterface.create("https://airnet-technologies.com/3d-live-wallpaper/apis/")

    fun fetchStaticWallpapers(onResult: (Response?) -> Unit) {
        val call = wallpaperService.getAllApiresponse()
        call?.enqueue(object : Callback<Response> {
            override fun onResponse(call: Call<Response>, response: retrofit2.Response<Response>) {
                if (response.isSuccessful) {
                    onResult(response.body())
                } else {
                    onResult(null)
                }
            }

            override fun onFailure(call: Call<Response>, t: Throwable) {
                onResult(null)
            }
        })
    }

    fun fetchLiveWallpapers(onResult: (LiveResponsePrent?) -> Unit) {
        val call = wallpaperService.getLiveAllApiresponse()
        call?.enqueue(object : Callback<LiveResponsePrent> {
            override fun onResponse(call: Call<LiveResponsePrent>, response: retrofit2.Response<LiveResponsePrent>) {
                if (response.isSuccessful) {
                    onResult(response.body())
                } else {
                    onResult(null)
                }
            }

            override fun onFailure(call: Call<LiveResponsePrent>, t: Throwable) {
                onResult(null)
            }
        })
    }
}