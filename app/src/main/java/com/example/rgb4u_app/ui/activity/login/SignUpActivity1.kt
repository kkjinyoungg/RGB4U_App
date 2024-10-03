package com.example.rgb4u_app.ui.activity.login

import android.content.Intent
import android.content.res.ColorStateList
import android.os.Bundle
import android.text.Editable
import android.text.InputFilter
import android.text.TextWatcher
import android.widget.Button
import android.widget.EditText
import android.widget.ImageButton
import android.widget.TextView
import androidx.appcompat.app.AppCompatActivity
import androidx.core.content.ContextCompat
import com.example.rgb4u_app.R
import com.google.firebase.database.FirebaseDatabase
import com.google.firebase.database.DatabaseReference
import android.util.Log

class SignUpActivity1 : AppCompatActivity() {

    private lateinit var editTextNickname: EditText
    private lateinit var charCount: TextView
    private lateinit var errorMessage: TextView
    private lateinit var buttonNext: Button
    private lateinit var buttonBack: ImageButton // 중복된 선언 제거
    private lateinit var database: DatabaseReference // 데이터베이스 접근용 변수

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_sign_up1)

        editTextNickname = findViewById(R.id.editTextNickname)
        charCount = findViewById(R.id.charCount)
        errorMessage = findViewById(R.id.errorMessage)
        buttonNext = findViewById(R.id.buttonNext)
        buttonBack = findViewById(R.id.buttonBack)

        // 에러 메시지의 텍스트 색상 빨간색으로 설정
        errorMessage.setTextColor(ContextCompat.getColor(this, R.color.error_text_color))

        // EditText의 최대 입력 길이를 11자로 설정
        editTextNickname.filters = arrayOf<InputFilter>(InputFilter.LengthFilter(11))

        // Firebase Database 초기화
        database = FirebaseDatabase.getInstance().reference

        editTextNickname.addTextChangedListener(object : TextWatcher {
            override fun beforeTextChanged(s: CharSequence?, start: Int, count: Int, after: Int) {}

            override fun onTextChanged(s: CharSequence?, start: Int, before: Int, count: Int) {
                val nickname = s.toString()
                charCount.text = "${nickname.length}/10" // 항상 'n/10' 형식 유지
                validateNickname(nickname)
            }

            override fun afterTextChanged(s: Editable?) {}
        })

        buttonBack.setOnClickListener {
            val intent = Intent(this, LoginActivity::class.java)
            startActivity(intent)
            finish() // 현재 액티비티 종료
        }

        buttonNext.setOnClickListener {
            val nickname = editTextNickname.text.toString()
            if (isNicknameValid(nickname)) {
                val userId = "userId" // 실제 사용자 ID로 변경
                database.child("users").child(userId).child("nickname").setValue(nickname)
                    .addOnCompleteListener { task ->
                        if (task.isSuccessful) {
                            // 저장 성공 시 다음 단계로 이동
                            val intent = Intent(this, SignUpActivity2::class.java)
                            startActivity(intent)
                            finish()
                        } else {
                            Log.e("FirebaseError", "닉네임 저장에 실패했습니다: ${task.exception?.message}")
                            // errorMessage.text = "닉네임 저장에 실패했습니다."
                            // errorMessage.visibility = TextView.VISIBLE
                        }
                    }
            }
        }
        // 초기 버튼 상태 설정
        setButtonState(false)
    }

    private fun validateNickname(nickname: String) {
        when {
            nickname.length == 11 -> {
                errorMessage.text = "10자 이내로 작성해 주세요"
                errorMessage.visibility = TextView.VISIBLE

                // 11자를 입력하면 charCount와 밑줄 색상을 빨간색으로 변경
                charCount.setTextColor(ContextCompat.getColor(this, R.color.error_text_color))
                editTextNickname.backgroundTintList = ColorStateList.valueOf(ContextCompat.getColor(this, R.color.error_text_color)) // 밑줄 색상 변경

                setButtonState(false)
            }
            nickname.length > 10 -> { // 10자를 초과하면 에러 메시지
                errorMessage.text = "10자 이내로 작성해 주세요"
                errorMessage.visibility = TextView.VISIBLE

                // charCount와 밑줄 색상을 빨간색으로 변경
                charCount.setTextColor(ContextCompat.getColor(this, R.color.error_text_color))
                editTextNickname.backgroundTintList = ColorStateList.valueOf(ContextCompat.getColor(this, R.color.error_text_color)) // 밑줄 색상 변경

                setButtonState(false)
            }
            else -> {
                errorMessage.visibility = TextView.GONE

                // 10자 이내면 charCount와 밑줄 색상을 검정색으로 변경
                charCount.setTextColor(ContextCompat.getColor(this, android.R.color.black))
                editTextNickname.backgroundTintList = ColorStateList.valueOf(ContextCompat.getColor(this, android.R.color.black)) // 밑줄 색상 원래대로

                setButtonState(true) // 유효한 경우 버튼 활성화
            }
        }
    }

    private fun setButtonState(isValid: Boolean) {
        buttonNext.isEnabled = isValid
    }

    private fun isNicknameValid(nickname: String): Boolean {
        // 10자 이내면 유효한 닉네임으로 처리
        return nickname.length <= 10
    }
}
