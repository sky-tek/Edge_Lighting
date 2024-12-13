package com.skytek.edgelighting.viewmodel

import androidx.lifecycle.ViewModel
import com.skytek.edgelighting.api.WallpaperInterface
import com.skytek.edgelighting.modelclass.LiveModelClass.LiveResponsePrent
import com.skytek.edgelighting.modelclass.Response
import com.skytek.edgelighting.repository.WallpaperRepository1
import retrofit2.Call
import retrofit2.Callback

class WallpaperViewModel1 : ViewModel() {
    private val repository = WallpaperRepository1()

    fun fetchStaticWallpapers(onResult: (Response?) -> Unit) {
        repository.fetchStaticWallpapers(onResult)
    }

    fun fetchLiveWallpapers(onResult: (LiveResponsePrent?) -> Unit) {
        repository.fetchLiveWallpapers(onResult)
    }
}


