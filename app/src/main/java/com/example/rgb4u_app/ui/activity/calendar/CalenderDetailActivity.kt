package com.example.rgb4u.ver1_app.ui.activity.calendar

import android.content.Intent
import android.os.Build
import android.os.Bundle
import android.util.Log
import android.view.View
import android.widget.ImageButton
import android.widget.ImageView
import android.widget.TextView
import androidx.activity.viewModels
import androidx.appcompat.app.AppCompatActivity
import androidx.appcompat.widget.Toolbar
import androidx.core.content.ContextCompat
import com.example.rgb4u.ver1_app.R
import com.example.rgb4u.ver1_appclass.DiaryViewModel
import com.google.android.material.button.MaterialButton
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.database.DataSnapshot
import com.google.firebase.database.DatabaseError
import com.google.firebase.database.DatabaseReference
import com.google.firebase.database.FirebaseDatabase
import com.google.firebase.database.ValueEventListener
import com.google.android.material.chip.Chip
import com.google.android.material.chip.ChipGroup

class CalenderDetailActivity : AppCompatActivity() {

    private lateinit var database: DatabaseReference
    private val diaryViewModel: DiaryViewModel by viewModels()

    // 감정 타입별 상위 카테고리 매핑
    private val emotionCategoryMap = mapOf(
        "움찔하는" to "Surprise", "황당한" to "Surprise", "깜짝 놀란" to "Surprise",
        "어안이 벙벙한" to "Surprise", "아찔한" to "Surprise", "충격적인" to "Surprise",
        "걱정스러운" to "Fear", "긴장된" to "Fear", "불안한" to "Fear",
        "겁나는" to "Fear", "무서운" to "Fear", "암담한" to "Fear",
        "기운 없는" to "Sadness", "서운한" to "Sadness", "슬픈" to "Sadness",
        "눈물이 나는" to "Sadness", "우울한" to "Sadness", "비참한" to "Sadness",
        "약 오른" to "Anger", "짜증나는" to "Anger", "화난" to "Anger",
        "억울한" to "Anger", "분한" to "Anger", "끓어오르는" to "Anger",
        "정 떨어지는" to "Disgust", "불쾌한" to "Disgust", "싫은" to "Disgust",
        "모욕적인" to "Disgust", "못마땅한" to "Disgust", "미운" to "Disgust"
    )

    // Chip 배경색 가져오기 함수
    private fun getChipColor(category: String) = when (category) {
        "Surprise" -> getColorStateList(R.color.surpriseColor_dark)
        "Fear" -> getColorStateList(R.color.fearColor_dark)
        "Sadness" -> getColorStateList(R.color.sadnessColor_dark)
        "Anger" -> getColorStateList(R.color.angerColor_dark)
        "Disgust" -> getColorStateList(R.color.disgustColor_dark)
        else -> getColorStateList(R.color.defaultChipColor) // 기본 색상
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_calender_detail)

        // 투명 상태바
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {
            window.apply {
                decorView.systemUiVisibility =
                    View.SYSTEM_UI_FLAG_LAYOUT_FULLSCREEN or View.SYSTEM_UI_FLAG_LAYOUT_STABLE
                statusBarColor = android.graphics.Color.TRANSPARENT
            }
        }

        // Toolbar 설정
        val toolbar: Toolbar = findViewById(R.id.toolbar)
        setSupportActionBar(toolbar)

        // 기본 뒤로가기 버튼, 앱 이름 숨기기
        supportActionBar?.setDisplayHomeAsUpEnabled(false)
        supportActionBar?.setDisplayShowTitleEnabled(false)

        // 툴바의 제목을 날짜로 설정
        val toolbarTitle: TextView = findViewById(R.id.toolbar_write_title)

        // 오른쪽 아이콘 안 보이게
        val buttonWriteAction2: ImageButton = findViewById(R.id.button_write_action2)
        buttonWriteAction2.visibility = View.GONE

        // Intent로부터 날짜 정보 받기
        val selectedDate = intent.getStringExtra("SELECTED_DATE")
        selectedDate?.let {
            toolbarTitle.text = it // 툴바 제목에 날짜 정보 설정
        }
        val datefordb = intent.getStringExtra("SELECTED_DATE_FOR_DB")
        Log.d("CalenderDetailActivity", "datefordb: $datefordb")


        // situationTextView와 thoughtTextView 참조
        val situationTextView = findViewById<TextView>(R.id.situationTextView)
        val thoughtTextView = findViewById<TextView>(R.id.thoughtTextView)
        val emotionIntensityTextView = findViewById<TextView>(R.id.emotionIntensityTextView)
        val emotionIntensityImageView = findViewById<ImageView>(R.id.emotionIntensityImageView)
        val emotionIntensityTextView02 = findViewById<TextView>(R.id.emotionIntensityTextView02)
        // 칩 그룹 참조
        val selectedChipGroup = findViewById<ChipGroup>(R.id.SummarySelectedChipGroup)
        val emotionChipGroup = findViewById<ChipGroup>(R.id.SummaryEmotionChipGroup)

        // 현재 로그인된 사용자의 UID를 가져오는 함수
        val userId = FirebaseAuth.getInstance().currentUser?.uid
        val changedDayButton: MaterialButton = findViewById(R.id.buttonNext)

        // totalCharacters 값 확인 후 '달라진 하루' 버튼 가리기
        val totalCharactersRef = FirebaseDatabase.getInstance()
            .getReference("users/$userId/diaries/$datefordb/aiAnalysis/secondAnalysis/totalCharacters")

        totalCharactersRef.addListenerForSingleValueEvent(object : ValueEventListener {
            override fun onDataChange(snapshot: DataSnapshot) {
                val totalCharacters = snapshot.getValue(Long::class.java) ?: 0L
                if (totalCharacters == 0L) {
                    changedDayButton.visibility = View.GONE
                } else {
                    changedDayButton.visibility = View.VISIBLE
                }
            }

            override fun onCancelled(error: DatabaseError) {
                error.toException().printStackTrace()
            }
        })

        // Firebase로부터 데이터 가져오기
        if (userId != null && datefordb != null) {
            // aiAnalysis 데이터 조회
            database = FirebaseDatabase.getInstance().getReference("users/$userId/diaries/$datefordb/aiAnalysis/firstAnalysis")
            database.addListenerForSingleValueEvent(object : ValueEventListener {
                override fun onDataChange(dataSnapshot: DataSnapshot) {
                    if (dataSnapshot.exists()) {
                        // 상황과 생각 가져오기
                        val situation = dataSnapshot.child("situation").getValue(String::class.java) ?: "상황 정보 없음"
                        val thoughts = dataSnapshot.child("thoughts").getValue(String::class.java) ?: "생각 정보 없음"

                        // TextView에 Realtime Database에서 가져온 값 설정
                        situationTextView.text = situation

                        // thoughts를 문장부호 기준으로 나누고 포맷팅
                        val formattedThoughts = thoughts.split(Regex("(?<=[.!?])\\s*")) // 문장부호 뒤에서 나누기
                            .filter { it.isNotBlank() } // 빈 문자열 제거
                            .joinToString("\n") { "•  $it" } // 각 문장 앞에 "• " 추가하고 줄바꿈
                        thoughtTextView.text = formattedThoughts

                        // emotionDegree와 emotionTypes를 userInput에서 가져오기
                        val userInputRef = FirebaseDatabase.getInstance().getReference("users/$userId/diaries/$datefordb/userInput")
                        userInputRef.addListenerForSingleValueEvent(object : ValueEventListener {
                            override fun onDataChange(userInputSnapshot: DataSnapshot) {
                                // emotionDegree에서 int와 string 가져오기
                                val emotionDegreeInt = userInputSnapshot.child("emotionDegree/int").getValue(Int::class.java) ?: 2
                                val emotionDegreeString = userInputSnapshot.child("emotionDegree/string").getValue(String::class.java) ?: "보통이었어"
                                val emotionDegreeImage = userInputSnapshot.child("emotionDegree/emotionimg").getValue(String::class.java) ?: "img_emotion_2"

                                // TextView에 감정 강도 설정
                                emotionIntensityTextView.text = "${emotionDegreeInt + 1}단계"
                                emotionIntensityTextView02.text = emotionDegreeString

                                // 감정 강도에 따른 이미지 설정
                                emotionIntensityImageView.setImageResource(getEmotionImageResource(emotionDegreeImage))

                                // emotionTypes는 리스트 형태로 가져온다
                                val emotionTypesList = userInputSnapshot.child("emotionTypes").children.mapNotNull { it.getValue(String::class.java) }
                                val emotionTypes = emotionTypesList.joinToString(", ") // 리스트를 문자열로 변환

                                // AI 분석 감정용 emotion 리스트 가져오기 ()
                                val emotionsList = dataSnapshot.child("emotion").children.mapNotNull { it.getValue(String::class.java) }

                                // 감정 강도와 감정 종류를 로그에 출력
                                Log.d("SummaryMainActivity", "감정 강도: $emotionDegreeInt ($emotionDegreeString), 감정 종류: $emotionTypes")

                                //selectedChipGroup에 감정 추가
                                for (emotion in emotionTypesList) {
                                    val category = emotionCategoryMap[emotion] ?: "default" // 감정의 상위 카테고리 찾기
                                    val chip = layoutInflater.inflate(R.layout.summary_selected_chip, emotionChipGroup, false) as Chip

                                        chip.text = emotion
                                        chip.isCloseIconVisible = false // 닫기 아이콘 숨기기
                                        chip.isClickable = false // 칩 클릭 비활성화
                                        chip.isFocusable = false // 포커스 비활성화

                                        chip.setTextColor(ContextCompat.getColor(this@CalenderDetailActivity, R.color.white)) // 텍스트 색상
                                        chip.chipBackgroundColor = getChipColor(category) // 배경색 설정
                                    // 선택한 감정 추가
                                    selectedChipGroup.addView(chip)
                                }

                                for (emotion in emotionsList) {
                                    val chip = layoutInflater.inflate(R.layout.summary_single_chip, emotionChipGroup, false) as Chip
                                    chip.text = emotion
                                    chip.isCloseIconVisible = false // 닫기 아이콘 숨기기
                                    chip.isClickable = false // 칩 클릭 비활성화
                                    chip.isFocusable = false // 포커스 비활성화

                                    // Chip의 shapeAppearanceModel 설정
                                    chip.shapeAppearanceModel = chip.shapeAppearanceModel.toBuilder()
                                        .setAllCornerSizes(50f) // 모서리 둥글기
                                        .build()

                                    // 텍스트 색상 설정
                                    chip.setTextColor(ContextCompat.getColor(this@CalenderDetailActivity, R.color.black)) // 텍스트 색상
                                    // 칩 테두리 색상 설정
                                    chip.setChipStrokeColorResource(R.color.black) // 테두리 색상
                                    chip.chipStrokeWidth = 2f // 테두리 두께 설정 (필요시 조정)

                                    emotionChipGroup.addView(chip) // ChipGroup에 추가
                                }

                            }

                            override fun onCancelled(databaseError: DatabaseError) {
                                // 오류 처리
                                Log.e("CalenderDetailActivity", "emotionDegree와 emotionTypes를 불러오는 데 실패했습니다: ${databaseError.message}")
                            }
                        })
                    } else {
                        // 데이터가 존재하지 않는 경우
                        situationTextView.text = "데이터가 존재하지 않습니다"
                        thoughtTextView.text = "데이터가 존재하지 않습니다"
                    }
                }

                override fun onCancelled(databaseError: DatabaseError) {
                    // 오류 처리
                    situationTextView.text = "오류 발생: ${databaseError.message}"
                    thoughtTextView.text = "오류 발생: ${databaseError.message}"
                }
            })
        } else {
            // userId가 null인 경우 처리
            situationTextView.text = "사용자 ID를 찾을 수 없음"
            thoughtTextView.text = "사용자 ID를 찾을 수 없음"
        }

        // situationDetailButton 클릭 리스너 추가
        findViewById<ImageButton>(R.id.situationDetailButton).setOnClickListener {
            // CalenderSituationActivity로 이동
            val intent = Intent(this, CalenderSituationActivity::class.java)
            intent.putExtra("date", datefordb) // datefordb를 Intent에 추가
            startActivity(intent)
        }

        // thoughtDetailButton 클릭 리스너 추가
        findViewById<ImageButton>(R.id.thoughtDetailButton).setOnClickListener {
            // CalenderThinkActivity로 이동
            val intent = Intent(this, CalenderThinkActivity::class.java)
            intent.putExtra("date", datefordb) // datefordb를 Intent에 추가
            startActivity(intent)
        }

        // buttonNext 클릭 리스너 추가
        findViewById<MaterialButton>(R.id.buttonNext).setOnClickListener {
            // CalenderChangedDayActivity로 이동
            val intent = Intent(this, CalenderChangedDayActivity::class.java)
            intent.putExtra("date", datefordb) // datefordb를 Intent에 추가
            intent.putExtra("Toolbar", selectedDate) //toolbar로 고치기
            startActivity(intent)
        }
    }

    // emotionimg 값에 따라 적절한 이미지 리소스를 반환하는 함수
    private fun getEmotionImageResource(emotionImg: String): Int {
        return when (emotionImg) {
            "img_emotion_0" -> R.drawable.img_emotion_0
            "img_emotion_1" -> R.drawable.img_emotion_1
            "img_emotion_2" -> R.drawable.img_emotion_2
            "img_emotion_3" -> R.drawable.img_emotion_3
            else -> R.drawable.img_emotion_4 // 4번 이미지 설정
        }
    }
}
