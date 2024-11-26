package com.example.rgb4u_app.ui.fragment

import android.content.Context
import android.content.Intent
import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import com.example.rgb4u_app.R
import com.example.rgb4u_app.ui.activity.home.MainActivity
import com.example.rgb4u_app.ui.activity.analysis.AnalysisActivity
import com.example.rgb4u_app.ui.activity.calendar.CalendarHomeActivity
import com.example.rgb4u_app.ui.activity.diary.DiaryWriteActivity
import com.example.rgb4u_app.ui.activity.mypage.MyPageMainActivity
import com.google.android.material.bottomnavigation.BottomNavigationView
import com.google.android.material.floatingactionbutton.FloatingActionButton

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
        var selectedNavItem = sharedPreferences.getInt("selectedNavItem", R.id.nav_home) // 기본값을 홈으로 설정
        Log.d("NavigationFragment", "Selected Nav Item: $selectedNavItem") // 로그 추가

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
            startActivity(Intent(activity, DiaryWriteActivity::class.java))
        }
    }
}
