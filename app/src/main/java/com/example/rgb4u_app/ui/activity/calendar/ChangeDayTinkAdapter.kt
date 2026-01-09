package com.example.rgb4u.ver1_app.ui.activity.calendar

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import com.example.rgb4u.ver1_app.R

class ChangeDayThinkAdapter(private val situations: List<ChangeDaySituation>) : RecyclerView.Adapter<ChangeDayThinkAdapter.ViewHolder>() {

    class ViewHolder(view: View) : RecyclerView.ViewHolder(view) {
        val iconImageView: ImageView = view.findViewById(R.id.iconImageView)
        val situationTextView: TextView = view.findViewById(R.id.situationTextView)
        val exampleText: TextView = view.findViewById(R.id.exampleText)
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
        val view = LayoutInflater.from(parent.context).inflate(R.layout.item_summary_changeday_think, parent, false)
        return ViewHolder(view)
    }

    override fun onBindViewHolder(holder: ViewHolder, position: Int) {
        val situation = situations[position]
        holder.iconImageView.setImageResource(situation.iconResId)
        holder.situationTextView.text = situation.text
        holder.exampleText.text = situation.exampleText
    }

    override fun getItemCount() = situations.size
}

data class ChangeDaySituation(val iconResId: Int, val text: String, val exampleText: String)
