package com.example.rgb4u_app.ui.fragment

import android.content.Context
import android.os.Build
import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import android.widget.TextView
import androidx.appcompat.app.AppCompatActivity
import androidx.appcompat.widget.Toolbar
import androidx.constraintlayout.widget.ConstraintLayout
import androidx.fragment.app.Fragment
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.airbnb.lottie.LottieAnimationView
import com.example.rgb4u_app.R
import com.example.rgb4u_app.ui.activity.home.AnalysisItem
import com.example.rgb4u_app.ui.activity.home.AnalysisItemAdapter
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.database.DataSnapshot
import com.google.firebase.database.DatabaseError
import com.google.firebase.database.DatabaseReference
import com.google.firebase.database.FirebaseDatabase
import com.google.firebase.database.ValueEventListener
import okhttp3.Call
import okhttp3.Callback
import okhttp3.MediaType.Companion.toMediaType
import okhttp3.OkHttpClient
import okhttp3.Request
import okhttp3.RequestBody.Companion.toRequestBody
import okhttp3.Response
import org.json.JSONObject
import java.io.IOException
import java.text.SimpleDateFormat
import java.util.Calendar
import java.util.Locale
import com.google.firebase.database.GenericTypeIndicator

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


    private var firebaseMessage: String? = null // Firebase에서 가져온 메시지를 저장할 변수
    private var isMessageSet: Boolean = false // 메시지가 설정되었는지 여부를 추적하는 변수

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

        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {
            requireActivity().window.apply {
                decorView.systemUiVisibility =
                    View.SYSTEM_UI_FLAG_LAYOUT_FULLSCREEN or View.SYSTEM_UI_FLAG_LAYOUT_STABLE
                statusBarColor = android.graphics.Color.TRANSPARENT
            }
        }

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
        observeFirebaseStates()
    }

    private fun observeFirebaseStates() {
        val userId = FirebaseAuth.getInstance().currentUser?.uid
        val databaseReference = FirebaseDatabase.getInstance().getReference("users/$userId/diaries")

        val currentDate = SimpleDateFormat("yyyy-MM-dd", Locale.getDefault()).format(Calendar.getInstance().time)
        Log.d("observeFirebaseStates", "Observing data for date: $currentDate")

        databaseReference.child(currentDate).addValueEventListener(object : ValueEventListener {
            override fun onDataChange(snapshot: DataSnapshot) {
                val savingStatus = snapshot.child("savingstatus").getValue(String::class.java)
                val readingStatus = snapshot.child("readingstatus").getValue(String::class.java)

                val userInput = snapshot.child("userInput")
                val emotionDegreeType = object : GenericTypeIndicator<Map<String, Any>>() {}
                val emotionDegree = userInput.child("emotionDegree").getValue(emotionDegreeType)
                val emotionDegreeInt = (emotionDegree?.get("int") as? Long)?.toInt() ?: 0

                val reMeasuredEmotionDegreeType = object : GenericTypeIndicator<Map<String, Any>>() {}
                val reMeasuredEmotionDegree = userInput.child("reMeasuredEmotionDegree").getValue(reMeasuredEmotionDegreeType)
                val reMeasuredEmotionDegreeInt = (reMeasuredEmotionDegree?.get("int") as? Long)?.toInt() ?: -1

                Log.d("onDataChange", "SavingStatus: $savingStatus, ReadingStatus: $readingStatus")
                Log.d("onDataChange", "emotionDegreeInt: $emotionDegreeInt, reMeasuredEmotionDegreeInt: $reMeasuredEmotionDegreeInt")

                when {
                    savingStatus == "save" && readingStatus == "read" -> {
                        updateBackground("after_analysis", emotionDegreeInt, reMeasuredEmotionDegreeInt)
                    }
                    savingStatus == "save" -> {
                        updateBackground("after_diary", emotionDegreeInt, reMeasuredEmotionDegreeInt)
                    }
                    else -> {
                        updateBackground("default", emotionDegreeInt, reMeasuredEmotionDegreeInt)
                    }
                }

                firebaseMessage = snapshot.child("message").getValue(String::class.java)

                if (firebaseMessage != null && !isMessageSet) {
                    isMessageSet = true
                    updateTextBox()
                }
            }

            override fun onCancelled(error: DatabaseError) {
                Log.e("FirebaseError", "Failed to read data: ${error.message}")
            }
        })
    }

    private fun updateTextBox() {
        // Firebase에서 가져온 메시지가 있으면 해당 메시지를 텍스트박스에 표시
        if (!firebaseMessage.isNullOrEmpty()) {
            textBox.text = firebaseMessage
        }
        // Firebase에서 메시지가 없으면 랜덤 메시지를 표시
        else {
            setInitialMessage()
        }
    }

    private fun updateBackground(state: String, emotionDegreeInt: Int, reMeasuredEmotionDegreeInt: Int) {
        Log.d("updateBackground", "Updating background to state: $state")

        // 랜덤 말풍선 텍스트 리스트
        val diaryMessages = listOf(
            "오늘도 기록을 잊지 않다니 대단해요!",
            "하루를 기록한 게 분명 도움이 될 거예요!",
            "꾸준히 기록하는 모습이 멋져요!",
            "오늘의 기록이 더 멋진 내일을 만들 거예요!",
            "매일의 기록이 큰 힘이 될 거예요!",
            "하루하루 정말 잘 해내고 있어요!",
            "하루를 기록하며 성장하는 모습이 멋져요!",
            "오늘도 한 걸음 나아갔네요! 잘하고 있어요!",
            "꾸준한 기록이 만들어낼 변화가 기대돼요!",
            "매일 하루를 돌아보는 모습이 멋져요!"
        )

        val lottieAnimationView = view?.findViewById<LottieAnimationView>(R.id.mainCharacterContainer)

        // 상태에 따라 배경과 랜덤 말풍선 텍스트 변경
        when (state) {
            "default" -> {
                mainConstraintLayout.setBackgroundResource(R.drawable.bg_home_defult)
                textBox.text = "기록을 시작해 보세요!"
                lottieAnimationView?.setAnimation("home_default_moa_02.json")
                lottieAnimationView?.playAnimation()
            }
            "after_diary" -> {
                mainConstraintLayout.setBackgroundResource(R.drawable.bg_home_after_diary)
                textBox.text = diaryMessages.random() // 랜덤 메시지 선택
                lottieAnimationView?.setAnimation("home_after_diary.json")
                lottieAnimationView?.playAnimation()
            }
            "after_analysis" -> {
                mainConstraintLayout.setBackgroundResource(R.drawable.bg_home_after_analysis)
                lottieAnimationView?.setAnimation("home_after_analysis.json")
                lottieAnimationView?.playAnimation()

                if (emotionDegreeInt == 0 || reMeasuredEmotionDegreeInt == -1) {
                    textBox.text = "내일도 저와 함께 하루를 기록해보세요&"
                } else {
                    // emotionDegreeInt와 reMeasuredEmotionDegreeInt 비교
                    when {
                        emotionDegreeInt > reMeasuredEmotionDegreeInt -> {
                            textBox.text = "감정이 나아졌어요! 내일도 저와 하루를 기록해요&"
                        }
                        emotionDegreeInt == reMeasuredEmotionDegreeInt -> {
                            textBox.text = "변화가 당장 느껴지지 않아도 도움이 되었을 거예요&"
                        }
                        emotionDegreeInt < reMeasuredEmotionDegreeInt -> {
                            textBox.text = "오늘은 천천히 하루를 돌아보면 어떨까요?&"
                        }
                    }
                }
            }
        }
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
        // getter 메서드를 통해 analysisList에 접근
        val notificationCount = adapter.getAnalysisList().count { it.hasAnalysisData }
        notificationCountText.text = "$notificationCount"
        // notificationCount가 0인 경우에도 보이게 수정
        notificationCountText.visibility = if (notificationCount > 0) View.VISIBLE else View.GONE
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
        // 결과가 나올 때까지 "· · ·"을 표시
        textBox.text = "· · ·"

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

    private fun setInitialMessage() {
        // 미리 정의된 텍스트 리스트
        val messageList = listOf(
            "오늘은 어떤 과자를 먹을까요?&",
            "오늘 날씨는 어땠나요?&",
            "오늘 하루도 힘차게 시작해요!",
            "좋은 일이 많이 생기길 바랄게요.&",
            "새로운 친구를 만났나요?&",
            "오늘은 어떤 이야기가 기다릴까요?",
            "하늘에 구름이 정말 예쁘네요.&",
            "새롭게 배우고 싶은 것이 있나요?",
            "오늘 하루 중 어떤 일이 가장 재밌었나요?&",
            "우주 속 행성은 어떤 모습일까요?",
            "오늘은 스스로를 응원해 볼까요?&",
            "오늘은 기분이 어떤가요?",
            "함께 놀 때가 가장 행복해요!",
            "내일은 또 어떤 일이 기다릴까요?&",
            "모아랑 먹고 싶은 메뉴가 있나요?",
            "하늘을 보면 기분이 좋아져요.&",
            "일찍 일어나면 기분이 상쾌해요!&",
            "떡볶이가 먹고 싶은 날이에요!",
            "함께할 수 있어서 기뻐요&",
            "슬픈 일이 있다면 모아와 함께 나눠요.&",
            "모아와 함께 오늘 하루를 돌아볼까요?",
            "괜찮아요, 오늘도 잘했어요.&",
            "힘든 일은 모아에게 언제든 이야기해요!&",
            "모든 일이 조금씩 나아질 거예요.",
            "힘든 순간에도 혼자가 아니에요.&",
            "너무 걱정하지 마세요. 잘 될 거예요.",
            "항상 최선을 다하고 있어요. 괜찮아요.&",
            "별들이 반짝이는 밤이 좋아요.&",
            "어떤 말을 들으면 기분이 좋을까요?&",
            "하루를 마무리하며 고마운 사람을 떠올려요.",
            "하늘에 떠 있는 별을 보면 꿈꾸고 싶어져요.&",
            "재미있는 일이 있었나요?&",
            "모아는 우주에 있는 별을 보고 싶어요.",
            "기분이 좋은 날은 언제인가요?&",
            "내일은 더 좋은 일이 있을 거예요!",
            "어두운 밤 뒤엔 항상 밝은 아침이 와요.&",
            "조금씩, 천천히 나아가면 돼요.",
            "언젠가 꿈이 이루어질 거예요.&",
            "모아가 항상 곁에 있어요.&",
            "슬플 땐 잠시 쉬어도 돼요. 괜찮아요.",
            "너무 걱정하지 말고, 천천히 해요.&",
            "힘든 순간이 지나면 더 단단해질 거예요.&",
            "오늘도 충분히 잘하고 있어요!",
            "너무 바쁘면 잠깐 쉬어도 괜찮아요.",
            "좋은 일은 예상치 못한 순간에 찾아와요.",
            "오늘 하루는 행복했나요?",
            "모아와 함께 소중한 순간을 간직해요",
            "기분이 좋아질 때까지 조금 더 기다려요.",
            "하루 중 고마운 일이 있었나요?&",
            "항상 미소를 잃지 않기를 바랄게요.",
            "가끔은 잠시 멈춰서 숨을 고르는 게 중요해요.",
            "오늘도 모아가 응원해요!",
            "어떤 일이든 즐겁게 할 수 있어요.",
            "매일 조금씩 성장하는 모습이 멋져요.",
            "힘든 날도 끝이 있대요&",
            "모아와 특별한 하루를 보내요!",
            "하루를 마무리하며 작은 행복을 찾아보세요."
        )

        // 리스트에서 랜덤으로 하나를 선택
        val randomMessage = messageList.random()

        // 텍스트박스에 설정 (UI 업데이트는 반드시 메인 스레드에서)
        requireActivity().runOnUiThread {
            textBox.text = randomMessage
        }
    }

    private fun callChatGPT(callback: (String) -> Unit) {

        val messagesArray = org.json.JSONArray().apply {
            put(JSONObject().apply {
                put("role", "user")
                put("content", "다양한 주제의 따뜻한 말을 짧은 한 문장으로 해주세요. 문장 끝에 종종 &를 쓰세요. ~해요 체를 쓰세요. 공백 포함 50byte를 넘지 마세요. (예: 오늘은 하늘이 예뻐요!&) 이 예시처럼 50byte를 넘지 마세요.")
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

                    // 바이트 수 확인
                    val messageByteSize = message.toByteArray(Charsets.UTF_8).size
                    Log.d("Message Size", "Message Byte Size: $messageByteSize") // 디버그 출력

                    // 바이트 수가 52바이트 초과 시, 미리 정의된 리스트에서 랜덤 메시지 선택
                    if (messageByteSize > 52) {
                        setInitialMessage()
                    } else {
                        // 바이트 수가 맞는 경우
                        requireActivity().runOnUiThread {
                            callback(message)
                        }
                    }
                }
            }

        })
    }
}
