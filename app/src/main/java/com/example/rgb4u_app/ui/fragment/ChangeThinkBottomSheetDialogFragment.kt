package com.example.rgb4u.ver1_app.ui.fragment

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import android.widget.TextView
import com.example.rgb4u.ver1_app.R
import com.google.android.material.bottomsheet.BottomSheetDialogFragment

class ChangeThinkBottomSheetDialogFragment : BottomSheetDialogFragment() {

    private lateinit var dialogImageView: ImageView
    private lateinit var titleTextView: TextView
    private lateinit var descriptionTextView: TextView

    // Fragment 인스턴스를 생성하는 메서드
    companion object {
        fun newInstance(imageResId: Int, title: String, description: String): ChangeThinkBottomSheetDialogFragment {
            val fragment = ChangeThinkBottomSheetDialogFragment()
            val args = Bundle().apply {
                putInt("imageResId", imageResId)
                putString("title", title)
                putString("description", description)
            }
            fragment.arguments = args
            return fragment
        }
    }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        // XML 레이아웃 파일을 inflate
        return inflater.inflate(R.layout.fragment_change_think_bottom_sheet_dialog, container, false)
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        // 뷰 초기화
        dialogImageView = view.findViewById(R.id.dialogImageView)
        titleTextView = view.findViewById(R.id.titleTextView)
        descriptionTextView = view.findViewById(R.id.descriptionTextView)

        // arguments에서 데이터 가져와서 뷰에 설정
        arguments?.let {
            val imageResId = it.getInt("imageResId")
            val title = it.getString("title") ?: ""
            val description = it.getString("description") ?: ""
            dialogImageView.setImageResource(imageResId)
            titleTextView.text = title
            descriptionTextView.text = description
        }
    }
}
