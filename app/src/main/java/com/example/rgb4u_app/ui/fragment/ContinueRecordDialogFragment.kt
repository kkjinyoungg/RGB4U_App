package com.example.rgb4u_app.ui.fragment

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Button
import androidx.fragment.app.DialogFragment
import com.example.rgb4u_app.R

class ContinueRecordDialogFragment : DialogFragment() {

    private var onNewRecordClick: (() -> Unit)? = null
    private var onContinueClick: (() -> Unit)? = null

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        val view = inflater.inflate(R.layout.fragment_continue_record_dialog, container, false)

        // 버튼 클릭 리스너 설정
        view.findViewById<Button>(R.id.btn_new_record).setOnClickListener {
            onNewRecordClick?.invoke()
            dismiss() // 다이얼로그 닫기
        }

        view.findViewById<Button>(R.id.btn_continue_record).setOnClickListener {
            onContinueClick?.invoke()
            dismiss() // 다이얼로그 닫기
        }

        return view
    }

    fun setOnNewRecordClickListener(listener: () -> Unit) {
        onNewRecordClick = listener
    }

    fun setOnContinueClickListener(listener: () -> Unit) {
        onContinueClick = listener
    }
}