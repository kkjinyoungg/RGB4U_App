package com.example.rgb4u_app.ui.activity.diary

import android.app.Dialog
import android.content.Intent
import android.os.Bundle
import android.os.Handler
import android.view.LayoutInflater
import android.view.Window
import android.view.WindowManager
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import com.example.rgb4u_app.R
import com.example.rgb4u_app.ui.activity.summary.SummaryMainActivity
import com.example.rgb4u_app.ui.fragment.MyRecordFragment
import com.google.android.material.chip.Chip
import com.google.android.material.chip.ChipGroup
import com.example.rgb4u_appclass.DiaryViewModel
import androidx.lifecycle.ViewModelProvider
import androidx.lifecycle.ViewModelStoreOwner
import com.example.rgb4u_app.MyApplication
import android.util.Log
import com.example.rgb4u_app.ChatGptAnalyzer


class EmotionSelectActivity : AppCompatActivity(), MyRecordFragment.NavigationListener {

    private lateinit var myRecordFragment: MyRecordFragment
    private lateinit var diaryViewModel: DiaryViewModel // ViewModel 선언
    private val selectedEmotions = mutableListOf<String>() // 선택된 감정 리스트
    private lateinit var selectedChipGroup: ChipGroup
    private val maxSelectableChips = 3  // 최대 선택 가능 칩 수
    private lateinit var loadingDialog: Dialog
    private lateinit var chatGptAnalyzer: ChatGptAnalyzer

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_emotion_select)

        chatGptAnalyzer = ChatGptAnalyzer() //초기화

        // Application에서 ViewModel 가져오기
        diaryViewModel = (application as MyApplication).diaryViewModel

        // 관찰자 추가
        diaryViewModel.emotionTypes.observe(this) { emotionTypes ->
            Log.d("EmotionSelectActivity", "Selected emotions in ViewModel: $emotionTypes")
        }

        // 선택된 감정을 ViewModel에 저장하고 다음 화면으로 전환
        if (selectedEmotions.isNotEmpty()) {
            diaryViewModel.emotionTypes.value = selectedEmotions
        }

        // Initialize loading dialog
        loadingDialog = Dialog(this)
        loadingDialog.requestWindowFeature(Window.FEATURE_NO_TITLE)
        loadingDialog.setContentView(R.layout.summary_loading)
        loadingDialog.setCancelable(false)

        myRecordFragment = supportFragmentManager.findFragmentById(R.id.myrecordFragment) as MyRecordFragment
        myRecordFragment.setQuestionText("어떤 부정적인 감정을 느꼈는지 골라주세요", "3개까지 고를 수 있어요")
        myRecordFragment.showIconForStep(4)

        selectedChipGroup = findViewById(R.id.selectedChipGroup)

        val ChatGptAnalyzer = ChatGptAnalyzer() // 변수로 선언

        val emotions = mapOf(
            "Surprise" to listOf("어안이 벙벙한", "아찔한", "황당한", "깜짝 놀란", "움찔하는", "충격적인"),
            "Fear" to listOf("걱정스러운", "암담한", "겁나는", "무서운", "불안한", "긴장된"),
            "Sadness" to listOf("기운 없는", "슬픈", "눈물이 나는", "우울한", "비참한", "서운한"),
            "Anger" to listOf("화난", "끓어오르는", "분한", "짜증나는", "약 오른", "억울한"),
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
                chip.setTextColor(getColor(R.color.black))

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
                        Log.d("EmotionSelectActivity", "Added: ${chip.text}") // 추가된 감정 로그

                    } else {
                        // 칩 취소 시 디폴트 색상으로 변경
                        chip.chipBackgroundColor = getColorStateList(R.color.defaultChipColor)
                        removeChipFromSelectedGroup(chip.text.toString())
                        selectedEmotions.remove(chip.text.toString()) // 선택된 감정에서 제거
                        Log.d("EmotionSelectActivity", "Removed: ${chip.text}") // 제거된 감정 로그

                    }

                    updateNextButtonState(selectedChipGroup.childCount)
                }

                // 선택된 감정에 해당하는 경우 칩 선택 상태 유지
                chip.isChecked = selectedEmotions.contains(label)

                chipGroup.addView(chip)
            }
        }

        myRecordFragment.setHelpButtonEnabled(false)
        myRecordFragment.setHelpButtonVisibility(false)
    }

    private fun updateSelectedChipGroup() { // 선택된 칩 그룹 업데이트
        selectedChipGroup.removeAllViews() // 기존의 칩 제거
        selectedEmotions.forEach { emotion ->
            val selectedChip = Chip(this).apply {
                text = emotion
                isCloseIconVisible = true
                setTextColor(getColor(R.color.black))
                setOnCloseIconClickListener {
                    selectedChipGroup.removeView(this)
                    diaryViewModel.emotionTypes.value = diaryViewModel.emotionTypes.value?.filter { it != emotion }
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

    private fun addChipToSelectedGroup(chip: Chip, category: String) {
        val selectedChip = Chip(this)
        selectedChip.text = chip.text
        selectedChip.isCloseIconVisible = true
        selectedChip.setTextColor(getColor(R.color.black))

        // 기본 색상으로 설정
        selectedChip.chipBackgroundColor = getColorStateList(R.color.defaultChipColor)
        // 원형 모양 설정
        selectedChip.chipCornerRadius = 50f // 적절한 값으로 설정 (dp 단위로 변경 필요)

        selectedChip.setOnCloseIconClickListener {
            selectedChipGroup.removeView(selectedChip)
            uncheckChipInGroup(selectedChip.text.toString())
        }

        selectedChipGroup.addView(selectedChip)
    }

    private fun getChipColor(category: String) = when (category) {
        "Surprise" -> getColorStateList(R.color.surpriseColor)
        "Fear" -> getColorStateList(R.color.fearColor)
        "Sadness" -> getColorStateList(R.color.sadnessColor)
        "Anger" -> getColorStateList(R.color.angerColor)
        "Disgust" -> getColorStateList(R.color.disgustColor)
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
                    chip.chipBackgroundColor = getColorStateList(R.color.defaultChipColor) // 디폴트 색상으로 변경
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
                Log.d("EmotionSelectActivity", "Selected emotions in ViewModel: $emotionTypes") // 확인용
            }

            showLoadingDialog()
            diaryViewModel.saveDiaryToFirebase("userId") // 파이어베이스에 데이터 저장 [ID 잘 설정해야]

            // Firebase에서 사용자 입력을 가져와 분석 수행
            val situationText = "사용자가 입력한 상황" // Firebase에서 가져온 상황 텍스트
            val thoughtsText = "사용자가 입력한 생각" // Firebase에서 가져온 생각 텍스트

            ChatGptAnalyzer.classifyText(situationText, thoughtsText, "YOUR_API_KEY") { emotion: String?, situation: String?, thought: String? ->
                ChatGptAnalyzer.saveResultsToRealtimeDatabase("userId", "diaryId", emotion, situation, thought) // diaryId는 적절히 설정
            }

            // 2초 후에 SummaryMainActivity로 이동
            Handler().postDelayed({
                hideLoadingDialog() //로딩다이얼로그 숨기기
                val intent = Intent(this, SummaryMainActivity::class.java) // SummaryMainActivity로 데이터 전달
                //intent.putExtra("EXTRA_SITUATION_TEXT", situationText)
                //intent.putExtra("EXTRA_THOUGHT_TEXT", thoughtText)
                startActivity(intent)
                finish()
            }, 2000) //2초 동안 로딩
        } else {
            Toast.makeText(this, "하나 이상의 감정을 선택해 주세요.", Toast.LENGTH_SHORT).show()
        }
    }

    override fun onBackButtonClicked() {
        val intent = Intent(this, EmotionStrengthActivity::class.java)
        startActivity(intent)
        finish()
    }
}