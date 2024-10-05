package com.example.rgb4u_app.ui.activity.summary
//re
import android.content.Intent
import android.os.Bundle
import android.widget.ImageButton
import android.widget.TextView
import androidx.appcompat.app.AppCompatActivity
import com.example.rgb4u_app.R
import com.example.rgb4u_app.ui.activity.MainActivity
import com.example.rgb4u_app.ui.activity.diary.EmotionSelectActivity
import com.google.firebase.database.* // Realtime Database 사용을 위한 import
import java.text.SimpleDateFormat
import java.util.Calendar
import java.util.Locale
import com.example.rgb4u_appclass.DiaryViewModel
import com.example.rgb4u_app.MyApplication
import androidx.activity.viewModels // ViewModel을 액티비티에서 가져오기 위한 import
import com.google.firebase.auth.FirebaseAuth


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

        //diaryId, ID 수신
        val diaryId = DiaryViewModel.diaryId

        // 현재 로그인된 사용자의 UID를 가져오는 함수
        val userId = FirebaseAuth.getInstance().currentUser?.uid

        if (userId != null && diaryId != null) {
            // Realtime Database에서 diaryId로 데이터 조회
            database = FirebaseDatabase.getInstance().getReference("users/$userId/diaries/$diaryId/aiAnalysis/firstAnalysis")
            database.addListenerForSingleValueEvent(object : ValueEventListener {
                override fun onDataChange(dataSnapshot: DataSnapshot) {
                    if (dataSnapshot.exists()) {
                        // Realtime Database에서 situation과 thoughts 가져오기
                        val situation = dataSnapshot.child("situation").getValue(String::class.java) ?: "상황 정보 없음"
                        val thoughts = dataSnapshot.child("thoughts").getValue(String::class.java) ?: "생각 정보 없음"

                        // TextView에 Realtime Database에서 가져온 값 설정
                        situationTextView.text = situation
                        thoughtTextView.text = thoughts
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
    }
}
