package com.example.rgb4u_app

import android.util.Log
import com.google.firebase.database.*
import com.google.firebase.database.ktx.database
import com.google.firebase.ktx.Firebase

class MonthlyDistortionUpdater {

    var distortionDate: String? = null
    var saveDate: String? = null

    private val TAG = "MonthlyDistortionUpdater" // TAG 정의

    fun saveThoughtsToFirebase(userId: String, diaryId: String, diaryDate: String, currentDate: String) {
        Log.d(TAG, "saveThoughtsToFirebase 호출: userId = $userId, diaryId = $diaryId")

        distortionDate = diaryDate
        saveDate = currentDate

        // Firebase 인스턴스 가져오기
        val firebaseDatabase = FirebaseDatabase.getInstance()

        // Firebase 참조 설정
        val diaryRef = firebaseDatabase.getReference("users/$userId/diaries/$diaryId/aiAnalysis/secondAnalysis/thoughtSets")
        diaryRef.get().addOnSuccessListener { dataSnapshot ->
            if (dataSnapshot.exists()) {
                val planetThoughts = mutableMapOf<String, MutableSet<String>>() // 중복 제거를 위한 Set 사용

                for (childSnapshot in dataSnapshot.children) {
                    val planetName = childSnapshot.key.toString() // planetName을 키로 활용
                    val thoughtsList = childSnapshot.children.mapNotNull { snapshot ->
                        snapshot.child("selectedThoughts").value?.toString()
                    }

                    // 중복 제거 후 추가
                    planetThoughts[planetName] = (planetThoughts[planetName] ?: mutableSetOf()).apply {
                        addAll(thoughtsList)
                    }
                }

                // monthlyAnalysis 참조 설정
                val month = saveDate?.substring(0, 7)
                if (month == null) {
                    Log.e(TAG, "saveDate가 null입니다.")
                    return@addOnSuccessListener // `return` 대신 `return@addOnSuccessListener`를 사용하여 콜백 종료
                }

                for ((planetName, thoughtsSet) in planetThoughts) {
                    val combinedThoughts = thoughtsSet.joinToString(". ") + "." // Set을 문자열로 변환
                    val monthlyRef = firebaseDatabase.getReference("users/$userId/monthlyAnalysis/$month/$planetName")

                    // monthlyRef에서 현재 데이터 가져오기
                    monthlyRef.get().addOnSuccessListener { monthlySnapshot ->
                        val currentCount = monthlySnapshot.child("count").value?.toString()?.toIntOrNull() ?: 0
                        val currentText = monthlySnapshot.child("entries/text").value?.toString().orEmpty()

                        // 업데이트할 데이터 설정
                        val updates = hashMapOf<String, Any>(
                            "count" to (currentCount + 1),
                            "entries/text" to (currentText + " " + combinedThoughts).trim(),
                            "entries/date" to distortionDate.orEmpty()
                        )

                        monthlyRef.updateChildren(updates).addOnSuccessListener {
                            Log.d(TAG, "Thoughts successfully updated in monthly analysis")
                        }.addOnFailureListener { e ->
                            Log.e(TAG, "Failed to update thoughts in monthly analysis: ${e.message}")
                        }
                    }.addOnFailureListener { e ->
                        Log.e(TAG, "Failed to fetch monthly analysis: ${e.message}")
                    }
                }
            } else {
                Log.d(TAG, "No thought sets found for this diary entry")
            }
        }.addOnFailureListener { e ->
            Log.e(TAG, "Failed to fetch thoughts from diary entry: ${e.message}")
        }
    }
}
