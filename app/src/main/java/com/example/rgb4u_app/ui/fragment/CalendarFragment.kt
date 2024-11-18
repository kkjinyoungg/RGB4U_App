package com.example.rgb4u_app.ui.fragment

import android.content.Intent
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
import androidx.core.view.children
import androidx.fragment.app.Fragment
import com.example.rgb4u_app.R
import com.example.rgb4u_app.ui.activity.diary.DiaryWriteActivity // DiaryWriteActivity 추가
import com.example.rgb4u_app.ui.activity.calendar.CalenderDetailActivity
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.database.FirebaseDatabase
import com.google.firebase.database.GenericTypeIndicator
import java.text.SimpleDateFormat
import java.util.*

class CalendarFragment : Fragment() {

    private lateinit var calendarGrid: GridLayout
    private lateinit var textCurrentMonth: TextView
    private var currentCalendar = Calendar.getInstance()
    private var startDay = 0

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        val view = inflater.inflate(R.layout.fragment_calendar, container, false)

        calendarGrid = view.findViewById(R.id.calendar_grid)
        textCurrentMonth = view.findViewById(R.id.toolbar_calendar_title)

        val buttonAction1 = view.findViewById<ImageButton>(R.id.button_calendar_action1)
        val buttonAction2 = view.findViewById<ImageButton>(R.id.button_calendar_action2)

        buttonAction1.setOnClickListener {
            changeMonth(-1)
        }
        buttonAction2.setOnClickListener {
            changeMonth(1)
        }

        updateCalendar()
        return view
    }

    private fun changeMonth(monthOffset: Int) {
        val today = Calendar.getInstance()

        if (monthOffset > 0 &&
            currentCalendar.get(Calendar.YEAR) == today.get(Calendar.YEAR) &&
            currentCalendar.get(Calendar.MONTH) >= today.get(Calendar.MONTH)
        ) {
            Toast.makeText(requireContext(), "다음 달로는 이동할 수 없어요", Toast.LENGTH_SHORT).show()
            return
        }

        currentCalendar.add(Calendar.MONTH, monthOffset)
        updateCalendar()
    }

    private fun updateCalendar() {
        textCurrentMonth.text = SimpleDateFormat("yyyy년 M월", Locale.getDefault()).format(currentCalendar.time)
        calendarGrid.removeAllViews()

        val maxDay = currentCalendar.getActualMaximum(Calendar.DAY_OF_MONTH)
        currentCalendar.set(Calendar.DAY_OF_MONTH, 1)
        startDay = currentCalendar.get(Calendar.DAY_OF_WEEK) - 1
        val customFont = ResourcesCompat.getFont(requireContext(), R.font.nanumsquareroundregular)
        val today = Calendar.getInstance()

        for (i in 0 until startDay + maxDay) {
            val frameLayout = FrameLayout(requireContext()).apply {
                layoutParams = GridLayout.LayoutParams().apply {
                    width = 0
                    height = 0
                    columnSpec = GridLayout.spec(GridLayout.UNDEFINED, 1f)
                    rowSpec = GridLayout.spec(GridLayout.UNDEFINED, 1f)
                }
            }

            if (i >= startDay) {
                val day = i - startDay + 1

                val dayTextView = TextView(requireContext()).apply {
                    layoutParams = FrameLayout.LayoutParams(
                        FrameLayout.LayoutParams.WRAP_CONTENT,
                        FrameLayout.LayoutParams.WRAP_CONTENT
                    ).apply {
                        gravity = Gravity.BOTTOM or Gravity.CENTER_HORIZONTAL
                    }
                    gravity = Gravity.CENTER
                    textSize = 12f
                    typeface = customFont
                    text = day.toString()
                    setTextColor(ResourcesCompat.getColor(resources, R.color.white, null))
                    setOnClickListener {
                        navigateToDetail(day)
                    }

                    if (isToday(day)) {
                        val todayCircle = View(requireContext()).apply {
                            layoutParams = FrameLayout.LayoutParams(30.dpToPx(), 20.dpToPx()).apply {
                                gravity = Gravity.BOTTOM or Gravity.CENTER_HORIZONTAL
                            }
                            background = ResourcesCompat.getDrawable(resources, R.drawable.bg_circle_yellow, null)
                        }
                        frameLayout.addView(todayCircle)
                        setTextColor(ResourcesCompat.getColor(resources, R.color.black, null))
                    }
                }

                frameLayout.addView(dayTextView)
            }
            calendarGrid.addView(frameLayout)
        }

        fetchDiaryDataForMonth()
    }

    private fun isToday(day: Int): Boolean {
        val today = Calendar.getInstance()
        return currentCalendar.get(Calendar.YEAR) == today.get(Calendar.YEAR) &&
                currentCalendar.get(Calendar.MONTH) == today.get(Calendar.MONTH) &&
                day == today.get(Calendar.DAY_OF_MONTH)
    }

    private fun navigateToDetail(day: Int) {
        val selectedDate = Calendar.getInstance().apply {
            time = currentCalendar.time
            set(Calendar.DAY_OF_MONTH, day)
        }

        // 미래 날짜인지 확인
        if (selectedDate.timeInMillis > Calendar.getInstance().timeInMillis) {
            Toast.makeText(requireContext(), "미래 날짜는 선택할 수 없습니다.", Toast.LENGTH_SHORT).show()
            return
        }

        val dateFormat = SimpleDateFormat("yyyy년 M월 d일 E요일", Locale.getDefault())
        val formattedDate = dateFormat.format(selectedDate.time)

        Toast.makeText(requireContext(), "$formattedDate 클릭됨", Toast.LENGTH_SHORT).show()

        // 일기 작성에 보내기 위해 year 값을 추출
        val year = SimpleDateFormat("yyyy", Locale.getDefault()).format(selectedDate.time)

        val intent = Intent(requireContext(), CalenderDetailActivity::class.java).apply {
            putExtra("SELECTED_DATE", formattedDate)
            putExtra("SELECTED_YEAR", year) // year 값을 Intent에 추가
        }
        startActivity(intent)
    }

    private fun Int.dpToPx(): Int {
        val density = resources.displayMetrics.density
        return (this * density).toInt()
    }

    private fun fetchDiaryDataForMonth() {
        val userId = FirebaseAuth.getInstance().currentUser?.uid ?: return
        val calendar = Calendar.getInstance().apply {
            // 현재 월을 기준으로 설정
            set(Calendar.DAY_OF_MONTH, 1)
        }
        val yearMonth = SimpleDateFormat("yyyy-MM", Locale.getDefault()).format(calendar.time)

        // 첫 번째 날과 마지막 날 계산
        val firstDayKey = "$yearMonth-01"
        calendar.set(Calendar.DAY_OF_MONTH, calendar.getActualMaximum(Calendar.DAY_OF_MONTH))
        val lastDayKey = SimpleDateFormat("yyyy-MM-dd", Locale.getDefault()).format(calendar.time)

        val firebaseRef = FirebaseDatabase.getInstance().getReference("users/$userId/diaries")

        firebaseRef.orderByKey().startAt(firstDayKey).endAt(lastDayKey)
            .get().addOnSuccessListener { snapshot ->
                val daysWithDiary = mutableSetOf<Int>()

                if (snapshot.exists()) {
                    for (diarySnapshot in snapshot.children) {
                        // GenericTypeIndicator를 사용하여 Map 타입을 명시적으로 지정
                        val diaryData = diarySnapshot.getValue(object : GenericTypeIndicator<Map<String, Any>>() {}) ?: continue

                        val date = diarySnapshot.key ?: continue // 날짜 키를 가져오고, 없으면 건너뛰기
                        val day = date.split("-")[2].toIntOrNull() ?: continue // 날짜에서 일(day)을 추출하고, 실패하면 건너뛰기

                        // userInput 추출
                        val userInput = diaryData["userInput"] as? Map<String, Any> ?: continue

                        // reMeasuredEmotionDegree가 있는지 확인하고 없으면 emotionDegree 사용
                        val reMeasuredEmotionDegree = userInput["reMeasuredEmotionDegree"] as? Map<String, Any>
                        val emotionDegree = if (reMeasuredEmotionDegree != null) {
                            (reMeasuredEmotionDegree["int"] as? Int) ?: 0 // "reMeasuredEmotionDegree"에서 "int" 값 가져오기
                        } else {
                            // reMeasuredEmotionDegree가 없다면 emotionDegree 사용
                            val emotionDegreeFallback = userInput["emotionDegree"] as? Map<String, Any>
                            (emotionDegreeFallback?.get("int") as? Int) ?: 0 // 기본 "emotionDegree"에서 "int" 값 가져오기
                        }

                        daysWithDiary.add(day)
                        addStampToCalendar(day, emotionDegree)
                    }
                }
                // 일기 데이터가 없는 날짜에 빈 도장을 찍어줍니다.
                for (day in 1..currentCalendar.getActualMaximum(Calendar.DAY_OF_MONTH)) {
                    if (day !in daysWithDiary) {
                        addBlankStampToCalendar(day)
                    }
                }
            }
    }

    private fun addBlankStampToCalendar(day: Int) {
        val today = Calendar.getInstance()
        // 오늘보다 미래인 날짜에는 빈 도장을 추가하지 않음
        if (currentCalendar.get(Calendar.YEAR) == today.get(Calendar.YEAR) &&
            currentCalendar.get(Calendar.MONTH) == today.get(Calendar.MONTH) &&
            day > today.get(Calendar.DAY_OF_MONTH)) {
            return
        }

        val index = day + startDay - 1
        if (index in 0 until calendarGrid.childCount) {
            val dayView = calendarGrid.getChildAt(index) as? FrameLayout
            dayView?.let {
                // 기존의 스탬프(빈 이미지) 제거
                it.children.filterIsInstance<ImageView>().forEach { child ->
                    if (child.drawable.constantState == ResourcesCompat.getDrawable(resources, R.drawable.img_calendaremotion_0, null)?.constantState) {
                        it.removeView(child)
                    }
                }

                // 빈 도장(이미지)을 추가하고 클릭 시 DiaryWriteActivity로 이동
                val blankImage = ImageView(requireContext()).apply {
                    layoutParams = FrameLayout.LayoutParams(40.dpToPx(), 40.dpToPx()).apply {
                        gravity = Gravity.CENTER
                    }
                    setImageDrawable(ResourcesCompat.getDrawable(resources, R.drawable.img_calendarblank, null))
                    setOnClickListener {
                        val selectedDate = Calendar.getInstance().apply {
                            time = currentCalendar.time
                            set(Calendar.DAY_OF_MONTH, day)
                        }
                        val formattedDate = SimpleDateFormat("MM월 dd일 E요일", Locale("ko", "KR")).format(selectedDate.time)
                        val year = SimpleDateFormat("yyyy", Locale.getDefault()).format(selectedDate.time) // year 값을 추출

                        val intent = Intent(requireContext(), DiaryWriteActivity::class.java).apply {
                            // 날짜 포맷을 "MM월 dd일 E요일" 형식으로 변경
                            putExtra("SELECTED_DATE", formattedDate)
                            putExtra("SELECTED_YEAR", year) // year 값을 Intent에 추가
                        }
                        startActivity(intent)
                    }
                }
                it.addView(blankImage)
            }
        }
    }

    private fun addStampToCalendar(day: Int, degree: Int) {
        val emotionDrawable = when (degree) {
            0 -> R.drawable.img_calendaremotion_0
            1 -> R.drawable.img_calendaremotion_1
            2 -> R.drawable.img_calendaremotion_2
            3 -> R.drawable.img_calendaremotion_3
            else -> R.drawable.img_calendaremotion_4
        }

        val index = day + startDay - 1
        if (index in 0 until calendarGrid.childCount) {
            val dayView = calendarGrid.getChildAt(index) as? FrameLayout
            dayView?.let {
                val emotionImageButton = ImageButton(requireContext()).apply {
                    layoutParams = FrameLayout.LayoutParams(40.dpToPx(), 40.dpToPx()).apply {
                        gravity = Gravity.CENTER
                    }
                    setImageDrawable(ResourcesCompat.getDrawable(resources, emotionDrawable, null))
                    background = null // 배경을 투명하게 설정

                    setOnClickListener {
                        // 감정 이미지 버튼 클릭 시 CalenderDetailActivity로 이동
                        val selectedDate = Calendar.getInstance().apply {
                            time = currentCalendar.time
                            set(Calendar.DAY_OF_MONTH, day)
                        }

                        val dateFormat = SimpleDateFormat("yyyy년 M월 d일 E요일", Locale.getDefault())
                        val formattedDate = dateFormat.format(selectedDate.time)

                        val intent = Intent(requireContext(), CalenderDetailActivity::class.java).apply {
                            putExtra("SELECTED_DATE", formattedDate)
                        }
                        startActivity(intent)
                    }
                }
                it.addView(emotionImageButton)
            }
        }
    }
}
