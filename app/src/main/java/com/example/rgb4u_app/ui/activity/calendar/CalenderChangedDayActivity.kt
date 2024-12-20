package com.example.rgb4u_app.ui.activity.calendar

import android.content.Intent
import android.os.Build
import android.os.Bundle
import android.util.Log
import android.view.View
import android.widget.ImageButton
import android.widget.ImageView
import android.widget.LinearLayout
import android.widget.TextView
import androidx.activity.enableEdgeToEdge
import androidx.appcompat.app.AppCompatActivity
import androidx.appcompat.widget.Toolbar
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.example.rgb4u_app.R
import com.example.rgb4u_app.ui.activity.distortiontype.EmotionReselectActivity2
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.database.DataSnapshot
import com.google.firebase.database.DatabaseError
import com.google.firebase.database.DatabaseReference
import com.google.firebase.database.FirebaseDatabase
import com.google.firebase.database.ValueEventListener

class CalenderChangedDayActivity : AppCompatActivity() {

    private lateinit var recyclerView: RecyclerView
    private lateinit var adapter: ChangeDayThinkAdapter
    private lateinit var database: DatabaseReference
    private val situations = mutableListOf<ChangeDaySituation>()
    private lateinit var secondChangedEmotionLayout: LinearLayout
    private lateinit var layoutAddEmotion: LinearLayout
    private lateinit var toolbarDate: String // lateinit으로 선언

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        setContentView(R.layout.activity_calender_changed_day)

        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {
            window.apply {
                decorView.systemUiVisibility =
                    View.SYSTEM_UI_FLAG_LAYOUT_FULLSCREEN or View.SYSTEM_UI_FLAG_LAYOUT_STABLE
                statusBarColor = android.graphics.Color.TRANSPARENT
            }
        }

        // Firebase 초기화
        database = FirebaseDatabase.getInstance().reference

        // diaryId, ID
        val date = intent.getStringExtra("date") ?: "defaultDate"
        val toolbarDate = intent.getStringExtra("Toolbar") ?: "defaultDate"
        // 현재 로그인된 사용자의 UID를 가져오는 함수
        val userId = FirebaseAuth.getInstance().currentUser?.uid

        // Toolbar 설정
        val toolbar: Toolbar = findViewById(R.id.toolbar)
        setSupportActionBar(toolbar)

        // 기본 뒤로가기 버튼, 앱 이름 숨기기
        supportActionBar?.setDisplayHomeAsUpEnabled(false)
        supportActionBar?.setDisplayShowTitleEnabled(false)

        // 툴바의 제목을 "달라진 하루"로 설정
        val toolbarTitle: TextView = findViewById(R.id.toolbar_write_title)
        toolbarTitle.text = "달라진 하루"

        // button_write_action2 버튼 숨기기
        val buttonWriteAction2: ImageButton = findViewById(R.id.button_write_action2)
        buttonWriteAction2.visibility = View.GONE

        // button_write_action1 클릭 리스너 추가
        val buttonWriteAction1: ImageButton = findViewById(R.id.button_write_action1)
        buttonWriteAction1.setOnClickListener {
            val intent = Intent(this, CalendarHomeActivity::class.java)
            startActivity(intent)
            finish()
        }

        // thoughtDetailButton 클릭 리스너 추가
        val thoughtDetailButton: ImageButton = findViewById(R.id.thoughtDetailButton)
        thoughtDetailButton.setOnClickListener {
            val intent = Intent(this, ChangeThinkThisActivity::class.java)
            intent.putExtra("date", date)
            startActivity(intent)
            finish()

        }

        secondChangedEmotionLayout = findViewById(R.id.secondChangedEmotionLayout) // XML ID에 맞게 설정
        layoutAddEmotion = findViewById(R.id.layout_add_emotion)

        val emotionStep1 = findViewById<TextView>(R.id.emotionStep1)
        val emotionIcon1 = findViewById<ImageView>(R.id.emotionIcon1)
        val emotionText1 = findViewById<TextView>(R.id.emotionText1)

        val emotionStep2 = findViewById<TextView>(R.id.emotionStep2)
        val emotionIcon2 = findViewById<ImageView>(R.id.emotionIcon2)
        val emotionText2 = findViewById<TextView>(R.id.emotionText2)

        // RecyclerView 설정
        recyclerView = findViewById(R.id.summaryThinkRecyclerView)
        recyclerView.layoutManager = LinearLayoutManager(this)
        adapter = ChangeDayThinkAdapter(situations)
        recyclerView.adapter = adapter

        if (userId != null && date != null) {
            // 첫 번째 경로에서 데이터 조회
            loadSecondAnalysisData(userId, date)

            // emotionDegree와 emotionTypes를 userInput에서 가져오기
            val userInputRef =
                FirebaseDatabase.getInstance().getReference("users/$userId/diaries/$date/userInput")
            userInputRef.addListenerForSingleValueEvent(object : ValueEventListener {
                override fun onDataChange(userInputSnapshot: DataSnapshot) {
                    // reMeasuredEmotionDegree 데이터를 확인
                    val emotionDegreeInt2 = userInputSnapshot.child("reMeasuredEmotionDegree/int").getValue(Int::class.java) ?: 2
                    val emotionDegreeString2 = userInputSnapshot.child("reMeasuredEmotionDegree/string").getValue(String::class.java) ?: "보통이었어"
                    val emotionDegreeImage2 = userInputSnapshot.child("reMeasuredEmotionDegree/emotionimg").getValue(String::class.java) ?: "img_emotion_2"

                    if (emotionDegreeInt2 != -1) {
                        // 데이터가 있을 때
                        secondChangedEmotionLayout.visibility = View.VISIBLE
                        layoutAddEmotion.visibility = View.GONE

                        emotionStep2.text = "${emotionDegreeInt2 + 1}단계"
                        emotionText2.text = emotionDegreeString2
                        emotionIcon2.setImageResource(getEmotionImageResource(emotionDegreeImage2))
                    } else {
                        // 데이터가 없을 때 '감정 재선택'으로 연결되는 버튼이 나오게끔 설계
                        secondChangedEmotionLayout.visibility = View.GONE // 기존 2번째 감정
                        layoutAddEmotion.visibility = View.VISIBLE // 버튼 있는 레이아웃

                        layoutAddEmotion.setOnClickListener {
                            val intent = Intent(this@CalenderChangedDayActivity, EmotionReselectActivity2::class.java) //화면 연결
                            intent.putExtra("Toolbar", toolbarDate) //toolbar로 고치기
                            intent.putExtra("Date", date) // date보내기
                            startActivity(intent)
                        }
                    }

                    // 첫 번째 감정 데이터는 항상 처리
                    val emotionDegreeInt =
                        userInputSnapshot.child("emotionDegree/int").getValue(Int::class.java) ?: 2
                    val emotionDegreeString =
                        userInputSnapshot.child("emotionDegree/string").getValue(String::class.java) ?: "보통이었어"
                    val emotionDegreeImage = userInputSnapshot.child("emotionDegree/emotionimg").getValue(String::class.java) ?: "img_emotion_2"

                    emotionStep1.text = "${emotionDegreeInt + 1}단계"
                    emotionText1.text = emotionDegreeString
                    emotionIcon1.setImageResource(getEmotionImageResource(emotionDegreeImage))
                }

                override fun onCancelled(error: DatabaseError) {
                    // 에러 처리
                    error.toException().printStackTrace()
                }
            })
        } else {
            Log.e(
                "CalenderChangedDay",
                "Invalid input: userId or date is null. userId=$userId, date=$date"
            )
        }
    }

    private fun loadSecondAnalysisData(userId: String, date: String) {
        val thoughtSetsRef = database.child("users").child(userId)
            .child("diaries").child(date).child("aiAnalysis").child("secondAnalysis")
            .child("thoughtSets")

        thoughtSetsRef.addListenerForSingleValueEvent(object : ValueEventListener {
            override fun onDataChange(snapshot: DataSnapshot) {
                situations.clear()

                // `thoughtSets`의 모든 데이터 탐색
                for (planetSnapshot in snapshot.children) {
                    val planetName = planetSnapshot.key ?: continue

                    // 각 행성에서 첫 번째 데이터만 처리
                    val firstItemSnapshot = planetSnapshot.children.firstOrNull()
                    if (firstItemSnapshot != null) {
                        val imageResourceName =
                            firstItemSnapshot.child("imageResource").value as? String
                        val selectedThoughts =
                            firstItemSnapshot.child("selectedThoughts").value as? String
                        val alternativeThoughts =
                            firstItemSnapshot.child("alternativeThoughts").value as? String

                        if (imageResourceName != null && selectedThoughts != null && alternativeThoughts != null) {
                            val imageResourceId =
                                resources.getIdentifier(imageResourceName, "drawable", packageName)
                            val situation = ChangeDaySituation(
                                imageResourceId,
                                selectedThoughts,
                                alternativeThoughts
                            )
                            situations.add(situation)
                        }
                    }
                }

                // RecyclerView 업데이트
                adapter.notifyDataSetChanged()
            }

            override fun onCancelled(error: DatabaseError) {
                // 에러 처리
                error.toException().printStackTrace()
            }
        })
    }

    // Helper function to get image resource ID from a string
    private fun getEmotionImageResource(imageName: String): Int {
        return resources.getIdentifier(imageName, "drawable", packageName)
    }
}
