package com.example.rgb4u.ver1_app.ui.activity.calendar

data class MyData(
    val imageResId: Int,  // 행성 유형의 이미지 리소스 ID
    val name: String,     // 행성 유형의 이름
    val description: String,  // 행성에 대한 설명
    val thinkMessages: List<String>,  // 여러 개의 Think 메시지
    val thinkExtraMessages: List<String>,  // 여러 개의 추가 Think 메시지
    val changeMessages: List<String>,  // 여러 개의 Change 메시지
    val changeExtraMessages: List<String> // 여러 개의 추가 Change 메시지
)
