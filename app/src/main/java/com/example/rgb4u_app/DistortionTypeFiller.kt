package com.example.rgb4u_app

import android.util.Log
import com.example.rgb4u_app.ui.activity.distortiontype.DistortionType
import com.google.firebase.database.*

class DistortionTypeFiller {

    private lateinit var database: DatabaseReference
    private val distortionTypes = mutableListOf<DistortionType>()

    // 사용자 ID와 다이어리 ID를 매개변수로 받는 생성자
    fun initialize(userId: String, diaryId: String) {
        database = FirebaseDatabase.getInstance().getReference("users/$userId/diaries/$diaryId/aiAnalysis/secondAnalysis/thoughtSets")
        fetchDistortionData()
    }

    private fun fetchDistortionData() {
        database.addListenerForSingleValueEvent(object : ValueEventListener {
            override fun onDataChange(snapshot: DataSnapshot) {
                for (typeSnapshot in snapshot.children) {
                    val type = typeSnapshot.key ?: continue // 키(유형 이름)를 가져옴

                    // 데이터가 존재하는지 확인
                    if (typeSnapshot.exists()) {
                        val characterDescription = typeSnapshot.child("characterDescription").children.joinToString("\n") { it.value?.toString() ?: "" }
                        val imageResource = typeSnapshot.child("imageResource").value?.toString() ?: ""
                        // 이미지 리소스 ID 변환
                        val imageResId = getImageResId(imageResource) // 함수 호출
                        // 각 세부 사항 가져오기
                        val detail1 = typeSnapshot.child("1").child("selectedThoughts").value?.toString() ?: ""
                        val extendedDetail1 = typeSnapshot.child("1").child("charactersReason").value?.toString() ?: ""
                        val alternativeThought1 = typeSnapshot.child("1").child("alternativeThoughts").value?.toString() ?: ""
                        val alternativeExtendedDetail1 = typeSnapshot.child("1").child("alternativeThoughtsReason").value?.toString() ?: ""

                        val detail2 = typeSnapshot.child("2").child("selectedThoughts").value?.toString() ?: ""
                        val extendedDetail2 = typeSnapshot.child("2").child("charactersReason").value?.toString() ?: ""
                        val alternativeThought2 = typeSnapshot.child("2").child("alternativeThoughts").value?.toString() ?: ""
                        val alternativeExtendedDetail2 = typeSnapshot.child("2").child("alternativeThoughtsReason").value?.toString() ?: ""

                        val detail3 = typeSnapshot.child("3").child("selectedThoughts").value?.toString() ?: ""
                        val extendedDetail3 = typeSnapshot.child("3").child("charactersReason").value?.toString() ?: ""
                        val alternativeThought3 = typeSnapshot.child("3").child("alternativeThoughts").value?.toString() ?: ""
                        val alternativeExtendedDetail3 = typeSnapshot.child("3").child("alternativeThoughtsReason").value?.toString() ?: ""

                        // DistortionType 객체 생성
                        val distortionType = DistortionType(
                            type = type,
                            subtitle = characterDescription,
                            imageResId = imageResId, // 변환된 리소스 ID 사용
                            detailTitle = "$type 이 나온 생각이에요",
                            detail = detail1,
                            extendedDetail = extendedDetail1,
                            alternativeThought = alternativeThought1,
                            alternativeExtendedDetail = alternativeExtendedDetail1,
                            detail2 = detail2,
                            extendedDetail2 = extendedDetail2,
                            alternativeThought2 = alternativeThought2,
                            alternativeExtendedDetail2 = alternativeExtendedDetail2,
                            detail3 = detail3,
                            extendedDetail3 = extendedDetail3,
                            alternativeThought3 = alternativeThought3,
                            alternativeExtendedDetail3 = alternativeExtendedDetail3
                        )

                        // distortionTypes 리스트에 추가
                        distortionTypes.add(distortionType)
                    }
                }

                // distortionTypes 리스트를 사용하여 필요한 작업 수행
                Log.d("DistortionData", distortionTypes.toString())
            }

            override fun onCancelled(error: DatabaseError) {
                Log.e("FirebaseError", error.message)
            }
        })
    }

    private fun getImageResId(imageResource: String): Int {
        return when (imageResource) {
            "ic_planet_a" -> R.drawable.ic_planet_a
            "ic_planet_b" -> R.drawable.ic_planet_a
            "ic_planet_c" -> R.drawable.ic_planet_a
            "ic_planet_d" -> R.drawable.ic_planet_a
            "ic_planet_e" -> R.drawable.ic_planet_a
            "ic_planet_f" -> R.drawable.ic_planet_a
            "ic_planet_g" -> R.drawable.ic_planet_a
            "ic_planet_h" -> R.drawable.ic_planet_a
            "ic_planet_i" -> R.drawable.ic_planet_a
            "ic_planet_j" -> R.drawable.ic_planet_a
            "ic_planet_k" -> R.drawable.ic_planet_a
            "ic_planet_l" -> R.drawable.ic_planet_a
            else -> R.drawable.ic_planet_a // 기본 이미지 ID (존재하지 않는 경우)
        }
    }
}
