package com.example.rgb4u_app.ui.activity.distortiontype

import android.util.Log
import androidx.fragment.app.Fragment
import androidx.fragment.app.FragmentActivity
import androidx.viewpager2.adapter.FragmentStateAdapter
import androidx.viewpager2.widget.ViewPager2
import com.example.rgb4u_app.ui.fragment.DistortionFragment

class DistortionPagerAdapter(
    activity: FragmentActivity,
    private val viewPager: ViewPager2 // ViewPager2를 인자로 추가
) : FragmentStateAdapter(activity) {

    override fun getItemCount(): Int {
        return DistortionData.distortionTypes.size // DistortionData의 크기를 사용
    }

    override fun createFragment(position: Int): Fragment {
        // DistortionData에서 현재 위치의 데이터를 가져옴
        val distortionType = DistortionData.distortionTypes[position]
        return DistortionFragment.newInstance(distortionType, viewPager, this) // distortionType을 전달
    }

    fun updateData() {
        Log.d("DistortionPagerAdapter", "Current distortionTypes size: ${DistortionData.distortionTypes.size}")
        notifyDataSetChanged() // 데이터가 변경되었음을 UI에 알림
    }
}
