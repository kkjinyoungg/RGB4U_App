package com.example.rgb4u_app

import android.content.Context
import android.content.Intent
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Button
import android.widget.ImageView
import android.widget.LinearLayout
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import com.example.rgb4u_app.ui.activity.home.MainActivity

class ChatAdapter(val chatList: MutableList<String>, private val context: Context) : RecyclerView.Adapter<ChatAdapter.ChatViewHolder>() {

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ChatViewHolder {
        val view = LayoutInflater.from(parent.context).inflate(R.layout.item_message, parent, false)
        return ChatViewHolder(view)
    }

    override fun onBindViewHolder(holder: ChatViewHolder, position: Int) {
        holder.chatText.text = chatList[position]

        // 캐릭터 이미지 설정 (예: drawable 리소스)
        holder.characterImage.setImageResource(R.drawable.ic_character) // 캐릭터 이미지 리소스

        // 마지막 대화에 버튼 보이기
        if (position == chatList.size - 1) {
            holder.buttonLayout.visibility = View.VISIBLE
            holder.showExamplesButton.setOnClickListener {
                // 예시 더보기 버튼 클릭 시 동작
                chatList.add("예시 대화 내용") // 예시 대화 추가
                notifyItemInserted(chatList.size - 1)
            }

            holder.exitButton.setOnClickListener {
                // 나가기 버튼 클릭 시 홈 화면으로 이동
                val intent = Intent(context, MainActivity::class.java)
                context.startActivity(intent)
            }
        } else {
            holder.buttonLayout.visibility = View.GONE
        }
    }

    override fun getItemCount(): Int = chatList.size

    inner class ChatViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {
        val characterImage: ImageView = itemView.findViewById(R.id.characterImage) // ImageView 추가
        val chatText: TextView = itemView.findViewById(R.id.messageText)
        val buttonLayout: LinearLayout = itemView.findViewById(R.id.buttonLayout)
        val showExamplesButton: Button = itemView.findViewById(R.id.showExamplesButton)
        val exitButton: Button = itemView.findViewById(R.id.exitButton)
    }
}
