package com.example.rgb4u_app.ui.activity.calendar

import android.os.Bundle
import android.widget.ImageButton
import android.widget.ImageView
import android.widget.TextView
import androidx.activity.enableEdgeToEdge
import androidx.appcompat.app.AppCompatActivity
import androidx.core.content.ContextCompat
import com.example.rgb4u_app.R
import com.example.rgb4u_app.ui.fragment.SummaryFragment

class CalenderSituationActivity : AppCompatActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        setContentView(R.layout.activity_calender_situation)

        // SummaryFragment 인스턴스 생성
        val summaryFragment = SummaryFragment.newInstance()

        // FragmentManager를 사용하여 Fragment를 추가
        supportFragmentManager.beginTransaction().apply {
            replace(R.id.fragment_container, summaryFragment)  // fragment_container에 Fragment 삽입
            commit()  // 트랜잭션 적용
        }

        // 프래그먼트 찾기
        val fragment = supportFragmentManager.findFragmentById(R.id.fragment_container) as SummaryFragment

        // Activity에서 사용할 때는 requireContext() 대신 this를 사용
        fragment.view?.findViewById<TextView>(R.id.titleTextView)?.setTextColor(ContextCompat.getColor(this, R.color.black))
        fragment.view?.findViewById<TextView>(R.id.userContentLableTextView)?.setTextColor(ContextCompat.getColor(this, R.color.black))
        fragment.view?.findViewById<TextView>(R.id.userContentTextView)?.setTextColor(ContextCompat.getColor(this, R.color.black))
        fragment.view?.findViewById<TextView>(R.id.summaryLableTextView)?.setTextColor(ContextCompat.getColor(this, R.color.blue2))
        fragment.view?.findViewById<TextView>(R.id.summarizedTextView)?.setTextColor(ContextCompat.getColor(this, R.color.black))
        fragment.view?.findViewById<TextView>(R.id.whySummaryLabelTextView)?.setTextColor(ContextCompat.getColor(this, R.color.black))
        fragment.view?.findViewById<TextView>(R.id.whySummaryTextView)?.setTextColor(ContextCompat.getColor(this, R.color.black))

        // 레이아웃 배경 설정
        fragment.view?.setBackgroundResource(R.drawable.background_light_sub)

        // 프래그먼트의 ImageView와 ImageButton 리소스 변경
        val imageView = fragment.view?.findViewById<ImageView>(R.id.whySummaryLabelImageView)
        imageView?.setColorFilter(ContextCompat.getColor(this, R.color.black), android.graphics.PorterDuff.Mode.SRC_IN)
        // fragment.view?.findViewById<ImageView>(R.id.whySummaryLabelImageView)?.setImageResource(R.drawable.ic_situation) // 블랙버전 추후 업데이트 필요
        fragment.view?.findViewById<ImageButton>(R.id.backButton)?.setImageResource(R.drawable.ic_back)


        summaryFragment.titleText = "이런 상황이 있었어요" //고정 제목
        summaryFragment.summaryLabelText = "AI로 요약된 상황" //고정 제목
        summaryFragment.userContentLabelText = "기록한 상황" //고정 제목

        summaryFragment.summarizedContent = "어쩌구어쩌구상황"
        summaryFragment.whySummaryReason = "대충요약이유쏼라"
    }
}
