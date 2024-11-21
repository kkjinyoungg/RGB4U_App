package com.example.rgb4u_app

import android.util.Log
import com.google.firebase.database.DatabaseReference
import com.google.firebase.database.FirebaseDatabase

class SampleData {
    private val firebaseDatabase: FirebaseDatabase = FirebaseDatabase.getInstance()
    private val TAG = "SampleData" // Logging Tag

    // 첫 번째 분석 데이터 채우는 함수
    fun fillingsummary(userId: String, diaryId: String, date: String, callback: () -> Unit) {
        val analysisRef: DatabaseReference =
            firebaseDatabase.getReference("users/$userId/diaries/$date/aiAnalysis/firstAnalysis")

        // 데이터 채우기
        val emotionList = listOf("짜증나", "힘들어") // 예시로 넣을 감정 리스트
        val situation =
            "학교 체육 시간에 다른 반이랑 발야구를 했어. 그런데 내가 계속 땅볼을 차서 결국 아웃되었어. 결국 우리 반이 경기에서 졌어. 어떤 남자애는 나 때문에 졌다고 말하고 다녔어." // 상황에 대한 AI 분석
        val thoughts =
            "나는 왜 잘하는 게 없을까? 내가 공을 차지 않았으면 이겼을 텐데 우리 반이 진 건 다 나 때문이야. 역시 체육 시간에는 나쁜 기억밖에 없어." // 생각에 대한 AI 분석
        val situationReason =
            "체육 시간에 발야구를 하다가 실수로 땅볼을 여러 번 차서 경기에 영향을 준 것 같았네요. ‘나는 왜 잘하는 게 없을까?’는 생각을 나타내고 있어서 옮겨졌어요. ‘너무 속상했어’는 감정을 나타내고 있어서 옮겨졌어요." // 상황 분석 이유
        val thoughtsReason =
            "내 실수로 팀에 피해를 준 것 같아 자책하고, 체육 시간이 나쁜 기억으로만 남는다고 생각했어요." // 생각 분석 이유

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
            "일반화성" to listOf(
                ThoughtData(
                    "나는 실수할 때도 있지만, 다른 부분에서는 잘하는 점이 있어.",
                    "자신의 능력을 한두 가지 실패로 일반화하지 않고, 다양한 측면에서 긍정적인 면을 찾아볼 수 있게 도와줘요.",
                    listOf("몇 가지 일로 모든 일에 부정적인", "결론을 짓게 해요"),
                    "발야구 실수라는 특정 상황만 보고, 자신의 모든 능력을 부정하며 일반화하고 있어요.",
                    "ic_planet_i",
                    "나는 왜 잘하는 게 없을까?"
                )
            ),
            "어둠성" to listOf(
                ThoughtData(
                    "체육 시간에 실수도 했지만, 즐거운 시간도 있었어.",
                    "부정적인 기억에만 초점을 맞추지 않고, 체육 시간의 다양한 경험을 떠올리며 균형 잡힌 사고를 할 수 있게 도와줘요.",
                    listOf("어떤 상황의 부정적인 면만을", "보게 해요"),
                    "체육 시간의 긍정적인 순간들은 무시한 채, 부정적인 기억만 떠올리며 자신을 단정 짓고 있어요.",
                    "ic_planet_l",
                    "역시 체육 시간에는 나쁜 기억밖에 없어."
                )
            )
        )

        // Firebase에 secondAnalysis 데이터만 저장
        analysisRef.setValue(
            mapOf(
                "thoughtSets" to thoughtSets,
                "totalCharacters" to 2, // 총 캐릭터 수
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

