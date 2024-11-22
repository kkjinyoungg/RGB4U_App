package com.example.rgb4u_app.ui.activity.distortiontype

import android.util.Log
import androidx.fragment.app.Fragment
import androidx.fragment.app.FragmentActivity
import androidx.viewpager2.adapter.FragmentStateAdapter
import androidx.viewpager2.widget.ViewPager2
import com.example.rgb4u_app.DistortionTypeFiller // DistortionTypeFiller를 임포트
import com.example.rgb4u_app.ui.fragment.DistortionFragment

class DistortionPagerAdapter(
    activity: FragmentActivity,
    private val viewPager: ViewPager2,
    private val userId: String,
    private val diaryId: String
) : FragmentStateAdapter(activity) {

    private val distortionFiller = DistortionTypeFiller()

    init {
        distortionFiller.setOnDataLoadedListener {
            updateData()
        }
        distortionFiller.initialize(userId, diaryId)
    }

    override fun getItemCount(): Int {
        return distortionFiller.getDistortionTypes().size
    }

    override fun createFragment(position: Int): Fragment {
        val distortionType = distortionFiller.getDistortionTypes()[position]
        return DistortionFragment.newInstance(distortionType, position, getItemCount(), viewPager, this)
    }

    fun updateData() {
        notifyDataSetChanged()
    }
}
