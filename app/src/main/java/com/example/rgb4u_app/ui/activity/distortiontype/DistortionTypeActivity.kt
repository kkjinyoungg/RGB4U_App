package com.example.rgb4u_app.ui.activity.distortiontype

import android.content.Intent
import android.os.Bundle
import android.view.View
import android.widget.Button
import android.widget.TextView
import androidx.appcompat.app.AppCompatActivity
import androidx.appcompat.widget.Toolbar
import androidx.viewpager2.widget.ViewPager2
import com.example.rgb4u_app.R
import com.example.rgb4u_app.ui.activity.summary.SummaryMainActivity
import com.example.rgb4u_app.ui.fragment.DistortionHelpBottomSheet
import com.example.rgb4u_app.DistortionTypeFiller
import java.text.SimpleDateFormat
import java.util.Date
import java.util.Locale
import android.util.Log

class DistortionTypeActivity : AppCompatActivity() {

    private lateinit var viewPager: ViewPager2
    private lateinit var pagerAdapter: DistortionPagerAdapter
    private lateinit var distortionTypeFiller: DistortionTypeFiller

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_distortion_type)

        val toolbar: Toolbar = findViewById(R.id.toolbar_write_diary)
        setSupportActionBar(toolbar)

        // ActionBar 설정
        supportActionBar?.setDisplayHomeAsUpEnabled(false)
        supportActionBar?.setDisplayShowTitleEnabled(false)

        toolbar.findViewById<TextView>(R.id.toolbar_write_title).text = getCurrentDate()

        viewPager = findViewById(R.id.view_pager)
        pagerAdapter = DistortionPagerAdapter(this, viewPager)
        viewPager.adapter = pagerAdapter

        // 터치로 페이지 넘기기 비활성화
        viewPager.isUserInputEnabled = false

        viewPager.registerOnPageChangeCallback(object : ViewPager2.OnPageChangeCallback() {
            override fun onPageSelected(position: Int) {
                super.onPageSelected(position)
                updateButtonVisibility(position)
            }
        })

        findViewById<Button>(R.id.btn_next).setOnClickListener {
            if (viewPager.currentItem < pagerAdapter.itemCount - 1) {
                viewPager.currentItem += 1
            } else {
                val intent = Intent(this, EmotionReselectActivity::class.java)
                startActivity(intent)
            }
        }

        findViewById<Button>(R.id.btn_previous).setOnClickListener {
            if (viewPager.currentItem > 0) {
                viewPager.currentItem -= 1
            }
        }

        toolbar.findViewById<View>(R.id.button_write_action2).setOnClickListener {
            showDistortionHelpBottomSheet()
        }

        toolbar.findViewById<View>(R.id.button_write_action1).setOnClickListener {
            val intent = Intent(this, SummaryMainActivity::class.java)
            startActivity(intent)
        }

        // Intent에서 사용자 ID와 다이어리 ID 가져오기
        val userId = intent.getStringExtra("USER_ID") ?: ""
        val diaryId = intent.getStringExtra("DIARY_ID") ?: ""
        // 로그 출력
        Log.d("DistortionTypeActivity", "Received User ID: $userId")
        Log.d("DistortionTypeActivity", "Received Diary ID: $diaryId")
        // DistortionTypeFiller 초기화 및 데이터 로드
        distortionTypeFiller = DistortionTypeFiller()
        distortionTypeFiller.initialize(userId, diaryId) // 전달받은 ID 사용

        // 데이터가 로드된 후 UI 갱신
        distortionTypeFiller.setOnDataLoadedListener {
            pagerAdapter.updateData() // UI 업데이트
        }
    }

    private fun showDistortionHelpBottomSheet() {
        val bottomSheet = DistortionHelpBottomSheet()
        bottomSheet.show(supportFragmentManager, "DistortionHelpBottomSheet")
    }

    private fun getCurrentDate(): String {
        val dateFormat = SimpleDateFormat("M월 d일 E요일", Locale.getDefault())
        return dateFormat.format(Date())
    }

    private fun updateButtonVisibility(position: Int) {
        val btnNext = findViewById<Button>(R.id.btn_next)
        val btnPrevious = findViewById<Button>(R.id.btn_previous)

        when (position) {
            0 -> {
                btnPrevious.visibility = View.GONE
                btnNext.text = "다음"
            }
            pagerAdapter.itemCount - 1 -> {
                btnPrevious.visibility = View.VISIBLE
                btnNext.text = "감정 변화 확인"
            }
            else -> {
                btnPrevious.visibility = View.VISIBLE
                btnNext.text = "다음"
            }
        }
    }

    // 툴바의 뒤로가기 버튼 클릭 시 처리
    override fun onSupportNavigateUp(): Boolean {
        onBackPressed()
        return true
    }
}
