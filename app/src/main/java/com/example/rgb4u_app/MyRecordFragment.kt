package com.example.rgb4u_app

import android.content.Intent
import android.graphics.Color
import android.os.Bundle
import android.text.Editable
import android.text.TextWatcher
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Button
import android.widget.EditText
import android.widget.LinearLayout
import android.widget.TextView
import androidx.fragment.app.Fragment
import java.text.SimpleDateFormat
import java.util.Calendar
import java.util.Locale

class MyRecordFragment : Fragment() {

    private lateinit var dateTextView: TextView

    private lateinit var inputField: EditText
    private lateinit var charCountTextView: TextView

    private lateinit var progressBar: LinearLayout

    private lateinit var buttonNext: Button
    private var nextActivityClass: Class<*>? = null

    fun setNextActivity(activityClass: Class<*>) {
        nextActivityClass = activityClass
    }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        val view = inflater.inflate(R.layout.fragment_my_record, container, false)
        dateTextView = view.findViewById(R.id.dateTextView)

        // 현재 날짜 가져오기
        val calendar = Calendar.getInstance()
        val sdf = SimpleDateFormat("MM월 dd일 EEEE", Locale.getDefault())
        val currentDate = sdf.format(calendar.time)

        // TextView에 날짜 설정
        dateTextView.text = currentDate

        inputField = view.findViewById(R.id.inputField)
        charCountTextView = view.findViewById(R.id.charCountTextView)

        // TextWatcher를 사용하여 글자 수 업데이트
        inputField.addTextChangedListener(object : TextWatcher {
            override fun beforeTextChanged(s: CharSequence?, start: Int, count: Int, after: Int) {}

            override fun onTextChanged(s: CharSequence?, start: Int, before: Int, count: Int) {
                val textLength = s?.length ?: 0
                charCountTextView.text = "$textLength/150"
            }

            override fun afterTextChanged(s: Editable?) {}
        })

        // Arguments에서 questionText 가져오기
        val questionText = arguments?.getString("questionText")
        val questionment: TextView = view.findViewById(R.id.questionment) // ID에 맞게 수정
        questionment.text = questionText // 텍스트 설정

        progressBar = view.findViewById(R.id.progressbar)

        // 현재 단계에 따라 progressbar 업데이트
        val currentStep = arguments?.getInt("currentStep", 1) ?: 1
        updateProgressBar(currentStep)

        return view
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        buttonNext = view.findViewById(R.id.buttonNext)
        buttonNext.setOnClickListener {
            Log.d("MyRecordFragment", "Next button clicked") // 로그 추가
            nextActivityClass?.let {
                val intent = Intent(activity, it)
                startActivity(intent)
            }
        }
    }

    private fun updateProgressBar(step: Int) {
        // 각 단계에 맞게 색상 변경
        for (i in 0 until progressBar.childCount) {
            val child = progressBar.getChildAt(i)
            child.setBackgroundColor(if (i < step) Color.DKGRAY else Color.LTGRAY)
        }
    }
}
