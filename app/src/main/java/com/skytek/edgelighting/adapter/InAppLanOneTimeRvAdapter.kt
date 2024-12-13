package com.skytek.edgelighting.adapter


import android.content.Context
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import android.widget.TextView
import androidx.appcompat.app.AppCompatActivity
import androidx.core.content.ContextCompat
import androidx.recyclerview.widget.RecyclerView
import com.skytek.edgelighting.R
import com.skytek.edgelighting.activities.onboarding.OnBoardingLanguageScreen
import com.skytek.edgelighting.utils.ConversationLanguage


class InAppLanOneTimeRvAdapter(
    private val context: Context,
    private var languageList: ArrayList<ConversationLanguage>,
    val pos:Int,
    private val onLanguageItemClick: (Boolean) -> Unit


) : RecyclerView.Adapter<InAppLanOneTimeRvAdapter.LanguageViewHolder>() {


    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): LanguageViewHolder {
        val view =
            LayoutInflater.from(context).inflate(R.layout.language_select_item_rv, parent, false)
        return LanguageViewHolder(view)
    }

    private var selectedPosition = pos
    private var scrollPosition: Int = -1
    override fun onBindViewHolder(holder: LanguageViewHolder, position: Int) {

        holder.languageTextView.text = languageList[position].languageName
        holder.languageImageView.setImageResource(languageList[position].languageFlag)

        if (selectedPosition == position) {
            holder.itemView.setBackgroundDrawable(
                ContextCompat.getDrawable(context, R.drawable.bg_selected_lan)
            )
        } else {
            holder.itemView.setBackgroundDrawable(
                ContextCompat.getDrawable(context, R.drawable.bg_function)
            )
        }

        holder.itemView.setOnClickListener {

            val previousSelectedPosition = selectedPosition
            scrollPosition = -1
            selectedPosition = position
            val languagePreferences =
                context.getSharedPreferences(
                    OnBoardingLanguageScreen.LANGUAGE_PREFERENCE_KEY,
                    AppCompatActivity.MODE_PRIVATE
                )
            val languagePreferenceEditor = languagePreferences.edit()

            languagePreferenceEditor.putString(
                OnBoardingLanguageScreen.LANGUAGE_PREFERENCE_VALUE_KEY,
                languageList[position].languageCode
            )
            languagePreferenceEditor.putString(
                OnBoardingLanguageScreen.LANGUAGE_PREFERENCE_NAME_KEY,
                languageList[position].languageName
            )
            languagePreferenceEditor.putInt(
                "LANGUAGE_PREFERENCE_Flag_KEY",
                languageList[position].languageFlag
            )
            languagePreferenceEditor.apply()
            notifyItemChanged(previousSelectedPosition)
            notifyItemChanged(selectedPosition)
            onLanguageItemClick(true)
        }


    }

    fun setScrollPosition(position: Int) {
        this.scrollPosition = position
        notifyItemChanged(position)
    }

    override fun getItemViewType(position: Int): Int {
        return position
    }

    override fun getItemCount(): Int {
        return languageList.size
    }

    inner class LanguageViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {
        val languageTextView: TextView = itemView.findViewById(R.id.languageName)

        val languageImageView: ImageView = itemView.findViewById(R.id.languageFlag)

    }

}