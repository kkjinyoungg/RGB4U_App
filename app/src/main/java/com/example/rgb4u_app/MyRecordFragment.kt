package com.example.rgb4u_app

import android.content.Context
import android.graphics.Color
import android.os.Bundle
import android.text.Editable
import android.text.TextWatcher
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Button
import android.widget.EditText
import android.widget.LinearLayout
import android.widget.TextView
import android.widget.Toast
import androidx.appcompat.widget.AppCompatImageButton
import androidx.fragment.app.Fragment
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
    private lateinit var inputField: EditText
    private lateinit var charCountTextView: TextView
    private lateinit var progressBar: LinearLayout
    private lateinit var nextButton: Button
    private lateinit var backButton: AppCompatImageButton

    override fun onAttach(context: Context) {
        super.onAttach(context)
        if (context is NavigationListener) {
            listener = context
        } else {
            throw ClassCastException("$context must implement NavigationListener")
        }
    }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        val view = inflater.inflate(R.layout.fragment_my_record, container, false)
        dateTextView = view.findViewById(R.id.dateTextView)

        // 현재 날짜 가져오기
        val calendar = Calendar.getInstance()
        val sdf = SimpleDateFormat("MM월 dd일 E요일", Locale("ko", "KR"))
        val currentDate = sdf.format(calendar.time)

        // TextView에 날짜 설정
        dateTextView.text = currentDate

        inputField = view.findViewById(R.id.inputField)
        charCountTextView = view.findViewById(R.id.charCountTextView)
        nextButton = view.findViewById(R.id.buttonNext)
        backButton = view.findViewById(R.id.backButton)  // 뒤로가기 버튼 초기화

        // 초기 버튼 상태 설정 (처음에는 비활성화)
        setButtonState(false)

        // 글자 수에 따라 버튼 활성화
        inputField.addTextChangedListener(object : TextWatcher {
            override fun beforeTextChanged(s: CharSequence?, start: Int, count: Int, after: Int) {}

            override fun onTextChanged(s: CharSequence?, start: Int, before: Int, count: Int) {
                val inputText = s.toString()
                charCountTextView.text = "${inputText.length}/150"  // 글자 수 업데이트
                validateInput(inputText)
            }

            override fun afterTextChanged(s: Editable?) {}
        })

        // 뒤로가기 버튼 동작
        backButton.setOnClickListener {
            listener?.onBackButtonClicked()
        }

        // 다음 버튼 동작
        nextButton.setOnClickListener {
            listener?.onNextButtonClicked()
        }

        // Arguments에서 questionText 가져오기
        val questionText = arguments?.getString("questionText")
        val questionment: TextView = view.findViewById(R.id.questionment)  // ID에 맞게 수정
        questionment.text = questionText  // 텍스트 설정

        progressBar = view.findViewById(R.id.progressbar)

        // 현재 단계에 따라 progressbar 업데이트
        val currentStep = arguments?.getInt("currentStep", 1) ?: 1
        updateProgressBar(currentStep)

        return view
    }

    override fun onDetach() {
        super.onDetach()
        listener = null
    }

    // 입력 유효성 검사
    private fun validateInput(input: String) {
        val byteLength = getByteLength(input)
        when {
            byteLength > 300 -> {
                charCountTextView.setTextColor(Color.RED)
                Toast.makeText(requireContext(), "150자 이상으로 쓸 수 없어!", Toast.LENGTH_SHORT).show()
                setButtonState(false)
            }
            else -> {
                charCountTextView.setTextColor(Color.BLACK)
                setButtonState(input.isNotEmpty())  // 입력이 있으면 활성화
            }
        }
    }

    // 버튼 활성화/비활성화 설정
    private fun setButtonState(isValid: Boolean) {
        nextButton.isEnabled = isValid
        // 버튼 색상은 rounded_button.xml에서 자동으로 처리됨
    }

    // 바이트 수를 계산하는 함수 (UTF-8 기준)
    private fun getByteLength(text: String): Int {
        return text.toByteArray(Charsets.UTF_8).size
    }

    private fun updateProgressBar(step: Int) {
        // 각 단계에 맞게 색상 변경
        for (i in 0 until progressBar.childCount) {
            val child = progressBar.getChildAt(i)
            child.setBackgroundColor(if (i < step) Color.DKGRAY else Color.LTGRAY)
        }
    }
}
