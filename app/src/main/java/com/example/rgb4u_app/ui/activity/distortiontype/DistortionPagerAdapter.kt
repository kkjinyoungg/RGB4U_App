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
    private val viewPager: ViewPager2, // ViewPager2를 인자로 추가
    private val userId: String, // 사용자 ID를 인자로 추가
    private val diaryId: String // 다이어리 ID를 인자로 추가
) : FragmentStateAdapter(activity) {

    private val distortionFiller = DistortionTypeFiller() // DistortionTypeFiller 인스턴스 생성

    init {
        //데이터 초기화 및 로드
        distortionFiller.setOnDataLoadedListener {
            updateData() // 데이터 로드 후 UI 업데이트 호출
        }
        distortionFiller.initialize(userId, diaryId) //실제 사용자 ID와 다이어리 ID 전달
    }

    override fun getItemCount(): Int {
        return distortionFiller.getDistortionTypes().size //DistortionFiller의 데이터 크기를 사용
    }

    override fun createFragment(position: Int): Fragment {
        // DistortionFiller에서 현재 위치의 데이터를 가져옴
        val distortionType = distortionFiller.getDistortionTypes()[position] //DistortionFiller에서 데이터 가져오기
        return DistortionFragment.newInstance(distortionType, viewPager, this) // distortionType을 전달
    }

    fun updateData() {
        Log.d("DistortionPagerAdapter", "Current distortionTypes size: ${distortionFiller.getDistortionTypes().size}") // ❤️ Filler에서 데이터 사이즈 로그
        notifyDataSetChanged() // 데이터가 변경되었음을 UI에 알림
    }
}
