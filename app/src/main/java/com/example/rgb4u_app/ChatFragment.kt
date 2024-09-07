package com.example.rgb4u_app

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Button
import android.widget.ImageButton
import androidx.activity.OnBackPressedCallback
import androidx.fragment.app.Fragment
import androidx.lifecycle.Observer
import androidx.lifecycle.ViewModelProvider
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView

class ChatFragment : Fragment() {
    private lateinit var viewModel: ChatViewModel
    private lateinit var recyclerView: RecyclerView
    private lateinit var adapter: ChatAdapter

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        return inflater.inflate(R.layout.fragment_chat, container, false)
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        viewModel = ViewModelProvider(this).get(ChatViewModel::class.java)

        recyclerView = view.findViewById(R.id.recyclerView)
        adapter = ChatAdapter(mutableListOf(), requireContext()) // 빈 MutableList로 초기화
        recyclerView.adapter = adapter
        recyclerView.layoutManager = LinearLayoutManager(context)

        // LiveData를 관찰하여 데이터 변경 시 어댑터에 업데이트
        viewModel.messages.observe(viewLifecycleOwner, Observer { messages ->
            adapter.chatList.clear() // 현재 리스트 초기화
            adapter.chatList.addAll(messages) // 새 메시지 추가
            adapter.notifyDataSetChanged() // 어댑터 갱신
        })

        view.findViewById<Button>(R.id.showExamplesButton).setOnClickListener {
            val exampleMessage = "이것은 예시 메시지입니다."
            viewModel.addMessage(exampleMessage) // ViewModel의 addMessage 호출
        }

        // OnBackPressedCallback을 사용하여 뒤로가기 버튼 처리
        val callback = object : OnBackPressedCallback(true) {
            override fun handleOnBackPressed() {
                // 이전 프래그먼트로 돌아가기
                requireActivity().supportFragmentManager.popBackStack()
            }
        }
        requireActivity().onBackPressedDispatcher.addCallback(viewLifecycleOwner, callback)

        view.findViewById<Button>(R.id.exitButton).setOnClickListener {
            requireActivity().onBackPressedDispatcher.onBackPressed() // 뒤로가기 처리
        }

        view.findViewById<ImageButton>(R.id.backButton).setOnClickListener {
            requireActivity().onBackPressedDispatcher.onBackPressed() // 뒤로가기 처리
        }
    }
}