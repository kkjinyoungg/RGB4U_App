package com.example.rgb4u_app.ui.activity

import android.content.Intent
import android.os.Bundle
import android.os.Handler
import android.os.Looper
import android.widget.Button
import android.widget.ImageButton
import android.widget.ImageView
import android.widget.TextView
import androidx.appcompat.app.AppCompatActivity
import androidx.core.content.ContextCompat
import com.example.rgb4u_app.R
import com.example.rgb4u_app.ui.activity.home.MainActivity

class PasswordEntryActivity : AppCompatActivity() {
    private var password = "" // 입력된 비밀번호 저장
    private lateinit var imageViews: Array<ImageView>

    // 사전에 설정한 비밀번호 (예시로 "1234"를 설정)
    private val correctPassword = "1234"

    // 이미지 리소스 배열 (각 숫자에 맞는 이미지 리소스를 설정)
    private val passwordImages = arrayOf(
        R.drawable.ic_image_for_digit_1,
        R.drawable.ic_image_for_digit_2,
        R.drawable.ic_image_for_digit_3,
        R.drawable.ic_image_for_digit_4
    )

    // 텍스트뷰 등 UI 요소 초기화
    private lateinit var tvstartPasswordTitle: TextView
    private lateinit var tvstartPasswordDescription: TextView
    private var selectedButtonId: Int? = null

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_password_entry)

        // 이미지 뷰 초기화
        imageViews = arrayOf(
            findViewById(R.id.img_1),
            findViewById(R.id.img_2),
            findViewById(R.id.img_3),
            findViewById(R.id.img_4)
        )

        // 타이틀과 설명 텍스트뷰 초기화
        tvstartPasswordTitle = findViewById(R.id.tv_start_password_title)
        tvstartPasswordDescription = findViewById(R.id.tv_start_password_description)

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
    }

    private fun onNumberButtonClick(number: String) {
        if (password.length < 4) {
            password += number
            updatePasswordImages()

            // 선택된 숫자의 색상 변경
            val buttonId = getButtonId(number)
            resetSelectedButtonColor()
            buttonId?.let {
                findViewById<Button>(it).setTextColor(ContextCompat.getColor(this, R.color.selected_text_color)) // 선택 색상
                selectedButtonId = it
            }
        }

        if (password.length == 4) {
            // 비밀번호가 4자리 입력되면 확인
            if (password == correctPassword) {
                // 비밀번호가 맞으면 MainActivity로 이동
                val intent = Intent(this, MainActivity::class.java)
                startActivity(intent)
                finish()
            } else {
                // 비밀번호가 틀리면 입력 초기화
                password = ""
                tvstartPasswordTitle.text = "비밀번호 입력"
                tvstartPasswordDescription.text = "비밀번호가 일치하지 않아요"
                tvstartPasswordDescription.setTextColor(ContextCompat.getColor(this, R.color.error_text_color)) // 텍스트 색상 빨간색으로 변경
                resetAllImageResources() // 이미지 리셋
                resetAllButtonColors()  // 버튼 색상 리셋

                // 일정 시간 후 텍스트를 원래대로 변경
                Handler(Looper.getMainLooper()).postDelayed({
                    tvstartPasswordDescription.text = "비밀번호를 입력해주세요"
                    tvstartPasswordDescription.setTextColor(ContextCompat.getColor(this, R.color.white)) // 기본 텍스트 색상으로 변경
                }, 2000) // 2000ms (2초) 후에 텍스트 변경
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
            R.id.img_1, R.id.img_2, R.id.img_3, R.id.img_4
        )

        // 초기 상태의 이미지 리소스 배열
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
                R.drawable.rounded_divider_view // 기본 이미지
            }
            findViewById<ImageView>(id).setImageResource(resourceId)
        }
    }

    private fun onBackspaceClick() {
        if (password.isNotEmpty()) {
            val lastChar = password.last().toString()
            password = password.dropLast(1)
            resetButtonColor(lastChar)
            updatePasswordImages()
        }
    }

    private fun updatePasswordImages() {
        for (i in imageViews.indices) {
            val resourceId = if (i < password.length) {
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
            findViewById<Button>(it).setTextColor(ContextCompat.getColor(this, R.color.black)) // 기본 색상
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
}
