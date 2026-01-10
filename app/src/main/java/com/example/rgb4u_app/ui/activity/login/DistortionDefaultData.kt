package com.example.rgb4u.ver1_app.ui.activity.login

import com.google.firebase.database.FirebaseDatabase

class DistortionDefaultData(val userId: String) {
    fun saveDistortionData() {
        // Firebase Realtime Database 참조
        val database = FirebaseDatabase.getInstance()
        val ref = database.getReference("users").child(userId).child("distortionInformation") // 저장할 경로 설정

        // 저장할 데이터
        val distortionData = mapOf(
            "흑백성" to mapOf(
                "imageResource" to "ic_planet_a",
                "description1" to "모든 일을 두 가지로만 나눠서",
                "description2" to "생각하게 해요",
                "subtitle" to "이런 생각이 떠오를 땐 두 가지가 아닌 더 다양한 가능성을 고려해보세요. 극단적인 생각을 피할 수 있을 거예요!"
            ),
            "재앙성" to mapOf(
                "imageResource" to "ic_planet_b",
                "description1" to "별다른 이유 없이 미래를 부정적으로",
                "description2" to "생각하게 해요",
                "subtitle" to "이런 생각이 떠오를 땐 최악의 결과를 상상하는 대신 현실적으로 생각해보세요. 불필요한 걱정을 줄일 수 있을 거예요!"
            ),
            "외면성" to mapOf(
                "imageResource" to "ic_planet_c",
                "description1" to "자신의 좋은 면을 못 보고 스스로를",
                "description2" to "낮춰 생각하게 해요",
                "subtitle" to "이런 생각이 떠오를 땐 자신의 성취와 장점을 인정해보세요. 자신의 능력을 믿으면 더 나은 결정을 내릴 수 있을 거예요!"
            ),
            "느낌성" to mapOf(
                "imageResource" to "ic_planet_d",
                "description1" to "자신의 느낌이 틀림없는 사실이라고",
                "description2" to "생각하게 해요",
                "subtitle" to "이런 생각이 떠오를 땐 주관적인 느낌과 객관적인 사실을 나눠서 생각해보세요. 더 현실적으로 생각할 수 있을 거예요!"
            ),
            "이름성" to mapOf(
                "imageResource" to "ic_planet_e",
                "description1" to "자신이나 다른 사람에게 부정적인 결론이",
                "description2" to "담긴 이름을 붙이게 해요",
                "subtitle" to "이런 생각이 떠오를 땐 다양한 관점에서 상황을 평가해보세요. 더 균형 잡힌 시각을 가질 수 있을 거예요!"
            ),
            "과장성" to mapOf(
                "imageResource" to "ic_planet_f",
                "description1" to "작은 실수를 지나치게 크게 확대해서",
                "description2" to "생각하게 해요",
                "subtitle" to "이런 생각이 떠오를 땐 문제를 과장하지 말고 현실적인 해결책을 찾아보세요."
            ),
            "부분성" to mapOf(
                "imageResource" to "ic_planet_g",
                "description1" to "전체를 보고 판단해야 하는데",
                "description2" to "일부분만 보고 결론을 내리게 해요",
                "subtitle" to "이런 생각이 떠오를 땐 전체 상황을 종합적으로 고려해보세요."
            ),
            "궁예성" to mapOf(
                "imageResource" to "ic_planet_h",
                "description1" to "사람들의 마음을 예측해서",
                "description2" to "자신의 생각을 왜곡하게 해요",
                "subtitle" to "이런 생각이 떠오를 땐 다른 사람의 생각을 추측하기보다는 직접 물어보세요."
            ),
            "일반화성" to mapOf(
                "imageResource" to "ic_planet_i",
                "description1" to "하나의 사건을 통해",
                "description2" to "모든 상황을 일반화해서 생각하게 해요",
                "subtitle" to "이런 생각이 떠오를 땐 개별적인 상황을 분리하여 생각해보세요."
            ),
            "내탓성" to mapOf(
                "imageResource" to "ic_planet_j",
                "description1" to "모든 일이 자신의 탓이라고",
                "description2" to "생각하게 해요",
                "subtitle" to "이런 생각이 떠오를 땐 자신의 책임을 과도하게 묻지 말고 객관적인 시각을 가져보세요."
            ),
            "해야해성" to mapOf(
                "imageResource" to "ic_planet_k",
                "description1" to "자신에게 지나치게 높은 기대를 걸고",
                "description2" to "해야 한다고 강박적으로 생각하게 해요",
                "subtitle" to "이런 생각이 떠오를 땐 자신에게 지나친 부담을 주지 말고, 여유를 가져보세요."
            ),
            "어둠성" to mapOf(
                "imageResource" to "ic_planet_l",
                "description1" to "어두운 생각에만 집중하게 해요",
                "description2" to "생각하게 해요",
                "subtitle" to "이런 생각이 떠오를 땐 긍정적인 측면을 찾아보세요. 더 밝은 시각을 가질 수 있을 거예요!"
            )
        )

        // 데이터 저장
        ref.setValue(distortionData)
            .addOnSuccessListener {
                println("인지왜곡 기본 데이터 저장 완료")
            }
            .addOnFailureListener { e ->
                println("인지왜곡 기본 데이터 저장 실패: ${e.message}")
            }
    }

}