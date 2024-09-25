package com.example.rgb4u_app.ui.fragment

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
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
        binding.btnBack.setOnClickListener(backButtonListener ?: View.OnClickListener {
            activity?.onBackPressed() // 기본 동작
        })
    }

    fun setTitle(title: String) {
        binding.tvTitle.text = title // 타이틀 변경 메서드
    }

    fun setBackButtonListener(listener: View.OnClickListener) {
        backButtonListener = listener
        binding.btnBack.setOnClickListener(listener) // 리스너 설정 메서드
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }
}
