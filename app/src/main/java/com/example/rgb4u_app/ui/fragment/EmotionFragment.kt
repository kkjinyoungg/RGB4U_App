package com.example.rgb4u.ver1_app.ui.fragment

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Button
import androidx.fragment.app.Fragment
import com.example.rgb4u.ver1_app.R

class EmotionFragment : Fragment() {

    private var listener: OnEmotionSelectedListener? = null

    interface OnEmotionSelectedListener {
        fun onEmotionSelected(emotion: String)
    }

    companion object {
        private const val ARG_EMOTION = "emotion"

        fun newInstance(emotion: String): EmotionFragment {
            val fragment = EmotionFragment()
            val args = Bundle()
            args.putString(ARG_EMOTION, emotion)
            fragment.arguments = args
            return fragment
        }
    }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        val rootView = inflater.inflate(R.layout.fragment_emotion, container, false)

        val emotion = arguments?.getString(ARG_EMOTION)

        // emotion 값에 따라 버튼의 텍스트나 동작을 설정합니다.
        val button1: Button = rootView.findViewById(R.id.button1)
        button1.setOnClickListener {
            listener?.onEmotionSelected(button1.text.toString())
        }
        val button2: Button = rootView.findViewById(R.id.button2)
        button2.setOnClickListener {
            listener?.onEmotionSelected(button2.text.toString())
        }
        val button3: Button = rootView.findViewById(R.id.button3)
        button3.setOnClickListener {
            listener?.onEmotionSelected(button3.text.toString())
        }
        val button4: Button = rootView.findViewById(R.id.button4)
        button4.setOnClickListener {
            listener?.onEmotionSelected(button4.text.toString())
        }
        val button5: Button = rootView.findViewById(R.id.button5)
        button5.setOnClickListener {
            listener?.onEmotionSelected(button5.text.toString())
        }
        val button6: Button = rootView.findViewById(R.id.button6)
        button6.setOnClickListener {
            listener?.onEmotionSelected(button6.text.toString())
        }



        when (emotion) {
            "놀람" -> {
                button1.text = "아찔한"
                button2.text = "황당한"
                button3.text = "깜짝놀란"
                button4.text = "충격적인"
                button5.text = "충격적인"
                button6.text = "어안이 벙벙한"
            }
            "두려움" -> {
                button1.text = "암담한"
                button2.text = "겁나는"
                button3.text = "무서운"
                button4.text = "불안한"
                button5.text = "긴장된"
                button6.text = "걱정스러운"
            }
            "슬픔" -> {
                button1.text = "슬픈"
                button2.text = "우울한"
                button3.text = "비참한"
                button4.text = "서운한"
                button5.text = "기운없는"
                button6.text = "눈물이 나는"
            }
            "분노" -> {
                button1.text = "화난"
                button2.text = "분한"
                button3.text = "약 오른"
                button4.text = "억울한"
                button5.text = "짜증나는"
                button6.text = "끌어오르는"
            }
            "혐오" -> {
                button1.text = "미운"
                button2.text = "싫은"
                button3.text = "불쾌한"
                button4.text = "모욕적인"
                button5.text = "못마땅한"
                button6.text = "정 떨어지는"
            }
        }

        return rootView
    }

    override fun onDetach() {
        super.onDetach()
        listener = null
    }
}
