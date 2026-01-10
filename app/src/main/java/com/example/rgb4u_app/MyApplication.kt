package com.example.rgb4u.ver1_app

import android.app.Application
import androidx.lifecycle.ViewModelProvider
import com.example.rgb4u.ver1_appclass.DiaryViewModel

class MyApplication : Application() {
    // ViewModel 인스턴스를 저장할 ViewModelProvider
    lateinit var diaryViewModel: DiaryViewModel

    override fun onCreate() {
        super.onCreate()

        // ViewModel 초기화
        diaryViewModel = ViewModelProvider.AndroidViewModelFactory.getInstance(this).create(DiaryViewModel::class.java)
    }
}
