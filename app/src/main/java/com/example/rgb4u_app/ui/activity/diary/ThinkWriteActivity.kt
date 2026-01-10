package com.example.rgb4u.ver1_app.ui.activity.diary

import android.content.Intent
import android.os.Bundle
import android.text.Editable
import android.text.TextWatcher
import android.util.Log
import android.view.View
import android.widget.Button
import android.widget.EditText
import android.widget.LinearLayout
import android.widget.TextView
import android.widget.Toast
import androidx.activity.viewModels
import androidx.appcompat.app.AppCompatActivity
import androidx.core.content.ContextCompat
import com.example.rgb4u.ver1_app.MyApplication
import com.example.rgb4u.ver1_app.R
import com.example.rgb4u.ver1_app.activity.ActivityType
import com.example.rgb4u.ver1_app.ui.activity.home.MainActivity
import com.example.rgb4u.ver1_app.ui.fragment.HelpBottomSheetFragment
import com.example.rgb4u.ver1_app.ui.fragment.HelpBottomSheetViewModel
import com.example.rgb4u.ver1_app.ui.fragment.MyRecordFragment
import com.example.rgb4u.ver1_app.ui.fragment.TemporarySaveDialogFragment
import com.example.rgb4u.ver1_appclass.DiaryViewModel
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.database.FirebaseDatabase

class ThinkWriteActivity : AppCompatActivity(), MyRecordFragment.NavigationListener {

    private lateinit var myRecordFragment: MyRecordFragment
    private lateinit var diaryViewModel: DiaryViewModel // ViewModel 선언
    private val helpViewModel: HelpBottomSheetViewModel by viewModels() // ViewModel 선언
    private lateinit var toolbarTitle: TextView  // 툴바 제목 텍스트뷰

    private var dbDate: String = ""  // nullable String을 non-null String으로 수정

    // 현재 로그인된 사용자의 UID를 가져오는 함수
    private val userId: String?
        get() = FirebaseAuth.getInstance().currentUser?.uid

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_think_write)

        toolbarTitle = findViewById(R.id.toolbar_write_title)
        // Intent로 전달된 toolbarTitle 텍스트 값을 가져옴
        val titleText = intent.getStringExtra("TOOLBAR_TITLE")
        titleText?.let {
            toolbarTitle.text = it // 툴바 제목 텍스트에 설정
        }
        dbDate = intent.getStringExtra("DBDATE") ?: ""

        // Application에서 ViewModel 가져오기
        diaryViewModel = (application as MyApplication).diaryViewModel

        // ViewModel 관찰자 추가
        diaryViewModel.thoughts.observe(this) { savedThoughts ->
            if (savedThoughts != null && savedThoughts.isNotEmpty()) {
                findViewById<EditText>(R.id.inputField).setText(savedThoughts)
            }
        }

        // 프래그먼트 초기화
        myRecordFragment =
            supportFragmentManager.findFragmentById(R.id.myrecordFragment) as MyRecordFragment

        // 버튼 클릭 리스너 설정
        myRecordFragment.setToolbarButtonListeners(
            backAction = { onToolbarAction1Clicked() }, // 뒤로 가기 버튼 동작을 메서드로 연결
            exitAction = { onToolbarAction2Clicked() } // 나가기 버튼 동작
        )

        // 프레그먼트에서 요구하는 인터페이스 이미지 구현
        myRecordFragment.setImage(R.drawable.img_ch_think)

        // 프래그먼트에서 요구하는 인터페이스 구현 확인
        myRecordFragment.setQuestionText("그때 떠오른 생각을 들려주세요", "그 상황에서 어떤 내용이 머릿속을 스쳐갔나요?")

        // 특정 단계의 이미지만 보이도록 설정 (예: 2단계 "생각 적기")
        myRecordFragment.showIconForStep(2)

        // 2단계에서 폰트 스타일을 바꾸는 부분
        changeFontStyleForStep(2)

        // Help 버튼 클릭 리스너 추가
        findViewById<LinearLayout>(R.id.think_helpButton).setOnClickListener {
            showHelpBottomSheet()
        }

        // 텍스트 필드와 글자 수 카운터 초기화
        val inputField = findViewById<EditText>(R.id.inputField)
        val charCountTextView = findViewById<TextView>(R.id.charCountTextView)
        val buttonNext = myRecordFragment.view?.findViewById<Button>(R.id.buttonNext)

        // 텍스트 필드 포커스 변경에 따른 테두리 변경
        inputField.onFocusChangeListener = View.OnFocusChangeListener { _, hasFocus ->
            if (hasFocus) {
                // 포커스가 있을 때 테두리 추가
                inputField.setBackgroundResource(R.drawable.edittext_border)
            } else {
                // 포커스가 없을 때 테두리 제거
                inputField.setBackgroundResource(R.drawable.edittext_no_border)
            }
        }


        // 초기 상태로 버튼 비활성화
        buttonNext?.isEnabled = false

        // 텍스트 필드 변화에 따라 버튼 활성화/비활성화 및 글자 수 업데이트
        inputField.addTextChangedListener(object : TextWatcher {
            override fun afterTextChanged(s: Editable?) {
                s?.let {
                    val charCount = s.length
                    val byteCount = s.toString().toByteArray(Charsets.UTF_8).size

                    // 텍스트가 300바이트를 초과할 경우
                    if (byteCount > 300) {
                        inputField.setText(s.toString().take(s.length - 1)) // 마지막 문자 제거
                        inputField.setSelection(inputField.text.length) // 커서 위치를 끝으로 이동
                        Toast.makeText(
                            this@ThinkWriteActivity,
                            "150자 이하로 작성해주세요",
                            Toast.LENGTH_SHORT
                        ).show()
                    }

                    // 글자 수가 1자 이상일 때만 버튼 활성화
                    val isTextValid = charCount in 1..150
                    buttonNext?.isEnabled = isTextValid

                    // 버튼은 항상 보이도록 수정
                    myRecordFragment.setButtonNextVisibility(true) // 항상 true로 설정

                    // 글자 수 업데이트
                    charCountTextView.text = "$charCount/150"

                    // 글자 수가 300바이트(150)자를 초과할 경우 색상 변경
                    if (byteCount > 300) {
                        charCountTextView.setTextColor(ContextCompat.getColor(this@ThinkWriteActivity, R.color.highlight_dark)) // 빨간색
                    } else {
                        charCountTextView.setTextColor(ContextCompat.getColor(this@ThinkWriteActivity, R.color.gray3)) // 기본 색상
                    }
                }
            }

            override fun beforeTextChanged(s: CharSequence?, start: Int, count: Int, after: Int) {}
            override fun onTextChanged(s: CharSequence?, start: Int, before: Int, count: Int) {}
        })

        // 버튼 클릭 시 onNextButtonClicked 호출
        buttonNext?.setOnClickListener {
            onNextButtonClicked()
        }
    }

    // 특정 단계에서 폰트 스타일을 변경하는 함수
    private fun changeFontStyleForStep(step: Int) {
        val myTextView: TextView = findViewById(R.id.text_thought)
        if (step == 2) {
            // 2단계에서 폰트 스타일을 Bold로 변경
            myTextView.setTextAppearance(R.style.Cp1_Bold)
            myTextView.setTextColor(ContextCompat.getColor(this, R.color.white_40))
        } else {
            // 기본 스타일로 변경
            myTextView.setTextAppearance(R.style.Cp1_Regular)
            myTextView.setTextColor(ContextCompat.getColor(this, R.color.white_20))
        }
    }

    override fun onNextButtonClicked() {
        val inputText = findViewById<EditText>(R.id.inputField).text.toString()
        // ViewModel에 입력된 생각 텍스트 저장
        diaryViewModel.thoughts.postValue(inputText)

        // EmotionStrengthActivity로 데이터를 전달하면서 이동
        val intent = Intent(this, EmotionStrengthActivity::class.java)
        intent.putExtra("TOOLBAR_TITLE", toolbarTitle.text.toString()) // toolbarTitle.text 값을 전달
        intent.putExtra("DBDATE", dbDate) // yyyy-mm-dd
        startActivity(intent)
    }

    override fun onToolbarAction1Clicked() {
        // "Back" 버튼 클릭 시 DiaryWriteActivity로 이동
        val inputText = findViewById<EditText>(R.id.inputField).text.toString()
        diaryViewModel.thoughts.setValue(inputText)
        val intent = Intent(this, DiaryWriteActivity::class.java)
        startActivity(intent)
        finish()
    }

    override fun onToolbarAction2Clicked() {
        // TemporarySaveDialogFragment 인스턴스 생성
        val dialog = TemporarySaveDialogFragment()

        // dialog의 리스너 설정
        dialog.listener = object : TemporarySaveDialogFragment.OnButtonClickListener {
            override fun onTemporarySave() {
                // 현재 입력 필드의 내용을 ViewModel에 저장하고 MainActivity로 이동
                val inputText = findViewById<EditText>(R.id.inputField).text.toString()
                diaryViewModel.thoughts.setValue(inputText)
                diaryViewModel.saveTemporaryDiaryToFirebase(userId ?: "defaultUserId") //NULL 처리 다시 고민하기
                val intent = Intent(this@ThinkWriteActivity, MainActivity::class.java)
                startActivity(intent)
                finish()
            }

            override fun onDelete() {
                // 현재 로그인된 사용자의 UID 가져오기
                val currentUserId = userId
                if (currentUserId != null) {
                    // Realtime Database의 해당 경로 참조
                    val databaseRef = FirebaseDatabase.getInstance().reference
                    val diaryRef = databaseRef.child("users").child(currentUserId).child("diaries").child(dbDate)

                    // 해당 경로의 데이터를 삭제
                    diaryRef.removeValue()
                        .addOnSuccessListener {
                            // 삭제 성공 시 처리할 코드
                            Log.d("ThinkWriteActivity", "Diary deleted successfully.")
                            val intent = Intent(this@ThinkWriteActivity, MainActivity::class.java)
                            startActivity(intent)
                            finish()
                        }
                        .addOnFailureListener { exception: Exception ->
                            // 삭제 실패 시 처리할 코드
                            Log.e("ThinkWriteActivity", "Failed to delete diary: ${exception.message}")
                        }
                } else {
                    Log.e("ThinkWriteActivity", "User ID is null, cannot delete diary.")
                }
            }
        }

        // 팝업 표시
        dialog.show(supportFragmentManager, "TemporarySaveDialog")
    }

    private fun showHelpBottomSheet() {
        val helpBottomSheetFragment = HelpBottomSheetFragment()
        helpViewModel.setSituations(ActivityType.ACTIVITY_THINK) // 상황 리스트 설정
        helpBottomSheetFragment.setSituations(helpViewModel.situations.value ?: emptyList()) // 상황 리스트 전달
        helpBottomSheetFragment.show(supportFragmentManager, helpBottomSheetFragment.tag)
    }
}
