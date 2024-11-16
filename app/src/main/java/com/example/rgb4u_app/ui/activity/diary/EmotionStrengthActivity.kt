package com.example.rgb4u_app.ui.activity.diary

import android.content.Intent
import android.os.Bundle
import android.widget.ImageView
import android.widget.SeekBar
import android.widget.TextView
import androidx.appcompat.app.AppCompatActivity
import com.example.rgb4u_app.MyApplication
import com.example.rgb4u_app.R
import com.example.rgb4u_app.ui.activity.home.MainActivity
import com.example.rgb4u_app.ui.fragment.MyEmotionFragment
import com.example.rgb4u_app.ui.fragment.TemporarySaveDialogFragment
import com.example.rgb4u_appclass.DiaryViewModel

class EmotionStrengthActivity : AppCompatActivity(), MyEmotionFragment.NavigationListener {

    private lateinit var myEmotionFragment: MyEmotionFragment
    private lateinit var diaryViewModel: DiaryViewModel // ViewModel 선언
    private lateinit var seekBar: SeekBar
    private lateinit var dynamicTextView: TextView
    private lateinit var squareView: ImageView

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_emotion_strength)

        // Application에서 ViewModel 가져오기
        diaryViewModel = (application as MyApplication).diaryViewModel

        // ViewModel 관찰자 추가
        diaryViewModel.emotionDegree.observe(this) { progress ->
            seekBar.progress = progress ?: 0
        }

        diaryViewModel.emotionString.observe(this) { emotionText ->
            dynamicTextView.text = emotionText ?: ""
        }

        // Initialize views
        seekBar = findViewById(R.id.seekBar)
        dynamicTextView = findViewById(R.id.dynamicTextView)
        squareView = findViewById(R.id.squareView)

        // Initialize fragment
        myEmotionFragment =
            supportFragmentManager.findFragmentById(R.id.myemotionFragment) as MyEmotionFragment

        // Set text and icon for fragment
        myEmotionFragment.setQuestionText("그때 부정적인 감정이 \n얼마나 심했는지 알려주세요", "")
        myEmotionFragment.showIconForStep(3)

        // 버튼 클릭 리스너 설정
        myEmotionFragment.setToolbarButtonListeners(
            backAction = { onToolbarAction1Clicked() }, // 뒤로 가기 버튼 동작을 메서드로 연결
            exitAction = { onToolbarAction2Clicked() } // 나가기 버튼 동작
        )

        // Set SeekBar listener
        seekBar.setOnSeekBarChangeListener(object : SeekBar.OnSeekBarChangeListener {
            override fun onProgressChanged(seekBar: SeekBar?, progress: Int, fromUser: Boolean) {
                dynamicTextView.text = when (progress) {
                    0 -> "매우 심하지 않았어"
                    1 -> "심하지 않았어"
                    2 -> "보통이었어"
                    3 -> "심했어"
                    4 -> "매우 심했어"
                    else -> ""
                }
                // Enable or disable the next button based on the SeekBar progress
                myEmotionFragment.setButtonNextEnabled(progress in 0..4)

                // ViewModel에 감정 정도 값, 텍스트 저장
                diaryViewModel.emotionDegree.postValue(progress) // 감정 정도 저장
                diaryViewModel.emotionString.postValue(dynamicTextView.text.toString()) // 감정 텍스트 저장

                // 감정 단계에 따라 이미지 변경
                squareView.setImageResource(getImageResId(progress))

            }

            override fun onStartTrackingTouch(seekBar: SeekBar?) {}
            override fun onStopTrackingTouch(seekBar: SeekBar?) {}
        })

        // Initialize the Next button state based on the initial SeekBar progress
        myEmotionFragment.setButtonNextEnabled(seekBar.progress in 1..4)
    }

    // 감정에 따라 이미지 리소스 ID를 반환하는 함수
    private fun getImageResId(progress: Int): Int {
        return when (progress) {
            0 -> R.drawable.img_emotion_0
            1 -> R.drawable.img_emotion_1
            2 -> R.drawable.img_emotion_2
            3 -> R.drawable.img_emotion_3
            4 -> R.drawable.img_emotion_4
            else -> R.drawable.img_emotion_0 // 기본 이미지 (예외 처리)
        }
    }

    override fun onNextButtonClicked() {
        // 현재 SeekBar에서 감정 정도 가져오기
        val progress = seekBar.progress
        // 현재 동적 텍스트 가져오기
        val emotionString = dynamicTextView.text.toString()

        // 다음 액티비티로 전환
        val intent = Intent(this, EmotionSelectActivity::class.java)
        startActivity(intent)

    }

    override fun onToolbarAction1Clicked() {
        val intent = Intent(this, ThinkWriteActivity::class.java)
        startActivity(intent)
        finish()
    }

    override fun onToolbarAction2Clicked() {
        // TemporarySaveDialogFragment 인스턴스 생성
        val dialog = TemporarySaveDialogFragment()

        // dialog의 리스너 설정
        dialog.listener = object : TemporarySaveDialogFragment.OnButtonClickListener {
            override fun onTemporarySave() {

                // SeekBar의 현재 진행 상태와 동적 텍스트를 ViewModel에 저장
                diaryViewModel.emotionDegree.postValue(seekBar.progress) // 감정 정도 저장
                diaryViewModel.emotionString.postValue(dynamicTextView.text.toString()) // 감정 텍스트 저장

                val intent = Intent(this@EmotionStrengthActivity, MainActivity::class.java)
                startActivity(intent)
                finish()
            }

            override fun onDelete() {
                // 삭제 동작: 아무것도 저장하지 않고 MainActivity로 이동
                val intent = Intent(this@EmotionStrengthActivity, MainActivity::class.java)
                startActivity(intent)
                finish()
            }
        }

        // 팝업 표시
        dialog.show(supportFragmentManager, "TemporarySaveDialog")
    }

}
