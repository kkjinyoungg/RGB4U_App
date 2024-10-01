package com.example.rgb4u_app.ui.activity.login

import android.app.AlertDialog
import android.content.Intent
import android.os.Bundle
import android.text.Editable
import android.text.TextWatcher
import android.util.Log
import android.view.View
import android.widget.AdapterView
import android.widget.ArrayAdapter
import android.widget.Button
import android.widget.EditText
import android.widget.ImageButton
import android.widget.ImageView
import android.widget.LinearLayout
import android.widget.Spinner
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import com.example.rgb4u_app.R
import com.example.rgb4u_app.ui.activity.MainActivity
import java.util.Calendar
import com.google.firebase.database.FirebaseDatabase
import com.google.firebase.database.DatabaseReference

class SignUpActivity2 : AppCompatActivity() {

    private lateinit var birthdayInput: EditText
    private lateinit var buttonNext: Button
    private lateinit var calendarIcon: ImageView
    private lateinit var buttonBack: ImageButton // 뒤로가기 버튼을 위한 변수 추가
    private lateinit var database: DatabaseReference //데이터베이스 접근용 변수

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_sign_up2)

        birthdayInput = findViewById(R.id.birthdayInput)
        buttonNext = findViewById(R.id.buttonNext)
        buttonBack = findViewById(R.id.buttonBack)
        calendarIcon = findViewById(R.id.calendarIcon)

        // Firebase Database 초기화
        database = FirebaseDatabase.getInstance().reference

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
                val userId = "userId" // 실제 사용자 ID로 변경
                database.child("users").child(userId).child("birthday").setValue(birthday) //생일 저장
                    .addOnCompleteListener { task ->
                        if (task.isSuccessful) {
                            Log.d("SignUpActivity2", "save Birth successfully")
                            // 저장 성공 시 다음 단계로 이동
                            val intent = Intent(this, MainActivity::class.java)
                            //intent.putExtra("BIRTHDAY", birthday)
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
        val years = (1900..Calendar.getInstance().get(Calendar.YEAR)).toList()
        val months = (1..12).toList()
        val days = (1..31).toList()

        val builder = AlertDialog.Builder(this)
        builder.setTitle("생년월일 선택")

        // 스피너 배열 생성
        val yearAdapter = ArrayAdapter(this, android.R.layout.simple_spinner_item, years)
        val monthAdapter = ArrayAdapter(this, android.R.layout.simple_spinner_item, months)
        val dayAdapter = ArrayAdapter(this, android.R.layout.simple_spinner_item, days)

        // 선택된 값 저장
        var selectedYear = years.first()
        var selectedMonth = months.first()
        var selectedDay = days.first()

        // 스피너 레이아웃 생성
        val layout = LinearLayout(this)
        layout.orientation = LinearLayout.VERTICAL

        val yearSpinner = Spinner(this)
        yearSpinner.adapter = yearAdapter
        yearSpinner.setSelection(years.size - 1) // 현재 연도로 설정

        val monthSpinner = Spinner(this)
        monthSpinner.adapter = monthAdapter

        val daySpinner = Spinner(this)
        daySpinner.adapter = dayAdapter

        // 스피너 선택 리스너
        yearSpinner.onItemSelectedListener = object : AdapterView.OnItemSelectedListener {
            override fun onItemSelected(parent: AdapterView<*>, view: View?, position: Int, id: Long) {
                selectedYear = years[position]
                updateDays(daySpinner, selectedYear, selectedMonth)
            }

            override fun onNothingSelected(parent: AdapterView<*>) {}
        }

        monthSpinner.onItemSelectedListener = object : AdapterView.OnItemSelectedListener {
            override fun onItemSelected(parent: AdapterView<*>, view: View?, position: Int, id: Long) {
                selectedMonth = months[position]
                updateDays(daySpinner, selectedYear, selectedMonth)
            }

            override fun onNothingSelected(parent: AdapterView<*>) {}
        }

        daySpinner.onItemSelectedListener = object : AdapterView.OnItemSelectedListener {
            override fun onItemSelected(parent: AdapterView<*>, view: View?, position: Int, id: Long) {
                selectedDay = days[position]
            }

            override fun onNothingSelected(parent: AdapterView<*>) {}
        }

        layout.addView(yearSpinner)
        layout.addView(monthSpinner)
        layout.addView(daySpinner)

        builder.setView(layout)
        builder.setPositiveButton("확인") { _, _ ->
            birthdayInput.setText(String.format("%04d년 %02d월 %02d일", selectedYear, selectedMonth, selectedDay))
        }
        builder.setNegativeButton("취소", null)

        builder.show()
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
