package com.example.rgb4u.ver1_app

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
    private val apiKey = ""  // API 키 설정 (따옴표 안에 키 넣기)
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

            사용자가 입력한 문장을 각각 독립적으로 분석하여 다음 항목을 구체적으로 분류해 주세요. 각 항목에 대해 이유를 친근하고 부드러운 말투로 설명해 주세요. 분석된 항목들은 명확하게 구분되어야 하며, 감정은 단어로만 추출하지 말고 감정의 뉘앙스도 함께 담아주세요. 상황과 생각도 마찬가지로 구체적인 내용으로 분리해 주세요.
            
            각 문장에 대해 분석을 진행하세요:
            1. **상황**: 사용자 입력에서 **객관적인 사건**이나 **사실적인 설명**을 추출해 주세요. 주관적인 해석이나 추측은 제외해 주세요.
                - 예시: "체육 시간에 발야구를 했어." (사실적인 설명이며 객관적인 사건이에요. 실제로 일어난 일을 말하는 거예요.)
                - 예시: "시험에서 50점을 받았어." (실제로 일어난 구체적인 사건을 나타내고 있어요. 사실 그대로의 일을 말해요.)
            
            2. **상황 요약 이유**: 상황에서 어떤 일이 있었는지를 친절하고 간결하게 설명해 주세요.
                - 예시: "체육 시간에 발야구를 하다가 넘어졌어요." (그때 어떤 일이 있었는지 설명해 주세요. 그래서 넘어진 일을 설명하고 있어요.)
            
            3. **생각**: 사용자 입력에서 **주관적인 생각**이나 **의견**, **추측**을 추출해 주세요. 자신에 대한 생각이나 의문, 자책 등이 포함됩니다.
                - 예시: "나는 왜 잘하는 게 하나도 없을까?" (스스로를 깎아내리는 생각이에요. 자신에 대해 부정적으로 생각하고 있어요.)
                - 예시: "애들이 나를 비웃었을 거야." (확실하지 않은 것을 넘겨짚는 생각이에요. 주관적으로 추측하고 있어요.)
            
            4. **생각 요약 이유**: 생각에서 어떤 내용이 주관적이었는지, 그리고 왜 그것이 사용자의 생각으로 분류되는지 설명해 주세요.
                - 예시: "자기 자신에 대해 부정적인 생각을 했고, 친구들이 나를 비웃을 것 같다고 추측했어요." (자신을 깎아내리고, 확실하지 않은 것을 추측하고 있어요. 자신의 주관적인 의견과 추측이라서 생각으로 분류되었어요.)
            
            5. **감정**: 사용자 입력에서 나타나는 감정 표현을 추출하고, **감정의 뉘앙스**까지 포함해 주세요. 예를 들어, '슬프다', '걱정된다'와 같이 감정의 구체적인 표현을 포함시켜 주세요. 또한, 감정의 강도나 깊이를 표현할 수 있으면 더 좋아요. 예: "조금 슬프다" 또는 "굉장히 우울하다".
                - 예시: "너무 창피해." (감정이 정말 강해요. '창피하다'라고 느끼는 감정의 강도가 커요.)
                - 예시: "진짜 우울해." (우울한 감정이 굉장히 강한 걸 알 수 있어요. '진짜'라는 표현이 그만큼 힘을 실어주고 있어요.)
            
            이 작업은 구조화된 JSON 형식으로 반환해 주세요.
            
            각 문장의 분석을 별도로 진행해주세요. 

            여기 내가 제공하는 텍스트를 분석해 주세요:
            $combinedText
            """.trimIndent()



            // function schema 정의
            val functionSchema = """
            {
                "name": "analyze_text",
                "description": "사용자가 입력한 문장에서 각각 감정, 상황, 생각을 분류하고, 각 항목에 대한 이유를 설명해 주세요. 결과는 각 문장의 분석으로 분리되어 제공해 주세요. 분석 항목에 대한 설명은 부드럽고 친근한 말투로 작성해 주세요.
",
                "parameters": {
                    "type": "object",
                    "properties": {
                        "emotion": {
                            "type": "array",
                            "items": { "type": "string" },
                            "description": "텍스트에서 확인된 감정 목록이에요. 예: '슬프다', '걱정된다'. 감정은 명확하고 구체적으로 표현해 주세요."
                        },
                        "situation": {
                            "type": "string",
                            "description": "텍스트에서 묘사된 상황이에요. 객관적인 사건이나 사실적인 설명만 포함해 주세요."
                        },
                        "thoughts": {
                            "type": "string",
                            "description": "텍스트에서 묘사된 생각이에요. 주관적인 의견, 판단, 추측 등을 포함해 주세요."
                        },
                        "situationReason": {
                            "type": "string",
                            "description": "상황을 사건이나 사실로 분류한 이유를 설명해 주세요. 왜 그것이 객관적인 상황으로 분류되는지 설명해 주세요."
                        },
                        "thoughtsReason": {
                            "type": "string",
                            "description": "생각을 주관적인 의견이나 추측으로 분류한 이유를 설명해 주세요. 왜 그것이 생각으로 분류되는지 설명해 주세요."
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
