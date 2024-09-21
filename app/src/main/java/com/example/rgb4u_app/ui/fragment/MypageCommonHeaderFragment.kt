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
        val title = arguments?.getString(ARG_TITLE)
        binding.tvTitle.text = title

        // 뒤로가기 버튼 클릭 리스너
        binding.btnBack.setOnClickListener {
            activity?.onBackPressed()
        }
    }

    fun setTitle(title: String) {
        binding.tvTitle.text = title // 타이틀 변경 메서드
    }

    fun setBackButtonListener(listener: View.OnClickListener) {
        binding.btnBack.setOnClickListener(listener) // 리스너 설정 메서드
    }

    // btn_back에 대한 공개 메서드
    fun getBackButton(): View {
        return binding.btnBack
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }
}
