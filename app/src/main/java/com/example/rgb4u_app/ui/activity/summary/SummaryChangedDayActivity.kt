package com.example.rgb4u_app.ui.activity.summary

import android.content.Intent
import android.os.Bundle
import android.view.View
import android.widget.ImageButton
import android.widget.TextView
import androidx.appcompat.app.AppCompatActivity
import androidx.appcompat.widget.Toolbar
import com.example.rgb4u_app.R
import com.example.rgb4u_app.ui.activity.distortiontype.EmotionReselectActivity
import com.example.rgb4u_app.ui.fragment.ChangedDayFragment
import java.text.SimpleDateFormat
import java.util.Date
import java.util.Locale

class SummaryChangedDayActivity : AppCompatActivity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_summary_changed_day)

        // Toolbar 설정
        val toolbar: Toolbar = findViewById(R.id.toolbar)
        setSupportActionBar(toolbar)

        supportActionBar?.setDisplayHomeAsUpEnabled(false)
        supportActionBar?.setDisplayShowTitleEnabled(false)

        // button_write_action2 버튼 숨기기
        val buttonWriteAction2: ImageButton = findViewById(R.id.button_write_action2)
        buttonWriteAction2.visibility = View.GONE

        // 기기 날짜를 "MM월 dd일 E요일" 형태로 포맷하여 설정
        val currentDate = SimpleDateFormat("MM월 dd일 E요일", Locale.getDefault()).format(Date())
        findViewById<TextView>(R.id.toolbar_write_title).text = currentDate

        // ChangedDayFragment 로드
        if (savedInstanceState == null) {
            supportFragmentManager.beginTransaction()
                .replace(R.id.fragment_container, ChangedDayFragment.newInstance(currentDate))
                .commit()
        }

        // button_write_action1 클릭 리스너 추가
        val buttonWriteAction1: ImageButton = findViewById(R.id.button_write_action1)
        buttonWriteAction1.setOnClickListener {
            val intent = Intent(this, EmotionReselectActivity::class.java)
            startActivity(intent)
            finish()
        }
    }
}

