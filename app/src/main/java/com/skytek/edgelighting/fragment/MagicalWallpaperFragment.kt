package com.skytek.edgelighting.fragment

import android.app.Activity
import android.app.ProgressDialog
import android.app.WallpaperManager
import android.content.ActivityNotFoundException
import android.content.ComponentName
import android.content.Context
import android.content.Intent
import android.graphics.Bitmap
import android.os.AsyncTask
import android.os.Bundle
import android.os.CountDownTimer
import android.text.Spannable
import android.text.SpannableString
import android.text.style.ForegroundColorSpan
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import android.widget.Toast
import androidx.activity.result.contract.ActivityResultContracts
import androidx.appcompat.app.AlertDialog
import androidx.core.content.ContextCompat
import androidx.fragment.app.Fragment
import androidx.preference.PreferenceManager
import androidx.recyclerview.widget.LinearLayoutManager
import com.android.volley.Request
import com.android.volley.RequestQueue
import com.android.volley.Response
import com.android.volley.VolleyError
import com.android.volley.toolbox.JsonArrayRequest
import com.android.volley.toolbox.Volley
import com.google.android.gms.ads.AdError
import com.google.android.gms.ads.AdRequest
import com.google.android.gms.ads.FullScreenContentCallback
import com.google.android.gms.ads.LoadAdError
import com.google.android.gms.ads.interstitial.InterstitialAd
import com.google.android.gms.ads.interstitial.InterstitialAdLoadCallback
import com.google.firebase.analytics.ktx.analytics
import com.google.firebase.ktx.Firebase
import com.mobi.pixels.adInterstitial.AdInterstitialShowListeners
import com.mobi.pixels.adInterstitial.Interstitial
import com.mobi.pixels.isOnline
import com.skytek.edgelighting.App

import com.skytek.edgelighting.R
import com.skytek.edgelighting.activities.LiveWallpaperActivity
import com.skytek.edgelighting.activities.LiveWallpaperActivity.Companion.wallpaperClicked
import com.skytek.edgelighting.adapter.WallpaperChildAdapter
import com.skytek.edgelighting.adapter.WallpaperParentAdapter

import com.skytek.edgelighting.api.WallpaperInterface
import com.skytek.edgelighting.api.WallpaperWebService
import com.skytek.edgelighting.databinding.FragmentMagicalWallpaperBinding

import com.skytek.edgelighting.model2.MagicalWallpapers
import com.skytek.edgelighting.models.WallpaperChildItem
import com.skytek.edgelighting.models.WallpaperParentItem
import com.skytek.edgelighting.repository.WallpaperRepository
import com.skytek.edgelighting.service.GLWallpaperService
import com.skytek.edgelighting.viewmodel.WallpaperViewModel
import org.json.JSONArray
import org.json.JSONException
import retrofit2.Call
import retrofit2.Callback
import java.io.*
import java.net.ConnectException
import java.net.ProtocolException
import java.net.SocketException
import java.net.URL
import java.util.concurrent.Executors
import javax.net.ssl.SSLException
import javax.net.ssl.SSLHandshakeException

class MagicalWallpaperFragment : Fragment(){

    var notchBottom = 0
    var notchTop = 0
    var wallpaperWebService: WallpaperWebService? = null
    var alertDialog: AlertDialog? = null
    var wallpaperRepository: WallpaperRepository? = null
    lateinit var wallpaperViewModel: WallpaperViewModel
    var binding: FragmentMagicalWallpaperBinding? = null
    var pDialog: ProgressDialog? = null
    var checkForAdLiveWallpaper = 0


    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

            pDialog = null
    }

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View? {
        binding = FragmentMagicalWallpaperBinding.inflate(layoutInflater)
        return binding!!.root
    }

    override fun onActivityCreated(savedInstanceState: Bundle?) {
        super.onActivityCreated(savedInstanceState)

        if(isOnline(requireContext())){

            binding!!.progressBar.visibility = View.VISIBLE

            var call = WallpaperInterface.create("https://mobipixels.net/3d-Live-wallpapers-api/").getMagicalWallpapers()
            call.enqueue(object : Callback<MagicalWallpapers>,OnItemClickListner2 {
                override fun onResponse(call: Call<MagicalWallpapers>, response: retrofit2.Response<MagicalWallpapers>) {
                    if(response.body() != null){
                        var wlist = response.body()
                        var activity = activity
                        if(activity!= null && isAdded)
                        {
                            val ParentRecyclerViewItem = binding!!.parentRecyclerview
                            val layoutManager = LinearLayoutManager(requireActivity())
                            val parentItemAdapter = WallpaperParentAdapter(requireContext(),ParentItemList(wlist!!),this,"magical_wallpaper")
                            ParentRecyclerViewItem.adapter = parentItemAdapter
                            ParentRecyclerViewItem.layoutManager = layoutManager
                            binding!!.progressBar.visibility = View.GONE
                        }
                    }else{
                        binding!!.progressBar.visibility = View.GONE
                    }
                }
                override fun onFailure(call: Call<MagicalWallpapers>, t: Throwable) {
                    try
                    {
                        var activity = activity
                        if(activity!= null && isAdded){
                            binding?.progressBar?.visibility = View.GONE
                            Log.d("tag", "onFailure: ")
                        }
                    }
                    catch (e:Exception)
                    {
                        e.printStackTrace()
                    }

                }

                override fun onAdapterItemClick(url: String?, bitmap: Bitmap) {

                    val file: File = File(
                        requireContext().getExternalFilesDir("live")!!.absolutePath + "/" + WallpaperChildAdapter.idnew
                    )
                    if(file.exists())
                    {
                        checkForAdLiveWallpaper = checkForAdLiveWallpaper + 1
                        if(checkForAdLiveWallpaper%3==0)
                        {
                            showDialog()
                            loadInterstitialAd(resources.getString(R.string.EL_I_Activities) , true)
                        }
                        else
                        {
                            val demointent = Intent(WallpaperManager.ACTION_CHANGE_LIVE_WALLPAPER)
                            demointent.putExtra(WallpaperManager.EXTRA_LIVE_WALLPAPER_COMPONENT, ComponentName(requireContext(), GLWallpaperService::class.java))
                        try {
                            activityResultLauncher.launch(demointent)
                        } catch (e: java.lang.Exception) {}
                        }
                    }
                    else
                    { wallpaperClicked = true
                    var activity = activity
                    if(activity!= null && isAdded){

                        val executor = Executors.newSingleThreadExecutor()
                        executor.execute {
                            val requestQueue: RequestQueue = Volley.newRequestQueue(context)
                            val jsonArrayRequest = JsonArrayRequest(
                                Request.Method.GET,url,null,
                                { response ->
                                    if (isAdded){
                                        WallpaperFragment.wallpaper = bitmap
                                        try {
                                            var image_path: String? = null
                                            pDialog =
                                                ProgressDialog(
                                                    requireContext(),
                                                    R.style.CutoutIgnoreprogressbar
                                                )
                                            pDialog?.setTitle(
                                                "      " + requireContext().getString(R.string.main_ongoing_download)
                                            )
                                            pDialog?.setIndeterminate(
                                                false
                                            )

                                            pDialog?.setMax(
                                                100
                                            )
                                            pDialog?.setProgressStyle(
                                                ProgressDialog.STYLE_HORIZONTAL
                                            )
                                            pDialog?.setProgressNumberFormat(
                                                ""
                                            )
                                            pDialog?.setProgressPercentFormat(
                                                null
                                            )
                                            pDialog?.setCancelable(
                                                false
                                            )
                                            pDialog?.show()

                                            for (i in 0 until response!!.length()) {
                                                val student = response!!.getJSONObject(i)

                                                // Get the current student (json object) data
                                                image_path = student.getString("img_path")
                                            }

                                            val pleaseDownloadFileHere = PleaseDownloadFileHere(image_path)
                                            pleaseDownloadFileHere.execute()


                                            //                                            DownloadFileFromURL().execute(image_path)
                                        } catch (e: JSONException) {
                                            e.printStackTrace()
                                        }
                                    }
                                },
                                object : Response.ErrorListener {
                                    override fun onErrorResponse(error: VolleyError?) {
                                        Log.d("", "onErrorResponse: " + error)
                                    }
                                })
                            requestQueue.add(jsonArrayRequest)
                        }
                    }
                    }
                }
            })
        }
        else
        {
            Toast.makeText(requireContext() , "No Internet Connection" , Toast.LENGTH_SHORT).show()
        }
    }

    private fun ParentItemList(magicalWallpapers: MagicalWallpapers): List<WallpaperParentItem> {
        val itemList: MutableList<WallpaperParentItem> = ArrayList()
        if(magicalWallpapers.response.isNotEmpty()){
            for(i in magicalWallpapers.response) {
                val item = WallpaperParentItem(i.Category, ChildItemList(i.wallpapers))
                itemList.add(item)
            }
        }
        return itemList
    }

    private fun ChildItemList(wallpaperList:List<com.skytek.edgelighting.model2.Wallpaper>): List<WallpaperChildItem> {
        val ChildItemList: MutableList<WallpaperChildItem> = ArrayList()
        for(i in wallpaperList) {
            ChildItemList.add(WallpaperChildItem(i.id!!, i.thumbPath!!,i.thumbPath))
        }
        return ChildItemList
    }

    inner class PleaseDownloadFileHere(var imagePathHere : String?) : AsyncTask<String?, String?, String?>() {
        private var RESULT_FAIL = false

         override fun doInBackground(vararg f_url: String?): String? {
            var count: Int
            var input: InputStream? = null
             var lenghtOfFile = 0
            try {
                Log.d("serfq", imagePathHere.toString())
                try {
                    val url = URL(imagePathHere)
                    val connection = url.openConnection()
                    connection.contentLength
                    connection.connect()
                    lenghtOfFile = connection.contentLength
                    input = BufferedInputStream(url.openStream(), 8192)
                } catch (e: SSLHandshakeException) {}
                catch (e : SocketException){}
                catch (e : IOException){}
                catch (e : ProtocolException){}
                catch (e : ConnectException){
                    requireActivity().runOnUiThread(Runnable {
                        Toast.makeText(App.context, "Failed to connect to a server", Toast.LENGTH_LONG).show()
                    })
                }
                val storageDir = requireActivity().getExternalFilesDir("live")!!.getAbsolutePath() + "/"
                val imageFile = File(storageDir + WallpaperChildAdapter.idnew)
                val output = FileOutputStream(imageFile)
                val data = ByteArray(1024)
                var total: Long = 0
                try {
                    try {
                        while (input!!.read(data).also { count = it } != -1) {
                            publishProgress("" + (total * 100 / lenghtOfFile).toInt())
                            total += count.toLong()
                            output.write(data, 0, count)
                        }
                        output.flush()
                        output.close()
                        input.close()
                    } catch (e: SSLException) {}
                    catch (e : ProtocolException){}
                } catch (e: SocketException) {}
            } catch (e:FileNotFoundException){}
            catch (e: RuntimeException) {}
            return null
        }
        override fun onProgressUpdate(vararg values: String?) {
            super.onProgressUpdate(*values)
            values[0]?.let { pDialog?.setProgress(it.toInt()) } }
        override fun onPostExecute(file_url: String?) {
            try {
                    if (LiveWallpaperActivity.loader != null && LiveWallpaperActivity.loader!!.isShowing()) {
                        LiveWallpaperActivity.loader!!.dismiss()
                    }
                    if(activity!= null && isAdded && LiveWallpaperActivity.pause == false){
                        val intent1 = Intent(WallpaperManager.ACTION_CHANGE_LIVE_WALLPAPER)
                        intent1.putExtra(WallpaperManager.EXTRA_LIVE_WALLPAPER_COMPONENT, ComponentName(App.context!!, GLWallpaperService::class.java))
                        try {
                            activityResultLauncher.launch(intent1)
//                                                            startActivityForResult(intent1, 1)
                        } catch (e: ActivityNotFoundException) {}
                    }


                    if (pDialog != null && pDialog?.isShowing() == true && isAdded) {

                        pDialog?.dismiss()

                    }

            } catch (e: NullPointerException) {}
        }

    }
    fun showDialog() {

        customdialog()

    }

    private fun customdialog() {
        if(isAdded)
        {
            val builder = AlertDialog.Builder(requireContext())
            val viewGroup = requireView().findViewById<ViewGroup>(android.R.id.content)
            val dialogView = LayoutInflater.from(requireContext()).inflate(R.layout.customview, viewGroup, false)
            builder.setView(dialogView)
            alertDialog = builder.create()
            alertDialog!!.setCancelable(false)
            if(alertDialog!= null)
            {
                alertDialog!!.show()
            }
        }
    }


    override fun onResume() {
        super.onResume()
        if(!isOnline(requireContext()))
        {
            Toast.makeText(requireContext() , "No Internet Connection" , Toast.LENGTH_SHORT).show()
        }
    }
    fun loadInterstitialAd(id: String? , checkForServiceCall:Boolean) {
        val adRequest = AdRequest.Builder().build()
        InterstitialAd.load(App.context!!, id!!, adRequest, object : InterstitialAdLoadCallback() {
            override fun onAdLoaded(interstitialAd: InterstitialAd) {



                if (alertDialog != null) {
                    try {
                        alertDialog!!.dismiss()
                        alertDialog = null
                    } catch (e: java.lang.Exception) {
                    }
                }
                Interstitial.show(requireActivity(),object :
                    AdInterstitialShowListeners {
                    override fun onShowed() {
                    }

                    override fun onError() {
                    }

                    override fun onDismissed() {
                        if(checkForServiceCall) {
                            if (LiveWallpaperActivity.loader != null && LiveWallpaperActivity.loader!!.isShowing()) {
                                LiveWallpaperActivity.loader!!.dismiss()
                            }
                            if(activity!= null && isAdded){
                                val intent1 = Intent(WallpaperManager.ACTION_CHANGE_LIVE_WALLPAPER)
                                intent1.putExtra(WallpaperManager.EXTRA_LIVE_WALLPAPER_COMPONENT, ComponentName(App.context!!, GLWallpaperService::class.java))
                                try {
                                    activityResultLauncher.launch(intent1)
//                                                            startActivityForResult(intent1, 1)
                                } catch (e: ActivityNotFoundException) {}
                            }


                            if (pDialog != null && pDialog?.isShowing() == true && isAdded) {

                                pDialog?.dismiss()

                            }
                        }
                    }

                })

            }
            override fun onAdFailedToLoad(loadAdError: LoadAdError) {
                // Handle the error
                Log.i("ad_testing", loadAdError.message)
                if (alertDialog != null) {
                    try {
                        alertDialog!!.dismiss()
                        alertDialog = null
                    } catch (e: java.lang.Exception) {
                    }
                }


            }
        })
    }
    private var activityResultLauncher = registerForActivityResult(ActivityResultContracts.StartActivityForResult()) { result ->
        if (result.resultCode == Activity.RESULT_OK) {
            if(isAdded)
            {
                if(isOnline(requireContext()))
                {
                    showDialog()
                    loadInterstitialAd(resources.getString(R.string.EL_I_Activities) , false)
                }
                else
                {
                    //miss karao
                }
            }
            val sharedPref = PreferenceManager.getDefaultSharedPreferences(requireContext())
            val sharedEditior  = sharedPref.edit()
            sharedEditior.putBoolean("check_wallpaper_set", true)
            sharedEditior.apply()
            val bundle = Bundle()
            bundle.putString("wallpaper_applied", "1")
            Firebase.analytics.logEvent("wallpaper_applied_event", bundle)
        }else{
            wallpaperClicked = false
        }
    }
}

interface OnItemClickListner2 {
    fun onAdapterItemClick(url: String?,bitmap: Bitmap)
}

