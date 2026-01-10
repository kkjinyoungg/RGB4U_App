package com.example.rgb4u.ver1_app.ui.activity.mypage

import android.content.Intent
import android.os.Build
import android.os.Bundle
import android.view.View
import android.widget.Button
import androidx.appcompat.app.AppCompatActivity
import com.example.rgb4u.ver1_app.R
import com.example.rgb4u.ver1_app.ui.activity.login.LoginActivity

class MyPageDeleteaccoutActivity : AppCompatActivity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_my_page_deleteaccout)

        // 상태바 투명
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {
            window.apply {
                decorView.systemUiVisibility =
                    View.SYSTEM_UI_FLAG_LAYOUT_FULLSCREEN or
                            View.SYSTEM_UI_FLAG_LAYOUT_STABLE or
                            View.SYSTEM_UI_FLAG_LIGHT_STATUS_BAR
                statusBarColor = android.graphics.Color.TRANSPARENT
            }
        }

        // btn_reset 버튼 클릭 시 화면 전환
        val btnReset: Button = findViewById(R.id.btn_reset)
        btnReset.setOnClickListener {
            // TODO: 화면 전환할 액티비티를 결정하면 여기에 작성
            val intent = Intent(this, LoginActivity::class.java)
            startActivity(intent)
            finish()
        }
    }
}