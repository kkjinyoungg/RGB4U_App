package com.example.rgb4u_app.ui.activity.distortiontype

import android.content.Intent
import android.os.Bundle
import android.view.View
import android.widget.Button
import android.widget.ImageButton
import android.widget.ImageView
import android.widget.SeekBar
import android.widget.TextView
import androidx.appcompat.app.AppCompatActivity
import com.example.rgb4u_app.R
import com.example.rgb4u_app.ui.activity.summary.SummaryChangedDayActivity
import java.text.SimpleDateFormat
import java.util.Date
import java.util.Locale

class EmotionReselectActivity : AppCompatActivity() {

    private lateinit var dynamicTextView: TextView
    private lateinit var seekBar: SeekBar
    private lateinit var buttonNext: Button
    private lateinit var squareView: ImageView

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_emotion_reselect)

        // 뷰 초기화
        dynamicTextView = findViewById(R.id.dynamicTextView)
        seekBar = findViewById(R.id.seekBar)
        squareView = findViewById(R.id.squareView) // ImageView 초기화
        buttonNext = findViewById(R.id.buttonNext)

        // 초기 상태에서 squareView에 디폴트 이미지 설정
        squareView.setImageResource(R.drawable.img_emotion_0)

        // SeekBar의 진행 상태에 따라 dynamicTextView의 텍스트 변경
        seekBar.setOnSeekBarChangeListener(object : SeekBar.OnSeekBarChangeListener {
            override fun onProgressChanged(seekBar: SeekBar?, progress: Int, fromUser: Boolean) {
                dynamicTextView.text = when (progress) {
                    0 -> {
                        squareView.setImageResource(R.drawable.img_emotion_0) // 이미지 리소스 설정
                        "매우 심하지 않았어"
                    }
                    1 -> {
                        squareView.setImageResource(R.drawable.img_emotion_1) // 이미지 리소스 설정
                        "심하지 않았어"
                    }
                    2 -> {
                        squareView.setImageResource(R.drawable.img_emotion_2) // 이미지 리소스 설정
                        "보통이었어"
                    }
                    3 -> {
                        squareView.setImageResource(R.drawable.img_emotion_3) // 이미지 리소스 설정
                        "심했어"
                    }
                    4 -> {
                        squareView.setImageResource(R.drawable.img_emotion_4) // 이미지 리소스 설정
                        "매우 심했어"
                    }
                    else -> ""
                }
            }

            override fun onStartTrackingTouch(seekBar: SeekBar?) {}

            override fun onStopTrackingTouch(seekBar: SeekBar?) {}
        })

        // toolbar의 button_write_action2 숨기기
        val toolbar = findViewById<androidx.appcompat.widget.Toolbar>(R.id.toolbar)
        val buttonWriteAction2 = toolbar.findViewById<ImageButton>(R.id.button_write_action2)
        buttonWriteAction2.visibility = View.GONE

        // toolbar의 날짜를 보여주는 텍스트뷰 가져오기
        val toolbarTitle = toolbar.findViewById<TextView>(R.id.toolbar_write_title)

        // 현재 날짜 가져와 설정하기
        val dateFormat = SimpleDateFormat("MM월 dd일 E요일", Locale("ko", "KR"))
        val currentDate = dateFormat.format(Date())
        toolbarTitle.text = currentDate

        // "다음" 버튼 클릭 시 다음 액티비티로 이동
        buttonNext.setOnClickListener {
            val intent = Intent(this, SummaryChangedDayActivity::class.java) // SummaryChangedDayActivity로 이동
            startActivity(intent)
        }
    }
}