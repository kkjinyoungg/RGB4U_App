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

    companion object {
        private const val VIEW_TYPE_STARLINE = 0
        private const val VIEW_TYPE_CHARACTER = 1
        private const val VIEW_TYPE_USER = 2
    }

    override fun getItemViewType(position: Int): Int {
        return when {
            position == 0 -> VIEW_TYPE_STARLINE
            chatList[position].sender == "CHARACTER" -> VIEW_TYPE_CHARACTER
            else -> VIEW_TYPE_USER
        }
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): RecyclerView.ViewHolder {
        val inflater = LayoutInflater.from(parent.context)
        return when (viewType) {
            VIEW_TYPE_STARLINE -> {
                val view = inflater.inflate(R.layout.item_starline, parent, false)
                StarlineViewHolder(view)
            }
            VIEW_TYPE_CHARACTER -> {
                val view = inflater.inflate(R.layout.item_chat_character, parent, false)
                CharacterViewHolder(view)
            }
            else -> {
                val view = inflater.inflate(R.layout.item_chat_user, parent, false)
                UserViewHolder(view)
            }
        }
    }

    override fun onBindViewHolder(holder: RecyclerView.ViewHolder, position: Int) {
        if (holder is StarlineViewHolder) {
            holder.bind("모아님이 입장하셨습니다") // 스타라인 메시지
        } else {
            val chatData = chatList[position]
            if (holder is CharacterViewHolder) {
                val isIconVisible = shouldShowIcon(position)
                holder.bind(chatData, isIconVisible)
            } else if (holder is UserViewHolder) {
                holder.bind(chatData)
            }
        }
    }

    override fun getItemCount() = chatList.size

    class StarlineViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {
        private val starlineTextView: TextView = itemView.findViewById(R.id.startline)

        fun bind(message: String) {
            starlineTextView.text = message
        }
    }

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
                iconPlaceholder.visibility = View.VISIBLE
            }

            if (chatData.imageResId != null) {
                imageView.visibility = View.VISIBLE
                imageView.setImageResource(chatData.imageResId)
            } else {
                imageView.visibility = View.GONE
            }
        }
    }

    class UserViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {
        private val textView: TextView = itemView.findViewById(R.id.chatMessage)

        fun bind(chatData: ChatData) {
            textView.text = chatData.message
        }
    }

    private fun shouldShowIcon(position: Int): Boolean {
        if (position == 1 && chatList[position].sender == "CHARACTER") {
            return true
        }
        if (position > 1 && chatList[position].sender == "CHARACTER" && chatList[position - 1].sender == "USER") {
            return true
        }
        return false
    }
}
