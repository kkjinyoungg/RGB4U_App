package com.example.rgb4u_appclass

import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import com.google.firebase.database.FirebaseDatabase
import android.util.Log
import java.text.SimpleDateFormat
import java.util.Date
import java.util.Locale

class DiaryViewModel : ViewModel() {
    // LiveData로 상황, 생각, 감정 정보를 저장
    val situation = MutableLiveData<String>()
    val thoughts = MutableLiveData<String>()
    val emotionDegree = MutableLiveData<Int>()
    val emotionString = MutableLiveData<String>()
    val emotionTypes = MutableLiveData<List<String>>()

    // 데이터를 파이어베이스에 저장하는 함수
    fun saveDiaryToFirebase(userId: String) {
        // 파이어베이스 참조
        val database = FirebaseDatabase.getInstance().getReference("users/$userId/diaries")

        // 일기 아이디를 생성
        val diaryId = database.push().key ?: return

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
            )
        )

        // 데이터 저장
        database.child(diaryId).setValue(diaryData).addOnSuccessListener {
            Log.d("DiaryViewModel", "일기 저장 성공")
        }.addOnFailureListener {
            Log.e("DiaryViewModel", "일기 저장 실패", it)
        }
    }

    private fun getCurrentDate(): String {
        // 현재 날짜 포맷팅
        val sdf = SimpleDateFormat("yyyy-MM-dd", Locale.getDefault())
        return sdf.format(Date())
    }
}
