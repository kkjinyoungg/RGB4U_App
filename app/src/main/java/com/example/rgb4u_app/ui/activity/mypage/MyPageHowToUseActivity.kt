package com.example.rgb4u_app.ui.activity.mypage

import android.content.Intent
import android.os.Bundle
import android.view.View
import androidx.appcompat.app.AppCompatActivity
import androidx.fragment.app.commit
import com.example.rgb4u_app.R
import com.example.rgb4u_app.ui.fragment.MypageCommonHeaderFragment

class MyPageHowToUseActivity : AppCompatActivity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_my_page_how_to_use)

        // Fragment를 추가하고 타이틀 설정
        val headerFragment = MypageCommonHeaderFragment.newInstance("(앱 이름) 이야기")
        supportFragmentManager.commit {
            replace(R.id.fragment_container, headerFragment)
        }

        // 뒤로가기 버튼 설정
        headerFragment.setBackButtonListener(View.OnClickListener {
            navigateToMyPageMainActivity()
        })
    }

    private fun navigateToMyPageMainActivity() {
        val intent = Intent(this, MyPageMainActivity::class.java)
        startActivity(intent)
        finish()
    }
}
