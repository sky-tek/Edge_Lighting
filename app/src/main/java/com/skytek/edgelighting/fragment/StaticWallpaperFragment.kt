package com.skytek.edgelighting.fragment


import android.content.Intent
import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ProgressBar
import androidx.fragment.app.Fragment
import androidx.recyclerview.widget.GridLayoutManager
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import androidx.swiperefreshlayout.widget.SwipeRefreshLayout
import com.mobi.pixels.firebase.fireEvent
import com.skytek.edgelighting.activities.StaticCategoryWallpapersActivity
import com.skytek.edgelighting.adapter.CategoryAdapter
import com.skytek.edgelighting.adapter.CategoryAdapter1
import com.skytek.edgelighting.api.WallpaperInterface
import com.skytek.edgelighting.databinding.FragmentStaticWallpaperBinding
import com.skytek.edgelighting.modelclass.Response
import com.skytek.edgelighting.viewmodel.StaticWallpaperViewModel
import retrofit2.Call
import retrofit2.Callback


class StaticWallpaperFragment : Fragment(), CategoryAdapter1.CategoryItemClickListener,
    CategoryAdapter.CategoryItemClickListener1 {

    private lateinit var binding: FragmentStaticWallpaperBinding
    private lateinit var progressBar: ProgressBar
    private lateinit var swipeRefreshLayout: SwipeRefreshLayout
    private var isDataLoaded = false
    private lateinit var viewModel: StaticWallpaperViewModel


    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        /*   viewModel = ViewModelProvider(this).get(StaticWallpaperViewModel::class.java)
           retainInstance = true  // Retain the Fragment instance across configuration changes
           viewModel = ViewModelProvider(this).get(StaticWallpaperViewModel::class.java)
           // Check if there is a saved instance state
           savedInstanceState?.let {
               // Restore the data from the ViewModel
               isDataLoaded = it.getBoolean(KEY_IS_DATA_LOADED, false)
           }*/
//        val packageInfo = requireContext().packageManager.getPackageInfo(requireContext().packageName, 0)
//        fireEvent("RV_${packageInfo.versionCode}_Static_Wallpaper ")
        Log.d("abcdddd", "onCreate of static fragment : ")
        // Set this activity as the listener for item clicks
//        val adapter = CategoryAdapter(categoriesResponse, this)
    }

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        binding = FragmentStaticWallpaperBinding.inflate(inflater, container, false)
        progressBar = binding.progressBar // Initialize the ProgressBar

        Log.d("abcddd", "onCreateView static : ")

        binding.refreshBar.setOnRefreshListener {
            Log.d("nsdgsdgshjdgs", "refreshing: ")
            getCategoryItems()
        }

        return binding.root
    }

    override fun onActivityCreated(savedInstanceState: Bundle?) {
        super.onActivityCreated(savedInstanceState)
        Log.d("abcd", "onActivityCreated static : ")

        getCategoryItems()
        showProgressBar()
        Log.d("abcdddd", "onActivityCreated of static fragment : ")
//        binding.refreshBar.isRefreshing = true

    }

    override fun onItemClicked(wallpaperPath: String) {

    }

    override fun onItemClicked1(categoryId: String) {
        val intent = Intent(context, StaticCategoryWallpapersActivity::class.java)
        intent.putExtra("categoryId", categoryId)
        startActivity(intent)
    }

    private fun getCategoryItems() {


        var call =
            WallpaperInterface.create("https://airnet-technologies.com/3d-live-wallpaper/apis/")
                .getAllApiresponse()
        call?.enqueue(object : Callback<Response> {
            override fun onResponse(
                call: Call<Response>,
                response: retrofit2.Response<Response>
            ) {
                if (isAdded && activity != null) { // Check if fragment is attached to activity
                    if (response.isSuccessful) {


                        // Extract the data from the Retrofit response and pass it to the interface


                        // Save the response in the ViewModel
//                    viewModel.wallpaperResponse.value = response.body()

                        val adapter = CategoryAdapter(response.body()!!, this@StaticWallpaperFragment)
                        Log.d("abcddd", "onResponse of apii category adapter: " + adapter)
                        binding.horizontalRecyclerView.layoutManager =
                            LinearLayoutManager(context, LinearLayoutManager.HORIZONTAL, false)
                        binding.horizontalRecyclerView.adapter = adapter


                        val adapter1 = response.body()?.let {
                            CategoryAdapter1(
                                it,
                                this@StaticWallpaperFragment,
                                requireContext()
                            )
                        }
                        Log.d("abcddd", "onResponse of liveee apii category adapter: $adapter1")
                        adapter1?.let {
                            val layoutManager = GridLayoutManager(context, it.getSpanCount())
                            binding.horizontalRecyclerView1.layoutManager = layoutManager
                            binding.horizontalRecyclerView1.adapter = it
                        }

// Load img on screen
                        binding.horizontalRecyclerView.addOnScrollListener(object :
                            RecyclerView.OnScrollListener() {
                            override fun onScrolled(recyclerView: RecyclerView, dx: Int, dy: Int) {
                                super.onScrolled(recyclerView, dx, dy)
                                val layoutManager = recyclerView.layoutManager as? LinearLayoutManager
                                layoutManager?.let {
                                    val visibleItemCount = it.childCount
                                    val totalItemCount = it.itemCount
                                    val firstVisibleItem = it.findFirstVisibleItemPosition()

                                    if ((visibleItemCount + firstVisibleItem) >= totalItemCount - 5) {
                                        // Load more data when nearing the end of the list
                                        // Fetch more items from the API and add them to the adapter
                                        // Update the adapter dataset with the new items and notify the adapter
                                    }
                                }
                            }
                        })

                        // Hide the ProgressBar once data is received
                        hideProgressBar()

                        // Set isDataLoaded to true


                        // Show text1 when data is received
                        binding.text1.visibility = View.VISIBLE
                        binding.text.visibility = View.VISIBLE
//                    when data received from api hide refresh bar
                        binding.refreshBar.isRefreshing = false

                    } else {
                        Log.d("ajaygaaaaa", "program to *** gaya reponse is ${response.code()}")
                        // ... Handle unsuccessful response ...
                        hideProgressBar()
                    }
                }

            }

            override fun onFailure(call: Call<Response>, t: Throwable) {
                Log.d("ajaygaaaaa", "else reponse is ${t.message}")
                // ... Handle unsuccessful response ...
                hideProgressBar()

            }
        })
    }

    private fun showProgressBar() {
        progressBar.visibility = View.VISIBLE // Show ProgressBar
    }

    private fun hideProgressBar() {
        progressBar.visibility = View.GONE // Hide ProgressBar
    }


    /*  override fun onSaveInstanceState(outState: Bundle) {
          super.onSaveInstanceState(outState)
          // Save the state to avoid reloading data on configuration changes
          outState.putBoolean(KEY_IS_DATA_LOADED, isDataLoaded)
      }*/


    companion object {
        private const val KEY_IS_DATA_LOADED = "isDataLoaded"
    }

}