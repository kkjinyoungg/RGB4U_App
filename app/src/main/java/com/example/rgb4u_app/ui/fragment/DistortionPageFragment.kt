package com.example.rgb4u_app.ui.fragment

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import android.widget.TextView
import androidx.fragment.app.Fragment
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.example.rgb4u_app.R
import com.example.rgb4u_app.ui.activity.distortiontype.DistortionData
import com.example.rgb4u_app.ui.activity.distortiontype.SentenceAdapter

class DistortionTypeFragment : Fragment() {

    private lateinit var planetImageView: ImageView
    private lateinit var planetNameTextView: TextView
    private lateinit var shortMessageTextView: TextView
    private lateinit var recyclerView: RecyclerView
    private lateinit var adapter: SentenceAdapter // 변경된 어댑터

    private val distortionDataList = listOf<DistortionData>() // 적절한 데이터로 초기화

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        // fragment_distortion_page.xml 레이아웃을 inflate
        val view = inflater.inflate(R.layout.fragment_distortion_page, container, false)

        // 뷰 찾기
        planetImageView = view.findViewById(R.id.iv_planet_image)
        planetNameTextView = view.findViewById(R.id.tv_planet_name)
        shortMessageTextView = view.findViewById(R.id.tv_short_message)
        recyclerView = view.findViewById(R.id.recycler_view_sentences)

        // 데이터 설정 (예시)
        planetImageView.setImageResource(R.drawable.ic_planet_a) // 이미지 설정
        planetNameTextView.text = "행성 A" // 행성 이름 설정
        shortMessageTextView.text = "짧은 메시지" // 짧은 메시지 설정

        // RecyclerView 설정
        recyclerView.layoutManager = LinearLayoutManager(context)
        adapter = SentenceAdapter(distortionDataList) // SentenceAdapter로 변경
        recyclerView.adapter = adapter

        return view
    }
}
