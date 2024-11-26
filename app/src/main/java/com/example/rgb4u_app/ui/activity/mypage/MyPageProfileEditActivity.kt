package com.example.rgb4u_app.ui.activity.mypage

//파이어베이스
import android.content.Intent
import android.os.Build
import android.os.Bundle
import android.text.Editable
import android.text.TextWatcher
import android.util.Log
import android.view.View
import android.widget.Button
import android.widget.EditText
import android.widget.ImageButton
import android.widget.NumberPicker
import android.widget.TextView
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import androidx.core.content.ContextCompat
import androidx.core.content.res.ResourcesCompat
import com.example.rgb4u_app.R
import com.example.rgb4u_app.ui.activity.mypage.MyPageMainActivity
import com.example.rgb4u_app.ui.fragment.MypageCommonHeaderFragment
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.database.DataSnapshot
import com.google.firebase.database.DatabaseError
import com.google.firebase.database.DatabaseReference
import com.google.firebase.database.FirebaseDatabase
import com.google.firebase.database.ValueEventListener
import java.util.Calendar

class MyPageProfileEditActivity : AppCompatActivity() {

    private lateinit var nicknameEditText: EditText
    private lateinit var birthdateEditText: EditText
    private lateinit var calendarButton: ImageButton
    private lateinit var charCountTextView: TextView
    private lateinit var errorTextView: TextView
    private lateinit var buttonNext: Button
    private lateinit var database: DatabaseReference

    // userId는 nullable로 선언
    private val userId: String? = FirebaseAuth.getInstance().currentUser?.uid

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_my_page_profile_edit)

        // 투명바
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {
            window.apply {
                decorView.systemUiVisibility =
                    View.SYSTEM_UI_FLAG_LAYOUT_FULLSCREEN or
                            View.SYSTEM_UI_FLAG_LAYOUT_STABLE or
                            View.SYSTEM_UI_FLAG_LIGHT_STATUS_BAR
                statusBarColor = android.graphics.Color.TRANSPARENT
            }
        }


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

        // TextWatcher를 추가하여 글자 수 카운트
        nicknameEditText.addTextChangedListener(object : TextWatcher {
            override fun beforeTextChanged(s: CharSequence?, start: Int, count: Int, after: Int) {}

            override fun onTextChanged(s: CharSequence?, start: Int, before: Int, count: Int) {
                // 현재 글자 수 카운트
                val length = s?.length ?: 0
                charCountTextView.text = "$length/10" // 카운트 기준 10자로 설정
                // charCountTextView 색상 업데이트
                updateCharCountTextColor(length)

                // 글자 수에 따른 유효성 검사 및 테두리 색상 변경
                if (length == 0) {
                    setErrorState("닉네임을 입력해 주세요.")
                } else if (length > 10) {
                    setErrorState("10글자 이내로 입력할 수 있어요.")
                } else {
                    clearErrorState() // 에러 메시지 지우고 기본 테두리 색상으로 변경
                }

                // 테두리 색상 변경
                nicknameEditText.setBackgroundResource(
                    when {
                        length == 0 -> R.drawable.et_nickname_error_background // 입력되지 않았을 때
                        length > 10 -> R.drawable.et_nickname_error_background // 11자 초과
                        else -> R.drawable.et_nickname_main_background // 유효한 닉네임
                    }
                )
                // 버튼 상태 변경 (유효한 닉네임일 경우에만 활성화)
                buttonNext.isEnabled = length > 0 && length <= 10

                // UI 강제 갱신
                nicknameEditText.invalidate() // 뷰를 다시 그리도록 요청
            }
            override fun afterTextChanged(s: Editable?) {}
        })

        // 닉네임 입력 시 테두리 색상 변경
        nicknameEditText.setOnFocusChangeListener { _, hasFocus ->
            if (hasFocus) {
                nicknameEditText.setBackgroundResource(R.drawable.et_nickname_main_background)
                birthdateEditText.setBackgroundResource(R.drawable.et_nickname_default_background)
            } else {
                // 포커스를 잃었을 때 에러 상태를 확인하고 색상 설정
                val length = nicknameEditText.text.toString().length
                nicknameEditText.setBackgroundResource(
                    if (length > 11) R.drawable.et_nickname_error_background else R.drawable.et_nickname_default_background
                )
            }
        }


        // 달력 버튼 클릭 리스너
        calendarButton.setOnClickListener {
            showDatePickerDialog() // NumberPicker 다이얼로그 표시
        }

        // 완료 버튼 클릭 리스너
        buttonNext.setOnClickListener {
            val nickname = nicknameEditText.text.toString()
            val birthday = birthdateEditText.text.toString()

            if (validateNickname(nickname)) {
                // 유효한 닉네임일 경우 버튼 활성화
                buttonNext.isEnabled = true

                // userId가 null인지 확인
                userId?.let { uid ->
                    // Firebase에서 현재 닉네임과 생일을 읽어오기
                    database.child("users").child(uid).addListenerForSingleValueEvent(object : ValueEventListener {
                        override fun onDataChange(dataSnapshot: DataSnapshot) {
                            val existingNickname = dataSnapshot.child("nickname").getValue(String::class.java)
                            val existingBirthday = dataSnapshot.child("birthday").getValue(String::class.java)

                            // 기존 값과 비교
                            if (nickname == existingNickname && birthday == existingBirthday) {
                                // 변경된 사항이 없을 경우
                                Log.d("MyPageProfileEditActivity", "변경된 사항이 없습니다.")
                                // MyPageMainActivity로 이동
                                val intent = Intent(this@MyPageProfileEditActivity, MyPageMainActivity::class.java)
                                startActivity(intent)
                                finish() // 현재 액티비티 종료
                                return
                            }

                            // 닉네임과 생일을 Firebase에 저장
                            val nicknameUpdateTask = database.child("users").child(uid).child("nickname").setValue(nickname)
                            val birthdayUpdateTask = database.child("users").child(uid).child("birthday").setValue(birthday)

                            // 모든 저장 작업이 완료된 후에 토스트 메시지 표시
                            nicknameUpdateTask.addOnCompleteListener { nicknameTask ->
                                birthdayUpdateTask.addOnCompleteListener { birthdayTask ->
                                    if (nicknameTask.isSuccessful && birthdayTask.isSuccessful) {
                                        // 저장 성공 시
                                        Log.d("MyPageProfileEditActivity", "프로필이 변경되었습니다.")
                                        Toast.makeText(this@MyPageProfileEditActivity, "프로필이 변경되었어요", Toast.LENGTH_SHORT).show()
                                        // MyPageMainActivity로 이동
                                        val intent = Intent(this@MyPageProfileEditActivity, MyPageMainActivity::class.java)
                                        startActivity(intent)
                                        finish() // 현재 액티비티 종료
                                    } else {
                                        // 저장 실패 시
                                        Log.e("MyPageProfileEditActivity", "프로필 저장에 실패했습니다.")
                                    }
                                }
                            }
                        }

                        override fun onCancelled(databaseError: DatabaseError) {
                            // 오류 처리
                            Log.e("MyPageProfileEditActivity", "데이터를 불러오는 데 실패했습니다: ${databaseError.message}")
                        }
                    })
                } ?: run {
                    // userId가 null일 때 처리
                    Log.e("MyPageProfileEditActivity", "사용자 정보가 없습니다. 로그인을 확인해 주세요.")
                }
            } else {
                // 유효하지 않은 닉네임일 경우 버튼 비활성화
                buttonNext.isEnabled = false
                Log.e("MyPageProfileEditActivity", "유효한 닉네임을 입력해 주세요.")
            }
        }
    }

    private fun updateCharCountTextColor(length: Int) {
        // charCountTextView 색상 변경
        charCountTextView.setTextColor(
            when {
                length == 0 -> ContextCompat.getColor(this, R.color.highlight) // 입력되지 않았을 때
                length > 10 -> ContextCompat.getColor(this, R.color.highlight) // 10자 초과
                else -> ContextCompat.getColor(this, R.color.gray2) // 유효한 닉네임
            }
        )
    }

    private fun loadUserProfile() {
        // userId가 null인지 확인
        userId?.let { uid ->
            // Firebase에서 사용자 정보를 읽어오기
            database.child("users").child(uid).addListenerForSingleValueEvent(object : ValueEventListener {
                override fun onDataChange(dataSnapshot: DataSnapshot) {
                    val nickname = dataSnapshot.child("nickname").getValue(String::class.java)
                    val birthday = dataSnapshot.child("birthday").getValue(String::class.java)

                    // EditText에 값 설정
                    nicknameEditText.setText(nickname)
                    birthdateEditText.setText(birthday)
                }

                override fun onCancelled(databaseError: DatabaseError) {
                    // 오류 처리
                    Log.e("MyPageProfileEditActivity", "마이페이지 프로필 정보를 파이어베이스에서 불러오는 데 실패했습니다: ${databaseError.message}")
                }
            })
        } ?: run {
            // userId가 null일 때 처리
            Log.e("MyPageProfileEditActivity", "사용자 정보가 없습니다. 로그인을 확인해 주세요.")
        }
    }

    private fun showDatePickerDialog() {

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
        yearPicker.wrapSelectorWheel = false // 순환 비활성화
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


//        // 커스텀 폰트 적용
//        val customFont = ResourcesCompat.getFont(this, R.font.nanumsquareroundbold) // 폰트 파일 이름에 맞게 수정
//        applyCustomFont(yearPicker, customFont)
//        applyCustomFont(monthPicker, customFont)
//        applyCustomFont(dayPicker, customFont)

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
        // 날짜 선택 후 birthdateEditText의 테두리 색상을 main으로 변경
        birthdateEditText.setBackgroundResource(R.drawable.et_nickname_main_background)
        nicknameEditText.setBackgroundResource(R.drawable.et_nickname_default_background)
    }

//    private fun applyCustomFont(picker: NumberPicker, typeface: Typeface?) {
//        for (i in 0 until picker.childCount) {
//            val child = picker.getChildAt(i)
//            if (child is EditText) {
//                child.typeface = typeface
//            }
//        }
//    }

    private fun isLeapYear(year: Int): Boolean {
        return (year % 4 == 0 && year % 100 != 0) || (year % 400 == 0)
    }

    private fun validateNickname(nickname: String): Boolean {
        return when {
            nickname.length > 10 -> { // 10자를 초과하면
                errorTextView.text = "10자 이내로 작성해 주세요"
                errorTextView.visibility = TextView.VISIBLE

                // 글자 수와 관련된 UI 변경
                charCountTextView.setTextColor(ContextCompat.getColor(this, R.color.highlight))
                nicknameEditText.setBackgroundResource(R.drawable.et_nickname_error_background)

                // 버튼 비활성화
                buttonNext.isEnabled = false
                false // 유효하지 않음
            }
            else -> { // 유효한 닉네임
                errorTextView.visibility = TextView.GONE

                // 글자 수가 유효할 때
                charCountTextView.setTextColor(ContextCompat.getColor(this, R.color.black))
                nicknameEditText.setBackgroundResource(R.drawable.et_nickname_main_background)

                // 버튼 활성화
                buttonNext.isEnabled = true
                true // 유효함
            }
        }
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
