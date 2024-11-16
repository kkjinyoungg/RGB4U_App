package com.example.rgb4u_app.ui.activity.diary

import android.content.Intent
import android.os.Bundle
import android.text.Editable
import android.text.TextWatcher
import android.widget.Button
import android.widget.EditText
import android.widget.TextView
import android.widget.Toast
import androidx.activity.viewModels
import androidx.appcompat.app.AppCompatActivity
import com.example.rgb4u_app.MyApplication
import com.example.rgb4u_app.R
import com.example.rgb4u_app.activity.ActivityType
import com.example.rgb4u_app.ui.activity.home.MainActivity
import com.example.rgb4u_app.ui.fragment.HelpBottomSheetFragment
import com.example.rgb4u_app.ui.fragment.HelpBottomSheetViewModel
import com.example.rgb4u_app.ui.fragment.MyRecordFragment
import com.example.rgb4u_app.ui.fragment.TemporarySaveDialogFragment
import com.example.rgb4u_appclass.DiaryViewModel
import com.google.firebase.auth.FirebaseAuth
import android.util.Log
import android.view.View

class DiaryWriteActivity : AppCompatActivity(), MyRecordFragment.NavigationListener {

    private lateinit var myRecordFragment: MyRecordFragment
    private lateinit var diaryViewModel: DiaryViewModel // ViewModel 선언
    private val helpViewModel: HelpBottomSheetViewModel by viewModels() // ViewModel 선언

    // 현재 로그인된 사용자의 UID를 가져오는 함수
    private val userId: String?
        get() = FirebaseAuth.getInstance().currentUser?.uid

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_write_diary)

        // Application에서 ViewModel 가져오기
        diaryViewModel = (application as MyApplication).diaryViewModel

        // ViewModel 관찰자 추가
        diaryViewModel.situation.observe(this) { savedSituation ->
            if (savedSituation != null && savedSituation.isNotEmpty()) {
                findViewById<EditText>(R.id.inputField).setText(savedSituation)
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
        myRecordFragment.setImage(R.drawable.img_ch_situation)

        // 프래그먼트에서 요구하는 인터페이스 구현 확인
        myRecordFragment.setQuestionText("어떤 상황이 있었는지 들려주세요", "오늘 힘들었던 순간이 있었나요?")

        // 특정 단계의 이미지만 보이도록 설정 (예: 2단계 "생각 적기")
        myRecordFragment.showIconForStep(1)

        // Help 버튼 클릭 리스너 추가
        findViewById<Button>(R.id.diary_helpButton).setOnClickListener {
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
                            this@DiaryWriteActivity,
                            "150자 이하로 작성해주세요",
                            Toast.LENGTH_SHORT
                        ).show()
                    }

                    // 글자 수가 1자 이상일 때만 버튼 활성화
                    val isTextValid = charCount in 1..150
                    buttonNext?.isEnabled = isTextValid
                    myRecordFragment.setButtonNextEnabled(isTextValid)
                    myRecordFragment.setButtonNextVisibility(isTextValid)

                    // 글자 수 업데이트
                    charCountTextView.text = "$charCount/150"
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

    override fun onNextButtonClicked() {
        val inputText = findViewById<EditText>(R.id.inputField).text.toString()

        // ViewModel에 입력된 상황 텍스트 저장
        diaryViewModel.situation.postValue(inputText)
        //diaryViewModel.situation.value = inputText


        // ThinkWriteActivity로 데이터를 전달하면서 이동
        val intent = Intent(this, ThinkWriteActivity::class.java)
        //intent.putExtra("EXTRA_SITUATION_TEXT", inputText)  // situationText로 전달할 데이터
        startActivity(intent)
    }

    override fun onToolbarAction1Clicked() {
        // TemporarySaveDialogFragment 인스턴스 생성
        val dialog = TemporarySaveDialogFragment()

        // dialog의 리스너 설정
        dialog.listener = object : TemporarySaveDialogFragment.OnButtonClickListener {
            override fun onTemporarySave() {
                // 현재 입력 필드의 내용을 ViewModel에 저장하고 MainActivity로 이동
                val inputText = findViewById<EditText>(R.id.inputField).text.toString()
                diaryViewModel.situation.postValue(inputText)
                diaryViewModel.saveDiaryToFirebase(userId ?: "defaultUserId") //NULL 처리 다시 고민하기

                val intent = Intent(this@DiaryWriteActivity, MainActivity::class.java)
                startActivity(intent)
                finish()
            }

            override fun onDelete() {
                // 삭제 동작: 아무것도 저장하지 않고 MainActivity로 이동
                val intent = Intent(this@DiaryWriteActivity, MainActivity::class.java)
                startActivity(intent)
                finish()
            }
        }

        // 팝업 표시
        dialog.show(supportFragmentManager, "TemporarySaveDialog")
    }

    override fun onToolbarAction2Clicked() {
        // TemporarySaveDialogFragment 인스턴스 생성
        val dialog = TemporarySaveDialogFragment()

        // dialog의 리스너 설정
        dialog.listener = object : TemporarySaveDialogFragment.OnButtonClickListener {
            override fun onTemporarySave() {
                // 현재 입력 필드의 내용을 ViewModel에 저장하고 MainActivity로 이동
                val inputText = findViewById<EditText>(R.id.inputField).text.toString()
                diaryViewModel.situation.postValue(inputText)
                Log.d("DiaryWriteActivity", "Saving input text: $inputText") // 로그 추가

                diaryViewModel.saveDiaryToFirebase(userId ?: "defaultUserId") //NULL 처리 다시 고민하기
                Log.d("DiaryWriteActivity", "User ID: ${userId ?: "defaultUserId"}") // 로그 추

                val intent = Intent(this@DiaryWriteActivity, MainActivity::class.java)
                startActivity(intent)
                finish()
            }

            override fun onDelete() {
                // 삭제 동작: 아무것도 저장하지 않고 MainActivity로 이동
                val intent = Intent(this@DiaryWriteActivity, MainActivity::class.java)
                startActivity(intent)
                finish()
            }
        }

        // 팝업 표시
        dialog.show(supportFragmentManager, "TemporarySaveDialog")
    }


    private fun showHelpBottomSheet() {
        val helpBottomSheetFragment = HelpBottomSheetFragment()
        helpViewModel.setSituations(ActivityType.ACTIVITY_DIARY) // 상황 리스트 설정
        helpBottomSheetFragment.setSituations(helpViewModel.situations.value ?: emptyList()) // 상황 리스트 전달
        helpBottomSheetFragment.show(supportFragmentManager, helpBottomSheetFragment.tag)
    }

}
