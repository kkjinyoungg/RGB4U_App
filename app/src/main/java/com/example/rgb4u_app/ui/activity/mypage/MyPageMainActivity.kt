package com.example.rgb4u_app.ui.activity.mypage

import android.content.Intent
import android.os.Bundle
import android.util.Log
import android.view.View
import android.widget.ImageButton
import android.widget.LinearLayout
import android.widget.TextView
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import com.example.rgb4u_app.R
import com.example.rgb4u_app.ui.activity.login.LoginActivity
import com.example.rgb4u_app.ui.fragment.ConfirmationDialogFragment
import com.google.android.gms.auth.api.signin.GoogleSignIn
import com.google.android.gms.auth.api.signin.GoogleSignInClient
import com.google.android.gms.auth.api.signin.GoogleSignInOptions
import com.google.android.material.switchmaterial.SwitchMaterial
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.database.DataSnapshot
import com.google.firebase.database.DatabaseError
import com.google.firebase.database.DatabaseReference
import com.google.firebase.database.FirebaseDatabase
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
    private lateinit var googleSignInClient: GoogleSignInClient

    companion object {
        private const val TAG = "MyPageMainActivity"
    }

    // 현재 로그인된 사용자의 UID를 가져오는 함수
    private val userId = FirebaseAuth.getInstance().currentUser?.uid

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_my_page_main)

        // Firebase Database 초기화
        database = FirebaseDatabase.getInstance().reference

        // GoogleSignInClient 초기화
        val gso = GoogleSignInOptions.Builder(GoogleSignInOptions.DEFAULT_SIGN_IN)
            .requestIdToken(getString(R.string.client_Id))
            .requestEmail()
            .build()

        googleSignInClient = GoogleSignIn.getClient(this, gso)

        // 사용자 프로필 로드
        loadUserProfile()  // 기존 사용자 정보 로드

        // UI 요소 초기화
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
        val switchState = sharedPreferences.getBoolean("switchPassword", false)
        switchPassword.isChecked = switchState
        changePasswordLayout.visibility = if (switchState) View.VISIBLE else View.GONE

        // 스위치 상태 변경 리스너 설정
        switchPassword.setOnCheckedChangeListener { _, isChecked ->
            if (isChecked) {
                val intent = Intent(this, MyPagePasswordSettingActivity::class.java)
                intent.putExtra("switchPasswordState", true)
                startActivity(intent)
            } else {
                changePasswordLayout.visibility = View.GONE
                with(sharedPreferences.edit()) {
                    putBoolean("switchPassword", false)
                    apply()
                }
            }
        }

        // 이전 액티비티에서 전달받은 인텐트 데이터 처리
        val isPasswordSet = intent.getBooleanExtra("passwordSet", false)
        if (isPasswordSet) {
            switchPassword.isChecked = true
            changePasswordLayout.visibility = View.VISIBLE
        }

        // 비밀번호 변경 버튼 클릭 리스너
        findViewById<ImageButton>(R.id.btn_change_password).setOnClickListener {
            val intent = Intent(this, MyPagePasswordEditActivity::class.java)
            startActivity(intent)
        }

        // 로그아웃 클릭 리스너
        tvLogout.setOnClickListener {
            val dialog = ConfirmationDialogFragment(
                title = "로그아웃하시겠습니까?",
                message = "",
                confirmButtonText = "로그아웃",
                onConfirm = {
                    // FirebaseAuth 인스턴스 초기화
                    val auth = FirebaseAuth.getInstance()

                    // 구글 로그아웃 처리
                    auth.signOut()
                    googleSignInClient.signOut().addOnCompleteListener {
                        Log.d(TAG, "구글 로그아웃 성공")
                    }

                    // 로그인 화면으로 이동
                    val intent = Intent(this, LoginActivity::class.java)
                    startActivity(intent)
                    finish()
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
                    // FirebaseAuth 인스턴스 초기화
                    val auth = FirebaseAuth.getInstance()
                    val user = auth.currentUser

                    user?.let {
                        // Firebase Realtime Database에서 사용자 데이터 삭제
                        deleteUserFromDatabase(user.uid)

                        // Firebase Auth에서 사용자 삭제
                        user.delete().addOnCompleteListener { task ->
                            if (task.isSuccessful) {
                                Toast.makeText(this, "회원 탈퇴가 완료되었습니다.", Toast.LENGTH_SHORT).show()

                                // 로그인 화면으로 이동
                                val intent = Intent(this, LoginActivity::class.java)
                                startActivity(intent)
                                finish()
                            } else {
                                Toast.makeText(this, "회원 탈퇴에 실패했습니다: ${task.exception?.message}", Toast.LENGTH_SHORT).show()
                            }
                        }
                    }
                }
            )
            dialog.show(supportFragmentManager, "deleteAccountConfirmation")
        }

        // 도움말 클릭 리스너
        btnHowToUseDetails.setOnClickListener {
            val intent = Intent(this, MyPageHowToUseActivity::class.java)
            startActivity(intent)
        }

        // 프로필 수정 클릭 리스너
        btnEditProfile.setOnClickListener {
            val intent = Intent(this, MyPageProfileEditActivity::class.java)
            startActivity(intent)
        }

        //알림 설정 클릭 리스너
        btnNotificationDetails.setOnClickListener{
            val intent = Intent(this, MyPageNotificationSettingsActivity::class.java)
            startActivity(intent)
        }

        btnTermsOfServiceDetails.setOnClickListener{
            val intent = Intent(this, MyPageServiceCheck::class.java)
            startActivity(intent)
        }
    }

    // Firebase Realtime Database에서 사용자 데이터 삭제
    private fun deleteUserFromDatabase(userId: String) {
        val userRef = database.child("users").child(userId)
        userRef.removeValue().addOnCompleteListener { task ->
            if (task.isSuccessful) {
                Log.d("회원 탈퇴", "사용자 데이터 삭제 완료")
            } else {
                Log.e("회원 탈퇴", "사용자 데이터 삭제 실패: ${task.exception?.message}")
            }
        }
    }

    override fun onResume() {
        super.onResume()
        val sharedPreferences = getSharedPreferences("MyPrefs", MODE_PRIVATE)
        val isSwitchPasswordOn = sharedPreferences.getBoolean("switchPassword", false)
        switchPassword.isChecked = isSwitchPasswordOn
        changePasswordLayout.visibility = if (isSwitchPasswordOn) View.VISIBLE else View.GONE
    }

    private fun loadUserProfile() {
        userId?.let { uid ->
            database.child("users").child(uid).addListenerForSingleValueEvent(object :
                ValueEventListener {
                override fun onDataChange(dataSnapshot: DataSnapshot) {
                    val nickname = dataSnapshot.child("nickname").getValue(String::class.java)
                    tvnickname.setText(nickname)
                }
                override fun onCancelled(databaseError: DatabaseError) {
                    Log.e("MyPageMainActivity", "닉네임을 불러오는 데 실패했습니다: ${databaseError.message}")
                }
            })
        } ?: run {
            Log.e(TAG, "사용자가 로그인되지 않았습니다.")
            Toast.makeText(this, "로그인 후 이용해 주세요.", Toast.LENGTH_SHORT).show()
            // 로그인 화면으로 리다이렉트
            val intent = Intent(this, LoginActivity::class.java)
            startActivity(intent)
            finish()
        }
    }
}

