package com.example.rgb4u_app.ui.activity.onboarding

data class ChatData(
    val message: String,
    val sender: String,
    val imageResId: Int? = null // 이미지 리소스 ID 추가 (없을 경우 null)
)