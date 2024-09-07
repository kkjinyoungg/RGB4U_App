package com.example.rgb4u_app

import android.content.Context
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageButton
import android.widget.RelativeLayout
import android.widget.TextView
import android.widget.Toast
import androidx.fragment.app.Fragment
import androidx.lifecycle.lifecycleScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext
import java.text.SimpleDateFormat
import java.util.Calendar
import java.util.Locale
import kotlin.random.Random

class HomeFragment : Fragment() {

    private lateinit var textBox: TextView
    private lateinit var dateTextView: TextView
    private lateinit var dDayTextView: TextView

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        return inflater.inflate(R.layout.fragment_home, container, false)
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        textBox = view.findViewById(R.id.textBox)
        dateTextView = view.findViewById(R.id.dateTextView)
        dDayTextView = view.findViewById(R.id.dDayTextView)
        val mainCharacterContainer = view.findViewById<RelativeLayout>(R.id.mainCharacterContainer)

        textBox.setOnClickListener {
            changeMessage()
        }

        updateDateAndDay()

        // 코루틴을 사용하여 비동기로 D-Day 계산
        viewLifecycleOwner.lifecycleScope.launch {
            calculateDDay()
        }

        val notificationButton: ImageButton = view.findViewById(R.id.notificationButton)
        notificationButton.setOnClickListener {
            Toast.makeText(requireContext(), "알림 버튼이 클릭되었습니다!", Toast.LENGTH_SHORT).show()
        }
    }

    private fun updateDateAndDay() {
        val currentDate = Calendar.getInstance()
        val dateFormat = SimpleDateFormat("M월 d일 EEEE", Locale.KOREA)
        dateTextView.text = dateFormat.format(currentDate.time)
    }

    private suspend fun calculateDDay() {
        // 비동기적으로 SharedPreferences에서 설치 날짜를 가져옴
        val dDay = withContext(Dispatchers.IO) {
            val sharedPreferences = requireActivity().getSharedPreferences("app_prefs", Context.MODE_PRIVATE)
            val installDateMillis = sharedPreferences.getLong("install_date", -1)

            if (installDateMillis == -1L) {
                val currentDateMillis = System.currentTimeMillis()
                sharedPreferences.edit().putLong("install_date", currentDateMillis).apply()
                0 // 설치 첫날
            } else {
                val currentDateMillis = System.currentTimeMillis()
                ((currentDateMillis - installDateMillis) / (1000 * 60 * 60 * 24)).toInt()
            }
        }

        // UI 업데이트는 메인 스레드에서 수행
        dDayTextView.text = "D+$dDay"
    }

    private fun changeMessage() {
        val messages = arrayOf(
            "안녕하세요! 반가워요!",
            "오늘 하루는 어땠나요?",
            "어떤 도움이 필요하신가요?",
            "서진아, 오늘은 햇버거가 땡기는 날이야."
        )

        val randomIndex = Random.nextInt(messages.size)
        textBox.text = messages[randomIndex]
        textBox.visibility = View.VISIBLE
    }
}
