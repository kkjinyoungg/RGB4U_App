package com.example.rgb4u_app.ui.activity.login

import android.content.Intent
import android.os.Bundle
import android.text.Editable
import android.text.TextWatcher
import android.util.Log
import android.widget.ArrayAdapter
import android.widget.Button
import android.widget.EditText
import android.widget.ImageButton
import android.widget.ImageView
import android.widget.NumberPicker
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import com.example.rgb4u_app.R
import com.example.rgb4u_app.ui.activity.MainActivity
import java.util.Calendar
import com.google.firebase.database.FirebaseDatabase
import com.google.firebase.database.DatabaseReference
import android.widget.Spinner // Spinner import 추가

class SignUpActivity2 : AppCompatActivity() {

    private lateinit var birthdayInput: EditText
    private lateinit var buttonNext: Button
    private lateinit var buttonBack: ImageButton
    private lateinit var calendarBtn: ImageButton
    private lateinit var calendarIcon: ImageView
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

        // 버튼 초기 상태 설정 (비활성화)
        buttonNext.isEnabled = false
        buttonNext.alpha = 0.5f

        // 달력 버튼 클릭 시 스피너 방식으로 생년월일 선택
        calendarBtn.setOnClickListener {
            showDateSpinnerDialog()
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
                val userId = "userId" // 실제 사용자 ID로 변경
                database.child("users").child(userId).child("birthday").setValue(birthday) // 생일 저장
                    .addOnCompleteListener { task ->
                        if (task.isSuccessful) {
                            Log.d("SignUpActivity2", "save Birth successfully")
                            // 저장 성공 시 다음 단계로 이동
                            val intent = Intent(this, MainActivity::class.java)
                            startActivity(intent)
                            finish()
                        } else {
                            Log.d("SignUpActivity2", "Birthday is empty")
                            Toast.makeText(this, "생일을 입력해 주세요.", Toast.LENGTH_SHORT).show()
                        }
                    }
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
        yearPicker.value = currentYear
        yearPicker.setFormatter { value -> "${value}년" }

        monthPicker.minValue = 1
        monthPicker.maxValue = 12
        monthPicker.value = currentMonth
        monthPicker.setFormatter { value -> "${value}월" }

        dayPicker.minValue = 1
        dayPicker.maxValue = 31
        dayPicker.value = currentDay
        dayPicker.setFormatter { value -> "${value}일" }

        val updateDayPicker = {
            val selectedYear = yearPicker.value
            val selectedMonth = monthPicker.value
            val maxDays = when (selectedMonth) {
                2 -> if (isLeapYear(selectedYear)) 29 else 28
                4, 6, 9, 11 -> 30
                else -> 31
            }
            dayPicker.maxValue = maxDays
            if (dayPicker.value > maxDays) {
                dayPicker.value = maxDays
            }
        }

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

            birthdayInput.setText(formattedDate)
            dialog.dismiss()
        }

        dialog.show()
    }

    private fun updateDays(daySpinner: Spinner, year: Int, month: Int) {
        val daysInMonth = when (month) {
            2 -> if (isLeapYear(year)) 29 else 28
            4, 6, 9, 11 -> 30
            else -> 31
        }
        val days = (1..daysInMonth).toList()
        val dayAdapter = ArrayAdapter(this, android.R.layout.simple_spinner_item, days)
        // daySpinner.adapter = dayAdapter // 여전히 daySpinner가 사용되지 않음
    }

    private fun isLeapYear(year: Int): Boolean {
        return (year % 4 == 0 && year % 100 != 0) || (year % 400 == 0)
    }
}
