package com.example.rgb4u_appclass

import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import com.google.firebase.database.FirebaseDatabase
import android.util.Log
import java.text.SimpleDateFormat
import java.util.Date
import java.util.Locale
import com.example.rgb4u_app.AiSummary // AiSummary 추가
import com.example.rgb4u_app.AiSecond // AiSecond 추가
import com.example.rgb4u_app.MonthlyStatsUpdater
import com.google.firebase.database.ServerValue

class DiaryViewModel : ViewModel() {

    //toolbarTitle의 날짜 저장
    var diaryDate: String? = null

    // LiveData로 상황, 생각, 감정 정보를 저장
    val situation = MutableLiveData<String>()
    val thoughts = MutableLiveData<String>()
    val emotionDegree = MutableLiveData<Int>()
    val emotionString = MutableLiveData<String>()
    val emotionTypes = MutableLiveData<List<String>>()
    val monthlyStatsUpdater = MonthlyStatsUpdater()

    // 전역 변수로 diaryId를 저장
    companion object {
        var diaryId: String? = null
    }

    // 콜백 함수 추가
    var onDiarySaved: (() -> Unit)? = null

    // 데이터를 파이어베이스에 저장하는 함수
    fun saveDiaryToFirebase(userId: String) {
        // 파이어베이스 참조
        val database = FirebaseDatabase.getInstance().getReference("users/$userId/diaries")

        // 일기 아이디를 생성
        diaryId = database.push().key // diaryId 변수를 업데이트
        if (diaryId == null) {
            Log.e("DiaryViewModel", "diaryId 생성 실패")
            return // diaryId가 null인 경우 return
        }

        // 저장할 데이터 구조
        val diaryData = mapOf(
            "date" to getCurrentDate(), // 현재 날짜 추가
            "userInput" to mapOf(
                "situation" to situation.value,
                "thoughts" to thoughts.value,
                "emotionDegree" to mapOf(
                    "int" to emotionDegree.value,
                    "string" to emotionString.value
                ),
                "reMeasuredEmotionDegree" to mapOf(
                    "int" to 5, // 재측정된 감정 (임시 데이터)
                    "string" to "보통" // 감정 상태 (임시 데이터)
                ),
                "emotionTypes" to emotionTypes.value
            ),
            "aiAnalysis" to mapOf(
                "firstAnalysis" to mapOf(
                    "situation" to "", // AI 분석 상황 (빈 데이터로 수정)
                    "thoughts" to "", // AI 분석 생각 (빈 데이터로 수정)
                    "emotion" to "", // AI 분석 감정 (빈 데이터로 수정)
                    "situationReason" to "", // AI 분석 이유 (빈 데이터로 수정)
                    "thoughtsReason" to "" // AI 생각 분석 이유 (빈 데이터로 수정)
                ),
                "secondAnalysis" to mapOf(
                    "totalSets" to 0, // 세트 총 개수 (임시 데이터)
                    "totalCharacters" to 0, // 총 문자 수 (임시 데이터)
                    "thoughtSets" to mapOf(
                        "1" to mapOf(
                            "selectedThoughts" to "",
                            "characters" to mapOf(
                                "int" to 5,
                                "string" to "보통"
                            ),
                            "reasoning" to mapOf(
                                "charactersReason" to "인지적 오류 나온 이유",
                                "alternativeThoughts" to "대안적 생각 문장",
                                "alternativeThoughtsReason" to "대안적 생각 이유"
                            )
                        ),
                        "2" to mapOf(
                            "selectedThoughts" to "",
                            "characters" to mapOf(
                                "int" to 5,
                                "string" to "보통"
                            ),
                            "reasoning" to mapOf(
                                "charactersReason" to "인지적 오류 나온 이유",
                                "alternativeThoughts" to "대안적 생각 문장",
                                "alternativeThoughtsReason" to "대안적 생각 이유"
                            )
                        )
                    )
                )
            )
        )

        // 데이터 저장
        database.child(diaryId!!).setValue(diaryData)
            .addOnSuccessListener {
                Log.d("DiaryViewModel", "일기 저장 성공")
                analyzeDiaryWithAI(userId, diaryId!!) // AI 분석 호출

                // 감정 키워드 통계 상세
                val date = getCurrentDate() // 현재 날짜
                val emotionTypes = (diaryData["userInput"] as Map<String, Any>)["emotionTypes"] as? List<String> ?: emptyList()

                // 감정 유형에 대한 키워드 카운트 업데이트
                val statsRef = FirebaseDatabase.getInstance().getReference("users/$userId/stats")
                val updates = mutableMapOf<String, Any>()

                // 감정 그래프에 대한 업데이트
                val emotionGraphRef = FirebaseDatabase.getInstance().getReference("users/$userId/emotiongraph")

                for (emotionType in emotionTypes) {
                    // emotionType에 맞는 키워드를 찾아서 카운트 증가
                    val keywordList = getKeywordListForEmotion(emotionType) ?: continue
                    for (keyword in keywordList) {
                        // 정확히 매칭된 키워드만 증가시킴
                        val keywordPath = "keywords/$emotionType/$keyword" // keyword를 감정 유형에 맞게 경로 설정
                        updates[keywordPath] = ServerValue.increment(1) // 이 부분에서 키워드 카운트를 1 증가시킴
                        Log.d("DiaryViewModel", "Updating keyword count for: $keyword, path: $keywordPath")
                    }

                    // 감정 유형에 대한 카운트를 graph에 기록
                    val emotionGraphPath = "emotiongraph/$emotionType"
                    updates[emotionGraphPath] = ServerValue.increment(1) // 감정 유형의 카운트를 1 증가시킴
                    Log.d("DiaryViewModel", "Updated emotion graph for $emotionType at path: $emotionGraphPath")
                }

                statsRef.updateChildren(updates) // updates를 한 번에 반영

                monthlyStatsUpdater.updateMonthlyStats(userId, diaryId!!, getCurrentDate(), emotionTypes)

            }.addOnFailureListener {
                Log.e("DiaryViewModel", "일기 저장 실패", it)
            }
    }

    // AI 분석을 수행하는 함수
    private fun analyzeDiaryWithAI(userId: String, diaryId: String) {
        Log.d("DiaryViewModel", "AI 분석 호출: userId = $userId, diaryId = $diaryId") // AI 분석 호출 로그

        // (1) AiSummary 호출
        val aiSummary = AiSummary()
        // 콜백을 전달하여 분석이 완료된 후 onDiarySaved 호출
        aiSummary.analyzeDiary(userId, diaryId) {
            // 분석이 완료된 후 onDiarySaved 콜백 호출
            onDiarySaved?.invoke()

            // (2) AiSecond 호출
            val aiSecond = AiSecond()
            aiSecond.analyzeThoughts(userId, diaryId) {
                // AiSecond 분석 완료 후의 작업을 여기에 추가
                Log.d("DiaryViewModel", "AiSecond 분석 완료")
                // 후속 작업 추가 가능
            }
        }
    }

    // 감정 유형에 맞는 키워드 목록을 반환하는 함수
    private fun getKeywordListForEmotion(emotionType: String): List<String>? {
        return when (emotionType) {
            "Surprise" -> listOf("움찔하는", "황당한", "깜짝 놀란", "어안이 벙벙한", "아찔한", "충격적인")
            "Fear" -> listOf("걱정스러운", "긴장된", "불안한", "겁나는", "무서운", "암담한")
            "Sadness" -> listOf("기운 없는", "서운한", "슬픈", "눈물이 나는", "우울한", "비참한")
            "Anger" -> listOf("약 오른", "짜증나는", "화난", "억울한", "분한", "끓어오르는")
            "Disgust" -> listOf("정 떨어지는", "불쾌한", "싫은", "모욕적인", "못마땅한", "미운")
            else -> null
        }
    }

    // 날짜 값을 ViewModel에 설정
    fun setCurrentDate(date: String) {
        diaryDate = date
        Log.d("DiaryViewModel", "Current Date Set: $diaryDate") // 설정된 날짜 로그 추가
    }

    // 날짜 값 반환 (yyyy-MM-dd 형식)
    fun getCurrentDate(): String {
        val currentDate = diaryDate ?: SimpleDateFormat("yyyy-MM-dd", Locale.getDefault()).format(Date())
        Log.d("DiaryViewModel", "Current Date Returned: $currentDate") // 반환할 날짜 로그 추가
        return currentDate
    }
}
