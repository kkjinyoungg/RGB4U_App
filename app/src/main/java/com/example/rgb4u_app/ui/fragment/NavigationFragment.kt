package com.example.rgb4u_app.ui.fragment

import android.content.Intent
import android.os.Bundle
import android.view.LayoutInflater
import android.view.MenuItem
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import com.example.rgb4u_app.R
import com.example.rgb4u_app.ui.activity.DiaryWriteActivity
import com.example.rgb4u_app.ui.activity.MainActivity
import com.google.android.material.bottomappbar.BottomAppBar
import com.google.android.material.floatingactionbutton.FloatingActionButton

class NavigationFragment : Fragment() {

    private lateinit var bottomAppBar: BottomAppBar
    private lateinit var fabThink: FloatingActionButton // FloatingActionButton 변수 추가

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
                    onNavigationButtonClicked(menuItem, MyPageFragment())
                    true
                }
                else -> false
            }
        }

        // FloatingActionButton 참조
        fabThink = view.findViewById(R.id.fab_think)
        fabThink.setOnClickListener {
            // DiaryWriteActivity로 이동
            val intent = Intent(activity, DiaryWriteActivity::class.java)
            startActivity(intent)
        }
    }

    private fun onNavigationButtonClicked(menuItem: MenuItem, fragment: Fragment) {
        resetMenuItems() // 모든 메뉴 항목 초기화
        setMenuItemActive(menuItem) // 클릭된 메뉴 항목 활성화

        // Fragment 전환
        (activity as MainActivity).openFragment(fragment)
    }

    private fun resetMenuItems() {
        val menu = bottomAppBar.menu
        for (i in 0 until menu.size()) {
            val item = menu.getItem(i)
            when (item.itemId) {
                R.id.nav_home -> item.setIcon(R.drawable.ic_home) // 기본 아이콘으로 설정
                R.id.nav_my_record -> item.setIcon(R.drawable.ic_my_record) // 기본 아이콘으로 설정
                R.id.nav_analysis -> item.setIcon(R.drawable.ic_analysis) // 기본 아이콘으로 설정
                R.id.nav_my_page -> item.setIcon(R.drawable.ic_profile) // 기본 아이콘으로 설정
            }
            item.icon?.alpha = 128 // 흐릿하게 설정 (0-255)
        }
    }

    private fun setMenuItemActive(menuItem: MenuItem) {
        when (menuItem.itemId) {
            R.id.nav_home -> menuItem.setIcon(R.drawable.ic_home_selected) // 선택된 아이콘으로 변경
            R.id.nav_my_record -> menuItem.setIcon(R.drawable.ic_my_record_selected) // 선택된 아이콘으로 변경
            R.id.nav_analysis -> menuItem.setIcon(R.drawable.ic_analysis_selected) // 선택된 아이콘으로 변경
            R.id.nav_my_page -> menuItem.setIcon(R.drawable.ic_profile_selected) // 선택된 아이콘으로 변경
        }
        menuItem.icon?.alpha = 255 // 선택된 항목은 명확하게 설정
    }

}
