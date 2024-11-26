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
import com.example.rgb4u_app.ui.activity.mypage.MyPageMainActivity


class MyPageServiceCheck : AppCompatActivity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_my_page_service_check)

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
        val headerFragment = MypageCommonHeaderFragment.newInstance("서비스 이용 약관")
        supportFragmentManager.commit {
            replace(R.id.header_fragment_container, headerFragment)
            setReorderingAllowed(true)
            addToBackStack(null) // 뒤로가기 스택에 추가
        }

        // Fragment의 Lifecycle을 감지하여 Listener 설정
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
                            navigateToMyPageMainActivity()
                        }
                    }
                }
            },
            true
        )
    }

    private fun navigateToMyPageMainActivity() {
        val intent = Intent(this, MyPageMainActivity::class.java)
        intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP or Intent.FLAG_ACTIVITY_NEW_TASK)
        startActivity(intent)
        finish()
    }

}
