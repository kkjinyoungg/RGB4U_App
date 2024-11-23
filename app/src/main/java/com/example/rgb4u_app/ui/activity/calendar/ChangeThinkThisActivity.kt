package com.example.rgb4u_app.ui.activity.calendar

import ChangeThinkThisAdapter
import android.content.Intent
import android.os.Bundle
import android.util.Log
import android.view.View
import android.widget.ImageButton
import android.widget.TextView
import androidx.activity.enableEdgeToEdge
import androidx.appcompat.app.AppCompatActivity
import androidx.appcompat.widget.Toolbar
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.example.rgb4u_app.R
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.database.*

class ChangeThinkThisActivity : AppCompatActivity() {
    private lateinit var recyclerView: RecyclerView
    private lateinit var adapter: ChangeThinkThisAdapter
    private lateinit var database: DatabaseReference

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        setContentView(R.layout.activity_change_think_this)

        // Firebase 초기화
        database = FirebaseDatabase.getInstance().reference

        // diaryId, date 가져오기
        val date = intent.getStringExtra("date") ?: "defaultDate"
        val userId = FirebaseAuth.getInstance().currentUser?.uid

        // Toolbar 설정
        val toolbar: Toolbar = findViewById(R.id.toolbar)
        setSupportActionBar(toolbar)
        supportActionBar?.setDisplayHomeAsUpEnabled(false)
        supportActionBar?.setDisplayShowTitleEnabled(false)

        val toolbarTitle: TextView = findViewById(R.id.toolbar_write_title)
        toolbarTitle.text = "이렇게 생각을 바꿔봤어요"

        val buttonWriteAction2: ImageButton = findViewById(R.id.button_write_action2)
        buttonWriteAction2.visibility = View.GONE

        val buttonWriteAction1: ImageButton = findViewById(R.id.button_write_action1)
        buttonWriteAction1.setOnClickListener {
            val intent = Intent(this, CalenderChangedDayActivity::class.java)
            startActivity(intent)
            finish()
        }

        // RecyclerView 초기화
        recyclerView = findViewById(R.id.ChangeThinkRecyclerView)
        recyclerView.layoutManager = LinearLayoutManager(this)

        // Firebase에서 데이터 로드
        if (userId != null) {
            database.child("users").child(userId).child("diaries").child(date)
                .child("aiAnalysis").child("secondAnalysis").child("thoughtSets")
                .addListenerForSingleValueEvent(object : ValueEventListener {
                    override fun onDataChange(snapshot: DataSnapshot) {
                        val myDataList = mutableListOf<MyData>()

                        for (nameSnapshot in snapshot.children) {
                            val name = nameSnapshot.key ?: continue
                            val thoughtsList = mutableListOf<String>()
                            val thoughtsReasonList = mutableListOf<String>()
                            val changeList = mutableListOf<String>()
                            val changeReasonList = mutableListOf<String>()
                            var imageResId: Int = R.drawable.ic_planet_a
                            var description = ""

                            for (thoughtSet in nameSnapshot.children) {
                                val imageResource = thoughtSet.child("imageResource").value?.toString()
                                val alternativeThoughts = thoughtSet.child("alternativeThoughts").value?.toString()
                                val alternativeThoughtsReason = thoughtSet.child("alternativeThoughtsReason").value?.toString()
                                val selectedThoughts = thoughtSet.child("selectedThoughts").value?.toString()
                                val characterDescription = thoughtSet.child("characterDescription").children.mapNotNull { it.value?.toString() }
                                val charactersReason = thoughtSet.child("charactersReason").value?.toString()

                                if (imageResource != null && imageResource.isNotEmpty()) {
                                    val resId = resources.getIdentifier(imageResource, "drawable", packageName)
                                    if (resId != 0) {
                                        imageResId = resId
                                    }
                                }

                                if (selectedThoughts != null) {
                                    thoughtsList.add(selectedThoughts)
                                }
                                if (charactersReason != null) {
                                    thoughtsReasonList.add(charactersReason)
                                }
                                if (alternativeThoughts != null) {
                                    changeList.add(alternativeThoughts)
                                }
                                if (alternativeThoughtsReason != null) {
                                    changeReasonList.add(alternativeThoughtsReason)
                                }
                                description = characterDescription.joinToString("\n")
                            }

                            val myData = MyData(
                                imageResId = imageResId,
                                name = name,
                                description = description,
                                thinkMessages = thoughtsList,
                                thinkExtraMessages = thoughtsReasonList,
                                changeMessages = changeList,
                                changeExtraMessages = changeReasonList
                            )
                            myDataList.add(myData)
                        }

                        // RecyclerView 어댑터에 데이터 반영
                        adapter = ChangeThinkThisAdapter(myDataList, supportFragmentManager)
                        recyclerView.adapter = adapter
                    }

                    override fun onCancelled(error: DatabaseError) {
                        Log.e("Firebase", "Failed to load data: ${error.message}")
                    }
                })
        } else {
            Log.e("Firebase", "User ID is null")
        }
    }
}
