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

class CalenderThinkActivity : AppCompatActivity() {
    private lateinit var summaryFragment: SummaryFragment

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        setContentView(R.layout.activity_calender_think)

        // SummaryFragment 인스턴스 생성
        summaryFragment = SummaryFragment.newInstance()

        // FragmentManager를 사용하여 Fragment를 추가
        supportFragmentManager.beginTransaction().apply {
            replace(R.id.fragment_container, summaryFragment)  // fragment_container에 Fragment 삽입
            commit()
        }

        // 고정 제목 및 콘텐츠 설정
        summaryFragment.titleText = "이렇게 생각했어요"
        summaryFragment.summaryLabelText = "AI로 요약된 생각"
        summaryFragment.userContentLabelText = "기록한 생각"
        summaryFragment.summarizedContent = "어쩌구어쩌구생각"
        summaryFragment.whySummaryReason = "대충요약이유쏼라"
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
}
