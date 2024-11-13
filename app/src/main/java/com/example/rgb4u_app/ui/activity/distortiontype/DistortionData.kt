package com.example.rgb4u_app.ui.activity.distortiontype

import android.os.Parcelable
import com.example.rgb4u_app.R
import kotlinx.parcelize.Parcelize

@Parcelize
data class DistortionType(
    val type: String, // 유형 제목
    val subtitle: String, // 유형 부제목
    val imageResId: String, // 이미지 리소스 String으로 변경 (Int에서)
    val detailTitle: String, // 상세 설명 제목
    val detail: String, // 상세 설명
    val extendedDetail: String, // 추가적인 상세 설명
    val alternativeThought: String, // 대안적 생각
    val alternativeExtendedDetail: String, // 대안적 추가 상세 설명

    val detail2: String, // 상세 설명2 - DistortionMoreThoughtsActivity
    val extendedDetail2: String, // 추가적인 상세 설명 - DistortionMoreThoughtsActivity
    val alternativeThought2: String, // 대안적 생각 - DistortionMoreThoughtsActivity
    val alternativeExtendedDetail2: String, // 대안적 추가 상세 설명 - DistortionMoreThoughtsActivity

    val detail3: String, // 상세 설명3 - DistortionMoreThoughtsActivity
    val extendedDetail3: String, // 추가적인 상세 설명 - DistortionMoreThoughtsActivity
    val alternativeThought3: String, // 대안적 생각 - DistortionMoreThoughtsActivity
    val alternativeExtendedDetail3: String // 대안적 추가 상세 설명
) : Parcelable

object DistortionData {
    val distortionTypes = listOf(
        DistortionType(
            type = "",
            subtitle = "",
            detailTitle = "",
            detail = "",
            extendedDetail = "",
            alternativeThought = "",
            alternativeExtendedDetail = "",
            imageResId = "", // 실제 이미지 리소스 ID

            detail2 = "",
            extendedDetail2 = "",
            alternativeThought2 = "",
            alternativeExtendedDetail2 = "",

            detail3 = "",
            extendedDetail3 = "",
            alternativeThought3 = "",
            alternativeExtendedDetail3 = ""
        ),
        DistortionType(
            type = "",
            subtitle = "",
            detailTitle = "",
            detail = "",
            extendedDetail = "",
            alternativeThought = "",
            alternativeExtendedDetail = "",
            imageResId = "", // 실제 이미지 리소스 ID

            detail2 = "",
            extendedDetail2 = "",
            alternativeThought2 = "",
            alternativeExtendedDetail2 = "",

            detail3 = "",
            extendedDetail3 = "",
            alternativeThought3 = "",
            alternativeExtendedDetail3 = ""
        ),
        DistortionType(
            type = "",
            subtitle = "",
            detailTitle = "",
            detail = "",
            extendedDetail = "",
            alternativeThought = "",
            alternativeExtendedDetail = "",
            imageResId = "", // 실제 이미지 리소스 ID

            detail2 = "",
            extendedDetail2 = "",
            alternativeThought2 = "",
            alternativeExtendedDetail2 = "",

            detail3 = "",
            extendedDetail3 = "",
            alternativeThought3 = "",
            alternativeExtendedDetail3 = ""
        )
    )
}
