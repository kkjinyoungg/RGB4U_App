package com.example.rgb4u_app.ui.activity.mypage

import android.app.DatePickerDialog
import android.content.Intent
import android.graphics.Color
import android.os.Bundle
import android.text.Editable
import android.text.InputFilter
import android.text.TextWatcher
import android.view.ViewGroup
import android.widget.Button
import android.widget.EditText
import android.widget.ImageButton
import android.widget.LinearLayout
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
        nicknameEditText = findViewById(R.id.et_nickname)
        birthdateEditText = findViewById(R.id.et_birthdate)
        calendarButton = findViewById(R.id.button_calendar)
        charCountTextView = findViewById(R.id.tv_char_count)
        errorTextView = TextView(this)
        buttonNext = findViewById(R.id.buttonNext)

        // 오류 메시지 TextView 설정
        errorTextView.layoutParams = LinearLayout.LayoutParams(
            LinearLayout.LayoutParams.WRAP_CONTENT,
            LinearLayout.LayoutParams.WRAP_CONTENT
        )
        errorTextView.setTextColor(Color.RED)
        errorTextView.textSize = 12f
        (nicknameEditText.parent as ViewGroup).addView(errorTextView)

        // 닉네임 글자 수 제한 및 특수 문자 필터링
        nicknameEditText.filters = arrayOf(
            InputFilter.LengthFilter(10),
            InputFilter { source, start, end, dest, dstart, dend ->
                for (i in start until end) {
                    if (!Character.isLetterOrDigit(source[i])) {
                        return@InputFilter "" // 특수 문자가 있으면 입력 안 함
                    }
                }
                null // 입력 허용
            }
        )

        // 글자 수 실시간 표시 및 오류 메시지 기능
        nicknameEditText.addTextChangedListener(object : TextWatcher {
            override fun beforeTextChanged(s: CharSequence?, start: Int, count: Int, after: Int) {}

            override fun onTextChanged(s: CharSequence?, start: Int, before: Int, count: Int) {
                val charCount = s?.length ?: 0
                charCountTextView.text = "$charCount/10"
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

            val datePickerDialog = DatePickerDialog(this, { _, selectedYear, selectedMonth, selectedDay ->
                val formattedDate = "${selectedYear}년 ${selectedMonth + 1}월 ${selectedDay}일"
                birthdateEditText.setText(formattedDate)
            }, year, month, day)

            datePickerDialog.show()
        }

        // 완료 버튼 클릭 리스너
        buttonNext.setOnClickListener {
            val nickname = nicknameEditText.text.toString()
            // 여기서 nickname을 저장하는 로직을 추가하세요

            // 토스트 메시지 표시
            Toast.makeText(this, "프로필이 변경되었어요", Toast.LENGTH_SHORT).show()

            // MyPageMainActivity로 이동
            val intent = Intent(this, MyPageMainActivity::class.java)
            startActivity(intent)
            finish() // 현재 액티비티 종료
        }

        // 뒤로가기 버튼 클릭 리스너 설정
        fragment.getBackButton().setOnClickListener {
            val nickname = nicknameEditText.text.toString()
            // 여기서 nickname을 저장하는 로직을 추가하세요

            val intent = Intent(this, MyPageMainActivity::class.java)
            startActivity(intent)
            finish() // 현재 액티비티 종료
        }
    }

    private fun validateNickname(nickname: String) {
        val isValid = nickname.length <= 10 && nickname.all { Character.isLetterOrDigit(it) }
        buttonNext.isEnabled = isValid // 버튼 활성화/비활성화
        errorTextView.text = when {
            nickname.length > 10 -> "10글자 이하로 입력해 줘!"
            nickname.isNotEmpty() && !nickname.all { Character.isLetterOrDigit(it) } -> "특수문자는 들어가면 안 돼!"
            else -> ""
        }
    }
}

