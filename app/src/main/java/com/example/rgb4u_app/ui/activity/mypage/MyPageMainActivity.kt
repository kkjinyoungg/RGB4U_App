package com.example.rgb4u_app.ui.activity.mypage

import android.content.Intent
import android.os.Bundle
import android.view.View
import android.widget.ImageButton
import android.widget.LinearLayout
import android.widget.TextView
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import com.example.rgb4u_app.R
import com.example.rgb4u_app.ui.activity.login.LoginActivity
import com.example.rgb4u_app.ui.fragment.ConfirmationDialogFragment
import com.google.android.material.switchmaterial.SwitchMaterial

class MyPageMainActivity : AppCompatActivity() {

    private lateinit var switchPassword: SwitchMaterial
    private lateinit var changePasswordLayout: LinearLayout // 여기 수정
    private lateinit var tvChangePassword: TextView
    private lateinit var btnNotificationDetails: ImageButton
    private lateinit var btnTermsOfServiceDetails: ImageButton
    private lateinit var btnEditProfile: ImageButton
    private lateinit var tvLogout: TextView
    private lateinit var tvDeleteAccount: TextView
    private lateinit var btnHowToUseDetails: ImageButton

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_my_page_main)

        // 비밀번호 설정 토글과 비밀번호 변경 메뉴
        switchPassword = findViewById(R.id.switch_password)
        changePasswordLayout = findViewById(R.id.layout_change_password) // LinearLayout 초기화
        tvChangePassword = findViewById(R.id.tv_change_password) // TextView 초기화
        tvLogout = findViewById(R.id.tv_logout) // 로그아웃 텍스트뷰 초기화
        tvDeleteAccount = findViewById(R.id.tv_delete_account) // 회원 탈퇴 텍스트뷰 초기화
        btnHowToUseDetails = findViewById(R.id.btn_how_to_use_details) // 도움말 버튼 초기화

        // 버튼 초기화
        btnNotificationDetails = findViewById(R.id.btn_notification_details)
        btnTermsOfServiceDetails = findViewById(R.id.btn_terms_of_service_details)
        btnEditProfile = findViewById(R.id.btn_edit_profile)

        // 토글에 따른 비밀번호 변경 메뉴 표시/숨김
        switchPassword.setOnCheckedChangeListener { _, isChecked ->
            changePasswordLayout.visibility = if (isChecked) View.VISIBLE else View.GONE
        }

        val btnChangePassword: ImageButton = findViewById(R.id.btn_change_password)
        btnChangePassword.setOnClickListener {
            val intent = Intent(this, MyPagePasswordEditActivity::class.java)
            startActivity(intent)
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
                title = "로그아웃하시겠습니까?",
                message = "", // 메시지를 빈 문자열로 설정하여 보이지 않게 함
                confirmButtonText = "로그아웃",
                onConfirm = {
                    val intent = Intent(this, LoginActivity::class.java)
                    startActivity(intent)
                    finish()  // 로그아웃 동작
                }
            )
            dialog.show(supportFragmentManager, "logoutConfirmation")
        }

        // 회원 탈퇴 클릭 리스너 추가
        tvDeleteAccount.setOnClickListener {
            val dialog = ConfirmationDialogFragment(
                title = "회원 탈퇴하시겠습니까?",
                message = "(앱 이름)을 탈퇴하면 기록과 분석 결과 등 모든 정보가 즉시 삭제되고 복구가 불가능합니다. \n계속하시겠습니까?",
                confirmButtonText = "탈퇴",
                onConfirm = {
                    // 회원 탈퇴 처리 로직
                    // 예: Firebase에서 사용자 계정 삭제 등
                    Toast.makeText(this, "회원 탈퇴가 완료되었습니다.", Toast.LENGTH_SHORT).show()
                }
            )
            dialog.show(supportFragmentManager, "deleteAccountConfirmation")
        }

        // 도움말 클릭 리스너
        btnHowToUseDetails.setOnClickListener {
            val intent = Intent(this, MyPageHowToUseActivity::class.java)
            startActivity(intent)
        }
    }
}
