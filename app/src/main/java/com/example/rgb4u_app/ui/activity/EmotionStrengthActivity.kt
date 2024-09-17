package com.example.rgb4u_app.ui.activity

import android.content.Intent
import android.os.Bundle
import android.text.Editable
import android.text.TextWatcher
import android.widget.Button
import android.widget.EditText
import android.widget.TextView
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import com.example.rgb4u_app.R
import com.example.rgb4u_app.ui.fragment.MyRecordFragment

class EmotionStrengthActivity : AppCompatActivity(), MyRecordFragment.NavigationListener {

    private lateinit var myRecordFragment: MyRecordFragment

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_write_diary)

        // 프래그먼트 초기화
        myRecordFragment = supportFragmentManager.findFragmentById(R.id.myrecordFragment) as MyRecordFragment

        // 프래그먼트에서 요구하는 인터페이스 구현 확인
        myRecordFragment.setQuestionText("그때 부정적인 감정이 얼마나 심했는지 알려주세요", "")

        // 특정 단계의 이미지만 보이도록 설정 (예: 2단계 "생각 적기")
        myRecordFragment.showIconForStep(3)




        // 버튼 클릭 시 onNextButtonClicked 호출
        buttonNext?.setOnClickListener {
            onNextButtonClicked()
        }
    }

    override fun onNextButtonClicked() {
        // "Next" 버튼 클릭 시 ThinkWriteActivity로 이동
        val intent = Intent(this, EmotionSelectActivity::class.java)
        startActivity(intent)
    }

    override fun onBackButtonClicked() {
        // "Back" 버튼 클릭 시 MainActivity로 이동
        val intent = Intent(this, ThinkWriteActivity::class.java)
        startActivity(intent)
        finish() // 현재 Activity를 종료하여 뒤로가기 시 MainActivity로 돌아가도록 합니다.
    }
}
