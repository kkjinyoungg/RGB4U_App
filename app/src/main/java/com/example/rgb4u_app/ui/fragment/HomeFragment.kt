package com.example.rgb4u_app.ui.fragment

import android.content.Context
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageButton
import android.widget.ImageView
import android.widget.TextView
import androidx.appcompat.app.AppCompatActivity
import androidx.appcompat.widget.Toolbar
import androidx.constraintlayout.widget.ConstraintLayout
import androidx.fragment.app.Fragment
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.example.rgb4u_app.R
import com.example.rgb4u_app.ui.activity.home.AnalysisItem
import com.example.rgb4u_app.ui.activity.home.AnalysisItemAdapter
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.database.*
import java.text.SimpleDateFormat
import java.util.Calendar
import java.util.Locale
import kotlin.random.Random

class HomeFragment : Fragment() {

    private lateinit var textBox: TextView
    private lateinit var dateTextView: TextView
    private lateinit var dDayTextView: TextView
    private lateinit var mainConstraintLayout: ConstraintLayout
    private lateinit var mainCharacterContainer: ImageView
    private var notificationCount = 0
    private lateinit var notificationCountText: TextView

    private lateinit var database: DatabaseReference
    private lateinit var analysisList: MutableList<AnalysisItem>
    private lateinit var adapter: AnalysisItemAdapter

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        return inflater.inflate(R.layout.fragment_home, container, false)
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        val userId = FirebaseAuth.getInstance().currentUser?.uid

        val toolbar: Toolbar = view.findViewById(R.id.toolbar_home)
        val activity = requireActivity() as AppCompatActivity
        activity.setSupportActionBar(toolbar)
        activity.supportActionBar?.setDisplayHomeAsUpEnabled(false)
        activity.supportActionBar?.setDisplayShowTitleEnabled(false)

        val notificationButton: ImageButton = view.findViewById(R.id.notificationButton)
        notificationButton.visibility = View.GONE

        textBox = view.findViewById(R.id.textBox)
        dateTextView = view.findViewById(R.id.dateTextView)
        dDayTextView = view.findViewById(R.id.dDayTextView)
        notificationCountText = view.findViewById(R.id.notificationCountText)

        val refreshIcon: ImageView = view.findViewById(R.id.refreshIcon)
        refreshIcon.setOnClickListener {
            changeMessage()
        }

        updateDateAndDay()
        calculateDDay()

        mainConstraintLayout = view.findViewById(R.id.mainConstraintLayout)
        mainCharacterContainer = view.findViewById(R.id.mainCharacterContainer)

        analysisList = mutableListOf()
        adapter = AnalysisItemAdapter(analysisList)
        val recyclerView: RecyclerView = view.findViewById(R.id.analysisRecyclerView)
        recyclerView.layoutManager = LinearLayoutManager(requireContext(), LinearLayoutManager.HORIZONTAL, false)
        recyclerView.adapter = adapter

        // Firebase 데이터베이스 참조 초기화
        database = FirebaseDatabase.getInstance().getReference("users/$userId/diaries")

        // Firebase 데이터 감시
        observeDiaries()
    }

    private fun observeDiaries() {
        database.addValueEventListener(object : ValueEventListener {
            override fun onDataChange(snapshot: DataSnapshot) {
                analysisList.clear()

                var unreadCount = 0
                var loadCount = 0

                for (dateSnapshot in snapshot.children) {
                    // 각 날짜의 diary 데이터를 가져옵니다.
                    val readingStatus = dateSnapshot.child("readingstatus").getValue(String::class.java)
                    val toolbardate = dateSnapshot.child("toolbardate").getValue(String::class.java)
                    val dateKey = dateSnapshot.key ?: ""  // yyyy-mm-dd 형식의 날짜

                    // 읽기 상태가 null이 아닐 때 체크
                    if (readingStatus != null) {
                        when (readingStatus) {
                            "unread" -> {
                                unreadCount++
                                // toolbardate이 null이 아닐 때 추가
                                analysisList.add(AnalysisItem(true, false, toolbardate ?: "", dateKey))
                            }
                            "load" -> {
                                loadCount++
                                // toolbardate이 null이 아닐 때 추가
                                analysisList.add(AnalysisItem(true, true, toolbardate ?: "", dateKey))
                            }
                        }
                    }
                }

                // unread와 load가 없으면 (false, false) 추가
                if (unreadCount == 0 && loadCount == 0) {
                    analysisList.add(AnalysisItem(false, false))
                }

                adapter.notifyDataSetChanged()
                updateNotificationCount(adapter)
            }

            override fun onCancelled(error: DatabaseError) {
                // 오류 처리
            }
        })
    }



    private fun updateNotificationCount(adapter: AnalysisItemAdapter) {
        notificationCount = adapter.itemCount
        notificationCountText.text = "$notificationCount"
        notificationCountText.visibility = View.VISIBLE
    }

    private fun updateDateAndDay() {
        val currentDate = Calendar.getInstance()
        val dateFormat = SimpleDateFormat("M월 d일 EEEE", Locale.KOREA)
        dateTextView.text = dateFormat.format(currentDate.time)
    }

    private fun calculateDDay() {
        val sharedPreferences = requireActivity().getSharedPreferences("app_prefs", Context.MODE_PRIVATE)
        val installDateMillis = sharedPreferences.getLong("install_date", -1)

        if (installDateMillis == -1L) {
            val currentDateMillis = System.currentTimeMillis()
            sharedPreferences.edit().putLong("install_date", currentDateMillis).apply()
            dDayTextView.text = "D+1"
        } else {
            val currentDateMillis = System.currentTimeMillis()
            val dDay = ((currentDateMillis - installDateMillis) / (1000 * 60 * 60 * 24) + 1).toInt()
            dDayTextView.text = "D+$dDay"
        }
    }

    private fun changeMessage() {
        val messages = arrayOf(
            "안녕하세요! 반가워요!",
            "오늘은 어떤 하루를 보냈나요?",
            "어떤 도움이 필요하신가요?",
            "서진아, 오늘은 햄버거가 땡기는 날이야.",
            "일기를 썼어요 고정멘트",
            "분석 결과 나옴 고정멘트"
        )

        val randomIndex = Random.nextInt(messages.size)
        textBox.text = messages[randomIndex]
        textBox.visibility = View.VISIBLE
    }
}
