package com.example.rgb4u_app.ui.activity

import android.content.Intent
import android.os.Bundle
import android.widget.Button
import androidx.appcompat.app.AppCompatActivity
import com.example.rgb4u_app.R

class LoginActivity : AppCompatActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_login)

        // 카카오 로그인 버튼 클릭 리스너 설정
        findViewById<Button>(R.id.btnKakaoLogin).setOnClickListener {
            startSignUpActivity1()
        }

        // 네이버 로그인 버튼 클릭 리스너 설정
        findViewById<Button>(R.id.btnNaverLogin).setOnClickListener {
            startSignUpActivity1()
        }

        // 구글 로그인 버튼 클릭 리스너 설정
        findViewById<Button>(R.id.btnGoogleLogin).setOnClickListener {
            startSignUpActivity1()
        }
    }

    private fun startSignUpActivity1() {
        val intent = Intent(this, SignUpActivity1::class.java)
        startActivity(intent)
    }
}
