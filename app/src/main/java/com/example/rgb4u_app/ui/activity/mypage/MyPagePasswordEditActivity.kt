package com.example.rgb4u_app.ui.activity.mypage

import android.content.Intent
import android.os.Bundle
import android.widget.Button
import android.widget.ImageButton
import android.widget.ImageView
import android.widget.TextView
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import com.example.rgb4u_app.R

class MyPagePasswordEditActivity : AppCompatActivity() {

    private var password = StringBuilder() // 입력된 비밀번호 저장
    private var confirmPassword = StringBuilder() // 확인용 비밀번호 저장
    private lateinit var imageViews: Array<ImageView>
    // 이미지 리소스 배열 (각 숫자에 맞는 이미지 리소스를 설정)
    private val passwordImages = arrayOf(
        R.drawable.ic_image_for_digit_1, // 1에 해당하는 이미지
        R.drawable.ic_image_for_digit_2, // 2에 해당하는 이미지
        R.drawable.ic_image_for_digit_3, // 3에 해당하는 이미지
        R.drawable.ic_image_for_digit_4  // 4에 해당하는 이미지
    )

    private lateinit var tvNewPasswordTitle: TextView
    private lateinit var tvNewPasswordDescription: TextView

    private var isConfirmingPassword = false // 비밀번호 확인 모드 플래그

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_my_page_password_edit)

        // 이미지 뷰 초기화
        imageViews = arrayOf(
            findViewById(R.id.img_1),
            findViewById(R.id.img_2),
            findViewById(R.id.img_3),
            findViewById(R.id.img_4)
        )

        // 타이틀과 설명 텍스트뷰 초기화
        tvNewPasswordTitle = findViewById(R.id.tv_new_password_title)
        tvNewPasswordDescription = findViewById(R.id.tv_new_password_description)

        // 각 번호 버튼에 대해 클릭 리스너 설정
        val buttons = listOf(
            R.id.btn_0, R.id.btn_1, R.id.btn_2, R.id.btn_3,
            R.id.btn_4, R.id.btn_5, R.id.btn_6, R.id.btn_7,
            R.id.btn_8, R.id.btn_9
        )

        buttons.forEach { id ->
            findViewById<Button>(id).setOnClickListener { button ->
                onNumberButtonClick((button as Button).text.toString())
            }
        }

        // 백스페이스 버튼 클릭 처리
        findViewById<ImageButton>(R.id.btn_backspace).setOnClickListener {
            onBackspaceClick()
        }

        // 뒤로 가기 버튼 처리
        findViewById<ImageButton>(R.id.btn_back).setOnClickListener {
            startActivity(Intent(this, MyPageMainActivity::class.java))
            finish() // 현재 Activity 종료
        }
    }

    // 번호 버튼 클릭 시 호출되는 함수
    private fun onNumberButtonClick(number: String) {
        if (isConfirmingPassword) {
            // 비밀번호 확인 모드일 때
            if (confirmPassword.length < 4) {
                confirmPassword.append(number)
                updatePasswordImages(true) // 두 번째 입력용 이미지 업데이트
            }

            // 두 번째 비밀번호가 4자리 입력되면 일치 여부 확인
            if (confirmPassword.length == 4) {
                checkPasswordMatch()
            }
        } else {
            // 첫 번째 비밀번호 입력
            if (password.length < 4) {
                password.append(number)
                updatePasswordImages(false) // 첫 번째 입력용 이미지 업데이트

                // 선택된 숫자의 색상 변경
                val buttonId = when (number) {
                    "0" -> R.id.btn_0
                    "1" -> R.id.btn_1
                    "2" -> R.id.btn_2
                    "3" -> R.id.btn_3
                    "4" -> R.id.btn_4
                    "5" -> R.id.btn_5
                    "6" -> R.id.btn_6
                    "7" -> R.id.btn_7
                    "8" -> R.id.btn_8
                    "9" -> R.id.btn_9
                    else -> null
                }
                buttonId?.let {
                    findViewById<Button>(it).setTextColor(0xFF6883B8.toInt()) // 선택 색상으로 변경
                }
            }

            // 비밀번호가 4자리 입력되면 다음 동작을 처리
            if (password.length == 4) {
                tvNewPasswordTitle.text = "새로운 비밀번호 다시 입력"
                tvNewPasswordDescription.text = "확인을 위해 한 번 더 입력해 주세요"
                isConfirmingPassword = true // 비밀번호 확인 모드로 전환
            }
        }
    }

    // 백스페이스 버튼 클릭 시 호출되는 함수
    private fun onBackspaceClick() {
        if (isConfirmingPassword && confirmPassword.isNotEmpty()) {
            val lastChar = confirmPassword.last().toString()
            confirmPassword.deleteCharAt(confirmPassword.length - 1)
            resetButtonColor(lastChar, true) // 두 번째 입력일 경우
            updatePasswordImages(true)
        } else if (!isConfirmingPassword && password.isNotEmpty()) {
            val lastChar = password.last().toString()
            password.deleteCharAt(password.length - 1)
            resetButtonColor(lastChar, false) // 첫 번째 입력일 경우
            updatePasswordImages(false)
        }
    }

    // 입력된 비밀번호에 따라 이미지 변경
    private fun updatePasswordImages(isConfirming: Boolean) {
        val currentPassword = if (isConfirming) confirmPassword else password

        for (i in imageViews.indices) {
            val resourceId = if (i < currentPassword.length) {
                when (i) {
                    0 -> R.drawable.ic_image_for_digit_1
                    1 -> R.drawable.ic_image_for_digit_2
                    2 -> R.drawable.ic_image_for_digit_3
                    3 -> R.drawable.ic_image_for_digit_4
                    else -> R.drawable.rounded_divider_view
                }
            } else {
                when (i) {
                    0 -> R.drawable.ic_pwbefore_1
                    1 -> R.drawable.ic_pwbefore_2
                    2 -> R.drawable.ic_pwbefore_3
                    3 -> R.drawable.ic_pwbefore_4
                    else -> R.drawable.rounded_divider_view
                }
            }
            imageViews[i].setImageResource(resourceId)
        }
    }

    // 마지막으로 입력한 숫자의 버튼 색상 원래대로 돌리기
    private fun resetButtonColor(lastChar: String, isConfirming: Boolean) {
        val buttonId = when (lastChar) {
            "0" -> R.id.btn_0
            "1" -> R.id.btn_1
            "2" -> R.id.btn_2
            "3" -> R.id.btn_3
            "4" -> R.id.btn_4
            "5" -> R.id.btn_5
            "6" -> R.id.btn_6
            "7" -> R.id.btn_7
            "8" -> R.id.btn_8
            "9" -> R.id.btn_9
            else -> null
        }
        buttonId?.let {
            findViewById<Button>(it).setTextColor(0xFF000000.toInt()) // 기본 검정색으로 설정
        }
    }

    private fun checkPasswordMatch() {
        if (password.toString() == confirmPassword.toString()) {
            Toast.makeText(this, "비밀번호가 변경되었습니다", Toast.LENGTH_SHORT).show()
            // MyPageMainActivity로 이동
            startActivity(Intent(this, MyPageMainActivity::class.java))
            finish() // 현재 Activity 종료
        } else {
            tvNewPasswordDescription.text = "비밀번호가 일치하지 않습니다"
            resetPasswordInput()
        }
    }

    // 비밀번호가 다를 경우 초기화
    private fun resetPasswordInput() {
        password.clear()
        confirmPassword.clear()
        updatePasswordImages(false)
        tvNewPasswordTitle.text = "새로운 비밀번호 입력"
        tvNewPasswordDescription.text = "새로운 비밀번호를 입력해주세요"
        isConfirmingPassword = false // 첫 번째 입력 모드로 전환
    }
}
