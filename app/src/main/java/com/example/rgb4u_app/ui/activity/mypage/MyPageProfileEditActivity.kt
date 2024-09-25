package com.example.rgb4u_app.ui.activity.mypage

import android.app.DatePickerDialog
import android.content.Intent
import android.graphics.Color
import android.os.Bundle
import android.text.Editable
import android.text.InputFilter
import android.text.TextWatcher
import android.view.View
import android.widget.Button
import android.widget.EditText
import android.widget.ImageButton
import android.widget.TextView
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import com.example.rgb4u_app.R
import com.example.rgb4u_app.ui.fragment.MypageCommonHeaderFragment
import java.util.Calendar

class MyPageProfileEditActivity : AppCompatActivity() {

    private lateinit var nicknameEditText: EditText
    private lateinit var birthdateEditText: EditText
    private lateinit var calendarButton: ImageButton
    private lateinit var charCountTextView: TextView
    private lateinit var errorTextView: TextView
    private lateinit var buttonNext: Button

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_my_page_profile_edit)

        // Fragment 설정
        val fragment = MypageCommonHeaderFragment.newInstance("프로필 수정")
        supportFragmentManager.beginTransaction()
            .replace(R.id.fragment_container, fragment)
            .commit()

        // UI 요소 초기화
        initializeUI()

        // 뒤로가기 버튼 클릭 리스너 설정
        // Fragment의 View가 완전히 초기화된 이후에 Listener를 설정하도록 변경 필요
        supportFragmentManager.addOnBackStackChangedListener {
            fragment.view?.findViewById<View>(R.id.btn_back)?.setOnClickListener {
                onBackPressed()
            }
        }
    }

    private fun initializeUI() {
        nicknameEditText = findViewById(R.id.et_nickname)
        birthdateEditText = findViewById(R.id.et_birthdate)
        calendarButton = findViewById(R.id.button_calendar)
        charCountTextView = findViewById(R.id.tv_char_count)
        errorTextView = findViewById(R.id.tv_error_message) // XML에서 미리 정의
        buttonNext = findViewById(R.id.buttonNext)

        // 닉네임 글자 수 제한을 11자로 설정
        nicknameEditText.filters = arrayOf(InputFilter.LengthFilter(11))

        // charCountTextView에서 글자 수가 10자를 넘으면 텍스트 색상이 빨간색이 되도록 수정
        nicknameEditText.addTextChangedListener(object : TextWatcher {
            override fun beforeTextChanged(s: CharSequence?, start: Int, count: Int, after: Int) {}

            override fun onTextChanged(s: CharSequence?, start: Int, before: Int, count: Int) {
                val charCount = s?.length ?: 0
                charCountTextView.text = "$charCount/10"

                if (charCount > 10) {
                    charCountTextView.setTextColor(Color.RED)
                } else {
                    charCountTextView.setTextColor(Color.BLACK)
                }
                validateNickname(s.toString())
            }

            override fun afterTextChanged(s: Editable?) {}
        })

        // 달력 버튼 클릭 리스너
        calendarButton.setOnClickListener {
            val calendar = Calendar.getInstance()
            val year = calendar.get(Calendar.YEAR)
            val month = calendar.get(Calendar.MONTH)
            val day = calendar.get(Calendar.DAY_OF_MONTH)

            val datePickerDialog =
                DatePickerDialog(this, { _, selectedYear, selectedMonth, selectedDay ->
                    val formattedDate = "${selectedYear}년 ${selectedMonth + 1}월 ${selectedDay}일"
                    birthdateEditText.setText(formattedDate)
                }, year, month, day)

            datePickerDialog.show()
        }

        // 완료 버튼 클릭 리스너
        buttonNext.setOnClickListener {
            val nickname = nicknameEditText.text.toString()
            if (validateNickname(nickname)) {
                // 닉네임 저장 로직 추가

                // 토스트 메시지 표시
                Toast.makeText(this, "프로필이 변경되었어요", Toast.LENGTH_SHORT).show()

                // MyPageMainActivity로 이동
                val intent = Intent(this, MyPageMainActivity::class.java)
                startActivity(intent)
                finish() // 현재 액티비티 종료
            } else {
                Toast.makeText(this, "유효한 닉네임을 입력해 주세요", Toast.LENGTH_SHORT).show()
            }
        }
    }

    private fun validateNickname(nickname: String): Boolean {
        val isValid = nickname.length in 1..10 // 1~10자일 경우만 유효
        buttonNext.isEnabled = isValid // 버튼 활성화/비활성화
        errorTextView.text = if (nickname.length > 10) {
            "10글자 이하로 입력해 줘!"
        } else {
            ""
        }
        return isValid
    }
}
