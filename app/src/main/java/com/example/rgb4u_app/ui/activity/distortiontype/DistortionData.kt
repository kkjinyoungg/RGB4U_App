package com.example.rgb4u_app.ui.activity.distortiontype

data class DistortionData(
    val myThoughtTitle: String,        // '~~성이 발견된 생각' 부분
    val myThought: String,             // 사용자가 입력한 생각
    val reasonTitle: String,           // '왜 ~~성일까요?' 부분
    val reason: String,                // 왜 그 성인지에 대한 설명
    val suggestionTitle: String,       // '이렇게 생각해보면 어떨까요?' 부분 (고정)
    val shortSuggestion: String,       // 간단한 제안 텍스트
    val suggestion: String,            // 긴 제안 텍스트
    val distortionType: String,        // 왜곡의 유형 (예: 흑백성)
    val imageResId: Int                // 왜곡의 유형 이미지 리소스
)
