package com.example.rgb4u_app

import android.util.Log
import com.google.firebase.database.DatabaseReference
import com.google.firebase.database.FirebaseDatabase

class SampleData2 {
    private val firebaseDatabase: FirebaseDatabase = FirebaseDatabase.getInstance()
    private val TAG = "SampleData2" // Logging Tag

    // 첫 번째 분석 데이터 채우는 함수
    fun fillingsummary(userId: String, diaryId: String, date: String, callback: () -> Unit) {
        val analysisRef: DatabaseReference =
            firebaseDatabase.getReference("users/$userId/diaries/$date/aiAnalysis/firstAnalysis")

        // 데이터 채우기
        val emotionList = listOf("진짜 우울해") // 예시로 넣을 감정 리스트
        val situation =
            "열심히 공부한 수학 시험에서 50점을 받았어. 내 친구들은 다 80점 이상을 받았어." // 상황에 대한 AI 분석
        val thoughts =
            "열심히 공부했는데 아무 소용이 없었어. 친구들이 내 점수를 보고 무시했을 거야. 나는 멍청이야." // 생각에 대한 AI 분석
        val situationReason =
            "열심히 공부했는데 수학 시험에서 50점, 친구들은 80점 이상을 받았어요. ‘나는 멍청이야.’ 생각을 나타내고 있어서 옮겨졌어요." // 상황 분석 이유
        val thoughtsReason =
            "열심히 공부했는데 점수가 낮아서 친구들이 무시할 것 같고, 내가 멍청한 것 같다고 생각했어요. ‘진짜 우울해.’는 감정을 나타내고 있어서 옮겨졌어요." // 생각 분석 이유

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
        val analysisRef: DatabaseReference =
            firebaseDatabase.getReference("users/$userId/diaries/$date/aiAnalysis/secondAnalysis")

        // 추가적인 thoughtSets 데이터 예시
        val thoughtSets = mapOf(
            "외면성" to listOf(
                ThoughtData(
                    "열심히 한 건 대단하니까 다음엔 더 잘할 수 있어!",
                    "이렇게 생각하면, 노력을 인정하고 긍정적인 마음을 가질 수 있어서 더 힘이 될 거예요.",
                    listOf("다른 사람의 생각을 마음대로", "넘겨짚게 해요"),
                    "이 생각은 자신이 노력한 긍정적인 점을 제대로 보지 않고 있어요. 사실 열심히 공부한 것 자체는 정말 대단한 일이에요.",
                    "ic_planet_h",
                    "열심히 공부했는데 아무 소용이 없었어."
                )
            ),
            "궁예성" to listOf(
                ThoughtData(
                    "내 점수를 보고 친구들이 어떻게 생각했는지는 알 수 없어",
                    "이렇게 생각하면 친구들 마음을 함부로 짐작하지 않아서 덜 걱정할 수 있어요.",
                    listOf("별다른 이유 없이 미래를 부정적으로", "생각하게 해요"),
                    "이 생각은 친구들이 어떻게 생각하는지 확실히 알지도 못하면서 그냥 그렇게 믿고 있어요.",
                    "ic_planet_b",
                    "친구들이 내 점수를 보고 무시했을 거야."
                )
            ),
            "이름성" to listOf(
                ThoughtData(
                    "실수할 수 있지만 다시 해 보면 더 잘할 수 있어!",
                    "이렇게 생각하면 실수를 받아들이고 다음에 더 잘할 수 있을 거예요.",
                    listOf("몇 가지 일로 모든 일에 부정적인", "결론을 짓게 해요"),
                    "한 번 실수했다고 해서 나쁜 이름을 스스로에게 붙이고 있어요.",
                    "ic_planet_i",
                    "나는 멍청이야."
                )
            )
        )

        // Firebase에 secondAnalysis 데이터만 저장
        analysisRef.setValue(
            mapOf(
                "thoughtSets" to thoughtSets,
                "totalCharacters" to 3, // 총 캐릭터 수
                "totalSets" to 3 // 총 sets 수
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

