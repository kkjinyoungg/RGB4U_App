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
import com.example.rgb4u_app.ui.activity.diary.DiaryWriteActivity
import com.example.rgb4u_app.ui.activity.mypage.MyPageMainActivity
import com.google.android.material.bottomappbar.BottomAppBar
import com.google.android.material.bottomnavigation.BottomNavigationView
import com.google.android.material.floatingactionbutton.FloatingActionButton

class NavigationFragment : Fragment() {

    private lateinit var bottomAppBar: BottomAppBar
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
        bottomAppBar.setOnMenuItemClickListener { menuItem ->
            when (menuItem.itemId) {
                R.id.nav_home -> {
                    onNavigationButtonClicked(menuItem, HomeFragment())
                    true
                }
                R.id.nav_my_record -> {
                    onNavigationButtonClicked(menuItem, MyRecordFragment())
                    true
                }
                R.id.nav_analysis -> {
                    onNavigationButtonClicked(menuItem, AnalysisFragment())
                    true
                }
                R.id.nav_my_page -> {
                    navigateToMyPageMainActivity() // MyPageMainActivity로 이동
                    true
                }
                else -> false
            }
        }

        // BottomNavigationView 클릭 리스너 설정 (deprecated 메서드 대체)
        val bottomNav = view.findViewById<BottomNavigationView>(R.id.main_bottom_nav)
        bottomNav.setOnItemSelectedListener { menuItem ->
            when (menuItem.itemId) {
                R.id.nav_home -> {
                    onNavigationButtonClicked(menuItem, HomeFragment())
                    true
                }
                R.id.nav_my_record -> {
                    onNavigationButtonClicked(menuItem, MyRecordFragment())
                    true
                }
                R.id.nav_analysis -> {
                    onNavigationButtonClicked(menuItem, AnalysisFragment())
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


    private fun navigateToMyPageMainActivity() {
        val intent = Intent(activity, MyPageMainActivity::class.java)
        startActivity(intent) // MyPageMainActivity로 이동
    }

    private fun onNavigationButtonClicked(menuItem: MenuItem, fragment: Fragment) {
        resetMenuItems()
        setMenuItemActive(menuItem)

        if (activity is MainActivity) {
            (activity as MainActivity).openFragment(fragment)
        } else {
            // MainActivity가 아닌 경우의 처리 (예: 로그 남기기)
            // 예외처리나 다른 화면 전환을 고려할 수 있습니다.
        }
    }

    private fun resetMenuItems() {
        val menu = bottomAppBar.menu
        for (i in 0 until menu.size()) {
            val item = menu.getItem(i)
            when (item.itemId) {
                R.id.nav_home -> {
                    item.setIcon(R.drawable.ic_home)
                    item.title = "홈"
                }
                R.id.nav_my_record -> {
                    item.setIcon(R.drawable.ic_my_record)
                    item.title = "나의 기록"
                }
                R.id.nav_analysis -> {
                    item.setIcon(R.drawable.ic_analysis)
                    item.title = "일지 분석"
                }
                R.id.nav_my_page -> {
                    item.setIcon(R.drawable.ic_profile)
                    item.title = "마이페이지"
                }
            }
            item.icon?.alpha = 128 // 선택되지 않은 아이콘의 투명도 설정
        }
    }


    private fun setMenuItemActive(menuItem: MenuItem) {
        when (menuItem.itemId) {
            R.id.nav_home -> {
                menuItem.setIcon(R.drawable.ic_home_selected) // 선택된 이미지
                menuItem.title = "홈" // 선택된 텍스트
            }
            R.id.nav_my_record -> {
                menuItem.setIcon(R.drawable.ic_my_record_selected)
                menuItem.title = "나의 기록"
            }
            R.id.nav_analysis -> {
                menuItem.setIcon(R.drawable.ic_analysis_selected)
                menuItem.title = "일지 분석"
            }
            R.id.nav_my_page -> {
                menuItem.setIcon(R.drawable.ic_profile_selected)
                menuItem.title = "마이페이지"
            }
        }
        menuItem.icon?.alpha = 255 // 선택된 아이콘의 투명도 설정
    }


}
