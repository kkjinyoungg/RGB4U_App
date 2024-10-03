package com.example.rgb4u_app.ui.activity.mypage

import android.content.Intent
import android.os.Bundle
import android.view.View
import android.widget.ImageButton
import android.widget.LinearLayout
import android.widget.TextView
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import com.example.rgb4u_app.R
import com.example.rgb4u_app.ui.activity.login.LoginActivity
import com.example.rgb4u_app.ui.fragment.ConfirmationDialogFragment
import com.google.android.material.switchmaterial.SwitchMaterial
//파이어베이스
import com.google.firebase.database.DatabaseReference
import com.google.firebase.database.FirebaseDatabase
import com.google.firebase.database.DataSnapshot
import android.util.Log
import com.google.firebase.database.DatabaseError
import com.google.firebase.database.ValueEventListener

class MyPageMainActivity : AppCompatActivity() {

    private lateinit var switchPassword: SwitchMaterial
    private lateinit var changePasswordLayout: LinearLayout
    private lateinit var tvChangePassword: TextView
    private lateinit var btnNotificationDetails: ImageButton
    private lateinit var btnTermsOfServiceDetails: ImageButton
    private lateinit var btnEditProfile: ImageButton
    private lateinit var tvLogout: TextView
    private lateinit var tvDeleteAccount: TextView
    private lateinit var btnHowToUseDetails: ImageButton
    private lateinit var database: DatabaseReference
    private lateinit var tvnickname: TextView //마이페이지 이름
    private val userId = "userId" // 실제 사용자 ID로 변경


    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_my_page_main)

        // Firebase Database 초기화
        database = FirebaseDatabase.getInstance().reference

        // 사용자 프로필 로드
        loadUserProfile()  // 기존 사용자 정보 로드

        // 비밀번호 설정 토글과 비밀번호 변경 메뉴
        switchPassword = findViewById(R.id.switch_password)
        changePasswordLayout = findViewById(R.id.layout_change_password)
        tvChangePassword = findViewById(R.id.tv_change_password)
        tvLogout = findViewById(R.id.tv_logout)
        tvDeleteAccount = findViewById(R.id.tv_delete_account)
        btnHowToUseDetails = findViewById(R.id.btn_how_to_use_details)
        tvnickname = findViewById(R.id.tv_nickname) //마이페이지 이름

        // 버튼 초기화
        btnNotificationDetails = findViewById(R.id.btn_notification_details)
        btnTermsOfServiceDetails = findViewById(R.id.btn_terms_of_service_details)
        btnEditProfile = findViewById(R.id.btn_edit_profile)

        // SharedPreferences에서 토글 상태 확인
        val sharedPreferences = getSharedPreferences("MyPrefs", MODE_PRIVATE)

        // SharedPreferences에서 저장된 값이 있으면 불러오고, 없으면 false로 기본값 설정 (스위치를 끈 상태로 시작)
        val switchState = sharedPreferences.getBoolean("switchPassword", false)

        // 불러온 값을 바탕으로 스위치 초기 상태 설정 (false일 경우 스위치가 OFF)
        switchPassword.isChecked = switchState

        // 비밀번호 변경 레이아웃 상태 설정
        changePasswordLayout.visibility = if (switchState) View.VISIBLE else View.GONE

        // 스위치 상태 변경 리스너 설정
        switchPassword.setOnCheckedChangeListener { _, isChecked ->
            if (isChecked) {
                // 비밀번호 설정 화면으로 이동
                val intent = Intent(this, MyPagePasswordSettingActivity::class.java)
                intent.putExtra("switchPasswordState", true) // 현재 상태 전달
                startActivity(intent)
            } else {
                // 스위치가 꺼진 상태일 경우 비밀번호 변경 레이아웃 숨김
                changePasswordLayout.visibility = View.GONE

                // 스위치 상태를 SharedPreferences에 저장 (OFF 상태 저장)
                with(sharedPreferences.edit()) {
                    putBoolean("switchPassword", false)
                    apply()
                }
            }
        }

        // 이전 액티비티에서 전달받은 인텐트 데이터 처리
        val isPasswordSet = intent.getBooleanExtra("passwordSet", false)

        if (isPasswordSet) {
            // 비밀번호가 설정되었을 경우에만 스위치를 켬
            switchPassword.isChecked = true
            changePasswordLayout.visibility = View.VISIBLE
        }

        val btnChangePassword: ImageButton = findViewById(R.id.btn_change_password)
        btnChangePassword.setOnClickListener {
            val intent = Intent(this, MyPagePasswordEditActivity::class.java)
            startActivity(intent)
        }

        // 버튼 클릭 리스너 설정
        btnNotificationDetails.setOnClickListener {
            val intent = Intent(this, MyPageNotificationSettingsActivity::class.java)
            startActivity(intent)
        }

        btnTermsOfServiceDetails.setOnClickListener {
            val intent = Intent(this, MyPageServiceCheck::class.java)
            startActivity(intent)
        }

        btnEditProfile.setOnClickListener {
            val intent = Intent(this, MyPageProfileEditActivity::class.java)
            startActivity(intent)
        }

        // 로그아웃 클릭 리스너
        tvLogout.setOnClickListener {
            val dialog = ConfirmationDialogFragment(
                title = "로그아웃하시겠습니까?",
                message = "", // 메시지를 빈 문자열로 설정하여 보이지 않게 함
                confirmButtonText = "로그아웃",
                onConfirm = {
                    val intent = Intent(this, LoginActivity::class.java)
                    startActivity(intent)
                    finish()  // 로그아웃 동작
                }
            )
            dialog.show(supportFragmentManager, "logoutConfirmation")
        }

        // 회원 탈퇴 클릭 리스너 추가
        tvDeleteAccount.setOnClickListener {
            val dialog = ConfirmationDialogFragment(
                title = "회원 탈퇴하시겠습니까?",
                message = "(앱 이름)을 탈퇴하면 기록과 분석 결과 등 모든 정보가 즉시 삭제되고 복구가 불가능합니다. \n계속하시겠습니까?",
                confirmButtonText = "탈퇴",
                onConfirm = {
                    // 회원 탈퇴 처리 로직
                    // 예: Firebase에서 사용자 계정 삭제 등
                    Toast.makeText(this, "회원 탈퇴가 완료되었습니다.", Toast.LENGTH_SHORT).show()

                    // MyPageDeleteaccoutActivity로 이동
                    val intent = Intent(this, MyPageDeleteaccoutActivity::class.java)
                    startActivity(intent)
                    finish() // 현재 액티비티 종료
                }
            )
            dialog.show(supportFragmentManager, "deleteAccountConfirmation")
        }

        // 도움말 클릭 리스너
        btnHowToUseDetails.setOnClickListener {
            val intent = Intent(this, MyPageHowToUseActivity::class.java)
            startActivity(intent)
        }
    }



    override fun onResume() {
        super.onResume()

        // SharedPreferences에서 토글 상태 확인
        val sharedPreferences = getSharedPreferences("MyPrefs", MODE_PRIVATE)
        val isSwitchPasswordOn = sharedPreferences.getBoolean("switchPassword", false)

        // 토글 버튼 상태 유지
        switchPassword.isChecked = isSwitchPasswordOn

        // 토글 상태에 따른 비밀번호 변경 레이아웃 표시/숨기기
        if (isSwitchPasswordOn) {
            changePasswordLayout.visibility = View.VISIBLE
        } else {
            changePasswordLayout.visibility = View.GONE
        }
    }

    private fun loadUserProfile() {
        // Firebase에서 사용자 정보를 읽어오기
        database.child("users").child(userId).addListenerForSingleValueEvent(object :
            ValueEventListener {
            override fun onDataChange(dataSnapshot: DataSnapshot) {
                val nickname = dataSnapshot.child("nickname").getValue(String::class.java)
                // EditText에 값 설정
                tvnickname.setText(nickname)
            }
            override fun onCancelled(databaseError: DatabaseError) {
                // 오류 처리
                Log.e("MyPageMainActivity", "마이페이지 메인 닉네임을 파이어베이스에서 불러오는 데 실패했습니다: ${databaseError.message}")            }
        })
    }
}
