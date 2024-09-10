package com.example.rgb4u_app

import android.content.Intent
import android.os.Bundle
import androidx.appcompat.app.AppCompatActivity
import androidx.fragment.app.Fragment

class DiaryWriteActivity : AppCompatActivity(), MyRecordFragment.NavigationListener {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_write_diary)

        if (savedInstanceState == null) {
            // MyRecordFragment를 생성하고 인자를 전달
            val fragment = MyRecordFragment().apply {
                arguments = Bundle().apply {
                    putString("questionText", "어떤 상황이었어?")
                    putInt("currentStep", 1)  // 현재 단계 설정
                }
            }
            openFragment(fragment)
        }
    }

    fun openFragment(fragment: Fragment) {
        supportFragmentManager.beginTransaction()
            .replace(R.id.fragment_container, fragment)
            .addToBackStack(null)
            .commit()
    }

    // NavigationListener 인터페이스 구현
    override fun onNextButtonClicked() {
        // ThinkWriteActivity로 이동
        val intent = Intent(this, ThinkWriteActivity::class.java)
        startActivity(intent)
    }

    override fun onBackButtonClicked() {
        // MainActivity로 이동
        val intent = Intent(this, MainActivity::class.java)
        startActivity(intent)
    }
}
