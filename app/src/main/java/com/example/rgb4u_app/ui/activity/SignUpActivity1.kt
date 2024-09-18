package com.example.rgb4u_app.ui.activity

import android.content.Intent
import android.os.Bundle
import android.text.Editable
import android.text.TextWatcher
import android.widget.Button
import android.widget.EditText
import android.widget.ImageButton
import android.widget.TextView
import androidx.appcompat.app.AppCompatActivity
import com.example.rgb4u_app.R

class SignUpActivity1 : AppCompatActivity() {

    private lateinit var editTextNickname: EditText
    private lateinit var charCount: TextView
    private lateinit var errorMessage: TextView
    private lateinit var buttonNext: Button
    private lateinit var buttonBack: ImageButton // 뒤로가기 버튼을 위한 변수 추가

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_sign_up1)

        editTextNickname = findViewById(R.id.editTextNickname)
        charCount = findViewById(R.id.charCount)
        errorMessage = findViewById(R.id.errorMessage)
        buttonNext = findViewById(R.id.buttonNext)
        buttonBack = findViewById(R.id.buttonBack) // 뒤로가기 버튼 초기화

        editTextNickname.addTextChangedListener(object : TextWatcher {
            override fun beforeTextChanged(s: CharSequence?, start: Int, count: Int, after: Int) {}

            override fun onTextChanged(s: CharSequence?, start: Int, before: Int, count: Int) {
                val nickname = s.toString()
                charCount.text = "${nickname.length}/10"
                validateNickname(nickname)
            }

            override fun afterTextChanged(s: Editable?) {}
        })

        buttonBack.setOnClickListener {
            // 로그인 액티비티로 이동
            val intent = Intent(this, LoginActivity::class.java)
            startActivity(intent)
            finish() // 현재 액티비티 종료
        }

        buttonNext.setOnClickListener {
            val nickname = editTextNickname.text.toString()
            if (isNicknameValid(nickname)) {
                // 닉네임이 유효할 경우 다음 단계로 이동
                val intent = Intent(this, SignUpActivity2::class.java)
                startActivity(intent)
                finish() // 현재 액티비티 종료
            }
        }

        // 초기 버튼 상태 설정
        setButtonState(false)
    }

    private fun validateNickname(nickname: String) {
        when {
            nickname.length > 10 -> {
                errorMessage.text = "10글자 이하로 입력해 줘!"
                errorMessage.visibility = TextView.VISIBLE
                setButtonState(false)
            }
            nickname.any { !it.isLetterOrDigit() } -> {
                errorMessage.text = "특수문자는 들어가면 안 돼!"
                errorMessage.visibility = TextView.VISIBLE
                setButtonState(false)
            }
            else -> {
                errorMessage.visibility = TextView.GONE
                setButtonState(true) // 유효한 경우 버튼 활성화
            }
        }
    }

    private fun setButtonState(isValid: Boolean) {
        buttonNext.isEnabled = isValid
        // 버튼 색상은 rounded_button.xml에서 자동으로 처리됨
    }

    private fun isNicknameValid(nickname: String): Boolean {
        return nickname.length <= 10 && nickname.all { it.isLetterOrDigit() }
    }
}