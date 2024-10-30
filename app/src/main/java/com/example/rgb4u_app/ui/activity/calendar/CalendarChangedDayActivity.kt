package com.example.rgb4u_app.ui.activity.calendar

import android.content.Intent
import android.os.Bundle
import android.view.MenuItem
import android.view.View
import android.widget.ImageButton
import android.widget.TextView
import androidx.appcompat.app.AppCompatActivity
import androidx.appcompat.widget.Toolbar
import com.example.rgb4u_app.R
import com.example.rgb4u_app.ui.fragment.ChangedDayFragment

class CalendarChangedDayActivity : AppCompatActivity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_calendar_changed_day)

        // Toolbar 설정
        val toolbar: Toolbar = findViewById(R.id.toolbar)
        setSupportActionBar(toolbar)

        // 기본 뒤로가기 버튼, 앱 이름 숨기기
        getSupportActionBar()?.setDisplayHomeAsUpEnabled(false)
        supportActionBar?.setDisplayShowTitleEnabled(false)

        // button_write_action2 버튼 숨기기
        val buttonWriteAction2: ImageButton = findViewById(R.id.button_write_action2)
        buttonWriteAction2.visibility = View.GONE

        // 이전 화면에서 선택된 날짜 받아오기
        val selectedDate = intent.getStringExtra("SELECTED_DATE") ?: "" // 키 수정
        findViewById<TextView>(R.id.toolbar_write_title).text = selectedDate

        // ChangedDayFragment 로드
        if (savedInstanceState == null) {
            supportFragmentManager.beginTransaction()
                .replace(R.id.fragment_container, ChangedDayFragment.newInstance(selectedDate))
                .commit()
        }

        // button_write_action1 클릭 리스너 추가
        val buttonWriteAction1: ImageButton = findViewById(R.id.button_write_action1)
        buttonWriteAction1.setOnClickListener {
            // CalendarHomeActivity로 이동
            val intent = Intent(this, CalendarHomeActivity::class.java)
            startActivity(intent)
            finish() // 현재 Activity를 종료
        }
    }

    override fun onOptionsItemSelected(item: MenuItem): Boolean {
        return when (item.itemId) {
            android.R.id.home -> {
                onBackPressed()
                true
            }
            else -> super.onOptionsItemSelected(item)
        }
    }
}

