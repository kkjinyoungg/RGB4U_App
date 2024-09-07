package com.example.rgb4u_app

import android.content.Context
import android.content.Intent
import android.graphics.Color
import android.os.Bundle
import android.text.Editable
import android.text.TextWatcher
import android.util.Log
import android.view.View
import android.view.inputmethod.InputMethodManager
import androidx.fragment.app.Fragment
import androidx.lifecycle.lifecycleScope
import com.example.rgb4u_app.databinding.FragmentMyRecordBinding
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext
import java.text.SimpleDateFormat
import java.util.Calendar
import java.util.Locale

class MyRecordFragment : Fragment() {

    private var _binding: FragmentMyRecordBinding? = null
    private val binding get() = _binding!!

    private var nextActivityClass: Class<*>? = null

    fun setNextActivity(activityClass: Class<*>) {
        nextActivityClass = activityClass
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        // 비동기로 현재 날짜 가져오기
        viewLifecycleOwner.lifecycleScope.launch {
            val currentDate = getCurrentDate()
            binding.dateTextView.text = currentDate
        }

        // TextWatcher를 사용하여 글자 수 업데이트
        setupTextWatcher()

        // Arguments에서 questionText 가져오기
        val questionText = arguments?.getString("questionText")
        binding.questionment.text = questionText

        // 현재 단계에 따라 progressbar 업데이트
        val currentStep = arguments?.getInt("currentStep", 1) ?: 1
        updateProgressBar(currentStep)

        // 버튼 클릭 리스너 설정
        binding.buttonNext.setOnClickListener {
            Log.d("MyRecordFragment", "Next button clicked")
            nextActivityClass?.let { activityClass ->
                startNextActivity(activityClass)
            } ?: run {
                Log.e("MyRecordFragment", "nextActivityClass is null")
            }
        }

        // 키보드 '확인' 버튼 리스너 설정
        binding.inputField.setOnEditorActionListener { _, actionId, _ ->
            if (actionId == android.view.inputmethod.EditorInfo.IME_ACTION_DONE) {
                // 키보드 숨기기
                val imm =
                    requireActivity().getSystemService(Context.INPUT_METHOD_SERVICE) as InputMethodManager
                imm.hideSoftInputFromWindow(binding.inputField.windowToken, 0)
                true
            } else {
                false
            }
        }
    }

    private suspend fun getCurrentDate(): String {
        return withContext(Dispatchers.IO) {
            val calendar = Calendar.getInstance()
            val sdf = SimpleDateFormat("MM월 dd일 EEEE", Locale.getDefault())
            sdf.format(calendar.time)
        }
    }

    private fun startNextActivity(activityClass: Class<*>) {
        val intent = Intent(requireActivity(), activityClass)
        startActivity(intent)
    }

    private fun setupTextWatcher() {
        binding.inputField.addTextChangedListener(object : TextWatcher {
            override fun beforeTextChanged(s: CharSequence?, start: Int, count: Int, after: Int) {}

            override fun onTextChanged(s: CharSequence?, start: Int, before: Int, count: Int) {
                val textLength = s?.length ?: 0
                binding.charCountTextView.text = "$textLength/150"
            }

            override fun afterTextChanged(s: Editable?) {}
        })
    }

    private fun updateProgressBar(step: Int) {
        for (i in 0 until binding.progressbar.childCount) {
            val child = binding.progressbar.getChildAt(i)
            child.setBackgroundColor(if (i < step) Color.DKGRAY else Color.LTGRAY)
        }
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null // 메모리 누수 방지를 위해 바인딩 객체 해제
    }
}
