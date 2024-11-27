package com.example.rgb4u_app.ui.fragment

import android.app.Dialog
import android.content.Intent
import android.graphics.Color
import android.os.Build
import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Button
import android.widget.ImageButton
import android.widget.LinearLayout
import android.widget.NumberPicker
import android.widget.TextView
import android.widget.Toast
import androidx.fragment.app.Fragment
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.example.rgb4u_app.R
import com.example.rgb4u_app.ui.activity.analysis.CardAdapter
import com.example.rgb4u_app.ui.activity.analysis.CardItem
import com.example.rgb4u_app.ui.activity.analysis.FrequentEmotionsActivity
import com.github.mikephil.charting.charts.PieChart
import com.github.mikephil.charting.data.PieData
import com.github.mikephil.charting.data.PieDataSet
import com.github.mikephil.charting.data.PieEntry
import com.github.mikephil.charting.formatter.ValueFormatter
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.database.DataSnapshot
import com.google.firebase.database.DatabaseError
import com.google.firebase.database.DatabaseReference
import com.google.firebase.database.FirebaseDatabase
import com.google.firebase.database.ValueEventListener
import java.text.SimpleDateFormat
import java.util.Calendar
import java.util.Locale

class AnalysisFragment : Fragment() {
    private lateinit var recyclerView: RecyclerView
    private lateinit var cardAdapter: CardAdapter
    private lateinit var pieChart: PieChart
    private lateinit var toolbarCalendarTitle: TextView
    private val database: DatabaseReference = FirebaseDatabase.getInstance().reference
    private lateinit var percentFear: TextView
    private lateinit var percentSadness: TextView
    private lateinit var percentAnger: TextView
    private lateinit var percentDisgust: TextView
    private lateinit var percentSurprise: TextView
    private lateinit var percentLayoutSurprise : LinearLayout
    private lateinit var percentLayoutFear : LinearLayout
    private lateinit var percentLayoutSadness : LinearLayout
    private lateinit var percentLayoutAnger : LinearLayout
    private lateinit var percentLayoutDisgust : LinearLayout
    private lateinit var overlayView: View // 반투명 막
    private lateinit var tvViewDetails: LinearLayout // "자세히 보기" 링크
    private lateinit var nodataLayout: LinearLayout
    private lateinit var emotionTitle: TextView
    private lateinit var emotionratioTitle: TextView
    private lateinit var viewDetail: LinearLayout
    private lateinit var emotionChart: LinearLayout
    private lateinit var btnMonth : LinearLayout
    private lateinit var emptyView: LinearLayout

    private var currentCalendar = Calendar.getInstance()

//    // 현재 날짜를 저장하는 변수
//    private val currentDate = Calendar.getInstance()

    //통계 상세를 위한 날짜 저장 (yyyy-mm)
    private var formattedDate2: String = ""

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        val view = inflater.inflate(R.layout.fragment_analysis, container, false)

        // 상태바 투명
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {
            requireActivity().window.apply {
                decorView.systemUiVisibility =
                    View.SYSTEM_UI_FLAG_LAYOUT_FULLSCREEN or View.SYSTEM_UI_FLAG_LAYOUT_STABLE
                statusBarColor = android.graphics.Color.TRANSPARENT
            }
        }

        // RecyclerView 설정
        recyclerView = view.findViewById(R.id.recycler_view_cards)
        recyclerView.layoutManager = LinearLayoutManager(context, LinearLayoutManager.HORIZONTAL, false)

        // TextView 초기화 (놀람, 두려움, 슬픔, 분노, 혐오)
        percentFear = view.findViewById(R.id.percent_fear)
        percentSadness = view.findViewById(R.id.percent_sadness)
        percentAnger = view.findViewById(R.id.percent_anger)
        percentDisgust = view.findViewById(R.id.percent_disgust)
        percentSurprise = view.findViewById(R.id.percent_surprise)

        // 감정 레이아웃
        percentLayoutSurprise = view.findViewById<LinearLayout>(R.id.percent_layout_surprise)
        percentLayoutFear = view.findViewById<LinearLayout>(R.id.percent_layout_fear)
        percentLayoutSadness = view.findViewById<LinearLayout>(R.id.percent_layout_sadness)
        percentLayoutAnger = view.findViewById<LinearLayout>(R.id.percent_layout_anger)
        percentLayoutDisgust = view.findViewById<LinearLayout>(R.id.percent_layout_disgust)

        toolbarCalendarTitle = view.findViewById(R.id.toolbar_calendar_title)

        // 카드 데이터 가져오기
        val cardList = fetchCardData()

        // cardAdapter에서 formattedDate2를 함께 전달
        cardAdapter = CardAdapter(cardList, formattedDate2) { cardItem, formattedDate2 ->
            val fragment = PlanetDetailFragment.newInstance(cardItem.typeName, cardItem.imageResourceId, formattedDate2)
            parentFragmentManager.beginTransaction()
                .replace(R.id.fragment_container, fragment)
                .addToBackStack(null)
                .commit()

        }

        // 초기 날짜 설정
        updateToolbarDate()

        // RecyclerView에 어댑터 설정
        recyclerView.adapter = cardAdapter

//        // 데이터가 없으면 RecyclerView 숨기고 emptyView 보이기
//        if (cardList.isEmpty()) {
//            recyclerView.visibility = View.GONE
//            emptyView.visibility = View.VISIBLE
//        } else {
//            recyclerView.visibility = View.VISIBLE
//            emptyView.visibility = View.GONE
//        }

        // PieChart 설정
        pieChart = view.findViewById(R.id.pie_chart)
        pieChart.isRotationEnabled = false

        toolbarCalendarTitle = view.findViewById(R.id.toolbar_calendar_title)
        btnMonth = view.findViewById(R.id.month_btn)

        btnMonth.setOnClickListener {
            showMonthYearPickerDialog()
        }

        // 초기 날짜 설정
        updateToolbarDate()

        // 버튼 설정
        val buttonAction1 = view.findViewById<ImageButton>(R.id.button_calendar_action1)
        val buttonAction2 = view.findViewById<ImageButton>(R.id.button_calendar_action2)

        // button_calendar_action2의 이미지 리소스 변경
        buttonAction1.setImageResource(R.drawable.ic_left_triangle_wh) // 임시라 추후 수정
        buttonAction2.setImageResource(R.drawable.ic_right_triangle_wh) // 임시라 추후 수정

        // 버튼 클릭 리스너 설정
        buttonAction1.setOnClickListener {
            // 이전 날짜로 이동하는 로직 추가
            moveToPreviousDate()
            fetchEmotionData() // 날짜 변경 시 데이터 다시 가져오기 )
            // 카드 데이터 가져오기
            val cardList = fetchCardData()
        }

        buttonAction2.setOnClickListener {
            // 다음 날짜로 이동하는 로직 추가
            moveToNextDate()
            fetchEmotionData() // 날짜 변경 시 데이터 다시 가져오기
            // 카드 데이터 가져오기
            val cardList = fetchCardData()
        }

        // overlayView 초기화
        overlayView = view.findViewById(R.id.overlay_view) // overlay_view의 ID를 사용하여 초기화

        nodataLayout = view.findViewById(R.id.no_data_layout)  // nodataLayout 초기화

        emptyView = view.findViewById(R.id.empty_view) // 행성 유형 데이터 없을 떄 / emptyView 레이아웃 초기화

        emotionTitle = view.findViewById(R.id.tv_emotion_title) // 행성 top3 타이틀
        emotionratioTitle = view.findViewById(R.id.tv_emotion_ratio_title) // 감정 타이틀
        viewDetail = view.findViewById(R.id.tv_view_details) // 감정 자세히 보기 버튼
        emotionChart = view.findViewById(R.id.linearLayout4) // 감정 그래프 부분

        // Firebase에서 감정 데이터 가져오기
        fetchEmotionData()


        // "자세히 보기" 링크 초기화 및 클릭 리스너 설정
        tvViewDetails = view.findViewById(R.id.tv_view_details)
        tvViewDetails.setOnClickListener {
            Toast.makeText(context, "자세히 보기를 클릭했습니다.", Toast.LENGTH_SHORT).show()
            // 날짜를 전달(FrequentEmotionsActivity로)
            val intent = Intent(context, FrequentEmotionsActivity::class.java)
            intent.putExtra("selectedDate", formattedDate2)
            Log.d("AnalysisFragment", "감정 통계 상세로 보낸 날짜: $formattedDate2")
            startActivity(intent)
        }

        return view
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

        // NumberPicker 범위 설정 (연도)
        yearPicker.minValue = currentYear - 100
        yearPicker.maxValue = currentYear
        yearPicker.wrapSelectorWheel = false // 순환 비활성화
        yearPicker.value = currentCalendar.get(Calendar.YEAR)
        yearPicker.displayedValues = Array(yearPicker.maxValue - yearPicker.minValue + 1) { i ->
            "${yearPicker.minValue + i}년"
        }

        // MonthPicker 업데이트 함수
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

        // 연도 변경 시 MonthPicker 범위 업데이트
        yearPicker.setOnValueChangedListener { _, _, newYear ->
            updateMonthPicker(newYear)
        }

        // 확인 버튼 클릭 시 동작
        btnConfirm.setOnClickListener {
            val selectedYear = yearPicker.value
            val selectedMonth = monthPicker.value // 이미 1부터 시작하는 값

            // 선택된 날짜를 currentCalendar에 반영
            currentCalendar.set(Calendar.YEAR, selectedYear)
            currentCalendar.set(Calendar.MONTH, selectedMonth - 1) // Month는 0부터 시작

            // 툴바의 날짜 갱신
            updateToolbarDate()

            fetchEmotionData() // 날짜 변경 시 데이터 다시 가져오기 )
            // 카드 데이터 가져오기
            val cardList = fetchCardData()

            dialog.dismiss()
        }

        dialog.show()
    }



    private fun moveToPreviousDate() {
        currentCalendar.add(Calendar.MONTH, -1) // 한 달 전으로 이동
        updateToolbarDate()
        fetchEmotionData() // 날짜 변경 시 데이터 다시 가져오기

        // 카드 데이터 가져오기
        val cardList = fetchCardData()
    }

    private fun moveToNextDate() {
        val currentDate = Calendar.getInstance()

        // 현재 연도와 월을 비교해서, currentCalendar가 현재 날짜 이후로 넘어가지 않도록 설정
        if (currentCalendar.get(Calendar.YEAR) < currentDate.get(Calendar.YEAR) ||
            (currentCalendar.get(Calendar.YEAR) == currentDate.get(Calendar.YEAR) && currentCalendar.get(Calendar.MONTH) < currentDate.get(Calendar.MONTH))) {
            currentCalendar.add(Calendar.MONTH, 1) // 한 달 후로 이동
            updateToolbarDate()
            fetchEmotionData() // 날짜 변경 시 데이터 다시 가져오기

            // 카드 데이터 가져오기
            val cardList = fetchCardData()
        } else {
            Toast.makeText(requireContext(), "다음 달로는 이동할 수 없어요", Toast.LENGTH_SHORT).show()
        }
    }



    // 툴바의 날짜를 업데이트하는 메소드
    private fun updateToolbarDate() {
        val formattedDate = SimpleDateFormat("yyyy년 MM월", Locale("ko", "KR")).format(currentCalendar.time)
        toolbarCalendarTitle.text = formattedDate
        formattedDate2 = SimpleDateFormat("yyyy-MM", Locale("ko", "KR")).format(currentCalendar.time)
        // cardAdapter에서 formattedDate2를 최신으로 전달
        cardAdapter.updateFormattedDate(formattedDate2)
        // 로그 찍기
        Log.d("ToolbarDate", "formattedDate: $formattedDate")  // formattedDate 로그
        Log.d("ToolbarDate", "formattedDate2: $formattedDate2")  // formattedDate2 로그
    }


    private fun setupPieChart(entries: List<PieEntry>) {
        // 파이 차트에 들어갈 데이터 설정
        val dataSet = PieDataSet(entries, "감정 비율").apply {
            colors = listOf(
                Color.parseColor("#86DDC4"), // 놀람
                Color.parseColor("#5CC1D4"), // 두려움
                Color.parseColor("#5795DC"), // 슬픔
                Color.parseColor("#EDA4C1"), // 분노
                Color.parseColor("#AC88F5")  // 혐오
            )
            valueTextSize = 0f // 데이터 값 텍스트 크기를 0으로 설정하여 텍스트 제거
            sliceSpace = 2f // 슬라이스 간의 공간 설정 (4dp)
        }

        val pieData = PieData(dataSet).apply {
            setValueTextSize(0f)

            // 퍼센트 형식으로 값 포맷 지정 (소수점 없이 자연수로 표시)
            setValueFormatter(object : ValueFormatter() {
                override fun getPieLabel(value: Float, pieEntry: PieEntry?): String {
                    return "${Math.round(value)}%" // 소수점 반올림 후 자연수 표시
                }
            })
        }

        // 차트 데이터 설정
        pieChart.data = pieData

        // 파이 차트 설정
        pieChart.setUsePercentValues(false)
        pieChart.description.isEnabled = false
        pieChart.isDrawHoleEnabled = true
        pieChart.setHoleColor(Color.TRANSPARENT)
        pieChart.setTransparentCircleAlpha(0)
        pieChart.holeRadius = 64f
        pieChart.setDrawEntryLabels(false)
        pieChart.legend.isEnabled = false   // 하단 설명 비활성화
        // pieChart.centerText = "감정 분석"
        // pieChart.setEntryLabelColor(Color.BLACK)
        // pieChart.setEntryLabelTextSize(12f)

        pieChart.invalidate() // 차트 새로 고침

        // 퍼센트 로그 출력
        entries.forEach { entry ->
            Log.d("AnalysisFragment", "${entry.label}: ${entry.value}%")
        }

        // 각 감정에 대해 해당 LinearLayout 숨기기/보이기 설정
        entries.forEach { entry ->
            when (entry.label) {
                "놀람" -> {
                    if (entry.value > 0) {
                        percentLayoutSurprise.visibility = View.VISIBLE
                        percentSurprise.text = "${Math.round(entry.value)}%" // 값을 표시
                    } else {
                        percentLayoutSurprise.visibility = View.GONE
                    }
                }

                "두려움" -> {
                    if (entry.value > 0) {
                        percentLayoutFear.visibility = View.VISIBLE
                        percentFear.text = "${Math.round(entry.value)}%" // 값을 표시
                    } else {
                        percentLayoutFear.visibility = View.GONE
                    }
                }

                "슬픔" -> {
                    if (entry.value > 0) {
                        percentLayoutSadness.visibility = View.VISIBLE
                        percentSadness.text = "${Math.round(entry.value)}%" // 값을 표시
                    } else {
                        percentLayoutSadness.visibility = View.GONE
                    }
                }

                "분노" -> {
                    if (entry.value > 0) {
                        percentLayoutAnger.visibility = View.VISIBLE
                        percentAnger.text = "${Math.round(entry.value)}%" // 값을 표시
                    } else {
                        percentLayoutAnger.visibility = View.GONE
                    }
                }

                "혐오" -> {
                    if (entry.value > 0) {
                        percentLayoutDisgust.visibility = View.VISIBLE
                        percentDisgust.text = "${Math.round(entry.value)}%" // 값을 표시
                    } else {
                        percentLayoutDisgust.visibility = View.GONE
                    }
                }
            }
        }
    }

    private fun fetchEmotionData() {
        // overlayView.visibility = View.VISIBLE // 데이터 로드 전 기본적으로 오버레이를 보이게 설정

        val userId = FirebaseAuth.getInstance().currentUser?.uid
        Log.d("AnalysisFragment", "현재 로그인된 사용자 ID: $userId")

        if (userId == null) {
            //Toast.makeText(context, "사용자가 로그인되어 있지 않습니다.", Toast.LENGTH_SHORT).show()
            return
        }

        val calendar = currentCalendar // 현재 날짜 사용
        val year = calendar.get(Calendar.YEAR)
        val month = calendar.get(Calendar.MONTH) + 1
        val monthFormatted = String.format("%04d-%02d", year, month)

        Log.d("fetchCardData", "월별 감정 분석 데이터 가져오기: $monthFormatted")

        database.child("users").child(userId)
            .child("monthlyStats").child(monthFormatted).child("emotionsGraph")
            .addValueEventListener(object : ValueEventListener {
                override fun onDataChange(snapshot: DataSnapshot) {
                    if (snapshot.exists()) {
                        // 데이터가 있는 경우
                        overlayView.visibility = View.GONE
                        nodataLayout.visibility = View.GONE

                        emotionTitle.visibility = View.VISIBLE
                        emotionratioTitle.visibility = View.VISIBLE
                        viewDetail.visibility = View.VISIBLE
                        emotionChart.visibility = View.VISIBLE

                        // 감정 데이터 처리
                        val surprise = snapshot.child("Surprise").getValue(Double::class.java)?.toFloat() ?: 0f
                        val fear = snapshot.child("Fear").getValue(Double::class.java)?.toFloat() ?: 0f
                        val sadness = snapshot.child("Sadness").getValue(Double::class.java)?.toFloat() ?: 0f
                        val anger = snapshot.child("Anger").getValue(Double::class.java)?.toFloat() ?: 0f
                        val disgust = snapshot.child("Disgust").getValue(Double::class.java)?.toFloat() ?: 0f

                        val total = surprise + fear + sadness + anger + disgust
                        val entries: List<PieEntry> = if (total > 0) {
                            val surprisePercent = (surprise / total * 100)
                            val fearPercent = (fear / total * 100)
                            val sadnessPercent = (sadness / total * 100)
                            val angerPercent = (anger / total * 100)
                            val disgustPercent = (disgust / total * 100)

                            percentSurprise.text = "${Math.round(surprisePercent)}%"
                            percentFear.text = "${Math.round(fearPercent)}%"
                            percentSadness.text = "${Math.round(sadnessPercent)}%"
                            percentAnger.text = "${Math.round(angerPercent)}%"
                            percentDisgust.text = "${Math.round(disgustPercent)}%"

                            listOf(
                                PieEntry(surprisePercent, "놀람"),
                                PieEntry(fearPercent, "두려움"),
                                PieEntry(sadnessPercent, "슬픔"),
                                PieEntry(angerPercent, "분노"),
                                PieEntry(disgustPercent, "혐오")
                            )
                        } else {
                            listOf(PieEntry(1f, "없음"))
                        }

                        setupPieChart(entries)

                    } else {
                        overlayView.visibility = View.GONE
                        nodataLayout.visibility = View.VISIBLE

                        emotionTitle.visibility = View.GONE
                        emotionratioTitle.visibility = View.GONE
                        viewDetail.visibility = View.GONE
                        emotionChart.visibility = View.GONE
                        recyclerView.visibility = View.GONE
                        emptyView.visibility = View.GONE
                    }
                }

                override fun onCancelled(error: DatabaseError) {
                    overlayView.visibility = View.VISIBLE // 오류 시 오버레이 보이게 설정
                }
            })

    }

    //인지왜곡 Top3 카드 연결
    private fun fetchCardData(): List<CardItem> {
        var cardList = mutableListOf<CardItem>()

        // Firebase에서 월별 분석 데이터 가져오기
        val userId = FirebaseAuth.getInstance().currentUser?.uid
        if (userId == null) {
            Log.d("fetchCardData", "사용자가 로그인되어 있지 않습니다.")
            return cardList
        }

        val calendar = currentCalendar
        val year = calendar.get(Calendar.YEAR)
        val month = calendar.get(Calendar.MONTH) + 1
        val monthFormatted = String.format("%04d-%02d", year, month)

        Log.d("fetchCardData", "월별 Top3 분석 데이터 가져오기: $monthFormatted")

        // MonthlyAnalysis에서 해당 월의 행성 데이터 가져오기
        database.child("users").child(userId)
            .child("monthlyAnalysis").child(monthFormatted)
            .addListenerForSingleValueEvent(object : ValueEventListener {
                override fun onDataChange(snapshot: DataSnapshot) {
                    if (snapshot.exists()) {
                        recyclerView.visibility = View.VISIBLE
                        emptyView.visibility = View.GONE

                        val planetCounts = mutableMapOf<String, Int>()

                        // 행성별 count 값을 가져와서 Map에 저장
                        for (planetSnapshot in snapshot.children) {
                            val planetName = planetSnapshot.key ?: continue
                            val count = planetSnapshot.child("count").getValue(Int::class.java) ?: 0
                            Log.d("fetchCardData", "행성: $planetName, count: $count")
                            if (count >= 1) {
                                planetCounts[planetName] = count
                            }
                        }

                        // count 값이 1 이상인 행성들을 count 순으로 정렬
                        val sortedPlanets = planetCounts.entries.sortedByDescending { it.value }.take(3)
                        Log.d("fetchCardData", "정렬된 행성들: $sortedPlanets")

                        // 행성이 하나라도 있으면 카드 리스트에 추가
                        if (sortedPlanets.isNotEmpty()) {

                            for ((planetName, _) in sortedPlanets) {
                                Log.d("fetchCardData", "행성 데이터 추가: $planetName")

                                // distortionInformation에서 해당 행성의 imageResource 가져오기
                                database.child("users").child(userId).child("distortionInformation").child(planetName)
                                    .child("imageResource").get().addOnSuccessListener { imageSnapshot ->
                                        // imageResource 이름 가져오기
                                        val imageResourceName = imageSnapshot.getValue(String::class.java) ?: "ic_planet_a"

                                        // imageResource와 imageResourceName 로그 출력
                                        Log.d("fetchCardData", "imageResource 가져옴: $imageResourceName")

                                        // context가 null일 경우를 처리하는 안전한 호출
                                        val imageResourceId = context?.let {
                                            // imageResourceName이 drawable 리소스로 잘 매칭되는지 확인
                                            val resourceId = it.resources.getIdentifier(imageResourceName, "drawable", it.packageName)
                                            Log.d("fetchCardData", "이미지 리소스 ID: $resourceId")  // 리소스 ID 로그 출력
                                            resourceId
                                        } ?: R.drawable.ic_planet_a  // context가 null일 경우 기본값 사용

                                        // imageResourceId 로그 출력
                                        Log.d("fetchCardData", "최종 이미지 리소스 ID: $imageResourceId")

                                        // 카드 아이템 생성
                                        cardList.add(CardItem(planetName, imageResourceId))
                                        // 어댑터에 데이터 업데이트
                                        cardAdapter.updateCardData(cardList)
                                        // 카드 어댑터 갱신
                                        Log.d("fetchCardData", "카드 어댑터 갱신: $planetName")
                                        cardAdapter.notifyDataSetChanged()

                                    }.addOnFailureListener { exception ->
                                        Log.e("fetchCardData", "imageResource 가져오기 실패: ${exception.message}")
                                    }

                            }
                        } else {
                            Log.d("fetchCardData", "행성 데이터가 없습니다.")
                            if (nodataLayout.visibility != View.VISIBLE) {
                                recyclerView.visibility = View.GONE
                                emptyView.visibility = View.VISIBLE
                            }
                        }
                    } else {
                        Log.d("fetchCardData", "MonthlyAnalysis 데이터가 없습니다.")
                        if (nodataLayout.visibility != View.VISIBLE) {
                            recyclerView.visibility = View.GONE
                            emptyView.visibility = View.VISIBLE
                        }
                    }
                }

                override fun onCancelled(error: DatabaseError) {
                    Log.e("fetchCardData", "데이터를 가져오는 데 실패했습니다: ${error.message}")
                }
            })

        return cardList
    }


}
