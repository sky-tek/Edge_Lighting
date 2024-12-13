package com.skytek.edgelighting.fragment

import android.annotation.SuppressLint
import android.app.Activity
import android.app.ActivityManager
import android.app.WallpaperManager
import android.content.ActivityNotFoundException
import android.content.ComponentName
import android.content.Intent
import android.graphics.Bitmap
import android.graphics.Color
import android.os.Build
import android.os.Bundle
import android.util.DisplayMetrics
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.activity.result.contract.ActivityResultContracts
import androidx.appcompat.app.AlertDialog
import androidx.fragment.app.Fragment
import androidx.preference.PreferenceManager
import androidx.recyclerview.widget.LinearLayoutManager
import com.google.firebase.ktx.Firebase
import com.mobi.pixels.firebase.fireEvent
import com.mobi.pixels.isOnline
import com.skytek.edgelighting.App

import com.skytek.edgelighting.CreateOrUpdateEdgesLight
import com.skytek.edgelighting.Listeners.BorderColorClickListners
import com.skytek.edgelighting.R
import com.skytek.edgelighting.WallpaperService
import com.skytek.edgelighting.WallpaperWindowEdgeService
import com.skytek.edgelighting.activities.EdgeOverlaySettingsActivity
import com.skytek.edgelighting.activities.LiveWallpaperActivity.Companion.pause
import com.skytek.edgelighting.activities.LiveWallpaperActivity.Companion.wallpaperClicked
import com.skytek.edgelighting.adapter.WallpaperParentAdapter
import com.skytek.edgelighting.api.WallpaperInterface
import com.skytek.edgelighting.databinding.FragmentWallpaperBinding
import com.skytek.edgelighting.models.Wallpaper
import com.skytek.edgelighting.models.WallpaperChildItem
import com.skytek.edgelighting.models.WallpaperParentItem
import com.skytek.edgelighting.models.Wallpapers
import com.skytek.edgelighting.utils.Const
import com.skytek.edgelighting.utils.MySharePreferencesEdge
import org.greenrobot.eventbus.EventBus
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response

class WallpaperFragment : Fragment() {

    var notchBottom = 0
    var notchTop = 0
    var alertDialog: AlertDialog? = null
    var checkForAd = 0
    private val active = ""

    //    var wallpaperWebService:WallpaperWebService? = null
//    var wallpaperRepository:WallpaperRepository? = null
//    lateinit var wallpaperViewModel: WallpaperViewModel
    var binding: FragmentWallpaperBinding? = null

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
//        binding = DataBindingUtil.inflate<FragmentWallpaperBinding>(inflater, com.skytek.edgelighting.R.layout.fragment_wallpaper, container, false)
        binding = FragmentWallpaperBinding.inflate(layoutInflater)
        return binding!!.root
    }

    override fun onActivityCreated(savedInstanceState: Bundle?) {
        super.onActivityCreated(savedInstanceState)
        val displayMetrics = DisplayMetrics()
        requireActivity().windowManager.defaultDisplay.getMetrics(displayMetrics)
        var width = displayMetrics.widthPixels
        val i = width / 2
        notchTop = i - 300
        notchBottom = i - 300

        color = intArrayOf(
            Color.parseColor("#EB1111"),
            Color.parseColor("#1A11EB"),
            Color.parseColor("#EB11DA"),
            Color.parseColor("#11D6EB"),
            Color.parseColor("#EBDA11"),
            Color.parseColor("#EBDA11"),
            Color.parseColor("#EB1111")
        )

        if (isOnline(requireContext())) {
            binding!!.progressBar.visibility = View.VISIBLE
            var call = WallpaperInterface.create("https://siahat.online/Admin/icon-frames/")
                .getWallpapers()
            call.enqueue(object : Callback<Wallpapers>, OnItemClickListner {
                override fun onResponse(call: Call<Wallpapers>, response: Response<Wallpapers>) {

                    if (response.body() != null) {
                        var wlist = response.body()
                        var activity = activity
                        if (activity != null && isAdded) {
                            val ParentRecyclerViewItem = binding!!.parentRecyclerview
                            val layoutManager = LinearLayoutManager(requireActivity())
                            val parentItemAdapter = WallpaperParentAdapter(
                                requireContext(),
                                ParentItemList(wlist!!),
                                this,
                                "wallpaper"
                            )
                            ParentRecyclerViewItem.adapter = parentItemAdapter
                            ParentRecyclerViewItem.layoutManager = layoutManager
                            binding!!.progressBar.visibility = View.GONE
                        }
                    } else {
                        binding!!.progressBar.visibility = View.GONE
                    }
                }

                override fun onFailure(call: Call<Wallpapers>, t: Throwable) {
                    var activity = activity
                    if (activity != null && isAdded) {
                        Log.d("tag", "onFailure: ")
                        binding!!.progressBar.visibility = View.GONE
                    }
                }

                override fun onAdapterItemClick(bitmap: Bitmap) {
                    wallpaperClicked = true
                    var activity = activity
                    if (activity != null && isAdded) {
                        wallpaper = bitmap
                        gotoLiveWallpaperScreen()
                    }
                }
            })
        }
    }

    override fun onResume() {
        super.onResume()
        if (!isOnline(requireContext())) {
            Toast.makeText(requireContext(), "No Internet Connection", Toast.LENGTH_SHORT).show()
        }
    }

    private fun ParentItemList(wallpaper: Wallpapers): List<WallpaperParentItem> {
        val itemList: MutableList<WallpaperParentItem> = ArrayList()
        for (i in wallpaper.response) {
            val item = WallpaperParentItem(i.Category!!, ChildItemList(i.Wallpapers!!))
            itemList.add(item)
        }
        return itemList
    }

    private fun ChildItemList(wallpaperList: List<Wallpaper>): List<WallpaperChildItem> {
        val ChildItemList: MutableList<WallpaperChildItem> = ArrayList()
        for (i in wallpaperList) {
            ChildItemList.add(WallpaperChildItem(i.id, i.Thumbnail, i.Background))
        }
        return ChildItemList
    }

    fun gotoLiveWallpaperScreen() {
        applyBorderLiveWallpaper()
        applyBorderOverlay_Edited()

        Log.d(
            "checkwallpaperchangehere",
            "wallapepr cahanged and deled1 activity is  ${activity} isadded is ${isAdded} and pause is ${pause}"
        )
        if (activity != null && isAdded && pause == false) {
//            val intent = Intent("android.service.wallpaper.CHANGE_LIVE_WALLPAPER")
//            intent.putExtra("android.service.wallpaper.extra.LIVE_WALLPAPER_COMPONENT", ComponentName(App.context!!, WallpaperService::class.java))
            Log.d("checkwallpaperchangehere", "wallapepr cahanged and deled2")

            if (Build.VERSION.SDK_INT == 27) {
                Log.d("checkhere", "code is insinde the build 2")
                WallpaperManager.getInstance(requireContext()).clear()
            }

            val intent = Intent(WallpaperManager.ACTION_CHANGE_LIVE_WALLPAPER)
            intent.putExtra(
                WallpaperManager.EXTRA_LIVE_WALLPAPER_COMPONENT,
                ComponentName(App.context!!, WallpaperService::class.java)
            )
            try {
                activityResultLauncher.launch(intent)
//                startActivityForResult(intent, 200)
            } catch (e: ActivityNotFoundException) {
                Log.d("checkwallpaperchangehere", "wallapepr cahanged and deled")
            }
        }

    }


    private fun customdialog() {
        if (isAdded) {
            val builder = AlertDialog.Builder(requireContext())
            val viewGroup = requireView().findViewById<ViewGroup>(android.R.id.content)
            val dialogView =
                LayoutInflater.from(requireContext()).inflate(R.layout.customview, viewGroup, false)
            builder.setView(dialogView)
            alertDialog = builder.create()
            alertDialog!!.setCancelable(false)
            alertDialog!!.show()
        }

    }

    private var activityResultLauncher =
        registerForActivityResult(ActivityResultContracts.StartActivityForResult()) { result ->
            if (result.resultCode == Activity.RESULT_OK) {
                Log.d("ercf", "onActivityResult: fdfdfd")
                try {
                    functionApply()

                    Log.d("checkdasdas", "asdasdsadsadasdasdas")
                    val intent = Intent(Const.Action_SetLiveWallpaper)
                    intent.putExtra(Const.Action_StopLiveWallpaper, Const.RUN)
                    intent.setPackage(requireActivity().packageName)
                    requireActivity().sendBroadcast(intent)

                    val sharedPref = PreferenceManager.getDefaultSharedPreferences(requireContext())
                    val sharedEditior = sharedPref.edit()
                    sharedEditior.putBoolean("check_wallpaper_set", true)
                    sharedEditior.apply()
                    fireEvent("wallp0aper_applied")
                } catch (e: Exception) {
                }
            } else {
                wallpaperClicked = false
            }
        }

    fun applyBorderLiveWallpaper() {
        if (MySharePreferencesEdge.getWallpaperBooleanValue(
                MySharePreferencesEdge.WALL_PAPER,
                context
            )
        ) {
            MySharePreferencesEdge.setInt(
                MySharePreferencesEdge.FINISH_SPEED,
                EdgeOverlaySettingsActivity.speed,
                activity
            )
            MySharePreferencesEdge.setInt(
                MySharePreferencesEdge.FINISH_SIZE,
                EdgeOverlaySettingsActivity.size,
                activity
            )
            MySharePreferencesEdge.setString(
                activity,
                MySharePreferencesEdge.FINISH_SHAPE,
                EdgeOverlaySettingsActivity.type
            )
            MySharePreferencesEdge.setInt(
                MySharePreferencesEdge.FINISH_RADIUSTOP,
                EdgeOverlaySettingsActivity.cornerTop,
                activity
            )
            MySharePreferencesEdge.setInt(
                MySharePreferencesEdge.FINISH_RADIUSBOTTOM,
                EdgeOverlaySettingsActivity.cornerBottom,
                activity
            )
            MySharePreferencesEdge.setInt(
                MySharePreferencesEdge.FINISH_NOTCHTOP,
                EdgeOverlaySettingsActivity.notchTop,
                activity
            )
            MySharePreferencesEdge.setInt(
                MySharePreferencesEdge.FINISH_NOTCHRADIUSTOP,
                EdgeOverlaySettingsActivity.notchRadiusTop,
                activity
            )
            MySharePreferencesEdge.setInt(
                MySharePreferencesEdge.FINISH_NOTCHRADIUSBOTTOM,
                EdgeOverlaySettingsActivity.notchRadiusBottom,
                activity
            )
            MySharePreferencesEdge.setInt(
                MySharePreferencesEdge.FINISH_NOTCHBOTTOM,
                EdgeOverlaySettingsActivity.notchBottom,
                activity
            )
            MySharePreferencesEdge.setInt(
                MySharePreferencesEdge.FINISH_NOTCHHEIGHT,
                EdgeOverlaySettingsActivity.notchHeight,
                activity
            )
            MySharePreferencesEdge.setString(
                activity,
                MySharePreferencesEdge.FINISH_COLOR1,
                BorderColorClickListners.convertIntToString(EdgeOverlaySettingsActivity.color[0])
            )
            MySharePreferencesEdge.setString(
                activity,
                MySharePreferencesEdge.FINISH_COLOR2,
                BorderColorClickListners.convertIntToString(EdgeOverlaySettingsActivity.color[1])
            )
            MySharePreferencesEdge.setString(
                activity,
                MySharePreferencesEdge.FINISH_COLOR3,
                BorderColorClickListners.convertIntToString(EdgeOverlaySettingsActivity.color[2])
            )
            MySharePreferencesEdge.setString(
                activity,
                MySharePreferencesEdge.FINISH_COLOR4,
                BorderColorClickListners.convertIntToString(EdgeOverlaySettingsActivity.color[3])
            )
            MySharePreferencesEdge.setString(
                activity,
                MySharePreferencesEdge.FINISH_COLOR5,
                BorderColorClickListners.convertIntToString(EdgeOverlaySettingsActivity.color[4])
            )
            MySharePreferencesEdge.setString(
                activity,
                MySharePreferencesEdge.FINISH_COLOR6,
                BorderColorClickListners.convertIntToString(EdgeOverlaySettingsActivity.color[5])
            )
            MySharePreferencesEdge.putBoolean(
                MySharePreferencesEdge.FINISH_CHECKNOTCH,
                EdgeOverlaySettingsActivity.checkNotch,
                activity
            )
            MySharePreferencesEdge.setInt(
                MySharePreferencesEdge.FINISH_BACKGROUND,
                EdgeOverlaySettingsActivity.checkBackground,
                activity
            )
            MySharePreferencesEdge.setString(
                activity,
                MySharePreferencesEdge.FINISH_BACKGROUNDCOLOR,
                EdgeOverlaySettingsActivity.colorBg
            )
            MySharePreferencesEdge.setString(
                activity,
                MySharePreferencesEdge.FINISH_BACKGROUNDLINK,
                EdgeOverlaySettingsActivity.linkBg
            )
        } else {
            MySharePreferencesEdge.setInt(MySharePreferencesEdge.FINISH_SPEED, 7, activity)
            MySharePreferencesEdge.setInt(MySharePreferencesEdge.FINISH_SIZE, 20, activity)
            MySharePreferencesEdge.setString(activity, MySharePreferencesEdge.FINISH_SHAPE, "line")
            MySharePreferencesEdge.setInt(MySharePreferencesEdge.FINISH_RADIUSTOP, 50, activity)
            MySharePreferencesEdge.setInt(MySharePreferencesEdge.FINISH_RADIUSBOTTOM, 50, activity)
            MySharePreferencesEdge.setInt(
                MySharePreferencesEdge.FINISH_NOTCHTOP,
                notchTop,
                activity
            )
            MySharePreferencesEdge.setInt(
                MySharePreferencesEdge.FINISH_NOTCHRADIUSTOP,
                50,
                activity
            )
            MySharePreferencesEdge.setInt(
                MySharePreferencesEdge.FINISH_NOTCHRADIUSBOTTOM,
                50,
                activity
            )
            MySharePreferencesEdge.setInt(
                MySharePreferencesEdge.FINISH_NOTCHBOTTOM,
                notchBottom,
                activity
            )
            MySharePreferencesEdge.setInt(MySharePreferencesEdge.FINISH_NOTCHHEIGHT, 60, activity)
            MySharePreferencesEdge.setString(
                activity,
                MySharePreferencesEdge.FINISH_COLOR1,
                BorderColorClickListners.convertIntToString(color[0])
            )
            MySharePreferencesEdge.setString(
                activity,
                MySharePreferencesEdge.FINISH_COLOR2,
                BorderColorClickListners.convertIntToString(color[1])
            )
            MySharePreferencesEdge.setString(
                activity,
                MySharePreferencesEdge.FINISH_COLOR3,
                BorderColorClickListners.convertIntToString(color[2])
            )
            MySharePreferencesEdge.setString(
                activity,
                MySharePreferencesEdge.FINISH_COLOR4,
                BorderColorClickListners.convertIntToString(color[3])
            )
            MySharePreferencesEdge.setString(
                activity,
                MySharePreferencesEdge.FINISH_COLOR5,
                BorderColorClickListners.convertIntToString(color[4])
            )
            MySharePreferencesEdge.setString(
                activity,
                MySharePreferencesEdge.FINISH_COLOR6,
                BorderColorClickListners.convertIntToString(color[5])
            )
            MySharePreferencesEdge.putBoolean(
                MySharePreferencesEdge.FINISH_CHECKNOTCH,
                false,
                activity
            )
            MySharePreferencesEdge.setInt(MySharePreferencesEdge.FINISH_BACKGROUND, 1, activity)
            MySharePreferencesEdge.setString(
                activity,
                MySharePreferencesEdge.FINISH_BACKGROUNDCOLOR,
                "#000000"
            )
            MySharePreferencesEdge.setString(
                activity,
                MySharePreferencesEdge.FINISH_BACKGROUNDLINK,
                "link"
            )
        }
    }

    private fun applyBorderLiveWallpaper_getters() {
        var string = MySharePreferencesEdge.getString(MySharePreferencesEdge.COLOR1, activity)
        var string2 = MySharePreferencesEdge.getString(MySharePreferencesEdge.COLOR2, activity)
        var string3 = MySharePreferencesEdge.getString(MySharePreferencesEdge.COLOR3, activity)
        var string4 = MySharePreferencesEdge.getString(MySharePreferencesEdge.COLOR4, activity)
        var string5 = MySharePreferencesEdge.getString(MySharePreferencesEdge.COLOR5, activity)
        var string6 = MySharePreferencesEdge.getString(MySharePreferencesEdge.COLOR6, activity)
        if (string == null) {
            string = "#EB1111"
        }
        if (string2 == null) {
            string2 = "#1A11EB"
        }
        if (string3 == null) {
            string3 = "#EB11DA"
        }
        if (string4 == null) {
            string4 = "#11D6EB"
        }
        if (string5 == null) {
            string5 = "#EBDA11"
        }
        if (string6 == null) {
            string6 = "#11EB37"
        }
        val i = MySharePreferencesEdge.getInt(MySharePreferencesEdge.SPEED, activity)
        val i2 = MySharePreferencesEdge.getInt(MySharePreferencesEdge.SIZE, activity)
        val i3 = MySharePreferencesEdge.getInt(MySharePreferencesEdge.RADIUSTOP, activity)
        val i4 = MySharePreferencesEdge.getInt(MySharePreferencesEdge.RADIUSBOTTOM, activity)
        val booleanValue =
            MySharePreferencesEdge.getBooleanValue(MySharePreferencesEdge.CHECKNOTCH, activity)
        val i5 = MySharePreferencesEdge.getInt(MySharePreferencesEdge.NOTCHTOP, activity)
        val i6 = MySharePreferencesEdge.getInt(MySharePreferencesEdge.NOTCHRADIUSTOP, activity)
        val i7 = MySharePreferencesEdge.getInt(MySharePreferencesEdge.NOTCHRADIUSBOTTOM, activity)
        val i8 = MySharePreferencesEdge.getInt(MySharePreferencesEdge.NOTCHBOTTOM, activity)
        val i9 = MySharePreferencesEdge.getInt(MySharePreferencesEdge.NOTCHHEIGHT, activity)

        MySharePreferencesEdge.setInt(MySharePreferencesEdge.FINISH_SIZE, i2, activity)
        MySharePreferencesEdge.setInt(MySharePreferencesEdge.FINISH_RADIUSTOP, i3, activity)
        MySharePreferencesEdge.setInt(MySharePreferencesEdge.FINISH_RADIUSBOTTOM, i4, activity)
        MySharePreferencesEdge.setInt(MySharePreferencesEdge.FINISH_NOTCHTOP, i5, activity)
        MySharePreferencesEdge.setInt(MySharePreferencesEdge.FINISH_NOTCHRADIUSTOP, i6, activity)
        MySharePreferencesEdge.setInt(MySharePreferencesEdge.FINISH_NOTCHRADIUSBOTTOM, i7, activity)
        MySharePreferencesEdge.setInt(MySharePreferencesEdge.FINISH_NOTCHBOTTOM, i8, activity)
        MySharePreferencesEdge.setInt(MySharePreferencesEdge.FINISH_NOTCHHEIGHT, i9, activity)
        MySharePreferencesEdge.setString(activity, MySharePreferencesEdge.FINISH_COLOR1, string)
        MySharePreferencesEdge.setString(activity, MySharePreferencesEdge.FINISH_COLOR2, string2)
        MySharePreferencesEdge.setString(activity, MySharePreferencesEdge.FINISH_COLOR3, string3)
        MySharePreferencesEdge.setString(activity, MySharePreferencesEdge.FINISH_COLOR4, string4)
        MySharePreferencesEdge.setString(activity, MySharePreferencesEdge.FINISH_COLOR5, string5)
        MySharePreferencesEdge.setString(activity, MySharePreferencesEdge.FINISH_COLOR6, string6)
        MySharePreferencesEdge.setInt(MySharePreferencesEdge.FINISH_SPEED, i, activity)
        MySharePreferencesEdge.putBoolean(
            MySharePreferencesEdge.FINISH_CHECKNOTCH,
            booleanValue,
            activity
        )
    }

    fun applyBorderOverlay_Edited() {
        if (MySharePreferencesEdge.getWallpaperBooleanValue(
                MySharePreferencesEdge.WALL_PAPER,
                context
            )
        ) {
            MySharePreferencesEdge.setInt(
                MySharePreferencesEdge.SIZE,
                EdgeOverlaySettingsActivity.size,
                activity
            )
            MySharePreferencesEdge.setInt(
                MySharePreferencesEdge.RADIUSTOP,
                EdgeOverlaySettingsActivity.cornerTop,
                activity
            )
            MySharePreferencesEdge.setInt(
                MySharePreferencesEdge.RADIUSBOTTOM,
                EdgeOverlaySettingsActivity.cornerBottom,
                activity
            )
            MySharePreferencesEdge.setInt(
                MySharePreferencesEdge.NOTCHTOP,
                EdgeOverlaySettingsActivity.notchTop,
                activity
            )
            MySharePreferencesEdge.setInt(
                MySharePreferencesEdge.NOTCHRADIUSTOP,
                EdgeOverlaySettingsActivity.notchRadiusTop,
                activity
            )
            MySharePreferencesEdge.setInt(
                MySharePreferencesEdge.NOTCHRADIUSBOTTOM,
                EdgeOverlaySettingsActivity.notchRadiusBottom,
                activity
            )
            MySharePreferencesEdge.setInt(
                MySharePreferencesEdge.NOTCHBOTTOM,
                EdgeOverlaySettingsActivity.notchBottom,
                activity
            )
            MySharePreferencesEdge.setInt(
                MySharePreferencesEdge.NOTCHHEIGHT,
                EdgeOverlaySettingsActivity.notchHeight,
                activity
            )
            MySharePreferencesEdge.setString(
                activity,
                MySharePreferencesEdge.COLOR1,
                BorderColorClickListners.convertIntToString(EdgeOverlaySettingsActivity.color[0])
            )
            MySharePreferencesEdge.setString(
                activity,
                MySharePreferencesEdge.COLOR2,
                BorderColorClickListners.convertIntToString(EdgeOverlaySettingsActivity.color[1])
            )
            MySharePreferencesEdge.setString(
                activity,
                MySharePreferencesEdge.COLOR3,
                BorderColorClickListners.convertIntToString(EdgeOverlaySettingsActivity.color[2])
            )
            MySharePreferencesEdge.setString(
                activity,
                MySharePreferencesEdge.COLOR4,
                BorderColorClickListners.convertIntToString(EdgeOverlaySettingsActivity.color[3])
            )
            MySharePreferencesEdge.setString(
                activity,
                MySharePreferencesEdge.COLOR5,
                BorderColorClickListners.convertIntToString(EdgeOverlaySettingsActivity.color[4])
            )
            MySharePreferencesEdge.setString(
                activity,
                MySharePreferencesEdge.COLOR6,
                BorderColorClickListners.convertIntToString(EdgeOverlaySettingsActivity.color[5])
            )
            MySharePreferencesEdge.setInt(
                MySharePreferencesEdge.SPEED,
                EdgeOverlaySettingsActivity.speed,
                activity
            )
            MySharePreferencesEdge.putBoolean(
                MySharePreferencesEdge.CHECKNOTCH,
                EdgeOverlaySettingsActivity.checkNotch,
                activity
            )
            MySharePreferencesEdge.setString(
                activity,
                MySharePreferencesEdge.SHAPE,
                EdgeOverlaySettingsActivity.type
            )
            MySharePreferencesEdge.setInt(
                MySharePreferencesEdge.BACKGROUND,
                EdgeOverlaySettingsActivity.checkBackground,
                activity
            )
            MySharePreferencesEdge.setString(
                activity,
                MySharePreferencesEdge.BACKGROUNDCOLOR,
                EdgeOverlaySettingsActivity.colorBg
            )
            MySharePreferencesEdge.setString(
                activity,
                MySharePreferencesEdge.BACKGROUNDLINK,
                EdgeOverlaySettingsActivity.linkBg
            )
        } else {
            MySharePreferencesEdge.setInt(MySharePreferencesEdge.SIZE, 20, activity)
            MySharePreferencesEdge.setInt(MySharePreferencesEdge.RADIUSTOP, 50, activity)
            MySharePreferencesEdge.setInt(MySharePreferencesEdge.RADIUSBOTTOM, 50, activity)
            MySharePreferencesEdge.setInt(MySharePreferencesEdge.NOTCHTOP, notchTop, activity)
            MySharePreferencesEdge.setInt(MySharePreferencesEdge.NOTCHRADIUSTOP, 50, activity)
            MySharePreferencesEdge.setInt(MySharePreferencesEdge.NOTCHRADIUSBOTTOM, 50, activity)
            MySharePreferencesEdge.setInt(MySharePreferencesEdge.NOTCHBOTTOM, notchBottom, activity)
            MySharePreferencesEdge.setInt(MySharePreferencesEdge.NOTCHHEIGHT, 60, activity)
            MySharePreferencesEdge.setString(
                activity,
                MySharePreferencesEdge.COLOR1,
                BorderColorClickListners.convertIntToString(color[0])
            )
            MySharePreferencesEdge.setString(
                activity,
                MySharePreferencesEdge.COLOR2,
                BorderColorClickListners.convertIntToString(color[1])
            )
            MySharePreferencesEdge.setString(
                activity,
                MySharePreferencesEdge.COLOR3,
                BorderColorClickListners.convertIntToString(color[2])
            )
            MySharePreferencesEdge.setString(
                activity,
                MySharePreferencesEdge.COLOR4,
                BorderColorClickListners.convertIntToString(color[3])
            )
            MySharePreferencesEdge.setString(
                activity,
                MySharePreferencesEdge.COLOR5,
                BorderColorClickListners.convertIntToString(color[4])
            )
            MySharePreferencesEdge.setString(
                activity,
                MySharePreferencesEdge.COLOR6,
                BorderColorClickListners.convertIntToString(color[5])
            )
            MySharePreferencesEdge.setInt(MySharePreferencesEdge.SPEED, 7, activity)
            MySharePreferencesEdge.putBoolean(MySharePreferencesEdge.CHECKNOTCH, false, activity)
            MySharePreferencesEdge.setString(activity, MySharePreferencesEdge.SHAPE, "line")
            MySharePreferencesEdge.setInt(MySharePreferencesEdge.BACKGROUND, 1, activity)
            MySharePreferencesEdge.setString(
                activity,
                MySharePreferencesEdge.BACKGROUNDCOLOR,
                "#000000"
            )
            MySharePreferencesEdge.setString(
                activity,
                MySharePreferencesEdge.BACKGROUNDLINK,
                "link"
            )
        }
    }

    private fun applyBackgroundLiveWallpaper() {
        val i = MySharePreferencesEdge.getInt(MySharePreferencesEdge.BACKGROUND, activity)
        val string =
            MySharePreferencesEdge.getString(MySharePreferencesEdge.BACKGROUNDCOLOR, activity)
        val string2 =
            MySharePreferencesEdge.getString(MySharePreferencesEdge.BACKGROUNDLINK, activity)
        MySharePreferencesEdge.setString(
            activity,
            MySharePreferencesEdge.FINISH_SHAPE,
            MySharePreferencesEdge.getString(MySharePreferencesEdge.SHAPE, activity)
        )
        MySharePreferencesEdge.setInt(MySharePreferencesEdge.FINISH_BACKGROUND, i, activity)
        MySharePreferencesEdge.setString(
            activity,
            MySharePreferencesEdge.FINISH_BACKGROUNDCOLOR,
            string
        )
        MySharePreferencesEdge.setString(
            activity,
            MySharePreferencesEdge.FINISH_BACKGROUNDLINK,
            string2
        )
    }

    fun functionApply() {
        try {
            val wallpaperInfo = WallpaperManager.getInstance(activity).wallpaperInfo
            val isMyServiceRunning = isMyServiceRunning(WallpaperWindowEdgeService::class.java)

            applyBackgroundLiveWallpaper()
            applyBorderLiveWallpaper_getters()
            if ((wallpaperInfo == null || wallpaperInfo.packageName != requireActivity().packageName) && !isMyServiceRunning) {
            } else {
                if (active == Const.UPDATE) {
                    Toast.makeText(
                        requireContext(),
                        resources.getString(R.string.Update_finish),
                        Toast.LENGTH_LONG
                    ).show()
                }
            }
            EventBus.getDefault().postSticky(CreateOrUpdateEdgesLight(true))
        } catch (e: Exception) {
        }
    }

    @SuppressLint("WrongConstant")
    fun isMyServiceRunning(cls: Class<*>): Boolean {
        for (runningServiceInfo in (requireActivity().getSystemService("activity") as ActivityManager).getRunningServices(
            Int.MAX_VALUE
        )) {
            if (cls.name == runningServiceInfo.service.className) {
                return true
            }
        }
        return false
    }


    companion object {
        var wallpaper: Bitmap? = null
        var color = intArrayOf()
    }

}

interface OnItemClickListner {
    fun onAdapterItemClick(wallpaper: Bitmap)
}

