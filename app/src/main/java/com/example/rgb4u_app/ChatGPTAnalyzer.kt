import com.google.firebase.firestore.FirebaseFirestore
import okhttp3.*
import java.io.IOException

class ChatGptAnalyzer {
    private val client = OkHttpClient()
    private val db = FirebaseFirestore.getInstance()

    fun classifyText(situation: String, thoughts: String, apiKey: String, callback: (String?, String?, String?) -> Unit) {
        val url = "https://api.openai.com/v1/chat/completions"

        val prompt = """
            너는 인지행동치료 전문가로, 사람들이 쓴 글에서 감정, 상황, 생각을 정확히 분리하는 역할을 해. 
            분석할 텍스트: 상황: $situation 생각: $thoughts
        """.trimIndent()

        val json = """
            {
                "model": "gpt-3.5-turbo",
                "messages": [{"role": "user", "content": "$prompt"}],
                "max_tokens": 400,
                "temperature": 0.7
            }
        """.trimIndent()

        val requestBody = RequestBody.create(MediaType.parse("application/json"), json)

        val request = Request.Builder()
            .url(url)
            .addHeader("sk--uSlihn9WK8ak3cCDzDHkWdlNQv5L2lU6bsAUBlmMGT3BlbkFJ51EWtuW95cvjpElOrkwhkHHECut5mZE6SoCrcxJ7sA", "Bearer $apiKey")
            .post(requestBody)
            .build()

        client.newCall(request).enqueue(object : Callback {
            override fun onFailure(call: Call, e: IOException) {
                callback(null, null, null)
            }

            override fun onResponse(call: Call, response: Response) {
                response.use {
                    if (!response.isSuccessful) throw IOException("Unexpected code $response")

                    val responseBody = response.body?.string()
                    val emotion = parseResponse(responseBody, "감정")
                    val situation = parseResponse(responseBody, "상황")
                    val thought = parseResponse(responseBody, "생각")

                    callback(emotion, situation, thought)
                }
            }
        })
    }

    private fun parseResponse(response: String?, label: String): String? {
        response?.let {
            val start = it.indexOf("$label:") + "$label:".length
            val end = it.indexOf("$label 끝")
            return if (start >= 0 && end > start) it.substring(start, end).trim() else null
        }
        return null
    }

    fun saveResultsToFirebase(userId: String, diaryId: String, emotion: String?, situation: String?, thought: String?) {
        val resultData = hashMapOf(
            "situation" to situation,
            "thoughts" to thought,
            "emotion" to emotion,
            "situationReason" to "AI 분석 이유", // 이유는 추가적으로 정의 필요
            "thoughtsReason" to "AI 생각 분석 이유" // 이유는 추가적으로 정의 필요
        )

        db.collection("users").document(userId)
            .collection("diaries").document(diaryId)
            .collection("aiAnalysis").document("firstAnalysis")
            .set(resultData)
            .addOnSuccessListener {
                // 성공적으로 저장됨
            }
            .addOnFailureListener { e ->
                // 저장 실패 처리
            }
    }
}
