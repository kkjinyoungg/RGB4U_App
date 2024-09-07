package com.example.rgb4u_app

import android.os.Bundle
import androidx.appcompat.app.AppCompatActivity
import androidx.fragment.app.Fragment

class DiaryWriteActivity : AppCompatActivity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_write_diary)

        if (savedInstanceState == null) {
            initializeFragment()
        }
    }

    private fun initializeFragment() {
        // setNextActivity가 올바르게 설정되도록 보장
        val fragment = MyRecordFragment().apply {
            arguments = Bundle().apply {
                putString("questionText", "어떤 상황이었어?")
                putInt("currentStep", 1) // 현재 단계 설정
            }
            setNextActivity(ThinkWriteActivity::class.java) // 다음 액티비티 설정
        }
        openFragment(fragment)
    }


    private fun openFragment(fragment: Fragment) {
        supportFragmentManager.beginTransaction()
            .replace(R.id.fragment_container, fragment)
            .addToBackStack(null)
            .commit()
    }
}
