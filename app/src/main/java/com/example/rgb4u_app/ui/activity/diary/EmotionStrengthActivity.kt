package com.example.rgb4u_app.ui.activity.diary

import android.content.Intent
import android.os.Bundle
import android.widget.SeekBar
import android.widget.TextView
import androidx.appcompat.app.AppCompatActivity
import com.example.rgb4u_app.R
import com.example.rgb4u_app.ui.fragment.MyRecordFragment
import com.example.rgb4u_appclass.DiaryViewModel
import androidx.lifecycle.ViewModelProvider
import androidx.lifecycle.ViewModelStoreOwner
import com.example.rgb4u_app.MyApplication

class EmotionStrengthActivity : AppCompatActivity(), MyRecordFragment.NavigationListener {

    private lateinit var myRecordFragment: MyRecordFragment
    private lateinit var diaryViewModel: DiaryViewModel // ViewModel 선언
    private lateinit var seekBar: SeekBar
    private lateinit var dynamicTextView: TextView

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

        // Initialize fragment
        myRecordFragment = supportFragmentManager.findFragmentById(R.id.myrecordFragment) as MyRecordFragment

        // Set text and icon for fragment
        myRecordFragment.setQuestionText("그때 부정적인 감정이 \n얼마나 심했는지 알려주세요", "")
        myRecordFragment.showIconForStep(3)

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
                myRecordFragment.setButtonNextEnabled(progress in 0..4)

                // ViewModel에 감정 정도 값, 텍스트 저장
                diaryViewModel.emotionDegree.postValue(progress) // 감정 정도 저장
                diaryViewModel.emotionString.postValue(dynamicTextView.text.toString()) // 감정 텍스트 저장
                //diaryViewModel.emotionDegree.value = progress
                //diaryViewModel.emotionString.value = dynamicTextView.text.toString()
            }

            override fun onStartTrackingTouch(seekBar: SeekBar?) {}
            override fun onStopTrackingTouch(seekBar: SeekBar?) {}
        })

        // Initialize the Next button state based on the initial SeekBar progress
        myRecordFragment.setButtonNextEnabled(seekBar.progress in 1..4)

        // Disable and hide the helpButton
        myRecordFragment.setHelpButtonEnabled(false)
        myRecordFragment.setHelpButtonVisibility(false)
    }

    override fun onNextButtonClicked() {
        // EmotionStrengthActivity로 넘어갈 때 ViewModel에서 값 가져오기
        //val progress = seekBar.progress
        //val emotionString = dynamicTextView.text.toString()

        //val situationText = intent.getStringExtra("EXTRA_SITUATION_TEXT")
        //val thoughtText = intent.getStringExtra("EXTRA_THOUGHT_TEXT")

        val intent = Intent(this, EmotionSelectActivity::class.java)
        //intent.putExtra("EXTRA_SITUATION_TEXT", situationText)
        //intent.putExtra("EXTRA_THOUGHT_TEXT", thoughtText)
        startActivity(intent)
    }



    override fun onBackButtonClicked() {
        val intent = Intent(this, ThinkWriteActivity::class.java)
        startActivity(intent)
        finish()
    }
}
