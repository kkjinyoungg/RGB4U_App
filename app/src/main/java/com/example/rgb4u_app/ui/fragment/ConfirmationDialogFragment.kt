package com.example.rgb4u_app.ui.fragment

import android.app.AlertDialog
import android.app.Dialog
import android.graphics.Color
import android.graphics.drawable.ColorDrawable
import android.os.Bundle
import android.view.Window
import android.widget.Button
import android.widget.TextView
import androidx.fragment.app.DialogFragment
import com.example.rgb4u_app.R

class ConfirmationDialogFragment(
    private val title: String,
    private val message: String,
    private val confirmButtonText: String, // 추가된 코드
    private val onConfirm: () -> Unit
) : DialogFragment() {

    override fun onCreateDialog(savedInstanceState: Bundle?): Dialog {
        val builder = AlertDialog.Builder(requireContext())
        val inflater = requireActivity().layoutInflater
        val view = inflater.inflate(R.layout.fragment_confirmation_dialog, null)

        val tvTitle = view.findViewById<TextView>(R.id.dialogTitle)
        val tvMessage = view.findViewById<TextView>(R.id.dialogMessage)
        val btnConfirm = view.findViewById<Button>(R.id.btnConfirm)
        val btnCancel = view.findViewById<Button>(R.id.btnCancel)

        tvTitle.text = title
        tvMessage.text = message

        // 버튼 텍스트 설정
        btnConfirm.text = confirmButtonText // 추가된 코드

        btnConfirm.setOnClickListener {
            onConfirm()
            dismiss()
        }

        btnCancel.setOnClickListener {
            dismiss()
        }

        builder.setView(view)
        return builder.create()
    }
}


