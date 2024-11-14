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
import com.example.rgb4u_app.ui.activity.MainActivity
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

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_summary_main)

        // 상단 날짜 설정
        val calendar = Calendar.getInstance()
        val sdf = SimpleDateFormat("MM월 dd일 E요일", Locale("ko", "KR"))
        findViewById<TextView>(R.id.dateTextView).text = sdf.format(calendar.time)

        // situationTextView와 thoughtTextView 참조
        val situationTextView = findViewById<TextView>(R.id.situationTextView)
        val thoughtTextView = findViewById<TextView>(R.id.thoughtTextView)
        val emotionIntensityTextView = findViewById<TextView>(R.id.emotionIntensityTextView)
        val emotionTypeTextView = findViewById<TextView>(R.id.emotionTypeTextView)
        val emotionIntensityImageView = findViewById<ImageView>(R.id.emotionIntensityImageView)

        // 칩 그룹 참조
        val selectedChipGroup = findViewById<ChipGroup>(R.id.SummarySelectedChipGroup)
        val emotionChipGroup = findViewById<ChipGroup>(R.id.SummaryEmotionChipGroup)

        val temporaryEmotions = listOf("행복", "슬픔", "분노", "놀람") // emotionChipGroup 확인용 임시 데이터

        // diaryId, ID 수신
        val diaryId = DiaryViewModel.diaryId

        // 현재 로그인된 사용자의 UID를 가져오는 함수
        val userId = FirebaseAuth.getInstance().currentUser?.uid


        if (userId != null && diaryId != null) {
            // aiAnalysis 데이터 조회
            database = FirebaseDatabase.getInstance().getReference("users/$userId/diaries/$diaryId/aiAnalysis/firstAnalysis")
            database.addListenerForSingleValueEvent(object : ValueEventListener {
                override fun onDataChange(dataSnapshot: DataSnapshot) {
                    if (dataSnapshot.exists()) {
                        // 상황과 생각 가져오기
                        val situation = dataSnapshot.child("situation").getValue(String::class.java) ?: "상황 정보 없음"
                        val thoughts = dataSnapshot.child("thoughts").getValue(String::class.java) ?: "생각 정보 없음"

                        // TextView에 Realtime Database에서 가져온 값 설정
                        situationTextView.text = situation
                        thoughtTextView.text = thoughts

                        // emotionDegree와 emotionTypes를 userInput에서 가져오기
                        val userInputRef = FirebaseDatabase.getInstance().getReference("users/$userId/diaries/$diaryId/userInput")
                        userInputRef.addListenerForSingleValueEvent(object : ValueEventListener {
                            override fun onDataChange(userInputSnapshot: DataSnapshot) {
                                // emotionDegree에서 int와 string 가져오기
                                val emotionDegreeInt = userInputSnapshot.child("emotionDegree/int").getValue(Int::class.java) ?: 0
                                val emotionDegreeString = userInputSnapshot.child("emotionDegree/string").getValue(String::class.java) ?: "감정 강도 정보 없음"

                                // emotionTypes는 리스트 형태로 가져온다
                                val emotionTypesList = userInputSnapshot.child("emotionTypes").children.mapNotNull { it.getValue(String::class.java) }
                                val emotionTypes = emotionTypesList.joinToString(", ") // 리스트를 문자열로 변환

                                // 감정 강도와 감정 종류를 로그에 출력
                                Log.d("SummaryMainActivity", "감정 강도: $emotionDegreeInt ($emotionDegreeString), 감정 종류: $emotionTypes")

                                //요약 화면 이미지 바꾸는 코드
                                emotionIntensityImageView.setImageResource(getImageResId(emotionDegreeInt))

                                // TextView에 감정 강도 중 숫자
                                emotionIntensityTextView.text = "${emotionDegreeInt}단계"

                                //$emotionDegreeString (심했어)-> 프론트 생기면 연결예정

                                //감정 종류 연결 코드 바꿀 예정
                                //emotionTypeTextView.text = emotionTypes

                                //selectedChipGroup에 감정 추가
                                for (emotion in emotionTypesList) {
                                    val chip = Chip(this@SummaryMainActivity).apply {
                                        text = emotion
                                        isCloseIconVisible = false // 닫기 아이콘 숨기기
                                        isClickable = false // 칩 클릭 비활성화
                                        isFocusable = false // 포커스 비활성화

                                        shapeAppearanceModel = shapeAppearanceModel.toBuilder()
                                            .setAllCornerSizes(50f) // 모서리 둥글기
                                            .build()

                                        setTextColor(ContextCompat.getColor(this@SummaryMainActivity, R.color.white)) // 텍스트 색상
                                    }
                                    // 선택한 감정 추가
                                    selectedChipGroup.addView(chip)
                                }

                                // emotionChipGroup에 감정 추가
                                for (emotion in temporaryEmotions) {
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

        /* 감정 강도 이미지 리소스 ID 관찰
        diaryViewModel.emotionImageResId.observe(this) { imageResId ->
            imageResId?.let {
                // ImageView에 이미지 설정
                emotionIntensityImageView.setImageResource(it)
            }
        }*/

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
            intent.putExtra("DIARY_ID", diaryId) // diaryId를 Intent에 추가
            startActivity(intent)
        }

        // thoughtDetailButton 클릭 리스너 추가
        findViewById<ImageButton>(R.id.thoughtDetailButton).setOnClickListener {
            // SummaryThinkActivity로 이동
            val intent = Intent(this, SummaryThinkActivity::class.java)
            intent.putExtra("DIARY_ID", diaryId) // diaryId를 Intent에 추가
            startActivity(intent)
        }

        // buttonNext 클릭 리스너 추가
        findViewById<Button>(R.id.buttonNext).setOnClickListener {
            // DistortionTypeActivity로 이동
            val intent = Intent(this, DistortionTypeActivity::class.java)
            if (userId != null) intent.putExtra("USER_ID", userId)
            if (diaryId != null) intent.putExtra("DIARY_ID", diaryId)

            Log.d("SummaryMainActivity", "Navigating to DistortionTypeActivity with User ID: $userId and Diary ID: $diaryId")

            startActivity(intent)
            finish()
        }
    }


    // 감정에 따라 이미지 리소스 ID를 반환하는 함수
    private fun getImageResId(progress: Int): Int {
        return when (progress) {
            0 -> R.drawable.img_emotion_0
            1 -> R.drawable.img_emotion_1
            2 -> R.drawable.img_emotion_2
            3 -> R.drawable.img_emotion_3
            4 -> R.drawable.img_emotion_4
            else -> R.drawable.img_emotion_0 // 기본 이미지 (예외 처리)
        }
    }
}
