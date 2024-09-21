package com.example.rgb4u_app.ui.activity.mypage

import android.content.Intent
import android.os.Bundle
import androidx.appcompat.app.AppCompatActivity
import androidx.fragment.app.commit
import com.example.rgb4u_app.R
import com.example.rgb4u_app.ui.fragment.MypageCommonHeaderFragment
import com.google.android.material.switchmaterial.SwitchMaterial

class MyPageNotificationSettingsActivity : AppCompatActivity() {

    private lateinit var switchMessage: SwitchMaterial
    private lateinit var switchAnalysis: SwitchMaterial

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_my_page_notification_settings)

        // Fragment를 추가하고 타이틀 설정
        val headerFragment = MypageCommonHeaderFragment()
        supportFragmentManager.commit {
            replace(R.id.header_fragment_container, headerFragment)
        }

        // Fragment의 타이틀을 '알림 설정'으로 변경
        headerFragment.setTitle("알림 설정")

        // Switch 버튼 초기화
        switchMessage = findViewById(R.id.switch_message)
        switchAnalysis = findViewById(R.id.switch_analysis)

        // 뒤로가기 버튼 설정
        headerFragment.setBackButtonListener {
            saveSettings()
            navigateToMyPageMainActivity()
        }
    }

    private fun saveSettings() {
        val messageEnabled = switchMessage.isChecked
        val analysisEnabled = switchAnalysis.isChecked

        // 설정 값 저장 (예: SharedPreferences 사용)
        val preferences = getSharedPreferences("notification_settings", MODE_PRIVATE)
        val editor = preferences.edit()
        editor.putBoolean("message_enabled", messageEnabled)
        editor.putBoolean("analysis_enabled", analysisEnabled)
        editor.apply()
    }

    private fun navigateToMyPageMainActivity() {
        val intent = Intent(this, MyPageMainActivity::class.java)
        startActivity(intent)
        finish()
    }
}
