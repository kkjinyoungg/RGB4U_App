package com.example.rgb4u_app.ui.fragment

import android.content.Context
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageButton
import android.widget.ImageView
import android.widget.TextView
import androidx.appcompat.app.AppCompatActivity
import androidx.appcompat.widget.Toolbar
import androidx.constraintlayout.widget.ConstraintLayout
import androidx.fragment.app.Fragment
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.example.rgb4u_app.R
import com.example.rgb4u_app.ui.activity.home.AnalysisItem
import com.example.rgb4u_app.ui.activity.home.AnalysisItemAdapter
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.database.*
import okhttp3.*
import org.json.JSONObject
import java.io.IOException
import java.text.SimpleDateFormat
import java.util.Calendar
import java.util.Locale
import okhttp3.MediaType.Companion.toMediaType
import okhttp3.RequestBody.Companion.toRequestBody


class HomeFragment : Fragment() {

    private lateinit var textBox: TextView
    private lateinit var dateTextView: TextView
    private lateinit var dDayTextView: TextView
    private lateinit var mainConstraintLayout: ConstraintLayout
    private lateinit var mainCharacterContainer: ImageView
    private var notificationCount = 0
    private lateinit var notificationCountText: TextView

    private lateinit var database: DatabaseReference
    private lateinit var analysisList: MutableList<AnalysisItem>
    private lateinit var adapter: AnalysisItemAdapter
    private var messages: String = "" // 메시지 변수 추가
    private val client = OkHttpClient() // OkHttpClient 초기화

    //apiKey = ""의 ""안에 키 넣기!
    private val apiKey = ""

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        return inflater.inflate(R.layout.fragment_home, container, false)
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        val userId = FirebaseAuth.getInstance().currentUser?.uid

        val toolbar: Toolbar = view.findViewById(R.id.toolbar_home)
        val activity = requireActivity() as AppCompatActivity
        activity.setSupportActionBar(toolbar)
        activity.supportActionBar?.setDisplayHomeAsUpEnabled(false)
        activity.supportActionBar?.setDisplayShowTitleEnabled(false)

        textBox = view.findViewById(R.id.textBox)
        dateTextView = view.findViewById(R.id.dateTextView)
        dDayTextView = view.findViewById(R.id.dDayTextView)
        notificationCountText = view.findViewById(R.id.notificationCountText)

        // chat_refresh 버튼 설정
        val chatRefreshButton: ImageView = view.findViewById(R.id.refreshIcon)
        chatRefreshButton.setOnClickListener {
            changeMessage() // 버튼 클릭 시 메시지 변경
        }

        updateDateAndDay()
        calculateDDay()

        mainConstraintLayout = view.findViewById(R.id.mainConstraintLayout)
        mainCharacterContainer = view.findViewById(R.id.mainCharacterContainer)

        analysisList = mutableListOf()
        adapter = AnalysisItemAdapter(analysisList)
        val recyclerView: RecyclerView = view.findViewById(R.id.analysisRecyclerView)
        recyclerView.layoutManager = LinearLayoutManager(requireContext(), LinearLayoutManager.HORIZONTAL, false)
        recyclerView.adapter = adapter

        // Firebase 데이터베이스 참조 초기화
        database = FirebaseDatabase.getInstance().getReference("users/$userId/diaries")

        // Firebase 데이터 감시
        observeDiaries()
    }

    private fun observeDiaries() {
        database.addValueEventListener(object : ValueEventListener {
            override fun onDataChange(snapshot: DataSnapshot) {
                analysisList.clear()
                var unreadCount = 0
                var loadCount = 0

                for (dateSnapshot in snapshot.children) {
                    val readingStatus = dateSnapshot.child("readingstatus").getValue(String::class.java)
                    val toolbardate = dateSnapshot.child("toolbardate").getValue(String::class.java)
                    val dateKey = dateSnapshot.key ?: ""

                    if (readingStatus != null) {
                        when (readingStatus) {
                            "unread" -> {
                                unreadCount++
                                analysisList.add(AnalysisItem(true, false, toolbardate ?: "", dateKey))
                            }
                            "load" -> {
                                loadCount++
                                analysisList.add(AnalysisItem(true, true, toolbardate ?: "", dateKey))
                            }
                        }
                    }
                }

                if (unreadCount == 0 && loadCount == 0) {
                    analysisList.add(AnalysisItem(false, false))
                }

                adapter.notifyDataSetChanged()
                updateNotificationCount(adapter)
            }

            override fun onCancelled(error: DatabaseError) {
                // 오류 처리
            }
        })
    }

    private fun updateNotificationCount(adapter: AnalysisItemAdapter) {
        notificationCount = adapter.itemCount
        notificationCountText.text = "$notificationCount"
        notificationCountText.visibility = View.VISIBLE
    }

    private fun updateDateAndDay() {
        val currentDate = Calendar.getInstance()
        val dateFormat = SimpleDateFormat("M월 d일 EEEE", Locale.KOREA)
        dateTextView.text = dateFormat.format(currentDate.time)
    }

    private fun calculateDDay() {
        val sharedPreferences = requireActivity().getSharedPreferences("app_prefs", Context.MODE_PRIVATE)
        val installDateMillis = sharedPreferences.getLong("install_date", -1)

        if (installDateMillis == -1L) {
            val currentDateMillis = System.currentTimeMillis()
            sharedPreferences.edit().putLong("install_date", currentDateMillis).apply()
            dDayTextView.text = "D+1"
        } else {
            val currentDateMillis = System.currentTimeMillis()
            val dDay = ((currentDateMillis - installDateMillis) / (1000 * 60 * 60 * 24) + 1).toInt()
            dDayTextView.text = "D+$dDay"
        }
    }

    private fun changeMessage() {
        // 결과가 나올 때까지 ". . ."을 표시
        textBox.text = ". . ."

        callChatGPT { result ->
            // 메인 스레드에서 UI 업데이트
            requireActivity().runOnUiThread {
                // API 호출 결과로 messages 업데이트
                messages = result
                // 결과를 textBox에 표시
                textBox.text = messages
            }
        }
    }

    private fun callChatGPT(callback: (String) -> Unit) {
        val messagesArray = org.json.JSONArray().apply {
            put(JSONObject().apply {
                put("role", "user")
                put("content", "다양한 주제로 다정한 말을 해주세요. 가끔 &울 사용하세요. 공백 포함 46바이트를 넘지 않도록 해주세요. 예: '오늘은 어떤 과자를 먹어볼까요?&' (이 예시처럼 46바이트를 넘지 않게 해주세요.)")
            })
        }

        val requestBody = JSONObject().apply {
            put("model", "gpt-3.5-turbo")
            put("messages", messagesArray)
        }.toString()


        val request = Request.Builder()
            .url("https://api.openai.com/v1/chat/completions")
            .post(requestBody.toRequestBody("application/json".toMediaType()))
            .addHeader("Authorization", "Bearer $apiKey")
            .build()

        client.newCall(request).enqueue(object : Callback {
            override fun onFailure(call: Call, e: IOException) {
                e.printStackTrace()
                // 메인 스레드에서 오류 메시지 콜백
                requireActivity().runOnUiThread {
                    callback("오류가 발생했습니다.")
                }
            }

            override fun onResponse(call: Call, response: Response) {
                response.use {
                    if (!it.isSuccessful) {
                        // 메인 스레드에서 오류 메시지 콜백
                        requireActivity().runOnUiThread {
                            callback("응답 오류: ${it.code}")
                        }
                        throw IOException("Unexpected code $it")
                    }

                    val responseBody = it.body?.string()
                    val jsonResponse = JSONObject(responseBody)
                    val message = jsonResponse.getJSONArray("choices")
                        .getJSONObject(0)
                        .getJSONObject("message")
                        .getString("content")

                    // 메인 스레드에서 UI 업데이트
                    requireActivity().runOnUiThread {
                        callback(message)
                    }
                }
            }
        })
    }



}
