package com.example.rgb4u_app.ui.activity.diary

import android.app.Dialog
import android.content.Intent
import android.content.res.ColorStateList //칩 배경색 추가
import android.graphics.Color //칩 배경색 추가
import android.os.Bundle
import android.util.Log
import android.util.TypedValue
import android.view.LayoutInflater
import android.view.Window
import android.view.WindowManager
import android.widget.LinearLayout
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import com.example.rgb4u_app.MyApplication
import com.example.rgb4u_app.R
import com.example.rgb4u_app.ui.activity.home.MainActivity
import com.example.rgb4u_app.ui.activity.summary.SummaryMainActivity
import com.example.rgb4u_app.ui.fragment.MyEmotionFragment
import com.example.rgb4u_app.ui.fragment.TemporarySaveDialogFragment
import com.example.rgb4u_appclass.DiaryViewModel
import com.google.android.material.chip.Chip
import com.google.android.material.chip.ChipGroup
import com.google.android.material.shape.ShapeAppearanceModel
import com.google.firebase.auth.FirebaseAuth
import android.content.Context // 칩 높이
// import android.view.View
import android.view.ViewGroup
import android.widget.EditText
import android.widget.TextView
import androidx.core.content.ContextCompat

class EmotionSelectActivity : AppCompatActivity(), MyEmotionFragment.NavigationListener {

    private lateinit var myEmotionFragment: MyEmotionFragment
    private lateinit var diaryViewModel: DiaryViewModel // ViewModel 선언
    private val selectedEmotions = mutableListOf<String>() // 선택된 감정 리스트
    private lateinit var selectedChipGroup: ChipGroup
    private val maxSelectableChips = 3  // 최대 선택 가능 칩 수
    private lateinit var loadingDialog: Dialog
    private lateinit var toolbarTitle: TextView  // 툴바 제목 텍스트뷰

    // 현재 로그인된 사용자의 UID를 가져오는 함수
    private val userId: String?
        get() = FirebaseAuth.getInstance().currentUser?.uid

    var yyyymmdd: String = ""

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_emotion_select)

        toolbarTitle = findViewById(R.id.toolbar_write_title)
        // Intent로 전달된 toolbarTitle 텍스트 값을 가져옴
        val titleText = intent.getStringExtra("TOOLBAR_TITLE")
        titleText?.let {
            toolbarTitle.text = it // 툴바 제목 텍스트에 설정
        }

        //다이어리뷰모델초기화
        diaryViewModel = (application as MyApplication).diaryViewModel

        // 관찰자 추가.
        diaryViewModel.emotionTypes.observe(this) { emotionTypes ->
            Log.d("EmotionSelectActivity", "Selected emotions in ViewModel: $emotionTypes")
        }

        yyyymmdd = diaryViewModel.getCurrentDate() //diaryviewmodel에서 가져오기

        // 선택된 감정을 ViewModel에 저장하고 다음 화면으로 전환
        if (selectedEmotions.isNotEmpty()) {
            diaryViewModel.emotionTypes.value = selectedEmotions
        }

        // Initialize loading dialog
        loadingDialog = Dialog(this)
        loadingDialog.requestWindowFeature(Window.FEATURE_NO_TITLE)
        loadingDialog.setContentView(R.layout.summary_loading)
        loadingDialog.setCancelable(false)

        myEmotionFragment =
            supportFragmentManager.findFragmentById(R.id.myemotionFragment) as MyEmotionFragment
        myEmotionFragment.setQuestionText("어떤 부정적인 감정을 느꼈는지 골라주세요", "3개까지 고를 수 있어요")
        myEmotionFragment.showIconForStep(4)

        // 4단계에서 폰트 스타일을 바꾸는 부분
        changeFontStyleForStep(4)

        // 버튼 클릭 리스너 설정
        myEmotionFragment.setToolbarButtonListeners(
            backAction = { onToolbarAction1Clicked() }, // 뒤로 가기 버튼 동작을 메서드로 연결
            exitAction = { onToolbarAction2Clicked() } // 나가기 버튼 동작
        )

        selectedChipGroup = findViewById(R.id.selectedChipGroup)

        val emotions = mapOf(
            "Surprise" to listOf("움찔하는", "황당한", "깜짝 놀란", "어안이 벙벙한", "아찔한", "충격적인"),
            "Fear" to listOf("걱정스러운", "긴장된", "불안한", "겁나는", "무서운", "암담한"),
            "Sadness" to listOf("기운 없는", "서운한", "슬픈", "눈물이 나는", "우울한", "비참한"),
            "Anger" to listOf("약 오른", "짜증나는", "화난", "억울한", "분한", "끓어오르는"),
            "Disgust" to listOf("정 떨어지는", "불쾌한", "싫은", "모욕적인", "못마땅한", "미운")
        )

        val inflater = LayoutInflater.from(this)

        val surpriseChipGroup = findViewById<ChipGroup>(R.id.surpriseChipGroup)
        val fearChipGroup = findViewById<ChipGroup>(R.id.fearChipGroup)
        val sadnessChipGroup = findViewById<ChipGroup>(R.id.sadnessChipGroup)
        val angerChipGroup = findViewById<ChipGroup>(R.id.angerChipGroup)
        val disgustChipGroup = findViewById<ChipGroup>(R.id.disgustChipGroup)

        val chipGroupMap = mapOf(
            "Surprise" to surpriseChipGroup,
            "Fear" to fearChipGroup,
            "Sadness" to sadnessChipGroup,
            "Anger" to angerChipGroup,
            "Disgust" to disgustChipGroup
        )

        for ((category, labels) in emotions) {
            val chipGroup = chipGroupMap[category] ?: continue
            for (label in labels) {
                val chip = inflater.inflate(R.layout.single_chip_item, chipGroup, false) as Chip
                chip.text = label
                chip.isCheckable = true
                chip.setTextColor(getColor(R.color.white))
                // 칩 배경색을 설정
                chip.chipBackgroundColor = ColorStateList.valueOf(Color.parseColor("#33FFFFFF"))

                chip.setOnCheckedChangeListener { _, isChecked ->
                    val selectedChipCount = selectedChipGroup.childCount

                    if (isChecked && selectedChipCount >= maxSelectableChips) {
                        // 이미 3개 선택된 상태에서 추가 선택 시 체크 해제하고 토스트 메시지 출력
                        chip.isChecked = false
                        Toast.makeText(this, "3개 이하로 골라주세요", Toast.LENGTH_SHORT).show()
                    } else if (isChecked) {
                        // 칩 선택 시 레이블 색상으로 변경
                        chip.chipBackgroundColor = getChipColor(category)
                        addChipToSelectedGroup(chip, category)
                        selectedEmotions.add(chip.text.toString()) // 선택된 감정 추가
                        chip.closeIcon = ContextCompat.getDrawable(this, R.drawable.ic_chip_delete) // 아이콘 설정
                        Log.d("EmotionSelectActivity", "Added: ${chip.text}") // 추가된 감정 로그

                    } else {
                        // 칩 취소 시 디폴트 색상으로 변경
                        chip.chipBackgroundColor = getColorStateList(R.color.defaultChipColor)
                        removeChipFromSelectedGroup(chip.text.toString())
                        selectedEmotions.remove(chip.text.toString()) // 선택된 감정에서 제거
                        chip.closeIcon = ContextCompat.getDrawable(this, R.drawable.ic_chip_plus) // 아이콘 설정
                        Log.d("EmotionSelectActivity", "Removed: ${chip.text}") // 제거된 감정 로그

                    }

                    updateNextButtonState(selectedChipGroup.childCount)
                }

                // 선택된 감정에 해당하는 경우 칩 선택 상태 유지
                chip.isChecked = selectedEmotions.contains(label)

                chipGroup.addView(chip)
            }
        }
    }

    // 특정 단계에서 폰트 스타일을 변경하는 함수
    private fun changeFontStyleForStep(step: Int) {
        val myTextView: TextView = findViewById(R.id.text_emotion_type)
        if (step == 4) {
            // 4단계에서 폰트 스타일을 Bold로 변경
            myTextView.setTextAppearance(R.style.Cp1_Bold)
            myTextView.setTextColor(ContextCompat.getColor(this, R.color.white_40))
        } else {
            // 기본 스타일로 변경
            myTextView.setTextAppearance(R.style.Cp1_Regular)
            myTextView.setTextColor(ContextCompat.getColor(this, R.color.white_20))
        }
    }

    private fun updateSelectedChipGroup() { // 선택된 칩 그룹 업데이트
        selectedChipGroup.removeAllViews() // 기존의 칩 제거
        selectedEmotions.forEach { emotion ->
            val selectedChip = Chip(this).apply {
                text = emotion
                isCloseIconVisible = true
                setTextColor(getColor(R.color.white))
                setOnCloseIconClickListener {
                    selectedChipGroup.removeView(this)
                    diaryViewModel.emotionTypes.value =
                        diaryViewModel.emotionTypes.value?.filter { it != emotion }
                }
                chipBackgroundColor = getColorStateList(R.color.defaultChipColor) // 디폴트 색상으로 설정
            }
            selectedChipGroup.addView(selectedChip)
        }
    }

    private fun showLoadingDialog() {
        if (!loadingDialog.isShowing) {
            // 다이얼로그를 풀스크린 스타일로 설정
            loadingDialog.window?.setBackgroundDrawableResource(android.R.color.transparent)
            loadingDialog.window?.setLayout(
                WindowManager.LayoutParams.MATCH_PARENT,
                WindowManager.LayoutParams.MATCH_PARENT
            )
            loadingDialog.show()
        }
    }


    private fun hideLoadingDialog() {
        if (loadingDialog.isShowing) {
            loadingDialog.dismiss()
        }
    }

    fun Int.dpToPx(context: Context): Int {
        return (this * context.resources.displayMetrics.density).toInt()
    }

    private fun addChipToSelectedGroup(chip: Chip, category: String) {
        val selectedChip = LayoutInflater.from(this)
            .inflate(R.layout.selected_emotion_chip, selectedChipGroup, false) as Chip

        selectedChip.text = chip.text
        selectedChip.chipBackgroundColor = getChipColor(category) // 배경색 설정

//        // 칩 높이
//        val params = ViewGroup.LayoutParams(
//            ViewGroup.LayoutParams.WRAP_CONTENT,
//            48.dpToPx(this) // 52dp를 px로 변환
//        )
//        selectedChip.layoutParams = params
//
//        // 칩 테두리 투명
//        selectedChip.chipStrokeColor = ColorStateList.valueOf(Color.TRANSPARENT)
//
//        // 원형 모양 설정
//        val shapeAppearanceModel = ShapeAppearanceModel.builder()
//            .setAllCornerSizes(50f) // 적절한 값으로 설정 (dp 단위로 변경 필요)
//            .build()
//        selectedChip.shapeAppearanceModel = shapeAppearanceModel


        selectedChip.setOnCloseIconClickListener {
            selectedChipGroup.removeView(selectedChip)
            uncheckChipInGroup(selectedChip.text.toString())
//            // ChipGroup에 칩이 없으면 다시 숨김
//            if (selectedChipGroup.childCount == 0) {
//                selectedChipGroup.visibility = View.GONE
//            }
        }

        selectedChipGroup.addView(selectedChip)
    }

    private fun getChipColor(category: String) = when (category) {
        "Surprise" -> getColorStateList(R.color.surpriseColor_dark)
        "Fear" -> getColorStateList(R.color.fearColor_dark)
        "Sadness" -> getColorStateList(R.color.sadnessColor_dark)
        "Anger" -> getColorStateList(R.color.angerColor_dark)
        "Disgust" -> getColorStateList(R.color.disgustColor_dark)
        else -> getColorStateList(R.color.defaultChipColor) // 기본 색상
    }

    private fun removeChipFromSelectedGroup(chipText: String) {
        for (i in 0 until selectedChipGroup.childCount) {
            val chip = selectedChipGroup.getChildAt(i) as Chip
            if (chip.text == chipText) {
                selectedChipGroup.removeView(chip)
                break
            }
        }
    }

    private fun uncheckChipInGroup(chipText: String) {
        val chipGroups = listOf(
            findViewById<ChipGroup>(R.id.surpriseChipGroup),
            findViewById<ChipGroup>(R.id.fearChipGroup),
            findViewById<ChipGroup>(R.id.sadnessChipGroup),
            findViewById<ChipGroup>(R.id.angerChipGroup),
            findViewById<ChipGroup>(R.id.disgustChipGroup)
        )

        for (chipGroup in chipGroups) {
            for (i in 0 until chipGroup.childCount) {
                val chip = chipGroup.getChildAt(i) as Chip
                if (chip.text == chipText) {
                    chip.isChecked = false
                    chip.chipBackgroundColor =
                        getColorStateList(R.color.defaultChipColor) // 디폴트 색상으로 변경
                    break
                }
            }
        }
    }

    private fun updateNextButtonState(selectedChipCount: Int) {
        //myRecordFragment.setButtonNextEnabled(selectedChipCount in 1..maxSelectableChips)
    }

    override fun onNextButtonClicked() {
        // 선택된 감정을 ViewModel에 저장하고 다음 화면으로 전환
        Log.d("EmotionSelectActivity", "Before saving: $selectedEmotions") // 선택된 감정 로그
        if (selectedEmotions.isNotEmpty()) {
            diaryViewModel.emotionTypes.value = selectedEmotions // ViewModel에 선택된 감정 저장

            // LiveData의 값이 변경되었음을 확인하기 위해 observe를 사용하여 로그 찍기
            diaryViewModel.emotionTypes.observe(this) { emotionTypes ->
                Log.d(
                    "EmotionSelectActivity",
                    "Selected emotions in ViewModel: $emotionTypes"
                ) // 확인용
            }

            showLoadingDialog()
            diaryViewModel.saveDiaryToFirebase(userId ?: "defaultUserId") //NULL 처리 다시 고민하기

            // 데이터 저장 성공 시 로딩 종료 및 SummaryMainActivity로 이동
            diaryViewModel.onDiarySaved = {
                hideLoadingDialog() // 로딩 다이얼로그 숨기기
                val intent =
                    Intent(this, SummaryMainActivity::class.java) // SummaryMainActivity로 데이터 전달
                intent.putExtra("Date", yyyymmdd) // diaryId를 Intent에 추가
                intent.putExtra("TOOLBAR_TITLE", toolbarTitle.text.toString()) // toolbarTitle.text 값을 전달
                startActivity(intent)
                finish()
            }
        } else {
            Toast.makeText(this, "하나 이상의 감정을 선택해 주세요.", Toast.LENGTH_SHORT).show()
        }
    }

    override fun onToolbarAction1Clicked() {
        val intent = Intent(this, EmotionStrengthActivity::class.java)
        startActivity(intent)
        finish()
    }

    override fun onToolbarAction2Clicked() {
        // TemporarySaveDialogFragment 인스턴스 생성
        val dialog = TemporarySaveDialogFragment()

        // dialog의 리스너 설정
        dialog.listener = object : TemporarySaveDialogFragment.OnButtonClickListener {
            override fun onTemporarySave() {
                if (selectedEmotions.isNotEmpty()) {
                    diaryViewModel.emotionTypes.value = selectedEmotions // ViewModel에 선택된 감정 저장

                    // LiveData의 값이 변경되었음을 확인하기 위해 observe를 사용하여 로그 찍기
                    diaryViewModel.emotionTypes.observe(this@EmotionSelectActivity) { emotionTypes ->
                        Log.d("EmotionSelectActivity", "Selected emotions in ViewModel: $emotionTypes")
                    }
                diaryViewModel.saveTemporaryDiaryToFirebase(userId ?: "defaultUserId") //NULL 처리 다시 고민하기
                val intent = Intent(this@EmotionSelectActivity, MainActivity::class.java)
                startActivity(intent)
                finish()
            }
                }
            override fun onDelete() {
                // 삭제 동작: 아무것도 저장하지 않고 MainActivity로 이동
                val intent = Intent(this@EmotionSelectActivity, MainActivity::class.java)
                startActivity(intent)
                finish()
            }
        }

        // 팝업 표시
        dialog.show(supportFragmentManager, "TemporarySaveDialog")
    }
}