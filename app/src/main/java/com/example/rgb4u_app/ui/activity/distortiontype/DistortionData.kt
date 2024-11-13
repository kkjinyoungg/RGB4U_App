package com.example.rgb4u_app.ui.activity.distortiontype

import android.os.Parcelable
import com.example.rgb4u_app.R
import kotlinx.parcelize.Parcelize

@Parcelize
data class DistortionType(
    val type: String,
    val subtitle: String,
    val imageResId: Int,
    val detailTitle: String,
    val detail: String,
    val extendedDetail: String,
    val alternativeThought: String,
    val alternativeExtendedDetail: String,
    val detail2: String,
    val extendedDetail2: String,
    val alternativeThought2: String,
    val alternativeExtendedDetail2: String,
    val detail3: String,
    val extendedDetail3: String,
    val alternativeThought3: String,
    val alternativeExtendedDetail3: String
) : Parcelable

object DistortionData {
    val distortionTypes = mutableListOf<DistortionType>()
}
