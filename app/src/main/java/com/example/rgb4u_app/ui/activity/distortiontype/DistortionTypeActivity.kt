package com.example.rgb4u_app.ui.activity.distortiontype

import android.content.Intent
import android.os.Build
import android.os.Bundle
import android.util.Log
import android.view.View
import android.widget.Button
import android.widget.ImageButton
import android.widget.TextView
import androidx.appcompat.app.AppCompatActivity
import androidx.appcompat.widget.Toolbar
import androidx.viewpager2.widget.ViewPager2
import com.example.rgb4u_app.DistortionTypeFiller
import com.example.rgb4u_app.R
import com.example.rgb4u_app.ui.activity.home.MainActivity
import com.example.rgb4u_app.ui.fragment.DistortionHelpBottomSheet
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.database.FirebaseDatabase

class DistortionTypeActivity : AppCompatActivity() {

    private lateinit var viewPager: ViewPager2
    private lateinit var pagerAdapter: DistortionPagerAdapter
    private lateinit var distortionTypeFiller: DistortionTypeFiller
    private lateinit var userId: String

    private lateinit var toolbarDate: String // lateinit으로 선언
    private lateinit var date: String // lateinit으로 선언

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_distortion_type)

        // 상태바 투명
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {
            window.apply {
                decorView.systemUiVisibility =
                    View.SYSTEM_UI_FLAG_LAYOUT_FULLSCREEN or View.SYSTEM_UI_FLAG_LAYOUT_STABLE
                statusBarColor = android.graphics.Color.TRANSPARENT
            }
        }

        userId = FirebaseAuth.getInstance().currentUser?.uid ?: ""

        // Intent에서 툴바랑 date 가져오기
        date = intent.getStringExtra("Date") ?: ""
        toolbarDate = intent.getStringExtra("Toolbar") ?: ""

        // 로그 출력
        Log.d("DistortionTypeActivity", "Received User ID: $userId")
        Log.d("DistortionTypeActivity", "Received Diary ID: $date")
        Log.d("DistortionTypeActivity", "Received Toolbar Date: $toolbarDate")

        // Firebase에서 readingStatus 업데이트
        updateReadingStatus(userId, date)

        val toolbar: Toolbar = findViewById(R.id.toolbar_write_diary)
        setSupportActionBar(toolbar)

        val helpButton = toolbar.findViewById<ImageButton>(R.id.button_write_action2)
        helpButton.setImageResource(R.drawable.ic_distortion_help_btn)

        // ActionBar 설정
        supportActionBar?.setDisplayHomeAsUpEnabled(false)
        supportActionBar?.setDisplayShowTitleEnabled(false)

        toolbar.findViewById<TextView>(R.id.toolbar_write_title).text = toolbarDate // 툴바 타이틀 설정 (toolbar_write_title)

        viewPager = findViewById(R.id.view_pager)
        pagerAdapter = DistortionPagerAdapter(this, viewPager, userId, date)
        viewPager.adapter = pagerAdapter

        Log.d("DistortionTypeActivity", "ViewPager initialized: $viewPager")
        Log.d("DistortionTypeActivity", "PagerAdapter initialized: $pagerAdapter")

        // 터치로 페이지 넘기기 비활성화
        viewPager.isUserInputEnabled = false

        viewPager.registerOnPageChangeCallback(object : ViewPager2.OnPageChangeCallback() {
            override fun onPageSelected(position: Int) {
                super.onPageSelected(position)
                updateButtonVisibility(position)
                Log.d("DistortionTypeActivity", "Page selected: $position")
            }
        })

        findViewById<Button>(R.id.btn_next).setOnClickListener {
            Log.d("DistortionTypeActivity", "Next button clicked, current item: ${viewPager.currentItem}")
            if (viewPager.currentItem < pagerAdapter.itemCount - 1) {
                viewPager.currentItem += 1
            } else {
                val intent = Intent(this, EmotionReselectActivity::class.java)
                intent.putExtra("Toolbar", toolbarDate) //toolbar로 고치기
                intent.putExtra("Date", date) // date보내기
                Log.d("DistortionTypeActivity", "$toolbarDate")
                startActivity(intent)
            }
        }

        findViewById<Button>(R.id.btn_previous).setOnClickListener {
            Log.d("DistortionTypeActivity", "Previous button clicked, current item: ${viewPager.currentItem}")
            if (viewPager.currentItem > 0) {
                viewPager.currentItem -= 1
            }
        }

        // 클릭 이벤트 설정
        helpButton.setOnClickListener {
            Log.d("DistortionTypeActivity", "Help button clicked")
            showDistortionHelpBottomSheet()
        }

        toolbar.findViewById<View>(R.id.button_write_action1).setOnClickListener {
            Log.d("DistortionTypeActivity", "Summary button clicked")
            val intent = Intent(this, MainActivity::class.java)
            startActivity(intent)
        }

        // DistortionTypeFiller 초기화 및 데이터 로드
        distortionTypeFiller = DistortionTypeFiller()
        distortionTypeFiller.initialize(userId, date) // 전달받은 ID 사용
        Log.d("DistortionTypeActivity", "DistortionTypeFiller initialized")

        // 데이터가 로드된 후 UI 갱신
        distortionTypeFiller.setOnDataLoadedListener {
            Log.d("DistortionTypeActivity", "Data loaded successfully")
            pagerAdapter.updateData() // UI 업데이트
        }
    }

    private fun updateReadingStatus(userId: String, date: String) {
        val database = FirebaseDatabase.getInstance()
            .getReference("users/$userId/diaries/$date/readingstatus")

        database.setValue("read").addOnSuccessListener {
            Log.d("DistortionTypeActivity", "readingStatus 업데이트 성공: 빈 문자열로 설정됨")
        }.addOnFailureListener { exception ->
            Log.e("DistortionTypeActivity", "readingStatus 업데이트 실패", exception)
        }
    }


    private fun showDistortionHelpBottomSheet() {
        val bottomSheet = DistortionHelpBottomSheet()
        bottomSheet.show(supportFragmentManager, "DistortionHelpBottomSheet")
    }

    private fun updateButtonVisibility(position: Int) {
        val btnNext = findViewById<Button>(R.id.btn_next)
        val btnPrevious = findViewById<Button>(R.id.btn_previous)

        if (pagerAdapter.itemCount == 1) {
            btnPrevious.visibility = View.GONE
            btnNext.text = "감정 변화 확인"
        } else {
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
        Log.d("DistortionTypeActivity", "Button visibility updated for position: $position")
    }

    // 툴바의 뒤로가기 버튼 클릭 시 처리
    override fun onSupportNavigateUp(): Boolean {
        onBackPressed()
        return true
    }
}
