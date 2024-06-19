package com.example.rgb4u_app

import android.content.Intent
import android.os.Bundle
import android.widget.Button
import android.widget.ImageButton
import androidx.appcompat.app.AppCompatActivity
import com.google.android.material.bottomnavigation.BottomNavigationView

class DiaryWriteActivity : AppCompatActivity() {
    private lateinit var bottomNavigation: BottomNavigationView
    private lateinit var btnBack: ImageButton
    private lateinit var btnComplete: Button

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_write_diary)

        // 뷰 참조 가져오기
        btnBack = findViewById(R.id.btn_back)
        btnComplete = findViewById(R.id.btn_complete)

        // 뒤로가기 버튼 클릭 시 메인 화면으로 이동
        btnBack.setOnClickListener {
            startActivity(Intent(this, MainActivity::class.java))
            finish()
        }

        // 완료 버튼 클릭 시 메인 화면으로 이동
        btnComplete.setOnClickListener {
            // 일기 내용 저장 로직 추가
            startActivity(Intent(this, MainActivity::class.java))
            finish()
        }

        bottomNavigation = findViewById(R.id.bottom_navigation)
        bottomNavigation.setOnItemSelectedListener { item ->
            when (item.itemId) {
                R.id.navigation_home -> {
                    // "Home" 메뉴 아이템 클릭 시 실행할 코드
                    true
                }

                R.id.navigation_search -> {
                    // "Search" 메뉴 아이템 클릭 시 실행할 코드
                    true
                }

                R.id.navigation_notifications -> {
                    // "Notifications" 메뉴 아이템 클릭 시 실행할 코드
                    true
                }

                R.id.navigation_profile -> {
                    // "Profile" 메뉴 아이템 클릭 시 실행할 코드
                    true
                }

                else -> false
            }
        }
    }
}
