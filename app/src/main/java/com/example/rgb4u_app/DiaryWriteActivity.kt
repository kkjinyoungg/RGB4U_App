package com.example.rgb4u_app

import android.content.Intent
import android.content.res.ColorStateList
import android.graphics.Color
import android.os.Bundle
import android.widget.Button
import android.widget.ImageButton
import android.widget.TextView
import androidx.appcompat.app.AppCompatActivity
import com.google.android.material.bottomnavigation.BottomNavigationView
import com.google.android.material.chip.Chip
import com.google.android.material.chip.ChipGroup
import java.text.SimpleDateFormat
import java.util.Date
import java.util.Locale

class DiaryWriteActivity : AppCompatActivity() {
    private lateinit var bottomNavigation: BottomNavigationView
    private lateinit var btnBack: ImageButton
    private lateinit var btnComplete: Button
    private lateinit var chipGroup: ChipGroup
    private val chipList = mutableListOf<Chip>()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_write_diary)

        // 현재 날짜와 요일 가져오기
        val currentDate = SimpleDateFormat("M월 d일 E", Locale.getDefault()).format(Date())
        val textViewDateDay = findViewById<TextView>(R.id.textview_date_day)
        textViewDateDay.text = currentDate

        // 뷰 참조 가져오기
        btnBack = findViewById(R.id.btn_back)
        btnComplete = findViewById(R.id.btn_complete)
        chipGroup = findViewById(R.id.chipGroup)

        // 뒤로가기 버튼 클릭 시 메인 화면으로 이동
        btnBack.setOnClickListener {
            saveAndExit()
        }

        // 완료 버튼 클릭 시 메인 화면으로 이동
        btnComplete.setOnClickListener {
            saveAndExit()
        }

        // 감정 칩 생성 및 추가
        createAndAddChips()

        bottomNavigation = findViewById(R.id.bottom_navigation)
        bottomNavigation.setOnItemSelectedListener { item ->
            when (item.itemId) {
                R.id.navigation_home -> {
                    // "Home" 메뉴 아이템 클릭 시 실행할 코드
                    true
                }

                R.id.navigation_search -> {
                    // "Search" 메뉴 아이템 클릭 시 실행할 코드
                    true
                }

                R.id.navigation_notifications -> {
                    // "Notifications" 메뉴 아이템 클릭 시 실행할 코드
                    true
                }

                R.id.navigation_profile -> {
                    // "Profile" 메뉴 아이템 클릭 시 실행할 코드
                    true
                }

                else -> false
            }
        }
    }

    private fun saveAndExit() {
        // 일기 내용 저장 로직 추가
        startActivity(Intent(this, MainActivity::class.java))
        finish()
    }

    private fun createAndAddChips() {
        val initialChips = listOf("괴로운", "슬픔")
        initialChips.forEach { emotion ->
            createAndAddChip(emotion)
        }

        val addEmotionChip = createAndAddChip("감정 추가하기+")
        addEmotionChip.setOnClickListener {
            val newChip = createAndAddChip("새로운 감정")
            chipList.add(newChip)
            chipGroup.removeView(addEmotionChip)
            chipGroup.addView(addEmotionChip, chipList.size)
        }
    }

    private fun createAndAddChip(text: String): Chip {
        val chip = Chip(this)
        chip.text = text
        chip.isCheckable = true
        chip.chipBackgroundColor = ColorStateList.valueOf(Color.TRANSPARENT)
        chip.setTextColor(Color.BLACK)
        chip.chipStrokeColor = ColorStateList.valueOf(Color.BLACK)
        chip.chipStrokeWidth = 1f
        chipGroup.addView(chip)
        chipList.add(chip)
        return chip
    }
}
