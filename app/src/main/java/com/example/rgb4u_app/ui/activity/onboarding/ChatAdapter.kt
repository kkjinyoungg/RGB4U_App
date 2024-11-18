package com.example.rgb4u_app.ui.activity.onboarding

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import android.widget.Space
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import com.example.rgb4u_app.R

class ChatAdapter(private val chatList: List<ChatData>) : RecyclerView.Adapter<RecyclerView.ViewHolder>() {

    override fun getItemViewType(position: Int): Int {
        // "CHARACTER" 메시지와 "USER" 메시지에 따라 레이아웃 구분
        return if (chatList[position].sender == "CHARACTER") R.layout.item_chat_character else R.layout.item_chat_user
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): RecyclerView.ViewHolder {
        val view = LayoutInflater.from(parent.context).inflate(viewType, parent, false)
        return if (viewType == R.layout.item_chat_character) {
            CharacterViewHolder(view)
        } else {
            UserViewHolder(view)
        }
    }

    override fun onBindViewHolder(holder: RecyclerView.ViewHolder, position: Int) {
        val chatData = chatList[position]
        if (holder is CharacterViewHolder) {
            val isIconVisible = shouldShowIcon(position) // 아이콘 표시 여부 결정
            holder.bind(chatData, isIconVisible)
        } else if (holder is UserViewHolder) {
            holder.bind(chatData)
        }
    }

    override fun getItemCount() = chatList.size

    // CHARACTER 메시지의 ViewHolder
    class CharacterViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {
        private val textView: TextView = itemView.findViewById(R.id.chatMessage)
        private val characterIcon: ImageView = itemView.findViewById(R.id.characterIcon)
        private val characterName: TextView = itemView.findViewById(R.id.characterNameOnboarding)
        private val iconPlaceholder: Space = itemView.findViewById(R.id.iconPlaceholder)
        private val imageView: ImageView = itemView.findViewById(R.id.imageView)

        fun bind(chatData: ChatData, isIconVisible: Boolean) {
            textView.text = chatData.message

            if (isIconVisible) {
                characterIcon.visibility = View.VISIBLE
                characterName.visibility = View.VISIBLE
                iconPlaceholder.visibility = View.GONE
            } else {
                characterIcon.visibility = View.GONE
                characterName.visibility = View.GONE
                iconPlaceholder.visibility = View.VISIBLE // 공간 유지
            }

            // 이미지가 있는 경우에만 표시
            if (chatData.imageResId != null) {
                imageView.visibility = View.VISIBLE
                imageView.setImageResource(chatData.imageResId)
            } else {
                imageView.visibility = View.GONE
            }
        }
    }

    // USER 메시지의 ViewHolder
    class UserViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {
        private val textView: TextView = itemView.findViewById(R.id.chatMessage)

        fun bind(chatData: ChatData) {
            textView.text = chatData.message
        }
    }

    // 캐릭터 아이콘을 표시해야 하는 조건 확인
    private fun shouldShowIcon(position: Int): Boolean {
        // 리스트의 첫 번째 메시지인 경우
        if (position == 0 && chatList[position].sender == "CHARACTER") {
            return true
        }
        // 이전 메시지가 USER이고 현재 메시지가 CHARACTER인 경우
        if (position > 0 && chatList[position].sender == "CHARACTER" && chatList[position - 1].sender == "USER") {
            return true
        }
        return false
    }
}
