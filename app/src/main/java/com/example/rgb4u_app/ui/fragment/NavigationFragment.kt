package com.example.rgb4u_app.ui.fragment

import android.content.Intent
import android.os.Bundle
import android.view.LayoutInflater
import android.view.MenuItem
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import com.example.rgb4u_app.R
import com.example.rgb4u_app.ui.activity.MainActivity
import com.example.rgb4u_app.ui.activity.analysis.AnalysisActivity
import com.example.rgb4u_app.ui.activity.diary.DiaryWriteActivity
import com.example.rgb4u_app.ui.activity.mypage.MyPageMainActivity
import com.google.android.material.bottomappbar.BottomAppBar
import com.google.android.material.bottomnavigation.BottomNavigationView
import com.google.android.material.floatingactionbutton.FloatingActionButton

class NavigationFragment : Fragment() {

    private lateinit var bottomAppBar: BottomAppBar
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

        bottomAppBar = view.findViewById(R.id.main_bottom_appBar)
        bottomNav = view.findViewById(R.id.main_bottom_nav)

        bottomAppBar.setOnMenuItemClickListener { menuItem ->
            when (menuItem.itemId) {
                R.id.nav_home -> {
                    navigateToMainActivity() // MainActivity로 이동
                    true
                }
                R.id.nav_my_record -> {
                    onNavigationButtonClicked(menuItem, MyRecordFragment())
                    true
                }
                R.id.nav_analysis -> {
                    navigateToAnalysisActivity() // AnalysisActivity로 이동
                    true
                }
                R.id.nav_my_page -> {
                    navigateToMyPageMainActivity() // MyPageMainActivity로 이동
                    true
                }
                else -> false
            }
        }

        // BottomNavigationView 클릭 리스너 설정
        bottomNav.setOnItemSelectedListener { menuItem ->
            when (menuItem.itemId) {
                R.id.nav_home -> {
                    navigateToMainActivity() // MainActivity로 이동
                    true
                }
                R.id.nav_my_record -> {
                    onNavigationButtonClicked(menuItem, MyRecordFragment())
                    true
                }
                R.id.nav_analysis -> {
                    navigateToAnalysisActivity() // AnalysisActivity로 이동
                    true
                }
                R.id.nav_my_page -> {
                    navigateToMyPageMainActivity() // MyPageMainActivity로 이동
                    true
                }
                else -> false
            }
        }

        fabThink = view.findViewById(R.id.fab_think)
        fabThink.setOnClickListener {
            val intent = Intent(activity, DiaryWriteActivity::class.java)
            startActivity(intent)
        }
    }

    // MainActivity로 이동하는 메소드
    private fun navigateToMainActivity() {
        val intent = Intent(activity, MainActivity::class.java)
        startActivity(intent) // MainActivity로 이동
    }

    private fun navigateToMyPageMainActivity() {
        val intent = Intent(activity, MyPageMainActivity::class.java)
        startActivity(intent) // MyPageMainActivity로 이동
    }

    private fun navigateToAnalysisActivity() {
        val intent = Intent(activity, AnalysisActivity::class.java)
        startActivity(intent) // AnalysisActivity로 이동
    }

    private fun onNavigationButtonClicked(menuItem: MenuItem, fragment: Fragment) {
        // 메뉴 아이템을 리셋한 후 활성화
        resetMenuItems() // 이전 아이템 리셋
        setMenuItemActive(menuItem) // 현재 선택한 아이템 활성화

        // 프래그먼트 열기
        if (activity is MainActivity) {
            (activity as MainActivity).openFragment(fragment)
        }
    }


    private fun resetMenuItems() {
        // BottomNavigationView 메뉴 리셋
        for (i in 0 until bottomNav.menu.size()) {
            val item = bottomNav.menu.getItem(i)
            resetMenuItem(item)
        }
        // BottomAppBar 메뉴 리셋
        val menu = bottomAppBar.menu
        for (i in 0 until menu.size()) {
            val item = menu.getItem(i)
            resetMenuItem(item)
        }
    }

    private fun resetMenuItem(item: MenuItem) {
        when (item.itemId) {
            R.id.nav_home -> {
                item.setIcon(R.drawable.ic_home) // 비활성화된 아이콘
                item.title = "홈"
            }
            R.id.nav_my_record -> {
                item.setIcon(R.drawable.ic_my_record) // 비활성화된 아이콘
                item.title = "나의 기록"
            }
            R.id.nav_analysis -> {
                item.setIcon(R.drawable.ic_analysis) // 비활성화된 아이콘
                item.title = "분석"
            }
            R.id.nav_my_page -> {
                item.setIcon(R.drawable.ic_profile) // 비활성화된 아이콘
                item.title = "마이페이지"
            }
        }
        // item.icon?.alpha = 128 // 이 줄을 주석 처리하거나 제거합니다.
    }

    private fun setMenuItemActive(menuItem: MenuItem) {
        when (menuItem.itemId) {
            R.id.nav_home -> {
                menuItem.setIcon(R.drawable.ic_home_selected) // 선택된 아이콘
                menuItem.title = "홈"
            }
            R.id.nav_my_record -> {
                menuItem.setIcon(R.drawable.ic_my_record_selected)
                menuItem.title = "나의 기록"
            }
            R.id.nav_analysis -> {
                menuItem.setIcon(R.drawable.ic_analysis_selected)
                menuItem.title = "분석"
            }
            R.id.nav_my_page -> {
                menuItem.setIcon(R.drawable.ic_profile_selected)
                menuItem.title = "마이페이지"
            }
        }
        // menuItem.icon?.alpha = 255 // 이 줄도 주석 처리하거나 제거합니다.
    }
}
