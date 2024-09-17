package com.example.rgb4u_app.ui.activity

import android.content.Intent
import android.os.Bundle
import android.view.LayoutInflater
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import com.example.rgb4u_app.R
import com.example.rgb4u_app.ui.fragment.MyRecordFragment
import com.google.android.material.chip.Chip
import com.google.android.material.chip.ChipGroup

class EmotionSelectActivity : AppCompatActivity(), MyRecordFragment.NavigationListener {

    private lateinit var myRecordFragment: MyRecordFragment

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_emotion_select)

        // Initialize fragment
        myRecordFragment = supportFragmentManager.findFragmentById(R.id.myrecordFragment) as MyRecordFragment

        // Set text and icon for fragment
        myRecordFragment.setQuestionText("어떤 부정적인 감정을 느꼈는지 골라주세요", "3개까지 고를 수 있어요")
        myRecordFragment.showIconForStep(4)

        // Emotion categories and chip labels
        val emotions = mapOf(
            "Surprise" to listOf("어안이 벙벙한", "아찔한", "황당한", "깜짝 놀란", "움찔하는", "충격적인"),
            "Fear" to listOf("걱정스러운", "암담한", "겁나는", "무서운", "불안한", "긴장된"),
            "Sadness" to listOf("기운 없는", "슬픈", "눈물이 나는", "우울한", "비참한", "서운한"),
            "Anger" to listOf("화난", "끓어오르는", "분한", "짜증나는", "약 오른", "억울한"),
            "Disgust" to listOf("정 떨어지는", "불쾌한", "싫은", "모욕적인", "못마땅한", "미운")
        )

        // LayoutInflater for creating chips
        val inflater = LayoutInflater.from(this)

        // Access existing ChipGroups from XML
        val surpriseChipGroup = findViewById<ChipGroup>(R.id.surpriseChipGroup)
        val fearChipGroup = findViewById<ChipGroup>(R.id.fearChipGroup)
        val sadnessChipGroup = findViewById<ChipGroup>(R.id.sadnessChipGroup)
        val angerChipGroup = findViewById<ChipGroup>(R.id.angerChipGroup)
        val disgustChipGroup = findViewById<ChipGroup>(R.id.disgustChipGroup)

        // Map ChipGroups to their respective emotions
        val chipGroupMap = mapOf(
            "Surprise" to surpriseChipGroup,
            "Fear" to fearChipGroup,
            "Sadness" to sadnessChipGroup,
            "Anger" to angerChipGroup,
            "Disgust" to disgustChipGroup
        )

        // Add chips to ChipGroups
        for ((category, labels) in emotions) {
            val chipGroup = chipGroupMap[category] ?: continue
            for (label in labels) {
                val chip = inflater.inflate(R.layout.single_chip_item, chipGroup, false) as Chip
                chip.text = label
                chip.isCheckable = true
                chip.setTextColor(getColor(R.color.white))
                chip.setOnCheckedChangeListener { _, isChecked ->
                    // Apply color based on the category when selected
                    when (category) {
                        "Surprise" -> chip.chipBackgroundColor = getColorStateList(R.color.surpriseColor)
                        "Fear" -> chip.chipBackgroundColor = getColorStateList(R.color.fearColor)
                        "Sadness" -> chip.chipBackgroundColor = getColorStateList(R.color.sadnessColor)
                        "Anger" -> chip.chipBackgroundColor = getColorStateList(R.color.angerColor)
                        "Disgust" -> chip.chipBackgroundColor = getColorStateList(R.color.disgustColor)
                    }
                    // Reset color if not checked
                    if (!isChecked) {
                        chip.chipBackgroundColor = getColorStateList(R.color.defaultChipColor)
                    }
                    // 텍스트 색상 고정
                    chip.setTextColor(getColor(R.color.white))  // 선택 상태에서도 텍스트 색상 유지
                    // Update the state of the next button based on chip selection
                    updateNextButtonState(chipGroup.checkedChipIds.size)
                }
                chipGroup.addView(chip)
            }
        }

        // Disable and hide the helpButton
        myRecordFragment.setHelpButtonEnabled(false)
        myRecordFragment.setHelpButtonVisibility(false)
    }

    // Update the state of the next button
    private fun updateNextButtonState(selectedChipCount: Int) {
        myRecordFragment.setButtonNextEnabled(selectedChipCount in 1..3)
    }

    override fun onNextButtonClicked() {
        // Handle next button click (proceed to the next activity or step)
        Toast.makeText(this, "Next button clicked", Toast.LENGTH_SHORT).show()
    }

    override fun onBackButtonClicked() {
        // Handle back button click (return to the MainActivity)
        val intent = Intent(this, EmotionStrengthActivity::class.java)
        startActivity(intent)
        finish() // Finish the current activity
    }

}
