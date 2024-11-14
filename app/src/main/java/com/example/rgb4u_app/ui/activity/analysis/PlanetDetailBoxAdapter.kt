package com.example.rgb4u_app.ui.activity.analysis

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import com.example.rgb4u_app.R


// 데이터 클래스 정의
data class BoxData(val content: String, val date: String)


class PlanetDetailBoxAdapter (private val boxList: List<BoxData>) :
    RecyclerView.Adapter<PlanetDetailBoxAdapter.BoxViewHolder>() {

    // ViewHolder 클래스 정의
    inner class BoxViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {
        val contentTextView: TextView = itemView.findViewById(R.id.tv_content)
        val dateTextView: TextView = itemView.findViewById(R.id.tv_date)
    }

    // ViewHolder 생성
    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): BoxViewHolder {
        val itemView = LayoutInflater.from(parent.context)
            .inflate(R.layout.item_planet_detail_box, parent, false)
        return BoxViewHolder(itemView)
    }

    // ViewHolder에 데이터 바인딩
    override fun onBindViewHolder(holder: BoxViewHolder, position: Int) {
        val boxData = boxList[position]
        holder.contentTextView.text = boxData.content
        holder.dateTextView.text = boxData.date
    }

    // 아이템 수 반환
    override fun getItemCount(): Int {
        return boxList.size
    }
}