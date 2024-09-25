package com.example.rgb4u_app

import android.util.Log
import com.google.firebase.database.DatabaseReference
import com.google.firebase.database.FirebaseDatabase
import okhttp3.*
import org.json.JSONArray
import org.json.JSONObject
import java.io.IOException
import okhttp3.MediaType.Companion.toMediaType

class AiSummary {

    private val firebaseDatabase: FirebaseDatabase = FirebaseDatabase.getInstance()
    private val client = OkHttpClient()
    private val apiKey = "API키"  // API 키 설정
    private val TAG = "AiSummary" // Logging Tag

    // 특정 diaryId의 situation과 thoughts를 가져와 ChatGPT API로 분석 후 저장하는 함수
    fun analyzeDiary(userId: String, diaryId: String) {
        val userRef: DatabaseReference = firebaseDatabase.getReference("users/$userId/diaries/$diaryId/userInput")

        // 파이어베이스에서 situation과 thoughts 가져오기
        userRef.get().addOnSuccessListener { dataSnapshot ->
            val situation = dataSnapshot.child("situation").value.toString()
            val thoughts = dataSnapshot.child("thoughts").value.toString()

            // 로그: Firebase에서 가져온 데이터 확인
            Log.d(TAG, "Situation: $situation, Thoughts: $thoughts")

            // ChatGPT API로 분석 요청 보내기
            val prompt = """
                너는 인지행동치료 전문가로, 사람들이 쓴 글에서 감정, 상황, 생각을 정확히 분리하는 역할을 해. 
                참고할 정의와 예시는 다음과 같아:
                
                감정: 감정은 정서적 반응이나 감정 형용사가 포함된 문장이야. 예시로는 '슬프다', '화가 난다', '기쁘다' 등이 있어.
                상황: 상황은 개인이 경험한 실제 사건이나 환경을 설명하는 문장이야. 예를 들어, '친구와 밥을 먹었다', '시험 공부를 했다' 같은 문장이야.
                생각: 생각은 특정 상황에 대한 개인의 즉각적인 반응이나 해석을 나타내는 문장이야. 예를 들어, '친구가 나를 싫어하는 것 같다', '시험을 망친 건 내 탓이다' 같은 문장이야.
                
                주의할 점은, 하나의 문장 안에 감정, 상황, 생각이 동시에 포함될 수 있다는 거야. 각 요소를 개별적으로 분류해줘.
                이 작업은 구조화된 JSON 형식으로 반환해줘.
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
                                "type": "string",
                                "description": "The emotional response from the text."
                            },
                            "situation": {
                                "type": "string",
                                "description": "The situation as described in the text."
                            },
                            "thoughts": {
                                "type": "string",
                                "description": "The thoughts as described in the text."
                            },
                            "situationReason": {
                                "type": "string",
                                "description": "Reason for categorizing the text as a situation."
                            },
                            "thoughtsReason": {
                                "type": "string",
                                "description": "Reason for categorizing the text as a thought."
                            }
                        },
                        "required": ["emotion", "situation", "thoughts", "situationReason", "thoughtsReason"]
                    }
                }
            """.trimIndent()

            val requestBody = JSONObject()
            requestBody.put("model", "gpt-3.5-turbo")
            requestBody.put("prompt", prompt)
            requestBody.put("functions", JSONArray().put(JSONObject(functionSchema)))
            requestBody.put("function_call", JSONObject().put("name", "analyze_text"))

            // RequestBody 생성
            val body = RequestBody.create("application/json; charset=utf-8".toMediaType(), requestBody.toString())

            // 로그: API 요청 정보
            Log.d(TAG, "Request Body: $requestBody")

            val request = Request.Builder()
                .url("https://api.openai.com/v1/completions")
                .post(body)
                .addHeader("Authorization", "Bearer $apiKey")
                .build()

            client.newCall(request).enqueue(object : Callback {
                override fun onFailure(call: Call, e: IOException) {
                    Log.e(TAG, "API Request Failed: ${e.message}")
                }

                override fun onResponse(call: Call, response: Response) {
                    if (response.isSuccessful) {
                        val responseBody = response.body // 응답 바디
                        Log.d(TAG, "API Response: ${responseBody?.string() ?: "No Response"}")

                        val resultJson = JSONObject(responseBody?.string() ?: "{}")

                        // 분석 결과 추출
                        val aiAnalysis = extractAiAnalysis(resultJson)

                        // 파이어베이스에 분석 결과 저장
                        val analysisRef: DatabaseReference = firebaseDatabase.getReference("users/$userId/diaries/$diaryId/aiAnalysis/firstAnalysis")
                        analysisRef.setValue(aiAnalysis).addOnSuccessListener {
                            Log.d(TAG, "Analysis successfully saved to Firebase.")
                        }.addOnFailureListener { e ->
                            Log.e(TAG, "Failed to save analysis to Firebase: ${e.message}")
                        }
                    } else {
                        Log.e(TAG, "API Response not successful: ${response.message}")
                    }
                }
            })
        }.addOnFailureListener { e ->
            Log.e(TAG, "Failed to get user input from Firebase: ${e.message}")
        }
    }

    // ChatGPT API 응답에서 분석 결과 추출하는 함수
    private fun extractAiAnalysis(resultJson: JSONObject): Map<String, String> {
        val functionCall = resultJson.getJSONArray("choices").getJSONObject(0).getJSONObject("message")
        val arguments = functionCall.getJSONObject("function_call").getJSONObject("arguments")

        // 분석 결과를 파이어베이스에 저장할 수 있도록 정리
        val emotion = arguments.getString("emotion")
        val situation = arguments.getString("situation")
        val thoughts = arguments.getString("thoughts")
        val situationReason = arguments.getString("situationReason")
        val thoughtsReason = arguments.getString("thoughtsReason")

        return mapOf(
            "emotion" to emotion,
            "situation" to situation,
            "thoughts" to thoughts,
            "situationReason" to situationReason,
            "thoughtsReason" to thoughtsReason
        )
    }
}
