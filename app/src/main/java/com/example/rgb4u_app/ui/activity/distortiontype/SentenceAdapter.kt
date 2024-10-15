package com.example.rgb4u_app.ui.activity.distortiontype

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import com.example.rgb4u_app.R

class SentenceAdapter(private val sentenceList: List<DistortionData>) :
    RecyclerView.Adapter<SentenceAdapter.SentenceViewHolder>() {

    inner class SentenceViewHolder(view: View) : RecyclerView.ViewHolder(view) {
        val tvMyThoughtTitle: TextView = view.findViewById(R.id.tv_my_thought_title)
        val tvMyThoughts: TextView = view.findViewById(R.id.tv_my_thoughts)
        val tvReasonTitle: TextView = view.findViewById(R.id.tv_reason_title)
        val tvReason: TextView = view.findViewById(R.id.tv_reason)
        val tvSuggestionTitle: TextView = view.findViewById(R.id.tv_suggestion_title)
        val tvShortSuggestion: TextView = view.findViewById(R.id.tv_short_suggestion)
        val tvSuggestion: TextView = view.findViewById(R.id.tv_suggestion)
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): SentenceViewHolder {
        val view = LayoutInflater.from(parent.context)
            .inflate(R.layout.item_sentence, parent, false)
        return SentenceViewHolder(view)
    }

    override fun onBindViewHolder(holder: SentenceViewHolder, position: Int) {
        val data = sentenceList[position]

        // XML 뷰와 DistortionData의 내용을 연결
        holder.tvMyThoughtTitle.text = data.myThoughtTitle
        holder.tvMyThoughts.text = data.myThought
        holder.tvReasonTitle.text = data.reasonTitle
        holder.tvReason.text = data.reason
        holder.tvSuggestionTitle.text = data.suggestionTitle
        holder.tvShortSuggestion.text = data.shortSuggestion
        holder.tvSuggestion.text = data.suggestion
    }

    override fun getItemCount(): Int {
        return sentenceList.size
    }
}

