package com.example.rgb4u_app.ui.activity.home

import android.content.Intent
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Button
import android.widget.TextView
import androidx.core.content.ContextCompat
import androidx.recyclerview.widget.RecyclerView
import com.example.rgb4u_app.R
import com.example.rgb4u_app.ui.activity.distortiontype.DistortionTypeActivity

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
                }

                // 버튼 클릭 리스너 추가
                analysisResultButton.setOnClickListener {
                    val context = itemView.context
                    val intent = Intent(context, DistortionTypeActivity::class.java)
                    // 화면 전환(전달할 데이터가 있다면 수정 필요함)
                    intent.putExtra("date", item.dbDate)  // yyyy-mm-dd 형식의 날짜 전달
                    intent.putExtra("toolbar", item.analysisDate)  // toolbar 형식의 날짜 전달
                    context.startActivity(intent)
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
