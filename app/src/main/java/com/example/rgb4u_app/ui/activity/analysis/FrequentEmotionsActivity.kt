package com.example.rgb4u_app.ui.activity.analysis

import android.content.Intent
import android.content.res.ColorStateList
import android.graphics.Color
import android.os.Bundle
import android.util.TypedValue
import android.view.View
import android.widget.ImageButton
import android.widget.LinearLayout
import android.widget.TextView
import androidx.appcompat.app.AppCompatActivity
import androidx.appcompat.widget.Toolbar
import androidx.cardview.widget.CardView
import com.example.rgb4u_app.R
import com.google.android.material.chip.Chip
import com.google.android.material.chip.ChipGroup
import com.google.android.material.shape.ShapeAppearanceModel
import android.util.Log
import com.google.firebase.database.DatabaseReference
import com.google.firebase.database.FirebaseDatabase
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.database.DataSnapshot

class FrequentEmotionsActivity : AppCompatActivity() {

    // Firebase Database 참조 선언
    private lateinit var database: DatabaseReference

    // View 선언
    private lateinit var surpriseCard: CardView
    private lateinit var surpriseChipGroup: ChipGroup
    private lateinit var fearCard: CardView
    private lateinit var fearChipGroup: ChipGroup
    private lateinit var sadnessCard: CardView
    private lateinit var sadnessChipGroup: ChipGroup
    private lateinit var angerCard: CardView
    private lateinit var angerChipGroup: ChipGroup
    private lateinit var disgustCard: CardView
    private lateinit var disgustChipGroup: ChipGroup

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_frequent_emotions)

        val selectedDate = intent.getStringExtra("selectedDate") ?: "2024-10"  // 기본값은 "2024-10"으로 설정

        // Firebase Authentication을 통해 현재 로그인한 사용자 UID 가져오기
        val user = FirebaseAuth.getInstance().currentUser
        if (user != null) {
            // 로그인된 사용자 UID로 Database 경로 설정
            val userId = user.uid
            database = FirebaseDatabase.getInstance().getReference("users/$userId/monthlyStats/$selectedDate")
            Log.d("FrequentEmotionsActivity", "사용자 UID: $userId, 선택된 날짜: $selectedDate")

            // Firebase에서 감정 데이터를 불러와 업데이트
            loadEmotionData()
        } else {
            // 로그인되지 않은 경우 처리
            Log.e("FrequentEmotionsActivity", "사용자가 로그인되지 않았습니다.")
            // 로그인 페이지로 이동하거나 에러 메시지 표시
        }

        // findViewById로 각 View 연결
        surpriseCard = findViewById(R.id.surpriseCard)
        surpriseChipGroup = findViewById(R.id.surpriseChipGroup)
        fearCard = findViewById(R.id.fearCard)
        fearChipGroup = findViewById(R.id.fearChipGroup)
        sadnessCard = findViewById(R.id.sadnessCard)
        sadnessChipGroup = findViewById(R.id.sadnessChipGroup)
        angerCard = findViewById(R.id.angerCard)
        angerChipGroup = findViewById(R.id.angerChipGroup)
        disgustCard = findViewById(R.id.disgustCard)
        disgustChipGroup = findViewById(R.id.disgustChipGroup)

        // 툴바 설정
        val toolbar = findViewById<Toolbar>(R.id.toolbar_frequent)
        setSupportActionBar(toolbar)
        supportActionBar?.setDisplayHomeAsUpEnabled(false)
        supportActionBar?.setDisplayShowTitleEnabled(false)

        // 툴바 타이틀 설정
        findViewById<TextView>(R.id.toolbar_base1_title).text = "내가 자주 선택한 감정"
        findViewById<ImageButton>(R.id.button_base1_action1).setOnClickListener {
            val intent = Intent(this, AnalysisActivity::class.java)
            startActivity(intent)
            finish()
        }
        findViewById<ImageButton>(R.id.button_base1_action2).visibility = View.GONE
    }

    // Firebase에서 감정 데이터를 불러오는 함수
    private fun loadEmotionData() {
        Log.d("FrequentEmotionsActivity", "감정 데이터를 불러오는 중...")

        // 감정별 키워드 데이터 가져오기
        database.child("SurpriseKeyword").get().addOnSuccessListener {
            Log.d("FrequentEmotionsActivity", "SurpriseKeyword 데이터 불러오기 성공")
            val surpriseData = getTopKeywords(it)
            updateChipGroup(surpriseChipGroup, surpriseData, "surprise")
        }.addOnFailureListener {
            Log.e("FrequentEmotionsActivity", "SurpriseKeyword 데이터 불러오기 실패: ${it.message}")
        }

        database.child("FearKeyword").get().addOnSuccessListener {
            Log.d("FrequentEmotionsActivity", "FearKeyword 데이터 불러오기 성공")
            val fearData = getTopKeywords(it)
            updateChipGroup(fearChipGroup, fearData, "fear")
        }.addOnFailureListener {
            Log.e("FrequentEmotionsActivity", "FearKeyword 데이터 불러오기 실패: ${it.message}")
        }

        database.child("SadnessKeyword").get().addOnSuccessListener {
            Log.d("FrequentEmotionsActivity", "SadnessKeyword 데이터 불러오기 성공")
            val sadnessData = getTopKeywords(it)
            updateChipGroup(sadnessChipGroup, sadnessData, "sadness")
        }.addOnFailureListener {
            Log.e("FrequentEmotionsActivity", "SadnessKeyword 데이터 불러오기 실패: ${it.message}")
        }

        database.child("AngerKeyword").get().addOnSuccessListener {
            Log.d("FrequentEmotionsActivity", "AngerKeyword 데이터 불러오기 성공")
            val angerData = getTopKeywords(it)
            updateChipGroup(angerChipGroup, angerData, "anger")
        }.addOnFailureListener {
            Log.e("FrequentEmotionsActivity", "AngerKeyword 데이터 불러오기 실패: ${it.message}")
        }

        database.child("DisgustKeyword").get().addOnSuccessListener {
            Log.d("FrequentEmotionsActivity", "DisgustKeyword 데이터 불러오기 성공")
            val disgustData = getTopKeywords(it)
            updateChipGroup(disgustChipGroup, disgustData, "disgust")
        }.addOnFailureListener {
            Log.e("FrequentEmotionsActivity", "DisgustKeyword 데이터 불러오기 실패: ${it.message}")
        }
    }

    // 감정 키워드 중 상위 3개의 키워드를 가져오는 함수
    private fun getTopKeywords(snapshot: DataSnapshot): List<String> {
        Log.d("FrequentEmotionsActivity", "getTopKeywords 함수 실행 중...")

        // 키워드 데이터와 정수 값들을 리스트로 변환
        val keywordList = snapshot.children.mapNotNull {
            val keyword = it.key ?: return@mapNotNull null
            val value = it.value.toString().toIntOrNull() ?: return@mapNotNull null
            keyword to value
        }
        Log.d("FrequentEmotionsActivity", "불러온 키워드 데이터: $keywordList")

        // 값이 1 이상인 것들 중 상위 3개 선택
        return keywordList.filter { it.second > 0 }
            .sortedByDescending { it.second }
            .take(3)
            .map { "${it.first} ${it.second}" }
    }

    // ChipGroup에 Chip 추가
    private fun updateChipGroup(chipGroup: ChipGroup, emotionList: List<String>, emotionType: String) {
        Log.d("FrequentEmotionsActivity", "updateChipGroup 함수 실행 중... 감정: $emotionType")

        chipGroup.removeAllViews()

        val textColor = when (emotionType) {
            "surprise" -> Color.parseColor("#33A080")
            "fear" -> Color.parseColor("#339EB3")
            "sadness" -> Color.parseColor("#2795DD")
            "anger" -> Color.parseColor("#C771C7")
            "disgust" -> Color.parseColor("#7461D1")
            else -> Color.BLACK
        }

        for (emotion in emotionList) {
            Log.d("FrequentEmotionsActivity", "Chip 추가: $emotion")
            val chip = Chip(this).apply {
                text = emotion
                setTextAppearance(R.style.chipText)
                shapeAppearanceModel = ShapeAppearanceModel.builder().setAllCornerSizes(50f).build()
                chipBackgroundColor = ColorStateList.valueOf(Color.WHITE)
                setTextColor(textColor)
                isClickable = false
                isCheckable = false
                chipStrokeWidth = 0f
                layoutParams = LinearLayout.LayoutParams(
                    LinearLayout.LayoutParams.WRAP_CONTENT,
                    TypedValue.applyDimension(TypedValue.COMPLEX_UNIT_DIP, 52f, resources.displayMetrics).toInt()
                )
            }
            chipGroup.addView(chip)
        }

        Log.d("FrequentEmotionsActivity", "ChipGroup 업데이트 완료: $emotionType")
    }
}
