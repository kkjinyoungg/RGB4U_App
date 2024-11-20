package com.example.rgb4u_app.ui.activity.onboarding

import android.content.Intent
import android.os.Bundle
import android.widget.Button
import android.widget.TextView
import androidx.appcompat.app.AppCompatActivity
import com.example.rgb4u_app.R

class OnboardingStartActivity : AppCompatActivity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_onboarding_start) // XML 파일 이름

        // btn_continue 버튼 가져오기
        val btnContinue: Button = findViewById(R.id.btn_continue)

        // userName 텍스트뷰 가져오기
        val userNameTextView: TextView = findViewById(R.id.userName)

        // 데이터 가져오기 (여기서는 예제 데이터를 사용)
        val userName = getUserData()

        // 텍스트뷰에 데이터 설정
        userNameTextView.text = "$userName"


        // 버튼 클릭 리스너 설정
        btnContinue.setOnClickListener {
            // OnboardingActivity로 이동
            val intent = Intent(this, OnboardingActivity::class.java)
            startActivity(intent)
        }
    }

    // 데이터를 가져오는 메서드 (예제)
    private fun getUserData(): String {
        // 예: 서버나 데이터베이스에서 데이터를 가져오는 코드
        // 여기서는 "나서진"이라는 임의의 데이터를 반환
        return "나서진"
    }
}