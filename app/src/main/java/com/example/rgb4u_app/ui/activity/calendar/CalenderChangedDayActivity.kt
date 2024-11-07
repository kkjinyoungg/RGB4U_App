package com.example.rgb4u_app.ui.activity.calendar

import android.content.Intent
import android.os.Bundle
import android.view.View
import android.widget.ImageButton
import android.widget.ImageView
import android.widget.TextView
import androidx.activity.enableEdgeToEdge
import androidx.appcompat.app.AppCompatActivity
import androidx.appcompat.widget.Toolbar
import com.example.rgb4u_app.R

class CalenderChangedDayActivity : AppCompatActivity() {

    // 뷰 변수 선언
    private lateinit var iconImageView1: ImageView
    private lateinit var situationTextView1: TextView
    private lateinit var exampleText1: TextView

    private lateinit var iconImageView2: ImageView
    private lateinit var situationTextView2: TextView
    private lateinit var exampleText2: TextView

    private lateinit var iconImageView3: ImageView
    private lateinit var situationTextView3: TextView
    private lateinit var exampleText3: TextView

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        setContentView(R.layout.activity_calender_changed_day)

        // 뷰 초기화
        iconImageView1 = findViewById(R.id.iconImageView1)
        situationTextView1 = findViewById(R.id.situationTextView1)
        exampleText1 = findViewById(R.id.exampleText1)

        iconImageView2 = findViewById(R.id.iconImageView2)
        situationTextView2 = findViewById(R.id.situationTextView2)
        exampleText2 = findViewById(R.id.exampleText2)

        iconImageView3 = findViewById(R.id.iconImageView3)
        situationTextView3 = findViewById(R.id.situationTextView3)
        exampleText3 = findViewById(R.id.exampleText3)


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

        // 임시 데이터 설정
        iconImageView1.setImageResource(R.drawable.ic_planet_a)  // 예시 이미지 설정
        situationTextView1.text = "엄마는 왜 맨날 나를 괴롭히고 내가 자는 걸 싫어하고 내가 행복한 꼴을 못 보실까??"
        exampleText1.text = "예시 1: 임시 예시 텍스트 1"

        iconImageView2.setImageResource(R.drawable.ic_planet_a)  // 예시 이미지 설정
        situationTextView2.text = "엄마는 왜 맨날 나를 괴롭히고 내가 자는 걸 싫어하고 내가 행복한 꼴을 못 보실까??"
        exampleText2.text = "예시 2: 임시 예시 텍스트 2"

        iconImageView3.setImageResource(R.drawable.ic_planet_a)  // 예시 이미지 설정
        situationTextView3.text = "엄마는 왜 맨날 나를 괴롭히고 내가 자는 걸 싫어하고 내가 행복한 꼴을 못 보실까??"
        exampleText3.text = "예시 3: 임시 예시 텍스트 3"


    }
}