package com.example.rgb4u_app.ui.activity.summary

import android.os.Bundle
import androidx.appcompat.app.AppCompatActivity
import com.example.rgb4u_app.R
import com.example.rgb4u_app.ui.fragment.SummaryFragment
import com.google.firebase.database.* // Firebase Realtime Database 관련 클래스
//import com.google.firebase.auth.FirebaseAuth // Firebase Authentication 관련 클래스
import android.util.Log // 로그 관련 클래스
import com.example.rgb4u_appclass.DiaryViewModel
import com.google.firebase.database.DataSnapshot
import com.google.firebase.database.DatabaseError
import com.google.firebase.database.ValueEventListener



class SummarySituationActivity : AppCompatActivity() {

    private lateinit var userId: String // userId 변수 추가

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_summary_situation)

        // Firebase Authentication에서 현재 사용자 ID 가져오기
        //val auth = FirebaseAuth.getInstance()
        //val user = auth.currentUser
        //userId = user?.uid ?: "" // 현재 사용자 ID 설정
        userId = "userId"

        // SummaryFragment 인스턴스 생성
        val summaryFragment = SummaryFragment.newInstance()

        // FragmentManager를 사용하여 Fragment를 추가
        supportFragmentManager.beginTransaction().apply {
            replace(R.id.fragment_container, summaryFragment)  // fragment_container에 Fragment 삽입
            commit()  // 트랜잭션 적용
        }

        getData()

        // Activity에서 SummaryFragment에 데이터를 전달하는 경우
        summaryFragment.userContent = "내가 작성한 글이다"
        summaryFragment.summarizedContent = "요약된 상황 표현"
        summaryFragment.whySummaryReason = "이유에 대한 텍스트가 여기에 나타납니다."
        summaryFragment.titleText = "이런 상황에서"
        summaryFragment.summaryLabelText = "요약된 상황 표현"
    }

    fun getData() {
        val database = FirebaseDatabase.getInstance() // FirebaseDatabase 인스턴스 가져오기
        val myRef = database.getReference("users/$userId/diaries")

        val postListener = object : ValueEventListener {
            override fun onDataChange(dataSnapshot: DataSnapshot) {
                // Get Post object and use the values to update the UI
                Log.d("SummarySituationActivity", dataSnapshot.toString())
                // ...
                for (dataModel in dataSnapshot.children){
                    Log.d("SummarySituationActivity", dataModel.toString())
                    val item = dataModel.getValue(DiaryViewModel::class.java) //모델 형태로 데이터 받기
                    Log.d("SummarySituationActivity", item.toString())

                }
            }

            override fun onCancelled(databaseError: DatabaseError) {
                // Getting Post failed, log a message
                Log.w("SummarySituationActivity", "loadPost:onCancelled", databaseError.toException())
            }
        }
        myRef.addListenerForSingleValueEvent(postListener)
    }
}
