package com.example.rgb4u.ver1_app

data class DiaryEntry(
    val aiAnalysis: AiAnalysis? = null,
    val diaryId: String? = null,
    val readingstatus: String? = null,
    val savingstatus: String? = null,
    val toolbardate: String? = null,
    val userInput: UserInput? = null
)

data class AiAnalysis(
    val firstAnalysis: FirstAnalysis? = null,
    val secondAnalysis: SecondAnalysis? = null
)

data class FirstAnalysis(
    val emotion: List<String>? = null,
    val situation: String? = null,
    val situationReason: String? = null,
    val thoughts: String? = null,
    val thoughtsReason: String? = null
)

data class SecondAnalysis(
    val thoughtSets: ThoughtSets? = null,
    val totalCharacters: Int? = null,
    val totalSets: Int? = null
)

data class ThoughtSets(
    val 과장성: List<Thought>? = null,
    val 흑백성: List<Thought>? = null
)

data class Thought(
    val alternativeThoughts: String? = null,
    val alternativeThoughtsReason: String? = null,
    val characterDescription: List<String>? = null,
    val charactersReason: String? = null,
    val imageResource: String? = null,
    val selectedThoughts: String? = null
)

data class UserInput(
    val emotionDegree: EmotionDegree? = null,
    val emotionTypes: List<String>? = null,
    val reMeasuredEmotionDegree: EmotionDegree? = null,
    val situation: String? = null,
    val thoughts: String? = null
)

data class EmotionDegree(
    val emotionimg: String? = null,
    val int: Int? = null,
    val string: String? = null
)
