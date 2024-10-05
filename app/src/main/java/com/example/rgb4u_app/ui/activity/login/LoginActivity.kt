package com.example.rgb4u_app.ui.activity.login

import android.content.Intent
import android.os.Bundle
import android.util.Log
import android.widget.ImageButton
import androidx.appcompat.app.AppCompatActivity
import com.example.rgb4u_app.R
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.auth.GoogleAuthProvider
import com.google.firebase.auth.FirebaseUser
import com.google.android.gms.auth.api.signin.GoogleSignIn
import com.google.android.gms.auth.api.signin.GoogleSignInAccount
import com.google.android.gms.auth.api.signin.GoogleSignInClient
import com.google.android.gms.auth.api.signin.GoogleSignInOptions
import com.google.android.gms.common.api.ApiException
import com.google.android.gms.tasks.Task
import com.google.android.gms.tasks.OnCompleteListener
import com.google.firebase.database.FirebaseDatabase

class LoginActivity : AppCompatActivity() {

    // 구글 로그인
    private lateinit var googleSignInClient: GoogleSignInClient
    private lateinit var auth: FirebaseAuth

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_login)

        // Firebase Auth 초기화
        auth = FirebaseAuth.getInstance()

        // Google Sign-In 옵션 설정
        val gso = GoogleSignInOptions.Builder(GoogleSignInOptions.DEFAULT_SIGN_IN)
            .requestIdToken(getString(R.string.client_Id)) // 클라이언트 OAUTH ID 사용
            .requestEmail()
            .build()

        googleSignInClient = GoogleSignIn.getClient(this, gso)

        // 구글 로그인 버튼 클릭 이벤트
        findViewById<ImageButton>(R.id.btn_kakao).setOnClickListener {
            loginWithGoogle()
        }
    }

    private fun loginWithGoogle() {
        val signInIntent = googleSignInClient.signInIntent
        startActivityForResult(signInIntent, RC_SIGN_IN)
    }

    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        super.onActivityResult(requestCode, resultCode, data)

        // Result returned from launching the Intent from GoogleSignInClient.getSignInIntent(...);
        if (requestCode == RC_SIGN_IN) {
            val task = GoogleSignIn.getSignedInAccountFromIntent(data)
            handleSignInResult(task)
        }
    }

    private fun handleSignInResult(completedTask: Task<GoogleSignInAccount>) {
        try {
            val account = completedTask.getResult(ApiException::class.java)!!
            firebaseAuthWithGoogle(account)
        } catch (e: ApiException) {
            // 로그인 실패 처리
            Log.w(TAG, "signInResult:failed code=${e.statusCode}, message=${e.message}")
        }
    }

    private fun firebaseAuthWithGoogle(account: GoogleSignInAccount) {
        val credential = GoogleAuthProvider.getCredential(account.idToken, null)
        auth.signInWithCredential(credential)
            .addOnCompleteListener(this, OnCompleteListener { task ->
                if (task.isSuccessful) {
                    // 로그인 성공
                    println("로그인 성공")
                    // 사용자 정보를 DB에 저장
                    val user = auth.currentUser
                    if (user != null) {
                        saveUserToDatabase(user)
                    }
                    // 로그인 성공 시 다음 화면으로 이동
                    startSignUpActivity1()
                } else {
                    // 로그인 실패 처리
                    println("로그인 실패")
                }
            })
    }

    companion object {
        private const val RC_SIGN_IN = 9001
        private const val TAG = "LoginActivity" // TAG 추가
    }

    // 사용자 정보 저장
    private fun saveUserToDatabase(user: FirebaseUser) {
        val database = FirebaseDatabase.getInstance()
        val userId = user.uid
        val userRef = database.getReference("users").child(userId)

        // 사용자 정보 객체 생성
        val userInfo = UserInfo(
            userId = userId,
            email = user.email,
            joinedDate = System.currentTimeMillis() // 현재 시간을 밀리초로 저장
        )

        // 사용자 정보를 Realtime Database에 저장
        userRef.setValue(userInfo).addOnCompleteListener { task ->
            if (task.isSuccessful) {
                println("사용자 정보 파이어베이스에 저장 완료")
            } else {
                println("사용자 정보 파이어베이스에 저장 실패: ${task.exception?.message}")
            }
        }
    }

    // 사용자 정보 클래스
    data class UserInfo(
        val userId: String,             // 고유 사용자 ID
        val email: String?,          // 이메일 주소
        val joinedDate: Long         // 가입 날짜 (밀리초)
    )

    private fun startSignUpActivity1() {
        val intent = Intent(this, SignUpActivity1::class.java)
        startActivity(intent)
    }
}
