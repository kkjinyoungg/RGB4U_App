package com.example.rgb4u_app

import android.content.Intent
import android.os.Bundle
import android.widget.ImageButton
import android.widget.SeekBar
import android.widget.TextView
import androidx.appcompat.app.AppCompatActivity
import java.text.SimpleDateFormat
import java.util.Date
import java.util.Locale

class EmotionStrengthActivity : AppCompatActivity() {
    private lateinit var seekBar: SeekBar
    private lateinit var dateTextView: TextView

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_emotion_strength)

        // SeekBar 초기화
        seekBar = findViewById(R.id.seekBar)

        // SeekBar의 최대값을 4로 설정
        seekBar.max = 4

        // dateTextView 초기화
        dateTextView = findViewById(R.id.dateTextView)

        // 현재 날짜 가져오기
        val currentDate = getCurrentDate()

        // 현재 날짜를 TextView에 설정하기
        dateTextView.text = currentDate

        // 뒤로가기 버튼 클릭 리스너
        findViewById<ImageButton>(R.id.backButton).setOnClickListener {
            val intent = Intent(this, EmotionSelectActivity::class.java)
            startActivity(intent)
        }

        // 나가기 버튼 클릭 리스너
        findViewById<ImageButton>(R.id.exitButton).setOnClickListener {
            val intent = Intent(this, MainActivity::class.java)
            startActivity(intent)
            finish() // 현재 액티비티 종료
        }
    }

    private fun getCurrentDate(): String {
        val dateFormat = SimpleDateFormat("MM월 dd일 E요일", Locale("ko", "KR"))
        return dateFormat.format(Date())
    }
}
