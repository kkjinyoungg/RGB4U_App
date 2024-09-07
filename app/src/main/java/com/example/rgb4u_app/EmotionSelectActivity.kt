package com.example.rgb4u_app

import android.content.Intent
import android.os.Bundle
import android.widget.ImageButton
import androidx.appcompat.app.AppCompatActivity
import androidx.viewpager.widget.ViewPager
import androidx.viewpager2.widget.ViewPager2
import com.google.android.material.chip.Chip
import com.google.android.material.chip.ChipGroup
import com.google.android.material.tabs.TabLayout
import com.google.android.material.tabs.TabLayoutMediator

class EmotionSelectActivity : AppCompatActivity() {

    private lateinit var tabLayout: TabLayout
    private lateinit var viewPager: ViewPager

    private lateinit var chipGroup: ChipGroup

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_emotion_select)

        chipGroup = findViewById(R.id.chipGroup)

        val tabLayout: TabLayout = findViewById(R.id.tabLayout)
        val viewPager: ViewPager2 = findViewById(R.id.viewPager)

        val adapter = ViewPagerAdapter(this)
        viewPager.adapter = adapter

        // TabLayoutMediator에서 getItem 대신 emotions 리스트를 사용
        TabLayoutMediator(tabLayout, viewPager) { tab, position ->
            tab.text = adapter.emotions[position]
        }.attach()

        // 뒤로가기 버튼 클릭 리스너
        findViewById<ImageButton>(R.id.backButton).setOnClickListener {
            val intent = Intent(this, ThinkWriteActivity::class.java)
            startActivity(intent)
        }

        // 나가기 버튼 클릭 리스너
        findViewById<ImageButton>(R.id.exitButton).setOnClickListener {
            val intent = Intent(this, MainActivity::class.java)
            startActivity(intent)
            finish() // 현재 액티비티 종료
        }

    }

    fun onEmotionSelected(emotion: String) {
        val chip = Chip(this).apply {
            text = emotion
            isCloseIconVisible = true
            setOnCloseIconClickListener {
                chipGroup.removeView(this)
            }
        }
        chipGroup.addView(chip)
    }
}
