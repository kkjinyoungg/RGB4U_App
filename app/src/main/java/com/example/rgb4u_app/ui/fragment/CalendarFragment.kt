package com.example.rgb4u_app.ui.fragment

import android.app.Dialog
import android.content.Intent
import android.os.Bundle
import android.view.Gravity
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Button
import android.widget.FrameLayout
import android.widget.GridLayout
import android.widget.ImageButton
import android.widget.ImageView
import android.widget.NumberPicker
import android.widget.TextView
import android.widget.Toast
import androidx.core.content.res.ResourcesCompat
import androidx.core.view.children
import androidx.fragment.app.Fragment
import com.example.rgb4u_app.R
import com.example.rgb4u_app.ui.activity.calendar.CalenderDetailActivity
import com.example.rgb4u_app.ui.activity.diary.DiaryWriteActivity
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.database.FirebaseDatabase
import com.google.firebase.database.GenericTypeIndicator
import java.text.SimpleDateFormat
import java.util.*
import android.util.Log
import android.widget.LinearLayout
import java.util.Calendar
import java.util.Locale

class CalendarFragment : Fragment() {

    private lateinit var calendarGrid: GridLayout
    private lateinit var textCurrentMonth: TextView
    private lateinit var monthBtn : LinearLayout
    private var currentCalendar = Calendar.getInstance()
    private var startDay = 0

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        val view = inflater.inflate(R.layout.fragment_calendar, container, false)

        calendarGrid = view.findViewById(R.id.calendar_grid)
        textCurrentMonth = view.findViewById(R.id.toolbar_calendar_title)
        monthBtn = view.findViewById(R.id.month_btn)

        // 현재 날짜 가져오기
        val calendar = Calendar.getInstance()
        val currentYear = calendar.get(Calendar.YEAR)
        val currentMonth = calendar.get(Calendar.MONTH) + 1 // MONTH는 0부터 시작하므로 +1 필요

        // TextView 초기값 설정
        textCurrentMonth.text = "${currentYear}년 ${currentMonth}월"

        val buttonAction1 = view.findViewById<ImageButton>(R.id.button_calendar_action1)
        val buttonAction2 = view.findViewById<ImageButton>(R.id.button_calendar_action2)

        buttonAction1.setOnClickListener {
            changeMonth(-1)
        }
        buttonAction2.setOnClickListener {
            changeMonth(1)
        }

        monthBtn.setOnClickListener {
            showMonthYearPickerDialog()
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

    private fun showMonthYearPickerDialog() {
        val dialog = Dialog(requireContext())
        dialog.setContentView(R.layout.dialog_month_year_picker)

        val yearPicker = dialog.findViewById<NumberPicker>(R.id.year_picker)
        val monthPicker = dialog.findViewById<NumberPicker>(R.id.month_picker)
        val btnConfirm = dialog.findViewById<Button>(R.id.btn_confirm_yearmonth)

        val currentDate = Calendar.getInstance()
        val currentYear = currentDate.get(Calendar.YEAR)
        val currentMonth = currentDate.get(Calendar.MONTH) + 1 // 월은 0부터 시작하므로 +1

        // NumberPicker 범위 설정
        yearPicker.minValue = currentYear - 100
        yearPicker.maxValue = currentYear
        yearPicker.wrapSelectorWheel = false // 순환 비활성화
        yearPicker.value = currentCalendar.get(Calendar.YEAR)
        yearPicker.displayedValues = Array(yearPicker.maxValue - yearPicker.minValue + 1) { i ->
            "${yearPicker.minValue + i}년"
        }

        fun updateMonthPicker(year: Int) {
            if (year == currentYear) {
                // 현재 연도인 경우, 현재 월까지만 표시
                val monthRange = 1..currentMonth
                monthPicker.displayedValues = null // 기존 캐싱된 값을 초기화
                monthPicker.minValue = monthRange.first
                monthPicker.maxValue = monthRange.last
                monthPicker.displayedValues = monthRange.map { "${it}월" }.toTypedArray()
            } else {
                // 과거 연도인 경우 전체 월(1~12) 표시
                val monthRange = 1..12
                monthPicker.displayedValues = null // 기존 캐싱된 값을 초기화
                monthPicker.minValue = monthRange.first
                monthPicker.maxValue = monthRange.last
                monthPicker.displayedValues = monthRange.map { "${it}월" }.toTypedArray()
            }
            // 초기 MonthPicker 값 설정: 현재 날짜의 달로 설정
            monthPicker.value = currentMonth

            // 현재 선택된 값이 유효 범위 내에 있도록 보정
            if (monthPicker.value < monthPicker.minValue) {
                monthPicker.value = monthPicker.minValue
            } else if (monthPicker.value > monthPicker.maxValue) {
                monthPicker.value = monthPicker.maxValue
            }
        }

        // 초기 MonthPicker 값 설정
        monthPicker.wrapSelectorWheel = false // 순환 비활성화
        updateMonthPicker(yearPicker.value)

        // 연도 선택 시 MonthPicker 범위 업데이트
        yearPicker.setOnValueChangedListener { _, _, newYear ->
            updateMonthPicker(newYear)
        }

        btnConfirm.setOnClickListener {
            val selectedYear = yearPicker.value
            val selectedMonth = monthPicker.value

            currentCalendar.set(Calendar.YEAR, selectedYear)
            currentCalendar.set(Calendar.MONTH, selectedMonth - 1) // Month는 0부터 시작
            updateCalendar()
            dialog.dismiss()
        }

        dialog.show()
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
        Log.d("FetchDiaryData", "User ID: $userId")

        val calendar = Calendar.getInstance().apply {
            set(Calendar.DAY_OF_MONTH, 1)
        }
        val yearMonth = SimpleDateFormat("yyyy-MM", Locale.getDefault()).format(calendar.time)

        val firstDayKey = "$yearMonth-01"
        calendar.set(Calendar.DAY_OF_MONTH, calendar.getActualMaximum(Calendar.DAY_OF_MONTH))
        val lastDayKey = SimpleDateFormat("yyyy-MM-dd", Locale.getDefault()).format(calendar.time)

        Log.d("FetchDiaryData", "Fetching data for range: $firstDayKey to $lastDayKey")

        val firebaseRef = FirebaseDatabase.getInstance().getReference("users/$userId/diaries")

        firebaseRef.orderByKey().startAt(firstDayKey).endAt(lastDayKey)
            .get().addOnSuccessListener { snapshot ->
                val daysWithDiary = mutableSetOf<Int>()

                if (snapshot.exists()) {
                    Log.d("FetchDiaryData", "Snapshot exists. Children count: ${snapshot.childrenCount}")
                    for (diarySnapshot in snapshot.children) {
                        val diaryData = diarySnapshot.getValue(object : GenericTypeIndicator<Map<String, Any>>() {})
                        Log.d("FetchDiaryData", "Processing diary for key: ${diarySnapshot.key}")

                        if (diaryData == null) {
                            Log.d("FetchDiaryData", "Diary data is null. Skipping.")
                            continue
                        }

                        val date = diarySnapshot.key ?: continue
                        val day = date.split("-")[2].toIntOrNull()

                        if (day == null) {
                            Log.d("FetchDiaryData", "Failed to parse day from date: $date")
                            continue
                        }

                        Log.d("FetchDiaryData", "Parsed day: $day")

                        val userInput = diaryData["userInput"] as? Map<String, Any>

                        if (userInput == null) {
                            Log.d("FetchDiaryData", "userInput is null. Skipping.")
                            continue
                        }

                        val emotionDegree = extractEmotionDegree(userInput)
                        Log.d("FetchDiaryData", "Emotion degree for day $day: $emotionDegree")

                        daysWithDiary.add(day)
                        addStampToCalendar(day, emotionDegree)
                    }
                } else {
                    Log.d("FetchDiaryData", "Snapshot does not exist.")
                }

                for (day in 1..calendar.getActualMaximum(Calendar.DAY_OF_MONTH)) {
                    if (day !in daysWithDiary) {
                        Log.d("FetchDiaryData", "No diary for day $day. Adding blank stamp.")
                        addBlankStampToCalendar(day)
                    }
                }
            }.addOnFailureListener { exception ->
                Log.e("FetchDiaryData", "Failed to fetch data: ${exception.message}", exception)
            }
    }

    // emotionDegree 값을 안전하게 추출하는 함수
    // emotionDegree 값을 안전하게 추출하는 함수
    private fun extractEmotionDegree(userInput: Map<String, Any>): Int {
        val reMeasuredEmotionDegree = userInput["reMeasuredEmotionDegree"] as? Map<String, Any>
        val emotionDegreeFallback = userInput["emotionDegree"] as? Map<String, Any>

        // reMeasuredEmotionDegree에서 값을 먼저 가져오고, 값이 -1이면 emotionDegree로 대체
        val reMeasuredValue = reMeasuredEmotionDegree?.let {
            val value = (it["int"] as? Long)?.toInt() ?: 0
            Log.d("FetchDiaryData", "Extracted reMeasuredEmotionDegree: $value")
            value
        } ?: 0

        // reMeasuredValue가 -1일 경우, emotionDegree를 가져오기
        return if (reMeasuredValue == -1) {
            Log.d("FetchDiaryData", "reMeasuredEmotionDegree is -1, extracting emotionDegree")
            // emotionDegreeFallback에서 값 가져오기
            emotionDegreeFallback?.let {
                val value = (it["int"] as? Long)?.toInt() ?: 0
                Log.d("FetchDiaryData", "Extracted emotionDegree fallback: $value")
                value
            } ?: 0
        } else {
            reMeasuredValue
        }
    }

    private fun addBlankStampToCalendar(day: Int) {
        val today = Calendar.getInstance()
        if (currentCalendar.get(Calendar.YEAR) == today.get(Calendar.YEAR) &&
            currentCalendar.get(Calendar.MONTH) == today.get(Calendar.MONTH) &&
            day > today.get(Calendar.DAY_OF_MONTH)) {
            return
        }

        val index = day + startDay - 1
        if (index in 0 until calendarGrid.childCount) {
            val dayView = calendarGrid.getChildAt(index) as? FrameLayout
            dayView?.let {
                it.children.filterIsInstance<ImageView>().forEach { child ->
                    if (child.drawable.constantState == ResourcesCompat.getDrawable(resources, R.drawable.img_calendaremotion_0, null)?.constantState) {
                        it.removeView(child)
                    }
                }

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
                        val year = SimpleDateFormat("yyyy", Locale.getDefault()).format(selectedDate.time)

                        val intent = Intent(requireContext(), DiaryWriteActivity::class.java).apply {
                            putExtra("SELECTED_DATE", formattedDate)
                            putExtra("SELECTED_YEAR", year)
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

                        // 기존 날짜 포맷 (예: "2024년 11월 20일 수요일")
                        val dateFormat = SimpleDateFormat("yyyy년 M월 d일 E요일", Locale.getDefault())
                        val formattedDate = dateFormat.format(selectedDate.time)

                        // 새로운 날짜 포맷 (예: "2024-11-20")
                        val dateFormatForDb = SimpleDateFormat("yyyy-MM-dd", Locale.getDefault())
                        val formattedDateForDb = dateFormatForDb.format(selectedDate.time)

                        val intent = Intent(requireContext(), CalenderDetailActivity::class.java).apply {
                            putExtra("SELECTED_DATE", formattedDate)
                            putExtra("SELECTED_DATE_FOR_DB", formattedDateForDb) // yyyy-mm-dd 형식 추가
                        }
                        startActivity(intent)
                    }
                }
                it.addView(emotionImageButton)
            }
        }
    }
}
