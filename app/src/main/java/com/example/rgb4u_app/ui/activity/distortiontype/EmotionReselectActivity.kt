package com.example.rgb4u_app.ui.activity.distortiontype

import android.content.Intent
import android.os.Bundle
import android.view.View
import android.widget.ImageView
import android.widget.LinearLayout
import android.widget.SeekBar
import android.widget.TextView
import androidx.appcompat.app.AppCompatActivity
import androidx.fragment.app.FragmentManager
import androidx.fragment.app.commit
import com.example.rgb4u_app.R
import com.example.rgb4u_app.ui.activity.MainActivity
import com.example.rgb4u_app.ui.fragment.MyEmotionFragment

class EmotionReselectActivity : AppCompatActivity(), MyEmotionFragment.NavigationListener {

    private lateinit var seekBar: SeekBar
    private lateinit var dynamicTextView: TextView
    private lateinit var squareView: ImageView
    private lateinit var myEmotionFragment: MyEmotionFragment

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_emotion_reselect)

        // FragmentManager를 통해 MyEmotionFragment를 추가
        val fragmentManager: FragmentManager = supportFragmentManager
        fragmentManager.commit {
            replace(R.id.fragment_container, MyEmotionFragment())
        }

        // 프래그먼트가 추가된 후 toolbarAction2 숨기기
        fragmentManager.addOnBackStackChangedListener {
            // 현재 액티비티에 있는 MyEmotionFragment를 찾아서 toolbarAction2 숨기기
            val fragment = supportFragmentManager.findFragmentById(R.id.fragment_container) as? MyEmotionFragment
            fragment?.hideToolbarAction2()  // hideToolbarAction2 메서드 호출
        }

        // 뷰 초기화
        seekBar = findViewById(R.id.seekBar)
        dynamicTextView = findViewById(R.id.dynamicTextView)
        squareView = findViewById(R.id.squareView)
        myEmotionFragment = supportFragmentManager.findFragmentById(R.id.fragment_container) as MyEmotionFragment

        // Set text and icon for fragment
        myEmotionFragment.setQuestionText(
            "지금은 부정적인 감정이 \n얼마나 심한지 한 번 더 알려주세요",
            "생각을 살펴보니 감정이 어떻게 바뀌었나요?"
        )

        // SeekBar 리스너 설정
        seekBar.setOnSeekBarChangeListener(object : SeekBar.OnSeekBarChangeListener {
            override fun onProgressChanged(seekBar: SeekBar?, progress: Int, fromUser: Boolean) {
                // SeekBar progress에 따라 텍스트 설정
                dynamicTextView.text = when (progress) {
                    0 -> "매우 심하지 않았어"
                    1 -> "심하지 않았어"
                    2 -> "보통이었어"
                    3 -> "심했어"
                    4 -> "매우 심했어"
                    else -> ""
                }

                // 버튼 활성화 상태 설정 (progress가 0~4 사이에 있을 때 활성화)
                myEmotionFragment.setButtonNextEnabled(progress in 1..4)

                // 감정 단계에 따른 이미지 변경
                squareView.setImageResource(getImageResId(progress))
            }

            override fun onStartTrackingTouch(seekBar: SeekBar?) {}
            override fun onStopTrackingTouch(seekBar: SeekBar?) {}
        })

        // 초기 SeekBar 상태에 따라 Next 버튼 상태 설정
        myEmotionFragment.setButtonNextEnabled(seekBar.progress in 1..4)

        // 프래그먼트가 추가된 후 navigationLayout 숨기기
        fragmentManager.addOnBackStackChangedListener {
            val fragment = supportFragmentManager.findFragmentById(R.id.fragment_container) as? MyEmotionFragment
            fragment?.let {
                val navigationLayout = fragment.view?.findViewById<LinearLayout>(R.id.navigationLayout)
                navigationLayout?.visibility = View.GONE
            }
        }
    }

    // 감정 단계에 따른 이미지 리소스 ID 반환
    private fun getImageResId(progress: Int): Int {
        return when (progress) {
            0 -> R.drawable.img_emotion_0
            1 -> R.drawable.img_emotion_1
            2 -> R.drawable.img_emotion_2
            3 -> R.drawable.img_emotion_3
            4 -> R.drawable.img_emotion_4
            else -> R.drawable.img_emotion_0 // 기본 이미지 처리
        }
    }

    // MyEmotionFragment에서 "다음" 버튼 클릭 시 호출되는 메서드
    override fun onNextButtonClicked() {
        val intent = Intent(this, MainActivity::class.java) // 추후 MainActivity 대신 올바른 액티비티로 변경
        startActivity(intent)
    }

    override fun onToolbarAction1Clicked() {
        // Toolbar의 첫 번째 버튼 클릭 처리
    }

    override fun onToolbarAction2Clicked() {
        // Toolbar의 두 번째 버튼을 숨기기

    }

}
