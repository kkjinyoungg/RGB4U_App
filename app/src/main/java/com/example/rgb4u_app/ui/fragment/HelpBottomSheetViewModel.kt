package com.example.rgb4u_app.ui.fragment

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import com.example.rgb4u_app.activity.ActivityType

class HelpBottomSheetViewModel : ViewModel() {
    private val _situations = MutableLiveData<List<String>>()
    val situations: LiveData<List<String>> get() = _situations

    fun setSituations(activityType: String) {
        val situationList = when (activityType) {
            ActivityType.ACTIVITY_DIARY -> listOf(
                "친구가 내가 아닌 다른 친구하다고 말을 했어.",
                "친구의 생일 파티에 초대받지 못했어.",
                "학교에 가는 길에 넘어져서 무릎을 다쳤어."
                // 필요한 문장을 추가하세요.
            )
            ActivityType.ACTIVITY_THINK -> listOf(
                "오늘 하루를 어떻게 보냈는지 적어보세요.",
                "최근에 느꼈던 감정은 무엇인가요?",
                "이번 주 목표는 무엇인가요?"
                // 필요한 문장을 추가하세요.
            )
            else -> emptyList()
        }

        _situations.value = situationList
    }
}

