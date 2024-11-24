package com.example.rgb4u_app.ui.fragment

import android.content.Context
import android.content.Intent
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import androidx.fragment.app.FragmentActivity
import com.example.rgb4u_app.R
import com.example.rgb4u_app.ui.activity.home.MainActivity
import com.example.rgb4u_app.ui.activity.analysis.AnalysisActivity
import com.example.rgb4u_app.ui.activity.calendar.CalendarHomeActivity
import com.example.rgb4u_app.ui.activity.diary.DiaryWriteActivity
import com.example.rgb4u_app.ui.activity.mypage.MyPageMainActivity
import com.google.android.material.bottomnavigation.BottomNavigationView
import com.google.android.material.floatingactionbutton.FloatingActionButton
import java.text.SimpleDateFormat
import java.util.Calendar
import java.util.Locale
import android.util.Log

class NavigationFragment : Fragment() {

    private lateinit var bottomNav: BottomNavigationView
    private lateinit var fabThink: FloatingActionButton

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        return inflater.inflate(R.layout.fragment_navigation, container, false)
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        bottomNav = view.findViewById(R.id.main_bottom_nav)

        // SharedPreferences를 사용하여 선택된 메뉴 항목 로드
        val sharedPreferences = requireActivity().getSharedPreferences("app_preferences", Context.MODE_PRIVATE)
        var selectedNavItem = sharedPreferences.getInt("selectedNavItem", -1)

        // SharedPreferences에서 값이 없을 경우 기본값 설정
        if (selectedNavItem == -1) {
            selectedNavItem = R.id.nav_home // 기본값 설정 (예: 홈 화면)
        }

        // 선택된 아이템 설정
        bottomNav.selectedItemId = selectedNavItem

        // BottomNavigationView 아이템 선택 리스너 설정
        bottomNav.setOnItemSelectedListener { menuItem ->
            val intent = Intent(activity, when (menuItem.itemId) {
                R.id.nav_home -> MainActivity::class.java
                R.id.nav_my_record -> CalendarHomeActivity::class.java
                R.id.nav_analysis -> AnalysisActivity::class.java
                R.id.nav_my_page -> MyPageMainActivity::class.java
                else -> null
            })

            if (intent != null) {
                // 선택된 메뉴 항목을 SharedPreferences에 저장
                with(sharedPreferences.edit()) {
                    putInt("selectedNavItem", menuItem.itemId)
                    apply()
                }
                startActivity(intent)
                true
            } else {
                false
            }
        }

        // FAB 버튼 클릭 리스너 설정
        fabThink = view.findViewById(R.id.fab_think)
        fabThink.setOnClickListener {
            // 현재 날짜를 가져오고 포맷팅
            val calendar = Calendar.getInstance()
            val dateFormatYear = SimpleDateFormat("yyyy", Locale.getDefault())
            val dateFormatDate = SimpleDateFormat("MM월 dd일 E요일", Locale.getDefault())

            val selectedYear = dateFormatYear.format(calendar.time) // yyyy 값
            val selectedDate = dateFormatDate.format(calendar.time) // MM월 dd일 E요일 형식

            // 로그 찍기
            Log.d("NavigationFragment", "Selected Year: $selectedYear")
            Log.d("NavigationFragment", "Selected Date: $selectedDate")

            // DiaryWriteActivity로 데이터 전달
            val intent = Intent(activity, DiaryWriteActivity::class.java).apply {
                putExtra("SELECTED_YEAR", selectedYear) // yyyy 값 전달
                putExtra("SELECTED_DATE", selectedDate) // MM월 dd일 E요일 전달
            }
            startActivity(intent)
        }
    }
}
