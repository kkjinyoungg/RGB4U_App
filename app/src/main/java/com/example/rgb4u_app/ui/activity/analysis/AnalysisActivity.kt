package com.example.rgb4u_app.ui.activity.analysis

import android.os.Bundle
import androidx.appcompat.app.AppCompatActivity
import com.example.rgb4u_app.R
import com.example.rgb4u_app.ui.fragment.AnalysisFragment

class AnalysisActivity : AppCompatActivity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_analysis) // 액티비티의 XML 레이아웃을 설정

        // Fragment를 Activity에 추가
        if (savedInstanceState == null) {
            supportFragmentManager.beginTransaction()
                .replace(R.id.fragment_container, AnalysisFragment()) // fragment_container는 Activity의 FrameLayout 또는 다른 컨테이너 ID
                .commit()
        }
    }
}
