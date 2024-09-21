package com.example.rgb4u_app.ui.activity.mypage

import android.content.Intent
import android.os.Bundle
import androidx.appcompat.app.AppCompatActivity
import androidx.fragment.app.commit
import com.example.rgb4u_app.R
import com.example.rgb4u_app.ui.fragment.MypageCommonHeaderFragment

class MyPageServiceCheck : AppCompatActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_my_page_service_check)

        // Fragment를 추가하고 타이틀 설정
        val headerFragment = MypageCommonHeaderFragment()
        supportFragmentManager.commit {
            replace(R.id.header_fragment_container, headerFragment)
        }

        // Fragment의 타이틀을 '알림 설정'으로 변경
        headerFragment.setTitle("서비스 이용 약관")


        // 뒤로가기 버튼 설정
        headerFragment.setBackButtonListener {
            navigateToMyPageMainActivity()
        }
    }

    private fun navigateToMyPageMainActivity() {
        val intent = Intent(this, MyPageMainActivity::class.java)
        startActivity(intent)
        finish()
    }
}