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
    private val apiKey = "API키 여기에"  // API 키 설정
    private val TAG = "AiSummary" // Logging Tag

    // 특정 diaryId의 situation과 thoughts를 가져와 ChatGPT API로 분석 후 저장하는 함수
    fun analyzeDiary(userId: String, diaryId: String) {
        val userRef: DatabaseReference = firebaseDatabase.getReference("users/$userId/diaries/$diaryId/userInput")

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
                너는 인지행동치료 전문가로, 사람들이 쓴 글에서 감정, 상황, 생각을 정확히 분리하고 상황을 상황으로 분류한 이유와 생각을 생각으로 분류한 이유를 설명하는 역할을 해. 
                참고할 정의와 예시는 다음과 같아:
                
                감정: 감정은 정서적 반응이나 감정 형용사가 포함된 문장이야. 예시로는 '슬프다', '화가 난다', '기쁘다' 등이 있어.
                상황: 상황은 개인이 경험한 실제 사건이나 환경을 설명하는 문장이야. 예를 들어, '친구와 밥을 먹었다', '시험 공부를 했다' 같은 문장이야.
                생각: 생각은 특정 상황에 대한 개인의 즉각적인 반응이나 해석을 나타내는 문장이야. 예를 들어, '친구가 나를 싫어하는 것 같다', '시험을 망친 건 내 탓이다' 같은 문장이야.
                
                주의할 점은, 하나의 문장 안에 감정, 상황, 생각이 동시에 포함될 수 있다는 거야. 각 요소를 개별적으로 분류해줘.
                그리고 상황과 생각을 분류하면서, 상황과 생각을 각각 그렇게 분류한 이유를 적어줘.
                모든 문장은 완성된 매끄러운 문장으로 만들어줘.
                
                이 작업은 구조화된 JSON 형식으로 반환해줘.
                
                여기 내가 제공하는 텍스트를 분석해줘:
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
            requestBody.put("messages", JSONArray().put(JSONObject().apply {
                put("role", "user")
                put("content", prompt)
            }))
            requestBody.put("functions", JSONArray().put(JSONObject(functionSchema)))
            requestBody.put("function_call", JSONObject().put("name", "analyze_text"))

            // RequestBody 생성
            val body = RequestBody.create("application/json; charset=utf-8".toMediaType(), requestBody.toString())

            // 로그: API 요청 정보
            Log.d(TAG, "Request Body: $requestBody")

            val request = Request.Builder()
                .url("https://api.openai.com/v1/chat/completions")
                .post(body)
                .addHeader("Authorization", "Bearer $apiKey")
                .build()

            client.newCall(request).enqueue(object : Callback {
                override fun onFailure(call: Call, e: IOException) {
                    Log.e(TAG, "API Request Failed: ${e.message}")
                }

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
                        val analysisRef: DatabaseReference = firebaseDatabase.getReference("users/$userId/diaries/$diaryId/aiAnalysis/firstAnalysis")
                        analysisRef.setValue(aiAnalysis).addOnSuccessListener {
                            Log.d(TAG, "Analysis successfully saved to Firebase.")
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
    private fun extractAiAnalysis(resultJson: JSONObject): Map<String, String> {
        val functionCall = resultJson.getJSONArray("choices").getJSONObject(0).getJSONObject("message")
        val argumentsString = functionCall.getJSONObject("function_call").getString("arguments")

        // argumentsString을 JSONObject로 변환
        val arguments = JSONObject(argumentsString)

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
