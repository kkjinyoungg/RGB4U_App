package com.example.rgb4u_app

import android.util.Log
import com.google.firebase.database.DatabaseReference
import com.google.firebase.database.FirebaseDatabase
import okhttp3.Call
import okhttp3.Callback
import okhttp3.MediaType.Companion.toMediaTypeOrNull
import okhttp3.OkHttpClient
import okhttp3.Request
import okhttp3.RequestBody.Companion.toRequestBody
import okhttp3.Response
import org.json.JSONArray
import org.json.JSONException
import org.json.JSONObject
import java.io.IOException
import java.text.SimpleDateFormat
import java.util.Calendar
import java.util.Locale
import java.util.Date

class AiSecond {

    private val firebaseDatabase: FirebaseDatabase = FirebaseDatabase.getInstance() // Firebase 인스턴스 초기화
    private val client = OkHttpClient() // OkHttpClient 인스턴스 초기화
    private val apiKey = ""// API 키 설정 (따옴표 안에 키 넣기)
    private val TAG = "AiSecond" // 로깅 태그
    // getCurrentDate() 값을 저장할 변수
    private var currentDate: String = ""  // null을 허용하지 않음

    // 12가지 인지 왜곡 유형 리스트
    private val cognitiveDistortions = listOf(
        "전부 아니면 전무의 사고", "재앙화", "긍정적인 면의 평가절하", "감정적 추론", "명명하기",
        "과장 및 축소", "정신적 여과", "독심술", "지나친 일반화", "자기 탓", "당위 진술", "터널 시야"
    )

    private val distortionMap = mapOf(
        "전부 아니면 전무의 사고" to "흑백성", "재앙화" to "재앙성", "긍정적인 면의 평가절하" to "외면성",
        "감정적 추론" to "느낌성", "명명하기" to "이름성", "과장 및 축소" to "과장성", "정신적 여과" to "부분성",
        "독심술" to "궁예성", "지나친 일반화" to "일반화성", "자기 탓" to "내탓성", "당위 진술" to "해야해성", "터널 시야" to "어둠성"
    )

    fun analyzeThoughts(userId: String, diaryId: String, currentDate: String, callback: () -> Unit) {
        Log.d(TAG, "analyzeThoughts 호출: userId = $userId, diaryId = $diaryId")

        this.setCurrentDate(currentDate)  // currentDate 저장

        val analysisRef: DatabaseReference = firebaseDatabase.getReference("users/$userId/diaries/$currentDate/aiAnalysis/firstAnalysis/thoughts")

        analysisRef.get().addOnSuccessListener { dataSnapshot ->
            Log.d(TAG, "Firebase에서 thoughts 데이터 가져오기 성공")
            val thoughts = dataSnapshot.value.toString()
            val sentences = splitSentences(thoughts)

            val results = mutableListOf<JSONObject>()
            var processedCount = 0

            for (sentence in sentences) {
                Log.d(TAG, "생각 분석 중: $sentence")
                analyzeCognitiveDistortions(sentence) { apiResponse ->
                    results.add(apiResponse)
                    processedCount++

                    // 모든 생각 분석이 완료된 경우
                    if (processedCount == sentences.size) {
                        val filteredResults = filterResults(results)
                        Log.d(TAG, "결과 필터링 완료, 필터링된 결과 수: ${filteredResults.size}")

                        // 필터링 결과가 비어 있을 때의 처리
                        if (filteredResults.isEmpty()) {
                            handleEmptyResults(userId, diaryId, callback)
                        } else {
                            // 필터링된 결과가 존재할 경우
                            val secondAnalysis = createSecondAnalysis(filteredResults)
                            //저장 함수
                            saveSecondAnalysis(userId, diaryId, currentDate, secondAnalysis, callback)
                        }
                    }
                }
            }
        }.addOnFailureListener { e ->
            Log.e(TAG, "Failed to get thoughts from Firebase: ${e.message}")
        }
    }

    private fun splitSentences(thoughts: String): List<String> {
        return thoughts.split(Regex("[.!?]\\s+")).map { it.trim() }.filter { it.isNotEmpty() }
    }

    private fun analyzeCognitiveDistortions(sentence: String, callback: (JSONObject) -> Unit) {
        Log.d(TAG, "인지 왜곡 분석 요청: $sentence")
        val prompt = """
        다음 생각이 12가지 인지 왜곡 유형 중 하나에 해당하는지 판단해주세요.
        
        ${cognitiveDistortions.joinToString("\n")}
        
        생각은: "$sentence" // 여기에 생각이 들어가야 해요.
        
        만약 이 생각이 인지 왜곡에 해당하지 않는다면, JSON 형식으로 아래와 같이 제시해주세요:
        {
            "유형": null, // 인지 왜곡에 해당하지 않으면 "유형" 필드를 null로 넣어주세요.
            "생각": "$sentence",
            "유형 이유": null, // "유형"이 null이면 "유형 이유"도 null로 해주세요.
            "대안적 생각": null, // 대안적 생각이 필요하지 않으면 null로 해주세요.
            "대안적 생각 이유": null // 대안적 생각 이유도 null로 해주세요.
        }

        만약 이 생각이 인지 왜곡에 해당한다면, 아래와 같은 형식으로 제시해주세요:
        {
            "유형": "유형 이름", // 인지 왜곡에 해당하는 유형을 적어주세요.
            "생각": "$sentence",
            "유형 이유": "이 생각이 이 유형에 해당하는 이유를 짧은 한 두 생각으로 작성해주세요. 말투는 ~해요체이고, 친절하고 다정하게 설명해 주세요. 초등학생도 이해할 수 있도록 쉬운 단어와 생각으로 자연스럽게 작성해 주세요.",
            "대안적 생각": "이 생각 대신 하면 좋은 적응적인 생각을 짧은 한 생각으로 작성해주세요. 반말로, '나는 ~했어'와 같은 형태로 자연스럽게 표현해 주세요.",
            "대안적 생각 이유": "이 대안적 생각을 추천한 이유를 짧은 한 두 생각으로 작성해주세요. '이렇게 생각하면'으로 시작해 주세요. 말투는 ~해요체이고, 친절하고 다정하게 설명해 주세요. 초등학생도 이해할 수 있도록 쉬운 단어와 생각으로 자연스럽게 작성해 주세요."
        }
        """.trimIndent()

        val mediaType = "application/json; charset=utf-8".toMediaTypeOrNull()
        val requestBody = JSONObject().apply {
            put("model", "gpt-3.5-turbo")
            put("messages", JSONArray().put(JSONObject().apply {
                put("role", "user")
                put("content", prompt)
            }))
        }
        val body = requestBody.toString().toRequestBody(mediaType)
        val request = Request.Builder()
            .url("https://api.openai.com/v1/chat/completions")
            .post(body)
            .addHeader("Authorization", "Bearer $apiKey")
            .build()

        client.newCall(request).enqueue(object : Callback {
            override fun onFailure(call: Call, e: IOException) {
                Log.e(TAG, "API 요청 실패: ${e.message}")
            }

            override fun onResponse(call: Call, response: Response) {
                response.use {
                    if (response.isSuccessful) {
                        Log.d(TAG, "API 요청 성공")
                        val jsonResponseString = response.body?.string() ?: ""
                        Log.d(TAG, "API 응답: $jsonResponseString")

                        try {
                            val json = JSONObject(jsonResponseString)
                            if (json.has("choices") && json.getJSONArray("choices").length() > 0) {
                                val choice = json.getJSONArray("choices").getJSONObject(0)
                                if (choice.has("message")) {
                                    val message = choice.getJSONObject("message")
                                    val content = message.getString("content")

                                    try {
                                        val contentJson = JSONObject(content) // content를 JSON으로 변환
                                        Log.d(TAG, "API 응답 JSON: $contentJson")

                                        // "유형"이 없는 경우 처리
                                        val type = contentJson.optString("유형")
                                        if (type == "null" || type.isEmpty()) {
                                            // 유형이 "null" 문자열이거나 빈 문자열이면 null 처리
                                            val noTypeResponse = JSONObject().apply {
                                                put("유형", JSONObject.NULL)
                                                put("생각", sentence)
                                                put("유형 이유", JSONObject.NULL)
                                                put("대안적 생각", JSONObject.NULL)
                                                put("대안적 생각 이유", JSONObject.NULL)
                                            }
                                            callback(noTypeResponse) // 콜백으로 응답 전달
                                        } else {
                                            // 유형이 distortionMap에 맞게 변환
                                            val mappedType = distortionMap[type] ?: JSONObject.NULL // 변환된 유형이 distortionMap에 없으면 null 처리

                                            // 모든 필요한 필드 추출
                                            val responseObj = JSONObject().apply {
                                                put("유형", mappedType) // 변환된 유형을 저장
                                                put("생각", contentJson.optString("생각", "생각 없음"))
                                                put("유형 이유", contentJson.optString("유형 이유", "이유 없음"))
                                                put("대안적 생각", contentJson.optString("대안적 생각", "대안 없음"))
                                                put("대안적 생각 이유", contentJson.optString("대안적 생각 이유", "이유 없음"))
                                            }

                                            callback(responseObj) // 콜백으로 응답 전달
                                        }
                                    } catch (e: JSONException) {
                                        Log.e(TAG, "content 파싱 오류: ${e.message}")
                                        val errorResponse = JSONObject().apply {
                                            put("유형", JSONObject.NULL)
                                            put("생각", sentence)
                                            put("유형 이유", JSONObject.NULL)
                                            put("대안적 생각", JSONObject.NULL)
                                            put("대안적 생각 이유", JSONObject.NULL)
                                        }
                                        callback(errorResponse) // 오류가 발생했을 때의 처리
                                    }
                                } else {
                                    Log.d(TAG, "message가 없습니다.")
                                }
                            } else {
                                Log.d(TAG, "choices가 없습니다.")
                            }
                        } catch (e: JSONException) {
                            Log.e(TAG, "JSON 파싱 오류: ${e.message}")
                        }
                    } else {
                        Log.e(TAG, "API 응답이 성공적이지 않음: ${response.message}")
                    }
                }
            }
        })
    }




    private fun filterResults(results: List<JSONObject>): Map<String, List<JSONObject>> {
        val filteredResults = mutableMapOf<String, MutableList<JSONObject>>()

        for (result in results) {
            val type = result.optString("유형") // 기본적으로 빈 문자열로 처리됨

            // "유형"이 null이 아니고 빈 문자열이 아닌 경우에만 추가
            if (type != null && type.isNotEmpty()) {
                // 유형이 이미 3개 이상이면 추가하지 않음
                if (filteredResults.size < 3) {
                    filteredResults[type] = filteredResults.getOrDefault(type, mutableListOf()).apply {
                        if (size < 3) add(result)
                    }
                }
            }
        }
        return filteredResults
    }


    private fun createSecondAnalysis(filteredResults: Map<String, List<JSONObject>>): Map<String, Any> {
        val thoughtSets = mutableMapOf<String, Any>()
        var totalSets = 0
        var totalCharacters = filteredResults.size

        // 각 인지 왜곡 유형에 대한 설명
        val characterDescriptions = mapOf(
            "흑백성" to listOf("모든 일을 두 가지로만 나눠서", "생각하게 해요"),
            "재앙성" to listOf("별다른 이유 없이 미래를 부정적으로", "생각하게 해요"),
            "외면성" to listOf("자신의 좋은 면을 못 보고 스스로를", "낮춰 생각하게 해요"),
            "느낌성" to listOf("자신의 느낌이 틀림없는 사실이라고", "생각하게 해요"),
            "이름성" to listOf("자신이나 다른 사람에게 부정적인 결론이", "담긴 이름을 붙이게 해요"),
            "과장성" to listOf("부정적인 것은 훨씬 크게, 긍정적인 것은", "훨씬 작게 생각하게 해요"),
            "부분성" to listOf("전체가 아닌 한 가지 작은 부분을", "계속 생각하게 해요"),
            "궁예성" to listOf("다른 사람의 생각을 마음대로", "넘겨짚게 해요"),
            "일반화성" to listOf("몇 가지 일로 모든 일에 부정적인", "결론을 짓게 해요"),
            "내탓성" to listOf("모든 일을 자신의 탓으로", "돌리게 해요"),
            "해야해성" to listOf("자신이나 다른 사람이 반드시 어떠해야", "한다고 정해두게 해요"),
            "어둠성" to listOf("어떤 상황의 부정적인 면만을", "보게 해요")
        )

        // 각 인지 왜곡 유형에 따른 이미지 리소스 매핑
        val imageResources = mapOf(
            "흑백성" to "ic_planet_a",
            "재앙성" to "ic_planet_b",
            "외면성" to "ic_planet_c",
            "느낌성" to "ic_planet_d",
            "이름성" to "ic_planet_e",
            "과장성" to "ic_planet_f",
            "부분성" to "ic_planet_g",
            "궁예성" to "ic_planet_h",
            "일반화성" to "ic_planet_i",
            "내탓성" to "ic_planet_j",
            "해야해성" to "ic_planet_k",
            "어둠성" to "ic_planet_l"
        )

        for ((type, thoughts) in filteredResults) {
            val thoughtSet = mutableListOf<Any>()
            thoughts.take(3).forEach { thought ->
                thoughtSet.add(mapOf<String, Any>(
                    "selectedThoughts" to thought.optString("생각", "Unknown"),
                    "charactersReason" to thought.optString("유형 이유", ""),
                    "alternativeThoughts" to thought.optString("대안적 생각", ""),
                    "alternativeThoughtsReason" to thought.optString("대안적 생각 이유", ""),
                    "characterDescription" to (characterDescriptions[type] ?: listOf("설명이 없습니다.")),
                    "imageResource" to (imageResources[type] ?: "ic_planet_a")
                ))
            }
            thoughtSets[type] = thoughtSet
            totalSets += thoughtSet.size
        }

        return mapOf(
            "totalSets" to totalSets,
            "totalCharacters" to totalCharacters,
            "thoughtSets" to thoughtSets
        )
    }

    private fun saveSecondAnalysis(userId: String, diaryId: String, currentDate: String, secondAnalysis: Map<String, Any>, callback: () -> Unit) {
        val analysisRef = firebaseDatabase.getReference("users/$userId/diaries/$currentDate/aiAnalysis/secondAnalysis")

        // secondAnalysis의 내용을 로그로 출력
        Log.d(TAG, "저장할 secondAnalysis 데이터: $secondAnalysis")

        analysisRef.setValue(secondAnalysis).addOnSuccessListener {
            Log.d(TAG, "두 번째 분석 결과 저장 성공")

            // DistortionTypeFiller 인스턴스 생성 및 초기화
            val distortionTypeFiller = DistortionTypeFiller()
            distortionTypeFiller.initialize(userId, currentDate) // 사용자 ID와 다이어리 ID를 전달하여 초기화
            Log.d("AiSecond", "filler 부름 userId: $userId, diaryId: $currentDate")

            callback()
        }.addOnFailureListener { e ->
            Log.e(TAG, "두 번째 분석 결과 저장 실패: ${e.message}")
        }
    }

    private fun handleEmptyResults(userId: String, diaryId: String, callback: () -> Unit) {
        Log.d(TAG, "생각 유형이 없어 handleEmptyResults 함수를 실행합니다.")

        // 사용자에게 알림을 보냄
        //notifyUser("분석할 내용이 없습니다.") // notifyUser는 사용자에게 메시지를 표시하는 메서드라고 가정

        // 기본 메시지를 Firebase에 저장
        val defaultAnalysis = mapOf(
            "totalSets" to 0,
            "totalCharacters" to 0,
            "thoughtSets" to mapOf("유형 없음" to "분석할 내용이 없습니다.")
        )
        saveSecondAnalysis(userId, diaryId, currentDate, defaultAnalysis, callback)
    }
    private fun notifyUser(message: String) {
        // 예: Toast 메시지로 사용자에게 알림
        //Toast.makeText(context, message, Toast.LENGTH_LONG).show()
    }

    // 현재 날짜를 설정하는 함수
    fun setCurrentDate(date: String) {
        currentDate = date
    }

    // 다른 함수에서 현재 날짜를 사용할 수 있도록 반환하는 함수
    fun getCurrentDate(): String? {
        return currentDate
    }

}
