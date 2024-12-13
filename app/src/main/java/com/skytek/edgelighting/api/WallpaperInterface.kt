package com.skytek.edgelighting.api

//import com.skytek.edgelighting.modelclass.StaticCatResponse
import android.util.Log
import androidx.annotation.Keep
import com.skytek.edgelighting.model2.MagicalWallpapers
import com.skytek.edgelighting.model2.StaticWallCat
import com.skytek.edgelighting.models.Wallpapers
import okhttp3.*
import okhttp3.MediaType.Companion.toMediaType
import retrofit2.Call
import retrofit2.Retrofit
import retrofit2.converter.gson.GsonConverterFactory
import retrofit2.http.GET
import retrofit2.http.Url
import java.util.concurrent.TimeUnit

interface WallpaperInterface {
    @Keep
    @GET("Backgrounds.php")
    fun getWallpapers() : Call<Wallpapers>
    @Keep
    @GET("newt.php")
    fun getMagicalWallpapers() : Call<MagicalWallpapers>

    @Keep
    @GET("static_api_tt.php")
    fun getAllApiresponse() : Call<com.skytek.edgelighting.modelclass.Response>
    //   @GET("single_static_categ.php?id=27")
    //  fun getCategoryresponse(): Call<List<StaticCatResponse>>
    //complete url
    @GET
    @Keep
    fun categoryWallpapersLive(@Url url: String?): Call<ArrayList<StaticWallCat>>

    @Keep
    @GET("category_api_wall.php")
    fun getLiveAllApiresponse() : Call<com.skytek.edgelighting.modelclass.LiveModelClass.LiveResponsePrent>

    companion object{

        lateinit var okHttpClient:OkHttpClient
        lateinit var retrofitVariableHolder : Retrofit;

        @JvmStatic
        fun create(url:String): WallpaperInterface{
            try {
                okHttpClient =
                    OkHttpClient.Builder()
                        .addInterceptor(EmptyBodyInterceptor())
                        .readTimeout(60, TimeUnit.SECONDS)
                        .build()
            } catch (e: Exception) {}
            Log.d("checkwallpaperinterface" , "interface has following values 3$okHttpClient")
            if(okHttpClient != null)
            {
                val retrofit = Retrofit.Builder()
                    .addConverterFactory(GsonConverterFactory.create())
                    .baseUrl(url)
                    .client(okHttpClient)
                    .build()
                retrofitVariableHolder = retrofit
                Log.d("checkwallpaperinterface" , "interface has following values2 $retrofit")
            }
            Log.d("checkwallpaperinterface" , "interface has following values1 $retrofitVariableHolder")
            return retrofitVariableHolder.create(WallpaperInterface::class.java)
        }
    }

    class EmptyBodyInterceptor : Interceptor {
        var response: Response? = null
        private lateinit var emptyBody: ResponseBody

        override fun intercept(chain: Interceptor.Chain): Response {
            try {
                response = chain.proceed(chain.request())

                if (response != null && (response!!.isSuccessful.not() || response!!.code.let { it != 204 && it != 205 })) {
                    return response!!
                }

                if (response != null && (response!!.body?.contentLength() ?: -1) > 0) {
                    return response!!.newBuilder().code(200).build()
                }

                emptyBody = ResponseBody.create("application/json".toMediaType(), "{}")

            } catch (e: NoSuchElementException) {
                Log.d("wrrrrrrrrrrrrrrrrrrrrr", "intercept: ${e.message} ")
            }

            return response?.newBuilder()?.code(200)?.body(emptyBody)?.build() ?: throw IllegalStateException("Unexpected null response")
        }
    }

}

