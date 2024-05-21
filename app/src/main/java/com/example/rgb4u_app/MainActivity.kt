package com.example.rgb4u_app

import android.icu.text.SimpleDateFormat
import android.os.Bundle
import android.text.SpannableString
import android.text.style.RelativeSizeSpan
import androidx.activity.enableEdgeToEdge
import androidx.appcompat.app.AppCompatActivity
import androidx.core.view.ViewCompat
import androidx.core.view.WindowInsetsCompat
import com.example.rgb4u_app.databinding.ActivityMainBinding
import java.util.Locale

class MainActivity : AppCompatActivity() {
    private lateinit var binding: ActivityMainBinding
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityMainBinding.inflate(layoutInflater)
        setContentView(binding.root)

        setFormattedDate()

        enableEdgeToEdge()
    }

    private fun setFormattedDate() {
        val dateFormat = SimpleDateFormat("yyyy년 MM월 dd일", Locale.getDefault())
        val date = java.util.Date()
        val formattedDate = dateFormat.format(date)

        val spannableString = SpannableString("오늘은 \n $formattedDate 이다냥~").apply {
            val start = 6
            val end = start + formattedDate.length
            setSpan(RelativeSizeSpan(1.5f), start, end, SpannableString.SPAN_EXCLUSIVE_EXCLUSIVE)
        }

        binding.todayTextView.text = spannableString

        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.main)) { v, insets ->
            val systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars())
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom)
            insets
        }


    }
}