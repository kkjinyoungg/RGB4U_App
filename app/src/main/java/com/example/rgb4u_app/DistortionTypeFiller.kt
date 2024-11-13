package com.example.rgb4u_app

import android.util.Log
import com.example.rgb4u_app.ui.activity.distortiontype.DistortionType
import com.google.firebase.database.*

class DistortionTypeFiller {

    private lateinit var database: DatabaseReference
    private val distortionTypes = mutableListOf<DistortionType>()
    private var dataLoadedListener: (() -> Unit)? = null // 데이터 로드 완료 리스너

    // 사용자 ID와 다이어리 ID를 매개변수로 받는 생성자
    fun initialize(userId: String, diaryId: String) {
        Log.d("DistortionTypeFiller", "Initializing with userId: $userId, diaryId: $diaryId")
        database = FirebaseDatabase.getInstance().getReference("users/$userId/diaries/$diaryId/aiAnalysis/secondAnalysis/thoughtSets")
        fetchDistortionData()
    }

    private fun fetchDistortionData() {
        Log.d("DistortionTypeFiller", "Fetching distortion data from Firebase...")
        database.addListenerForSingleValueEvent(object : ValueEventListener {
            override fun onDataChange(snapshot: DataSnapshot) {
                if (snapshot.exists()) {
                    Log.d("DistortionTypeFiller", "Data fetched successfully: ${snapshot.childrenCount} entries")
                    distortionTypes.clear()  // 이전 데이터를 지우고 시작
                    for (typeSnapshot in snapshot.children) {
                        if (typeSnapshot.value is List<*>) {
                            val thoughtSetList = typeSnapshot.value as List<*>
                            for (thoughtSet in thoughtSetList) {
                                if (thoughtSet is Map<*, *>) {
                                    val characterDescription = (thoughtSet["characterDescription"] as? List<*>)?.joinToString("\n") { it.toString() } ?: ""
                                    Log.d("DistortionTypeFiller", "Fetched characterDescription: '$characterDescription'")

                                    val imageResource = thoughtSet["imageResource"]?.toString() ?: ""
                                    Log.d("DistortionTypeFiller", "Fetched imageResource: '$imageResource'")

                                    val imageResId = getImageResId(imageResource)
                                    Log.d("DistortionTypeFiller", "Image resource: $imageResource, Image res ID: $imageResId")

                                    val selectedThoughts = thoughtSet["selectedThoughts"]?.toString() ?: ""
                                    val alternativeThoughts = thoughtSet["alternativeThoughts"]?.toString() ?: ""
                                    val charactersReason = thoughtSet["charactersReason"]?.toString() ?: ""
                                    val alternativeThoughtsReason = thoughtSet["alternativeThoughtsReason"]?.toString() ?: ""

                                    // DistortionType 객체 생성 (필요한 모든 매개변수 추가)
                                    val distortionType = DistortionType(
                                        type = typeSnapshot.key ?: "알 수 없음",
                                        subtitle = characterDescription,
                                        imageResId = imageResId,
                                        detailTitle = "${typeSnapshot.key} 이 나온 생각이에요",
                                        detail = selectedThoughts,
                                        extendedDetail = charactersReason,
                                        alternativeThought = alternativeThoughts,
                                        alternativeExtendedDetail = alternativeThoughtsReason,
                                        detail2 = "", // 적절한 값을 추가
                                        extendedDetail2 = "", // 적절한 값을 추가
                                        alternativeThought2 = "", // 적절한 값을 추가
                                        alternativeExtendedDetail2 = "", // 적절한 값을 추가
                                        detail3 = "", // 적절한 값을 추가
                                        extendedDetail3 = "", // 적절한 값을 추가
                                        alternativeThought3 = "", // 적절한 값을 추가
                                        alternativeExtendedDetail3 = "" // 적절한 값을 추가
                                    )

                                    distortionTypes.add(distortionType)
                                    Log.d("DistortionTypeFiller", "Added distortion type: $distortionType")
                                }
                            }
                        }
                    }

                    if (distortionTypes.isNotEmpty()) {
                        Log.d("DistortionTypeFiller", "Data loaded successfully, invoking listener.")
                        dataLoadedListener?.invoke()
                    } else {
                        Log.d("DistortionTypeFiller", "No distortion types found.")
                    }
                } else {
                    Log.d("DistortionTypeFiller", "No data found for the given userId and diaryId.")
                }
            }

            override fun onCancelled(error: DatabaseError) {
                Log.e("FirebaseError", error.message)
            }
        })
    }


    fun setOnDataLoadedListener(listener: () -> Unit) {
        dataLoadedListener = listener // 리스너
    }

    fun getDistortionTypes(): List<DistortionType> { // 추가된 메서드
        return distortionTypes
    }

    private fun getImageResId(imageResource: String): Int {
        return when (imageResource) {
            "ic_planet_a" -> R.drawable.ic_planet_a
            "ic_planet_b" -> R.drawable.ic_planet_b
            "ic_planet_c" -> R.drawable.ic_planet_c
            "ic_planet_d" -> R.drawable.ic_planet_d
            "ic_planet_e" -> R.drawable.ic_planet_e
            "ic_planet_f" -> R.drawable.ic_planet_f
            "ic_planet_g" -> R.drawable.ic_planet_g
            "ic_planet_h" -> R.drawable.ic_planet_h
            "ic_planet_i" -> R.drawable.ic_planet_i
            "ic_planet_j" -> R.drawable.ic_planet_j
            "ic_planet_k" -> R.drawable.ic_planet_k
            "ic_planet_l" -> R.drawable.ic_planet_l
            else -> R.drawable.ic_planet_a // 기본 이미지 ID (존재하지 않는 경우)
        }
    }
}
