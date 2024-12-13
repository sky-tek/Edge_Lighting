package com.skytek.edgelighting.api

import com.google.gson.GsonBuilder
import okhttp3.OkHttpClient
import retrofit2.Retrofit
import retrofit2.converter.gson.GsonConverterFactory
import java.util.concurrent.TimeUnit

object StaticApiClient {
    //http://192.168.10.22:8080/local-server/
    //https://airnet-technologies.com/3d-live-wallpaper/apis/static_api_tt.php
    var BASE_URL = "https://airnet-technologies.com/"
    var gson = GsonBuilder()
        .setLenient()
        .create()
    private var retrofit: Retrofit? = null
    fun get():Retrofit? {
        val client = OkHttpClient.Builder()
            .readTimeout(60, TimeUnit.SECONDS).build()
        if (retrofit == null) {
            retrofit = Retrofit.Builder()
                .baseUrl(BASE_URL)
                .client(client)
                .addConverterFactory(GsonConverterFactory.create(gson))
                .build()
        }
        return retrofit
    }
}