package com.example.rgb4u_app.ui.activity.distortiontype

import android.os.Parcelable
import com.example.rgb4u_app.R
import kotlinx.parcelize.Parcelize

@Parcelize
data class DistortionType(
    val type: String, // 유형 제목
    val subtitle: String, // 유형 부제목
    val imageResId: Int, // 이미지 리소스 ID
    val detailTitle: String, // 상세 설명 제목
    var detail: String, // 상세 설명 - var로 변경
    var extendedDetail: String, // 추가적인 상세 설명 - var로 변경
    var alternativeThought: String, // 대안적 생각 - var로 변경
    var alternativeExtendedDetail: String, // 대안적 추가 상세 설명 - var로 변경

    var detail2: String, // 상세 설명2 - var로 변경
    var extendedDetail2: String, // 추가적인 상세 설명 - var로 변경
    var alternativeThought2: String, // 대안적 생각 - var로 변경
    var alternativeExtendedDetail2: String, // 대안적 추가 상세 설명 - var로 변경

    var detail3: String, // 상세 설명3 - var로 변경
    var extendedDetail3: String, // 추가적인 상세 설명 - var로 변경
    var alternativeThought3: String, // 대안적 생각 - var로 변경
    var alternativeExtendedDetail3: String // 대안적 추가 상세 설명 - var로 변경
) : Parcelable


object DistortionData {
    val distortionTypes = listOf(
        DistortionType(
            type = "흑백성",
            subtitle = "흑백성은 부정적인 것은 훨씬 크게, 긍정적인 것은 훨씬 작게 생각하게 해요",
            detailTitle = "흑백성이 나온 생각이에요",
            detail = "시험에서 100점 맞지 않으면 난 바보야. 시험에서 100점 맞지 않으면 난 바보야.",
            extendedDetail = "추가적인 상세 설명이 여기에 들어갑니다.",
            alternativeThought = "엄마는 왜 만날 나를 괴롭히고 내가 사는 걸 싫어하고 내가 행복한 꼴을 못 보실까?",
            alternativeExtendedDetail = "추가적인 상세 설명이 여기에 들어갑니다.",
            imageResId = R.drawable.ic_planet_a, // 실제 이미지 리소스 ID

            detail2 = "시험에서 100점 맞지 않으면 난 바보야. 시험에서 100점 맞지 않으면 난 바보야.",
            extendedDetail2 = "추가적인 상세 설명이 여기에 들어갑니다.",
            alternativeThought2 = "엄마는 왜 만날 나를 괴롭히고 내가 사는 걸 싫어하고 내가 행복한 꼴을 못 보실까?",
            alternativeExtendedDetail2 = "추가적인 상세 설명이 여기에 들어갑니다.",

            detail3 = "시험에서 100점 맞지 않으면 난 바보야. 시험에서 100점 맞지 않으면 난 바보야.",
            extendedDetail3 = "추가적인 상세 설명이 여기에 들어갑니다.",
            alternativeThought3 = "엄마는 왜 만날 나를 괴롭히고 내가 사는 걸 싫어하고 내가 행복한 꼴을 못 보실까?",
            alternativeExtendedDetail3 = "추가적인 상세 설명이 여기에 들어갑니다."
        ),
        DistortionType(
            type = "과장성",
            subtitle = "과장성은 부정적인 것은 훨씬 크게, 긍정적인 것은 훨씬 작게 생각하게 해요",
            detailTitle = "과장성이 나온 생각이에요",
            detail = "시험에서 100점 맞지 않으면 난 바보야. 시험에서 100점 맞지 않으면 난 바보야.",
            extendedDetail = "추가적인 상세 설명이 여기에 들어갑니다.",
            alternativeThought = "엄마는 왜 만날 나를 괴롭히고 내가 사는 걸 싫어하고 내가 행복한 꼴을 못 보실까?",
            alternativeExtendedDetail = "추가적인 상세 설명이 여기에 들어갑니다.",
            imageResId = R.drawable.ic_planet_a, // 실제 이미지 리소스 ID

            detail2 = "시험에서 100점 맞지 않으면 난 바보야. 시험에서 100점 맞지 않으면 난 바보야.",
            extendedDetail2 = "추가적인 상세 설명이 여기에 들어갑니다.",
            alternativeThought2 = "엄마는 왜 만날 나를 괴롭히고 내가 사는 걸 싫어하고 내가 행복한 꼴을 못 보실까?",
            alternativeExtendedDetail2 = "추가적인 상세 설명이 여기에 들어갑니다.",

            detail3 = "시험에서 100점 맞지 않으면 난 바보야. 시험에서 100점 맞지 않으면 난 바보야.",
            extendedDetail3 = "추가적인 상세 설명이 여기에 들어갑니다.",
            alternativeThought3 = "엄마는 왜 만날 나를 괴롭히고 내가 사는 걸 싫어하고 내가 행복한 꼴을 못 보실까?",
            alternativeExtendedDetail3 = "추가적인 상세 설명이 여기에 들어갑니다."
        ),
        DistortionType(
            type = "궁예썽",
            subtitle = "궁예썽은 부정적인 것은 훨씬 크게, 긍정적인 것은 훨씬 작게 생각하게 해요",
            detailTitle = "궁예썽이 나온 생각이에요",
            detail = "시험에서 100점 맞지 않으면 난 바보야. 시험에서 100점 맞지 않으면 난 바보야.",
            extendedDetail = "추가적인 상세 설명이 여기에 들어갑니다.",
            alternativeThought = "엄마는 왜 만날 나를 괴롭히고 내가 사는 걸 싫어하고 내가 행복한 꼴을 못 보실까?",
            alternativeExtendedDetail = "추가적인 상세 설명이 여기에 들어갑니다.",
            imageResId = R.drawable.ic_planet_a, // 실제 이미지 리소스 ID

            detail2 = "시험에서 100점 맞지 않으면 난 바보야. 시험에서 100점 맞지 않으면 난 바보야.",
            extendedDetail2 = "추가적인 상세 설명이 여기에 들어갑니다.",
            alternativeThought2 = "엄마는 왜 만날 나를 괴롭히고 내가 사는 걸 싫어하고 내가 행복한 꼴을 못 보실까?",
            alternativeExtendedDetail2 = "추가적인 상세 설명이 여기에 들어갑니다.",

            detail3 = "시험에서 100점 맞지 않으면 난 바보야. 시험에서 100점 맞지 않으면 난 바보야.",
            extendedDetail3 = "추가적인 상세 설명이 여기에 들어갑니다.",
            alternativeThought3 = "엄마는 왜 만날 나를 괴롭히고 내가 사는 걸 싫어하고 내가 행복한 꼴을 못 보실까?",
            alternativeExtendedDetail3 = "추가적인 상세 설명이 여기에 들어갑니다."
        )
    )
}