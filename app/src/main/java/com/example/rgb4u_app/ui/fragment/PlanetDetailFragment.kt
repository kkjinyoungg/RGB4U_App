package com.example.rgb4u_app.ui.fragment

import android.content.Intent
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageButton
import android.widget.TextView
import androidx.appcompat.app.AppCompatActivity
import androidx.appcompat.widget.Toolbar
import androidx.fragment.app.Fragment
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.example.rgb4u_app.R
import com.example.rgb4u_app.ui.activity.analysis.AnalysisActivity
import com.example.rgb4u_app.ui.activity.analysis.BoxData
import com.example.rgb4u_app.ui.activity.analysis.PlanetDetailBoxAdapter
import android.widget.ImageView

class PlanetDetailFragment : Fragment() {

    private var typeName: String = "Default Title" // 멤버 변수로 선언

    companion object {
        private const val ARG_TYPE_NAME = "type_name"
        private const val ARG_IMAGE_RESOURCE_ID = "image_resource_id"

        // PlanetDetailFragment 생성 시 typeName과 imageResourceId를 전달
        fun newInstance(typeName: String, imageResourceId: Int): PlanetDetailFragment {
            val fragment = PlanetDetailFragment()
            val args = Bundle().apply {
                putString(ARG_TYPE_NAME, typeName)
                putInt(ARG_IMAGE_RESOURCE_ID, imageResourceId)
            }
            fragment.arguments = args
            return fragment
        }
    }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        // Fragment의 레이아웃을 인플레이트하고 rootView에 저장
        val rootView = inflater.inflate(R.layout.fragment_planet_detail, container, false)


        val imageResourceId = arguments?.getInt(ARG_IMAGE_RESOURCE_ID)
        val planetImageView: ImageView = rootView.findViewById(R.id.planet_image)
        imageResourceId?.let {
            planetImageView.setImageResource(it)
        }

        // Toolbar 설정
        val toolbar: Toolbar = rootView.findViewById(R.id.toolbar)
        val activity = requireActivity() as AppCompatActivity
        activity.setSupportActionBar(toolbar)

        // 기본 뒤로가기 버튼, 앱 이름 숨기기
        activity.supportActionBar?.setDisplayHomeAsUpEnabled(false)
        activity.supportActionBar?.setDisplayShowTitleEnabled(false)

        val toolbarTitle: TextView = rootView.findViewById(R.id.toolbar_base1_title)
        // 전달받은 typeName을 toolbarTitle에 설정
        typeName = arguments?.getString(ARG_TYPE_NAME) ?: "Default Title"
        toolbarTitle.text = typeName

        val mainTitle: TextView = rootView.findViewById(R.id.planet_description)
        // typeName에 "이 나온 생각이에요"를 붙여 titleName을 설정
        val titleName = "${typeName}이 나온 생각이에요"
        mainTitle.text = titleName

        // button_write_action2 버튼 숨기기
        val buttonWriteAction2: ImageButton = rootView.findViewById(R.id.button_base1_action2)
        buttonWriteAction2.visibility = View.GONE

        // button_write_action1 클릭 리스너 추가
        val buttonWriteAction1: ImageButton = rootView.findViewById(R.id.button_base1_action1)
        buttonWriteAction1.setOnClickListener {
            // CalendarHomeActivity로 이동
            val intent = Intent(requireContext(), AnalysisActivity::class.java)
            startActivity(intent)
            // Fragment에서는 finish()를 사용할 수 없으므로 activity?.finish()를 사용
            activity?.finish()
        }

        // 샘플 데이터 생성
        val boxDataList = listOf(
            BoxData("가족들이랑 여행을 가서 엄마와 한 방에서 잤는데...", "7월 23일 화요일"),
            BoxData("나는 일등이야 끝났어.", "7월 15일 월요일"),
            BoxData("급식은 맵거나 맛있거나 맛없다~", "7월 10일 수요일")
            // 더 많은 데이터를 추가할 수 있습니다.
        )

        // planet_counter TextView 설정
        val planetCounter: TextView = rootView.findViewById(R.id.planet_counter)
        planetCounter.text = boxDataList.size.toString()  // boxDataList의 크기 설정

        // RecyclerView 설정
        val recyclerView = rootView.findViewById<RecyclerView>(R.id.boxTextrecyclerView)
        recyclerView.layoutManager = LinearLayoutManager(requireContext())
        recyclerView.adapter = PlanetDetailBoxAdapter(boxDataList)

        return rootView
    }
}
