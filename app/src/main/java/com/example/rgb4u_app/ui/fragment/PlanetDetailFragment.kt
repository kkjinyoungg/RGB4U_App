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
    private lateinit var rootView: View

    // boxDataList를 클래스 레벨에서 선언
    private var boxDataList = mutableListOf<BoxData>()

    companion object {
        private const val ARG_TYPE_NAME = "type_name"
        private const val ARG_IMAGE_RESOURCE_ID = "image_resource_id"

        fun newInstance(typeName: String, imageResourceId: Int, formattedDate2: String): PlanetDetailFragment {
            val fragment = PlanetDetailFragment()
            val args = Bundle().apply {
                putString(ARG_TYPE_NAME, typeName)
                putInt(ARG_IMAGE_RESOURCE_ID, imageResourceId)
                putString("formattedDate2", formattedDate2)
            }
            fragment.arguments = args
            return fragment
        }
    }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        rootView = inflater.inflate(R.layout.fragment_planet_detail, container, false)

        // Firebase 인증과 데이터베이스 참조 설정
        val userId = FirebaseAuth.getInstance().currentUser?.uid
        if (userId == null) {
            Toast.makeText(context, "사용자가 로그인되어 있지 않습니다.", Toast.LENGTH_SHORT).show()
            return rootView
        }
        val database: FirebaseDatabase = FirebaseDatabase.getInstance()
        val distortionRef: DatabaseReference = database.getReference("users/$userId/distortionInformation")

        val imageResourceId = arguments?.getInt(ARG_IMAGE_RESOURCE_ID)
        val planetImageView: ImageView = rootView.findViewById(R.id.planet_image)
        imageResourceId?.let {
            planetImageView.setImageResource(it)
        }

        formattedDate2 = arguments?.getString("formattedDate2") ?: "2024-09"

        // Toolbar 설정
        val toolbar: Toolbar = rootView.findViewById(R.id.toolbar)
        val activity = requireActivity() as AppCompatActivity
        activity.setSupportActionBar(toolbar)

        activity.supportActionBar?.setDisplayHomeAsUpEnabled(false)
        activity.supportActionBar?.setDisplayShowTitleEnabled(false)

        val toolbarTitle: TextView = rootView.findViewById(R.id.toolbar_base1_title)
        typeName = arguments?.getString(ARG_TYPE_NAME) ?: "Default Title"
        toolbarTitle.text = typeName

        val mainTitle: TextView = rootView.findViewById(R.id.planet_description)
        val titleName = "${typeName}이 나온 생각이에요"
        mainTitle.text = titleName

        // 버튼 클릭 리스너 설정
        val buttonWriteAction1: ImageButton = rootView.findViewById(R.id.button_base1_action1)
        buttonWriteAction1.setOnClickListener {
            val intent = Intent(requireContext(), AnalysisActivity::class.java)
            startActivity(intent)
            activity?.finish()
        }

        // planet_counter 텍스트 설정
        val planetCounter: TextView = rootView.findViewById(R.id.planet_counter)
        planetCounter.text = boxDataList.size.toString()  // boxDataList의 크기 설정

        // RecyclerView 설정
        val recyclerView = rootView.findViewById<RecyclerView>(R.id.boxTextrecyclerView)
        recyclerView.layoutManager = LinearLayoutManager(requireContext())
        recyclerView.adapter = PlanetDetailBoxAdapter(boxDataList)

        // fetchDistortionData 호출
        fetchDistortionData(distortionRef, typeName)

        // boxfiller 호출
        boxfiller(formattedDate2, typeName)

        return rootView
    }

    private fun fetchDistortionData(
        distortionRef: DatabaseReference,
        typeName: String
    ) {
        distortionRef.child(typeName).addListenerForSingleValueEvent(object : ValueEventListener {
            override fun onDataChange(snapshot: DataSnapshot) {
                if (snapshot.exists()) {
                    // Firebase 데이터 매핑
                    val subtitle = snapshot.child("subtitle").value as? String ?: "Subtitle 없음"
                    val description1 = snapshot.child("description1").value as? String ?: "Description1 없음"
                    val description2 = snapshot.child("description2").value as? String ?: "Description2 없음"

                    val semiTitle: TextView = rootView.findViewById(R.id.planet_additional_info)
                    val shortDescription: TextView = rootView.findViewById(R.id.planet_short_description)
                    semiTitle.text = subtitle
                    shortDescription.text = "$description1\n$description2"
                } else {
                    val semiTitle: TextView = rootView.findViewById(R.id.planet_additional_info)
                    val shortDescription: TextView = rootView.findViewById(R.id.planet_short_description)
                    semiTitle.text = "데이터 없음"
                    shortDescription.text = "데이터 없음"
                }
            }

            override fun onCancelled(error: DatabaseError) {
                val semiTitle: TextView = rootView.findViewById(R.id.planet_additional_info)
                val shortDescription: TextView = rootView.findViewById(R.id.planet_short_description)
                semiTitle.text = "오류 발생"
                shortDescription.text = "오류 발생: ${error.message}"
            }
        })
    }

    private fun boxfiller(formattedDate2: String, typeName: String) {
        val database = FirebaseDatabase.getInstance()
        val userId = FirebaseAuth.getInstance().currentUser?.uid
        if (userId == null) {
            Toast.makeText(context, "사용자가 로그인되어 있지 않습니다.", Toast.LENGTH_SHORT).show()
            return
        }

        val monthlyAnalysisRef = database.getReference("users/$userId/monthlyAnalysis/$formattedDate2")

        monthlyAnalysisRef.child(typeName).addListenerForSingleValueEvent(object : ValueEventListener {
            override fun onDataChange(snapshot: DataSnapshot) {
                if (snapshot.exists()) {
                    val count = snapshot.child("count").value as? Int ?: 0
                    Log.d("boxfiller", "$typeName 의 count: $count")

                    val entriesSnapshot = snapshot.child("entries")
                    boxDataList.clear() // 기존 데이터를 초기화

                    for (entrySnapshot in entriesSnapshot.children) {
                        val text = entrySnapshot.child("text").value as? String ?: ""
                        val date = entrySnapshot.child("Date").value as? String ?: ""

                        val sentences = text.split(".").map { it.trim() }.filter { it.isNotEmpty() }

                        for (sentence in sentences) {
                            boxDataList.add(BoxData(sentence, date))
                        }
                    }

                    // RecyclerView 갱신
                    val recyclerView = rootView.findViewById<RecyclerView>(R.id.boxTextrecyclerView)
                    recyclerView.adapter?.notifyDataSetChanged()

                    // planet_counter 텍스트 갱신
                    val planetCounter: TextView = rootView.findViewById(R.id.planet_counter)
                    planetCounter.text = boxDataList.size.toString()
                }
            }

            override fun onCancelled(error: DatabaseError) {
                Log.e("boxfiller", "데이터 불러오기 실패: ${error.message}")
            }
        })
    }
}

