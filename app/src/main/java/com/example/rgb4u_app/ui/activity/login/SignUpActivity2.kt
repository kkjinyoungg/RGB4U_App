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
import android.widget.Spinner
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import com.example.rgb4u_app.R
import com.example.rgb4u_app.ui.activity.MainActivity
import java.util.Calendar

class SignUpActivity2 : AppCompatActivity() {

    private lateinit var birthdayInput: EditText
    private lateinit var buttonNext: Button
    private lateinit var buttonBack: ImageButton
    private lateinit var calendarIcon: ImageView

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_sign_up2)

        birthdayInput = findViewById(R.id.birthdayInput)
        buttonNext = findViewById(R.id.buttonNext)
        buttonBack = findViewById(R.id.buttonBack)
        calendarIcon = findViewById(R.id.calendarIcon)

        // 버튼 초기 상태 설정 (비활성화)
        buttonNext.isEnabled = false
        buttonNext.alpha = 0.5f

        // 달력 아이콘 클릭 시 스피너 방식으로 생년월일 선택
        calendarIcon.setOnClickListener {
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
                val intent = Intent(this, MainActivity::class.java)
                intent.putExtra("BIRTHDAY", birthday)
                startActivity(intent)
                finish()
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


        val currentYear = Calendar.getInstance().get(Calendar.YEAR)
        yearPicker.minValue = currentYear - 100
        yearPicker.maxValue = currentYear
        yearPicker.setFormatter { value -> "${value.toString()}년" }

        monthPicker.minValue = 1
        monthPicker.maxValue = 12
        monthPicker.setFormatter { value -> "${value.toString()}월" }

        dayPicker.minValue = 1
        dayPicker.maxValue = 31
        dayPicker.setFormatter { value -> "${value.toString()}일" }

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
        daySpinner.adapter = dayAdapter
        daySpinner.setSelection(0) // 첫 번째 날로 설정
    }

    private fun isLeapYear(year: Int): Boolean {
        return (year % 4 == 0 && year % 100 != 0) || (year % 400 == 0)
    }
}
