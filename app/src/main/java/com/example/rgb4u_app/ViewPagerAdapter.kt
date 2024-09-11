package com.example.rgb4u_app

import androidx.fragment.app.Fragment
import androidx.fragment.app.FragmentActivity
import androidx.viewpager2.adapter.FragmentStateAdapter

class ViewPagerAdapter(fragmentActivity: FragmentActivity) : FragmentStateAdapter(fragmentActivity) {

    val emotions = listOf("놀람", "두려움", "슬픔", "분노", "혐오")



    override fun getItemCount(): Int = emotions.size

    override fun createFragment(position: Int): Fragment {
        return EmotionFragment.newInstance(emotions[position])
    }
}

