package com.example.rgb4u_app.ui.activity.calendar

import android.os.Bundle
import android.util.Log
import android.widget.ImageButton
import android.widget.ImageView
import android.widget.TextView
import androidx.activity.enableEdgeToEdge
import androidx.activity.viewModels
import androidx.appcompat.app.AppCompatActivity
import androidx.core.content.ContextCompat
import com.example.rgb4u_app.R
import com.example.rgb4u_app.ui.fragment.SummaryFragment
import com.example.rgb4u_appclass.DiaryViewModel
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.database.DataSnapshot
import com.google.firebase.database.DatabaseError
import com.google.firebase.database.DatabaseReference
import com.google.firebase.database.FirebaseDatabase
import com.google.firebase.database.ValueEventListener

class CalenderThinkActivity : AppCompatActivity() {
    private lateinit var summaryFragment: SummaryFragment
    // Realtime Database 참조 선언
    private lateinit var database: DatabaseReference
    private val diaryViewModel: DiaryViewModel by viewModels()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        setContentView(R.layout.activity_calender_think)

        //diaryId, ID
        val date = intent.getStringExtra("date") ?: "defaultDate"
        // 현재 로그인된 사용자의 UID를 가져오는 함수
        val userId = FirebaseAuth.getInstance().currentUser?.uid

        // SummaryFragment 인스턴스 생성
        summaryFragment = SummaryFragment.newInstance()

        // FragmentManager를 사용하여 Fragment를 추가
        supportFragmentManager.beginTransaction().apply {
            replace(R.id.fragment_container, summaryFragment)  // fragment_container에 Fragment 삽입
            commit()
        }

        if (userId != null && date != null) {
            // 첫 번째 경로에서 데이터 조회
            loadAiAnalysisData(userId, date, summaryFragment)
            // 두 번째 경로에서 데이터 조회
            loadUserInputData(userId, date, summaryFragment)
        } else {
            // 데이터가 존재하지 않는 경우
            summaryFragment.summarizedContent = "생각 데이터가 존재하지 않습니다"
            summaryFragment.whySummaryReason = "생각 이유 데이터가 존재하지 않습니다"
            // UI 업데이트 호출
            summaryFragment.updateUI()
        }

        summaryFragment.titleText = "이렇게 생각했어요" //고정 제목
        summaryFragment.summaryLabelText = "요약된 생각이에요" //고정 제목
        summaryFragment.userContentLabelText = "기록한 생각이에요" //고정 제목
    }

    override fun onResume() {
        super.onResume()

        // 프래그먼트의 view가 준비되었는지 null 체크 후 접근
        summaryFragment.view?.let { fragmentView ->
            fragmentView.findViewById<TextView>(R.id.titleTextView)?.setTextColor(ContextCompat.getColor(this, R.color.black))
            fragmentView.findViewById<TextView>(R.id.userContentLableTextView)?.setTextColor(ContextCompat.getColor(this, R.color.black))
            fragmentView.findViewById<TextView>(R.id.userContentTextView)?.setTextColor(ContextCompat.getColor(this, R.color.black))
            fragmentView.findViewById<TextView>(R.id.summaryLableTextView)?.setTextColor(ContextCompat.getColor(this, R.color.blue2))
            fragmentView.findViewById<TextView>(R.id.summarizedTextView)?.setTextColor(ContextCompat.getColor(this, R.color.black))
            fragmentView.findViewById<TextView>(R.id.whySummaryLabelTextView)?.setTextColor(ContextCompat.getColor(this, R.color.black))
            fragmentView.findViewById<TextView>(R.id.whySummaryTextView)?.setTextColor(ContextCompat.getColor(this, R.color.black))

            // 레이아웃 배경 설정
            fragmentView.setBackgroundResource(R.drawable.background_light_sub)

            // ImageView와 ImageButton 리소스 변경
            val imageView = fragmentView.findViewById<ImageView>(R.id.whySummaryLabelImageView)
            imageView?.setColorFilter(ContextCompat.getColor(this, R.color.black), android.graphics.PorterDuff.Mode.SRC_IN)
            fragmentView.findViewById<ImageButton>(R.id.backButton)?.setImageResource(R.drawable.ic_back)
        }
    }

    private fun loadAiAnalysisData(userId: String, date: String, summaryFragment: SummaryFragment) {
        database = FirebaseDatabase.getInstance().getReference("users/$userId/diaries/$date/aiAnalysis/firstAnalysis")
        database.addListenerForSingleValueEvent(object : ValueEventListener {
            override fun onDataChange(dataSnapshot: DataSnapshot) {
                if (dataSnapshot.exists()) {
                    val thoughts = dataSnapshot.child("thoughts").getValue(String::class.java) ?: "생각 정보 없음"
                    val thoughtsreason = dataSnapshot.child("thoughtsReason").getValue(String::class.java) ?: "생각 이유 정보 없음"

                    // 문장부호 기준으로 나누고 포맷팅
                    summaryFragment.summarizedContent = formatThoughts(thoughts)
                    summaryFragment.whySummaryReason = thoughtsreason
                } else {
                    summaryFragment.summarizedContent = "생각 데이터가 존재하지 않습니다"
                    summaryFragment.whySummaryReason = "생각 이유 데이터가 존재하지 않습니다"
                }
                summaryFragment.updateUI()
            }

            override fun onCancelled(databaseError: DatabaseError) {
                summaryFragment.summarizedContent = "오류 발생: ${databaseError.message}"
                summaryFragment.whySummaryReason = "오류 발생: ${databaseError.message}"
                summaryFragment.updateUI()
            }
        })
    }

    // thoughts를 포맷팅하는 함수
    private fun formatThoughts(text: String): String {
        // 문장부호를 기준으로 나누고 포맷팅
        return text.split(Regex("(?<=[.!?])\\s*")) // 문장부호 뒤에서 나누기
            .filter { it.isNotBlank() } // 빈 문자열 제거
            .joinToString("\n") { "•  $it" } // 각 문장 앞에 "• " 추가하고 줄바꿈
    }

    private fun loadUserInputData(userId: String, date: String, summaryFragment: SummaryFragment) {
        database = FirebaseDatabase.getInstance().getReference("users/$userId/diaries/$date/userInput")
        database.addListenerForSingleValueEvent(object : ValueEventListener {
            override fun onDataChange(dataSnapshot: DataSnapshot) {
                if (dataSnapshot.exists()) {
                    val userInputThoughts = dataSnapshot.child("thoughts").getValue(String::class.java) ?: "사용자 입력 상황 정보 없음"
                    summaryFragment.userContent = userInputThoughts
                    Log.d("SummaryFragment", "User content: ${summaryFragment.userContent}")//데이터로드확인용-로그캣에서 확인
                } else {
                    summaryFragment.userContent = "사용자 입력 데이터가 존재하지 않습니다"
                }
                summaryFragment.updateUI()
            }

            override fun onCancelled(databaseError: DatabaseError) {
                summaryFragment.userContent = "오류 발생: ${databaseError.message}"
                summaryFragment.updateUI()
            }
        })
    }
}
