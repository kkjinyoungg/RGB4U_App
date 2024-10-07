package com.example.rgb4u_app.ui.fragment

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import com.example.rgb4u_app.R
import com.google.android.material.bottomsheet.BottomSheetDialogFragment

class HelpBottomSheetFragment : BottomSheetDialogFragment() {

    // 내용 변경을 위한 변수
    private var helpMessage: String? = null

    // Fragment 생성 시 호출
    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        return inflater.inflate(R.layout.fragment_help_bottom_sheet, container, false)
    }

    // 내용 설정
    fun setHelpMessage(message: String) {
        helpMessage = message
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        // messageTextView를 fragment_help_bottom_sheet.xml에서 설정
        view.findViewById<TextView>(R.id.messageTextView).text = helpMessage
    }
}
