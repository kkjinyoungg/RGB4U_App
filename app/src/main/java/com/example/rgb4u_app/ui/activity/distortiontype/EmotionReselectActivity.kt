package com.example.rgb4u_app.ui.activity.distortiontype

import android.content.Intent
import android.os.Bundle
import android.view.View
import android.widget.Button
import android.widget.ImageView
import android.widget.SeekBar
import android.widget.TextView
import androidx.appcompat.app.AppCompatActivity
import com.example.rgb4u_app.R
import com.example.rgb4u_app.ui.activity.summary.SummaryChangedDayActivity
import com.google.firebase.database.DatabaseReference
import com.google.firebase.database.FirebaseDatabase
import android.util.Log
import com.google.firebase.auth.FirebaseAuth

class EmotionReselectActivity : AppCompatActivity() {

    private lateinit var dynamicTextView: TextView
    private lateinit var seekBar: SeekBar
    private lateinit var buttonNext: Button
    private lateinit var squareView: ImageView
    private lateinit var userId: String

    // Firebase Database Reference
    private lateinit var database: DatabaseReference

    // 현재 로그인된 사용자의 UID를 가져오는 함수
    private lateinit var toolbar: String
    private lateinit var date: String

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_emotion_reselect)

        // Firebase 초기화
        database = FirebaseDatabase.getInstance().reference
        userId = FirebaseAuth.getInstance().currentUser?.uid ?: ""

        // 인텐트에서 데이터 받기
        val intent = intent
        toolbar = intent.getStringExtra("Toolbar") ?: ""
        date = intent.getStringExtra("Date") ?: ""

        // 받은 데이터 확인 (Log로 출력)
        Log.d("EmotionReselectActivity", "$toolbar")
        Log.d("EmotionReselectActivity", "Received Date: $date")

        // 뷰 초기화
        dynamicTextView = findViewById(R.id.dynamicTextView)
        seekBar = findViewById(R.id.seekBar)
        squareView = findViewById(R.id.squareView)
        buttonNext = findViewById(R.id.buttonNext)

        // 초기 상태에서 squareView에 디폴트 이미지 설정
        squareView.setImageResource(R.drawable.img_emotion_0)

        // Firebase에서 emotionDegree 값을 가져와 SeekBar 초기값 설정
        val diaryRef = database.child("users").child(userId).child("diaries").child(date).child("userInput").child("emotionDegree")

        diaryRef.get().addOnSuccessListener { snapshot ->
            if (snapshot.exists()) {
                // 'int' 필드에서 값을 가져옴
                val emotionDegree = snapshot.child("int").getValue(Int::class.java) ?: 0 // 기본값 0 설정
                seekBar.progress = emotionDegree  // SeekBar의 진행 상태 설정

                // 진행 상태에 맞는 텍스트 및 이미지 업데이트
                dynamicTextView.text = when (emotionDegree) {
                    0 -> {
                        squareView.setImageResource(R.drawable.img_emotion_0)
                        "전혀 심하지 않았어"
                    }
                    1 -> {
                        squareView.setImageResource(R.drawable.img_emotion_1)
                        "심하지 않았어"
                    }
                    2 -> {
                        squareView.setImageResource(R.drawable.img_emotion_2)
                        "보통이었어"
                    }
                    3 -> {
                        squareView.setImageResource(R.drawable.img_emotion_3)
                        "심했어"
                    }
                    4 -> {
                        squareView.setImageResource(R.drawable.img_emotion_4)
                        "매우 심했어"
                    }
                    else -> ""
                }
            }
        }.addOnFailureListener { exception ->
            Log.e("EmotionReselectActivity", "Error fetching emotionDegree: ${exception.message}")
        }


        // SeekBar의 진행 상태에 따라 dynamicTextView의 텍스트 변경
        seekBar.setOnSeekBarChangeListener(object : SeekBar.OnSeekBarChangeListener {
            override fun onProgressChanged(seekBar: SeekBar?, progress: Int, fromUser: Boolean) {
                dynamicTextView.text = when (progress) {
                    0 -> {
                        squareView.setImageResource(R.drawable.img_emotion_0)
                        "전혀 심하지 않았어"
                    }
                    1 -> {
                        squareView.setImageResource(R.drawable.img_emotion_1)
                        "심하지 않았어"
                    }
                    2 -> {
                        squareView.setImageResource(R.drawable.img_emotion_2)
                        "보통이었어"
                    }
                    3 -> {
                        squareView.setImageResource(R.drawable.img_emotion_3)
                        "심했어"
                    }
                    4 -> {
                        squareView.setImageResource(R.drawable.img_emotion_4)
                        "매우 심했어"
                    }
                    else -> ""
                }
            }

            override fun onStartTrackingTouch(seekBar: SeekBar?) {}

            override fun onStopTrackingTouch(seekBar: SeekBar?) {}
        })

        // "다음" 버튼 클릭 시 Firebase에 데이터 저장
        buttonNext.setOnClickListener {
            // SeekBar의 진행 상태 값 가져오기
            val reMeasuredEmotionDegreeInt = seekBar.progress
            val reMeasuredEmotionDegreeString = dynamicTextView.text.toString()

            // SeekBar 진행 상태에 따른 emotionimg 값 설정
            val reMeasuredEmotionDegreeImage = when (reMeasuredEmotionDegreeInt) {
                0 -> "img_emotion_0"
                1 -> "img_emotion_1"
                2 -> "img_emotion_2"
                3 -> "img_emotion_3"
                4 -> "img_emotion_4"
                else -> "img_emotion_0"  // 기본값 설정
            }

            // Firebase에 데이터 저장
            val diaryRef = database.child("users").child(userId).child("diaries").child(date).child("userInput").child("reMeasuredEmotionDegree")

            // Firebase에 저장할 데이터
            val emotionData = mapOf(
                "int" to reMeasuredEmotionDegreeInt,
                "string" to reMeasuredEmotionDegreeString,
                "emotionimg" to reMeasuredEmotionDegreeImage
            )

            diaryRef.setValue(emotionData).addOnCompleteListener { task ->
                if (task.isSuccessful) {
                    // 저장 성공 시 로그 출력 및 다음 액티비티로 이동
                    Log.d("EmotionReselectActivity", "Data saved successfully.")
                    // Intent 생성하여 데이터 전달
                    val intent = Intent(this, SummaryChangedDayActivity::class.java).apply {
                        putExtra("Date", date)  // date 전달
                        putExtra("Toolbar", toolbar)  // toolbar 전달
                    }
                    startActivity(intent)
                    finish()  // 현재 액티비티 종료
                } else {
                    // 저장 실패 시 에러 로그 출력
                    Log.e("EmotionReselectActivity", "Error saving data: ${task.exception?.message}")
                }
            }
        }

    }
}
