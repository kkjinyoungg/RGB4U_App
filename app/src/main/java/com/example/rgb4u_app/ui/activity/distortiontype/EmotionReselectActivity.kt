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

class EmotionReselectActivity : AppCompatActivity() {

    private lateinit var dynamicTextView: TextView
    private lateinit var seekBar: SeekBar
    private lateinit var buttonNext: Button
    private lateinit var squareView: ImageView

    // Firebase Database Reference
    private lateinit var database: DatabaseReference

    // 사용자 ID와 일기 ID를 저장할 변수
    private lateinit var userId: String
    private lateinit var date: String

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_emotion_reselect)

        // Firebase 초기화
        database = FirebaseDatabase.getInstance().reference

        // Intent로부터 사용자 ID와 일기 ID 받아오기
        userId = intent.getStringExtra("userId") ?: ""
        date = intent.getStringExtra("date") ?: ""

        // userId와 diaryId 확인을 위해 로그 출력
        Log.d("EmotionReselectActivity", "Received User ID: $userId")
        Log.d("EmotionReselectActivity", "Received Diary ID: $date")

        // 뷰 초기화
        dynamicTextView = findViewById(R.id.dynamicTextView)
        seekBar = findViewById(R.id.seekBar)
        squareView = findViewById(R.id.squareView)
        buttonNext = findViewById(R.id.buttonNext)

        // 초기 상태에서 squareView에 디폴트 이미지 설정
        squareView.setImageResource(R.drawable.img_emotion_0)

        // SeekBar의 진행 상태에 따라 dynamicTextView의 텍스트 변경
        seekBar.setOnSeekBarChangeListener(object : SeekBar.OnSeekBarChangeListener {
            override fun onProgressChanged(seekBar: SeekBar?, progress: Int, fromUser: Boolean) {
                dynamicTextView.text = when (progress) {
                    0 -> {
                        squareView.setImageResource(R.drawable.img_emotion_0)
                        "매우 심하지 않았어"
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
            val reMeasuredEmotionDegreeInt = seekBar.progress
            val reMeasuredEmotionDegreeString = dynamicTextView.text.toString()

            // Firebase에 데이터 저장
            database.child("users").child(userId).child("diaries").child(date)
                .child("userInput").child("reMeasuredEmotionDegree").setValue(
                    mapOf(
                        "int" to reMeasuredEmotionDegreeInt,
                        "string" to reMeasuredEmotionDegreeString
                    )
                ).addOnCompleteListener { task ->
                    if (task.isSuccessful) {
                        // 저장 성공 시 로그 출력 및 다음 액티비티로 이동
                        Log.d("EmotionReselectActivity", "Data saved successfully.")
                        startActivity(Intent(this, SummaryChangedDayActivity::class.java))
                    } else {
                        // 저장 실패 시 에러 로그 출력
                        Log.e("EmotionReselectActivity", "Error saving data: ${task.exception?.message}")
                    }
                }
        }
    }
}
