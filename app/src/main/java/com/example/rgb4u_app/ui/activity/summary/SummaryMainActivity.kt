package com.example.rgb4u_app.ui.activity.summary

import android.content.Intent
import android.content.res.ColorStateList
import android.graphics.Color
import android.os.Bundle
import android.util.Log
import android.widget.Button
import android.widget.ImageButton
import android.widget.ImageView
import android.widget.TextView
import androidx.activity.viewModels
import androidx.appcompat.app.AppCompatActivity
import androidx.core.content.ContextCompat
import com.example.rgb4u_app.R
import com.example.rgb4u_app.ui.activity.home.MainActivity
import com.example.rgb4u_app.ui.activity.diary.EmotionSelectActivity
import com.example.rgb4u_app.ui.activity.distortiontype.DistortionTypeActivity
import com.example.rgb4u_appclass.DiaryViewModel
import com.google.android.material.chip.Chip
import com.google.android.material.chip.ChipGroup
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.database.DataSnapshot
import com.google.firebase.database.DatabaseError
import com.google.firebase.database.DatabaseReference
import com.google.firebase.database.FirebaseDatabase
import com.google.firebase.database.ValueEventListener
import java.text.SimpleDateFormat
import java.util.Calendar
import java.util.Locale

class SummaryMainActivity : AppCompatActivity() {

    // Realtime Database 참조 선언
    private lateinit var database: DatabaseReference
    private val diaryViewModel: DiaryViewModel by viewModels()
    private lateinit var toolbarTitle: TextView  // 툴바 제목 텍스트뷰

    // 감정 타입별 상위 카테고리 매핑
    private val emotionCategoryMap = mapOf(
        "움찔하는" to "Surprise", "황당한" to "Surprise", "깜짝 놀란" to "Surprise",
        "어안이 벙벙한" to "Surprise", "아찔한" to "Surprise", "충격적인" to "Surprise",
        "걱정스러운" to "Fear", "긴장된" to "Fear", "불안한" to "Fear",
        "겁나는" to "Fear", "무서운" to "Fear", "암담한" to "Fear",
        "기운 없는" to "Sadness", "서운한" to "Sadness", "슬픈" to "Sadness",
        "눈물이 나는" to "Sadness", "우울한" to "Sadness", "비참한" to "Sadness",
        "약 오른" to "Anger", "짜증나는" to "Anger", "화난" to "Anger",
        "억울한" to "Anger", "분한" to "Anger", "끓어오르는" to "Anger",
        "정 떨어지는" to "Disgust", "불쾌한" to "Disgust", "싫은" to "Disgust",
        "모욕적인" to "Disgust", "못마땅한" to "Disgust", "미운" to "Disgust"
    )

    // Chip 배경색 가져오기 함수
    private fun getChipColor(category: String) = when (category) {
        "Surprise" -> getColorStateList(R.color.surpriseColor_dark)
        "Fear" -> getColorStateList(R.color.fearColor_dark)
        "Sadness" -> getColorStateList(R.color.sadnessColor_dark)
        "Anger" -> getColorStateList(R.color.angerColor_dark)
        "Disgust" -> getColorStateList(R.color.disgustColor_dark)
        else -> getColorStateList(R.color.defaultChipColor) // 기본 색상
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_summary_main)

        toolbarTitle = findViewById(R.id.dateTextView)
        // Intent로 전달된 toolbarTitle 텍스트 값을 가져옴
        val titleText = intent.getStringExtra("TOOLBAR_TITLE")
        val yyyymmdd = intent.getStringExtra("Date")
        Log.d("SummaryMainActivity", "Received TOOLBAR_TITLE: $titleText")
        titleText?.let {
            toolbarTitle.text = it // 툴바 제목 텍스트에 설정
        }

        // situationTextView와 thoughtTextView 참조
        val situationTextView = findViewById<TextView>(R.id.situationTextView)
        val thoughtTextView = findViewById<TextView>(R.id.thoughtTextView)
        val emotionIntensityTextView = findViewById<TextView>(R.id.emotionIntensityTextView)
        val emotionIntensityTextView02 = findViewById<TextView>(R.id.emotionIntensityTextView02)
        val emotionTypeTextView = findViewById<TextView>(R.id.emotionTypeTextView)
        val emotionIntensityImageView = findViewById<ImageView>(R.id.emotionIntensityImageView)

        // 칩 그룹 참조
        val selectedChipGroup = findViewById<ChipGroup>(R.id.SummarySelectedChipGroup)
        val emotionChipGroup = findViewById<ChipGroup>(R.id.SummaryEmotionChipGroup)


        // diaryId, ID 수신
        val diaryId = DiaryViewModel.diaryId

        // 현재 로그인된 사용자의 UID를 가져오는 함수
        val userId = FirebaseAuth.getInstance().currentUser?.uid


        if (userId != null && yyyymmdd != null) {
            // aiAnalysis 데이터 조회
            database = FirebaseDatabase.getInstance().getReference("users/$userId/diaries/$yyyymmdd/aiAnalysis/firstAnalysis")
            database.addListenerForSingleValueEvent(object : ValueEventListener {
                override fun onDataChange(dataSnapshot: DataSnapshot) {
                    if (dataSnapshot.exists()) {
                        // 상황과 생각 가져오기
                        val situation = dataSnapshot.child("situation").getValue(String::class.java) ?: "상황 정보 없음"
                        val thoughts = dataSnapshot.child("thoughts").getValue(String::class.java) ?: "생각 정보 없음"

                        // TextView에 Realtime Database에서 가져온 값 설정
                        situationTextView.text = situation

                        // thoughts를 문장부호 기준으로 나누고 포맷팅
                        val formattedThoughts = thoughts.split(Regex("(?<=[.!?])\\s*")) // 문장부호 뒤에서 나누기
                            .filter { it.isNotBlank() } // 빈 문자열 제거
                            .joinToString("\n") { "•  $it" } // 각 문장 앞에 "• " 추가하고 줄바꿈
                        thoughtTextView.text = formattedThoughts

                        // emotionDegree와 emotionTypes를 userInput에서 가져오기
                        val userInputRef = FirebaseDatabase.getInstance().getReference("users/$userId/diaries/$yyyymmdd/userInput")
                        userInputRef.addListenerForSingleValueEvent(object : ValueEventListener {
                            override fun onDataChange(userInputSnapshot: DataSnapshot) {
                                // emotionDegree에서 int와 string 가져오기
                                val emotionDegreeInt = userInputSnapshot.child("emotionDegree/int").getValue(Int::class.java) ?: 2
                                val emotionDegreeString = userInputSnapshot.child("emotionDegree/string").getValue(String::class.java) ?: "보통이었어"
                                val emotionDegreeImage = userInputSnapshot.child("emotionDegree/emotionimg").getValue(String::class.java) ?: "img_emotion_2"


                                // emotionTypes는 리스트 형태로 가져온다
                                val emotionTypesList = userInputSnapshot.child("emotionTypes").children.mapNotNull { it.getValue(String::class.java) }
                                val emotionTypes = emotionTypesList.joinToString(", ") // 리스트를 문자열로 변환

                                // AI 분석 감정용 emotion 리스트 가져오기 ()
                                val emotionsList = dataSnapshot.child("emotion").children.mapNotNull { it.getValue(String::class.java) }

                                // 감정 강도와 감정 종류를 로그에 출력
                                Log.d("SummaryMainActivity", "감정 강도: $emotionDegreeInt ($emotionDegreeString), 감정 종류: $emotionTypes")

                                //요약 화면 이미지 바꾸는 코드
                                emotionIntensityImageView.setImageResource(getEmotionImageResource(emotionDegreeImage))

                                // TextView에 감정 강도 중 숫자
                                emotionIntensityTextView.text = "${emotionDegreeInt + 1}단계"

                                //$emotionDegreeString (심했어)-> 프론트 생기면 연결예정
                                emotionIntensityTextView02.text = "$emotionDegreeString"

                                //감정 종류 연결 코드 바꿀 예정
                                //emotionTypeTextView.text = emotionTypes

                                //selectedChipGroup에 감정 추가
                                for (emotion in emotionTypesList) {
                                    val category = emotionCategoryMap[emotion] ?: "default" // 감정의 상위 카테고리 찾기
                                    val chip = Chip(this@SummaryMainActivity).apply {
                                        text = emotion
                                        isCloseIconVisible = false // 닫기 아이콘 숨기기
                                        isClickable = false // 칩 클릭 비활성화
                                        isFocusable = false // 포커스 비활성화

                                        shapeAppearanceModel = shapeAppearanceModel.toBuilder()
                                            .setAllCornerSizes(50f) // 모서리 둥글기
                                            .build()

                                        setTextColor(ContextCompat.getColor(this@SummaryMainActivity, R.color.white)) // 텍스트 색상
                                        chipBackgroundColor = getChipColor(category) // 배경색 설정
                                    }
                                    // 선택한 감정 추가
                                    selectedChipGroup.addView(chip)
                                }

                                // emotionChipGroup에 감정 추가
                                for (emotion in emotionsList) {
                                    val chip = layoutInflater.inflate(R.layout.summary_single_chip, emotionChipGroup, false) as Chip
                                    chip.text = emotion
                                    chip.isCloseIconVisible = false // 닫기 아이콘 숨기기
                                    chip.isClickable = false // 칩 클릭 비활성화
                                    chip.isFocusable = false // 포커스 비활성화

                                    emotionChipGroup.addView(chip) // ChipGroup에 추가
                                }
//                                //emotionChipGroup에 감정 추가, 테두리 투명 배경 적용 X... 이유 못찾음
//                                for (emotion in temporaryEmotions) {
//                                    val chip = Chip(this@SummaryMainActivity).apply {
//
//                                        text = emotion
//                                        isCloseIconVisible = false
//                                        isClickable = false
//                                        isFocusable = false
//
//                                        // Chip 속성 설정
//                                        shapeAppearanceModel = shapeAppearanceModel.toBuilder()
//                                            .setAllCornerSizes(50f)
//                                            .build()
//
//                                        setTextColor(ContextCompat.getColor(this@SummaryMainActivity, R.color.white)) // 텍스트 색상
//                                        setChipStrokeColorResource(R.color.white) // 테두리 색상
//                                        chipStrokeWidth = 2f // 테두리 두께
//                                        setChipBackgroundColor(ColorStateList.valueOf(Color.TRANSPARENT)) // 배경색
//
//                                    }
//
//                                    // ChipGroup에 추가
//                                    findViewById<ChipGroup>(R.id.SummaryEmotionChipGroup).addView(chip)
//                                }


                            }

                            override fun onCancelled(databaseError: DatabaseError) {
                                // 오류 처리
                                Log.e("SummaryMainActivity", "emotionDegree와 emotionTypes를 불러오는 데 실패했습니다: ${databaseError.message}")
                            }
                        })
                    } else {
                        // 데이터가 존재하지 않는 경우
                        situationTextView.text = "데이터가 존재하지 않습니다"
                        thoughtTextView.text = "데이터가 존재하지 않습니다"
                    }
                }

                override fun onCancelled(databaseError: DatabaseError) {
                    // 오류 처리
                    situationTextView.text = "오류 발생: ${databaseError.message}"
                    thoughtTextView.text = "오류 발생: ${databaseError.message}"
                }
            })
        } else {
            // diaryId가 null인 경우 처리
            situationTextView.text = "일기 ID를 찾을 수 없음"
            thoughtTextView.text = "일기 ID를 찾을 수 없음"
        }

        // Back 버튼 클릭 리스너 설정
        findViewById<ImageButton>(R.id.backButton).setOnClickListener {
            // EmotionSelectActivity로 이동
            startActivity(Intent(this, EmotionSelectActivity::class.java))
            finish()
        }

        // Exit 버튼 클릭 리스너 설정
        findViewById<ImageButton>(R.id.exitButton).setOnClickListener {
            // MainActivity로 이동
            startActivity(Intent(this, MainActivity::class.java))
            finish()
        }

        // situationDetailButton 클릭 리스너 추가
        findViewById<ImageButton>(R.id.situationDetailButton).setOnClickListener {
            // SummarySituationActivity로 이동
            val intent = Intent(this, SummarySituationActivity::class.java)
            intent.putExtra("Date", yyyymmdd) // diaryId를 Intent에 추가
            startActivity(intent)
        }

        // thoughtDetailButton 클릭 리스너 추가
        findViewById<ImageButton>(R.id.thoughtDetailButton).setOnClickListener {
            // SummaryThinkActivity로 이동
            val intent = Intent(this, SummaryThinkActivity::class.java)
            intent.putExtra("Date", yyyymmdd) // diaryId를 Intent에 추가
            startActivity(intent)
        }

        // buttonNext 클릭 리스너 추가
        findViewById<Button>(R.id.buttonNext).setOnClickListener {
            // DistortionTypeActivity로 이동
            val intent = Intent(this, DistortionTypeActivity::class.java)
            if (userId != null) intent.putExtra("USER_ID", userId)
            if (yyyymmdd != null) intent.putExtra("Date", yyyymmdd)

            Log.d("SummaryMainActivity", "Navigating to DistortionTypeActivity with User ID: $userId and Diary ID: $yyyymmdd")

            startActivity(intent)
            finish()
        }
    }

    // emotionimg 값에 따라 적절한 이미지 리소스를 반환하는 함수
    private fun getEmotionImageResource(emotionImg: String): Int {
        return when (emotionImg) {
            "img_emotion_0" -> R.drawable.img_emotion_0
            "img_emotion_1" -> R.drawable.img_emotion_1
            "img_emotion_2" -> R.drawable.img_emotion_2
            "img_emotion_3" -> R.drawable.img_emotion_3
            else -> R.drawable.img_emotion_4 // 4번 이미지 설정
        }
    }
}
