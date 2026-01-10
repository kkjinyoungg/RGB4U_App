package com.example.rgb4u.ver1_app.ui.fragment

import android.content.Context
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Button
import androidx.fragment.app.DialogFragment
import com.example.rgb4u.ver1_app.R
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.database.FirebaseDatabase

class ContinueRecordDialogFragment : DialogFragment() {

    private var onNewRecordClick: (() -> Unit)? = null
    private var onContinueClick: (() -> Unit)? = null

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        val view = inflater.inflate(R.layout.fragment_continue_record_dialog, container, false)

        val userId = FirebaseAuth.getInstance().currentUser?.uid
        val currentDate = getCurrentDate()

        // "새로 쓰기" 버튼 클릭 시 동작 설정
        view.findViewById<Button>(R.id.btn_new_record).setOnClickListener {
            // Firebase Realtime Database에서 기존 기록 삭제
            if (userId != null) {
                val diaryRef = FirebaseDatabase.getInstance().getReference("users/$userId/diaries/$currentDate")
                diaryRef.removeValue().addOnSuccessListener {
                    // 삭제 성공
                    onNewRecordClick?.invoke()
                }.addOnFailureListener { error ->
                    // 삭제 실패 시 로그 출력
                    error.printStackTrace()
                }
            }
            dismiss() // 다이얼로그 닫기
        }

        // "이어서 쓰기" 버튼 클릭 시 동작 설정
        view.findViewById<Button>(R.id.btn_continue_record).setOnClickListener {
            // navigateToDiaryWriteActivity() 동작 수행
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

    // 현재 날짜를 yyyy-MM-dd 형식으로 반환
    private fun getCurrentDate(): String {
        val dateFormat = java.text.SimpleDateFormat("yyyy-MM-dd", java.util.Locale.getDefault())
        return dateFormat.format(java.util.Calendar.getInstance().time)
    }
}
