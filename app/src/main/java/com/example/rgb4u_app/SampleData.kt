package com.example.rgb4u_app

import android.util.Log
import com.google.firebase.database.DatabaseReference
import com.google.firebase.database.FirebaseDatabase

class SampleData {
    private val firebaseDatabase: FirebaseDatabase = FirebaseDatabase.getInstance()
    private val TAG = "SampleData" // Logging Tag

    // 첫 번째 분석 데이터 채우는 함수
    fun fillingsummary(userId: String, diaryId: String, date: String, callback: () -> Unit) {
        val analysisRef: DatabaseReference = firebaseDatabase.getReference("users/$userId/diaries/$date/aiAnalysis/firstAnalysis")

        // 데이터 채우기
        val emotionList = listOf("행복", "기쁨") // 예시로 넣을 감정 리스트
        val situation = "랄랄라였어" // 상황에 대한 AI 분석
        val thoughts = "랄랄라였어" // 생각에 대한 AI 분석
        val situationReason = "AI 분석 이유" // 상황 분석 이유
        val thoughtsReason = "AI 생각 분석 이유" // 생각 분석 이유

        // Firebase에 데이터 저장
        analysisRef.setValue(
            mapOf(
                "emotion" to emotionList,
                "situation" to situation,
                "thoughts" to thoughts,
                "situationReason" to situationReason,
                "thoughtsReason" to thoughtsReason
            )
        ).addOnSuccessListener {
            Log.d(TAG, "첫 번째 분석 데이터 저장 성공")
            callback() // 성공 시 콜백 호출
        }.addOnFailureListener {
            Log.e(TAG, "첫 번째 분석 데이터 저장 실패: ${it.message}")
        }
    }

    // 두 번째 분석 데이터 채우는 함수
    fun fillinganalysis(userId: String, diaryId: String, date: String, callback: () -> Unit) {
        val analysisRef: DatabaseReference = firebaseDatabase.getReference("users/$userId/diaries/$date/aiAnalysis/secondAnalysis")

        // 데이터 채우기
        val emotionList = listOf("슬픔", "불안") // 예시로 넣을 감정 리스트
        val situation = "AI 두 번째 분석 상황" // 두 번째 상황에 대한 AI 분석
        val thoughts = "AI 두 번째 분석 생각" // 두 번째 생각에 대한 AI 분석
        val situationReason = "두 번째 AI 분석 이유" // 두 번째 상황 분석 이유
        val thoughtsReason = "두 번째 AI 생각 분석 이유" // 두 번째 생각 분석 이유

        // 추가적인 thoughtSets 데이터 예시
        val thoughtSets = mapOf(
            "랄라성" to listOf(
                ThoughtData(
                    "나는 다른 사람들과 다르게 내 각별한 능력과 가치가 있다.",
                    "각 사람들은 서로 다른 장점과 능력을 갖고 있으며, 자신의 독특함을 인정하고 긍정적으로 받아들이면 자존감이 높아지고 더 나은 관계를 형성할 수 있다.",
                    listOf("자신이나 다른 사람에게 부정적인 결론이 담긴 이름을 붙이게 해요"),
                    "자신을 다른 사람과 비교하여 부정적으로 이름붙이는 행위로, 자신의 능력이나 가치를 지나치게 일반화하고 과장하는 것이 특징이다.",
                    "ic_planet_e",
                    "나는 친구보다 훨씬 못났어"
                ),
                ThoughtData(
                    "나는 현재 이 문제에 대해 아직 충분히 이해하지 못했을 뿐이지, 멍청한 것은 아니다.",
                    "모든 사람은 어떤 분야에서 완벽하지 않을 수 있고, 지식을 쌓는 과정에서 실수를 할 수 있다는 점을 이해하고 자신을 지나치게 비난하지 않는 것이 중요하다.",
                    listOf("자신이나 다른 사람에게 부정적인 결론이 담긴 이름을 붙이게 해요"),
                    "자신에게나 다른 사람에게 전반적이고 고정된 부정적인 이름을 붙이기 때문에 해당된다. 멍청하다는 고정된 이름을 붙이고 있음.",
                    "ic_planet_e",
                    "나는 멍청해"
                ),
                ThoughtData(
                    "나는 노답이 아니고 성장하고 발전할 여지가 많다.",
                    "자신을 부정적으로 정의하는 것은 성장과 발전을 막을 수 있기 때문에 긍정적으로 바라볼 필요가 있습니다.",
                    listOf("자신이나 다른 사람에게 부정적인 결론이 담긴 이름을 붙이게 해요"),
                    "본인과 다른 사람을 고정적이고 부정적으로 평가하는 경향이 있습니다.",
                    "ic_planet_e",
                    "나는 노답이야."
                )
            )
        )

        // Firebase에 데이터 저장
        analysisRef.setValue(
            mapOf(
                "emotion" to emotionList,
                "situation" to situation,
                "thoughts" to thoughts,
                "situationReason" to situationReason,
                "thoughtsReason" to thoughtsReason,
                "thoughtSets" to thoughtSets
            )
        ).addOnSuccessListener {
            Log.d(TAG, "두 번째 분석 데이터 저장 성공")
            callback() // 성공 시 콜백 호출
        }.addOnFailureListener {
            Log.e(TAG, "두 번째 분석 데이터 저장 실패: ${it.message}")
        }
    }

    // ThoughtData 클래스 정의 (secondAnalysis에 사용)
    data class ThoughtData(
        val alternativeThoughts: String,
        val alternativeThoughtsReason: String,
        val characterDescription: List<String>,
        val charactersReason: String,
        val imageResource: String,
        val selectedThoughts: String
    )
}
