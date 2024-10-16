package com.example.rgb4u_app.ui.activity.distortiontype

import android.content.Intent
import android.os.Bundle
import android.view.View
import android.widget.Button
import android.widget.ImageButton
import android.widget.TextView
import androidx.appcompat.app.AppCompatActivity
import androidx.viewpager2.widget.ViewPager2
import com.example.rgb4u_app.R
import com.example.rgb4u_app.ui.activity.MainActivity
import com.example.rgb4u_app.ui.fragment.DistortionTypeFragment
import java.text.SimpleDateFormat
import java.util.Date
import java.util.Locale

class DistortionTypeActivity : AppCompatActivity() {

    private lateinit var viewPager: ViewPager2
    private lateinit var pagerAdapter: DistortionPagerAdapter
    private lateinit var btnNext: Button
    private lateinit var btnPrev: Button
    private lateinit var btnNextTask: Button
    private lateinit var tvPageIndicator: TextView // 페이지 인디케이터

    private var currentPage = 0
    private var totalTypes = 3 // 받아온 유형 수에 맞게 동적으로 변경
    private lateinit var distortionDataList: List<DistortionData> // 데이터 리스트

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_distortion_type)

        // 프래그먼트를 추가하는 코드
        if (savedInstanceState == null) {
            supportFragmentManager.beginTransaction()
                .replace(R.id.fragment_container, DistortionTypeFragment()) // 프래그먼트 추가
                .commit()
        }

        // 툴바 버튼 설정
        val buttonAction1 = findViewById<ImageButton>(R.id.button_write_action1)
        val buttonAction2 = findViewById<ImageButton>(R.id.button_write_action2)

        // 툴바 버튼 클릭 리스너
        buttonAction1.setOnClickListener {
            val intent = Intent(this, MainActivity::class.java) // MainActivity로 이동
            startActivity(intent)
            finish() // 현재 Activity 종료
        }

        buttonAction2.setOnClickListener {
            // 기능은 공백으로 나중에 추가할 수 있습니다.
        }

        // button_write_action2의 이미지 리소스 동적으로 변경
        buttonAction2.setImageResource(R.drawable.ic_help) // ic_help 이미지로 변경

        // 데이터 리스트를 받아온다 (예시로 dummy data 사용)
        distortionDataList = getDistortionDataList()

        // 툴바 설정
        setupToolbar()

        // ViewPager2 설정
        viewPager = findViewById(R.id.viewPager)
        pagerAdapter = DistortionPagerAdapter(this, distortionDataList)
        viewPager.adapter = pagerAdapter

        // 페이지 인디케이터 초기화
        tvPageIndicator = findViewById(R.id.tv_page_indicator)
        updatePageIndicator()

        // 버튼 초기화
        btnNext = findViewById(R.id.btn_next)
        btnPrev = findViewById(R.id.btn_prev)
        btnNextTask = findViewById(R.id.btn_next_task)

        updateButtons()

        // ViewPager 페이지 변화 감지
        viewPager.registerOnPageChangeCallback(object : ViewPager2.OnPageChangeCallback() {
            override fun onPageSelected(position: Int) {
                currentPage = position
                updateButtons()
                updatePageIndicator() // 페이지 인디케이터 업데이트
            }
        })


        // 버튼 클릭 이벤트 처리
        btnNext.setOnClickListener { moveToNextPage() }
        btnPrev.setOnClickListener { moveToPrevPage() }
        btnNextTask.setOnClickListener { moveToNextTask() }
    }

    private fun updatePageIndicator() {
        tvPageIndicator.text = "(${currentPage + 1}/$totalTypes)" // 현재 페이지/총 페이지 수
    }

    // DistortionData 리스트를 반환하는 함수
    private fun getDistortionDataList(): List<DistortionData> {
        return listOf(
            DistortionData(
                myThoughtTitle = "흑백성이 발견된 생각",
                myThought = "여기에 사용자가 적은 생각을 입력할 수 있습니다.",
                reasonTitle = "왜 흑백성일까요?",
                reason = "흑백성은 모든 일을 두 가지로만 나눠서 생각하게 해요.",
                suggestionTitle = "이렇게 생각해보면 어떨까요?",
                shortSuggestion = "‘한 번의 시험으로 모든 것이 결정되진 않아’",
                suggestion = "한 번의 시험으로 모든 것이 끝나지 않는다고 생각해보면 어떨까? 시험은 한 번만 있는 것이 아니니까, 이번 시험 결과가 나쁘더라도 다음에 노력할 수 있을거야.",
                distortionType = "흑백성",
                imageResId = R.drawable.ic_planet_a // 이미지 리소스 추가
            ),
            DistortionData(
                myThoughtTitle = "왜곡성",
                myThought = "이건 왜곡성",
                reasonTitle = "왜 왜곡성일까요?",
                reason = "왜곡성은 사실과 다르게 생각하게 해요.",
                suggestionTitle = "이렇게 생각해보면 어떨까요?",
                shortSuggestion = "‘모든 것을 정확히 알 필요는 없어’",
                suggestion = "모든 것을 정확히 알지 못한다고 해서 두려워할 필요는 없다고 생각해보면 어떨까요?",
                distortionType = "왜곡성",
                imageResId = R.drawable.ic_planet_a // 다른 이미지 리소스 추가
            ),
            // 다른 DistortionData 객체 추가
        )
    }


    private fun setupToolbar() {
        // 툴바 자체를 찾는 코드 (Toolbar)
        val toolbar = findViewById<androidx.appcompat.widget.Toolbar>(R.id.toolbar_write_diary)
        setSupportActionBar(toolbar)

        // 툴바의 뒤로가기 버튼 설정
        supportActionBar?.setDisplayHomeAsUpEnabled(true)
        toolbar.setNavigationOnClickListener { finish() }

        // 날짜를 표시하는 TextView
        val currentDate = SimpleDateFormat("yyyy년 MM월 dd일 E요일", Locale.getDefault()).format(Date())
        findViewById<TextView>(R.id.toolbar_write_title).text = currentDate
    }

    private fun moveToNextPage() {
        if (currentPage < totalTypes - 1) {
            viewPager.currentItem = currentPage + 1
        }
    }

    private fun moveToPrevPage() {
        if (currentPage > 0) {
            viewPager.currentItem = currentPage - 1
        }
    }

    private fun moveToNextTask() {
        // 다음 과업으로 이동하는 로직 작성
    }

    private fun updateButtons() {
        btnPrev.visibility = if (currentPage == 0) View.GONE else View.VISIBLE
        btnNextTask.visibility = if (currentPage == totalTypes - 1) View.VISIBLE else View.GONE
        btnNext.visibility = if (currentPage == totalTypes - 1) View.GONE else View.VISIBLE
    }


}
