package com.example.rgb4u_app.ui.activity.mypage

import android.content.Intent
import android.os.Bundle
import android.view.View
import android.widget.Button
import android.widget.TextView
import androidx.appcompat.app.AppCompatActivity
import com.example.rgb4u_app.R
import com.google.android.material.switchmaterial.SwitchMaterial

class MyPageMainActivity : AppCompatActivity() {

    private lateinit var switchPassword: SwitchMaterial
    private lateinit var tvChangePassword: TextView
    private lateinit var btnNotificationDetails: Button
    private lateinit var btnTermsOfServiceDetails: Button
    private lateinit var btnEditProfile: Button

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_my_page_main)

        // 비밀번호 설정 토글과 비밀번호 변경 메뉴
        switchPassword = findViewById(R.id.switch_password)
        tvChangePassword = findViewById(R.id.tv_change_password)

        // 토글에 따른 비밀번호 변경 메뉴 표시/숨김
        switchPassword.setOnCheckedChangeListener { _, isChecked ->
            tvChangePassword.visibility = if (isChecked) View.VISIBLE else View.GONE
        }

        // 버튼 초기화
        btnNotificationDetails = findViewById(R.id.btn_notification_details)
        btnTermsOfServiceDetails = findViewById(R.id.btn_terms_of_service_details)
        btnEditProfile = findViewById(R.id.btn_edit_profile)

        // 버튼 클릭 리스너 설정
        btnNotificationDetails.setOnClickListener {
            val intent = Intent(this, MyPageNotificationSettingsActivity::class.java)
            startActivity(intent)
        }

        btnTermsOfServiceDetails.setOnClickListener {
            val intent = Intent(this, MyPageServiceCheck::class.java)
            startActivity(intent)
        }

        btnEditProfile.setOnClickListener {
            val intent = Intent(this, MyPageProfileEditActivity::class.java)
            startActivity(intent)
        }
    }
}
