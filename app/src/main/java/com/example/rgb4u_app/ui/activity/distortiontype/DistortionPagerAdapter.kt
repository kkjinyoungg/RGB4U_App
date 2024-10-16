package com.example.rgb4u_app.ui.activity.distortiontype

import android.content.Context
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import com.example.rgb4u_app.R

class DistortionPagerAdapter(
    private val context: Context,
    private val distortionDataList: List<DistortionData>
) : RecyclerView.Adapter<DistortionPagerAdapter.ViewHolder>() {

    inner class ViewHolder(view: View) : RecyclerView.ViewHolder(view) {
        val title: TextView = view.findViewById(R.id.tv_my_thought_title)
        val myThought: TextView = view.findViewById(R.id.tv_my_thoughts)
        val reasonTitle: TextView = view.findViewById(R.id.tv_reason_title)
        val reason: TextView = view.findViewById(R.id.tv_reason)
        val suggestionTitle: TextView = view.findViewById(R.id.tv_suggestion_title)
        val shortSuggestion: TextView = view.findViewById(R.id.tv_short_suggestion)
        val suggestions: TextView = view.findViewById(R.id.tv_suggestion)
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
        // item_sentence.xml을 inflate
        val view = LayoutInflater.from(context)
            .inflate(R.layout.item_sentence, parent, false)
        return ViewHolder(view)
    }

    override fun onBindViewHolder(holder: ViewHolder, position: Int) {
        val data = distortionDataList[position]
        holder.title.text = data.myThoughtTitle
        holder.myThought.text = data.myThought
        holder.reasonTitle.text = data.reasonTitle
        holder.reason.text = data.reason
        holder.suggestionTitle.text = data.suggestionTitle
        holder.shortSuggestion.text = data.shortSuggestion
        holder.suggestions.text = data.suggestion
    }

    override fun getItemCount(): Int {
        return distortionDataList.size
    }
}
