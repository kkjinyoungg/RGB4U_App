package com.example.rgb4u_app

import android.util.Log
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import com.google.firebase.database.DataSnapshot
import com.google.firebase.database.DatabaseError
import com.google.firebase.database.FirebaseDatabase
import com.google.firebase.database.ValueEventListener

class AllViewModel : ViewModel() {
    private val _diaryData = MutableLiveData<List<DiaryEntry>>()  // List로 변경
    val diaryData: LiveData<List<DiaryEntry>> get() = _diaryData

    val diaryEntry: DiaryEntry?
        get() = _diaryData.value?.firstOrNull()  // 첫 번째 diaryEntry 가져오기

    // userId와 yyyymmdd를 매개변수로 받는 loadDiaryData 함수
    fun loadDiaryData(userId: String, yyyymmdd: String) {
        val databaseReference = FirebaseDatabase.getInstance().getReference("users/$userId/diaries/$yyyymmdd")

        databaseReference.addListenerForSingleValueEvent(object : ValueEventListener {
            override fun onDataChange(snapshot: DataSnapshot) {
                if (snapshot.exists()) {
                    val diaryEntry = snapshot.getValue(DiaryEntry::class.java)
                    if (diaryEntry != null) {
                        _diaryData.value = listOf(diaryEntry) // List<DiaryEntry>로 감싸서 전달
                    } else {
                        Log.e("AllViewModel", "DiaryEntry가 null입니다.")
                    }
                } else {
                    Log.e("AllViewModel", "해당 날짜의 데이터가 존재하지 않습니다.")
                }
            }

            override fun onCancelled(error: DatabaseError) {
                Log.e("AllViewModel", "데이터 로드 실패: ${error.message}")
            }
        })
    }
}


