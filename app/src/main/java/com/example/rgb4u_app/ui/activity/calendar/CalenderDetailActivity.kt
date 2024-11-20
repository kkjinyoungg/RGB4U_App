package com.example.rgb4u_app.ui.activity.calendar

import android.content.Intent
import android.os.Bundle
import android.util.Log
import android.view.View
import android.widget.ImageButton
import android.widget.ImageView
import android.widget.TextView
import androidx.activity.viewModels
import androidx.appcompat.app.AppCompatActivity
import androidx.appcompat.widget.Toolbar
import com.example.rgb4u_app.R
import com.example.rgb4u_appclass.DiaryViewModel
import com.google.android.material.button.MaterialButton
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.database.DataSnapshot
import com.google.firebase.database.DatabaseError
import com.google.firebase.database.DatabaseReference
import com.google.firebase.database.FirebaseDatabase
import com.google.firebase.database.ValueEventListener

class CalenderDetailActivity : AppCompatActivity() {

    private lateinit var database: DatabaseReference
    private val diaryViewModel: DiaryViewModel by viewModels()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_calender_detail)

        // Toolbar 설정
        val toolbar: Toolbar = findViewById(R.id.toolbar)
        setSupportActionBar(toolbar)

        // 기본 뒤로가기 버튼, 앱 이름 숨기기
        supportActionBar?.setDisplayHomeAsUpEnabled(false)
        supportActionBar?.setDisplayShowTitleEnabled(false)

        // 툴바의 제목을 날짜로 설정
        val toolbarTitle: TextView = findViewById(R.id.toolbar_write_title)

        // Intent로부터 날짜 정보 받기
        val selectedDate = intent.getStringExtra("SELECTED_DATE")
        selectedDate?.let {
            toolbarTitle.text = it // 툴바 제목에 날짜 정보 설정
        }
        val datefordb = intent.getStringExtra("SELECTED_DATE_FOR_DB")
        Log.d("CalenderDetailActivity", "datefordb: $datefordb")

        // 현재 로그인된 사용자의 UID를 가져오는 함수
        val userId = FirebaseAuth.getInstance().currentUser?.uid

        // situationTextView와 thoughtTextView 참조
        val situationTextView = findViewById<TextView>(R.id.situationTextView)
        val thoughtTextView = findViewById<TextView>(R.id.thoughtTextView)
        val emotionIntensityTextView = findViewById<TextView>(R.id.emotionIntensityTextView)
        val emotionTypeTextView = findViewById<TextView>(R.id.emotionTypeTextView)
        val emotionIntensityImageView = findViewById<ImageView>(R.id.emotionIntensityImageView)

        // Firebase로부터 데이터 가져오기
        if (userId != null && datefordb != null) {
            // aiAnalysis 데이터 조회
            database = FirebaseDatabase.getInstance().getReference("users/$userId/diaries/$datefordb/aiAnalysis/firstAnalysis")
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
                        val userInputRef = FirebaseDatabase.getInstance().getReference("users/$userId/diaries/$datefordb/userInput")
                        userInputRef.addListenerForSingleValueEvent(object : ValueEventListener {
                            override fun onDataChange(userInputSnapshot: DataSnapshot) {
                                // emotionDegree에서 int와 string 가져오기
                                val emotionDegreeInt = userInputSnapshot.child("emotionDegree/int").getValue(Int::class.java) ?: 2
                                val emotionDegreeString = userInputSnapshot.child("emotionDegree/string").getValue(String::class.java) ?: "보통이었어"
                                val emotionDegreeImage = userInputSnapshot.child("emotionDegree/emotionimg").getValue(String::class.java) ?: "img_emotion_2"

                                // TextView에 감정 강도 설정
                                emotionIntensityTextView.text = "${emotionDegreeInt + 1}단계"
                                emotionTypeTextView.text = emotionDegreeString

                                // 감정 강도에 따른 이미지 설정
                                emotionIntensityImageView.setImageResource(getEmotionImageResource(emotionDegreeImage))
                            }

                            override fun onCancelled(databaseError: DatabaseError) {
                                // 오류 처리
                                Log.e("CalenderDetailActivity", "emotionDegree와 emotionTypes를 불러오는 데 실패했습니다: ${databaseError.message}")
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
            // userId가 null인 경우 처리
            situationTextView.text = "사용자 ID를 찾을 수 없음"
            thoughtTextView.text = "사용자 ID를 찾을 수 없음"
        }

        // situationDetailButton 클릭 리스너 추가
        findViewById<ImageButton>(R.id.situationDetailButton).setOnClickListener {
            // CalenderSituationActivity로 이동
            val intent = Intent(this, CalenderSituationActivity::class.java)
            intent.putExtra("date", datefordb) // datefordb를 Intent에 추가
            startActivity(intent)
        }

        // thoughtDetailButton 클릭 리스너 추가
        findViewById<ImageButton>(R.id.thoughtDetailButton).setOnClickListener {
            // CalenderThinkActivity로 이동
            val intent = Intent(this, CalenderThinkActivity::class.java)
            intent.putExtra("date", datefordb) // datefordb를 Intent에 추가
            startActivity(intent)
        }

        // buttonNext 클릭 리스너 추가
        findViewById<MaterialButton>(R.id.buttonNext).setOnClickListener {
            // CalenderChangedDayActivity로 이동
            val intent = Intent(this, CalenderChangedDayActivity::class.java)
            intent.putExtra("date", datefordb) // datefordb를 Intent에 추가
            startActivity(intent)
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
