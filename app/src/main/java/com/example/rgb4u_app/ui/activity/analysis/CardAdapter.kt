package com.example.rgb4u.ver1_app.ui.activity.analysis

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageButton
import android.widget.ImageView
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import com.example.rgb4u.ver1_app.R
import android.util.Log

// CardItem에 대한 어댑터
class CardAdapter(
    private var cardList: List<CardItem>,
    private var formattedDate2: String,
    private val onCardClickListener: (CardItem, String) -> Unit) :
    RecyclerView.Adapter<CardAdapter.CardViewHolder>() {

    // ViewHolder 클래스 정의zz
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

    // formattedDate2 업데이트 메소드
    fun updateFormattedDate(newDate: String) {
        formattedDate2 = newDate
        notifyDataSetChanged()  // 데이터 변경 시 호출하여 UI 갱신
    }

    // 데이터 바인딩
    override fun onBindViewHolder(holder: CardViewHolder, position: Int) {
        val cardItem = cardList[position]
        holder.imageView.setImageResource(cardItem.imageResourceId)
        holder.textView.text = cardItem.typeName
        // formattedDate2를 사용하여 클릭 이벤트 전달
        holder.itemView.setOnClickListener {
            onCardClickListener(cardItem, formattedDate2) // formattedDate2 전달
            Log.d("CardClick", "Card clicked! Type: ${cardItem.typeName}, Date: $formattedDate2")
        }
    }

    // 아이템 수 반환
    override fun getItemCount(): Int {
        return cardList.size
    }

    fun updateCardData(newCardList: List<CardItem>) {
        cardList = newCardList
        notifyDataSetChanged() // 데이터가 변경되었음을 알리고 UI를 갱신
    }
}

