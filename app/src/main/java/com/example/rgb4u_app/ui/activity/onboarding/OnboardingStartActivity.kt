package com.example.rgb4u_app.ui.activity.onboarding

import android.content.Intent
import android.os.Bundle
import android.util.Log
import android.widget.Button
import android.widget.TextView
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import com.example.rgb4u_app.R
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.database.*

class OnboardingStartActivity : AppCompatActivity() {

    private lateinit var database: DatabaseReference
    private val userId: String? = FirebaseAuth.getInstance().currentUser?.uid // UID를 nullable로 가져옴

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_onboarding_start)

        // Firebase Realtime Database 초기화
        database = FirebaseDatabase.getInstance().reference

        // btn_continue 버튼 가져오기
        val btnContinue: Button = findViewById(R.id.btn_continue)

        // userName 텍스트뷰 가져오기
        val userNameTextView: TextView = findViewById(R.id.userName)

        // userId가 null인지 확인 후 데이터 가져오기
        if (userId != null) {
            getUserData(userId) { nickname ->
                if (nickname != null) {
                    userNameTextView.text = nickname
                } else {
                    Log.e("OnboardingStartActivity", "사용자 데이터를 불러오지 못했습니다.")
                }
            }
        } else {
            Log.e("OnboardingStartActivity", "사용자 UID를 가져올 수 없습니다.")
        }

        // 버튼 클릭 리스너 설정
        btnContinue.setOnClickListener {
            val intent = Intent(this, OnboardingActivity::class.java)
            startActivity(intent)
        }
    }

    // 데이터를 가져오는 메서드
    private fun getUserData(userId: String, callback: (String?) -> Unit) {
        val userRef = database.child("users").child(userId).child("nickname")

        userRef.addListenerForSingleValueEvent(object : ValueEventListener {
            override fun onDataChange(snapshot: DataSnapshot) {
                val nickname = snapshot.getValue(String::class.java)
                callback(nickname) // 데이터를 콜백으로 반환
            }

            override fun onCancelled(error: DatabaseError) {
                Log.e("OnboardingStartActivity", "데이터베이스 에러: ${error.message}")
                callback(null) // 에러 발생 시 null 반환
            }
        })
    }
}
