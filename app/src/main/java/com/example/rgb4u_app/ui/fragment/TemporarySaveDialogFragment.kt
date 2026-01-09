package com.example.rgb4u.ver1_app.ui.fragment

import android.graphics.Color
import android.graphics.drawable.ColorDrawable
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.view.Window
import android.widget.Button
import androidx.fragment.app.DialogFragment
import com.example.rgb4u.ver1_app.R

class TemporarySaveDialogFragment : DialogFragment() {

    interface OnButtonClickListener {
        fun onTemporarySave()
        fun onDelete()
    }

    var listener: OnButtonClickListener? = null

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        val view = inflater.inflate(R.layout.fragment_temporary_save_dialog, container, false)
        dialog?.window?.setBackgroundDrawable(ColorDrawable(Color.TRANSPARENT))
        dialog?.window?.requestFeature(Window.FEATURE_NO_TITLE)

        val temporarySaveButton: Button = view.findViewById(R.id.btnTemporarySave)
        val deleteButton: Button = view.findViewById(R.id.btnDelete)
        val cancelButton: Button = view.findViewById(R.id.btnCancel)

        temporarySaveButton.setOnClickListener {
            listener?.onTemporarySave()
            dismiss()
        }

        deleteButton.setOnClickListener {
            listener?.onDelete()
            dismiss()
        }

        cancelButton.setOnClickListener {
            dismiss()
        }

        return view
    }
}
