package com.example.rgb4u_app

import android.app.Application
import androidx.lifecycle.ViewModelProvider
import com.example.rgb4u_appclass.DiaryViewModel
import com.kakao.sdk.common.KakaoSdk

class MyApplication : Application() {
    // ViewModel 인스턴스를 저장할 ViewModelProvider
    lateinit var diaryViewModel: DiaryViewModel

    override fun onCreate() {
        super.onCreate()

        // 카카오 SDK 초기화
        KakaoSdk.init(this, getString(R.string.kakao_app_key))
        // ViewModel 초기화
        diaryViewModel = ViewModelProvider.AndroidViewModelFactory.getInstance(this).create(DiaryViewModel::class.java)
    }
}
