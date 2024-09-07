package com.example.rgb4u_app

import android.os.Bundle
import androidx.appcompat.app.AppCompatActivity
import androidx.fragment.app.Fragment

class DiaryWriteActivity : AppCompatActivity() {

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
                // 다음 액티비티 설정
                setNextActivity(ThinkWriteActivity::class.java)
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
}
