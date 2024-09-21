package com.example.rgb4u_app.ui.activity.diary

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

class ThinkWriteActivity : AppCompatActivity(), MyRecordFragment.NavigationListener {

    private lateinit var myRecordFragment: MyRecordFragment

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_think_write)

        // 프래그먼트 초기화
        myRecordFragment = supportFragmentManager.findFragmentById(R.id.myrecordFragment) as MyRecordFragment

        // 프래그먼트에서 요구하는 인터페이스 구현 확인
        myRecordFragment.setQuestionText("그때 떠오른 생각을 들려주세요", "그 상황에서 어떤 내용이 머릿속을 스쳐갔나요?")

        // 특정 단계의 이미지만 보이도록 설정 (예: 2단계 "생각 적기")
        myRecordFragment.showIconForStep(2)

        // 텍스트 필드와 글자 수 카운터 초기화
        val inputField = findViewById<EditText>(R.id.inputField)
        val charCountTextView = findViewById<TextView>(R.id.charCountTextView)
        val buttonNext = myRecordFragment.view?.findViewById<Button>(R.id.buttonNext)

        // 초기 상태로 버튼 비활성화
        buttonNext?.isEnabled = false

        // 텍스트 필드 변화에 따라 버튼 활성화/비활성화 및 글자 수 업데이트
        inputField.addTextChangedListener(object : TextWatcher {
            override fun afterTextChanged(s: Editable?) {
                s?.let {
                    val charCount = s.length
                    val byteCount = s.toString().toByteArray(Charsets.UTF_8).size

                    // 텍스트가 300바이트를 초과할 경우
                    if (byteCount > 300) {
                        inputField.setText(s.toString().take(s.length - 1)) // 마지막 문자 제거
                        inputField.setSelection(inputField.text.length) // 커서 위치를 끝으로 이동
                        Toast.makeText(this@ThinkWriteActivity, "150자 이하로 작성해주세요", Toast.LENGTH_SHORT).show()
                    }

                    // 글자 수가 1자 이상일 때만 버튼 활성화
                    val isTextValid = charCount in 1..150
                    buttonNext?.isEnabled = isTextValid
                    myRecordFragment.setButtonNextEnabled(isTextValid)
                    myRecordFragment.setButtonNextVisibility(isTextValid)

                    // 글자 수 업데이트
                    charCountTextView.text = "$charCount/150"
                }
            }

            override fun beforeTextChanged(s: CharSequence?, start: Int, count: Int, after: Int) {}
            override fun onTextChanged(s: CharSequence?, start: Int, before: Int, count: Int) {}
        })

        // 버튼 클릭 시 onNextButtonClicked 호출
        buttonNext?.setOnClickListener {
            onNextButtonClicked()
        }
    }

    override fun onNextButtonClicked() {
        val inputText = findViewById<EditText>(R.id.inputField).text.toString()
        val situationText = intent.getStringExtra("EXTRA_SITUATION_TEXT")

        // EmotionStrengthActivity로 데이터를 전달하면서 이동
        val intent = Intent(this, EmotionStrengthActivity::class.java)
        intent.putExtra("EXTRA_SITUATION_TEXT", situationText)  // DiaryWriteActivity에서 전달받은 데이터
        intent.putExtra("EXTRA_THOUGHT_TEXT", inputText)  // thoughtText로 전달할 데이터
        startActivity(intent)
    }




    override fun onBackButtonClicked() {
        // "Back" 버튼 클릭 시 DiaryWriteActivity로 이동
        val intent = Intent(this, DiaryWriteActivity::class.java)
        startActivity(intent)
        finish()
    }
}
