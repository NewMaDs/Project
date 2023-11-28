package com.example.alarm

import android.content.Intent
import android.os.Bundle
import android.os.Handler
import android.os.SystemClock
import android.widget.ImageButton
import android.widget.TextView
import androidx.appcompat.app.AppCompatActivity

class timer : AppCompatActivity() {
    private lateinit var textView: TextView
    private lateinit var startButton: ImageButton
    private lateinit var resultButton: ImageButton

    private var isTimerRunning = false
    private var startTime: Long = 0
    private var elapsedTime: Long = 0

    private val handler = Handler()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_timer)

        textView = findViewById(R.id.textView)
        startButton = findViewById(R.id.imageButton2)
        resultButton = findViewById(R.id.imageButton3)

        startButton.setOnClickListener {
            if (isTimerRunning) {
                stopTimer()
            } else {
                startTimer()
            }
        }

        resultButton.setOnClickListener {
            val intent = Intent(this, MainActivity::class.java)
            startActivity(intent)
        }
    }

    private fun startTimer() {
        startTime = SystemClock.elapsedRealtime() - elapsedTime
        handler.postDelayed(timerRunnable, 0)
        isTimerRunning = true
        startButton.setImageResource(R.drawable.stop)
    }

    private fun stopTimer() {
        handler.removeCallbacks(timerRunnable)
        isTimerRunning = false
        startButton.setImageResource(R.drawable.start)
    }

    private val timerRunnable = object : Runnable {
        override fun run() {
            elapsedTime = SystemClock.elapsedRealtime() - startTime
            val hours = (elapsedTime / 3600000).toInt()
            val minutes = ((elapsedTime - hours * 3600000) / 60000).toInt()
            val seconds = ((elapsedTime - hours * 3600000 - minutes * 60000) / 1000).toInt()

            val timeString = String.format("%02d:%02d:%02d", hours, minutes, seconds)
            textView.text = timeString

            handler.postDelayed(this, 1000)
        }
    }
}

