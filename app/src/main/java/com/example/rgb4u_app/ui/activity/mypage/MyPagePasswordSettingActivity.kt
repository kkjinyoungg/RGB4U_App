package com.example.rgb4u.ver1_app.ui.activity.mypage

import android.content.Intent
import android.os.Build
import android.os.Bundle
import android.view.View
import android.widget.Button
import android.widget.ImageButton
import android.widget.ImageView
import android.widget.TextView
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import androidx.core.content.ContextCompat
import com.example.rgb4u.ver1_app.R
import com.example.rgb4u.ver1_app.ui.activity.mypage.MyPageMainActivity
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.database.DatabaseReference
import com.google.firebase.database.FirebaseDatabase

class MyPagePasswordSettingActivity : AppCompatActivity() {
    private var password = "" // 입력된 비밀번호 저장
    private var confirmPassword = "" // 확인용 비밀번호 저장
    private lateinit var imageViews: Array<ImageView>
    private lateinit var userId: String

    // Firebase Database Reference
    private lateinit var database: DatabaseReference

    // 이미지 리소스 배열 (각 숫자에 맞는 이미지 리소스를 설정)
    private val passwordImages = arrayOf(
        R.drawable.ic_image_for_digit_1,
        R.drawable.ic_image_for_digit_2,
        R.drawable.ic_image_for_digit_3,
        R.drawable.ic_image_for_digit_4
    )

    private lateinit var tvNewPasswordTitle: TextView
    private lateinit var tvNewPasswordDescription: TextView
    private var isConfirmingPassword = false
    private var selectedButtonId: Int? = null

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_my_page_password_setting)

        // 상태바 투명
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {
            window.apply {
                decorView.systemUiVisibility =
                    View.SYSTEM_UI_FLAG_LAYOUT_FULLSCREEN or View.SYSTEM_UI_FLAG_LAYOUT_STABLE
                statusBarColor = android.graphics.Color.TRANSPARENT
            }
        }

        // Firebase 초기화
        database = FirebaseDatabase.getInstance().reference
        userId = FirebaseAuth.getInstance().currentUser?.uid ?: ""

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

                // 선택된 숫자의 색상 변경
                val buttonId = getButtonId(number)
                resetSelectedButtonColor()
                buttonId?.let {
                    findViewById<Button>(it).setTextColor(ContextCompat.getColor(this, R.color.selected_text_color)) // 선택 색상
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

                // 선택된 숫자의 색상 변경
                val buttonId = getButtonId(number)
                resetSelectedButtonColor()
                buttonId?.let {
                    findViewById<Button>(it).setTextColor(ContextCompat.getColor(this, R.color.selected_text_color)) // 선택 색상
                    selectedButtonId = it
                }
            }

            if (password.length >= 4) {
                tvNewPasswordTitle.text = "비밀번호 다시 입력"
                tvNewPasswordDescription.text = "확인을 위해 한 번 더 입력해 주세요"

                // **숫자 색상 초기화**
                resetAllButtonColors()
                resetAllImageResources() // 이미지 초기화
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
            findViewById<Button>(id).setTextColor(ContextCompat.getColor(this, R.color.black)) // 기본 색상으로 초기화
        }
    }

    private fun resetAllImageResources() {
        val imageViews = listOf(
            R.id.img_1, R.id.img_2, R.id.img_3, R.id.img_4,
            // 필요한 만큼 추가
        )

        // pwbefore 이미지 리소스 ID 설정
        val defaultImageResIds = listOf(
            R.drawable.ic_pwbefore_1,
            R.drawable.ic_pwbefore_2,
            R.drawable.ic_pwbefore_3,
            R.drawable.ic_pwbefore_4
        )

        imageViews.forEachIndexed { index, id ->
            val resourceId = if (index < defaultImageResIds.size) {
                defaultImageResIds[index]
            } else {
                R.drawable.rounded_divider_view // 기본으로 설정할 이미지
            }
            findViewById<ImageView>(id).setImageResource(resourceId)
        }
    }

    private fun onBackspaceClick() {
        if (isConfirmingPassword && confirmPassword.isNotEmpty()) {
            val lastChar = confirmPassword.last().toString()
            confirmPassword = confirmPassword.dropLast(1)
            resetButtonColor(lastChar, true)
            updatePasswordImages(true)
        } else if (!isConfirmingPassword && password.isNotEmpty()) {
            val lastChar = password.last().toString()
            password = password.dropLast(1)
            resetButtonColor(lastChar, false)
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

    private fun resetButtonColor(lastChar: String, isConfirming: Boolean) {
        val buttonId = getButtonId(lastChar)
        resetButtonColorById(buttonId)
    }

    private fun resetSelectedButtonColor() {
        resetButtonColorById(selectedButtonId)
    }

    private fun resetButtonColorById(buttonId: Int?) {
        buttonId?.let {
            findViewById<Button>(it).setTextColor(ContextCompat.getColor(this, R.color.black)) // 기본 검정색
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
            // 비밀번호가 일치하면 저장
            database.child("users").child(userId).child("password").setValue(confirmPassword)
                .addOnCompleteListener { task ->
                    if (task.isSuccessful) {
                        Toast.makeText(this, "비밀번호가 설정되었어요", Toast.LENGTH_SHORT).show()

                        // 토글 상태 저장
                        val sharedPreferences = getSharedPreferences("MyPrefs", MODE_PRIVATE)
                        with(sharedPreferences.edit()) {
                            putBoolean("switchPassword", true) // 비밀번호 설정 후 토글을 켠 상태로 저장
                            apply()
                        }

                        // MyPageMainActivity로 이동
                        val intent = Intent(this, MyPageMainActivity::class.java)
                        intent.putExtra("passwordSet", true) // 비밀번호 설정 상태 전달
                        startActivity(intent)
                        finish()
                    } else {
                        Toast.makeText(this, "비밀번호 저장 실패", Toast.LENGTH_SHORT).show()
                    }
                }
        } else {
            tvNewPasswordDescription.text = "비밀번호가 일치하지 않아요"
            tvNewPasswordDescription.setTextColor(ContextCompat.getColor(this, R.color.highlight_dark)) // 빨간색

            // 2초 후에 비밀번호 입력 초기화
            android.os.Handler().postDelayed({
                resetPasswordInput()
            }, 500) // 0.5초 대기
        }
    }

    private fun resetPasswordInput() {
        password = ""
        confirmPassword = ""
        updatePasswordImages(false) // 초기 비밀번호 이미지 리셋
        updatePasswordImages(true)  // 확인 비밀번호 이미지 리셋
        tvNewPasswordTitle.text = "비밀번호 입력"
        tvNewPasswordDescription.text = "사용하실 비밀번호를 입력해주세요"
        tvNewPasswordDescription.setTextColor(ContextCompat.getColor(this, R.color.blue2_30)) // 기본 색으로 초기화
        isConfirmingPassword = false
        resetSelectedButtonColor()  // 버튼 색상 초기화
    }
}
