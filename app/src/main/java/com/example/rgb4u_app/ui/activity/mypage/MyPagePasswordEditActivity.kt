package com.example.rgb4u_app.ui.activity.mypage

import android.content.Intent
import android.os.Bundle
import android.widget.Button
import android.widget.ImageButton
import android.widget.ImageView
import android.widget.TextView
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import androidx.core.content.ContextCompat
import com.example.rgb4u_app.R

class MyPagePasswordEditActivity : AppCompatActivity() {
    private var password = "" // 입력된 비밀번호 저장
    private var confirmPassword = "" // 확인용 비밀번호 저장
    private lateinit var imageViews: Array<ImageView>

    // 이미지 리소스 배열 (각 숫자에 맞는 이미지 리소스를 설정)
    private val passwordImages = arrayOf(
        R.drawable.ic_image_for_digit_1,
        R.drawable.ic_image_for_digit_2,
        R.drawable.ic_image_for_digit_3,
        R.drawable.ic_image_for_digit_4
    )

    private lateinit var tvPasswordTitle: TextView
    private lateinit var tvPasswordDescription: TextView
    private var isConfirmingPassword = false
    private var selectedButtonId: Int? = null

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
        tvPasswordTitle = findViewById(R.id.tv_new_password_title)
        tvPasswordDescription = findViewById(R.id.tv_new_password_description)

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
            val intent = Intent(this, MyPageMainActivity::class.java)
            intent.flags = Intent.FLAG_ACTIVITY_CLEAR_TOP or Intent.FLAG_ACTIVITY_NEW_TASK
            startActivity(intent)
            finish() // 현재 Activity 종료
        }
    }

    private fun onNumberButtonClick(number: String) {
        if (isConfirmingPassword) {
            if (confirmPassword.length < 4) {
                confirmPassword += number
                updatePasswordImages(true)

                val buttonId = getButtonId(number)
                resetSelectedButtonColor()
                buttonId?.let {
                    findViewById<Button>(it).setTextColor(ContextCompat.getColor(this, R.color.selected_text_color))
                    selectedButtonId = it
                }
            }

            if (confirmPassword.length >= 4) {
                checkPasswordMatch()
            }
        } else {
            if (password.length < 4) {
                password += number
                updatePasswordImages(false)

                val buttonId = getButtonId(number)
                resetSelectedButtonColor()
                buttonId?.let {
                    findViewById<Button>(it).setTextColor(ContextCompat.getColor(this, R.color.selected_text_color))
                    selectedButtonId = it
                }
            }

            if (password.length >= 4) {
                tvPasswordTitle.text = "비밀번호 다시 입력"
                tvPasswordDescription.text = "확인을 위해 한 번 더 입력해 주세요"
                resetAllButtonColors()
                isConfirmingPassword = true
            }
        }
    }

    private fun resetAllButtonColors() {
        val buttons = listOf(
            R.id.btn_0, R.id.btn_1, R.id.btn_2, R.id.btn_3,
            R.id.btn_4, R.id.btn_5, R.id.btn_6, R.id.btn_7,
            R.id.btn_8, R.id.btn_9
        )

        buttons.forEach { id ->
            findViewById<Button>(id).setTextColor(ContextCompat.getColor(this, R.color.default_text_color))
        }
    }

    private fun onBackspaceClick() {
        if (isConfirmingPassword && confirmPassword.isNotEmpty()) {
            val lastChar = confirmPassword.last().toString()
            confirmPassword = confirmPassword.dropLast(1)
            resetButtonColor(lastChar)
            updatePasswordImages(true)
        } else if (!isConfirmingPassword && password.isNotEmpty()) {
            val lastChar = password.last().toString()
            password = password.dropLast(1)
            resetButtonColor(lastChar)
            updatePasswordImages(false)
        }
    }

    private fun updatePasswordImages(isConfirming: Boolean) {
        val currentPassword = if (isConfirming) confirmPassword else password

        for (i in imageViews.indices) {
            val resourceId = if (i < currentPassword.length) {
                passwordImages[i]
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

    private fun resetButtonColor(lastChar: String) {
        val buttonId = getButtonId(lastChar)
        resetButtonColorById(buttonId)
    }

    private fun resetSelectedButtonColor() {
        resetButtonColorById(selectedButtonId)
    }

    private fun resetButtonColorById(buttonId: Int?) {
        buttonId?.let {
            findViewById<Button>(it).setTextColor(ContextCompat.getColor(this, R.color.default_text_color))
        }
    }

    private fun getButtonId(number: String): Int? {
        return when (number) {
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
    }

    private fun checkPasswordMatch() {
        if (password == confirmPassword) {
            Toast.makeText(this, "비밀번호가 설정되었어요", Toast.LENGTH_SHORT).show()
            val intent = Intent(this, MyPageMainActivity::class.java)
            intent.putExtra("passwordSet", true)
            startActivity(intent)
            finish()
        } else {
            tvPasswordDescription.text = "비밀번호가 일치하지 않습니다"
            tvPasswordDescription.setTextColor(ContextCompat.getColor(this, R.color.error_text_color))

            android.os.Handler().postDelayed({
                resetPasswordInput()
            }, 2000)
        }
    }

    private fun resetPasswordInput() {
        password = ""
        confirmPassword = ""
        updatePasswordImages(false)
        updatePasswordImages(true)
        tvPasswordTitle.text = "새로운 비밀번호 입력"
        tvPasswordDescription.text = "새로운 비밀번호를 입력해주세요"
        tvPasswordDescription.setTextColor(ContextCompat.getColor(this, R.color.default_text_color))
        isConfirmingPassword = false
        resetSelectedButtonColor()
    }
}
