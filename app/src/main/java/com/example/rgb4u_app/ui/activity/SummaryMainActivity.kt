package com.example.rgb4u_app.ui.activity

import android.content.Intent
import android.os.Bundle
import android.widget.ImageButton
import android.widget.TextView
import androidx.appcompat.app.AppCompatActivity
import com.example.rgb4u_app.R
import java.text.SimpleDateFormat
import java.util.Calendar
import java.util.Locale

class SummaryMainActivity : AppCompatActivity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_summary_main)

        // 상단 날짜 설정
        val calendar = Calendar.getInstance()
        val sdf = SimpleDateFormat("MM월 dd일 E요일", Locale("ko", "KR"))
        findViewById<TextView>(R.id.dateTextView).text = sdf.format(calendar.time)

        // Intent로부터 데이터 수신
        val situationText = intent.getStringExtra("EXTRA_SITUATION_TEXT")
        val thoughtText = intent.getStringExtra("EXTRA_THOUGHT_TEXT")

        // 수신한 데이터를 TextView에 설정
        findViewById<TextView>(R.id.situationTextView).text = situationText
        findViewById<TextView>(R.id.thoughtTextView).text = thoughtText

        // 버튼 클릭 리스너 설정
        findViewById<ImageButton>(R.id.backButton).setOnClickListener {
            // EmotionSelectActivity로 이동
            startActivity(Intent(this, EmotionSelectActivity::class.java))
            finish()
        }

        findViewById<ImageButton>(R.id.exitButton).setOnClickListener {
            // MainActivity로 이동
            startActivity(Intent(this, MainActivity::class.java))
            finish()
        }
    }

}
