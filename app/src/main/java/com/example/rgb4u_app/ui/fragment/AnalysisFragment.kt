package com.example.rgb4u_app.ui.fragment

import android.content.Intent
import android.graphics.Color
import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageButton
import android.widget.LinearLayout
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
    private lateinit var overlayView: View // 반투명 막
    private lateinit var tvViewDetails: LinearLayout // "자세히 보기" 링크

    // 현재 날짜를 저장하는 변수
    private val currentDate = Calendar.getInstance()

    //통계 상세를 위한 날짜 저장 (yyyy-mm)
    private var formattedDate2: String = ""

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        val view = inflater.inflate(R.layout.fragment_analysis, container, false)

        // RecyclerView 설정
        recyclerView = view.findViewById(R.id.recycler_view_cards)
        recyclerView.layoutManager = LinearLayoutManager(context, LinearLayoutManager.HORIZONTAL, false)

        // TextView 초기화 (놀람, 두려움, 슬픔, 분노, 혐오)
        percentFear = view.findViewById(R.id.percent_fear)
        percentSadness = view.findViewById(R.id.percent_sadness)
        percentAnger = view.findViewById(R.id.percent_anger)
        percentDisgust = view.findViewById(R.id.percent_disgust)
        percentSurprise = view.findViewById(R.id.percent_surprise)

        // 카드 데이터 가져오기
        val cardList = fetchCardData()

        // 어댑터 설정
        cardAdapter = CardAdapter(cardList) { cardItem ->
            // PlanetDetailFragment로 이동하고 typeName 전달
            val fragment = PlanetDetailFragment.newInstance(cardItem.typeName)
            parentFragmentManager.beginTransaction()  // 또는 requireActivity().supportFragmentManager.beginTransaction()
                .replace(R.id.fragment_container, fragment)
                .addToBackStack(null)
                .commit()

        }

        recyclerView.adapter = cardAdapter

        // PieChart 설정
        pieChart = view.findViewById(R.id.pie_chart)
        pieChart.isRotationEnabled = false

        toolbarCalendarTitle = view.findViewById(R.id.toolbar_calendar_title)

        // 초기 날짜 설정
        updateToolbarDate()

        // 버튼 설정
        val buttonAction1 = view.findViewById<ImageButton>(R.id.button_calendar_action1)
        val buttonAction2 = view.findViewById<ImageButton>(R.id.button_calendar_action2)

        // button_calendar_action2의 이미지 리소스 변경
        buttonAction1.setImageResource(R.drawable.ic_analysis_back) // 임시라 추후 수정
        buttonAction2.setImageResource(R.drawable.ic_analysis_next) // 임시라 추후 수정

        // 버튼 클릭 리스너 설정
        buttonAction1.setOnClickListener {
            // 이전 날짜로 이동하는 로직 추가
            moveToPreviousDate()
            fetchEmotionData() // 날짜 변경 시 데이터 다시 가져오기
        }

        buttonAction2.setOnClickListener {
            // 다음 날짜로 이동하는 로직 추가
            moveToNextDate()
            fetchEmotionData() // 날짜 변경 시 데이터 다시 가져오기
        }

        // overlayView 초기화
        overlayView = view.findViewById(R.id.overlay_view) // overlay_view의 ID를 사용하여 초기화

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

    private fun moveToPreviousDate() {
        currentDate.add(Calendar.MONTH, -1) // 한 달 전으로 이동
        updateToolbarDate()
        fetchEmotionData() // 날짜 변경 시 데이터 다시 가져오기
    }

    private fun moveToNextDate() {
        currentDate.add(Calendar.MONTH, 1) // 한 달 후로 이동
        updateToolbarDate()
        fetchEmotionData() // 날짜 변경 시 데이터 다시 가져오기
    }

    // 툴바의 날짜를 업데이트하는 메소드
    private fun updateToolbarDate() {
        val formattedDate = SimpleDateFormat("yyyy년 MM월", Locale("ko", "KR")).format(currentDate.time)
        toolbarCalendarTitle.text = formattedDate
        //통계 상세용 날짜도 함께 업데이트
        formattedDate2 = SimpleDateFormat("yyyy-MM", Locale("ko", "KR")).format(currentDate.time)
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
    }

    private fun fetchEmotionData() {
        overlayView.visibility = View.VISIBLE // 데이터 로드 전 기본적으로 오버레이를 보이게 설정

        val userId = FirebaseAuth.getInstance().currentUser?.uid
        Log.d("AnalysisFragment", "현재 로그인된 사용자 ID: $userId")

        if (userId == null) {
            Toast.makeText(context, "사용자가 로그인되어 있지 않습니다.", Toast.LENGTH_SHORT).show()
            return
        }

        val calendar = currentDate // 현재 날짜 사용
        val year = calendar.get(Calendar.YEAR)
        val month = calendar.get(Calendar.MONTH) + 1
        val monthFormatted = String.format("%04d-%02d", year, month)

        database.child("users").child(userId)
            .child("monthlyStats").child(monthFormatted).child("emotionsGraph")
            .addValueEventListener(object : ValueEventListener {
                override fun onDataChange(snapshot: DataSnapshot) {
                    if (snapshot.exists()) {
                        // 데이터가 있는 경우 오버레이 숨김
                        overlayView.visibility = View.GONE

                        val surprise = snapshot.child("Surprise").getValue(Double::class.java)?.toFloat() ?: 0f
                        val fear = snapshot.child("Fear").getValue(Double::class.java)?.toFloat() ?: 0f
                        val sadness = snapshot.child("Sadness").getValue(Double::class.java)?.toFloat() ?: 0f
                        val anger = snapshot.child("Anger").getValue(Double::class.java)?.toFloat() ?: 0f
                        val disgust = snapshot.child("Disgust").getValue(Double::class.java)?.toFloat() ?: 0f

                        Log.d("AnalysisFragment", "Surprise: $surprise, Fear: $fear, Sadness: $sadness, Anger: $anger, Disgust: $disgust")

                        val total = surprise + fear + sadness + anger + disgust
                        val entries: List<PieEntry> = if (total > 0) {
                            // 퍼센트 계산
                            val surprisePercent = (surprise / total * 100)
                            val fearPercent = (fear / total * 100)
                            val sadnessPercent = (sadness / total * 100)
                            val angerPercent = (anger / total * 100)
                            val disgustPercent = (disgust / total * 100)

                            // TextView에 값 설정 및 보이기/숨기기
                            if (surprisePercent > 0) {
                                percentSurprise.text = "${Math.round(surprisePercent)}%"
                                percentSurprise.visibility = View.VISIBLE
                            } else {
                                percentSurprise.visibility = View.GONE
                            }

                            if (fearPercent > 0) {
                                percentFear.text = "${Math.round(fearPercent)}%"
                                percentFear.visibility = View.VISIBLE
                            } else {
                                percentFear.visibility = View.GONE
                            }

                            if (sadnessPercent > 0) {
                                percentSadness.text = "${Math.round(sadnessPercent)}%"
                                percentSadness.visibility = View.VISIBLE
                            } else {
                                percentSadness.visibility = View.GONE
                            }

                            if (angerPercent > 0) {
                                percentAnger.text = "${Math.round(angerPercent)}%"
                                percentAnger.visibility = View.VISIBLE
                            } else {
                                percentAnger.visibility = View.GONE
                            }

                            if (disgustPercent > 0) {
                                percentDisgust.text = "${Math.round(disgustPercent)}%"
                                percentDisgust.visibility = View.VISIBLE
                            } else {
                                percentDisgust.visibility = View.GONE
                            }

                            listOf(
                                PieEntry(surprisePercent, "놀람"),
                                PieEntry(fearPercent, "두려움"),
                                PieEntry(sadnessPercent, "슬픔"),
                                PieEntry(angerPercent, "분노"),
                                PieEntry(disgustPercent, "혐오")
                            )
                        } else {
                            // 데이터가 없을 때
                            listOf(PieEntry(1f, "없음"))
                        }
                        setupPieChart(entries)
                        // 로그 출력으로 entries 확인
                        Log.d("계산완료", "Entries: $entries")
                    } else {
                        // 데이터가 없을 때 오버레이 보임
                        overlayView.visibility = View.VISIBLE
                        Toast.makeText(context, "데이터를 찾을 수 없습니다.", Toast.LENGTH_SHORT).show()
                    }
                }

                override fun onCancelled(error: DatabaseError) {
                    overlayView.visibility = View.VISIBLE // 오류 시 오버레이 보이게 설정
                    Toast.makeText(context, "데이터 로딩 실패: ${error.message}", Toast.LENGTH_SHORT)
                        .show()
                }
            })
    }

    //인지왜곡 Top3 카드 연결
    private fun fetchCardData(): List<CardItem> {
        val cardList = mutableListOf<CardItem>()

        // Firebase에서 월별 분석 데이터 가져오기
        val userId = FirebaseAuth.getInstance().currentUser?.uid
        if (userId == null) {
            Log.d("fetchCardData", "사용자가 로그인되어 있지 않습니다.")
            return cardList
        }

        val calendar = currentDate
        val year = calendar.get(Calendar.YEAR)
        val month = calendar.get(Calendar.MONTH) + 1
        val monthFormatted = String.format("%04d-%02d", year, month)

        Log.d("fetchCardData", "월별 분석 데이터 가져오기: $monthFormatted")

        // MonthlyAnalysis에서 해당 월의 행성 데이터 가져오기
        database.child("users").child(userId)
            .child("monthlyAnalysis").child(monthFormatted)
            .addListenerForSingleValueEvent(object : ValueEventListener {
                override fun onDataChange(snapshot: DataSnapshot) {
                    if (snapshot.exists()) {
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

                                        // 카드 어댑터 갱신
                                        Log.d("fetchCardData", "카드 어댑터 갱신: $planetName")
                                        cardAdapter.notifyDataSetChanged()
                                    }.addOnFailureListener { exception ->
                                        Log.e("fetchCardData", "imageResource 가져오기 실패: ${exception.message}")
                                    }

                            }
                        } else {
                            Log.d("fetchCardData", "행성 데이터가 없습니다.")
                        }
                    } else {
                        Log.d("fetchCardData", "MonthlyAnalysis 데이터가 없습니다.")
                    }
                }

                override fun onCancelled(error: DatabaseError) {
                    Log.e("fetchCardData", "데이터를 가져오는 데 실패했습니다: ${error.message}")
                }
            })

        return cardList
    }


}
