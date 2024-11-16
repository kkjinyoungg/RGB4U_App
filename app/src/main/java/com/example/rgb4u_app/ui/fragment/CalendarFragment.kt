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

        val intent = Intent(requireContext(), CalenderDetailActivity::class.java).apply {
            putExtra("SELECTED_DATE", formattedDate)
        }
        startActivity(intent)
    }

    private fun Int.dpToPx(): Int {
        val density = resources.displayMetrics.density
        return (this * density).toInt()
    }

    private fun fetchDiaryDataForMonth() {
        val userId = FirebaseAuth.getInstance().currentUser?.uid
        val yearMonth = SimpleDateFormat("yyyy-MM", Locale.getDefault()).format(currentCalendar.time)
        val firebaseRef = FirebaseDatabase.getInstance().getReference("users/$userId/diaries")

        // 일기 데이터를 가져오고 날짜에 맞춰 일기 정보를 확인
        firebaseRef.orderByChild("date").startAt("$yearMonth-01").endAt("$yearMonth-31")
            .get().addOnSuccessListener { snapshot ->
                val daysWithDiary = mutableSetOf<Int>()

                if (snapshot.exists()) {
                    for (diarySnapshot in snapshot.children) {
                        val diaryData = diarySnapshot.getValue(object : GenericTypeIndicator<Map<String, Any>>() {})
                        diaryData?.let {
                            val date = it["date"] as? String ?: return@let
                            val day = date.split("-")[2].toIntOrNull() ?: return@let
                            val emotionDegree = (it["emotion"] as? Long)?.toInt() ?: 0
                            daysWithDiary.add(day)
                            addStampToCalendar(day, emotionDegree)
                        }
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
                        val intent = Intent(requireContext(), DiaryWriteActivity::class.java).apply {
                            putExtra("SELECTED_DATE", SimpleDateFormat("yyyy년 M월 d일", Locale.getDefault()).format(selectedDate.time))
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
