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
import com.example.rgb4u_app.ui.activity.analysis.AnalysisActivity
import com.example.rgb4u_app.ui.activity.calendar.CalendarHomeActivity
import com.example.rgb4u_app.ui.activity.diary.DiaryWriteActivity
import com.example.rgb4u_app.ui.activity.home.MainActivity
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
        val sharedPreferences =
            requireActivity().getSharedPreferences("app_preferences", Context.MODE_PRIVATE)
        var selectedNavItem = sharedPreferences.getInt("selectedNavItem", -1)

        // SharedPreferences에서 값이 없을 경우 기본값 설정
        if (selectedNavItem == -1) {
            selectedNavItem = R.id.nav_home // 기본값 설정 (예: 홈 화면)
        }

        // 선택된 아이템 설정
        bottomNav.selectedItemId = selectedNavItem

        // BottomNavigationView 아이템 선택 리스너 설정
        bottomNav.setOnItemSelectedListener { menuItem ->
            val intent = Intent(
                activity, when (menuItem.itemId) {
                    R.id.nav_home -> MainActivity::class.java
                    R.id.nav_my_record -> CalendarHomeActivity::class.java
                    R.id.nav_analysis -> AnalysisActivity::class.java
                    R.id.nav_my_page -> MyPageMainActivity::class.java
                    else -> null
                }
            )

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
            sshowContinueRecordDialog(requireActivity())
            // startActivity(Intent(activity, DiaryWriteActivity::class.java))

            /*val isConditionMet = checkYourCondition() // 조건 확인 함수
            if (isConditionMet) {
                fabThink.setImageResource(R.drawable.ic_fnb) // 새로운 이미지로 변경
            } else {
                fabThink.setImageResource(R.drawable.ic_fnb) // 기본 이미지로 복구
            }*/
        }

    }

    // 임시저장 팝업 불러옴
    fun sshowContinueRecordDialog(activity: FragmentActivity) {
        val dialog = ContinueRecordDialogFragment()

        dialog.setOnNewRecordClickListener {
            // "새로 쓰기" 버튼 클릭 시 동작
            // 예: 새 기록 화면으로 이동
            startActivity(Intent(activity, DiaryWriteActivity::class.java))
        }

        dialog.setOnContinueClickListener {
            // "이어서 쓰기" 버튼 클릭 시 동작
            // 예: 기존 기록 불러오기
            startActivity(Intent(activity, DiaryWriteActivity::class.java))
        }

        dialog.show(activity.supportFragmentManager, "ContinueRecordDialog")
    }
}
