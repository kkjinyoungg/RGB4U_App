package com.example.rgb4u_app

import android.util.Log // Log 클래스 import
import com.google.firebase.database.* // Firebase 관련 클래스 import
import com.google.firebase.database.Transaction // Transaction import

class MonthlyStatsUpdater {

    private val firebaseDatabase: FirebaseDatabase = FirebaseDatabase.getInstance()

    private val emotions = mapOf(
        "Surprise" to listOf("어안이 벙벙한", "아찔한", "황당한", "깜짝 놀란", "움찔하는", "충격적인"),
        "Fear" to listOf("걱정스러운", "암담한", "겁나는", "무서운", "불안한", "긴장된"),
        "Sadness" to listOf("기운 없는", "슬픈", "눈물이 나는", "우울한", "비참한", "서운한"),
        "Anger" to listOf("화난", "끓어오르는", "분한", "짜증나는", "약 오른", "억울한"),
        "Disgust" to listOf("정 떨어지는", "불쾌한", "싫은", "모욕적인", "못마땅한", "미운")
    )

    // 다이어리 ID와 날짜를 기반으로 월간 통계 업데이트
    fun updateMonthlyStats(userId: String, diaryId: String, date: String, emotionTypes: List<String>) {
        val database: DatabaseReference = firebaseDatabase.getReference("users/$userId/diaries/$diaryId")

        // 월 정보를 추출
        val month = date.substring(0, 7) // "2024-09" 형태로
        val monthlyStatsRef = firebaseDatabase.getReference("users/$userId/monthlyStats/$month")

        // 로그 추가 - 월간 통계 업데이트 시작
        Log.d("MonthlyStatsUpdater", "updateMonthlyStats 호출됨: userId = $userId, diaryId = $diaryId, date = $date, emotionTypes = $emotionTypes")

        // 해당 월의 데이터가 이미 있는지 확인
        monthlyStatsRef.get().addOnSuccessListener { snapshot ->
            if (!snapshot.exists()) {
                // 데이터가 없으면 초기화
                Log.d("MonthlyStatsUpdater", "월간 데이터가 존재하지 않음, 초기화 중")
                val initialMonthlyData = mapOf(
                    "thoughtsRank" to emptyMap<String, Int>(),
                    "emotionsGraph" to mapOf(
                        "Surprise" to 0,
                        "Fear" to 0,
                        "Sadness" to 0,
                        "Anger" to 0,
                        "Disgust" to 0
                    ),
                    "SurpriseKeyword" to initializeKeywords("Surprise"),
                    "FearKeyword" to initializeKeywords("Fear"),
                    "SadnessKeyword" to initializeKeywords("Sadness"),
                    "AngerKeyword" to initializeKeywords("Anger"),
                    "DisgustKeyword" to initializeKeywords("Disgust")
                )
                monthlyStatsRef.setValue(initialMonthlyData)
                    .addOnSuccessListener {
                        Log.d("MonthlyStatsUpdater", "초기 월간 데이터 저장 성공")
                        // 초기 데이터 저장 후 감정 수치 업데이트 호출
                        updateEmotionCounts(monthlyStatsRef, emotionTypes)
                    }.addOnFailureListener {
                        Log.e("MonthlyStatsUpdater", "초기 월간 데이터 저장 실패", it)
                    }
            } else {
                // 데이터가 이미 존재하면 업데이트
                Log.d("MonthlyStatsUpdater", "월간 데이터가 이미 존재함, 감정 수치 업데이트 중")
                updateEmotionCounts(monthlyStatsRef, emotionTypes)
            }
        }.addOnFailureListener {
            Log.e("MonthlyStatsUpdater", "월간 데이터 가져오기 실패", it)
        }
    }

    // 키워드 초기화 함수
    private fun initializeKeywords(emotion: String): Map<String, Int> {
        return emotions[emotion]?.associate { it to 0 } ?: emptyMap()
    }

    // 감정 수치 업데이트 함수
    private fun updateEmotionCounts(monthlyStatsRef: DatabaseReference, emotionTypes: List<String>) {
        val emotionCountMap = mutableMapOf<String, Any>() // Map<String, Any>로 변경

        // 로그 추가 - 감정 수치 업데이트 시작
        Log.d("MonthlyStatsUpdater", "감정 수치 업데이트 중: emotionTypes = $emotionTypes")

        // 감정 유형에 따라 카운트 증가
        for (emotionType in emotionTypes) {
            for ((emotion, keywords) in emotions) {
                if (keywords.contains(emotionType)) {
                    // 감정 그래프 업데이트
                    emotionCountMap[emotion] = (emotionCountMap[emotion] as? Int ?: 0) + 1
                }
            }
        }

        // Firebase에 업데이트
        monthlyStatsRef.child("emotionsGraph").updateChildren(emotionCountMap).addOnSuccessListener {
            Log.d("MonthlyStatsUpdater", "Emotion counts updated successfully.")
        }.addOnFailureListener {
            Log.e("MonthlyStatsUpdater", "Emotion counts update failed", it)
        }

        // 키워드 카운트 업데이트
        for (emotionType in emotionTypes) {
            for ((emotion, keywords) in emotions) {
                if (keywords.contains(emotionType)) {
                    Log.d("MonthlyStatsUpdater", "키워드 업데이트 중: emotion = $emotion, emotionType = $emotionType")
                    monthlyStatsRef.child("${emotion}Keyword/$emotionType").runTransaction(object : Transaction.Handler {
                        override fun doTransaction(currentCount: MutableData): Transaction.Result {
                            val newCount = (currentCount.value as? Long ?: 0) + 1
                            currentCount.value = newCount // 새로운 값을 설정
                            return Transaction.success(currentCount) // 트랜잭션 성공 반환
                        }

                        override fun onComplete(
                            databaseError: DatabaseError?,  // 올바른 매개변수: DatabaseError
                            committed: Boolean,             // 올바른 매개변수: Boolean
                            dataSnapshot: DataSnapshot?     // 올바른 매개변수: DataSnapshot
                        ) {
                            if (databaseError != null) {
                                Log.e("MonthlyStatsUpdater", "Transaction failed: ${databaseError.message}")
                            } else if (committed) {
                                Log.d("MonthlyStatsUpdater", "Keyword count updated successfully for $emotionType.")
                            }
                        }
                    })
                }
            }
        }
    }
}
