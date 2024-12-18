package com.skytek.edgelighting.activities

import android.app.Activity
import android.app.WallpaperManager
import android.content.ComponentName
import android.content.Context
import android.content.Intent
import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.widget.ProgressBar
import android.widget.TextView
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import androidx.core.app.ActivityCompat
import androidx.core.content.ContextCompat
import androidx.recyclerview.widget.GridLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.mobi.pixels.adInterstitial.AdInterstitialLoadListeners
import com.mobi.pixels.adInterstitial.AdInterstitialShowListeners
import com.mobi.pixels.adInterstitial.Interstitial

import com.skytek.edgelighting.R
import com.skytek.edgelighting.adapter.LiveSubCategoryAdapter
import com.skytek.edgelighting.adapter.LiveSubCategoryAdapter.Companion.isDownloadCompleted
import com.skytek.edgelighting.api.StaticApiClient
import com.skytek.edgelighting.api.WallpaperInterface
import com.skytek.edgelighting.databinding.ActivityLiveCategoryWallpaperBinding
import com.skytek.edgelighting.model2.StaticWallCat
import com.skytek.edgelighting.service.GLWallpaperService


import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import kotlinx.coroutines.suspendCancellableCoroutine
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response

class LiveCategoryWallpaperActivity : AppCompatActivity(),
    LiveSubCategoryAdapter.CategoryItemClickListener {

    private lateinit var binding: ActivityLiveCategoryWallpaperBinding
    private lateinit var progressBar: ProgressBar
    private var mProgressIsShowing = false
    lateinit var adapter1: LiveSubCategoryAdapter


    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        binding = ActivityLiveCategoryWallpaperBinding.inflate(layoutInflater)
        setContentView(binding.root)
        // Change the status bar color
        window.statusBarColor = ContextCompat.getColor(this, R.color.background)

        progressBar = binding.progressBar // Initialize the ProgressBar
        showProgressBar()


        val categoryId = intent.getStringExtra("categoryId")
        val categoryName = intent.getStringExtra("categoryName")
        binding.textViewTitle.text = categoryName

        CoroutineScope(Dispatchers.IO).launch {
            val categoryId = intent.getStringExtra("categoryId") ?: ""
            val categoryName = intent.getStringExtra("categoryName") ?: ""

            val data = APIData(categoryId)
            runOnUiThread {
                updateUIWithData(data, categoryName)
                hideProgressBar()
                binding.refreshBar.isRefreshing = false
            }
        }
        binding.refreshBar.setOnRefreshListener {

            CoroutineScope(Dispatchers.IO).launch {
                val categoryId = intent.getStringExtra("categoryId") ?: ""
                val categoryName = intent.getStringExtra("categoryName") ?: ""

                val data = APIData(categoryId)
                runOnUiThread {
                    updateUIWithData(data, categoryName)
                    hideProgressBar()
                    binding.refreshBar.isRefreshing = false
                }
            }
        }
        binding.backArrow.setOnClickListener {
            // Navigate to StaticWallpaperFragment on back arrow click
            onBackPressed()
        }

    }


    private suspend fun APIData(catID: String): ArrayList<StaticWallCat> =
        suspendCancellableCoroutine { continuation ->
            val jsonPlaceHolderApi: WallpaperInterface? = StaticApiClient.get()?.create(
                WallpaperInterface::class.java
            )
            val call =
                jsonPlaceHolderApi?.categoryWallpapersLive("https://airnet-technologies.com/3d-live-wallpaper/apis/live_wallpaper_demoapi.php?id=${catID}")
            call?.enqueue(object : Callback<ArrayList<StaticWallCat>> {
                override fun onResponse(
                    call: Call<ArrayList<StaticWallCat>>,
                    response: Response<ArrayList<StaticWallCat>>
                ) {
                    if (response.isSuccessful) {
                        val dataResponse = response.body()
                        // Retrieve the passed categoryId from the intent


                        if (dataResponse != null) {
                            Log.d("chectheres", "response saus ${dataResponse}")
                            val categoryName = intent.getStringExtra("categoryName") ?: ""
                            updateUIWithData(dataResponse, categoryName)
//                        hide the progres bar when data is received
                            hideProgressBar()
                            // binding.refreshBar.visibility=View.GONE
                            binding.refreshBar.isRefreshing = false

                        } else {
                            Log.d("chectheres", "response else ${dataResponse}")
                            hideProgressBar()
                        }
                    }
                }

                override fun onFailure(call: Call<ArrayList<StaticWallCat>>, t: Throwable) {
                    Toast.makeText(
                        this@LiveCategoryWallpaperActivity,
                        "error says ${t.message}",
                        Toast.LENGTH_SHORT
                    ).show()
                }
            })
        }

    private fun updateUIWithData(data: ArrayList<StaticWallCat>, categoryName: String) {
        Log.d("callbysomeOOne", "updateUIWithData: ${data.size}")
        val recyclerView: RecyclerView = findViewById(R.id.horizontalRecyclerView1)
        adapter1 = LiveSubCategoryAdapter(categoryName, data, this, this)
        val layoutManager = GridLayoutManager(
            this, adapter1.getSpanCount()
        ) // Assuming you have a method to get span count in your StaticCategoryAdapter

        recyclerView.layoutManager = layoutManager
        recyclerView.adapter = adapter1
    }


    private fun dismissProgressDialog1() {
        if (mProgressIsShowing) {
            binding.layout.removeViewAt(binding.layout.childCount - 1)
            mProgressIsShowing = false
        }
    }


    private fun showProgressBar() {
        progressBar.visibility = View.VISIBLE // Show ProgressBar
    }

    private fun hideProgressBar() {
        progressBar.visibility = View.GONE // Hide ProgressBar
    }

    override fun onItemClicked(wallpaperPath: Boolean, outputFile: String) {
        if (!wallpaperPath && outputFile == "") {
            runOnUiThread {
                dismissProgressDialog()
            }
            return
        }
        if (wallpaperPath) {
            Log.d("clickedHere", "onItemClicked111111111111: ")
            showProgressDialog()
        } else {
            dismissProgressDialog()



            Log.d("abcd", "onBindViewHolder: ${outputFile}")
            val sharedPreferences = this.getSharedPreferences("MyPreferences", Context.MODE_PRIVATE)
            oldID = sharedPreferences.getString("wallpaperPath", null)
            Log.d("fdsfsd","190"+ oldID.toString())
            currentId = outputFile
            newID = currentId
            Log.d("fdsfsd","193"+ newID.toString())
            val intent = Intent(WallpaperManager.ACTION_CHANGE_LIVE_WALLPAPER)
            intent.putExtra(
                WallpaperManager.EXTRA_LIVE_WALLPAPER_COMPONENT,
                ComponentName(this, GLWallpaperService::class.java)
            )
            ActivityCompat.startActivityForResult(this as Activity, intent, 100, null)
        }
    }

    fun showProgressDialog() {
        Log.d("TAG", "out of cancel btn of sub categoryshowProgressDialog: ")
        if (!mProgressIsShowing) {
            val view = LayoutInflater.from(this)
                .inflate(R.layout.layout_progress_dialog, binding.layout, true)
            mProgressIsShowing = true
            view.findViewById<TextView>(R.id.cancelBtn).setOnClickListener {
                isDownloadCompleted = false
                Log.d("TAG", "cancel btn of sub categoryshowProgressDialog: ")
                LiveSubCategoryAdapter.isDownloadEnabled = false
                dismissProgressDialog()
            }
        }
    }

    fun dismissProgressDialog() {
        if (mProgressIsShowing) {
            binding.layout.removeViewAt(binding.layout.childCount - 1)
            mProgressIsShowing = false
        }
    }


    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        super.onActivityResult(requestCode, resultCode, data)
        Log.d("asdfdasf12332", "oldID: $oldID")
        Log.d("asdfdasf12332", "newID: $newID")
        if (requestCode == 100 && resultCode == RESULT_OK) {
            oldID = newID
            val sharedPreferences = getSharedPreferences("MyPreferences", Context.MODE_PRIVATE)
            val editor = sharedPreferences.edit()
            editor.putString("wallpaperPath", oldID)
            editor.apply()


            //showRateApp(this@StaticWallpaperActivity)
        }

        if (requestCode == 100 && resultCode == RESULT_CANCELED) {
            Log.d("asdfdasf12332", "onActivityResult: ")
            currentId = oldID
            Log.d("asdfdasf12332","245"+ oldID.toString())

        }


    }


    override fun onPause() {
        super.onPause()
        dismissProgressDialog1()
        LiveSubCategoryAdapter.isDownloadEnabled = false
        // Check if a RecyclerView item was clicked
        /*  if (adapter1.clickedItemPosition != null) {
              adapter1.setCancelDownload(adapter1.clickedItemPosition!!, true)

              // Set the download completion status to false
              adapter1.downloadedMap[adapter1.clickedItemPosition!!] = false

              // Cancel the current downloading position
              adapter1.cancelDownload()

              adapter1.clickedItemPosition = null  // Reset clickedItemPosition after handling
          }*/
    }


}