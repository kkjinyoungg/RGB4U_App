package com.example.rgb4u_app.ui.fragment

import android.graphics.Color
import android.os.Bundle
import android.util.Log // 추가된 import
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageButton
import android.widget.TextView
import android.widget.Toast
import androidx.fragment.app.Fragment
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.example.rgb4u_app.R
import com.example.rgb4u_app.ui.activity.analysis.CardAdapter
import com.example.rgb4u_app.ui.activity.analysis.CardItem
import com.github.mikephil.charting.charts.PieChart
import com.github.mikephil.charting.data.PieData
import com.github.mikephil.charting.data.PieDataSet
import com.github.mikephil.charting.data.PieEntry
import com.github.mikephil.charting.formatter.ValueFormatter
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.database.DatabaseReference
import com.google.firebase.database.FirebaseDatabase
import com.google.firebase.database.DataSnapshot
import com.google.firebase.database.DatabaseError
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
            // 카드 클릭 시 상세보기로 이동하는 로직
            Toast.makeText(context, "${cardItem.typeName} 상세보기로 이동", Toast.LENGTH_SHORT).show()
        }
        recyclerView.adapter = cardAdapter

        // PieChart 설정
        pieChart = view.findViewById(R.id.pie_chart)

        toolbarCalendarTitle = view.findViewById(R.id.toolbar_calendar_title)

        // 현재 날짜 가져오기
        val currentDate = Calendar.getInstance().time
        val dateFormat = SimpleDateFormat("MM월 dd일 E요일", Locale("ko", "KR"))
        val formattedDate = dateFormat.format(currentDate)

        // 툴바에 날짜 설정
        toolbarCalendarTitle.text = formattedDate

        // 버튼 설정
        val buttonAction1 = view.findViewById<ImageButton>(R.id.button_calendar_action1)
        val buttonAction2 = view.findViewById<ImageButton>(R.id.button_calendar_action2)

        // button_calendar_action2의 이미지 리소스 변경
        buttonAction2.setImageResource(R.drawable.ic_back)

        // 버튼 클릭 리스너 설정
        buttonAction1.setOnClickListener {
            // 이전 날짜로 이동하는 로직 추가
            moveToPreviousDate()
        }

        buttonAction2.setOnClickListener {
            // 다음 날짜로 이동하는 로직 추가
            moveToNextDate()
        }

        // Firebase에서 감정 데이터 가져오기
        fetchEmotionData()

        return view
    }

    private fun moveToPreviousDate() {
        val currentDate = Calendar.getInstance()
        currentDate.add(Calendar.DAY_OF_MONTH, -1) // 하루 전으로 이동
        val formattedDate = SimpleDateFormat("MM월 dd일 E요일", Locale.getDefault()).format(currentDate.time)
        toolbarCalendarTitle.text = formattedDate
    }

    private fun moveToNextDate() {
        val currentDate = Calendar.getInstance()
        currentDate.add(Calendar.DAY_OF_MONTH, 1) // 하루 후로 이동
        val formattedDate = SimpleDateFormat("MM월 dd일 E요일", Locale.getDefault()).format(currentDate.time)
        toolbarCalendarTitle.text = formattedDate
    }

    private fun setupPieChart(entries: List<PieEntry>) {
        val dataSet = PieDataSet(entries, "감정 비율")
        dataSet.colors = listOf(
            Color.parseColor("#4CAF50"), // 놀람 (녹색)
            Color.parseColor("#FF9800"), // 두려움 (주황색)
            Color.parseColor("#03A9F4"), // 슬픔 (파란색)
            Color.parseColor("#F44336"), // 분노 (빨간색)
            Color.parseColor("#9C27B0")  // 혐오 (보라색)
        )

        val pieData = PieData(dataSet)  // pieData 먼저 생성
        pieData.setValueTextSize(12f)
        pieData.setValueTextColor(Color.WHITE)

        // 퍼센트 형식으로 값 포맷 지정 (소수점 없이 자연수로 표시)
        pieData.setValueFormatter(object : ValueFormatter() {
            override fun getPieLabel(value: Float, pieEntry: PieEntry?): String {
                return "${Math.round(value)}%" // 소수점 반올림 후 자연수 표시
            }
        })

        pieChart.data = pieData

        // 파이 차트 설정
        pieChart.setUsePercentValues(true)
        pieChart.description.isEnabled = false
        pieChart.isDrawHoleEnabled = true
        pieChart.setHoleColor(Color.TRANSPARENT)
        pieChart.setTransparentCircleAlpha(0)
        pieChart.holeRadius = 45f
        pieChart.setDrawEntryLabels(false)
        pieChart.centerText = "감정 분석"
        pieChart.setEntryLabelColor(Color.BLACK)
        pieChart.setEntryLabelTextSize(12f)

        pieChart.invalidate() // 차트 새로고침

        // 퍼센트 로그 출력
        entries.forEach { entry ->
            Log.d("AnalysisFragment", "${entry.label}: ${entry.value}%")
        }
    }

    private fun fetchEmotionData() {
        val userId = FirebaseAuth.getInstance().currentUser?.uid
        Log.d("AnalysisFragment", "현재 로그인된 사용자 ID: $userId")

        if (userId == null) {
            Toast.makeText(context, "사용자가 로그인되어 있지 않습니다.", Toast.LENGTH_SHORT).show()
            return
        }

        val calendar = Calendar.getInstance()
        val year = calendar.get(Calendar.YEAR)
        val month = calendar.get(Calendar.MONTH) + 1
        val monthFormatted = String.format("%04d-%02d", year, month)

        database.child("users").child(userId)
            .child("monthlyStats").child(monthFormatted).child("emotionsGraph")
            .addValueEventListener(object : ValueEventListener {
                override fun onDataChange(snapshot: DataSnapshot) {
                    if (snapshot.exists()) {
                        val surprise = snapshot.child("Surprise").getValue(Double::class.java)?.toFloat() ?: 0f
                        val fear = snapshot.child("Fear").getValue(Double::class.java)?.toFloat() ?: 0f
                        val sadness = snapshot.child("Sadness").getValue(Double::class.java)?.toFloat() ?: 0f
                        val anger = snapshot.child("Anger").getValue(Double::class.java)?.toFloat() ?: 0f
                        val disgust = snapshot.child("Disgust").getValue(Double::class.java)?.toFloat() ?: 0f

                        Log.d("AnalysisFragment", "Surprise: $surprise, Fear: $fear, Sadness: $sadness, Anger: $anger, Disgust: $disgust")

                        val total = surprise + fear + sadness + anger + disgust
                        val entries: List<PieEntry> = if (total > 0) {
                            val surprisePercentage = (surprise / total * 100)
                            val fearPercentage = (fear / total * 100)
                            val sadnessPercentage = (sadness / total * 100)
                            val angerPercentage = (anger / total * 100)
                            val disgustPercentage = (disgust / total * 100)

                            // 퍼센트가 0보다 큰 경우에만 텍스트 설정 (없으면 안보이게)
                            percentSurprise.visibility = if (surprisePercentage > 0) View.VISIBLE else View.GONE
                            percentSurprise.text = "${Math.round(surprisePercentage)}%"

                            percentFear.visibility = if (fearPercentage > 0) View.VISIBLE else View.GONE
                            percentFear.text = "${Math.round(fearPercentage)}%"

                            percentSadness.visibility = if (sadnessPercentage > 0) View.VISIBLE else View.GONE
                            percentSadness.text = "${Math.round(sadnessPercentage)}%"

                            percentAnger.visibility = if (angerPercentage > 0) View.VISIBLE else View.GONE
                            percentAnger.text = "${Math.round(angerPercentage)}%"

                            percentDisgust.visibility = if (disgustPercentage > 0) View.VISIBLE else View.GONE
                            percentDisgust.text = "${Math.round(disgustPercentage)}%"

                            listOf(
                                PieEntry(surprisePercentage, "놀람"),
                                PieEntry(fearPercentage, "두려움"),
                                PieEntry(sadnessPercentage, "슬픔"),
                                PieEntry(angerPercentage, "분노"),
                                PieEntry(disgustPercentage, "혐오")
                            )
                        } else {
                            listOf(
                                PieEntry(1f, "없음")
                            )
                        }

                        setupPieChart(entries)
                    } else {
                        Toast.makeText(context, "데이터를 찾을 수 없습니다.", Toast.LENGTH_SHORT).show()
                    }
                }

                override fun onCancelled(error: DatabaseError) {
                    Toast.makeText(context, "데이터 로딩 실패: ${error.message}", Toast.LENGTH_SHORT)
                        .show()
                }
            })
    }


    // API 또는 데이터베이스에서 카드 데이터를 가져오는 메소드 예시
    private fun fetchCardData(): List<CardItem> {
        // 실제 데이터 소스에서 가져온다고 가정
        return listOf(
            CardItem("흑백성", R.drawable.ic_planet_a),
            CardItem("걱정성", R.drawable.ic_planet_a),
            CardItem("변화성", R.drawable.ic_planet_a)
        )
    }
}
