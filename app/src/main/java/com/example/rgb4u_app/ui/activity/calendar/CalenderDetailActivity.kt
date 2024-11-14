package com.example.rgb4u_app.ui.activity.calendar

import android.content.Intent
import android.os.Bundle
import android.view.View
import android.widget.ImageButton
import android.widget.ImageView
import android.widget.TextView
import androidx.activity.enableEdgeToEdge
import androidx.appcompat.app.AppCompatActivity
import androidx.appcompat.widget.Toolbar
import com.example.rgb4u_app.R
import com.example.rgb4u_appclass.DiaryViewModel.Companion.diaryId
import com.google.android.material.button.MaterialButton
import com.google.android.material.chip.ChipGroup

class CalenderDetailActivity : AppCompatActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        setContentView(R.layout.activity_calender_detail)

        // Toolbar 설정
        val toolbar: Toolbar = findViewById(R.id.toolbar)
        setSupportActionBar(toolbar)

        // 기본 뒤로가기 버튼, 앱 이름 숨기기
        getSupportActionBar()?.setDisplayHomeAsUpEnabled(false)
        supportActionBar?.setDisplayShowTitleEnabled(false)

        // 툴바의 제목을 날짜로 설정
        val toolbarTitle: TextView = findViewById(R.id.toolbar_write_title)

        // Intent로부터 '월,일,요일' 정보 받기
        val selectedDate = intent.getStringExtra("SELECTED_DATE")
        selectedDate?.let {
            toolbarTitle.text = it // 툴바 제목에 날짜 정보 설정
        }

        // button_write_action2 버튼 숨기기
        val buttonWriteAction2: ImageButton = findViewById(R.id.button_write_action2)
        buttonWriteAction2.visibility = View.GONE

        // button_write_action1 클릭 리스너 추가
        val buttonWriteAction1: ImageButton = findViewById(R.id.button_write_action1)
        buttonWriteAction1.setOnClickListener {
            // CalendarHomeActivity로 이동
            val intent = Intent(this, CalendarHomeActivity::class.java)
            startActivity(intent)
            finish() // 현재 Activity를 종료
        }

        // situationDetailButton 클릭 리스너 추가
        findViewById<ImageButton>(R.id.situationDetailButton).setOnClickListener {
            // SummarySituationActivity로 이동
            val intent = Intent(this, CalenderSituationActivity::class.java)
            intent.putExtra("DIARY_ID", diaryId) // diaryId를 Intent에 추가
            startActivity(intent)
        }

        // thoughtDetailButton 클릭 리스너 추가
        findViewById<ImageButton>(R.id.thoughtDetailButton).setOnClickListener {
            // SummaryThinkActivity로 이동
            val intent = Intent(this, CalenderThinkActivity::class.java)
            intent.putExtra("DIARY_ID", diaryId) // diaryId를 Intent에 추가
            startActivity(intent)
        }

        findViewById<MaterialButton>(R.id.buttonNext).setOnClickListener{
            // SummaryThinkActivity로 이동
            val intent = Intent(this, CalenderChangedDayActivity::class.java)
            intent.putExtra("DIARY_ID", diaryId) // diaryId를 Intent에 추가
            startActivity(intent)
        }

        // SummaryMainActivity랑 유사한 것 같긴한데 복붙하면 안될 것 같아서 일단 비워둠...
        // situationTextView와 thoughtTextView 참조
        val situationTextView = findViewById<TextView>(R.id.situationTextView)
        val thoughtTextView = findViewById<TextView>(R.id.thoughtTextView)
        // val thoughtTextView2 = findViewById<TextView>(R.id.thoughtTextView2)
        val emotionIntensityTextView = findViewById<TextView>(R.id.emotionIntensityTextView)
        val emotionTypeTextView = findViewById<TextView>(R.id.emotionTypeTextView)
        val emotionIntensityImageView = findViewById<ImageView>(R.id.emotionIntensityImageView)

        // 임시 데이터 설정
        val situationTextData = "오늘은 기분이 좋았어요."
        val thoughtTextData = "긍정적인 생각을 많이 했어요."
        val thoughtTextData2 = "두 번째 생각"  // 실제 데이터가 있을 경우
        val emotionIntensityData = "강도: 높음"
        val emotionTypeData = "감정 유형: 기쁨"

        // 텍스트뷰에 데이터 설정
        situationTextView.text = situationTextData
        thoughtTextView.text = thoughtTextData
        emotionIntensityTextView.text = emotionIntensityData
        emotionTypeTextView.text = emotionTypeData


        // 칩 그룹 참조 (SummaryMainActivity의 코드를 복붙하기엔 문제가 될거같아서 일단 비워둡니다..)
        val selectedChipGroup = findViewById<ChipGroup>(R.id.SummarySelectedChipGroup)
        val emotionChipGroup = findViewById<ChipGroup>(R.id.SummaryEmotionChipGroup)
    }
}