package com.example.rgb4u.ver1_app.ui.activity.calendar

import android.os.Build
import android.os.Bundle
import android.util.Log
import android.view.View
import android.widget.ImageButton
import android.widget.ImageView
import android.widget.LinearLayout
import android.widget.TextView
import androidx.activity.enableEdgeToEdge
import androidx.activity.viewModels
import androidx.appcompat.app.AppCompatActivity
import androidx.core.content.ContextCompat
import com.example.rgb4u.ver1_app.R
import com.example.rgb4u.ver1_app.ui.fragment.SummaryFragment
import com.example.rgb4u.ver1_appclass.DiaryViewModel
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.database.DataSnapshot
import com.google.firebase.database.DatabaseError
import com.google.firebase.database.DatabaseReference
import com.google.firebase.database.FirebaseDatabase
import com.google.firebase.database.ValueEventListener

class CalenderSituationActivity : AppCompatActivity() {
    private lateinit var summaryFragment: SummaryFragment
    // Realtime Database 참조 선언
    private lateinit var database: DatabaseReference
    private val diaryViewModel: DiaryViewModel by viewModels()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        setContentView(R.layout.activity_calender_situation)

        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {
            window.apply {
                decorView.systemUiVisibility =
                        // 글자색 검은색으로 유지
                    View.SYSTEM_UI_FLAG_LAYOUT_FULLSCREEN or
                            View.SYSTEM_UI_FLAG_LAYOUT_STABLE or
                            View.SYSTEM_UI_FLAG_LIGHT_STATUS_BAR
                statusBarColor = android.graphics.Color.TRANSPARENT
            }
        }

        // 현재 로그인된 사용자의 UID를 가져오는 함수
        val userId = FirebaseAuth.getInstance().currentUser?.uid
        val datefordb = intent.getStringExtra("date")

        // SummaryFragment 인스턴스 생성
        summaryFragment = SummaryFragment.newInstance()

        // FragmentManager를 사용하여 Fragment를 추가
        supportFragmentManager.beginTransaction().apply {
            replace(R.id.fragment_container, summaryFragment)  // fragment_container에 Fragment 삽입
            commit()  // 트랜잭션 적용
        }

        if (userId != null && datefordb!= null) {
            // 첫 번째 경로에서 데이터 조회
            loadAiAnalysisData(userId, datefordb, summaryFragment)
            // 두 번째 경로에서 데이터 조회
            loadUserInputData(userId, datefordb, summaryFragment)
        } else {
            // 데이터가 존재하지 않는 경우
            summaryFragment.summarizedContent = "상황 데이터가 존재하지 않습니다"
            summaryFragment.whySummaryReason = "상황 이유 데이터가 존재하지 않습니다"
            // UI 업데이트 호출
            summaryFragment.updateUI()
        }
        summaryFragment.titleText = "이런 상황이 있었어요" //고정 제목
        summaryFragment.summaryLabelText = "요약된 상황이에요" //고정 제목
        summaryFragment.userContentLabelText = "기록한 상황이에요" //고정 제목
    }

    override fun onResume() {
        super.onResume()

        // 프래그먼트의 view가 준비되었는지 null 체크 후 접근
        summaryFragment.view?.let { fragmentView ->
            // TextView 색상 설정
            fragmentView.findViewById<TextView>(R.id.titleTextView)?.setTextColor(ContextCompat.getColor(this, R.color.black))
            fragmentView.findViewById<TextView>(R.id.userContentLableTextView)?.setTextColor(ContextCompat.getColor(this, R.color.black))
            fragmentView.findViewById<TextView>(R.id.userContentTextView)?.setTextColor(ContextCompat.getColor(this, R.color.black))
            fragmentView.findViewById<TextView>(R.id.summaryLableTextView)?.setTextColor(ContextCompat.getColor(this, R.color.blue2))
            fragmentView.findViewById<TextView>(R.id.summarizedTextView)?.setTextColor(ContextCompat.getColor(this, R.color.black))
            fragmentView.findViewById<TextView>(R.id.whySummaryLabelTextView)?.setTextColor(ContextCompat.getColor(this, R.color.black))
            fragmentView.findViewById<TextView>(R.id.whySummaryTextView)?.setTextColor(ContextCompat.getColor(this, R.color.black))

            // 레이아웃 배경 설정
            fragmentView.setBackgroundResource(R.drawable.background_light_sub)

            // 프래그먼트의 ImageView와 ImageButton 리소스 변경
            val imageView = fragmentView.findViewById<ImageView>(R.id.whySummaryLabelImageView)
            imageView?.setColorFilter(ContextCompat.getColor(this, R.color.black), android.graphics.PorterDuff.Mode.SRC_IN)
            fragmentView.findViewById<ImageButton>(R.id.backButton)?.setImageResource(R.drawable.ic_back)
            fragmentView.findViewById<View>(R.id.whySummarybg)?.setBackgroundResource(R.drawable.edittext_background_wh)
            fragmentView.findViewById<ImageView>(R.id.summaryline)?.setBackgroundResource(R.drawable.help_bottom_sheet_line)
        }
    }

    private fun loadAiAnalysisData(userId: String, date: String, summaryFragment: SummaryFragment) {
        database = FirebaseDatabase.getInstance().getReference("users/$userId/diaries/$date/aiAnalysis/firstAnalysis")
        database.addListenerForSingleValueEvent(object : ValueEventListener {
            override fun onDataChange(dataSnapshot: DataSnapshot) {
                if (dataSnapshot.exists()) {
                    val situation = dataSnapshot.child("situation").getValue(String::class.java) ?: "상황 정보 없음"
                    val situationreason = dataSnapshot.child("situationReason").getValue(String::class.java) ?: "상황 이유 정보 없음"
                    summaryFragment.summarizedContent = situation
                    summaryFragment.whySummaryReason = situationreason
                } else {
                    summaryFragment.summarizedContent = "상황 데이터가 존재하지 않습니다"
                    summaryFragment.whySummaryReason = "상황 이유 데이터가 존재하지 않습니다"
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

    private fun loadUserInputData(userId: String, date: String, summaryFragment: SummaryFragment) {
        database = FirebaseDatabase.getInstance().getReference("users/$userId/diaries/$date/userInput")
        database.addListenerForSingleValueEvent(object : ValueEventListener {
            override fun onDataChange(dataSnapshot: DataSnapshot) {
                if (dataSnapshot.exists()) {
                    val userInputSituation = dataSnapshot.child("situation").getValue(String::class.java) ?: "사용자 입력 상황 정보 없음"
                    summaryFragment.userContent = userInputSituation
                    Log.d("SummaryFragment", "User content: ${summaryFragment.userContent}") //데이터로드확인용-로그캣에서 확인
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
