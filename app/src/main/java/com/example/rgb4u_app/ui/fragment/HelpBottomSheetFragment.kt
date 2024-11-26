package com.example.rgb4u_app.ui.fragment

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageButton
import android.widget.ImageView
import android.widget.TextView
import androidx.lifecycle.ViewModelProvider
import com.example.rgb4u_app.R
import com.google.android.material.bottomsheet.BottomSheetDialogFragment

class HelpBottomSheetFragment : BottomSheetDialogFragment() {

    private lateinit var viewModel: HelpBottomSheetViewModel
    private lateinit var situationList: List<String>  // 상황 리스트 추가
    private var currentSituations: MutableList<String> = mutableListOf() // 현재 보여지는 상황 리스트

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        // Inflate the layout for this fragment
        val view = inflater.inflate(R.layout.fragment_help_bottom_sheet, container, false)

        viewModel = ViewModelProvider(this).get(HelpBottomSheetViewModel::class.java)

        // 상황 텍스트뷰 설정
        val refreshButton = view.findViewById<ImageView>(R.id.refreshButton)

        // 초기 상황 설정
        updateSituations(view)

        // 새로고침 버튼 클릭 리스너
        refreshButton.setOnClickListener {
            updateSituations(view)
        }

        return view
    }

    // 바텀 시트 높이값 고정
    override fun onStart() {
        super.onStart()

        dialog?.let {
            val bottomSheet = it.findViewById<View>(com.google.android.material.R.id.design_bottom_sheet)
            bottomSheet?.layoutParams?.height = 400.dpToPx()  // 원하는 높이로 설정
            bottomSheet?.requestLayout()
        }
    }

    // dp를 px로 변환하는 확장 함수
    private fun Int.dpToPx(): Int {
        return (this * resources.displayMetrics.density).toInt()
    }

    // 상황 리스트 설정
    fun setSituations(situations: List<String>) {
        this.situationList = situations
        // view가 null이 아닌 경우에만 updateSituations를 호출합니다.
        view?.let { updateSituations(it) } // 초기 상황을 설정합니다.
    }

    private fun updateSituations(view: View) {
        // 랜덤으로 3개의 새로운 상황 선택
        val availableSituations = situationList.filter { !currentSituations.contains(it) }
        currentSituations.clear()

        if (availableSituations.isNotEmpty()) {
            currentSituations.addAll(availableSituations.shuffled().take(3))
        }

        // 텍스트뷰에 새로운 상황 설정
        val exampleTextView1 = view.findViewById<TextView>(R.id.exampleTextView1)
        val exampleTextView2 = view.findViewById<TextView>(R.id.exampleTextView2)
        val exampleTextView3 = view.findViewById<TextView>(R.id.exampleTextView3)

        // 상황을 각 TextView에 설정
        exampleTextView1.text = if (currentSituations.size > 0) currentSituations[0] else ""
        exampleTextView2.text = if (currentSituations.size > 1) currentSituations[1] else ""
        exampleTextView3.text = if (currentSituations.size > 2) currentSituations[2] else ""
    }
}
