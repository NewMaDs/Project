package com.example.alarm

import android.content.Intent
import android.media.MediaPlayer
import android.os.Bundle
import android.os.Handler
import android.view.KeyEvent
import android.widget.EditText
import android.widget.TextView
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import kotlin.random.Random

class AlarmPage : AppCompatActivity() {

    private var mediaPlayer: MediaPlayer? = null
    private val handler = Handler()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_alarm_page)

        val num1TextView: TextView = findViewById(R.id.num1)
        val num2TextView: TextView = findViewById(R.id.num2)
        val operTextView: TextView = findViewById(R.id.oper)
        val inputEditText: EditText = findViewById(R.id.inputOtvet)


        mediaPlayer = MediaPlayer.create(this, R.raw.bud)
        mediaPlayer?.isLooping = true

        mediaPlayer?.start()


        generateAndSetNumbersAndOperator(num1TextView, num2TextView, operTextView, inputEditText)
    }

    private fun generateAndSetNumbersAndOperator(
        num1TextView: TextView,
        num2TextView: TextView,
        operTextView: TextView,
        inputEditText: EditText
    ) {

        val randomNum1 = Random.nextInt(1, 11)
        val randomNum2 = Random.nextInt(1, 11)
        val operators = arrayOf("+", "-", "*",)
        val randomOperator = operators.random()

        val correctAnswer = calculateAnswer(randomNum1, randomNum2, randomOperator)

        num1TextView.text = randomNum1.toString()
        num2TextView.text = randomNum2.toString()
        operTextView.text = randomOperator


        inputEditText.text.clear()


        handler.postDelayed({

            checkAnswer(inputEditText.text.toString().toIntOrNull(), correctAnswer, num1TextView, num2TextView, operTextView, inputEditText)
        }, 5000)
    }

    private fun calculateAnswer(num1: Int, num2: Int, operator: String): Int {
        return when (operator) {
            "+" -> num1 + num2
            "-" -> num1 - num2
            "*" -> num1 * num2
            else -> throw IllegalArgumentException("Invalid operator")
        }
    }

    private fun checkAnswer(
        userAnswer: Int?,
        correctAnswer: Int,
        num1TextView: TextView,
        num2TextView: TextView,
        operTextView: TextView,
        inputEditText: EditText
    ) {
        if (userAnswer == correctAnswer) {

            mediaPlayer?.stop()
            mediaPlayer?.release()
            mediaPlayer = null

            showToast("Правильный ответ!")
            val intent = Intent(this, MainActivity::class.java)
            startActivity(intent)

        } else {
            showToast("Неправильный ответ!")
            generateAndSetNumbersAndOperator(num1TextView, num2TextView, operTextView, inputEditText)
        }
    }

    private fun showToast(message: String) {
        Toast.makeText(this, message, Toast.LENGTH_SHORT).show()
    }

    override fun onKeyDown(keyCode: Int, event: KeyEvent?): Boolean {
        if (keyCode == KeyEvent.KEYCODE_BACK) {
            return true
        }
        return super.onKeyDown(keyCode, event)
    }
}

