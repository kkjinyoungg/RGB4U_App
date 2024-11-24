package com.example.rgb4u_app.ui.fragment

import android.content.Context
import android.content.Intent
import android.os.Bundle
import android.util.Log
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
import java.text.SimpleDateFormat
import java.util.Calendar
import java.util.Locale

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

        // 상황에 따라 FAB 아이콘 변경하는 코드 - 수정 필요 -
       /* if (someCondition) {
            fab.setImageResource(R.drawable.ic_image1)
        } else {
            fab.setImageResource(R.drawable.ic_image2)
        }*/

        fabThink.setOnClickListener {

           // sshowContinueRecordDialog(requireActivity()) // 조건 상관없이 팝업 확인해야하면 밑에꺼 주석처리하고 이거 실행 ㄱㄱ

            // 데이터 유무를 확인 (예시로 SharedPreferences를 사용. - 수정 필요 -) -> 그래서 예시로는 데이터 유무 판단 못해서 현재 바로 DiaryWriteActivity로 이동함
            val sharedPreferences = requireContext().getSharedPreferences("DiaryData", Context.MODE_PRIVATE)
            val existingData = sharedPreferences.getString("CURRENT_RECORD", null)

            if (existingData.isNullOrEmpty()) {
                // 데이터가 없으면 바로 DiaryWriteActivity로 이동
                navigateToDiaryWriteActivity()
            } else {
                // 데이터가 있으면 팝업 띄우기
                sshowContinueRecordDialog(requireActivity())
            }
        }
    }

    // DiaryWriteActivity로 바로 이동하는 함수
    private fun navigateToDiaryWriteActivity() {
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

    // 팝업을 띄우는 함수
    fun sshowContinueRecordDialog(activity: FragmentActivity) {
        val dialog = ContinueRecordDialogFragment()

        // "새로 쓰기" 버튼 클릭 시 동작
        dialog.setOnNewRecordClickListener {
            navigateToDiaryWriteActivity() // 새로 쓰기로 이동
        }

        // "이어서 쓰기" 버튼 클릭 시 동작
        dialog.setOnContinueClickListener {
            navigateToDiaryWriteActivity() // 이어서 쓰기로 이동 (필요 시 추가 로직 구현 가능)
        }

        // 팝업 표시
        dialog.show(activity.supportFragmentManager, "ContinueRecordDialog")
    }
}
