package com.example.rgb4u_app.ui.fragment

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.activity.OnBackPressedCallback
import androidx.fragment.app.Fragment
import com.example.rgb4u_app.databinding.FragmentMypageCommonHeaderBinding

class MypageCommonHeaderFragment : Fragment() {

    private var _binding: FragmentMypageCommonHeaderBinding? = null
    private val binding get() = _binding!!

    private var backButtonListener: View.OnClickListener? = null

    companion object {
        private const val ARG_TITLE = "arg_title"

        fun newInstance(title: String): MypageCommonHeaderFragment {
            val fragment = MypageCommonHeaderFragment()
            val args = Bundle()
            args.putString(ARG_TITLE, title)
            fragment.arguments = args
            return fragment
        }
    }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        _binding = FragmentMypageCommonHeaderBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        // 기본 타이틀 설정
        val title = arguments?.getString(ARG_TITLE) ?: "기본 타이틀"
        binding.tvTitle.text = title

        // 뒤로가기 버튼 리스너 설정
        binding.btnBack.setOnClickListener {
            backButtonListener?.onClick(it) ?: requireActivity().onBackPressedDispatcher.onBackPressed()
        }

        // onBackPressedCallback 설정 (선택 사항)
        requireActivity().onBackPressedDispatcher.addCallback(viewLifecycleOwner, object : OnBackPressedCallback(true) {
            override fun handleOnBackPressed() {
                backButtonListener?.onClick(binding.btnBack) ?: requireActivity().onBackPressed()
            }
        })
    }

    fun setTitle(title: String) {
        binding.tvTitle.text = title // 타이틀 변경 메서드
    }

    fun setBackButtonListener(listener: View.OnClickListener) {
        backButtonListener = listener
        // 기존 리스너를 다시 설정하지 않음
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }
}
