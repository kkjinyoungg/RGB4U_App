package com.example.rgb4u_app.ui.activity.login

import android.content.Intent
import android.os.Bundle
import android.text.Editable
import android.text.TextWatcher
import android.util.Log
import android.widget.Button
import android.widget.EditText
import android.widget.ImageButton
import android.widget.NumberPicker
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import com.example.rgb4u_app.R
import com.example.rgb4u_app.ui.activity.onboarding.OnboardingActivity
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.database.DatabaseReference
import com.google.firebase.database.FirebaseDatabase
import java.util.Calendar


class SignUpActivity2 : AppCompatActivity() {

    private lateinit var birthdayInput: EditText
    private lateinit var buttonNext: Button
    private lateinit var buttonBack: ImageButton
    private lateinit var calendarBtn: ImageButton
    private lateinit var database: DatabaseReference

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_sign_up2)

        birthdayInput = findViewById(R.id.birthdayInput)
        buttonNext = findViewById(R.id.buttonNext)
        buttonBack = findViewById(R.id.buttonBack)
        calendarBtn = findViewById(R.id.calendarBtn)

        // Firebase Database 초기화
        database = FirebaseDatabase.getInstance().reference

        // 버튼 초기 상태 설정 (활성화)
        buttonNext.isEnabled = false

        // 오늘 날짜를 가져와서 초기 값 설정
        val calendar = Calendar.getInstance()
        val year = calendar.get(Calendar.YEAR)
        val month = calendar.get(Calendar.MONTH) + 1 // Calendar.MONTH는 0부터 시작
        val day = calendar.get(Calendar.DAY_OF_MONTH)
        val today = "${year}년 ${month}월 ${day}일"
        birthdayInput.setText(today)

        // 달력 버튼 클릭 시 스피너 방식으로 생년월일 선택
        calendarBtn.setOnClickListener {
            showDateSpinnerDialog()
            birthdayInput.setBackgroundResource(R.drawable.et_nickname_main_background) //테두리

        }

        buttonBack.setOnClickListener {
            val intent = Intent(this, SignUpActivity1::class.java)
            startActivity(intent)
            finish()
        }

        buttonNext.setOnClickListener {
            val birthday = birthdayInput.text.toString()
            if (birthday.isNotEmpty()) {
                Log.d("SignUpActivity2", "Birthday is valid: $birthday")

                // 현재 로그인된 사용자의 UID를 가져오는 함수
                val userId = FirebaseAuth.getInstance().currentUser?.uid

                if (userId != null) {
                    // UID가 null이 아닌 경우에만 데이터를 저장
                    database.child("users").child(userId).child("birthday").setValue(birthday)
                        .addOnCompleteListener { task ->
                            if (task.isSuccessful) {
                                Log.d("SignUpActivity2", "Birth saved successfully")
                                // 저장 성공 시 다음 단계로 이동
                                val intent = Intent(this, OnboardingActivity::class.java)
                                startActivity(intent)
                                finish()
                            } else {
                                Log.e("FirebaseError", "ID는 있지만 생일 저장에 실패했습니다: ${task.exception?.message}")
                                //Toast.makeText(this, "저장에 실패했습니다.", Toast.LENGTH_SHORT).show()
                            }
                        }
                } else {
                    // UID가 null인 경우 처리
                    Log.e("FirebaseAuthError", "로그인이 되지 않아 생일 저장에 실패했습니다")
                    //Toast.makeText(this, "로그인이 되지 않았어요", Toast.LENGTH_SHORT).show()
                }
            } else {
                Log.d("SignUpActivity2", "Birthday is empty")
                Toast.makeText(this, "생일을 입력해 주세요.", Toast.LENGTH_SHORT).show()
            }
        }

        birthdayInput.addTextChangedListener(object : TextWatcher {
            override fun beforeTextChanged(s: CharSequence?, start: Int, count: Int, after: Int) {}

            override fun onTextChanged(s: CharSequence?, start: Int, before: Int, count: Int) {}

            override fun afterTextChanged(s: Editable?) {
                val isBirthdayValid = s.toString().isNotEmpty()
                buttonNext.isEnabled = isBirthdayValid
                buttonNext.alpha = if (isBirthdayValid) 1f else 0.5f
            }
        })
    }

    private fun showDateSpinnerDialog() {
        val dialogView = layoutInflater.inflate(R.layout.number_picker_dialog, null)

        val yearPicker = dialogView.findViewById<NumberPicker>(R.id.yearPicker)
        val monthPicker = dialogView.findViewById<NumberPicker>(R.id.monthPicker)
        val dayPicker = dialogView.findViewById<NumberPicker>(R.id.dayPicker)

        val calendar = Calendar.getInstance()
        val currentYear = calendar.get(Calendar.YEAR)
        val currentMonth = calendar.get(Calendar.MONTH) + 1
        val currentDay = calendar.get(Calendar.DAY_OF_MONTH)
        yearPicker.minValue = currentYear - 100
        yearPicker.maxValue = currentYear
        yearPicker.wrapSelectorWheel = false // 순환 비활성화
        yearPicker.value = currentYear
        yearPicker.displayedValues = Array(yearPicker.maxValue - yearPicker.minValue + 1) { i ->
            "${yearPicker.minValue + i}년"
        }

        monthPicker.minValue = 1
        monthPicker.maxValue = 12
        monthPicker.value = currentMonth
        monthPicker.wrapSelectorWheel = false // 순환 비활성화
        monthPicker.displayedValues = Array(monthPicker.maxValue) { i ->
            "${i + 1}월"
        }

        dayPicker.minValue = 1
        dayPicker.maxValue = 31
        dayPicker.value = currentDay
        dayPicker.wrapSelectorWheel = false // 순환 비활성화
        dayPicker.displayedValues = Array(dayPicker.maxValue) { i ->
            "${i + 1}일"
        }

        val updateDayPicker = {
            val selectedYear = yearPicker.value
            val selectedMonth = monthPicker.value
            val maxDays = when (selectedMonth) {
                2 -> if (isLeapYear(selectedYear)) 29 else 28
                4, 6, 9, 11 -> 30
                else -> 31
            }
            dayPicker.maxValue = maxDays
            dayPicker.displayedValues = Array(maxDays) { i -> "${i + 1}일" }
            if (dayPicker.value > maxDays) {
                dayPicker.value = maxDays
            }
        }

        monthPicker.setOnValueChangedListener { _, _, _ -> updateDayPicker() }
        yearPicker.setOnValueChangedListener { _, _, _ -> updateDayPicker() }

        // Update day picker initially
        updateDayPicker()

        val dialog = android.app.AlertDialog.Builder(this)
            .setView(dialogView)
            .create()

        dialogView.findViewById<Button>(R.id.btn_confirm).setOnClickListener {
            val selectedYear = yearPicker.value
            val selectedMonth = monthPicker.value
            val selectedDay = dayPicker.value
            val formattedDate = "${selectedYear}년 ${selectedMonth}월 ${selectedDay}일"

            birthdayInput.setText(formattedDate)
            dialog.dismiss()
        }

        dialog.show()
    }


    private fun isLeapYear(year: Int): Boolean {
        return (year % 4 == 0 && year % 100 != 0) || (year % 400 == 0)
    }
}