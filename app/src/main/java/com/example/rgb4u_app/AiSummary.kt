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
import org.json.JSONObject
import java.io.IOException

class AiSummary {

    private val firebaseDatabase: FirebaseDatabase = FirebaseDatabase.getInstance()
    private val client = OkHttpClient()
    private val apiKey = "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"  // API 키 설정 (따옴표 안에 키 넣기)
    private val TAG = "AiSummary" // Logging Tag

    // 특정 diaryId의 situation과 thoughts를 가져와 ChatGPT API로 분석 후 저장하는 함수
    fun analyzeDiary(userId: String, diaryId: String, date: String, callback: () -> Unit) {
        val userRef: DatabaseReference = firebaseDatabase.getReference("users/$userId/diaries/$date/userInput")

        // 파이어베이스에서 situation과 thoughts 가져오기
        userRef.get().addOnSuccessListener { dataSnapshot ->
            val situation = dataSnapshot.child("situation").value.toString()
            val thoughts = dataSnapshot.child("thoughts").value.toString()

            // 상황과 생각을 하나의 텍스트로 합침
            val combinedText = "$situation $thoughts"  // 상황과 생각을 합침

            // 로그: Firebase에서 가져온 데이터 확인
            Log.d(TAG, "Combined Text: $combinedText")

            // ChatGPT API로 분석 요청 보내기
            val prompt = """
    너는 인지행동치료 전문가로, 사람들이 쓴 글에서 감정, 상황, 생각을 정확히 분리하는 역할을 해요. 각 요소를 구별할 때는 친절하고 다정하게 설명해주고, ~해요체로 표현해 주세요. 
    예를 들어, '스스로를 나쁘게 생각하고 있어요.' 같은 식으로요.

    하나의 문장 안에 감정, 상황, 생각이 동시에 포함될 수 있다는 점에 유의해 주세요. 각 요소를 개별적으로 분류해주세요.
    상황과 생각을 분류할 때, 각각 그렇게 분류한 이유도 친절하게 설명해주면 좋겠어요. 예를 들어, '오늘 있었던 실제 사건에 대한 내용이에요.', '오늘 있었던 일에 대한 생각을 나타내는 내용이에요.'라고요.
    만약 상황에서 생각과 감정을 분류했다면, 생각과 감정이 사라진 이유를 친절하게 설명해주면 좋겠어요. 예를 들어, '(옮겨진 내용)은 감정을 나타내고 있어서 옮겨졌어요', '(옮겨진 내용)은 생각을 나타내고 있어서 옮겨졌어요'라고요.
    만약 생각에서 상황과 감정을 분류했다면, 상황과 감정이 사라진 이유를 친절하게 설명해주면 좋겠어요. 예를 들어, '(옮겨진 내용)은 감정을 나타내고 있어서 옮겨졌어요', '(옮겨진 내용)은 상황을 나타내고 있어서 옮겨졌어요'라고요.

    참고할 정의와 예시는 다음과 같아요:

    감정: 감정은 정서적 반응이나 감정 형용사가 포함된 문장이에요. 예를 들어, '슬프다', '화가 나다', '기쁘다', '불안하다', '상처받다' 등의 키워드가 있어요. 이러한 키워드를 잘 식별해 주세요. 감정이 여러 개 있다면 리스트 형태로 표현해 주세요.
    상황: 상황은 개인이 경험한 실제 사건이나 환경을 설명하는 문장이에요. 예를 들어, '친구와 밥을 먹었어', '시험 공부를 했어' 같은 문장이에요.
    생각: 생각은 특정 상황에 대한 개인의 즉각적인 반응이나 해석을 나타내는 문장이에요. 예를 들어, '나는 친구가 나를 싫어하는 것 같아', '나는 시험을 망친 게 내 탓이라고 생각해'처럼 사람의 말투로 부드럽게 표현해 주세요.

    생각이 너무 축약되거나 여러 문장이 합쳐지지 않도록 주의해 주세요. 예를 들어, '나는 공부를 못해'처럼 구체적으로 풀어서 작성해 주세요. 문장은 끊어서 각각 한국어 기준 80byte 이내로 작성해주세요.

    이 작업은 구조화된 JSON 형식으로 반환해 주세요.

    여기 내가 제공하는 텍스트를 분석해 주세요:
    $combinedText
""".trimIndent()


// function schema 정의
            val functionSchema = """
{
    "name": "analyze_text",
    "description": "Analyze a user's text and return emotions, situation, and thoughts",
    "parameters": {
        "type": "object",
        "properties": {
            "emotion": {
                "type": "array",
                "items": { "type": "string" },
                "description": "List of emotions identified in the text, e.g., '슬프다', '화가 나다'. Identify the emotional response from the text. It should include recognizable emotional keywords, such as '슬프다', '화가 나다', '기쁘다', '불안하다', '상처받다', etc. Please ensure to accurately identify the emotions. If the identified content represents an emotion, specify that '(내용)은 감정을 나타내고 있어서 옮겨졌어요'."
            },
            "situation": {
                "type": "string",
                "description": "The situation as described in the text, expressed in a casual manner, like talking to oneself. Use informal language like '~했어'. If the identified content represents a situation, specify that '(내용)은 상황을 나타내고 있어서 옮겨졌어요'."
            },
            "thoughts": {
                "type": "string",
                "description": "The thoughts as described in the text, expressed in a casual manner, like talking to oneself. Use informal language like '~했어'. The thoughts should not be overly condensed from the original and should not be combined into a single sentence. If the identified content represents a thought, specify that '(내용)은 생각을 나타내고 있어서 옮겨졌어요'."
            },
            "situationReason": {
                "type": "string",
                "description": "Reason for categorizing the text as a situation, expressed kindly and in a gentle tone using ~해요체. Use simple words and sentences that are easy for elementary students to understand. Explain if thoughts or emotions have been separated, e.g., '(내용)은 생각을 나타내고 있어서 옮겨졌어요'."
            },
            "thoughtsReason": {
                "type": "string",
                "description": "Reason for categorizing the text as a thought, expressed kindly and in a gentle tone using ~해요체. Use simple words and sentences that are easy for elementary students to understand. Explain if situations or emotions have been separated, e.g., '(내용)은 감정을 나타내고 있어서 옮겨졌어요'."
            }
        },
        "required": ["emotion", "situation", "thoughts", "situationReason", "thoughtsReason"]
    }
}
""".trimIndent()


            val requestBody = JSONObject()
            requestBody.put("model", "gpt-3.5-turbo")
            requestBody.put("messages", JSONArray().put(JSONObject().apply {
                put("role", "user")
                put("content", prompt)
            }))
            requestBody.put("functions", JSONArray().put(JSONObject(functionSchema)))
            requestBody.put("function_call", JSONObject().put("name", "analyze_text"))

            // RequestBody 생성
            val body = requestBody.toString().toRequestBody("application/json; charset=utf-8".toMediaTypeOrNull())


            // 로그: API 요청 정보
            Log.d(TAG, "Request Body: $requestBody")

            val request = Request.Builder()
                .url("https://api.openai.com/v1/chat/completions")
                .post(body)
                .addHeader("Authorization", "Bearer $apiKey")
                .build()

            //API 요청 후 응답 처리
            client.newCall(request).enqueue(object : Callback {
                override fun onFailure(call: Call, e: IOException) {
                    Log.e(TAG, "API Request Failed: ${e.message}")
                }

                //응답처리로직
                override fun onResponse(call: Call, response: Response) {
                    if (response.isSuccessful) {
                        val responseBody = response.body?.string() // 응답 바디를 안전하게 가져옴
                        Log.d(TAG, "API Response: ${responseBody ?: "No Response"}")

                        // 응답 바디가 null이 아닐 경우에만 JSONObject 생성
                        val resultJson = if (responseBody != null) {
                            JSONObject(responseBody)
                        } else {
                            JSONObject() // 빈 JSON 객체로 초기화
                        }

                        // 분석 결과 추출
                        val aiAnalysis = extractAiAnalysis(resultJson)

                        // 파이어베이스에 분석 결과 저장
                        val analysisRef: DatabaseReference = firebaseDatabase.getReference("users/$userId/diaries/$date/aiAnalysis/firstAnalysis")
                        analysisRef.setValue(aiAnalysis).addOnSuccessListener {
                            Log.d(TAG, "Analysis successfully saved to Firebase.")
                            callback() // 분석 완료 후 콜백 호출
                        }.addOnFailureListener { e ->
                            Log.e(TAG, "Failed to save analysis to Firebase: ${e.message}")
                        }
                    } else {
                        Log.e(TAG, "API Response not successful: ${response.message}")
                        Log.e(TAG, "API Response not successful: ${response.message} - ${response.body?.string()}")
                    }
                }
            })
        }.addOnFailureListener { e ->
            Log.e(TAG, "Failed to get user input from Firebase: ${e.message}")
        }
    }

    // ChatGPT API 응답에서 분석 결과 추출하는 함수
    private fun extractAiAnalysis(resultJson: JSONObject): Map<String, Any> { // 반환 타입 수정
        val choices = resultJson.getJSONArray("choices")
        val message = choices.getJSONObject(0).getJSONObject("message")

        // function_call 필드가 있는지 확인
        if (!message.has("function_call")) {
            Log.e(TAG, "Function call is missing in the response.")
            return emptyMap() // 오류 시 빈 맵 반환
        }

        val argumentsString = message.getJSONObject("function_call").getString("arguments")
        val arguments = JSONObject(argumentsString)

        // emotion을 리스트로 변환
        val emotionString = arguments.getString("emotion")
        val emotionArray = JSONArray(emotionString) // JSON 배열로 변환
        val emotionList = List(emotionArray.length()) { emotionArray.getString(it) } // 리스트로 변환

        val situation = arguments.getString("situation")
        val thoughts = arguments.getString("thoughts")
        val situationReason = arguments.getString("situationReason")
        val thoughtsReason = arguments.getString("thoughtsReason")

        return mapOf(
            "emotion" to emotionList, // 리스트로 반환
            "situation" to situation,
            "thoughts" to thoughts,
            "situationReason" to situationReason,
            "thoughtsReason" to thoughtsReason
        )
    }

}
