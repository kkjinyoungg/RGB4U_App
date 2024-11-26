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
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.database.DataSnapshot
import com.google.firebase.database.DatabaseError
import com.google.firebase.database.DatabaseReference
import com.google.firebase.database.FirebaseDatabase
import com.google.firebase.database.ValueEventListener

class NavigationFragment : Fragment() {

    private lateinit var bottomNav: BottomNavigationView
    private lateinit var fabThink: FloatingActionButton
    private lateinit var database: DatabaseReference
    private val currentDate: String
        get() {
            val dateFormat = SimpleDateFormat("yyyy-MM-dd", Locale.getDefault())
            return dateFormat.format(Calendar.getInstance().time)
        }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        return inflater.inflate(R.layout.fragment_navigation, container, false)
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)


        val userId = FirebaseAuth.getInstance().currentUser?.uid
        database = FirebaseDatabase.getInstance().getReference("users/$userId/diaries")

        bottomNav = view.findViewById(R.id.main_bottom_nav)
        fabThink = view.findViewById(R.id.fab_think)

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

        // FAB 아이콘과 클릭 동작 설정
        updateFabStatus()

        fabThink.setOnClickListener {
            val userId = FirebaseAuth.getInstance().currentUser?.uid
            val diaryRef = FirebaseDatabase.getInstance().getReference("users/$userId/diaries/$currentDate")
            diaryRef.addListenerForSingleValueEvent(object : ValueEventListener {
                override fun onDataChange(snapshot: DataSnapshot) {
                    if (snapshot.exists() && snapshot.child("savingstatus").value == "temp") {
                        sshowContinueRecordDialog(requireActivity())
                    } else {
                        navigateToDiaryWriteActivity()
                    }
                }

                override fun onCancelled(error: DatabaseError) {
                    Log.e("NavigationFragment", "Error: ${error.message}")
                }
            })
        }
    }

    private fun updateFabStatus() {
        val userId = FirebaseAuth.getInstance().currentUser?.uid
        val diaryRef = FirebaseDatabase.getInstance().getReference("users/$userId/diaries/$currentDate")
        diaryRef.addListenerForSingleValueEvent(object : ValueEventListener {
            override fun onDataChange(snapshot: DataSnapshot) {
                if (snapshot.exists()) {
                    val savingStatus = snapshot.child("savingstatus").value as? String
                    if (savingStatus == "save") {
                        fabThink.setImageResource(R.drawable.ic_fab_done) //회색버튼
                        fabThink.isClickable = false
                    } else {
                        fabThink.setImageResource(R.drawable.ic_fab_yet) //노란색버튼
                        fabThink.isClickable = true
                    }
                } else {
                    fabThink.setImageResource(R.drawable.ic_fab_yet)
                    fabThink.isClickable = true
                }
            }

            override fun onCancelled(error: DatabaseError) {
                Log.e("NavigationFragment", "Error: ${error.message}")
            }
        })
    }

    private fun navigateToDiaryWriteActivity() {
        val calendar = Calendar.getInstance()
        val dateFormatYear = SimpleDateFormat("yyyy", Locale.getDefault())
        val dateFormatDate = SimpleDateFormat("MM월 dd일 E요일", Locale.getDefault())

        val selectedYear = dateFormatYear.format(calendar.time)
        val selectedDate = dateFormatDate.format(calendar.time)

        val intent = Intent(activity, DiaryWriteActivity::class.java).apply {
            putExtra("SELECTED_YEAR", selectedYear)
            putExtra("SELECTED_DATE", selectedDate)
        }
        startActivity(intent)
    }

    // 팝업을 띄우는 함수
    fun sshowContinueRecordDialog(activity: FragmentActivity) {
        val dialog = ContinueRecordDialogFragment()

        dialog.setOnNewRecordClickListener {
            // 새로 쓰기 클릭 시 동작 (기록 삭제 후 DiaryWriteActivity로 이동)
            navigateToDiaryWriteActivity()
        }

        dialog.setOnContinueClickListener {
            // 이어서 쓰기 클릭 시 동작
            navigateToDiaryWriteActivity()
        }

        dialog.show(activity.supportFragmentManager, "ContinueRecordDialog")
    }
}
