package com.example.rgb4u_app

import android.content.Intent
import android.os.Bundle
import android.widget.Button
import androidx.appcompat.app.AppCompatActivity

class MainActivity : AppCompatActivity() {
    private lateinit var btnWriteDiary: Button

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)

        // "일기 쓰기" 버튼 찾기
        btnWriteDiary = findViewById(R.id.button_write_diary)

        // "일기 쓰기" 버튼 클릭 리스너 설정
        btnWriteDiary.setOnClickListener {
            // 일기 작성 화면으로 이동하는 코드 작성
            val intent = Intent(this, DiaryWriteActivity::class.java)
            startActivity(intent)
        }
    }
}
