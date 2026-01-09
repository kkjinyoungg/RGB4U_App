package com.example.rgb4u.ver1_app.ui.activity.onboarding

import android.content.Intent
import android.os.Bundle
import android.util.Log
import android.view.View
import android.widget.Button
import android.widget.ImageButton
import android.widget.TextView
import androidx.appcompat.app.AppCompatActivity
import androidx.appcompat.widget.Toolbar
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.example.rgb4u.ver1_app.R
import com.example.rgb4u.ver1_app.ui.activity.home.MainActivity
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.database.DataSnapshot
import com.google.firebase.database.DatabaseError
import com.google.firebase.database.DatabaseReference
import com.google.firebase.database.FirebaseDatabase
import com.google.firebase.database.ValueEventListener
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.GlobalScope
import kotlinx.coroutines.delay
import kotlinx.coroutines.launch

class OnboardingActivity : AppCompatActivity() {
    private lateinit var recyclerView: RecyclerView
    private lateinit var btnNext: Button
    private val chatList = mutableListOf<ChatData>()
    private lateinit var adapter: ChatAdapter

    private lateinit var database: DatabaseReference
    // 현재 로그인된 사용자의 UID를 가져오는 함수
    private val userId = FirebaseAuth.getInstance().currentUser?.uid

    private var realnickname: String = ""

    // 캐릭터 메시지 단계별 데이터
    private val characterMessagesTemplate = listOf(
        listOf(
            ChatData("저는 [닉네임]님의 우주에 살고 있는 달토끼, 모아예요.", "CHARACTER"),
            ChatData("오늘은 제가 지내고 있는 우주에 대해 소개해 드리고 싶어요!", "CHARACTER", R.drawable.img_onboarding_02),
            ChatData("하루 동안 학교나 집에서 시간을 보내다 보면 [닉네임]님의 우주에는 다양한 순간들이 펼쳐져요.", "CHARACTER"),
            ChatData("이렇게 매일 경험하는 여러 순간들을 상황이라고 해요.", "CHARACTER"),
            ChatData("예를 들어, 친구와 함께 밥을 먹지 못한 상황이 있을 수 있어요.", "CHARACTER", R.drawable.img_onboarding_03)
        ),
        listOf(
            ChatData("맞아요!", "CHARACTER"),
            ChatData("상황 속에서 [닉네임]님의 우주에는 여러 가지 생각 행성이 나타나요.", "CHARACTER"),
            ChatData("친구와 함께 밥을 먹지 못한 상황에서는...", "CHARACTER"),
            ChatData("'친구가 나를 피하는 것 같아’라는 생각 행성이 떠오를 수 있어요.'", "CHARACTER", R.drawable.img_onboarding_04),
            ChatData("이런 생각 행성이 나타나면 슬프고 속상해져서 감정 별이 뾰족하게 일그러질 수 있죠.", "CHARACTER", R.drawable.img_onboarding_05)
        ),
        listOf(
            ChatData("바로 그거예요!", "CHARACTER"),
            ChatData("이렇게 상황, 생각 행성, 그리고 감정 별은 서로 이어져 있어요.", "CHARACTER"),
            ChatData("똑같은 상황이라도 어떤 생각 행성을 떠올리는지에 따라 감정 별의 모양이 달라질 수 있죠.", "CHARACTER", R.drawable.img_onboarding_06),
            ChatData("같은 상황에서 '친구가 바쁜가 보다'라는 생각 행성을 떠올리면...", "CHARACTER"),
            ChatData("슬픔으로 일그러진 감정 별이 안정될 수 있어요.", "CHARACTER", R.drawable.img_onboarding_07)
        ),
        listOf(
            ChatData("정확해요!", "CHARACTER"),
            ChatData("가끔은 [닉네임]님의 감정 별을 유독 슬프게 만드는 생각 행성이 나타나기도 해요.", "CHARACTER", R.drawable.img_onboarding_08),
            ChatData("그럴 땐 저와 함께 우주 속 생각 행성을 하나씩 살펴봐요!", "CHARACTER", R.drawable.img_onboarding_09),
            ChatData("감정 별을 힘들게 만드는 생각 행성을 찾아서 더 나은 생각 행성으로 바꾸면", "CHARACTER"),
            ChatData("감정 별이 다시 밝게 반짝일 거예요", "CHARACTER", R.drawable.img_onboarding_10)
        )
    )

    // 사용자 메시지 리스트
    private val nextUserMessages = listOf(
        "오늘 있었던 일들이 상황이구나!",
        "생각에 따라 감정이 달라지는구나!",
        "생각을 바꾸면 감정도 달라질 수 있겠다!"
    )

    // 현재 상태 추적용 변수
    private var currentMessageIndex = 0
    private var currentUserMessageIndex = 0

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_onboarding)

        // Firebase Realtime Database 초기화
        database = FirebaseDatabase.getInstance().reference

        val toolbar: Toolbar = findViewById(R.id.toolbar_base1)
        setSupportActionBar(toolbar)

        supportActionBar?.setDisplayHomeAsUpEnabled(false)
        supportActionBar?.setDisplayShowTitleEnabled(false)

        val toolbarTitle = findViewById<TextView>(R.id.toolbar_base1_title)
        toolbarTitle.text = "생각모아"

        val toolbarButton = findViewById<ImageButton>(R.id.button_base1_action2)
        toolbarButton.visibility = View.GONE

        recyclerView = findViewById(R.id.recyclerView)
        btnNext = findViewById(R.id.btnNext)

        val layoutManager = LinearLayoutManager(this)
        layoutManager.stackFromEnd = true
        recyclerView.layoutManager = layoutManager
        adapter = ChatAdapter(chatList)
        recyclerView.adapter = adapter

        btnNext.visibility = View.GONE // 초기에는 버튼 숨김

        // 닉네임 로드
        if (userId != null) {
            getUserData(userId) { nickname ->
                if (nickname != null) {
                    realnickname = nickname
                    updateCharacterMessagesWithNickname()
                    loadInitialChat()
                } else {
                    Log.e("OnboardingActivity", "사용자 데이터를 불러오지 못했습니다.")
                }
            }
        } else {
            Log.e("OnboardingActivity", "사용자 UID를 가져올 수 없습니다.")
        }

        btnNext.setOnClickListener {
            if (btnNext.text == "생각모아 시작") {
                startActivity(Intent(this, MainActivity::class.java))
                finish()
            } else {
                addNextMessageStep()
            }
        }
    }

    private fun updateCharacterMessagesWithNickname() {
        for (step in characterMessagesTemplate) {
            for (message in step) {
                message.message = message.message.replace("[닉네임]", realnickname) // Modify message directly
            }
        }
    }

    private fun loadInitialChat() {
        // 첫 번째 메시지 위에 Starline 추가
        chatList.add(ChatData("", "STARLINE"))  // Starline 추가
        chatList.add(ChatData("안녕하세요!", "CHARACTER", R.drawable.img_onboarding_01))  // 첫 메시지 추가

        adapter.notifyItemRangeInserted(0, 2)  // 두 개 항목이 추가되었으므로 범위로 업데이트
        scrollToBottom()

        GlobalScope.launch(Dispatchers.Main) {
            showCharacterMessagesWithDelay(characterMessagesTemplate[currentMessageIndex])
        }
    }


    private fun addNextMessageStep() {
        if (currentUserMessageIndex < nextUserMessages.size) {
            val userMessage = nextUserMessages[currentUserMessageIndex]
            chatList.add(ChatData(userMessage, "USER"))
            adapter.notifyItemInserted(chatList.size - 1)
            scrollToBottom()

            currentUserMessageIndex++

            if (currentMessageIndex < characterMessagesTemplate.size - 1) {
                currentMessageIndex++
                GlobalScope.launch(Dispatchers.Main) {
                    showCharacterMessagesWithDelay(characterMessagesTemplate[currentMessageIndex])
                }
            }
        }
        updateNextButtonState()
    }

    private fun showCharacterMessagesWithDelay(messages: List<ChatData>) {
        btnNext.visibility = View.GONE // 메시지 추가 중 버튼 숨김

        GlobalScope.launch(Dispatchers.Main) {
            for (message in messages) {
                chatList.add(message)
                adapter.notifyItemInserted(chatList.size - 1)
                scrollToBottom()
                delay(700)
            }
            updateNextButtonState()
        }
    }

    private fun updateNextButtonState() {
        when {
            currentMessageIndex == characterMessagesTemplate.size - 1 &&
                    currentUserMessageIndex == nextUserMessages.size -> {
                btnNext.text = "생각모아 시작"
                btnNext.visibility = View.VISIBLE
            }
            currentUserMessageIndex < nextUserMessages.size -> {
                btnNext.text = nextUserMessages[currentUserMessageIndex]
                btnNext.visibility = View.VISIBLE
            }
            else -> {
                btnNext.visibility = View.GONE
            }
        }
    }

    private fun scrollToBottom() {
        recyclerView.scrollToPosition(chatList.size - 1)
    }

    private fun getUserData(userId: String, callback: (String?) -> Unit) {
        val userRef = database.child("users").child(userId).child("nickname")
        userRef.addListenerForSingleValueEvent(object : ValueEventListener {
            override fun onDataChange(snapshot: DataSnapshot) {
                val nickname = snapshot.getValue(String::class.java)
                callback(nickname)
            }

            override fun onCancelled(error: DatabaseError) {
                Log.e("OnboardingActivity", "데이터베이스 에러: ${error.message}")
                callback(null)
            }
        })
    }
}
