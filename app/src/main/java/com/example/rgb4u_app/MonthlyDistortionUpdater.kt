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
                val planetThoughts = mutableMapOf<String, MutableList<Pair<String, String>>>() // text와 date를 저장할 List 사용

                for (childSnapshot in dataSnapshot.children) {
                    val planetName = childSnapshot.key.toString() // planetName을 키로 활용

                    // thoughtsList를 Pair로 만들어서 저장
                    childSnapshot.children.forEach { thoughtSnapshot ->
                        val selectedThoughts = thoughtSnapshot.child("selectedThoughts").value?.toString().orEmpty()
                        planetThoughts[planetName] = (planetThoughts[planetName] ?: mutableListOf()).apply {
                            add(Pair(selectedThoughts, distortionDate ?: ""))
                        }
                    }
                }

                // monthlyAnalysis 참조 설정
                val month = saveDate?.substring(0, 7)
                if (month == null) {
                    Log.e(TAG, "saveDate가 null입니다.")
                    return@addOnSuccessListener // `return` 대신 `return@addOnSuccessListener`를 사용하여 콜백 종료
                }

                for ((planetName, thoughtsList) in planetThoughts) {
                    val monthlyRef = firebaseDatabase.getReference("users/$userId/monthlyAnalysis/monthlyDistortions/$month/$planetName")

                    // monthlyRef에서 현재 데이터 가져오기
                    monthlyRef.get().addOnSuccessListener { monthlySnapshot ->
                        val currentCount = monthlySnapshot.child("count").value?.toString()?.toIntOrNull() ?: 0
                        val currentEntries = monthlySnapshot.child("entries").children.mapNotNull {
                            val text = it.child("text").value?.toString().orEmpty()
                            val date = it.child("date").value?.toString().orEmpty()
                            if (text.isNotEmpty() && date.isNotEmpty()) {
                                mapOf("text" to text, "date" to date)
                            } else {
                                null
                            }
                        }.toMutableList()

                        // 새로운 생각들을 entries에 추가
                        currentEntries.addAll(thoughtsList.map { mapOf("text" to it.first, "date" to it.second) })

                        // 업데이트할 데이터 설정
                        val updates = hashMapOf<String, Any>(
                            "count" to (currentCount + 1),
                            "entries" to currentEntries
                        )

                        monthlyRef.setValue(updates).addOnSuccessListener {
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
