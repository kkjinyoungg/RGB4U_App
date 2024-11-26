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
        val emotionList = listOf("너무 창피해") // 예시로 넣을 감정 리스트
        val situation =
            "체육 시간에 발야구를 했어. 그런데 내가 공을 차다가 미끄러져서 애들이 다 보는 앞에서 넘어졌어. 어떤 남자애는 내가 넘어진 자세가 웃기다며 크게 웃었어." // 상황에 대한 AI 분석
        val thoughts =
            "애들이 나를 속으로 엄청 비웃었겠지? 다들 나를 웃음거리로 생각할 거야. 역시 체육 시간에는 나쁜 기억밖에 없어. 나는 왜 잘하는 게 하나도 없을까?" // 생각에 대한 AI 분석
        val situationReason =
            "체육 시간에 발야구를 하다가 넘어져서 한 남자애가 웃었어요. ‘나는 왜 잘하는 게 하나도 없을까?’는 생각을 나타내고 있어서 옮겨졌어요." // 상황 분석 이유
        val thoughtsReason =
            "친구들이 나를 속으로 비웃었을 것 같고, 체육 시간에는 나쁜 기억만 남아서 왜 잘하는 게 없는지 고민했어요. ‘너무 창피해.’는 감정을 나타내고 있어서 옮겨졌어요." // 생각 분석 이유

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
            "궁예성" to listOf(
                ThoughtData(
                    "친구들이 실제로 어떻게 생각하는지는 알 수 없어",
                    "이렇게 생각하면 다른 사람의 마음을 추측하지 않게 되고, 불필요한 걱정을 줄일 수 있어요.",
                    listOf("다른 사람의 생각을 마음대로", "넘겨짚게 해요"),
                    "이 생각은 다른 사람의 마음을 읽고 있어요. 친구들이 어떻게 생각하는지 알 수 없는데도 그렇게 결론짓고 있어요.",
                    "ic_planet_h",
                    "애들이 나를 속으로 엄청 비웃었겠지?"
                )
            ),
            "재앙성" to listOf(
                ThoughtData(
                    "친구들은 날 웃음거리로 보지 않고 그냥 넘어졌다고 생각할 거야",
                    "이렇게 생각하면 모두가 나를 비웃는다고 크게 걱정하지 않고, 잠깐 있었던 일을 가볍게 넘길 수 있어요.",
                    listOf("별다른 이유 없이 미래를 부정적으로", "생각하게 해요"),
                    "이 생각은 한 친구가 웃은 일을 보고 “모두가 나를 웃음거리로 생각할 거야”라고 크게 걱정하고 있어요.",
                    "ic_planet_b",
                    "다들 나를 웃음거리로 생각할 거야."
                )
            ),
            "일반화성" to listOf(
                ThoughtData(
                    "체육 시간에 좋은 기억이 있었던 날도 있었어",
                    "이렇게 말하면 긍정적인 경험을 떠올리게 되고, 앞으로의 체육 시간도 조금 더 기대할 수 있을 거예요.",
                    listOf("몇 가지 일로 모든 일에 부정적인", "결론을 짓게 해요"),
                    "한 가지 경험을 바탕으로 모든 체육 시간에 대해 부정적인 결론을 내리고 있어요.",
                    "ic_planet_i",
                    "역시 체육 시간에는 나쁜 기억밖에 없어."
                ),
                ThoughtData(
                    "한두 가지 못한다고 해서 내가 잘하는 게 없는 건 아니야",
                    "이 생각은 잘하는 부분이 있다는 것을 상기시켜 주기 때문에 자신에 대해 더 균형 있게 바라볼 수 있어요.",
                    listOf("몇 가지 일로 모든 일에 부정적인", "결론을 짓게 해요"),
                    "특정한 경험을 바탕으로 '나는 아무것도 잘하지 못해'라고 일반화하고 있어요.",
                    "ic_planet_i",
                    "나는 왜 잘하는 게 하나도 없을까?"
                )
            )
        )

        // Firebase에 secondAnalysis 데이터만 저장
        analysisRef.setValue(
            mapOf(
                "thoughtSets" to thoughtSets,
                "totalCharacters" to 3, // 총 캐릭터 수
                "totalSets" to 4 // 총 sets 수
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

