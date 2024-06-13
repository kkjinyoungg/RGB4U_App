package com.example.rgb4u_app


import android.content.Intent
import android.os.Bundle
import android.widget.ImageView
import android.widget.TextView
import androidx.appcompat.app.AppCompatActivity
import androidx.appcompat.widget.AppCompatImageButton
import java.util.Calendar

class MainActivity : AppCompatActivity() {
    private lateinit var prevDateButton: AppCompatImageButton // 타입 변경
    private lateinit var nextDateButton: AppCompatImageButton // 타입 변경
    private lateinit var addChallengeButton: AppCompatImageButton // 타입 변경
    private lateinit var dateTextView: TextView
    private lateinit var characterImageView: ImageView // 이미지 뷰 추가

    private var currentYear: Int = 0
    private var currentMonth: Int = 0
    private var currentDay: Int = 0
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)

        prevDateButton = findViewById(R.id.prev_date_button)
        nextDateButton = findViewById(R.id.next_date_button)
        addChallengeButton = findViewById(R.id.add_Challenge_Button)
        dateTextView = findViewById(R.id.date_text_view)
        characterImageView = findViewById(R.id.character_image_view) // 이미지 뷰 참조

        // 기기의 현재 날짜 가져오기
        val calendar = Calendar.getInstance()
        currentYear = calendar.get(Calendar.YEAR)
        currentMonth = calendar.get(Calendar.MONTH) + 1 // 월은 0부터 시작하므로 +1
        currentDay = calendar.get(Calendar.DAY_OF_MONTH)

        setSelectedDate(currentYear, currentMonth, currentDay)

        // 캐릭터 이미지 설정
        characterImageView.setImageResource(R.drawable.your_character_image) // 실제 이미지로 변경

        prevDateButton.setOnClickListener {
            adjustDate(false)
        }

        nextDateButton.setOnClickListener {
            adjustDate(true)
        }

        addChallengeButton.setOnClickListener { // +버튼 클릭 시 실행
            val intent = Intent(this, PlanActivity::class.java) // PlanActivity로 이동
            intent.putExtra("year", currentYear)
            intent.putExtra("month", currentMonth)
            intent.putExtra("day", currentDay)
            startActivity(intent)
        }
    }

    private fun adjustDate(isNext: Boolean) {
        val calendar = Calendar.getInstance()
        calendar.set(Calendar.YEAR, currentYear)
        calendar.set(Calendar.MONTH, currentMonth - 1) // 월은 0부터 시작하므로 -1
        calendar.set(Calendar.DAY_OF_MONTH, currentDay)

        if (isNext) {
            calendar.add(Calendar.DAY_OF_MONTH, 1)
        } else {
            calendar.add(Calendar.DAY_OF_MONTH, -1)
        }

        currentYear = calendar.get(Calendar.YEAR)
        currentMonth = calendar.get(Calendar.MONTH) + 1
        currentDay = calendar.get(Calendar.DAY_OF_MONTH)

        setSelectedDate(currentYear, currentMonth, currentDay)
    }

    private fun setSelectedDate(year: Int, month: Int, day: Int) {
        dateTextView.text = String.format("%d년 %d월 %d일", year, month, day)
    }
}
