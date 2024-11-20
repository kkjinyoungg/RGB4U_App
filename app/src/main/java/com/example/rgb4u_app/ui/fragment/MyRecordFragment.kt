package com.example.rgb4u_app.ui.fragment

import android.content.Context
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Button
import android.widget.ImageButton
import android.widget.ImageView
import android.widget.TextView
import androidx.core.content.ContextCompat
import androidx.core.content.res.ResourcesCompat
import androidx.fragment.app.Fragment
import com.example.rgb4u_app.R
import java.text.SimpleDateFormat
import java.util.Calendar
import java.util.Locale

class MyRecordFragment : Fragment() {

    interface NavigationListener {
        fun onNextButtonClicked()
        fun onToolbarAction1Clicked()  // 첫 번째 버튼 클릭
        fun onToolbarAction2Clicked()  // 두 번째 버튼 클릭
    }

    private var listener: NavigationListener? = null

    private lateinit var questionment: TextView
    private lateinit var subQuestionment: TextView
    private lateinit var imgCharacter : ImageView

    private lateinit var nextButton: Button

    private lateinit var iconSituation: ImageView
    private lateinit var textSituation: TextView
    private lateinit var iconThought: ImageView
    private lateinit var iconEmotionStrength: ImageView
    private lateinit var iconEmotionType: ImageView

    private lateinit var toolbarTitle: TextView  // 툴바에 있는 제목 텍스트 뷰

    override fun onAttach(context: Context) {
        super.onAttach(context)
        try {
            listener = context as NavigationListener
        } catch (e: ClassCastException) {
            throw ClassCastException("$context must implement NavigationListener")
        }
    }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        val view = inflater.inflate(R.layout.fragment_my_record, container, false)

        // UI 요소 초기화
        questionment = view.findViewById(R.id.questionment)
        subQuestionment = view.findViewById(R.id.subQuestionment)
        nextButton = view.findViewById(R.id.buttonNext)
        iconSituation = view.findViewById(R.id.icon_situation)
        iconThought = view.findViewById(R.id.icon_thought)
        iconEmotionStrength = view.findViewById(R.id.icon_emotion_strength)
        iconEmotionType = view.findViewById(R.id.icon_emotion_type)
        imgCharacter = view.findViewById(R.id.imgCharacter)
        textSituation = view.findViewById(R.id.text_situation)

        // 툴바에서 제목 텍스트 뷰 찾기
        toolbarTitle = view.findViewById(R.id.toolbar_write_title)

        // 현재 날짜 설정 (툴바에 있는 텍스트 뷰에 적용)
        val calendar = Calendar.getInstance()
        val sdf = SimpleDateFormat("MM월 dd일 E요일", Locale("ko", "KR"))
        toolbarTitle.text = sdf.format(calendar.time)  // 툴바에 날짜 설정

        val toolbarAction1 = view.findViewById<ImageButton>(R.id.button_write_action1)
        val toolbarAction2 = view.findViewById<ImageButton>(R.id.button_write_action2)

        // Toolbar 버튼 클릭 리스너 설정
        toolbarAction1.setOnClickListener { listener?.onToolbarAction1Clicked() }
        toolbarAction2.setOnClickListener { listener?.onToolbarAction2Clicked() }

        // 각 버튼에 클릭 리스너 설정
        nextButton.setOnClickListener { listener?.onNextButtonClicked() }

        // 기본 상태로 아이콘 모두 숨기기
        hideAllIcons()

        return view
    }

    override fun onDetach() {
        super.onDetach()
        listener = null
    }

    fun setButtonNextEnabled(enabled: Boolean) {
        nextButton.isEnabled = enabled
    }

    fun setButtonNextVisibility(visible: Boolean) {
        nextButton.visibility = if (visible) View.VISIBLE else View.INVISIBLE
    }

    // questionment와 subQuestionment의 텍스트를 동적으로 설정하는 메서드
    fun setQuestionText(mainQuestion: String, subQuestion: String) {
        questionment.text = mainQuestion
        subQuestionment.text = subQuestion
    }

    // 모아 이미지를 동적으로 설정하는 메서드
    fun setImage(resourceId: Int) {
        imgCharacter.setImageResource(resourceId)
    }

    // 모든 아이콘 숨기기
    private fun hideAllIcons() {
        iconSituation.visibility = View.GONE
        iconThought.visibility = View.GONE
        iconEmotionStrength.visibility = View.GONE
        iconEmotionType.visibility = View.GONE
    }

    // 특정 아이콘만 보이게 하는 메서드
    fun showIconForStep(step: Int) {
        hideAllIcons() // 우선 모든 아이콘 숨기기

        when (step) {
            1 -> {
                iconSituation.visibility = View.VISIBLE
                textSituation.apply {
                    visibility = View.VISIBLE
                    setTextColor(ContextCompat.getColor(context, R.color.white_40)) // 선택된 색상으로 변경
                }
            }

            2 -> {
                iconThought.visibility = View.VISIBLE
                textSituation.apply {
                    visibility = View.VISIBLE
                    setTextColor(ContextCompat.getColor(context, R.color.white_40))
                }
            }

            3 -> {
                iconEmotionStrength.visibility = View.VISIBLE
                textSituation.apply {
                    visibility = View.VISIBLE
                    setTextColor(ContextCompat.getColor(context, R.color.white_40))
                }
            }

            4 -> {
                iconEmotionType.visibility = View.VISIBLE
                textSituation.apply {
                    visibility = View.VISIBLE
                    setTextColor(ContextCompat.getColor(context, R.color.white_40))
                }
            }
//        when (step) {
//            1 -> iconSituation.visibility = View.VISIBLE
//            2 -> iconThought.visibility = View.VISIBLE
//            3 -> iconEmotionStrength.visibility = View.VISIBLE
//            4 -> iconEmotionType.visibility = View.VISIBLE
//        }
        }
    }


    fun setToolbarButtonListeners(backAction: () -> Unit, exitAction: () -> Unit) {
        val toolbarAction1 = view?.findViewById<ImageButton>(R.id.button_write_action1)
        val toolbarAction2 = view?.findViewById<ImageButton>(R.id.button_write_action2)

        toolbarAction1?.setOnClickListener { backAction() }
        toolbarAction2?.setOnClickListener { exitAction() }
    }

}
