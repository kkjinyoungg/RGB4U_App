package com.example.rgb4u_app.ui.activity.login

import android.content.Intent
import android.os.Bundle
import android.util.Log
import android.widget.ImageButton
import androidx.activity.result.ActivityResultLauncher
import androidx.activity.result.contract.ActivityResultContracts
import androidx.appcompat.app.AppCompatActivity
import com.example.rgb4u_app.R
import com.example.rgb4u_app.ui.activity.home.MainActivity
import com.google.android.gms.auth.api.signin.GoogleSignIn
import com.google.android.gms.auth.api.signin.GoogleSignInAccount
import com.google.android.gms.auth.api.signin.GoogleSignInClient
import com.google.android.gms.auth.api.signin.GoogleSignInOptions
import com.google.android.gms.common.api.ApiException
import com.google.android.gms.tasks.Task
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.auth.FirebaseUser
import com.google.firebase.auth.GoogleAuthProvider
import com.google.firebase.database.FirebaseDatabase
import com.example.rgb4u_app.ui.activity.login.SignUpActivity1
import com.example.rgb4u_app.ui.activity.login.DistortionDefaultData

class LoginActivity : AppCompatActivity() {

    private lateinit var googleSignInClient: GoogleSignInClient
    private lateinit var auth: FirebaseAuth
    private lateinit var googleSignInLauncher: ActivityResultLauncher<Intent>

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_login)

        auth = FirebaseAuth.getInstance() // Firebase Auth 초기화

        //googlesigninoptions 설정
        val gso = GoogleSignInOptions.Builder(GoogleSignInOptions.DEFAULT_SIGN_IN)
            .requestIdToken(getString(R.string.client_Id))
            .requestEmail()
            .build()

        googleSignInClient = GoogleSignIn.getClient(this, gso) //googlesigninclient 초기화

        auth.signOut() // 자동로그인 방지용 이전 세션 로그아웃

        // ActivityResultLauncher 초기화
        googleSignInLauncher = registerForActivityResult(
            ActivityResultContracts.StartActivityForResult()
        ) { result ->
            if (result.resultCode == RESULT_OK) {
                val data = result.data
                val task = GoogleSignIn.getSignedInAccountFromIntent(data)
                handleSignInResult(task)
            }
        }

        // 구글 로그인 버튼 클릭 이벤트 (버튼 이름 변경하기)
        findViewById<ImageButton>(R.id.btn_google).setOnClickListener {
            loginWithGoogle()
        }
    }

    private fun loginWithGoogle() {
        // 로그인 시 계정 선택 창을 항상 띄우기 위해 로그아웃
        googleSignInClient.signOut().addOnCompleteListener {
            googleSignInClient.revokeAccess().addOnCompleteListener {
                val signInIntent = googleSignInClient.signInIntent
                googleSignInLauncher.launch(signInIntent)
            }
        }
    }

    private fun handleSignInResult(completedTask: Task<GoogleSignInAccount>) {
        try {
            val account = completedTask.getResult(ApiException::class.java)!!
            firebaseAuthWithGoogle(account)
        } catch (e: ApiException) {
            Log.w(TAG, "signInResult:failed code=${e.statusCode}, message=${e.message}")
        }
    }

    private fun firebaseAuthWithGoogle(account: GoogleSignInAccount) {
        val credential = GoogleAuthProvider.getCredential(account.idToken, null)
        auth.signInWithCredential(credential)
            .addOnCompleteListener { task ->
                if (task.isSuccessful) {
                    Log.d(TAG, "로그인 성공")
                    val user = auth.currentUser // 현재 사용자 정보 가져오기
                    if (user != null) {
                        checkUserInDatabase(user) // 사용자 정보를 DB에 저장
                    }
                } else {
                    Log.e(TAG, "로그인 실패: ${task.exception?.message}")
                }
            }
    }

    //기존 가입한 유저인지 체크
    private fun checkUserInDatabase(user: FirebaseUser) {
        val database = FirebaseDatabase.getInstance()
        val userId = user.uid
        val userRef = database.getReference("users").child(userId)

        userRef.get().addOnCompleteListener { task ->
            if (task.isSuccessful) {
                if (task.result.exists()) {
                    Log.d("유저체크", "기존 사용자입니다")
                    startMainActivity()  // 사용자가 이미 가입된 경우 메인화면으로 이동
                } else {
                    saveUserToDatabase(user)
                    // DistortionDefaultData 객체를 생성하고 userId를 넘겨주기
                    val distortionData = DistortionDefaultData(userId)
                    distortionData.saveDistortionData() // 데이터 저장 호출
                    Log.d("유저체크", "신규가입자입니다") // 태그와 메시지를 올바르게 설정
                    startSignUpActivity1() // 사용자가 가입되지 않은 경우 닉네임 입력으로 이동
                }
            } else {
                Log.e(TAG, "사용자 정보 조회 실패: ${task.exception?.message}")
            }
        }
    }

    //요청 코드, 로드 태그 정의
    companion object {
        private const val TAG = "LoginActivity"
    }

    // 사용자 정보 저장
    private fun saveUserToDatabase(user: FirebaseUser) {
        val database = FirebaseDatabase.getInstance()
        val userId = user.uid
        val userRef = database.getReference("users").child(userId)

        // 사용자 정보 객체 생성
        val userInfo = UserInfo(
            userId = userId, // 고유 사용자 ID
            email = user.email, // 이메일 주소
            joinedDate = System.currentTimeMillis() // 현재 시간을 밀리초로 저장
        )

        // 사용자 정보를 Realtime Database에 저장
        userRef.setValue(userInfo).addOnCompleteListener { task ->
            if (task.isSuccessful) {
                Log.d(TAG, "사용자 정보 파이어베이스에 저장 완료")
            } else {
                Log.e(TAG, "사용자 정보 파이어베이스에 저장 실패: ${task.exception?.message}")
            }
        }
    }

    data class UserInfo(
        val userId: String,
        val email: String?,
        val joinedDate: Long
    )

    private fun startSignUpActivity1() {
        val intent = Intent(this, SignUpActivity1::class.java)
        startActivity(intent)
    }

    private fun startMainActivity() {
        val intent = Intent(this, MainActivity::class.java)
        startActivity(intent)
    }
}
