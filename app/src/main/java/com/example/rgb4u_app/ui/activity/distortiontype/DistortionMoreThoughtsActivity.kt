package com.example.rgb4u_app.ui.activity.distortiontype

import android.os.Bundle
import android.view.View
import android.widget.ImageButton
import android.widget.ImageView
import android.widget.TextView
import androidx.activity.enableEdgeToEdge
import androidx.appcompat.app.AppCompatActivity
import com.example.rgb4u_app.R

class DistortionMoreThoughtsActivity : AppCompatActivity() {

    private lateinit var tvTypeDetailTitle2: TextView
    private lateinit var tvTypeDetail2: TextView
    private lateinit var tvToggleDetail2: TextView
    private lateinit var btnToggleDetail2: ImageButton
    private lateinit var tvTypeDetailExtended2: TextView
    private lateinit var tvAlternativeThoughtHeader2: TextView
    private lateinit var tvAlternativeThought2: TextView
    private lateinit var tvAlternativeDetailToggle2: TextView
    private lateinit var btnAlternativeDetailToggle2: ImageButton
    private lateinit var tvAlternativeDetailExtended2: TextView

    private lateinit var tvTypeDetailTitle3: TextView
    private lateinit var tvTypeDetail3: TextView
    private lateinit var tvToggleDetail3: TextView
    private lateinit var btnToggleDetail3: ImageButton
    private lateinit var tvTypeDetailExtended3: TextView
    private lateinit var tvAlternativeThoughtHeader3: TextView
    private lateinit var ivCharacterFace3: ImageView
    private lateinit var tvAlternativeThought3: TextView
    private lateinit var tvAlternativeDetailToggle3: TextView
    private lateinit var btnAlternativeDetailToggle3: ImageButton
    private lateinit var tvAlternativeDetailExtended3: TextView

    private lateinit var distortionType: DistortionType

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        setContentView(R.layout.activity_distortion_more_thoughts)

        // 툴바의 뒤로가기 버튼 설정 및 타이틀, 불필요한 버튼 숨김
        val toolbar = findViewById<View>(R.id.toolbar_write_diary)
        val backButton = toolbar.findViewById<ImageButton>(R.id.button_write_action1)
        val titleTextView = toolbar.findViewById<TextView>(R.id.toolbar_write_title)
        val actionButton2 = toolbar.findViewById<ImageButton>(R.id.button_write_action2)

        // 타이틀과 button_write_action2 숨기기
        titleTextView.visibility = View.GONE
        actionButton2.visibility = View.GONE

        // 뒤로가기 버튼 기능 추가
        backButton.setOnClickListener {
            finish() // 현재 액티비티 종료, 이전 액티비티로 돌아감
        }

        // 전달된 유형 데이터 받기
        distortionType = intent.getParcelableExtra("distortionType") ?: throw IllegalArgumentException("DistortionType is required")

        // 뷰 초기화
        // 첫 번째 섹션의 뷰 초기화
        tvTypeDetailTitle2 = findViewById(R.id.tv_type_detail_title_2)
        tvTypeDetail2 = findViewById(R.id.tv_type_detail_2)
        tvToggleDetail2 = findViewById(R.id.tv_toggle_detail_2)
        btnToggleDetail2 = findViewById(R.id.btn_toggle_detail_2)
        tvTypeDetailExtended2 = findViewById(R.id.tv_type_detail_extended_2)
        tvAlternativeThoughtHeader2 = findViewById(R.id.tv_alternative_thought_header_2)
        tvAlternativeThought2 = findViewById(R.id.tv_alternative_thought_2)
        tvAlternativeDetailToggle2 = findViewById(R.id.tv_alternative_detail_toggle_2)
        btnAlternativeDetailToggle2 = findViewById(R.id.btn_alternative_detail_toggle_2)
        tvAlternativeDetailExtended2 = findViewById(R.id.tv_alternative_detail_extended_2)

        // 두 번째 섹션의 요소들 초기화
        tvTypeDetailTitle3 = findViewById(R.id.tv_type_detail_title_3)
        tvTypeDetail3 = findViewById(R.id.tv_type_detail_3)
        tvToggleDetail3 = findViewById(R.id.tv_toggle_detail_3)
        btnToggleDetail3 = findViewById(R.id.btn_toggle_detail_3)
        tvTypeDetailExtended3 = findViewById(R.id.tv_type_detail_extended_3)
        tvAlternativeThoughtHeader3 = findViewById(R.id.tv_alternative_thought_header_3)
        ivCharacterFace3 = findViewById(R.id.iv_character_face_3)
        tvAlternativeThought3 = findViewById(R.id.tv_alternative_thought_3)
        tvAlternativeDetailToggle3 = findViewById(R.id.tv_alternative_detail_toggle_3)
        btnAlternativeDetailToggle3 = findViewById(R.id.btn_alternative_detail_toggle_3)
        tvAlternativeDetailExtended3 = findViewById(R.id.tv_alternative_detail_extended_3)

        // 두 번째 섹션의 데이터가 없을 경우 숨김 처리
        if (distortionType.alternativeThought.isNullOrEmpty() &&
            distortionType.detail3.isNullOrEmpty() &&
            distortionType.extendedDetail3.isNullOrEmpty() &&
            distortionType.alternativeThought3.isNullOrEmpty()) {

            // 두 번째 섹션의 모든 뷰를 숨깁니다.
            tvTypeDetailTitle3.visibility = View.GONE
            tvTypeDetail3.visibility = View.GONE
            tvToggleDetail3.visibility = View.GONE
            btnToggleDetail3.visibility = View.GONE
            tvTypeDetailExtended3.visibility = View.GONE
            tvAlternativeThoughtHeader3.visibility = View.GONE
            ivCharacterFace3.visibility = View.GONE
            tvAlternativeThought3.visibility = View.GONE
            tvAlternativeDetailToggle3.visibility = View.GONE
            btnAlternativeDetailToggle3.visibility = View.GONE
            tvAlternativeDetailExtended3.visibility = View.GONE
        } else {
            // 데이터가 있는 경우 UI 요소에 데이터 설정
            tvTypeDetailTitle3.text = distortionType.detailTitle // 제목 설정
            tvTypeDetail3.text = distortionType.detail3 // 상세 내용 설정
            tvTypeDetailExtended3.text = distortionType.extendedDetail3 // 추가 상세 내용 설정
            tvAlternativeThought3.text = distortionType.alternativeThought3 // 대안적 생각 설정
            tvAlternativeDetailExtended3.text = distortionType.alternativeExtendedDetail3 // 대안적 추가 상세 설정
        }

        // 첫 번째 섹션 데이터 설정
        setupFirstSection()

        // 두 번째 섹션 데이터 설정
        setupSecondSection()

        // 첫 번째 '자세히 보기' 토글 버튼 동작 설정
        setToggleFunctionality(tvToggleDetail2, btnToggleDetail2, tvTypeDetailExtended2)
        setToggleFunctionality(tvAlternativeDetailToggle2, btnAlternativeDetailToggle2, tvAlternativeDetailExtended2)

        // 두 번째 '자세히 보기' 토글 버튼 동작 설정
        setToggleFunctionality(tvToggleDetail3, btnToggleDetail3, tvTypeDetailExtended3)
        setToggleFunctionality(tvAlternativeDetailToggle3, btnAlternativeDetailToggle3, tvAlternativeDetailExtended3)
    }

    private fun setupFirstSection() {
        tvTypeDetailTitle2.text = distortionType.detailTitle
        tvTypeDetail2.text = distortionType.detail2
        tvTypeDetailExtended2.text = distortionType.extendedDetail2

        tvAlternativeThought2.text = distortionType.alternativeThought2
        tvAlternativeDetailExtended2.text = distortionType.alternativeExtendedDetail2
    }

    private fun setupSecondSection() {
        tvTypeDetailTitle3.text = distortionType.detailTitle
        tvTypeDetail3.text = distortionType.detail3
        tvTypeDetailExtended3.text = distortionType.extendedDetail3

        tvAlternativeThought3.text = distortionType.alternativeThought3
        tvAlternativeDetailExtended3.text = distortionType.alternativeExtendedDetail3
    }

    private fun setToggleFunctionality(
        textView: TextView,
        button: ImageButton,
        hiddenText: TextView
    ) {
        val toggleListener = View.OnClickListener {
            val isVisible = hiddenText.visibility == View.VISIBLE
            hiddenText.visibility = if (isVisible) View.GONE else View.VISIBLE
            button.setImageResource(if (isVisible) R.drawable.ic_toggle_down else R.drawable.ic_toggle_up)
            textView.text = if (isVisible) "자세히 보기" else "숨기기"
        }
        textView.setOnClickListener(toggleListener)
        button.setOnClickListener(toggleListener)
    }
}
