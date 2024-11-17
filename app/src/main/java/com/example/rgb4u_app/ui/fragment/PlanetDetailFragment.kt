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
import com.google.firebase.database.DatabaseReference
import com.google.firebase.database.FirebaseDatabase
import com.google.firebase.database.DataSnapshot
import com.google.firebase.database.DatabaseError
import com.google.firebase.database.ValueEventListener
import android.util.Log
import android.widget.Toast
import com.google.firebase.auth.FirebaseAuth

class PlanetDetailFragment : Fragment() {

    private var typeName: String = "Default Title" // 멤버 변수로 선언
    private var formattedDate2: String = ""  // formattedDate2를 변수로 선언

    companion object {
        private const val ARG_TYPE_NAME = "type_name"
        private const val ARG_IMAGE_RESOURCE_ID = "image_resource_id"

        // PlanetDetailFragment 생성 시 typeName과 imageResourceId를 전달
        fun newInstance(typeName: String, imageResourceId: Int, formattedDate2: String): PlanetDetailFragment {
            val fragment = PlanetDetailFragment()
            val args = Bundle().apply {
                putString(ARG_TYPE_NAME, typeName)
                putInt(ARG_IMAGE_RESOURCE_ID, imageResourceId)
                putString("formattedDate2", formattedDate2) // formattedDate2를 전달
            }
            fragment.arguments = args
            // formattedDate2 로그 출력
            Log.d("PlanetDetailFragment", "formattedDate2: $formattedDate2")

            return fragment
        }
    }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        // Fragment의 레이아웃을 인플레이트하고 rootView에 저장
        val rootView = inflater.inflate(R.layout.fragment_planet_detail, container, false)

        // 현재 로그인된 사용자 ID 가져오기
        val userId = FirebaseAuth.getInstance().currentUser?.uid
        Log.d("AnalysisFragment", "현재 로그인된 사용자 ID: $userId")

        if (userId == null) {
            Toast.makeText(context, "사용자가 로그인되어 있지 않습니다.", Toast.LENGTH_SHORT).show()
            return rootView
        }

        // Firebase Realtime Database 참조 생성
        val database: FirebaseDatabase = FirebaseDatabase.getInstance()
        val distortionRef: DatabaseReference = database.getReference("users/$userId/distortionInformation")

        val imageResourceId = arguments?.getInt(ARG_IMAGE_RESOURCE_ID)
        val planetImageView: ImageView = rootView.findViewById(R.id.planet_image)
        imageResourceId?.let {
            planetImageView.setImageResource(it)
        }

        // formattedDate2 값 설정
        formattedDate2 = arguments?.getString("formattedDate2") ?: "2024-09"
        Log.d("PlanetDetailFragment", "formattedDate2 값: $formattedDate2")

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

        // 세미타이틀 및 소개글 텍스트뷰 가져오기
        val semiTitle: TextView = rootView.findViewById(R.id.planet_additional_info)
        val shortDescription: TextView = rootView.findViewById(R.id.planet_short_description)

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

        // fetchDistortionData 호출
        fetchDistortionData(distortionRef, typeName, semiTitle, shortDescription)

        return rootView
    }

    private fun fetchDistortionData(
        distortionRef: DatabaseReference,
        typeName: String,
        semiTitle: TextView,
        shortDescription: TextView
    ) {
        distortionRef.child(typeName).addListenerForSingleValueEvent(object : ValueEventListener {
            override fun onDataChange(snapshot: DataSnapshot) {
                if (snapshot.exists()) {
                    // Firebase 데이터 매핑
                    val subtitle = snapshot.child("subtitle").value as? String ?: "Subtitle 없음"
                    val description1 = snapshot.child("description1").value as? String ?: "Description1 없음"
                    val description2 = snapshot.child("description2").value as? String ?: "Description2 없음"

                    // 텍스트뷰에 데이터 설정
                    semiTitle.text = subtitle
                    shortDescription.text = "$description1\n$description2"
                } else {
                    semiTitle.text = "데이터 없음"
                    shortDescription.text = "데이터 없음"
                }
            }

            override fun onCancelled(error: DatabaseError) {
                semiTitle.text = "오류 발생"
                shortDescription.text = "오류 발생: ${error.message}"
            }
        })
    }
}
