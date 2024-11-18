package com.example.rgb4u_app.ui.fragment

import android.content.Context
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageButton
import android.widget.TextView
import androidx.appcompat.app.AppCompatActivity
import androidx.appcompat.widget.Toolbar
import androidx.fragment.app.Fragment
import com.example.rgb4u_app.R
import java.text.SimpleDateFormat
import java.util.Calendar
import java.util.Locale
import kotlin.random.Random

class HomeFragment : Fragment() {

    private lateinit var textBox: TextView
    private lateinit var dateTextView: TextView
    private lateinit var dDayTextView: TextView // moodScoreTextView를 dDayTextView로 변경

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        // 레이아웃을 Inflate합니다.
        return inflater.inflate(R.layout.fragment_home, container, false)
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        // Toolbar 설정
        val toolbar: Toolbar = view.findViewById(R.id.toolbar_home) // 수정: view를 사용
        val activity = requireActivity() as AppCompatActivity
        activity.setSupportActionBar(toolbar)

        // 기본 뒤로가기 버튼, 앱 이름 숨기기
        activity.supportActionBar?.setDisplayHomeAsUpEnabled(false)
        activity.supportActionBar?.setDisplayShowTitleEnabled(false)

        // button_write_action2 버튼 숨기기
        // val notificationButton: ImageButton = view.findViewById(R.id.notificationButton)
        // notificationButton.visibility = View.GONE


        // 뷰를 초기화합니다.
        textBox = view.findViewById(R.id.textBox)
        dateTextView = view.findViewById(R.id.dateTextView) // ID에 맞게 수정
        dDayTextView = view.findViewById(R.id.dDayTextView) // ID 수정

        // chat_refresh 버튼 설정
        val chatRefreshButton: ImageButton = view.findViewById(R.id.chat_refresh)
        chatRefreshButton.setOnClickListener {
            changeMessage() // 버튼 클릭 시 메시지 변경
        }

        // 말풍선 클릭 리스너 추가
//        textBox.setOnClickListener {
//            changeMessage()
//        }

        // 현재 날짜 및 요일 설정
        updateDateAndDay()

        // 앱 설치 날짜를 기반으로 디데이 계산
        calculateDDay()
    }

    private fun updateDateAndDay() {
        // 현재 날짜를 가져옵니다.
        val currentDate = Calendar.getInstance()
        val dateFormat = SimpleDateFormat("M월 d일 EEEE", Locale.KOREA)
        dateTextView.text = dateFormat.format(currentDate.time) // 날짜 및 요일 설정
    }

    private fun calculateDDay() {
        // SharedPreferences에서 설치 날짜를 가져옵니다.
        val sharedPreferences = requireActivity().getSharedPreferences("app_prefs", Context.MODE_PRIVATE)
        val installDateMillis = sharedPreferences.getLong("install_date", -1)

        // 설치 날짜가 없다면 현재 날짜를 설치 날짜로 설정
        if (installDateMillis == -1L) {
            val currentDateMillis = System.currentTimeMillis()
            sharedPreferences.edit().putLong("install_date", currentDateMillis).apply()
            dDayTextView.text = "D+1"  // 설치 날을 1일로 간주합니다.
        } else {
            // 설치 날짜를 기준으로 D-Day 계산
            val currentDateMillis = System.currentTimeMillis()
            val dDay = ((currentDateMillis - installDateMillis) / (1000 * 60 * 60 * 24) + 1).toInt() // 일수로 변환 (1일 추가)
            dDayTextView.text = "D+$dDay"
        }
    }


    private fun changeMessage() {
        val messages = arrayOf(
            "안녕하세요! 반가워요!",
            "오늘 하루는 어땠나요?",
            "어떤 도움이 필요하신가요?",
            "오늘은 햄버거가 땡기는 날이야.",
            "오늘 하루도 고생많았어요&"
        )

        val randomIndex = Random.nextInt(messages.size)
        textBox.text = messages[randomIndex]
        textBox.visibility = View.VISIBLE
    }
}
