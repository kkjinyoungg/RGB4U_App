package com.example.rgb4u_app.ui.fragment

import android.content.Context
import android.content.Intent
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Button
import android.widget.ImageButton
import android.widget.ImageView
import android.widget.TextView
import androidx.appcompat.widget.AppCompatImageButton
import androidx.fragment.app.Fragment
import com.example.rgb4u_app.R
import com.example.rgb4u_app.ui.activity.MainActivity
import java.text.SimpleDateFormat
import java.util.Calendar
import java.util.Locale

class MyRecordFragment : Fragment() {

    interface NavigationListener {
        fun onNextButtonClicked()
        fun onBackButtonClicked()
    }

    private var listener: NavigationListener? = null
    private lateinit var dateTextView: TextView
    private lateinit var questionment: TextView
    private lateinit var subQuestionment: TextView
    private lateinit var nextButton: Button
    private lateinit var backButton: AppCompatImageButton
    private lateinit var exitButton: AppCompatImageButton
    private lateinit var iconSituation: ImageView
    private lateinit var iconThought: ImageView
    private lateinit var iconEmotionStrength: ImageView
    private lateinit var iconEmotionType: ImageView
    private lateinit var helpButton: ImageButton

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
        dateTextView = view.findViewById(R.id.dateTextView)
        questionment = view.findViewById(R.id.questionment)
        subQuestionment = view.findViewById(R.id.subQuestionment)
        nextButton = view.findViewById(R.id.buttonNext)
        backButton = view.findViewById(R.id.backButton)
        exitButton = view.findViewById(R.id.exitButton)
        iconSituation = view.findViewById(R.id.icon_situation)
        iconThought = view.findViewById(R.id.icon_thought)
        iconEmotionStrength = view.findViewById(R.id.icon_emotion_strength)
        iconEmotionType = view.findViewById(R.id.icon_emotion_type)
        helpButton = view.findViewById(R.id.helpButton) // Initialize here

        // 현재 날짜 설정
        val calendar = Calendar.getInstance()
        val sdf = SimpleDateFormat("MM월 dd일 E요일", Locale("ko", "KR"))
        dateTextView.text = sdf.format(calendar.time)

        // 각 버튼에 클릭 리스너 설정
        backButton.setOnClickListener { listener?.onBackButtonClicked() }
        nextButton.setOnClickListener { listener?.onNextButtonClicked() }
        exitButton.setOnClickListener {
            val intent = Intent(activity, MainActivity::class.java)
            startActivity(intent)
            activity?.finish()
        }

        // helpButton 클릭 리스너 설정
        helpButton.setOnClickListener {
            val bottomSheet = HelpBottomSheetFragment()
            bottomSheet.show(childFragmentManager, bottomSheet.tag)
        }

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
            1 -> iconSituation.visibility = View.VISIBLE
            2 -> iconThought.visibility = View.VISIBLE
            3 -> iconEmotionStrength.visibility = View.VISIBLE
            4 -> iconEmotionType.visibility = View.VISIBLE
        }
    }

    fun setHelpButtonEnabled(enabled: Boolean) {
        helpButton.isEnabled = enabled
    }

    fun setHelpButtonVisibility(visible: Boolean) {
        helpButton.visibility = if (visible) View.VISIBLE else View.GONE
    }
}
