package com.example.rgb4u_app.ui.fragment

import android.graphics.Color
import android.os.Bundle
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
import java.text.SimpleDateFormat
import java.util.Calendar
import java.util.Locale

class AnalysisFragment : Fragment() {
    private lateinit var recyclerView: RecyclerView
    private lateinit var cardAdapter: CardAdapter // 제네릭 제거
    private lateinit var pieChart: PieChart
    private lateinit var toolbarCalendarTitle: TextView

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        val view = inflater.inflate(R.layout.fragment_analysis, container, false)

        // RecyclerView 설정
        recyclerView = view.findViewById(R.id.recycler_view_cards)
        recyclerView.layoutManager = LinearLayoutManager(context, LinearLayoutManager.HORIZONTAL, false)

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
        setupPieChart()

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



    private fun setupPieChart() {
        // 파이 차트에 들어갈 데이터 설정
        val entries = listOf(
            PieEntry(38f, "놀람"),
            PieEntry(18f, "두려움"),
            PieEntry(24f, "슬픔"),
            PieEntry(12f, "분노"),
            PieEntry(8f, "혐오")
        )

        val dataSet = PieDataSet(entries, "감정 비율")
        dataSet.colors = listOf(
            Color.parseColor("#4CAF50"), // 놀람 (녹색)
            Color.parseColor("#FF9800"), // 두려움 (주황색)
            Color.parseColor("#03A9F4"), // 슬픔 (파란색)
            Color.parseColor("#F44336"), // 분노 (빨간색)
            Color.parseColor("#9C27B0")  // 혐오 (보라색)
        )

        val pieData = PieData(dataSet)
        pieData.setValueTextSize(12f)
        pieData.setValueTextColor(Color.WHITE)

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
    }


    // API 또는 데이터베이스에서 카드 데이터를 가져오는 메소드 예시
    private fun fetchCardData(): List<CardItem> {
        // 실제 데이터 소스에서 가져온다고 가정
        return listOf(
            CardItem("흑백성", R.drawable.ic_planet_a),
            CardItem("걱정성", R.drawable.ic_planet_a),
            CardItem("과장성", R.drawable.ic_planet_a)
            // 더 많은 아이템을 가져올 수 있습니다.
        ).take(2) // 원하는 개수만큼 가져오기
    }

}
