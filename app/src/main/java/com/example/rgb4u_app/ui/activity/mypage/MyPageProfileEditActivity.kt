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
import com.example.rgb4u_app.ui.activity.mypage.MyPageMainActivity
//파이어베이스
import com.google.firebase.database.DatabaseReference
import com.google.firebase.database.FirebaseDatabase
import com.google.firebase.database.DataSnapshot
import android.util.Log
import com.google.firebase.database.ValueEventListener
import com.google.firebase.database.DatabaseError

class MyPageProfileEditActivity : AppCompatActivity() {

    private lateinit var nicknameEditText: EditText
    private lateinit var birthdateEditText: EditText
    private lateinit var calendarButton: ImageButton
    private lateinit var charCountTextView: TextView
    private lateinit var errorTextView: TextView
    private lateinit var buttonNext: Button
    private lateinit var database: DatabaseReference
    private val userId = "userId" // 실제 사용자 ID로 변경

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_my_page_profile_edit)

        // Firebase Database 초기화
        database = FirebaseDatabase.getInstance().reference

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

        // 사용자 프로필 로드
        loadUserProfile()  // 기존 사용자 정보 로드
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
            val birthday = birthdateEditText.text.toString()

            if (validateNickname(nickname)) {
                val userId = "userId" // 실제 사용자 ID로 변경

                // Firebase에서 현재 닉네임과 생일을 읽어오기
                database.child("users").child(userId).addListenerForSingleValueEvent(object : ValueEventListener {
                    override fun onDataChange(dataSnapshot: DataSnapshot) {
                        val existingNickname = dataSnapshot.child("nickname").getValue(String::class.java)
                        val existingBirthday = dataSnapshot.child("birthday").getValue(String::class.java)

                        // 기존 값과 비교
                        if (nickname == existingNickname && birthday == existingBirthday) {
                            //닉네임과 생일 둘 다 바뀐 것이 없이 값이 모두 같으면 Toast 메시지를 출력하지 않음
                            //Toast.makeText(this@MyPageProfileEditActivity, "변경된 사항이 없습니다.", Toast.LENGTH_SHORT).show(

                            // MyPageMainActivity로 이동
                            val intent = Intent(this@MyPageProfileEditActivity, MyPageMainActivity::class.java)
                            startActivity(intent)
                            finish() // 현재 액티비티 종료
                            return
                        }

                        // 닉네임과 생일을 Firebase에 저장
                        val nicknameUpdateTask = database.child("users").child(userId).child("nickname").setValue(nickname)
                        val birthdayUpdateTask = database.child("users").child(userId).child("birthday").setValue(birthday)

                        // 모든 저장 작업이 완료된 후에 토스트 메시지 표시
                        nicknameUpdateTask.addOnCompleteListener { nicknameTask ->
                            birthdayUpdateTask.addOnCompleteListener { birthdayTask ->
                                if (nicknameTask.isSuccessful && birthdayTask.isSuccessful) {
                                    // 저장 성공 시
                                    Toast.makeText(this@MyPageProfileEditActivity, "프로필이 변경되었어요", Toast.LENGTH_SHORT).show()

                                    // MyPageMainActivity로 이동
                                    val intent = Intent(this@MyPageProfileEditActivity, MyPageMainActivity::class.java)
                                    startActivity(intent)
                                    finish() // 현재 액티비티 종료
                                } else {
                                    // 저장 실패 시
                                    Toast.makeText(this@MyPageProfileEditActivity, "프로필 저장에 실패했습니다. 프로필을 다시 변경해 주세요.", Toast.LENGTH_SHORT).show()
                                }
                            }
                        }
                    }

                    override fun onCancelled(databaseError: DatabaseError) {
                        // 오류 처리
                        Toast.makeText(this@MyPageProfileEditActivity, "데이터를 불러오는 데 실패했습니다.", Toast.LENGTH_SHORT).show()
                    }
                })
            } else {
                Toast.makeText(this, "유효한 닉네임을 입력해 주세요", Toast.LENGTH_SHORT).show()
            }
        }

    }

    private fun loadUserProfile() {
        // Firebase에서 사용자 정보를 읽어오기
        database.child("users").child(userId).addListenerForSingleValueEvent(object : ValueEventListener {
            override fun onDataChange(dataSnapshot: DataSnapshot) {
                val nickname = dataSnapshot.child("nickname").getValue(String::class.java)
                val birthday = dataSnapshot.child("birthday").getValue(String::class.java)

                // EditText에 값 설정
                nicknameEditText.setText(nickname)
                birthdateEditText.setText(birthday)
            }

            override fun onCancelled(databaseError: DatabaseError) {
                // 오류 처리
                Log.e("MyPageProfileEditActivity", "마이페이지 프로필 정보를 파이어베이스에서 불러오는 데 실패했습니다: ${databaseError.message}")            }
        })
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
