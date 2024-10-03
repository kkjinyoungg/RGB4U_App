package com.example.rgb4u_app.ui.activity.mypage

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
import android.widget.NumberPicker
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

        // 뒤로가기 버튼 클릭 리스너 설정
        fragment.setBackButtonListener {
            onBackPressedDispatcher.onBackPressed()
        }


        // UI 요소 초기화
        initializeUI()
    }


    private fun initializeUI() {
        nicknameEditText = findViewById(R.id.et_nickname)
        birthdateEditText = findViewById(R.id.et_birthdate)
        calendarButton = findViewById(R.id.button_calendar)
        charCountTextView = findViewById(R.id.tv_char_count)
        errorTextView = findViewById(R.id.tv_error_message) // XML에서 미리 정의
        buttonNext = findViewById(R.id.buttonNext)
        clearErrorState()

        // 닉네임 글자 수 제한을 11자로 설정
        nicknameEditText.filters = arrayOf(InputFilter.LengthFilter(11))

        // charCountTextView에서 글자 수가 10자를 넘으면 텍스트 색상이 빨간색이 되도록 수정
        nicknameEditText.addTextChangedListener(object : TextWatcher {
            override fun beforeTextChanged(s: CharSequence?, start: Int, count: Int, after: Int) {}

            override fun onTextChanged(s: CharSequence?, start: Int, before: Int, count: Int) {
                val charCount = s?.length ?: 0
                charCountTextView.text = "$charCount/10"

                // 글자 수가 10자를 넘는 경우 텍스트 색상 변경
                charCountTextView.setTextColor(if (charCount > 10) Color.RED else Color.BLACK)

                validateNickname(s.toString())
            }

            override fun afterTextChanged(s: Editable?) {}
        })


        // 달력 버튼 클릭 리스너
        calendarButton.setOnClickListener {
            showDatePickerDialog() // NumberPicker 다이얼로그 표시
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

    private fun showDatePickerDialog() {
        val dialogView = layoutInflater.inflate(R.layout.number_picker_dialog, null)

        val yearPicker = dialogView.findViewById<NumberPicker>(R.id.yearPicker)
        val monthPicker = dialogView.findViewById<NumberPicker>(R.id.monthPicker)
        val dayPicker = dialogView.findViewById<NumberPicker>(R.id.dayPicker)

        // 현재 날짜 가져오기
        val calendar = Calendar.getInstance()
        val currentYear = calendar.get(Calendar.YEAR)
        val currentMonth = calendar.get(Calendar.MONTH) + 1 // 월은 0부터 시작하므로 +1
        val currentDay = calendar.get(Calendar.DAY_OF_MONTH)

        yearPicker.minValue = currentYear - 100
        yearPicker.maxValue = currentYear
        yearPicker.value = currentYear // 현재 연도로 기본 설정
        yearPicker.setFormatter { value -> "${value}년" }

        monthPicker.minValue = 1
        monthPicker.maxValue = 12
        monthPicker.value = currentMonth // 현재 월로 기본 설정
        monthPicker.setFormatter { value -> "${value}월" }

        dayPicker.minValue = 1
        dayPicker.maxValue = 31
        dayPicker.value = currentDay // 현재 일로 기본 설정
        dayPicker.setFormatter { value -> "${value}일" }

        // MonthPicker나 YearPicker가 변경될 때마다 dayPicker의 최대 일수를 업데이트
        val updateDayPicker = {
            val selectedYear = yearPicker.value
            val selectedMonth = monthPicker.value
            val maxDays = when (selectedMonth) {
                2 -> if (isLeapYear(selectedYear)) 29 else 28
                4, 6, 9, 11 -> 30
                else -> 31
            }
            dayPicker.maxValue = maxDays
            // 현재 선택된 날짜가 최대 일수를 초과하지 않도록 설정
            if (dayPicker.value > maxDays) {
                dayPicker.value = maxDays
            }
        }

        // MonthPicker와 YearPicker에 변경 리스너 추가
        monthPicker.setOnValueChangedListener { _, _, _ -> updateDayPicker() }
        yearPicker.setOnValueChangedListener { _, _, _ -> updateDayPicker() }

        val dialog = android.app.AlertDialog.Builder(this)
            .setView(dialogView)
            .create()

        dialogView.findViewById<Button>(R.id.btn_confirm).setOnClickListener {
            val selectedYear = yearPicker.value
            val selectedMonth = monthPicker.value
            val selectedDay = dayPicker.value
            val formattedDate = "${selectedYear}년 ${selectedMonth}월 ${selectedDay}일"

            birthdateEditText.setText(formattedDate)
            dialog.dismiss()
        }

        dialog.show()
    }

    private fun isLeapYear(year: Int): Boolean {
        return (year % 4 == 0 && year % 100 != 0) || (year % 400 == 0)
    }



    private fun validateNickname(nickname: String): Boolean {
        val isValid = nickname.length in 1..10
        buttonNext.isEnabled = isValid

        if (nickname.length > 10) {
            setErrorState("10글자 이하로 입력해 줘!")
        } else {
            clearErrorState()
        }

        return isValid
    }

    private fun setErrorState(message: String) {
        errorTextView.text = message
        errorTextView.visibility = View.VISIBLE
        nicknameEditText.setBackgroundResource(R.drawable.et_nickname_error_background)
    }

    private fun clearErrorState() {
        errorTextView.text = ""
        errorTextView.visibility = View.GONE
        nicknameEditText.setBackgroundResource(R.drawable.et_nickname_default_background)
    }



}
