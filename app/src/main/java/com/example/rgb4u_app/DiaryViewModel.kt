package com.example.rgb4u_app

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
        database.child(diaryId).setValue(diaryData)
            .addOnSuccessListener {
                Log.d("DiaryViewModel", "일기 저장 성공")
            }
            .addOnFailureListener {
                Log.e("DiaryViewModel", "일기 저장 실패", it)
            }
    }
}
