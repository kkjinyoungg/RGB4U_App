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
    private lateinit var stepName: TextView
    private lateinit var dateTextView: TextView

    val stepNames = arrayOf("단계 1", "단계 2", "단계 3", "단계 4", "단계 5")

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_emotion_strength)

        seekBar = findViewById(R.id.seekBar)
        stepName = findViewById(R.id.stepName)

        // SeekBar의 최대값을 4로 설정
        seekBar.max = 4

        seekBar.setOnSeekBarChangeListener(object : SeekBar.OnSeekBarChangeListener {
            override fun onProgressChanged(seekBar: SeekBar?, progress: Int, fromUser: Boolean) {
                // 체크 포인트에 맞춰 단계 이름 변경
                stepName.text = stepNames[progress]
            }

            override fun onStartTrackingTouch(seekBar: SeekBar?) {}

            override fun onStopTrackingTouch(seekBar: SeekBar?) {}
        })

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
