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

    private lateinit var toolbarDate: String
    private lateinit var date: String

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_summary_changed_day)

        // 인텐트에서 데이터 받기
        val intent = intent
        toolbarDate = intent.getStringExtra("ToolbarDate") ?: ""
        date = intent.getStringExtra("Date") ?: ""

        // Toolbar 설정
        val toolbar: Toolbar = findViewById(R.id.toolbar)
        setSupportActionBar(toolbar)
        toolbar.findViewById<TextView>(R.id.toolbar_write_title).text = toolbarDate

        supportActionBar?.setDisplayHomeAsUpEnabled(false)
        supportActionBar?.setDisplayShowTitleEnabled(false)

        // button_write_action2 버튼 숨기기
        val buttonWriteAction2: ImageButton = findViewById(R.id.button_write_action2)
        buttonWriteAction2.visibility = View.GONE

        // ChangedDayFragment 로드
        if (savedInstanceState == null) {
            val fragment = ChangedDayFragment.newInstance(toolbarDate, date) // date도 전달
            supportFragmentManager.beginTransaction()
                .replace(R.id.fragment_container, fragment)
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
