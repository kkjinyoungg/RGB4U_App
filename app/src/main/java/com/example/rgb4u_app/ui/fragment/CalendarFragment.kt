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
import androidx.fragment.app.Fragment
import com.example.rgb4u_app.R
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.database.DataSnapshot
import com.google.firebase.database.DatabaseError
import com.google.firebase.database.DatabaseReference
import com.google.firebase.database.FirebaseDatabase
import com.google.firebase.database.GenericTypeIndicator
import com.example.rgb4u_app.ui.activity.calendar.CalenderDetailActivity
import java.text.SimpleDateFormat
import java.util.Calendar
import java.util.Locale
import android.util.Log

class CalendarFragment : Fragment() {

    private lateinit var calendarGrid: GridLayout
    private lateinit var textCurrentMonth: TextView
    private var currentCalendar = Calendar.getInstance()
    private var startDay = 0  // startDay를 클래스 변수로 선언

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
        startDay = currentCalendar.get(Calendar.DAY_OF_WEEK) - 1 // 일요일부터 시작
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

                // Calculate the day from the grid position
                val day = i - startDay + 1

                setTextColor(ResourcesCompat.getColor(resources, R.color.white, null)) // 텍스트 색상 설정

                if (i >= startDay) {
                    text = day.toString()
                    setOnClickListener {
                        // 날짜 클릭 시 다른 화면으로 이동 처리
                        navigateToDetail(day)
                    }

                    // 오늘 날짜 표시
                    if (isToday(day)) {
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
                }
            }

            frameLayout.addView(dayTextView)
            calendarGrid.addView(frameLayout)
        }

        // Firebase 데이터를 가져온 후 도장 추가
        fetchDiaryDataForMonth() // Firebase 데이터 가져오기 호출
    }

    private fun isToday(day: Int): Boolean {
        val today = Calendar.getInstance()
        return currentCalendar.get(Calendar.YEAR) == today.get(Calendar.YEAR) &&
                currentCalendar.get(Calendar.MONTH) == today.get(Calendar.MONTH) &&
                day == today.get(Calendar.DAY_OF_MONTH)
    }

    private fun navigateToDetail(day: Int) {
        // 날짜 클릭 시 '월,일,요일' 정보 생성
        val selectedDate = Calendar.getInstance().apply {
            time = currentCalendar.time
            set(Calendar.DAY_OF_MONTH, day)
        }

        val dateFormat = SimpleDateFormat("yyyy년 M월 d일 E요일", Locale.getDefault()) // '월,일,요일' 포맷
        val formattedDate = dateFormat.format(selectedDate.time)

        // 클릭한 날짜에 대한 토스트 메시지 표시
        Toast.makeText(requireContext(), "$formattedDate 클릭됨", Toast.LENGTH_SHORT).show()

        // 클릭한 날짜 상세 화면으로 이동 (예시)
        //val intent = Intent(requireContext(), DetailActivity::class.java)
        //intent.putExtra("SELECTED_DAY", day)
        //startActivity(intent)
        val intent = Intent(requireContext(), CalenderDetailActivity::class.java)
        intent.putExtra("SELECTED_DATE", formattedDate) // '월,일,요일' 정보를 넘김
        startActivity(intent)
    }


    // dpToPx 확장 함수
    private fun Int.dpToPx(): Int {
        val density = resources.displayMetrics.density
        return (this * density).toInt()
    }

    private fun fetchDiaryDataForMonth() {
        val userId = FirebaseAuth.getInstance().currentUser?.uid
        val yearMonth = SimpleDateFormat("yyyy-MM", Locale.getDefault()).format(currentCalendar.time)
        val firebaseRef = FirebaseDatabase.getInstance().getReference("users/$userId/diaries")

        firebaseRef.orderByChild("date").startAt("$yearMonth-01").endAt("$yearMonth-31")
            .get().addOnSuccessListener { snapshot ->
                if (snapshot.exists()) {
                    for (diarySnapshot in snapshot.children) {
                        // Diary data 가져오기
                        val diaryData = diarySnapshot.getValue(object : GenericTypeIndicator<Map<String, Any>>() {})

                        // diaryData가 null일 경우 처리
                        if (diaryData == null) {
                            Log.d("CalendarFragment", "Diary data is null, skipping this entry")
                            return@addOnSuccessListener // 현재 diarySnapshot 건너뛰기
                        }

                        Log.d("CalendarFragment", "Fetched diaryData: $diaryData")  // diaryData 확인

                        // "userInput"에서 emotionDegree와 reMeasuredEmotionDegree 가져오기
                        val userInput = diaryData["userInput"] as? Map<String, Any>
                        Log.d("CalendarFragment", "Fetched userInput: $userInput")  // userInput 로그 추가

                        // "emotionDegree"와 "reMeasuredEmotionDegree"를 Map<String, Any>에서 가져오기
                        val emotionDegreeMap = userInput?.get("emotionDegree") as? Map<String, Any>
                        val reMeasuredEmotionDegreeMap = userInput?.get("reMeasuredEmotionDegree") as? Map<String, Any>

                        // 각각의 Map 로그 추가
                        Log.d("CalendarFragment", "Fetched emotionDegreeMap: $emotionDegreeMap")  // emotionDegreeMap 로그 추가
                        Log.d("CalendarFragment", "Fetched reMeasuredEmotionDegreeMap: $reMeasuredEmotionDegreeMap")  // reMeasuredEmotionDegreeMap 로그 추가

                        // "int" 값을 안전하게 가져오기
                        val emotionDegreeInt = (emotionDegreeMap?.get("int") as? Long)?.toInt()
                        val reMeasuredEmotionDegreeInt = (reMeasuredEmotionDegreeMap?.get("int") as? Long)?.toInt()

                        // 각 "int" 값 로그 추가
                        Log.d("CalendarFragment", "Fetched emotionDegreeInt: $emotionDegreeInt")  // emotionDegreeInt 로그 추가
                        Log.d("CalendarFragment", "Fetched reMeasuredEmotionDegreeInt: $reMeasuredEmotionDegreeInt")  // reMeasuredEmotionDegreeInt 로그 추가

                        // 로그에 null 체크 추가
                        Log.d("CalendarFragment", "Emotion Degree Int: ${emotionDegreeInt ?: "null"}")
                        Log.d("CalendarFragment", "Re-measured Emotion Degree Int: ${reMeasuredEmotionDegreeInt ?: "null"}")

                        // emotionDegree와 reMeasuredEmotionDegree에서 값 가져오기
                        val degreeValue = reMeasuredEmotionDegreeInt ?: emotionDegreeInt ?: 0

                        // degreeValue가 null인 경우, 기본 값 처리
                        if (degreeValue != 0) {
                            val date = diaryData["date"] as? String
                            if (date == null) {
                                Log.d("CalendarFragment", "Date is null, skipping this entry")
                                return@addOnSuccessListener // 여기에서 반복문을 건너뛰도록 처리
                            }

                            Log.d("CalendarFragment", "Fetched diary for date: $date")
                            val dateComponents = date.split("-")
                            if (dateComponents.size == 3) {
                                val day = dateComponents[2].toIntOrNull()
                                if (day != null) {
                                    Log.d("CalendarFragment", "Day from date: $day")
                                    addStampToCalendar(day, degreeValue)  // degreeValue는 이미 int 값
                                } else {
                                    Log.e("CalendarFragment", "Invalid day value: $day")
                                }
                            }
                        } else {
                            Log.d("CalendarFragment", "Degree value is null, using default value 0")
                        }
                    }
                }
            }
    }



    private fun addStampToCalendar(day: Int, degree: Int) {
        val emotionDrawable = when (degree) {
            0 -> R.drawable.img_calenderemotion_0
            1 -> R.drawable.img_calenderemotion_1
            2 -> R.drawable.img_calenderemotion_2
            3 -> R.drawable.img_calenderemotion_3
            else -> R.drawable.img_calenderemotion_4
        }

        // startDay와 maxDay를 고려하여 index 계산
        val index = day + startDay - 1

        // 인덱스를 로그로 출력하여 위치 확인
        Log.d("CalendarFragment", "Adding stamp for day $day at index $index")

        // 유효한 인덱스인지 확인
        if (index >= 0 && index < calendarGrid.childCount) {
            val dayView = calendarGrid.getChildAt(index) as? FrameLayout
            dayView?.let {
                // 도장 이미지를 추가하는 부분
                val stampImage = ImageView(requireContext()).apply {
                    layoutParams = FrameLayout.LayoutParams(40.dpToPx(), 40.dpToPx()).apply {
                        gravity = Gravity.CENTER
                    }
                    setImageDrawable(ResourcesCompat.getDrawable(resources, emotionDrawable, null))
                }
                it.addView(stampImage)
            }
        } else {
            Log.e("CalendarFragment", "유효하지 않은 인덱스: $index, 도장을 추가할 수 없습니다.")
        }
    }
}
