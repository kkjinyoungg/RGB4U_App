package com.example.rgb4u_app.ui.fragment

import android.content.Intent
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageButton
import android.widget.TextView
import androidx.fragment.app.Fragment
import com.example.rgb4u_app.R
import com.example.rgb4u_app.ui.activity.summary.SummaryMainActivity

class SummaryFragment : Fragment() {

    private lateinit var titleTextView: TextView
    private lateinit var userContentLabelTextView: TextView
    private lateinit var userContentTextView: TextView
    private lateinit var summarizedTextView: TextView
    private lateinit var whySummaryTextView: TextView
    private lateinit var backButton: ImageButton

    // Variables to store content passed from the activity
    var userContent: String? = null
    var summarizedContent: String? = null
    var whySummaryReason: String? = null
    var titleText: String? = null
    var summaryLabelText: String? = null
    var userContentLabelText: String? = null // 추가된 변수

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        // Inflate the layout for this fragment
        return inflater.inflate(R.layout.fragment_summary, container, false)
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        // Initialize views
        titleTextView = view.findViewById(R.id.titleTextView)
        userContentLabelTextView = view.findViewById(R.id.userContentLableTextView) // 올바른 변수명 수정
        userContentTextView = view.findViewById(R.id.userContentTextView)
        summarizedTextView = view.findViewById(R.id.summarizedTextView)
        whySummaryTextView = view.findViewById(R.id.whySummaryTextView)
        backButton = view.findViewById(R.id.backButton)

        // UI 업데이트
        updateUI()

        // Set the text for the title (received from the activity)
        titleText?.let {
            titleTextView.text = it
        }

        // Set the text for user-written content and summarized content
        userContent?.let {
            userContentTextView.text = it
        }

        summarizedContent?.let {
            summarizedTextView.text = it
        }

        whySummaryReason?.let {
            whySummaryTextView.text = it
        }

        // Set the summary label text (received from the activity)
        summaryLabelText?.let {
            view.findViewById<TextView>(R.id.summaryLableTextView).text = it
        }

        // Set the user content label text
        userContentLabelText?.let {
            userContentLabelTextView.text = it
        }

        // Set up back button listener
        backButton.setOnClickListener {
            // Intent를 사용하여 SummaryMainActivity로 이동
            val intent = Intent(activity, SummaryMainActivity::class.java)
            startActivity(intent)

            // 현재 Activity를 종료하는 경우, 필요에 따라 추가
            activity?.finish()
        }
    }

    fun updateUI() {
        titleTextView.text = titleText ?: "기본 제목"
        userContentTextView.text = userContent ?: "기본 사용자 내용"
        summarizedTextView.text = summarizedContent ?: "기본 요약 내용"
        whySummaryTextView.text = whySummaryReason ?: "기본 이유 내용"

        // summaryLabelText 설정
        summaryLabelText?.let {
            view?.findViewById<TextView>(R.id.summaryLableTextView)?.text = it
        }

        // userContentLabelText 설정
        userContentLabelText?.let {
            userContentLabelTextView.text = it
        }
    }

    companion object {
        fun newInstance(): SummaryFragment {
            return SummaryFragment()
        }
    }
}
