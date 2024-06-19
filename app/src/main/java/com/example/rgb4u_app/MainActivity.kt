package com.example.rgb4u_app


import android.content.Intent
import android.os.Bundle
import android.widget.Button
import android.widget.TextView
import androidx.appcompat.app.AppCompatActivity
import java.util.Calendar

class MainActivity : AppCompatActivity() {
    private lateinit var textviewTodayDate: TextView
    private lateinit var btnWriteDiary: Button
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)

        // 뷰 바인딩
        textviewTodayDate = findViewById(R.id.textview_today_date)
        btnWriteDiary = findViewById(R.id.btn_write_diary)

        // 오늘의 날짜 표시
        val todayDate = getTodayDateString()
        textviewTodayDate.text = todayDate

        // "일기 쓰기" 버튼 클릭 리스너 설정
        btnWriteDiary.setOnClickListener {
            // 일기 작성 화면으로 이동하는 코드 작성
            val intent = Intent(this, DiaryWriteActivity::class.java)
            startActivity(intent)
        }
    }

    private fun getTodayDateString(): String {
        val calendar = Calendar.getInstance()
        val month = calendar.get(Calendar.MONTH) + 1
        val day = calendar.get(Calendar.DAY_OF_MONTH)
        val dayOfWeek = when (calendar.get(Calendar.DAY_OF_WEEK)) {
            Calendar.SUNDAY -> "일요일"
            Calendar.MONDAY -> "월요일"
            Calendar.TUESDAY -> "화요일"
            Calendar.WEDNESDAY -> "수요일"
            Calendar.THURSDAY -> "목요일"
            Calendar.FRIDAY -> "금요일"
            Calendar.SATURDAY -> "토요일"
            else -> ""
        }
        return String.format("%d월 %d일 %s", month, day, dayOfWeek)
    }
}
