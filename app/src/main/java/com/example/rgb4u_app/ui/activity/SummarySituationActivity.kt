package com.example.rgb4u_app.ui.activity

import android.os.Bundle
import androidx.appcompat.app.AppCompatActivity
import com.example.rgb4u_app.R
import com.example.rgb4u_app.ui.fragment.SummaryFragment

class SummarySituationActivity : AppCompatActivity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_summary_situation)

        // SummaryFragment 인스턴스 생성
        val summaryFragment = SummaryFragment.newInstance()

        // FragmentManager를 사용하여 Fragment를 추가
        supportFragmentManager.beginTransaction().apply {
            replace(R.id.fragment_container, summaryFragment)  // fragment_container에 Fragment 삽입
            commit()  // 트랜잭션 적용
        }

        // Activity에서 SummaryFragment에 데이터를 전달하는 경우
        summaryFragment.userContent = "내가 작성한 글이다"
        summaryFragment.summarizedContent = "요약된 상황 표현"
        summaryFragment.whySummaryReason = "이유에 대한 텍스트가 여기에 나타납니다."
        summaryFragment.titleText = "이런 상황에서"
        summaryFragment.summaryLabelText = "요약된 상황 표현"
    }
}
