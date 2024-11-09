package com.example.rgb4u_app.ui.fragment

import android.content.Intent
import android.graphics.Paint
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import android.widget.TextView
import androidx.fragment.app.Fragment
import androidx.viewpager2.widget.ViewPager2
import com.example.rgb4u_app.R
import com.example.rgb4u_app.ui.activity.distortiontype.DistortionMoreThoughtsActivity
import com.example.rgb4u_app.ui.activity.distortiontype.DistortionPagerAdapter
import com.example.rgb4u_app.ui.activity.distortiontype.DistortionType

class DistortionFragment : Fragment() {

    private lateinit var viewPager: ViewPager2
    private lateinit var pagerAdapter: DistortionPagerAdapter
    private lateinit var distortionType: DistortionType

    companion object {
        fun newInstance(distortionType: DistortionType, viewPager: ViewPager2, adapter: DistortionPagerAdapter): DistortionFragment {
            val fragment = DistortionFragment()
            fragment.distortionType = distortionType // 전달된 DistortionType을 저장
            return fragment
        }
    }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        return inflater.inflate(R.layout.fragment_distortion, container, false)
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        val titleTextView = view.findViewById<TextView>(R.id.tv_type_title) // 유형 타입
        titleTextView.text = distortionType.type

        val subtitleTextView = view.findViewById<TextView>(R.id.tv_type_subtitle) // 유형 한줄 소개
        subtitleTextView.text = distortionType.subtitle

        view.findViewById<ImageView>(R.id.iv_type_image).setImageResource(distortionType.imageResId) // 이미지 리소스 ID

        view.findViewById<TextView>(R.id.tv_type_detail_title).text = distortionType.detailTitle // 유형에 대한 생각입니다

        val detailTextView = view.findViewById<TextView>(R.id.tv_type_detail) // 유형에 대한 상세 설명
        detailTextView.text = distortionType.detail // 중복없이 한 번만 설정

        val extendedDetailTextView = view.findViewById<TextView>(R.id.tv_type_detail_extended) // 유형에 대한 자세한 설명
        extendedDetailTextView.text  = distortionType.extendedDetail

        val moreThoughtsTextView = view.findViewById<TextView>(R.id.tv_more_thoughts)

        val alternativeThoughtTextView = view.findViewById<TextView>(R.id.tv_alternative_thought) // 유형에 대한 대안적 생각
        alternativeThoughtTextView.text = distortionType.alternativeThought

        val alternativeExtendedDetailTextView = view.findViewById<TextView>(R.id.tv_alternative_detail_extended) // 유형에 대한 자세한 대안적 생각
        alternativeExtendedDetailTextView.text = distortionType.alternativeExtendedDetail



        // "다른 생각도 더 보실래요?"에 밑줄 효과 추가
        moreThoughtsTextView.paintFlags = Paint.UNDERLINE_TEXT_FLAG

        // 클릭 리스너
        moreThoughtsTextView.setOnClickListener {
            val context = requireContext()
            val intent = Intent(context, DistortionMoreThoughtsActivity::class.java)
            intent.putExtra("distortionType", distortionType) // 전달할 DistortionType 객체 추가
            context.startActivity(intent)
        }

        // 토글 관련 초기화
        val toggleDetailTextView = view.findViewById<TextView>(R.id.tv_toggle_detail)
        val btnToggleDetail = view.findViewById<ImageView>(R.id.btn_toggle_detail)

        val alternativeToggleTextView = view.findViewById<TextView>(R.id.tv_alternative_detail_toggle)
        val btnAlternativeToggle = view.findViewById<ImageView>(R.id.btn_alternative_detail_toggle)


        // 첫 번째 토글 기능
        toggleDetailTextView.setOnClickListener {
            toggleView(extendedDetailTextView, btnToggleDetail, R.drawable.ic_toggle_up, R.drawable.ic_toggle_down)
        }

        btnToggleDetail.setOnClickListener {
            toggleView(extendedDetailTextView, btnToggleDetail, R.drawable.ic_toggle_up, R.drawable.ic_toggle_down)
        }

        // 두 번째 토글 기능
        alternativeToggleTextView.setOnClickListener {
            toggleView(alternativeExtendedDetailTextView, btnAlternativeToggle, R.drawable.ic_toggle_up, R.drawable.ic_toggle_down)
        }

        btnAlternativeToggle.setOnClickListener {
            toggleView(alternativeExtendedDetailTextView, btnAlternativeToggle, R.drawable.ic_toggle_up, R.drawable.ic_toggle_down)
        }
    }

    private fun toggleView(extendedView: TextView, toggleButton: ImageView, upDrawable: Int, downDrawable: Int) {
        if (extendedView.visibility == View.GONE) {
            extendedView.visibility = View.VISIBLE
            toggleButton.setImageResource(upDrawable) // 올리는 아이콘으로 변경
        } else {
            extendedView.visibility = View.GONE
            toggleButton.setImageResource(downDrawable) // 내리는 아이콘으로 변경
        }
    }
}