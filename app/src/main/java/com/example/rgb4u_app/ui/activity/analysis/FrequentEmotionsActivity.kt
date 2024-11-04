package com.example.rgb4u_app.ui.activity.analysis

import android.content.Intent
import android.content.res.ColorStateList
import android.graphics.Color
import android.os.Bundle
import android.util.TypedValue
import android.view.View
import android.widget.ImageButton
import android.widget.LinearLayout
import android.widget.TextView
import androidx.appcompat.app.AppCompatActivity
import androidx.appcompat.widget.Toolbar
import androidx.cardview.widget.CardView
import com.example.rgb4u_app.R
import com.google.android.material.chip.Chip
import com.google.android.material.chip.ChipGroup
import com.google.android.material.shape.ShapeAppearanceModel

class FrequentEmotionsActivity : AppCompatActivity() {

    // View 선언
    private lateinit var surpriseCard: CardView
    private lateinit var surpriseChipGroup: ChipGroup
    private lateinit var fearCard: CardView
    private lateinit var fearChipGroup: ChipGroup
    private lateinit var sadnessCard: CardView
    private lateinit var sadnessChipGroup: ChipGroup
    private lateinit var angerCard: CardView
    private lateinit var angerChipGroup: ChipGroup
    private lateinit var disgustCard: CardView
    private lateinit var disgustChipGroup: ChipGroup

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_frequent_emotions)

        // findViewById로 각 View를 연결
        surpriseCard = findViewById(R.id.surpriseCard)
        surpriseChipGroup = findViewById(R.id.surpriseChipGroup)
        fearCard = findViewById(R.id.fearCard)
        fearChipGroup = findViewById(R.id.fearChipGroup)
        sadnessCard = findViewById(R.id.sadnessCard)
        sadnessChipGroup = findViewById(R.id.sadnessChipGroup)
        angerCard = findViewById(R.id.angerCard)
        angerChipGroup = findViewById(R.id.angerChipGroup)
        disgustCard = findViewById(R.id.disgustCard)
        disgustChipGroup = findViewById(R.id.disgustChipGroup)


        // 툴바 설정
        val toolbar = findViewById<Toolbar>(R.id.toolbar_frequent)
        setSupportActionBar(toolbar)

        // 기본 뒤로가기 버튼, 앱 이름 숨기기
        getSupportActionBar()?.setDisplayHomeAsUpEnabled(false)
        supportActionBar?.setDisplayShowTitleEnabled(false)

        // 툴바 타이틀 설정
        val toolbarTitle = findViewById<TextView>(R.id.toolbar_base1_title)
        toolbarTitle.text = "내가 자주 선택한 감정"

        // button_base1_action1: 뒤로가기 버튼 설정
        val backButton = findViewById<ImageButton>(R.id.button_base1_action1)
        backButton.setOnClickListener {
            val intent = Intent(this, AnalysisActivity::class.java)
            startActivity(intent)
            finish() // 현재 액티비티 종료
        }

        // button_base1_action2: 버튼 숨기기
        val actionButton2 = findViewById<ImageButton>(R.id.button_base1_action2)
        actionButton2.visibility = View.GONE

        // 더미 데이터 예시
        val emotionsData = mapOf(
            "surprise" to listOf("움찔하는 32", "어안이 벙벙한 14", "충격적인 8"),
            "fear" to listOf("걱정스러운 17", "긴장된 3"),
            "sadness" to listOf("눈물이 나는 9", "우울한 4", "슬픈 2"),
            "anger" to listOf("화난 9", "짜증난 4"),
            "disgust" to listOf("싫은 21")
        )

        updateEmotionsView(emotionsData)
    }

    private fun updateEmotionsView(emotionsData: Map<String, List<String>>) {
        // 놀람 카테고리
        if (emotionsData["surprise"].isNullOrEmpty()) {
            surpriseCard.visibility = View.GONE
        } else {
            surpriseCard.visibility = View.VISIBLE
            updateChipGroup(surpriseChipGroup, emotionsData["surprise"]!!, "surprise")
        }

        // 두려움 카테고리
        if (emotionsData["fear"].isNullOrEmpty()) {
            fearCard.visibility = View.GONE
        } else {
            fearCard.visibility = View.VISIBLE
            updateChipGroup(fearChipGroup, emotionsData["fear"]!!, "fear")
        }

        // 슬픔 카테고리
        if (emotionsData["sadness"].isNullOrEmpty()) {
            sadnessCard.visibility = View.GONE
        } else {
            sadnessCard.visibility = View.VISIBLE
            updateChipGroup(sadnessChipGroup, emotionsData["sadness"]!!, "sadness")
        }

        // 분노 카테고리
        if (emotionsData["anger"].isNullOrEmpty()) {
            angerCard.visibility = View.GONE
        } else {
            angerCard.visibility = View.VISIBLE
            updateChipGroup(angerChipGroup, emotionsData["anger"]!!, "anger")
        }

        // 혐오 카테고리
        if (emotionsData["disgust"].isNullOrEmpty()) {
            disgustCard.visibility = View.GONE
        } else {
            disgustCard.visibility = View.VISIBLE
            updateChipGroup(disgustChipGroup, emotionsData["disgust"]!!, "disgust")
        }
    }

    private fun updateChipGroup(chipGroup: ChipGroup, emotionList: List<String>, emotionType: String) {
        chipGroup.removeAllViews() // 기존 Chip들을 모두 제거

        // 감정 카테고리에 따른 텍스트 색상 설정
        val textColor = when (emotionType) {
            "surprise" -> Color.parseColor("#33A080")
            "fear" -> Color.parseColor("#339EB3")
            "sadness" -> Color.parseColor("#2795DD")
            "anger" -> Color.parseColor("#C771C7")
            "disgust" -> Color.parseColor("#7461D1")
            else -> Color.BLACK // 기본 색상
        }

        for (emotion in emotionList) {
            val chip = Chip(this).apply {
                text = emotion
                setTextAppearance(R.style.chipText) // 폰트 적용

                // 모서리를 둥글게 설정 (50dp로 설정)
                shapeAppearanceModel = ShapeAppearanceModel.builder()
                    .setAllCornerSizes(50f) // 둥근 모양 설정
                    .build()

                // 배경색을 흰색으로 설정
                chipBackgroundColor = ColorStateList.valueOf(Color.WHITE)

                // 텍스트 색상 설정
                setTextColor(textColor)

                // 칩의 클릭 불가능, 선택 불가능 설정
                isClickable = false
                isCheckable = false

                // 테두리 제거
                chipStrokeWidth = 0f

                // Chip의 높이 설정
                val params = LinearLayout.LayoutParams(
                    LinearLayout.LayoutParams.WRAP_CONTENT, // 너비는 내용에 맞춤
                    TypedValue.applyDimension(TypedValue.COMPLEX_UNIT_DIP, 52f, resources.displayMetrics).toInt()
                )// 높이는 52dp
                layoutParams = params // 설정한 LayoutParams 적용
            }
            chipGroup.addView(chip)
        }
    }
}
