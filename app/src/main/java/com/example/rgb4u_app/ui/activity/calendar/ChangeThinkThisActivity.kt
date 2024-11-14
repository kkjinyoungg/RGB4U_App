package com.example.rgb4u_app.ui.activity.calendar

import ChangeThinkThisAdapter
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

class ChangeThinkThisActivity : AppCompatActivity() {
    private lateinit var recyclerView: RecyclerView
    private lateinit var adapter: ChangeThinkThisAdapter

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        setContentView(R.layout.activity_change_think_this)

        // Toolbar 설정
        val toolbar: Toolbar = findViewById(R.id.toolbar)
        setSupportActionBar(toolbar)

        // 기본 뒤로가기 버튼, 앱 이름 숨기기
        supportActionBar?.setDisplayHomeAsUpEnabled(false)
        supportActionBar?.setDisplayShowTitleEnabled(false)

        // 툴바의 제목을 "이렇게 생각을 바꿔봤어요"로 설정
        val toolbarTitle: TextView = findViewById(R.id.toolbar_write_title)
        toolbarTitle.text = "이렇게 생각을 바꿔봤어요"

        // button_write_action2 버튼 숨기기
        val buttonWriteAction2: ImageButton = findViewById(R.id.button_write_action2)
        buttonWriteAction2.visibility = View.GONE

        // button_write_action1 클릭 리스너 추가
        val buttonWriteAction1: ImageButton = findViewById(R.id.button_write_action1)
        buttonWriteAction1.setOnClickListener {
            // CalenderChangedDayActivity 이동
            val intent = Intent(this, CalenderChangedDayActivity::class.java)
            startActivity(intent)
            finish() // 현재 Activity를 종료
        }

        // RecyclerView 초기화
        recyclerView = findViewById(R.id.ChangeThinkRecyclerView)
        recyclerView.layoutManager = LinearLayoutManager(this)

        // myDataList 초기화 (수정된 MyData 클래스를 반영)
        val myDataList = listOf(
            MyData(
                imageResId = R.drawable.ic_planet_a,
                name = "흑백성",
                description = "흑백성에 대한 설명",
                thinkMessages = listOf("생각 1A", "생각 2A", "생각 3A"),  // 여러 개의 Think 메시지
                thinkExtraMessages = listOf("추가 설명 1A", "추가 설명 2A", "추가 설명 3A"),  // 여러 개의 추가 Think 메시지
                changeMessages = listOf("변화 1A", "변화 2A", "변화 3A"),  // 여러 개의 Change 메시지
                changeExtraMessages = listOf("추가 변화 설명 1A", "추가 변화 설명 2A", "추가 변화 설명 3A") // 여러 개의 추가 Change 메시지
            ),
            MyData(
                imageResId = R.drawable.ic_planet_a,
                name = "과장성",
                description = "과장성에 대한 설명",
                thinkMessages = listOf("생각 1B", "생각 2B", "생각 3B"),
                thinkExtraMessages = listOf("추가 설명 1B", "추가 설명 2B", "추가 설명 3B"),
                changeMessages = listOf("변화 1B", "변화 2B", "변화 3B"),
                changeExtraMessages = listOf("추가 변화 설명 1B", "추가 변화 설명 2B", "추가 변화 설명 3B")
            ),
            MyData(
                imageResId = R.drawable.ic_planet_a,
                name = "궁예성",
                description = "궁예성에 대한 설명",
                thinkMessages = listOf("생각 1C", "생각 2C", "생각 3C"),
                thinkExtraMessages = listOf("추가 설명 1C", "추가 설명 2C", "추가 설명 3C"),
                changeMessages = listOf("변화 1C", "변화 2C", "변화 3C"),
                changeExtraMessages = listOf("추가 변화 설명 1C", "추가 변화 설명 2C", "추가 변화 설명 3C")
            )
        )

        // 어댑터 설정
        adapter = ChangeThinkThisAdapter(myDataList, supportFragmentManager)
        recyclerView.adapter = adapter
    }
}
