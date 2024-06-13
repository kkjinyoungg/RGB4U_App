package com.example.rgb4u_app

import android.os.Bundle
import androidx.appcompat.app.AppCompatActivity
import com.google.android.material.bottomnavigation.BottomNavigationView

class PlanActivity : AppCompatActivity() {
    private lateinit var bottomNavigation: BottomNavigationView

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_plan)

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
