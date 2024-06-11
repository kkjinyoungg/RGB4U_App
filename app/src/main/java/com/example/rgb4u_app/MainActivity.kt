package com.example.rgb4u_app

import android.content.Intent // Intent 임포트
import android.os.Bundle
import androidx.appcompat.app.AppCompatActivity
import com.example.rgb4u_app.databinding.ActivityMainBinding
import com.google.android.material.bottomnavigation.BottomNavigationView
import android.widget.Button

class MainActivity : AppCompatActivity() {
    private lateinit var binding: ActivityMainBinding
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityMainBinding.inflate(layoutInflater)
        setContentView(binding.root)

        // 버튼 클릭 시 PlanActivity로 이동
        val button: Button = findViewById(R.id.button)
        button.setOnClickListener {
            val intent = Intent(this, PlanActivity::class.java)
            startActivity(intent)
        }

        // BottomNavigationView에서 아이템 선택 시 해당 액티비티로 전환
        val bottomNavigation: BottomNavigationView = findViewById(R.id.bottom_navigation)
        bottomNavigation.setOnNavigationItemSelectedListener { item ->
            when (item.itemId) {
                R.id.nav_home -> {
                    // 홈 화면으로 이동 (현재 액티비티)
                    true
                }
                R.id.nav_plan -> {
                    // PlanActivity로 이동
                    val intent = Intent(this, PlanActivity::class.java)
                    startActivity(intent)
                    true
                }
                else -> false
            }
        }
    }
}