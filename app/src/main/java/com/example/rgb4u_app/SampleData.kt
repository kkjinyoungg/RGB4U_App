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
        val emotionList = listOf("끔찍해", "너무 속상했어") // 예시로 넣을 감정 리스트
        val situation =
            "다정이랑 둘이서만 점심을 먹고 싶었는데, 갑자기 혜진이가 다가와서 셋이서 점심을 먹었어. 다정이랑 혜진이가 너무 친해 보여서 둘이 나누는 대화에 끼어들기가 어려웠어. " // 상황에 대한 AI 분석
        val thoughts =
            "다정이는 나랑 같이 다니기 싫은가 봐. 이러다가 혼자서 다니게 될 것 같아. 혜진이가 다정이에게 나를 빼고 다니자고 하면 어떡하지? " // 생각에 대한 AI 분석
        val situationReason =
            "다정이와 둘이서 점심을 먹고 싶었지만, 혜진이가 와서 셋이서 함께 식사를 했어요. ‘혜진이가 다정이에게 나를 빼고 다니자고 하면 어떡하지?’는 생각을 나타내고 있어서 옮겨졌어요. ‘너무 속상했어.’는 감정을 나타내고 있어서 옮겨졌어요." // 상황 분석 이유
        val thoughtsReason =
            "다정이와의 관계가 멀어질까 봐 걱정되고, 이러다가 혼자 다니게 될 것 같다는 생각이 들었어요. ‘끔찍해’는 감정을 나타내고 있어서 옮겨졌어요." // 생각 분석 이유

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
                    "다정이의 생각은 다정이에게 직접 물어봐야 해",
                    "다정이의 생각을 직접 확인하면, 불필요한 걱정을 줄이고 문제를 해결할 수 있어요.",
                    listOf("다른 사람의 생각을 마음대로", "넘겨짚게 해요"),
                    "실제로 다정이의 생각은 알 수 없음에도, 다정이의 마음을 추측하고 있어요. 내 마음대로 다정이가 나와 같이 다니기 싫어한다고 결론짓고 있어요.",
                    "ic_planet_h",
                    "다정이는 나랑 같이 다니기 싫은가 봐."
                )
            ),
            "재앙성" to listOf(
                ThoughtData(
                    "다정이와 계속 함께 다닐 수도 있어.",
                    "혼자 다니는 상황에 대한 과도한 걱정을 줄이고 긍정적으로 생각할 수 있어요.",
                    listOf("별다른 이유 없이 미래를 부정적으로", "생각하게 해요"),
                    "가장 나쁜 상황인 ‘혼자 다니는 일’을 상상하고 있어요. 여러가지 해결책이 있을 수 있지만 지나치게 부정적인 결과만 생각하고 있어요.",
                    "ic_planet_b",
                    "이러다가 혼자서 다니게 될 것 같아."
                ),
                ThoughtData(
                    "혜진이가 나를 빼고 다니자고 할 이유는 없을 거야.",
                    "불필요한 걱정을 없애고 친구의 행동을 과도하게 걱정하지 않을 수 있어요.",
                    listOf("별다른 이유 없이 미래를 부정적으로", "생각하게 해요"),
                    "다정이가 나를 빼고 다른 친구들과 다니게 되는 극단적인 결과를 걱정하고 있어요. 일어나지 않을 수 있는 최악의 상황을 지나치게 상상하고 있어요.",
                    "ic_planet_b",
                    "혜진이가 다정이에게 나를 빼고 다니자고 하면 어떡하지?"
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

