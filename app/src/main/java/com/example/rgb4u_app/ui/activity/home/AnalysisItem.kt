package com.example.rgb4u_app.ui.activity.home

data class AnalysisItem(
    val hasAnalysisData: Boolean,
    val isInProgress: Boolean,
    val analysisDate: String = "",
    val dbDate: String = "",  // 기본값 설정
    val emptyMessage: String = "분석할 기록이 없어요"
)

