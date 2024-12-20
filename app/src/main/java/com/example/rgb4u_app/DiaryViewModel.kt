package com.example.rgb4u_appclass

import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import android.util.Log
import java.text.SimpleDateFormat
import java.util.Date
import java.util.Locale
import com.example.rgb4u_app.AiSummary // AiSummary 추가
import com.example.rgb4u_app.AiSecond // AiSecond 추가
import com.example.rgb4u_app.SampleData //SampleData 추가
import com.example.rgb4u_app.SampleData2 //SampleData 추가
import com.example.rgb4u_app.MonthlyStatsUpdater
import com.google.firebase.database.ServerValue
import com.example.rgb4u_app.MonthlyDistortionUpdater
import android.os.Handler
import android.os.Looper
import com.google.firebase.database.FirebaseDatabase
import com.google.firebase.database.DatabaseReference
import com.google.firebase.database.Transaction
import com.google.firebase.database.MutableData
import com.google.firebase.database.DatabaseError
import com.google.firebase.database.DataSnapshot

class DiaryViewModel : ViewModel() {

    //yyyy-mm-dd날짜 저장
    var diaryDate: String? = null

    // LiveData로 상황, 생각, 감정 정보를 저장
    val situation = MutableLiveData<String>()
    val thoughts = MutableLiveData<String>()
    val emotionDegree = MutableLiveData<Int>()
    val emotionString = MutableLiveData<String>()
    val emotionImage = MutableLiveData<String>()
    val toolBarDate = MutableLiveData<String>() //월일요일
    val emotionTypes = MutableLiveData<List<String>>()
    val monthlyStatsUpdater = MonthlyStatsUpdater()

    // 전역 변수로 diaryId를 저장
    companion object {
        var diaryId: String? = null
    }

    // 콜백 함수 추가
    var onDiarySaved: (() -> Unit)? = null

    fun loadDiaryFromFirebase(userId: String, formattedDate: String, onSituationLoaded: (String) -> Unit) {
        val databaseRef = FirebaseDatabase.getInstance()
            .getReference("users/$userId/diaries/$formattedDate/userInput")

        databaseRef.get().addOnSuccessListener { snapshot ->
            if (snapshot.exists()) {
                // 데이터가 존재할 경우 각 필드 처리
                val situationValue = snapshot.child("situation").getValue(String::class.java) ?: ""
                val thoughtsValue = snapshot.child("thoughts").getValue(String::class.java) ?: ""
                val emotionDegreeValue = snapshot.child("emotionDegree/int").getValue(Int::class.java) ?: 0

                val emotionTypesValue = snapshot.child("emotionTypes").getValue() as? List<String> ?: emptyList()

                // ViewModel 필드 업데이트
                situation.value = situationValue
                thoughts.value = thoughtsValue
                emotionDegree.value = emotionDegreeValue
                emotionTypes.value = emotionTypesValue

                // 상황 값 반환
                onSituationLoaded(situationValue)
            } else {
                // 데이터가 없을 경우 빈 값으로 초기화
                situation.value = ""
                thoughts.value = ""
                emotionDegree.value = 0
                emotionTypes.value = emptyList()

                // 빈 상황 값 반환
                onSituationLoaded("")
            }
        }.addOnFailureListener { e ->
            Log.e("DiaryViewModel", "Firebase 데이터 로드 실패: ${e.message}")

            // 실패 시에도 기본값 반환
            situation.value = ""
            thoughts.value = ""
            emotionDegree.value = 0
            emotionTypes.value = emptyList()

            onSituationLoaded("")
        }
    }

    // 데이터를 파이어베이스에 저장하는 함수
    fun saveDiaryToFirebase(userId: String) {
        // 날짜를 가져옵니다.
        val currentDate = getCurrentDate()

        // 감정 상태 로그 추가
        Log.d("DiaryViewModel", "저장되는 감정 상태: ${emotionString.value}")

        // 파이어베이스 참조
        val database = FirebaseDatabase.getInstance()
            .getReference("users/$userId/diaries/$currentDate")

        // 일기 아이디를 생성
        diaryId = database.push().key // diaryId 변수를 업데이트
        if (diaryId == null) {
            Log.e("DiaryViewModel", "diaryId 생성 실패")
            return // diaryId가 null인 경우 return
        }

        val diaryData = mapOf(
            "diaryId" to diaryId, // diaryId 추가 //
            "savingstatus" to "save", // savingstatus 추가 //
            "readingstatus" to "null", // readingstatus 추가 //
            "toolbardate" to toolBarDate.value, // 월일요일 형식 날짜
            "userInput" to mapOf(
                "situation" to situation.value,
                "thoughts" to thoughts.value,
                "emotionDegree" to mapOf(
                    "int" to emotionDegree.value,
                    "string" to emotionString.value,
                    "emotionimg" to emotionImage.value
                ),
                "reMeasuredEmotionDegree" to mapOf(
                    "int" to -1, // 재측정된 감정 (임시 데이터)
                    "string" to "", // 감정 상태 (임시 데이터)
                    "emotionimg" to "" // 이미지 상태 (임시 데이터)
                ),
                "emotionTypes" to emotionTypes.value // 감정 유형
            ),
            "aiAnalysis" to mapOf(
                "firstAnalysis" to mapOf(
                    "situation" to "", // AI 분석 상황 (빈 데이터로 수정)
                    "thoughts" to "", // AI 분석 생각 (빈 데이터로 수정)
                    "emotion" to listOf<String>(), // AI 분석 감정 (빈 데이터로 수정)
                    "situationReason" to "", // AI 분석 이유 (빈 데이터로 수정)
                    "thoughtsReason" to "" // AI 생각 분석 이유 (빈 데이터로 수정)
                ),
                "secondAnalysis" to mapOf(
                    "totalSets" to 0, // 세트 총 개수 (임시 데이터)
                    "totalCharacters" to 0, // 총 문자 수 (임시 데이터)
                    "thoughtSets" to mapOf(
                        "이름성" to listOf( // 리스트로 변경
                            mapOf(
                                "alternativeThoughts" to "",
                                "alternativeThoughtsReason" to "",
                                "characterDescription" to listOf(
                                    "",
                                    ""
                                ),
                                "charactersReason" to "",
                                "imageResource" to "",
                                "selectedThoughts" to ""
                            )
                        )
                    )
                )
            )
        )

        // 데이터 저장
        database.setValue(diaryData)
            .addOnSuccessListener {
                Log.d("DiaryViewModel", "일기 저장 성공: $diaryId")
                analyzeDiaryWithAI(userId, diaryId!!, getCurrentDate()) // AI 분석 호출
                Log.d("DiaryViewModel", "toolBarDate: ${toolBarDate.value}")

                // 감정 키워드 통계 상세
                val date = getCurrentDate() // 현재 날짜
                val yyyymmdate = date.substring(0, 7) // "2024-09" 형태로
                val emotionTypes = (diaryData["userInput"] as Map<String, Any>)["emotionTypes"] as? List<String> ?: emptyList()

                // 감정 유형에 대한 키워드 카운트 업데이트
                val statsRef = FirebaseDatabase.getInstance().getReference("users/$userId/monthlyStats/$yyyymmdate")
                val updates = mutableMapOf<String, Any>()

                Log.d("DiaryViewModel", "현재 감정 유형: $emotionTypes")

                // 감정 유형이 비어있지 않다면 실행
                if (emotionTypes.isNotEmpty()) {
                    // 감정 유형들을 순회하면서 처리
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
                        val emotionGraphRef = FirebaseDatabase.getInstance().getReference(emotionGraphPath)

                        emotionGraphRef.runTransaction(object : Transaction.Handler {
                            override fun doTransaction(currentData: MutableData): Transaction.Result {
                                val currentValue = currentData.value as? Long ?: 0
                                currentData.value = currentValue + 1 // 기존 값에 1을 더함
                                return Transaction.success(currentData) // 트랜잭션 성공 반환
                            }

                            override fun onComplete(databaseError: DatabaseError?, committed: Boolean, dataSnapshot: DataSnapshot?) {
                                if (databaseError != null) {
                                    Log.e("DiaryViewModel", "Transaction failed: ${databaseError.message}")
                                } else {
                                    Log.d("DiaryViewModel", "Updated emotion graph for $emotionType")
                                }
                            }
                        })
                    }

                    // 감정 유형 업데이트 적용
                    statsRef.updateChildren(updates) // updates를 한 번에 반영
                } else {
                    Log.w("DiaryViewModel", "emotionTypes 리스트가 비어 있습니다.")
                }

                // 월간 통계 업데이트
                monthlyStatsUpdater.updateMonthlyStats(
                    userId,
                    diaryId!!,
                    yyyymmdate,
                    emotionTypes
                ) // 월간 통계 업데이트 호출

            }.addOnFailureListener {
                Log.e("DiaryViewModel", "일기 저장 실패", it)
            }
    }


    // AI 분석을 수행하는 함수
    private fun analyzeDiaryWithAI(userId: String, diaryId: String, diaryDate: String) {
        Log.d("DiaryViewModel", "AI 분석 호출: userId = $userId, diaryId = $diaryId, diaryDate = $diaryDate")

        if (diaryDate == "2024-12-01") {
            // diaryDate가 "2024-11-28"
            Log.d("DiaryViewModel", "AI 분석을 수행하지 않음, diaryDate = 2024-11-28")
            val sampledata = SampleData()
            sampledata.fillingsummary(userId, diaryId, getCurrentDate()) {
                // Optional callback code after the data is saved (empty for now)
                Log.d("SampleData", "fillingsummary is completed.")
            }

            updateReadingStatus1(userId)  // readingStatus 업데이트

            // 3초 후에 onDiarySaved 호출
            Handler(Looper.getMainLooper()).postDelayed({
                onDiarySaved?.invoke()
                Log.d("DiaryViewModel", "onDiarySaved 호출 완료")
            }, 3000) // 3000 milliseconds = 3 seconds


            sampledata.fillinganalysis(userId, diaryId, getCurrentDate()) {
                // Optional callback code after the data is saved (empty for now)
                Log.d("SampleData", "fillinganalysis is completed.")

                // (3) saveThoughtsToFirebase 호출
                val monthlyUpdater = MonthlyDistortionUpdater()
                monthlyUpdater.saveThoughtsToFirebase(userId, diaryId, diaryDate, getCurrentDate())
                Log.d("DiaryViewModel", "왜곡 통계 저장 완료")

                updateReadingStatus2(userId)  // readingStatus 업데이트
            }
        } else if (diaryDate == "2024-11-01") {
            // diaryDate가 "2024-11-11"일 경우 처리하지 않음
            Log.d("DiaryViewModel", "AI 분석을 수행하지 않음, diaryDate = 2024-11-11")
            val sampledata2 = SampleData2()
            sampledata2.fillingsummary(userId, diaryId, getCurrentDate()) {
                // Optional callback code after the data is saved (empty for now)
                Log.d("SampleData2", "fillingsummary is completed.")
            }

            updateReadingStatus1(userId)  // readingStatus 업데이트

            // 3초 후에 onDiarySaved 호출
            Handler(Looper.getMainLooper()).postDelayed({
                onDiarySaved?.invoke()
                Log.d("DiaryViewModel", "onDiarySaved 호출 완료")
            }, 3000) // 3000 milliseconds = 3 seconds


            sampledata2.fillinganalysis(userId, diaryId, getCurrentDate()) {
                // Optional callback code after the data is saved (empty for now)
                Log.d("SampleData2", "fillinganalysis is completed.")

                // (3) saveThoughtsToFirebase 호출
                val monthlyUpdater = MonthlyDistortionUpdater()
                monthlyUpdater.saveThoughtsToFirebase(userId, diaryId, diaryDate, getCurrentDate())
                Log.d("DiaryViewModel", "왜곡 통계 저장 완료")

                updateReadingStatus2(userId)  // readingStatus 업데이트
            }
        }
        else {
            // (1) AiSummary 호출
            val aiSummary = AiSummary()
            aiSummary.analyzeDiary(userId, diaryId, getCurrentDate()) {
                Log.d("DiaryViewModel", "AiSummary 분석 완료")
                updateReadingStatus1(userId)  // readingStatus 업데이트

                // 분석 완료 후 onDiarySaved 호출
                onDiarySaved?.invoke()  // 여기에 이동

                // (2) AiSecond 호출
                val aiSecond = AiSecond()
                aiSecond.analyzeThoughts(userId, diaryId, getCurrentDate()) {
                    // 실패 시 재시도 로직을 포함시킬 필요가 있을 수 있으므로
                    try {
                        Log.d("DiaryViewModel", "AiSecond 분석 완료")

                        // (3) saveThoughtsToFirebase 호출
                        val monthlyUpdater = MonthlyDistortionUpdater()
                        monthlyUpdater.saveThoughtsToFirebase(userId, diaryId, diaryDate, getCurrentDate())
                        Log.d("DiaryViewModel", "왜곡 통계 저장 완료")

                        updateReadingStatus2(userId)  // readingStatus 업데이트
                    } catch (e: Exception) {
                        Log.e("DiaryViewModel", "AiSecond 분석 실패, 다시 시도합니다.")
                        // 실패 시 재시도 로직
                        retryAiSecond(userId, diaryId, diaryDate)
                    }
                }
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
        // diaryDate가 설정되어 있으면 그 값을 반환하고, 없으면 현재 날짜를 설정
        if (diaryDate == null) {
            diaryDate = SimpleDateFormat("yyyy-MM-dd", Locale.getDefault()).format(Date())
            Log.d("DiaryViewModel", "Current Date Set in Get: $diaryDate") // 설정된 날짜 로그 추가
        }

        Log.d("DiaryViewModel", "Current Date Returned: $diaryDate") // 반환할 날짜 로그 추가
        return diaryDate!!
    }

    // readingStatus를 "unread"로 업데이트하는 함수
    private fun updateReadingStatus2(userId: String) {
        val currentDate = getCurrentDate()
        val database = FirebaseDatabase.getInstance()
            .getReference("users/$userId/diaries/$currentDate/readingstatus")

        database.setValue("unread").addOnSuccessListener {
            Log.d("DiaryViewModel", "readingStatus 분석완료 성공: unread for date $currentDate")
        }.addOnFailureListener { exception ->
            Log.e("DiaryViewModel", "readingStatus 분석완료 실패", exception)
        }
    }

    // readingStatus를 "unread"로 업데이트하는 함수
    private fun updateReadingStatus1(userId: String) {
        val currentDate = getCurrentDate()
        val database = FirebaseDatabase.getInstance()
            .getReference("users/$userId/diaries/$currentDate/readingstatus")

        database.setValue("load").addOnSuccessListener {
            Log.d("DiaryViewModel", "readingStatus 분석중: unread for date $currentDate")
        }.addOnFailureListener { exception ->
            Log.e("DiaryViewModel", "readingStatus 분석중 실패", exception)
        }
    }

    // 임시저장용 함수
    fun saveTemporaryDiaryToFirebase(userId: String) {
        // 날짜를 가져옵니다.
        val currentDate = getCurrentDate()

        // 파이어베이스 참조
        val database = FirebaseDatabase.getInstance()
            .getReference("users/$userId/diaries/$currentDate")

        // 일기 아이디를 생성
        diaryId = database.push().key // diaryId 변수를 업데이트
        if (diaryId == null) {
            Log.e("DiaryViewModel", "diaryId 생성 실패")
            return // diaryId가 null인 경우 return
        }
        Log.d("DiaryViewModel", "situation.value: ${situation.value}")

        val diaryData = mapOf(
            "diaryId" to diaryId, // diaryId 추가 //
            "savingstatus" to "temp", // savingstatus 추가 //
            "readingstatus" to "null", // readingstatus 추가 //
            "toolbardate" to toolBarDate.value, // 월일요일 형식 날짜
            "userInput" to mapOf(
                "situation" to situation.value,
                "thoughts" to thoughts.value,
                "emotionDegree" to mapOf(
                    "int" to emotionDegree.value,
                    "string" to emotionString.value,
                    "emotionimg" to emotionImage.value
                ),
                "reMeasuredEmotionDegree" to mapOf(
                    "int" to -1, // 재측정된 감정 (임시 데이터)
                    "string" to "", // 감정 상태 (임시 데이터)
                    "emotionimg" to "" // 이미지 상태 (임시 데이터)
                ),
                "emotionTypes" to emotionTypes.value
            ),
            "aiAnalysis" to mapOf(
                "firstAnalysis" to mapOf(
                    "situation" to "", // AI 분석 상황 (빈 데이터로 수정)
                    "thoughts" to "", // AI 분석 생각 (빈 데이터로 수정)
                    "emotion" to listOf<String>(), // AI 분석 감정 (빈 데이터로 수정)
                    "situationReason" to "", // AI 분석 이유 (빈 데이터로 수정)
                    "thoughtsReason" to "" // AI 생각 분석 이유 (빈 데이터로 수정)
                ),
                "secondAnalysis" to mapOf(
                    "totalSets" to 0, // 세트 총 개수 (임시 데이터)
                    "totalCharacters" to 0, // 총 문자 수 (임시 데이터)
                    "thoughtSets" to mapOf(
                        "이름성" to listOf( // 리스트로 변경
                            mapOf(
                                "alternativeThoughts" to "",
                                "alternativeThoughtsReason" to "",
                                "characterDescription" to listOf(
                                    "",
                                    ""
                                ),
                                "charactersReason" to "",
                                "imageResource" to "",
                                "selectedThoughts" to ""
                            )
                        )
                    )
                )
            )
        )

        // 데이터 저장
        database.setValue(diaryData)
            .addOnSuccessListener {
                Log.d("DiaryViewModel", "일기 저장 성공: $diaryId")
            }.addOnFailureListener {
                Log.e("DiaryViewModel", "일기 저장 실패", it)
            }
    }

    fun retryAiSecond(userId: String, diaryId: String, diaryDate: String) {
        val aiSecond = AiSecond()
        aiSecond.analyzeThoughts(userId, diaryId, getCurrentDate()) {
            try {
                Log.d("DiaryViewModel", "AiSecond 분석 완료 (재시도)")

                val monthlyUpdater = MonthlyDistortionUpdater()
                monthlyUpdater.saveThoughtsToFirebase(userId, diaryId, diaryDate, getCurrentDate())
                Log.d("DiaryViewModel", "왜곡 통계 저장 완료 (재시도)")
                updateReadingStatus2(userId)
            } catch (e: Exception) {
                Log.e("DiaryViewModel", "AiSecond 분석 실패 (재시도 시에도 실패)")
            }
        }
    }
}


