package com.example.rgb4u.ver1_app.ui.activity.home

import android.os.Bundle
import androidx.appcompat.app.AppCompatActivity
import androidx.fragment.app.Fragment
import androidx.fragment.app.commit
import com.example.rgb4u.ver1_app.R
import com.example.rgb4u.ver1_app.ui.fragment.HomeFragment

class MainActivity : AppCompatActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)


        // 처음에는 홈 프래그먼트를 열도록 설정
        if (savedInstanceState == null) {
            openFragment(HomeFragment())
        }

    }

    fun openFragment(fragment: Fragment) {
        supportFragmentManager.commit {
            replace(R.id.fragment_container, fragment) // fragment_container에 프래그먼트 교체
            addToBackStack(null) // 백 스택에 추가
        }
    }
}