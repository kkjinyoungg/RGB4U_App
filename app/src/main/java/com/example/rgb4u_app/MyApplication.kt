package com.example.rgb4u.ver1_app

import android.app.Application
import androidx.lifecycle.ViewModelProvider
import com.example.rgb4u.ver1_appclass.DiaryViewModel
import com.microsoft.clarity.Clarity
import com.microsoft.clarity.ClarityConfig
import com.microsoft.clarity.models.LogLevel

class MyApplication : Application() {
    // ViewModel 인스턴스를 저장할 ViewModelProvider
    lateinit var diaryViewModel: DiaryViewModel

    override fun onCreate() {
        super.onCreate()

        // Microsoft Clarity 초기화
        val config = ClarityConfig(
            projectId = "v3y3d2nn4u",
            logLevel = LogLevel.None // 테스트 시 Verbose ,테스트 끝나면 None
        )
        Clarity.initialize(this, config)

        // ViewModel 초기화
        diaryViewModel = ViewModelProvider.AndroidViewModelFactory.getInstance(this).create(DiaryViewModel::class.java)
    }
}
