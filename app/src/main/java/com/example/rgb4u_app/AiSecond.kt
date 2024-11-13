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

class AiSecond {

    private val firebaseDatabase: FirebaseDatabase = FirebaseDatabase.getInstance() // Firebase 인스턴스 초기화
    private val client = OkHttpClient() // OkHttpClient 인스턴스 초기화
    private val apiKey = ""// API 키 설정 (따옴표 안에 키 넣기)
    private val TAG = "AiSecond" // 로깅 태그

    private val cognitiveDistortions = listOf(
        "1. 흑백성 : 전부 아니면 전무의 사고. 연속적 개념보다는 오직 두 가지의 범주로 나누어 상황을 본다. 예: 완벽하게 성공하지 못하면, 실패한 것이다.",
        "2. 재앙성 : 재앙화. 미래에 대해 보다 현실적인 어떤 다른 고려도 없이 부정적으로 예상한다. 예: 나는 매우 화가 날 것이고, 전혀 기능하지 못할 것이다.",
        "3. 외면성 : 긍정적인 면의 평가절하. 자신의 긍정적 경험 혹은 행한 일이나 자질 등을 고려하지 않고 스스로에게 비이성적으로 말한다. 예: 계획이 성공했지만 내가 유능한 것이 아니라 단지 운이 있었을 뿐이다.",
        "4. 느낌성 : 감정적 추론. 그것을 너무 강하게 느끼고 실제적으로 믿기 때문에 그 반대되는 증거는 무시하거나 고려하지 않고, 어떤 일이 틀림없는 사실이라고 생각한다. 예: 일에 있어 많은 것을 잘 한 줄은 알지만, 여전히 나는 실패자라고 느낀다.",
        "5. 이름성 : 명명하기. 덜 위험한 결론으로 이끄는 좀 더 합리적인 증거를 고려하지 않고, 자신이나 다른 사람에게 고정적이며 전반적인 이름을 붙인다. 예: 나는 실패자야. 그는 전혀 좋지 않아.",
        "6. 과장성 : 과장 또는 축소. 자신이나 다른 사람 혹은 어떤 상황을 평가할 때, 비이성적으로 부정적인 측면을 강조하고 긍정적인 면을 최소화한다. 예: 평범하다는 평가를 받는 것은 내가 얼마나 부적합한지 증명하는 것이다.",
        "7. 부분성 : 정신적 여과. 전체 그림을 보는 대신에 한 가지 작은 세세한 것에 필요 없이 관심을 가진다. 예: 나의 평가에서 받은 한 가지 낮은 점수는, 비록 몇 가지 높은 점수가 있음에도 불구하고, 내가 일을 엉망으로 한다는 것을 의미한다.",
        "8. 궁예성 : 독심술. 좀 더 현실적인 가능성을 고려하지 않고, 다른 이들이 생각하는 것을 알 수 있다고 믿는다. 예: 그는 내가 이 계획의 구체적인 것도 모른다고 생각하고 있다.",
        "9. 일반화성 : 지나친 일반화. 현재의 상황을 넘어서는 싹쓸이식 부정적 결론을 내린다. 예: 그 모임에서 불편하다고 느꼈으므로 나는 친구를 사귀기에 필요한 요소를 가지고 있지 않다.",
        "10. 내탓성 : 자기 탓. 다른 사람의 행동에 대한 좀 더 타당한 설명을 고려하지 않고, 자신 때문에 다른 사람들이 부정적으로 행동한다고 믿는다. 예: 그 수리공이 나에게 퉁명스럽게 대했던 것은 내가 무엇인가 잘못했기 때문이다.",
        "11. 해야해성 : 당위 진술. 자신이나 다른 사람들의 행동에 대해 확실하고 고정된 사고를 가지고 있으며, 이런 기대를 충족하지 못하게 되면 얼마나 나쁜지를 과대평가한다. 예: 내가 실수를 한다는 것은 끔찍한 일이다. 나는 항상 최선을 다해야 한다.",
        "12. 어둠성 : 터널 시야. 어떤 상황의 부정적인 면만을 본다. 예: 우리 아들의 담임 선생은 올바로 하는 것이 없어. 그는 비판적이며 무감각하고 형편없이 가르친다."
    )

    fun analyzeThoughts(userId: String, diaryId: String, callback: () -> Unit) {
        Log.d(TAG, "analyzeThoughts 호출: userId = $userId, diaryId = $diaryId")

        val analysisRef: DatabaseReference = firebaseDatabase.getReference("users/$userId/diaries/$diaryId/aiAnalysis/firstAnalysis/thoughts")

        analysisRef.get().addOnSuccessListener { dataSnapshot ->
            Log.d(TAG, "Firebase에서 thoughts 데이터 가져오기 성공")
            val thoughts = dataSnapshot.value.toString()
            val sentences = splitSentences(thoughts)

            val results = mutableListOf<JSONObject>()
            var processedCount = 0

            for (sentence in sentences) {
                Log.d(TAG, "문장 분석 중: $sentence")
                analyzeCognitiveDistortions(sentence) { apiResponse ->
                    results.add(apiResponse)
                    processedCount++

                    // 모든 문장 분석이 완료된 경우
                    if (processedCount == sentences.size) {
                        val filteredResults = filterResults(results)
                        Log.d(TAG, "결과 필터링 완료, 필터링된 결과 수: ${filteredResults.size}")

                        // 필터링 결과가 비어 있을 때의 처리
                        if (filteredResults.isEmpty()) {
                            handleEmptyResults(userId, diaryId, callback)
                        } else {
                            // 필터링된 결과가 존재할 경우
                            val secondAnalysis = createSecondAnalysis(filteredResults)
                            saveSecondAnalysis(userId, diaryId, secondAnalysis, callback)
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
        다음 문장이 12가지 인지 왜곡 유형 중 하나에 해당하는지 판단해줘.
        
        판단 시 참고해야 할 인지 왜곡 유형과 정의는 다음과 같아:
        ${cognitiveDistortions.joinToString("\n")}
        
        인지 왜곡에 해당하면 다음을 아래 형식으로 JSON 형식으로 제시해줘:
        {
            "유형": "유형 이름", //만약 유형이 없다면, "유형" 필드에 null을 넣어줘.
            "문장": "$sentence",
            "유형 이유": "이 유형에 해당하는 이유를 한국어 기준 200byte 이내로 작성해줘.",
            "대안적 생각": "이 문장 대신 하면 좋은 적응적인 생각을 74byte 문장 이내로 간단하게 작성해줘.",
            "대안적 생각 이유": "이 대안적 생각을 추천한 이유를 한국어 기준 200byte 이내로 작성해줘."
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
                Log.e(TAG, "API request failed: ${e.message}")
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
                                    val content = message.getString("content") // content 가져오기
                                    val contentJson = JSONObject(content) // content를 JSON으로 변환

                                    Log.d(TAG, "API 응답 JSON: $contentJson")

                                    // "유형"이 없는 경우 처리
                                    if (!contentJson.has("유형") || contentJson.getString("유형").isEmpty()) {
                                        val noTypeResponse = JSONObject().apply {
                                            put("유형", JSONObject.NULL)
                                            put("문장", sentence)
                                            put("유형 이유", JSONObject.NULL)
                                            put("대안적 생각", JSONObject.NULL)
                                            put("대안적 생각 이유", JSONObject.NULL)
                                        }
                                        callback(noTypeResponse) // 콜백으로 응답 전달
                                    } else {
                                        // 모든 필요한 필드 추출
                                        val responseObj = JSONObject().apply {
                                            put("유형", contentJson.optString("유형", "유형 없음"))
                                            put("문장", contentJson.optString("문장", "문장 없음"))
                                            put("유형 이유", contentJson.optString("유형 이유", "이유 없음"))
                                            put("대안적 생각", contentJson.optString("대안적 생각", "대안 없음"))
                                            put("대안적 생각 이유", contentJson.optString("대안적 생각 이유", "이유 없음"))
                                        }

                                        callback(responseObj) // 콜백으로 응답 전달
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

            // "유형"이 비어있지 않은 경우에만 추가
            if (type.isNotEmpty()) {
                filteredResults[type] = filteredResults.getOrDefault(type, mutableListOf()).apply {
                    if (size < 3) add(result)
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
            "흑백성" to "ic_planet_a",  // 흑백성에 대한 이미지
            "재앙성" to "ic_planet_b",  // 재앙성에 대한 이미지
            "외면성" to "ic_planet_c",  // 외면성에 대한 이미지
            "느낌성" to "ic_planet_d",  // 느낌성에 대한 이미지
            "이름성" to "ic_planet_e",  // 이름성에 대한 이미지
            "과장성" to "ic_planet_f",  // 과장성에 대한 이미지
            "부분성" to "ic_planet_g",  // 부분성에 대한 이미지
            "궁예성" to "ic_planet_h",  // 궁예성에 대한 이미지
            "일반화성" to "ic_planet_i", // 일반화성에 대한 이미지
            "내탓성" to "ic_planet_j",  // 내탓성에 대한 이미지
            "해야해성" to "ic_planet_k", // 해야해성에 대한 이미지
            "어둠성" to "ic_planet_l"   // 어둠성에 대한 이미지
        )
        for ((type, thoughts) in filteredResults) {
            val thoughtSet = mutableMapOf<String, Any>()
            thoughts.take(3).forEachIndexed { index, thought ->
                thoughtSet["${index + 1}"] = mapOf<String, Any>( // 키와 값을 명시적으로 지정
                    "selectedThoughts" to thought.optString("문장", "Unknown"),
                    "charactersReason" to thought.optString("유형 이유", ""),
                    "alternativeThoughts" to thought.optString("대안적 생각", ""),
                    "alternativeThoughtsReason" to thought.optString("대안적 생각 이유", ""),
                    "characterDescription" to (characterDescriptions[type] ?: listOf("설명이 없습니다.")),
                    "imageResource" to (imageResources[type] ?: "ic_planet_a") // 기본값 설정
                )
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

    private fun saveSecondAnalysis(userId: String, diaryId: String, secondAnalysis: Map<String, Any>, callback: () -> Unit) {
        val analysisRef = firebaseDatabase.getReference("users/$userId/diaries/$diaryId/aiAnalysis/secondAnalysis")

        // secondAnalysis의 내용을 로그로 출력
        Log.d(TAG, "저장할 secondAnalysis 데이터: $secondAnalysis")

        analysisRef.setValue(secondAnalysis).addOnSuccessListener {
            Log.d(TAG, "두 번째 분석 결과 저장 성공")

            // DistortionTypeFiller 인스턴스 생성 및 초기화
            val distortionTypeFiller = DistortionTypeFiller()
            distortionTypeFiller.initialize(userId, diaryId) // 사용자 ID와 다이어리 ID를 전달하여 초기화
            Log.d("AiSecond", "filler 부름 userId: $userId, diaryId: $diaryId")

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
        saveSecondAnalysis(userId, diaryId, defaultAnalysis, callback)
    }
    private fun notifyUser(message: String) {
        // 예: Toast 메시지로 사용자에게 알림
        //Toast.makeText(context, message, Toast.LENGTH_LONG).show()
    }
}
