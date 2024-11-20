package com.example.rgb4u_app.ui.fragment

import android.content.Intent
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import com.example.rgb4u_app.R
import com.example.rgb4u_app.databinding.FragmentChangedDayBinding
import com.example.rgb4u_app.ui.activity.MainActivity
import com.google.firebase.database.DatabaseReference
import com.google.firebase.database.FirebaseDatabase
import android.util.Log
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.database.DataSnapshot
import com.google.firebase.database.DatabaseError
import com.google.firebase.database.ValueEventListener
import com.google.firebase.database.GenericTypeIndicator

class ChangedDayFragment : Fragment() {
    private lateinit var binding: FragmentChangedDayBinding
    private var isSituationExpanded = false
    private lateinit var userId: String
    private lateinit var database: DatabaseReference

    private lateinit var toolbar: String
    private lateinit var date: String

    private var situationTitle: String = "이런 상황이 있었어요"
    private var situationDetailText: String = ""
    private var exampleTexts: List<String> = listOf()
    private var emotionSteps: List<String> = listOf("단계1", "단계2")
    private var emotionTexts: List<String> = listOf("", "")

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        // arguments에서 데이터 받기
        arguments?.let {
            toolbar = it.getString("Toolbar", "")
            date = it.getString("Date", "")
        }
        binding = FragmentChangedDayBinding.inflate(inflater, container, false)

        // Firebase에서 UserId 가져오기
        userId = FirebaseAuth.getInstance().currentUser?.uid ?: ""

        // 데이터 확인 (로그 출력)
        println("Date: $date")
        println("Toolbar: $toolbar")

        // "이런 상황이 있었어요" 토글 버튼 기능
        binding.toggleSituation.setOnClickListener {
            isSituationExpanded = !isSituationExpanded
            binding.situationDetail.visibility = if (isSituationExpanded) View.VISIBLE else View.GONE
            binding.toggleSituation.setImageResource(
                if (isSituationExpanded) R.drawable.ic_arrow_up else R.drawable.ic_arrow_down_2
            )
        }

        // UI 설정
        binding.tvDayChange.text = "달라진 하루를 느껴보세요"
        binding.situationTitle.text = situationTitle

        // Firebase에서 데이터 가져오기
        loadDataFromFirebase()

        // 확인 버튼 클릭 리스너
        binding.confirmButton.setOnClickListener {
            // MainActivity로 이동
            val intent = Intent(requireActivity(), MainActivity::class.java)
            startActivity(intent)
        }

        return binding.root
    }

    private fun loadDataFromFirebase() {
        // Firebase database 참조
        database = FirebaseDatabase.getInstance().reference
        val diaryRef = database.child("users").child(userId).child("diaries").child(date)

        // 데이터 리스너 추가
        diaryRef.addListenerForSingleValueEvent(object : ValueEventListener {
            override fun onDataChange(snapshot: DataSnapshot) {
                if (snapshot.exists()) {
                    // GenericTypeIndicator 사용
                    val userInput = snapshot.child("userInput").getValue(object : GenericTypeIndicator<Map<String, Any>>() {}) ?: emptyMap()
                    val aiAnalysis = snapshot.child("aiAnalysis").getValue(object : GenericTypeIndicator<Map<String, Any>>() {}) ?: emptyMap()
                    val secondAnalysis = aiAnalysis["secondAnalysis"] as? Map<String, Any>
                    val thoughtSets = secondAnalysis?.get("thoughtSets") as? Map<String, Any>

                    // (1) situation 데이터 가져오기
                    situationDetailText = aiAnalysis["situation"] as? String ?: ""
                    binding.situationDetail.text = situationDetailText

                    // (2) thoughtSets의 alternativeThoughts 데이터 가져오기
                    val thoughtList = mutableListOf<String>()
                    thoughtSets?.forEach { (key, value) ->
                        val thoughts = value as? List<Map<String, Any>> ?: return@forEach
                        thoughts.forEach { thought ->
                            val alternativeThoughts = thought["alternativeThoughts"] as? String
                            alternativeThoughts?.let { thoughtList.add(it) }
                        }
                    }
                    exampleTexts = thoughtList
                    setupExampleTexts()

                    // (3) emotionDegree 가져오기
                    val emotionDegree = userInput["emotionDegree"] as? Map<String, Any>
                    val emotionDegreeInt = (emotionDegree?.get("int") as? Long)?.toInt()?.takeIf { it in Int.MIN_VALUE..Int.MAX_VALUE } ?: 0
                    val emotionDegreeString = emotionDegree?.get("string") as? String ?: ""
                    val emotionDegreeImage = emotionDegree?.get("emotionimg") as? String ?: ""
                    setupEmotionData(emotionDegreeInt, emotionDegreeString, emotionDegreeImage, 1)

                    // (4) reMeasuredEmotionDegree 가져오기
                    val reMeasuredEmotionDegree = userInput["reMeasuredEmotionDegree"] as? Map<String, Any>
                    val reMeasuredEmotionDegreeInt = (reMeasuredEmotionDegree?.get("int") as? Long)?.toInt()?.takeIf { it in Int.MIN_VALUE..Int.MAX_VALUE } ?: 0
                    val reMeasuredEmotionDegreeString = reMeasuredEmotionDegree?.get("string") as? String ?: ""
                    val reMeasuredEmotionDegreeImage = reMeasuredEmotionDegree?.get("emotionimg") as? String ?: ""
                    setupEmotionData(reMeasuredEmotionDegreeInt, reMeasuredEmotionDegreeString, reMeasuredEmotionDegreeImage, 2)
                }
            }

            override fun onCancelled(error: DatabaseError) {
                // 오류 처리 (필요시)
                Log.e("Firebase", "Failed to read value.", error.toException())
            }
        })
    }

    private fun setupExampleTexts() {
        // 예시 텍스트를 표시할 TextView 목록
        val exampleTextViews = listOf(binding.exampleText1, binding.exampleText2, binding.exampleText3)
        // 예시 블루박스 뷰 목록
        val exampleBlueBoxViews = listOf(binding.examplebluebox1, binding.examplebluebox2, binding.examplebluebox3)
        // 예시 이미지 뷰 목록
        val exampleImageViews = listOf(binding.exampleImage1, binding.exampleImage2, binding.exampleImage3)

        // exampleTexts 리스트의 각 항목을 TextView에 설정
        exampleTexts.forEachIndexed { index, text ->
            if (index < exampleTextViews.size) {
                exampleTextViews[index].text = text
                exampleTextViews[index].visibility = View.VISIBLE // 텍스트가 있으면 보이도록 설정
                exampleBlueBoxViews[index].visibility = View.VISIBLE // 블루박스도 보이도록 설정
                exampleImageViews[index].visibility = View.VISIBLE // 이미지도 보이도록 설정
            }
        }

        // exampleTexts 리스트보다 TextView, BlueBox, Image가 많으면 나머지는 숨김 처리
        for (i in exampleTexts.size until exampleTextViews.size) {
            exampleTextViews[i].visibility = View.GONE // 나머지 TextView는 화면에서 제거
            exampleBlueBoxViews[i].visibility = View.GONE // 나머지 BlueBox는 화면에서 제거
            exampleImageViews[i].visibility = View.GONE // 나머지 ImageView는 화면에서 제거
        }
    }


    private fun setupEmotionData(emotionDegreeInt: Int, emotionDegreeString: String, emotionDegreeImage: String, step: Int) {
        if (step == 1) {
            binding.emotionStep1.text = "${emotionDegreeInt + 1}단계"
            binding.emotionText1.text = emotionDegreeString
            binding.emotionIcon1.setImageResource(resources.getIdentifier(emotionDegreeImage, "drawable", context?.packageName))
        } else {
            binding.emotionStep2.text = "${emotionDegreeInt + 1}단계"
            binding.emotionText2.text = emotionDegreeString
            binding.emotionIcon2.setImageResource(resources.getIdentifier(emotionDegreeImage, "drawable", context?.packageName))
        }
    }

    companion object {
        fun newInstance(toolbar: String, date: String): ChangedDayFragment {
            val fragment = ChangedDayFragment()
            val bundle = Bundle()
            bundle.putString("Toolbar", toolbar)
            bundle.putString("Date", date)  // date 전달
            fragment.arguments = bundle
            return fragment
        }
    }
}
