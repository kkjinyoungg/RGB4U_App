package com.example.rgb4u_app.ui.activity.mypage

import android.content.Intent
import android.os.Bundle
import android.view.View
import android.widget.Button
import android.widget.ImageButton
import android.widget.TextView
import androidx.appcompat.app.AppCompatActivity
import com.example.rgb4u_app.R
import com.example.rgb4u_app.ui.activity.login.LoginActivity
import com.example.rgb4u_app.ui.fragment.ConfirmationDialogFragment
import com.google.android.material.switchmaterial.SwitchMaterial
/*
class MyPageMainActivity : AppCompatActivity() {

    private lateinit var switchPassword: SwitchMaterial
    private lateinit var tvChangePassword: TextView
    private lateinit var btnNotificationDetails: Button
    private lateinit var btnTermsOfServiceDetails: Button
    private lateinit var btnEditProfile: ImageButton // ImageButton으로 변경
    private lateinit var tvLogout: TextView
    private lateinit var tvDeleteAccount: TextView

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_my_page_main)

        // 비밀번호 설정 토글과 비밀번호 변경 메뉴
        switchPassword = findViewById(R.id.switch_password)
        tvChangePassword = findViewById(R.id.tv_change_password)
        tvLogout = findViewById(R.id.tv_logout) // 로그아웃 텍스트뷰 초기화
        tvDeleteAccount = findViewById(R.id.tv_delete_account) // 회원 탈퇴 텍스트뷰 초기화

        // 버튼 초기화
        btnNotificationDetails = findViewById(R.id.btn_notification_details)
        btnTermsOfServiceDetails = findViewById(R.id.btn_terms_of_service_details)
        btnEditProfile = findViewById(R.id.btn_edit_profile) // ImageButton으로 초기화

        // 토글에 따른 비밀번호 변경 메뉴 표시/숨김
        switchPassword.setOnCheckedChangeListener { _, isChecked ->
            tvChangePassword.visibility = if (isChecked) View.VISIBLE else View.GONE
        }


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

        // 로그아웃 클릭 리스너
        tvLogout.setOnClickListener {
            val dialog = ConfirmationDialogFragment(
                title = "로그아웃",
                message = "정말 로그아웃하시겠습니까?",
                onConfirm = {
                    val intent = Intent(this, LoginActivity::class.java)
                    startActivity(intent)
                    finish()  // 로그아웃 동작
                }
            )
            dialog.show(supportFragmentManager, "logoutConfirmation")
        }

        // 회원 탈퇴 클릭 리스너
        tvDeleteAccount.setOnClickListener {
            val dialog = ConfirmationDialogFragment(
                title = "회원 탈퇴",
                message = "정말 회원 탈퇴하시겠습니까?",
                onConfirm = {
                    // 회원 탈퇴 처리 로직
                    // 예: val intent = Intent(this, DeleteAccountActivity::class.java)
                    // startActivity(intent)
                }
            )
            dialog.show(supportFragmentManager, "deleteAccountConfirmation")
        }

    }
}
*/