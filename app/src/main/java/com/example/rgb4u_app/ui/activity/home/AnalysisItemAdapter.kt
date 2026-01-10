package com.example.rgb4u.ver1_app.ui.activity.home

import android.content.Intent
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Button
import android.widget.TextView
import androidx.core.content.ContextCompat
import androidx.recyclerview.widget.RecyclerView
import com.example.rgb4u.ver1_app.R
import com.example.rgb4u.ver1_app.ui.activity.distortiontype.DistortionTypeActivity
import com.example.rgb4u.ver1_app.ui.activity.distortiontype.NotDistortionTypeInfoActivity
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.database.FirebaseDatabase

class AnalysisItemAdapter(private val analysisList: List<AnalysisItem>) :
    RecyclerView.Adapter<RecyclerView.ViewHolder>() {

    companion object {
        const val TYPE_ANALYSIS = 0
        const val TYPE_EMPTY = 1
    }

    override fun getItemViewType(position: Int): Int {
        return if (analysisList[position].hasAnalysisData) {
            TYPE_ANALYSIS
        } else {
            TYPE_EMPTY
        }
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): RecyclerView.ViewHolder {
        return when (viewType) {
            TYPE_ANALYSIS -> {
                val view = LayoutInflater.from(parent.context).inflate(R.layout.item_home_analysis, parent, false)
                AnalysisViewHolder(view)
            }
            TYPE_EMPTY -> {
                val view = LayoutInflater.from(parent.context).inflate(R.layout.item_home_analysis_empty, parent, false)
                view.alpha = 0.4f  // 50% 투명도
                EmptyViewHolder(view)
            }
            else -> throw IllegalArgumentException("Unknown view type: $viewType")
        }
    }


    override fun onBindViewHolder(holder: RecyclerView.ViewHolder, position: Int) {
        val item = analysisList[position]

        when (holder) {
            is AnalysisViewHolder -> {
                holder.bind(item)
            }
            is EmptyViewHolder -> {
                holder.bind(item)
            }
        }
    }

    // 외부에서 analysisList에 접근할 수 있도록 getter 메서드 추가
    fun getAnalysisList(): List<AnalysisItem> {
        return analysisList
    }

    override fun getItemCount(): Int {
        return analysisList.size
    }

    inner class AnalysisViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {
        private val analysisDateText: TextView = itemView.findViewById(R.id.analysisDateText)
        private val analysisResultButton: Button = itemView.findViewById(R.id.analysisResultButton)

        fun bind(item: AnalysisItem) {
            analysisDateText.text = item.analysisDate

            // 버튼 상태 업데이트
            if (item.isInProgress) {
                analysisResultButton.apply {
                    text = "분석중..."
                    isEnabled = false
                    backgroundTintList = ContextCompat.getColorStateList(itemView.context, R.color.gray4)
                    setTextColor(ContextCompat.getColor(itemView.context, R.color.gray3)) // 텍스트 색상 설정
                }
            } else {
                analysisResultButton.apply {
                    text = "분석 결과 보기"
                    isEnabled = true
                    backgroundTintList = ContextCompat.getColorStateList(itemView.context, R.color.main)
                    setTextColor(ContextCompat.getColor(itemView.context, R.color.black)) // 텍스트 색상 설정
                }

                // 버튼 클릭 리스너 추가
                analysisResultButton.setOnClickListener {
                    val context = itemView.context
                    val userId = FirebaseAuth.getInstance().currentUser?.uid
                    val date = item.dbDate // yyyy-mm-dd 형식

                    // **Firebase Realtime Database에서 totalCharacters 값 확인**
                    val databaseReference = FirebaseDatabase.getInstance().getReference("users/$userId/diaries/$date/aiAnalysis/secondAnalysis/totalCharacters")

                    databaseReference.get().addOnSuccessListener { snapshot ->
                        val totalCharacters = snapshot.getValue(Int::class.java) ?: 0

                        if (totalCharacters == 0) {
                            // **totalCharacters가 0일 경우 NotDistortionTypeInfoActivity로 이동**
                            val intent = Intent(context, NotDistortionTypeInfoActivity::class.java)
                            intent.putExtra("Date", item.dbDate)  // yyyy-mm-dd 형식의 날짜 전달
                            intent.putExtra("Toolbar", item.analysisDate)  // toolbar 형식의 날짜 전달
                            context.startActivity(intent)
                        } else {
                            // **totalCharacters가 0이 아닐 경우 기존 동작 유지**
                            val intent = Intent(context, DistortionTypeActivity::class.java)
                            intent.putExtra("Date", item.dbDate)  // yyyy-mm-dd 형식의 날짜 전달
                            intent.putExtra("Toolbar", item.analysisDate)  // toolbar 형식의 날짜 전달
                            context.startActivity(intent)
                        }
                    }.addOnFailureListener {
                        // Firebase에서 데이터 읽기 실패 시 처리
                        it.printStackTrace()
                    }
                }
            }
        }
    }

    inner class EmptyViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {
        private val analysisDetailText: TextView = itemView.findViewById(R.id.analysisDetailText)

        fun bind(item: AnalysisItem) {
            analysisDetailText.text = item.emptyMessage
        }
    }
}
