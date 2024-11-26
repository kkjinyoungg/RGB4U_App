package com.example.rgb4u_app.ui.activity.mypage

import android.content.Intent
import android.os.Build
import android.os.Bundle
import android.view.View
import androidx.appcompat.app.AppCompatActivity
import androidx.fragment.app.Fragment
import androidx.fragment.app.FragmentManager
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

        // Fragment를 추가하고 타이틀 설정
        val headerFragment = MypageCommonHeaderFragment.newInstance("알림 설정")
        supportFragmentManager.commit {
            replace(R.id.header_fragment_container, headerFragment)
        }

        // Switch 버튼 초기화
        switchMessage = findViewById(R.id.switch_message)
        switchAnalysis = findViewById(R.id.switch_analysis)

        // Switch 색상 설정
        switchMessage.thumbTintList = resources.getColorStateList(R.color.switch_thumb_color, null)
        switchMessage.trackTintList = resources.getColorStateList(R.color.switch_track_color, null)

        switchAnalysis.thumbTintList = resources.getColorStateList(R.color.switch_thumb_color, null)
        switchAnalysis.trackTintList = resources.getColorStateList(R.color.switch_track_color, null)

        // Switch 상태 초기화
        initializeSwitchStates()

        // Fragment LifecycleCallbacks를 이용하여 Fragment의 View가 생성된 후에 Listener 설정
        supportFragmentManager.registerFragmentLifecycleCallbacks(
            object : FragmentManager.FragmentLifecycleCallbacks() {
                override fun onFragmentViewCreated(
                    fm: FragmentManager,
                    f: Fragment,
                    v: View,
                    savedInstanceState: Bundle?
                ) {
                    if (f is MypageCommonHeaderFragment) {
                        f.setBackButtonListener {
                            saveSettings()
                            navigateToMyPageMainActivity()
                        }
                    }
                }
            },
            true
        )
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

    private fun initializeSwitchStates() {
        val preferences = getSharedPreferences("notification_settings", MODE_PRIVATE)
        switchMessage.isChecked = preferences.getBoolean("message_enabled", false) // 기본값 false
        switchAnalysis.isChecked = preferences.getBoolean("analysis_enabled", false) // 기본값 false
    }

}
