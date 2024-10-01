package com.example.rgb4u_app.ui.activity.summary

import android.content.Intent
import android.os.Bundle
import androidx.appcompat.app.AppCompatActivity
import com.example.rgb4u_app.R
import com.example.rgb4u_app.ui.fragment.SummaryFragment
import com.google.firebase.database.* // Firebase Realtime Database 관련 클래스
//import com.google.firebase.auth.FirebaseAuth // Firebase Authentication 관련 클래스
import android.util.Log // 로그 관련 클래스
import androidx.activity.viewModels
import com.example.rgb4u_appclass.DiaryViewModel
import com.google.firebase.database.DataSnapshot
import com.google.firebase.database.DatabaseError
import com.google.firebase.database.ValueEventListener
import com.example.rgb4u_app.MyApplication
import androidx.activity.viewModels // ViewModel을 액티비티에서 가져오기 위한 import


class SummarySituationActivity : AppCompatActivity() {

    // Realtime Database 참조 선언
    private lateinit var database: DatabaseReference
    private val diaryViewModel: DiaryViewModel by viewModels()
    //private lateinit var userId: String // userId 변수 추가

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_summary_situation)

        //diaryId, ID
        val diaryId = intent.getStringExtra("DIARY_ID") ?: "defaultDiaryId"
        val userId = "userId" // 실제 사용자 ID로 변경해야 함

        // SummaryFragment 인스턴스 생성
        val summaryFragment = SummaryFragment.newInstance()

        // FragmentManager를 사용하여 Fragment를 추가
        supportFragmentManager.beginTransaction().apply {
            replace(R.id.fragment_container, summaryFragment)  // fragment_container에 Fragment 삽입
            commit()  // 트랜잭션 적용
        }

        if (diaryId != null) {
            // 첫 번째 경로에서 데이터 조회
            loadAiAnalysisData(userId, diaryId, summaryFragment)
            // 두 번째 경로에서 데이터 조회
            loadUserInputData(userId, diaryId, summaryFragment)
        } else {
            // 데이터가 존재하지 않는 경우
            summaryFragment.summarizedContent = "상황 데이터가 존재하지 않습니다"
            summaryFragment.whySummaryReason = "상황 이유 데이터가 존재하지 않습니다"
            // UI 업데이트 호출
            summaryFragment.updateUI()
        }

        // Activity에서 SummaryFragment에 데이터를 전달
        //summaryFragment.userContent = "내가 작성한 글이다"
        //summaryFragment.summarizedContent = "요약된 상황 표현"
        //summaryFragment.whySummaryReason = "이유에 대한 텍스트가 여기에 나타납니다."
        summaryFragment.titleText = "이런 상황에서" //고정 제목
        summaryFragment.summaryLabelText = "요약된 상황 표현" //고정 제목
    }

    private fun loadAiAnalysisData(userId: String, diaryId: String, summaryFragment: SummaryFragment) {
        database = FirebaseDatabase.getInstance().getReference("users/$userId/diaries/$diaryId/aiAnalysis/firstAnalysis")
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

    private fun loadUserInputData(userId: String, diaryId: String, summaryFragment: SummaryFragment) {
        database = FirebaseDatabase.getInstance().getReference("users/$userId/diaries/$diaryId/userInput")
        database.addListenerForSingleValueEvent(object : ValueEventListener {
            override fun onDataChange(dataSnapshot: DataSnapshot) {
                if (dataSnapshot.exists()) {
                    val userInputSituation = dataSnapshot.child("situation").getValue(String::class.java) ?: "사용자 입력 상황 정보 없음"
                    summaryFragment.userContent = userInputSituation
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
