package com.skytek.edgelighting.activities

import android.os.Bundle
import android.util.Log
import android.view.View
import android.widget.ProgressBar
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import androidx.core.content.ContextCompat
import androidx.recyclerview.widget.GridLayoutManager
import androidx.recyclerview.widget.RecyclerView
import androidx.swiperefreshlayout.widget.SwipeRefreshLayout
import com.skytek.edgelighting.R
import com.skytek.edgelighting.adapter.StaticCategoryAdapter
import com.skytek.edgelighting.api.StaticApiClient
import com.skytek.edgelighting.api.WallpaperInterface
import com.skytek.edgelighting.databinding.ActivityStaticCategoryWallpapersBinding
import com.skytek.edgelighting.model2.StaticWallCat
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import kotlinx.coroutines.suspendCancellableCoroutine
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response

class StaticCategoryWallpapersActivity : AppCompatActivity() {

    private lateinit var binding: ActivityStaticCategoryWallpapersBinding
    private lateinit var progressBar: ProgressBar
    private lateinit var swipeRefreshLayout: SwipeRefreshLayout

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityStaticCategoryWallpapersBinding.inflate(layoutInflater)
        setContentView(binding.root)
// Change the status bar color
        window.statusBarColor = ContextCompat.getColor(this, R.color.background)

        progressBar = binding.progressBar // Initialize the ProgressBar
        swipeRefreshLayout = binding.refreshBar // Initialize the SwipeRefreshLayout
        showProgressBar()


        val categoryId = intent.getStringExtra("categoryId")
        val categoryName = intent.getStringExtra("categoryName")
        Log.d("abcd",categoryName!!.toString())
        binding.textViewTitle.text=categoryName

        CoroutineScope(Dispatchers.IO).launch {
            val categoryId = intent.getStringExtra("categoryId") ?: ""
            val categoryName = intent.getStringExtra("categoryName") ?: ""

            val data = APIData(categoryId)
            runOnUiThread {

                updateUIWithData(data, categoryName)
                binding.refreshBar.isRefreshing = false
                hideProgressBar()
            }
        }


        binding.refreshBar.setOnRefreshListener {

            CoroutineScope(Dispatchers.IO).launch {
                val categoryId = intent.getStringExtra("categoryId") ?: ""
                val categoryName = intent.getStringExtra("categoryName") ?: ""

                val data = APIData(categoryId)
                runOnUiThread {

                    updateUIWithData(data, categoryName)
                    binding.refreshBar.isRefreshing = false
                    hideProgressBar()
                }
            }
        }

        binding.backArrow.setOnClickListener {
            // Navigate to StaticWallpaperFragment on back arrow click
            onBackPressed()
        }

    }



    private suspend fun APIData(catID:String): ArrayList<StaticWallCat> = suspendCancellableCoroutine { continuation ->
        val jsonPlaceHolderApi: WallpaperInterface? = StaticApiClient.get()?.create(
            WallpaperInterface::class.java)
        val call = jsonPlaceHolderApi?.categoryWallpapersLive("https://airnet-technologies.com/3d-live-wallpaper/apis/single_static_categ.php?id=${catID}")
        call?.enqueue(object : Callback<ArrayList<StaticWallCat>> {
            override fun onResponse(call: Call<ArrayList<StaticWallCat>>, response: Response<ArrayList<StaticWallCat>>) {
                if (response.isSuccessful) {
                    val dataResponse = response.body()
                    // Retrieve the passed categoryId from the intent


                    if (dataResponse != null)
                    {
                        Log.d("chectheres" , "response saus ${dataResponse}")
                        val categoryName = intent.getStringExtra("categoryName") ?: ""
                        updateUIWithData(dataResponse, categoryName)
//                        hide the progres bar when data is received
                        hideProgressBar()
                        binding.refreshBar.isRefreshing = false
                    }
                    else
                    {
                        Log.d("chectheres" , "response else ${dataResponse}")
                        hideProgressBar()
                    }
                }
            }
            override fun onFailure(call: Call<ArrayList<StaticWallCat>>, t: Throwable) {
                Toast.makeText(this@StaticCategoryWallpapersActivity,"error says ${t.message}", Toast.LENGTH_SHORT).show()
            }
        })
    }

    private fun updateUIWithData(data: ArrayList<StaticWallCat>, categoryName: String) {
        val recyclerView: RecyclerView = findViewById(R.id.horizontalRecyclerView1)
        val adapter = StaticCategoryAdapter(categoryName, data, object : StaticCategoryAdapter.CategoryItemClickListener {
            override fun onItemClicked(wallpaperPath: String) {
                Log.d("isClickedVjhdujyvuyj", "onItemClicked: ")
            }
        })
        val layoutManager = GridLayoutManager(this, adapter.getSpanCount()) // Assuming you have a method to get span count in your StaticCategoryAdapter

        recyclerView.layoutManager = layoutManager
        recyclerView.adapter = adapter
    }


    private fun showProgressBar() {
        progressBar.visibility = View.VISIBLE // Show ProgressBar
    }

    private fun hideProgressBar() {
        progressBar.visibility = View.GONE // Hide ProgressBar
    }
}