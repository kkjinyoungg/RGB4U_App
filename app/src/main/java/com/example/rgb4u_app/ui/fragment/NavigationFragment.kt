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

        (activity as MainActivity).openFragment(fragment)
    }

    private fun resetMenuItems() {
        val menu = bottomAppBar.menu
        for (i in 0 until menu.size()) {
            val item = menu.getItem(i)
            when (item.itemId) {
                R.id.nav_home -> item.setIcon(R.drawable.ic_home)
                R.id.nav_my_record -> item.setIcon(R.drawable.ic_my_record)
                R.id.nav_analysis -> item.setIcon(R.drawable.ic_analysis)
                R.id.nav_my_page -> item.setIcon(R.drawable.ic_profile)
            }
            item.icon?.alpha = 128
        }
    }

    private fun setMenuItemActive(menuItem: MenuItem) {
        when (menuItem.itemId) {
            R.id.nav_home -> menuItem.setIcon(R.drawable.ic_home_selected)
            R.id.nav_my_record -> menuItem.setIcon(R.drawable.ic_my_record_selected)
            R.id.nav_analysis -> menuItem.setIcon(R.drawable.ic_analysis_selected)
            R.id.nav_my_page -> menuItem.setIcon(R.drawable.ic_profile_selected)
        }
        menuItem.icon?.alpha = 255
    }
}
