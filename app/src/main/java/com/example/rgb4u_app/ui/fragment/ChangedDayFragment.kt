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

class ChangedDayFragment : Fragment() {
    private lateinit var binding: FragmentChangedDayBinding
    private var isSituationExpanded = false

    // 데이터 예시 (실제 데이터는 서버나 DB에서 받아올 수 있음)
    private var situationTitle: String = "이런 상황이 있었어요"
    private var situationDetailText: String = "너무 한 가지 부정적인 상황으로 전반적인 판단을 내리고 있어요..."
    private var exampleTexts: List<String> = listOf(
        "엄마는 왜 매번 나를 괴롭히고 내가 하는 걸 싫어하고...",
        "친구가 나를 좋아하지 않나? 다들 나를 피하는 것 같아.",
        "나는 다른 사람들에게 항상 부족해 보여. 아무도 날 인정해주지 않아."
    )
    private var emotionSteps: List<String> = listOf("4단계", "4단계")
    private var emotionTexts: List<String> = listOf("아주 싫었어", "아주 좋았어")

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        binding = FragmentChangedDayBinding.inflate(inflater, container, false)
        // 전달받은 데이터 처리
        val date = arguments?.getString("Date")
        val reMeasuredEmotionDegreeInt = arguments?.getInt("reMeasuredEmotionDegreeInt")
        val reMeasuredEmotionDegreeString = arguments?.getString("reMeasuredEmotionDegreeString")
        val reMeasuredEmotionDegreeImage = arguments?.getString("reMeasuredEmotionDegreeImage")

        // "이런 상황이 있었어요" 토글 버튼 기능
        binding.toggleSituation.setOnClickListener {
            isSituationExpanded = !isSituationExpanded
            binding.situationDetail.visibility = if (isSituationExpanded) View.VISIBLE else View.GONE
            binding.toggleSituation.setImageResource(
                if (isSituationExpanded) R.drawable.ic_arrow_up else R.drawable.ic_arrow_down_2
            )
        }

        // 데이터 확인 (로그 출력)
        println("Date: $date")
        println("Emotion Degree Int: $reMeasuredEmotionDegreeInt")
        println("Emotion Degree String: $reMeasuredEmotionDegreeString")
        println("Emotion Degree Image: $reMeasuredEmotionDegreeImage")

        // UI 설정
        binding.tvDayChange.text = "달라진 하루를 느껴보세요"
        binding.situationTitle.text = situationTitle
        binding.situationDetail.text = situationDetailText
        binding.tvThinkChange.text = "생각을 바꿔봤어요"
        binding.tvThinkSubtitle.text = "생각 유형별로 한 가지씩 나온거다~"
        binding.tvEmotionChange.text = "감정 정도가 이렇게 변했어요"

        // 예시 텍스트 및 감정 데이터 설정
        setupExampleTexts()
        setupEmotionData()

        // 확인 버튼 클릭 리스너
        binding.confirmButton.setOnClickListener {
            // MainActivity로 이동
            val intent = Intent(requireActivity(), MainActivity::class.java)
            startActivity(intent)
        }

        return binding.root
    }

    private fun setupExampleTexts() {
        // 예시 텍스트 설정
        binding.exampleText1.text = exampleTexts[0]
        binding.exampleText2.text = exampleTexts[1]
        binding.exampleText3.text = exampleTexts[2]
    }

    private fun setupEmotionData() {
        // 감정 데이터 설정
        binding.emotionStep1.text = emotionSteps[0]
        binding.emotionText1.text = emotionTexts[0]
        binding.emotionStep2.text = emotionSteps[1]
        binding.emotionText2.text = emotionTexts[1]
    }

    companion object {
        fun newInstance(date: String) = ChangedDayFragment().apply {
            arguments = Bundle().apply {
                putString("selectedDate", date)
            }
        }
    }
}
