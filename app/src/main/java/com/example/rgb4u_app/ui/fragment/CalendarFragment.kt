package com.example.rgb4u_app.ui.fragment

import android.os.Bundle
import android.view.Gravity
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.FrameLayout
import android.widget.GridLayout
import android.widget.ImageButton
import android.widget.ImageView
import android.widget.TextView
import android.widget.Toast
import androidx.core.content.res.ResourcesCompat
import androidx.fragment.app.Fragment
import com.example.rgb4u_app.R
import java.text.SimpleDateFormat
import java.util.Calendar
import java.util.Locale

class CalendarFragment : Fragment() {

    private lateinit var calendarGrid: GridLayout
    private lateinit var textCurrentMonth: TextView
    private var currentCalendar = Calendar.getInstance()

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        val view = inflater.inflate(R.layout.fragment_calendar, container, false)

        calendarGrid = view.findViewById(R.id.calendar_grid)
        textCurrentMonth = view.findViewById(R.id.toolbar_calendar_title)

        // 버튼을 뷰에서 찾기
        val buttonAction1 = view.findViewById<ImageButton>(R.id.button_calendar_action1)
        val buttonAction2 = view.findViewById<ImageButton>(R.id.button_calendar_action2)

        // 버튼 클릭 리스너 설정
        buttonAction1.setOnClickListener {
            changeMonth(-1)
        }
        buttonAction2.setOnClickListener {
            changeMonth(1)
            //buttonAction2.setImageResource(R.drawable.ic_forward_wh) // 이미지 변경 (예시)
        }


        updateCalendar()
        return view
    }

    private fun changeMonth(monthOffset: Int) {
        currentCalendar.add(Calendar.MONTH, monthOffset)
        updateCalendar()
    }

    private fun updateCalendar() {
        textCurrentMonth.text = SimpleDateFormat("yyyy년 M월", Locale.getDefault()).format(currentCalendar.time)
        calendarGrid.removeAllViews()

        val maxDay = currentCalendar.getActualMaximum(Calendar.DAY_OF_MONTH)
        currentCalendar.set(Calendar.DAY_OF_MONTH, 1)
        val startDay = currentCalendar.get(Calendar.DAY_OF_WEEK) - 1 // 일요일부터 시작
        val customFont = ResourcesCompat.getFont(requireContext(), R.font.nanumsquareroundregular) // 폰트 리소스 가져오기


        for (i in 0 until startDay + maxDay) {
            val frameLayout = FrameLayout(requireContext()).apply {
                layoutParams = GridLayout.LayoutParams().apply {
                    width = 0
                    height = 0
                    columnSpec = GridLayout.spec(GridLayout.UNDEFINED, 1f)
                    rowSpec = GridLayout.spec(GridLayout.UNDEFINED, 1f)
                }
            }

            val dayTextView = TextView(requireContext()).apply {
                layoutParams = FrameLayout.LayoutParams(FrameLayout.LayoutParams.WRAP_CONTENT, FrameLayout.LayoutParams.WRAP_CONTENT).apply {
                    gravity = Gravity.BOTTOM or Gravity.CENTER_HORIZONTAL
                }
                gravity = Gravity.CENTER
                textSize = 12f  // 텍스트 크기를 12sp로 설정
                typeface = customFont // 폰트 설정

                setTextColor(ResourcesCompat.getColor(resources, R.color.white, null)) // 텍스트 색상 설정

                if (i >= startDay) {
                    val day = i - startDay + 1
                    text = day.toString()
                    setOnClickListener {
                        // 날짜 클릭 시 다른 화면으로 이동 처리
                        navigateToDetail(day)
                    }

                    // 오늘 날짜 표시
                    if (isToday(day)) {
                        // today_circle 뷰를 추가
                        val todayCircle = View(requireContext()).apply {
                            layoutParams = FrameLayout.LayoutParams(30.dpToPx(), 20.dpToPx()).apply {
                                gravity = Gravity.BOTTOM or Gravity.CENTER_HORIZONTAL
                            }
                            background = ResourcesCompat.getDrawable(resources, R.drawable.bg_circle_yellow, null)
                        }
                        frameLayout.addView(todayCircle)
                        setTextColor(ResourcesCompat.getColor(resources, R.color.black, null))
                        typeface = customFont // 폰트 설정
                    }

                    // 도장 이미지 표시 (예: 1일에 도장이 찍힌다고 가정)
                    if (day == 1) { // 예시로 1일에 도장을 표시
                        val stampImage = ImageView(requireContext()).apply {
                            layoutParams = FrameLayout.LayoutParams(40.dpToPx(), 40.dpToPx()).apply {
                                gravity = Gravity.CENTER
                            }
                            setImageDrawable(ResourcesCompat.getDrawable(resources, R.drawable.img_emotion_0, null)) // 도장 이미지
                        }
                        frameLayout.addView(stampImage)
                    }
                }
            }

            frameLayout.addView(dayTextView)
            calendarGrid.addView(frameLayout)
        }
    }

    private fun isToday(day: Int): Boolean {
        val today = Calendar.getInstance()
        return currentCalendar.get(Calendar.YEAR) == today.get(Calendar.YEAR) &&
                currentCalendar.get(Calendar.MONTH) == today.get(Calendar.MONTH) &&
                day == today.get(Calendar.DAY_OF_MONTH)
    }

    private fun navigateToDetail(day: Int) {
        // 클릭한 날짜에 대한 토스트 메시지 표시
        Toast.makeText(requireContext(), "${day}일 클릭됨", Toast.LENGTH_SHORT).show()

        // 클릭한 날짜 상세 화면으로 이동 (예시)
        //val intent = Intent(requireContext(), DetailActivity::class.java)
        //intent.putExtra("SELECTED_DAY", day)
        //startActivity(intent)

    }

    // dpToPx 확장 함수
    private fun Int.dpToPx(): Int {
        val density = resources.displayMetrics.density
        return (this * density).toInt()
    }
}