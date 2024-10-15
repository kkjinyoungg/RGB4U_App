package com.example.rgb4u_app.ui.activity

import android.content.Intent
import android.os.Bundle
import android.widget.Button
import androidx.appcompat.app.AppCompatActivity
import androidx.fragment.app.Fragment
import androidx.fragment.app.commit
import com.example.rgb4u_app.R
import com.example.rgb4u_app.ui.activity.distortiontype.EmotionReselectActivity
import com.example.rgb4u_app.ui.fragment.HomeFragment

class MainActivity : AppCompatActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)


        // 처음에는 홈 프래그먼트를 열도록 설정
        if (savedInstanceState == null) {
            openFragment(HomeFragment())
        }

        // 임시 버튼 초기화
        val btnTestDistortion = findViewById<Button>(R.id.btn_test_distortion)

        // 버튼 클릭 리스너
        btnTestDistortion.setOnClickListener {
            val intent = Intent(this, EmotionReselectActivity::class.java)
            startActivity(intent) // DistortionTypeActivity로 이동
        }

    }

    fun openFragment(fragment: Fragment) {
        supportFragmentManager.commit {
            replace(R.id.fragment_container, fragment) // fragment_container에 프래그먼트 교체
            addToBackStack(null) // 백 스택에 추가
        }
    }
}