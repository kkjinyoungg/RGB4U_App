package com.example.rgb4u_app

import android.app.Activity
import android.content.Intent
import android.os.Bundle
import android.widget.Button
import android.widget.ImageButton
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import com.google.android.material.chip.Chip
import com.google.android.material.chip.ChipGroup

class EmotionSelectActivity : AppCompatActivity() {
    private lateinit var chipGroup: ChipGroup
    private val selectedEmotions = mutableSetOf<String>()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_emotion_select)

        chipGroup = findViewById<ChipGroup>(R.id.chipGroup)

        // 30개의 감정 칩 버튼 생성 및 추가
        createAndAddChips()

        // 완료 버튼 클릭 시 선택된 감정 정보 전달
        findViewById<Button>(R.id.btn_complete).setOnClickListener {
            val intent = Intent().apply {
                putExtra("selectedEmotions", ArrayList(selectedEmotions))
            }
            setResult(Activity.RESULT_OK, intent)
            finish()
        }

        // 뒤로 가기 버튼 클릭 시 DiaryWriteActivity로 이동
        val btnBackEmotion = findViewById<ImageButton>(R.id.btn_back_emotion)
        btnBackEmotion.setOnClickListener {
            // DiaryWriteActivity로 이동
            val intent = Intent(this, DiaryWriteActivity::class.java)
            startActivity(intent)
        }

// 완료 버튼 클릭 시 DiaryWriteActivity로 이동
        val btnCompleteEmotion = findViewById<Button>(R.id.btn_complete)
        btnCompleteEmotion.setOnClickListener {
            // DiaryWriteActivity로 이동
            val intent = Intent(this, DiaryWriteActivity::class.java)
            startActivity(intent)
        }
    }

    private fun createAndAddChips() {
        val emotions = listOf(
            "행복한", "기쁜", "즐거운", "사랑스러운", "설레는", "감사한", "편안한",
            "화난", "짜증나는", "슬픈", "우울한", "외로운", "걱정되는", "두려운",
            "피곤한", "힘든", "스트레스받는", "당황스러운", "불안한", "죄책감 드는"
        )

        emotions.forEach { emotion ->
            val chip = Chip(this).apply {
                text = emotion
                isCheckable = true
                var isChecked = false // 변수 선언을 var로 변경
                setOnCheckedChangeListener { _, checked ->
                    isChecked = checked // 변수 값 변경
                    if (isChecked) {
                        selectedEmotions.add(emotion)
                        if (selectedEmotions.size > 2) {
                            isChecked = false // 변수 값 변경
                            selectedEmotions.remove(emotion)
                            // 최대 2개까지만 선택 가능하도록 처리
                            Toast.makeText(this@EmotionSelectActivity, "최대 2개까지 선택 가능합니다.", Toast.LENGTH_SHORT).show()
                        }
                    } else {
                        selectedEmotions.remove(emotion)
                    }
                }
            }
            chipGroup.addView(chip)
        }
    }
}

