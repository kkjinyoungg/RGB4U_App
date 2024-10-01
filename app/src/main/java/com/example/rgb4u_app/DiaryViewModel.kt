package com.example.rgb4u_appclass

import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import com.google.firebase.database.FirebaseDatabase
import android.util.Log
import java.text.SimpleDateFormat
import java.util.Date
import java.util.Locale
import com.example.rgb4u_app.AiSummary //AiSummary 추가

class DiaryViewModel : ViewModel() {
    //.LiveData로 상황, 생각, 감정 정보를 저장
    val situation = MutableLiveData<String>()
    val thoughts = MutableLiveData<String>()
    val emotionDegree = MutableLiveData<Int>()
    val emotionString = MutableLiveData<String>()
    val emotionTypes = MutableLiveData<List<String>>()

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
        diaryId = database.push().key //diaryId 변수를 업데이트
        if (diaryId == null) return // diaryId가 null인 경우 return

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
                "emotionTypes" to emotionTypes.value
            ),
            "aiAnalysis" to mapOf(
                "firstAnalysis" to mapOf(
                    "situation" to "AI 분석 상황", // AI 분석 상황 (임시 데이터)
                    "thoughts" to "AI 분석 생각", // AI 분석 생각 (임시 데이터)
                    "emotion" to "AI 분석 감정", // AI 분석 감정 (임시 데이터)
                    "situationReason" to "AI 분석 이유", // AI 분석 이유 (임시 데이터)
                    "thoughtsReason" to "AI 생각 분석 이유" // AI 생각 분석 이유 (임시 데이터)
                ),
                "secondAnalysis" to mapOf(
                    "badCharacters" to listOf("캐릭터1", "캐릭터2"), // AI 분석 불안정한 캐릭터 (임시 데이터)
                    "alternativeThoughts" to "대안적 생각 문장" // 대안적 생각 문장 (임시 데이터)
                )
            ),
            "reMeasuredEmotion" to mapOf(
                "int" to 5, // 재측정된 감정 (임시 데이터)
                "string" to "보통" // 감정 상태 (임시 데이터)
            )
        )

        // 데이터 저장
        database.child(diaryId!!).setValue(diaryData)
            .addOnSuccessListener {
                Log.d("DiaryViewModel", "일기 저장 성공")
                analyzeDiaryWithAI(userId, diaryId!!) //AI 분석 호출
            }.addOnFailureListener {
                Log.e("DiaryViewModel", "일기 저장 실패", it)
            }
    }

    // AI 분석을 수행하는 함수
    private fun analyzeDiaryWithAI(userId: String, diaryId: String) {
        Log.d("DiaryViewModel", "AI 분석 호출: userId = $userId, diaryId = $diaryId") // AI 분석 호출 로그
        val aiSummary = AiSummary()

        // 콜백을 전달하여 분석이 완료된 후 onDiarySaved 호출
        aiSummary.analyzeDiary(userId, diaryId) {
            // 분석이 완료된 후 onDiarySaved 콜백 호출
            onDiarySaved?.invoke()
        }
    }

    // 현재 날짜를 "yyyy-MM-dd" 형식으로 반환하는 함수
    private fun getCurrentDate(): String {
        val dateFormat = SimpleDateFormat("yyyy-MM-dd", Locale.getDefault())
        return dateFormat.format(Date())
    }
}
