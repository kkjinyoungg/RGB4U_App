package com.example.rgb4u_app.ui.activity.calendar

import android.content.Intent
import android.os.Bundle
import android.view.View
import android.widget.ImageButton
import android.widget.TextView
import androidx.activity.enableEdgeToEdge
import androidx.appcompat.app.AppCompatActivity
import androidx.appcompat.widget.Toolbar
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.example.rgb4u_app.R

class CalenderChangedDayActivity : AppCompatActivity() {

    private lateinit var recyclerView: RecyclerView

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        setContentView(R.layout.activity_calender_changed_day)

        // Toolbar 설정
        val toolbar: Toolbar = findViewById(R.id.toolbar)
        setSupportActionBar(toolbar)

        // 기본 뒤로가기 버튼, 앱 이름 숨기기
        getSupportActionBar()?.setDisplayHomeAsUpEnabled(false)
        supportActionBar?.setDisplayShowTitleEnabled(false)

        // 툴바의 제목을 "달라진 하루"로 설정
        val toolbarTitle: TextView = findViewById(R.id.toolbar_write_title)
        toolbarTitle.text = "달라진 하루"


        // button_write_action2 버튼 숨기기
        val buttonWriteAction2: ImageButton = findViewById(R.id.button_write_action2)
        buttonWriteAction2.visibility = View.GONE

        // button_write_action1 클릭 리스너 추가
        val buttonWriteAction1: ImageButton = findViewById(R.id.button_write_action1)
        buttonWriteAction1.setOnClickListener {
            // CalendarHomeActivity로 이동
            val intent = Intent(this, CalendarHomeActivity::class.java)
            startActivity(intent)
            finish() // 현재 Activity를 종료
        }

        // thoughtDetailButton 클릭 리스너 추가
        val thoughtDetailButton: ImageButton = findViewById(R.id.thoughtDetailButton)
        thoughtDetailButton.setOnClickListener {
            // ChangeThinkThisActivity로 이동
            val intent = Intent(this, ChangeThinkThisActivity::class.java)
            startActivity(intent)
            finish() // 현재 Activity를 종료
        }

        recyclerView = findViewById(R.id.summaryThinkRecyclerView)
        recyclerView.layoutManager = LinearLayoutManager(this)

        // 예시 데이터
        val situations = listOf(
            ChangeDaySituation(R.drawable.ic_planet_a, "엄마는 왜 맨날 나를 괴롭히고 내가 자는 걸 싫어하고 내가 행복한 꼴을 못 보실까?", "예시 텍스트 1"),
            ChangeDaySituation(R.drawable.ic_planet_a, "엄마는 왜 맨날 나를 괴롭히고 내가 자는 걸 싫어하고 내가 행복한 꼴을 못 보실까?", "예시 텍스트 2"),
            ChangeDaySituation(R.drawable.ic_planet_a, "엄마는 왜 맨날 나를 괴롭히고 내가 자는 걸 싫어하고 내가 행복한 꼴을 못 보실까?", "예시 텍스트 3")
        )

        val adapter = ChangeDayThinkAdapter(situations)
        recyclerView.adapter = adapter

    }
}