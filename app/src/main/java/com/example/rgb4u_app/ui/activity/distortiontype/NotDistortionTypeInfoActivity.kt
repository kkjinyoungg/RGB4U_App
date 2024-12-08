package com.example.rgb4u_app.ui.activity.distortiontype

import android.content.Intent
import android.os.Build
import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.widget.Button
import android.widget.ImageButton
import android.widget.TextView
import androidx.activity.enableEdgeToEdge
import androidx.appcompat.app.AppCompatActivity
import androidx.appcompat.widget.Toolbar
import com.example.rgb4u_app.R
import com.example.rgb4u_app.ui.activity.home.MainActivity
import com.example.rgb4u_app.ui.fragment.DistortionHelpBottomSheet
import com.google.android.material.bottomsheet.BottomSheetDialog
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.database.FirebaseDatabase

class NotDistortionTypeInfoActivity : AppCompatActivity() {

    private lateinit var userId: String

    private lateinit var toolbarDate: String // lateinit으로 선언
    private lateinit var date: String // lateinit으로 선언


    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        setContentView(R.layout.activity_not_distortion_type_info)

        // 상태바 투명
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {
            window.apply {
                decorView.systemUiVisibility =
                    View.SYSTEM_UI_FLAG_LAYOUT_FULLSCREEN or View.SYSTEM_UI_FLAG_LAYOUT_STABLE
                statusBarColor = android.graphics.Color.TRANSPARENT
            }
        }

        userId = FirebaseAuth.getInstance().currentUser?.uid ?: ""

        // Intent에서 툴바랑 date 가져오기
        date = intent.getStringExtra("Date") ?: ""
        toolbarDate = intent.getStringExtra("Toolbar") ?: ""

        // 로그 출력
        Log.d("DistortionTypeActivity", "Received User ID: $userId")
        Log.d("DistortionTypeActivity", "Received Diary ID: $date")
        Log.d("DistortionTypeActivity", "Received Toolbar Date: $toolbarDate")

        // Firebase에서 readingStatus 업데이트
        updateReadingStatus(userId, date)

        val toolbar: Toolbar = findViewById(R.id.toolbar_write_diary)
        setSupportActionBar(toolbar)

        val helpButton = toolbar.findViewById<ImageButton>(R.id.button_write_action2)
        helpButton.setImageResource(R.drawable.ic_distortion_help_btn)

        // ActionBar 설정
        supportActionBar?.setDisplayHomeAsUpEnabled(false)
        supportActionBar?.setDisplayShowTitleEnabled(false)

        toolbar.findViewById<TextView>(R.id.toolbar_write_title).text = toolbarDate // 툴바 타이틀 설정 (toolbar_write_title)

        // 클릭 이벤트 설정
        helpButton.setOnClickListener {
            Log.d("DistortionTypeActivity", "Help button clicked")
            showDistortionHelpBottomSheet()
        }

        toolbar.findViewById<View>(R.id.button_write_action1).setOnClickListener {
            Log.d("DistortionTypeActivity", "Summary button clicked")
            val intent = Intent(this, MainActivity::class.java)
            startActivity(intent)
        }

        // 확인 버튼 클릭 리스너
        val buttonNext: Button = findViewById(R.id.buttonNext)
        buttonNext.setOnClickListener {
            showBottomSheet() // 바텀시트 열기
        }
    }

    private fun updateReadingStatus(userId: String, date: String) {
        val database = FirebaseDatabase.getInstance()
            .getReference("users/$userId/diaries/$date/readingstatus")

        database.setValue("read").addOnSuccessListener {
            Log.d("DistortionTypeActivity", "readingStatus 업데이트 성공: 빈 문자열로 설정됨")
        }.addOnFailureListener { exception ->
            Log.e("DistortionTypeActivity", "readingStatus 업데이트 실패", exception)
        }
    }

    private fun showDistortionHelpBottomSheet() {
        val bottomSheet = DistortionHelpBottomSheet()
        bottomSheet.show(supportFragmentManager, "DistortionHelpBottomSheet")
    }

    private fun showBottomSheet() {
        // 바텀시트 레이아웃 inflate
        val bottomSheetView = LayoutInflater.from(this).inflate(R.layout.fragment_summarychanged_bottom_sheet, null)
        val bottomSheetDialog = BottomSheetDialog(this)
        bottomSheetDialog.setContentView(bottomSheetView)

        // 확인 버튼 클릭 이벤트
        val confirmButton: Button = bottomSheetView.findViewById(R.id.btn_confirm_changed)
        confirmButton.setOnClickListener {
            bottomSheetDialog.dismiss() // 바텀시트 닫기

            // MainActivity로 이동
            val intent = Intent(this, MainActivity::class.java)
            startActivity(intent)
            finish() // 현재 액티비티 종료
        }

        bottomSheetDialog.show() // 바텀시트 표시
    }
}