package com.skytek.edgelighting.fragment

import android.content.Context
import android.content.Intent
import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.fragment.app.Fragment
import androidx.recyclerview.widget.GridLayoutManager
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import androidx.swiperefreshlayout.widget.SwipeRefreshLayout
import com.mobi.pixels.firebase.fireEvent
import com.skytek.edgelighting.R
import com.skytek.edgelighting.activities.LiveCategoryWallpaperActivity
import com.skytek.edgelighting.adapter.LiveCategoryAdapter
import com.skytek.edgelighting.adapter.LiveCategoryAdapter1
import com.skytek.edgelighting.api.WallpaperInterface
import com.skytek.edgelighting.databinding.FragmentLiveWallpaperBinding
import com.skytek.edgelighting.modelclass.LiveModelClass.LiveResponsePrent
import retrofit2.Call
import retrofit2.Callback

class   LiveWallpaperFragment : Fragment(), LiveCategoryAdapter1.LiveCategoryItemClickListener {
    private lateinit var binding: FragmentLiveWallpaperBinding
    private var mProgressIsShowing = false
    private lateinit var swipeRefreshLayout: SwipeRefreshLayout
    private lateinit var fragmentContext: Context
    lateinit var adapter1: LiveCategoryAdapter1
    // Declare adapter1 at the global level


//    lateinit var  adapter1: LiveCategoryAdapter1

    /*    val inflater = LayoutInflater.from(requireContext())
        val customProgressDialogView = inflater.inflate(R.layout.layout_progress_dialog, null)
        // Find the "Cancel" button in the inflated layout
        val cancelButton = customProgressDialogView.findViewById<TextView>(R.id.cancelBtn)*/

    private val SET_LIVE_WALLPAPER_REQUEST = 100

    private var callbackToActivity: callbacktoActivity? = null


    private lateinit var fragmentCallback: FragmentCallback

    override fun onAttach(context: Context) {
        super.onAttach(context)
        fragmentContext = context
        fragmentCallback = context as FragmentCallback
    }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?
    ): View {
        binding = FragmentLiveWallpaperBinding.inflate(inflater, container, false)



        binding.refreshBar.setOnRefreshListener {
            Log.d("nsdgsdgshjdgs", "refreshing: ")
            getLiveCategoryItems()
        }



        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        Log.d("abcdddddddddd", " of live paperrrrrrrrrrrrcreaghvgjuxv : ")
    }
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        Log.d("abcdddddddddd", " of live paperrrrrrrrrrrr : ")
//        val packageInfo = requireContext().packageManager.getPackageInfo(requireContext().packageName, 0)
//        fireEvent("RV_${packageInfo.versionCode}_live_Wallpaper ")
    }
    override fun onActivityCreated(savedInstanceState: Bundle?) {
        super.onActivityCreated(savedInstanceState)
        getLiveCategoryItems()
        showProgressBar()
        Log.d("abcd", " of live paperrrrrrrrrrrr : ")


    }


    override fun onItemClicked(wallpaperPath: Boolean, absolutePath: String) {

        Log.d("yesssssssssssssssssssssss", "onItemClicked: ${wallpaperPath} -> ${absolutePath} ")
        fragmentCallback.onNameAdded(wallpaperPath, absolutePath)
    }



    private fun getLiveCategoryItems() {
        fragmentContext.let {
            var call =
                WallpaperInterface.create("https://airnet-technologies.com/3d-live-wallpaper/apis/")
                    .getLiveAllApiresponse()

            call.enqueue(object : Callback<LiveResponsePrent> {
                override fun onResponse(
                    call: Call<LiveResponsePrent>, response: retrofit2.Response<LiveResponsePrent>
                ) {
                    if (isAdded && activity != null) {
                        if (response.isSuccessful) {
                            val adapter = LiveCategoryAdapter(response.body()!!,
                                object : LiveCategoryAdapter.CategoryItemClickListener1 {
                                    override fun onItemClicked1(categoryId: String) {
                                        val intent = Intent(
                                            fragmentContext,
                                            LiveCategoryWallpaperActivity::class.java
                                        )
                                        intent.putExtra("categoryId", categoryId)
                                        startActivity(intent)
                                    }
                                })
                            binding.horizontalRecyclerView.layoutManager =
                                LinearLayoutManager(context, LinearLayoutManager.HORIZONTAL, false)
                            binding.horizontalRecyclerView.adapter = adapter


                            adapter1 = LiveCategoryAdapter1(
                                response.body()!!, this@LiveWallpaperFragment, fragmentContext
                            )
                            val layoutManager = GridLayoutManager(
                                fragmentContext,
                                3,
                                GridLayoutManager.VERTICAL,
                                false
                            )
                            binding.horizontalRecyclerView1.layoutManager = layoutManager
                            binding.horizontalRecyclerView1.adapter = adapter1

                            //                   load img faster on screen
                            binding.horizontalRecyclerView.addOnScrollListener(object :
                                RecyclerView.OnScrollListener() {
                                override fun onScrolled(
                                    recyclerView: RecyclerView,
                                    dx: Int,
                                    dy: Int
                                ) {
                                    super.onScrolled(recyclerView, dx, dy)
                                    val visibleItemCount = layoutManager.childCount
                                    val totalItemCount = layoutManager.itemCount
                                    val firstVisibleItem =
                                        layoutManager.findFirstVisibleItemPosition()

                                    if ((visibleItemCount + firstVisibleItem) >= totalItemCount - 5) {
                                        // Load more data when nearing the end of the list
                                        // Fetch more items from the API and add them to the adapter
                                        // Update the adapter dataset with the new items and notify the adapter
                                    }
                                }
                            })


                            // Hide the ProgressBar once data is received
                            hideProgressBar()
                            // Show text1 when data is received
                            binding.text1.visibility = View.VISIBLE
                            binding.text.visibility = View.VISIBLE
                            //                    when data received from api hide refresh bar
                            Log.d("nsdgsdgshjdgs", "onResponse: ")
                            binding.refreshBar.isRefreshing = false
                        } else {
                            Log.d("ajaygaaaaa", "response =  ${response.code()}")

                        }
                    }


                }

                override fun onFailure(call: Call<LiveResponsePrent>, t: Throwable) {
                    if (isAdded && activity != null) { // Check if fragment is attached to activity
                        Toast.makeText(requireActivity(), "Network Error", Toast.LENGTH_SHORT)
                            .show()
                        hideProgressBar()
                    }
                }

            })
        }


    }


    private fun showProgressBar() {
        binding.progressBar.visibility = View.VISIBLE // Show ProgressBar
    }

    private fun hideProgressBar() {
        binding.progressBar.visibility = View.GONE // Hide ProgressBar
    }

    interface callbacktoActivity {
        fun onCallbackItemClicked(wallpaperPath: Boolean, fragmentContext: Context)

    }


    interface FragmentCallback {
        fun onNameAdded(wallpaperPath: Boolean, absolutePath: String)
    }

}