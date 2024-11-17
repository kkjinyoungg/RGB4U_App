package com.example.rgb4u_app.ui.activity.analysis

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageButton
import android.widget.ImageView
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import com.example.rgb4u_app.R

// CardItem에 대한 어댑터
class CardAdapter(private val cardList: List<CardItem>, private val onCardClick: (CardItem) -> Unit) :
    RecyclerView.Adapter<CardAdapter.CardViewHolder>() {

    // ViewHolder 클래스 정의
    inner class CardViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {
        val imageView: ImageView = itemView.findViewById(R.id.image_type)
        val textView: TextView = itemView.findViewById(R.id.tv_type_name)
        val detailButton: ImageButton = itemView.findViewById(R.id.btn_detail)
    }

    // ViewHolder 생성
    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): CardViewHolder {
        val itemView = LayoutInflater.from(parent.context)
            .inflate(R.layout.item_card_view, parent, false)
        return CardViewHolder(itemView)
    }

    // 데이터 바인딩
    override fun onBindViewHolder(holder: CardViewHolder, position: Int) {
        val cardItem = cardList[position]
        holder.imageView.setImageResource(cardItem.imageResourceId)
        holder.textView.text = cardItem.typeName

        // 상세 보기로 이동하는 버튼 클릭 리스너
        holder.detailButton.setOnClickListener {
            onCardClick(cardItem)
        }
    }

    // 아이템 수 반환
    override fun getItemCount(): Int {
        return cardList.size
    }
}

