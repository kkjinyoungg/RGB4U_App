package com.example.rgb4u_app

import android.content.Intent
import android.os.Bundle
import android.text.InputFilter
import android.widget.Button
import android.widget.EditText
import android.widget.ImageButton
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity

class SignUpActivity : AppCompatActivity() {
    private lateinit var editTextUsername: EditText
    private lateinit var buttonCompleteSignUp: Button

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_sign_up)

        // 뒤로 가기 버튼 클릭 이벤트 처리
        findViewById<ImageButton>(R.id.btn_back_login).setOnClickListener {
            finish()
        }

        editTextUsername = findViewById(R.id.edittext_username)
        buttonCompleteSignUp = findViewById(R.id.btn_complete_signup)

        // EditText 글자 수 제한 설정
        editTextUsername.filters = arrayOf(InputFilter.LengthFilter(8))

        // 완료 버튼 클릭 이벤트 처리
        buttonCompleteSignUp.setOnClickListener {
            val username = editTextUsername.text.toString().trim()

            // 사용자 이름 유효성 검사
            if (username.isNotEmpty()) {
                // 메인 액티비티로 이동
                val intent = Intent(this, MainActivity::class.java)
                startActivity(intent)
                finish()
            } else {
                // 사용자 이름이 입력되지 않은 경우 처리
                Toast.makeText(this, "닉네임을 입력해 주세요.", Toast.LENGTH_SHORT).show()
            }
        }
    }
}
