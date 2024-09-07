package com.example.rgb4u_app

import android.content.Intent
import android.os.Bundle
import android.view.LayoutInflater
import android.view.MenuItem
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
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
            item.icon?.alpha = 128 // 흐릿하게 설정 (0-255)
            item.title = item.title.toString() // 텍스트 변경 없음
        }
    }

    private fun setMenuItemActive(menuItem: MenuItem) {
        menuItem.icon?.alpha = 255 // 원래 상태로 설정
    }
}
