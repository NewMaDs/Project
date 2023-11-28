package com.example.alarm

import android.app.TimePickerDialog
import android.content.Intent
import android.os.Bundle
import android.os.Handler
import android.os.Looper
import android.view.View
import android.widget.EditText
import android.widget.ImageButton
import android.widget.ImageView
import android.widget.Switch
import android.widget.TextView
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import androidx.cardview.widget.CardView
import java.text.SimpleDateFormat
import java.util.Calendar
import java.util.Date
import java.util.Locale

class MainActivity : AppCompatActivity() {
    private lateinit var clockTextView: TextView
    private val handler = Handler(Looper.getMainLooper())

    private lateinit var alarm1Text: EditText
    private lateinit var alarm2Text: EditText
    private lateinit var alarm3Text: EditText
    private lateinit var alarm4Text: EditText
    private lateinit var alarm5Text: EditText

    private lateinit var resultButton: ImageButton

    private lateinit var switch1: Switch
    private lateinit var switch2: Switch
    private lateinit var switch3: Switch
    private lateinit var switch4: Switch
    private lateinit var switch5: Switch

    private val cardViews by lazy {
        arrayOf(
            findViewById<CardView>(R.id.alarm1),
            findViewById<CardView>(R.id.alarm2),
            findViewById<CardView>(R.id.alarm3),
            findViewById<CardView>(R.id.alarm4),
            findViewById<CardView>(R.id.alarm5)
        )
    }

    private var currentVisibleIndex = 0

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)

        clockTextView = findViewById(R.id.clock)

        val sdf = SimpleDateFormat("HH:mm")

        alarm1Text = findViewById(R.id.alarm1_text)
        alarm2Text = findViewById(R.id.alarm2_text)
        alarm3Text = findViewById(R.id.alarm3_text)
        alarm4Text = findViewById(R.id.alarm4_text)
        alarm5Text = findViewById(R.id.alarm5_text)

        switch1 = findViewById(R.id.switch1)
        switch2 = findViewById(R.id.switch2)
        switch3 = findViewById(R.id.switch3)
        switch4 = findViewById(R.id.switch4)
        switch5 = findViewById(R.id.switch5)


        restoreSavedValues()
        restoreCardViewVisibility()
        restoreSwitchStates()

        setEditTextClickListener(alarm1Text)
        setEditTextClickListener(alarm2Text)
        setEditTextClickListener(alarm3Text)
        setEditTextClickListener(alarm4Text)
        setEditTextClickListener(alarm5Text)

        val plusImageView = findViewById<ImageView>(R.id.plus)
        val resultButton = findViewById<ImageView>(R.id.imageView2)

        resultButton.setOnClickListener {
            val intent = Intent(this, timer::class.java)
            startActivity(intent)
        }

        plusImageView.setOnClickListener {
            currentVisibleIndex = (currentVisibleIndex + 1) % cardViews.size
            cardViews[currentVisibleIndex].visibility = View.VISIBLE
            showToast("Будильник добавлен")
            saveCardViewVisibility()
        }

        plusImageView.setOnLongClickListener {
            cardViews[currentVisibleIndex].visibility = View.GONE
            resetValuesForCardView(currentVisibleIndex)
            currentVisibleIndex = (currentVisibleIndex - 1 + cardViews.size) % cardViews.size
            showToast("Будильник удален")
            saveCardViewVisibility()

            true
        }

        val checkAlarmsRunnable = object : Runnable {
            override fun run() {
                val currentTime = sdf.format(Date())
                clockTextView.text = currentTime
                checkAlarm(alarm1Text, switch1, currentTime)
                checkAlarm(alarm2Text, switch2, currentTime)
                checkAlarm(alarm3Text, switch3, currentTime)
                checkAlarm(alarm4Text, switch4, currentTime)
                checkAlarm(alarm5Text, switch5, currentTime)
                handler.postDelayed(this, 1000)
            }
        }
        handler.post(checkAlarmsRunnable)
    }

    private fun setEditTextClickListener(editText: EditText) {
        editText.setOnClickListener {
            showTimePickerDialog(editText)
        }
    }

    private fun showTimePickerDialog(editText: EditText) {
        val calendar = Calendar.getInstance()
        val hour = calendar.get(Calendar.HOUR_OF_DAY)
        val minute = calendar.get(Calendar.MINUTE)

        val timePickerDialog = TimePickerDialog(
            this,
            TimePickerDialog.OnTimeSetListener { _, hourOfDay, minute ->
                val time = String.format(Locale.getDefault(), "%02d:%02d", hourOfDay, minute)
                editText.setText(time)

                resetAlarmTriggerFlag(editText)
                saveValues()
            },
            hour,
            minute,
            true
        )
        timePickerDialog.show()
    }

    private fun resetAlarmTriggerFlag(alarmText: EditText) {
        val alarmTriggeredKey = "alarm_triggered_${alarmText.id}"
        getSharedPreferences("AlarmPrefs", MODE_PRIVATE)
            .edit()
            .putBoolean(alarmTriggeredKey, false)
            .apply()
    }

    private fun checkAlarm(alarmText: EditText, alarmSwitch: Switch, currentTime: String) {
        val alarmTime = alarmText.text.toString()

        val alarmTriggeredKey = "alarm_triggered_${alarmText.id}"
        val alarmTriggered = getSharedPreferences("AlarmPrefs", MODE_PRIVATE)
            .getBoolean(alarmTriggeredKey, false)

        if (alarmSwitch.isChecked && alarmTime == currentTime && !alarmTriggered) {
            showToast("Будильник сработал")
            getSharedPreferences("AlarmPrefs", MODE_PRIVATE)
                .edit()
                .putBoolean(alarmTriggeredKey, true)
                .apply()
            val intent = Intent(this, AlarmPage::class.java)
            startActivity(intent)
        }
    }

    private fun saveValues() {
        val prefs = getSharedPreferences("AlarmPrefs", MODE_PRIVATE)
        val editor = prefs.edit()

        editor.putString("alarm1", alarm1Text.text.toString())
        editor.putBoolean("switch1_state", switch1.isChecked)

        editor.putString("alarm2", alarm2Text.text.toString())
        editor.putBoolean("switch2_state", switch2.isChecked)

        editor.putString("alarm3", alarm3Text.text.toString())
        editor.putBoolean("switch3_state", switch3.isChecked)

        editor.putString("alarm4", alarm4Text.text.toString())
        editor.putBoolean("switch4_state", switch4.isChecked)

        editor.putString("alarm5", alarm5Text.text.toString())
        editor.putBoolean("switch5_state", switch5.isChecked)

        editor.apply()
    }

    private fun restoreSwitchStates() {
        val prefs = getSharedPreferences("AlarmPrefs", MODE_PRIVATE)

        switch1.isChecked = prefs.getBoolean("switch1_state", false)
        switch2.isChecked = prefs.getBoolean("switch2_state", false)
        switch3.isChecked = prefs.getBoolean("switch3_state", false)
        switch4.isChecked = prefs.getBoolean("switch4_state", false)
        switch5.isChecked = prefs.getBoolean("switch5_state", false)
    }

    private fun restoreSavedValues() {
        val prefs = getSharedPreferences("AlarmPrefs", MODE_PRIVATE)

        alarm1Text.setText(prefs.getString("alarm1", ""))
        switch1.isChecked = prefs.getBoolean("switch1_state", false)

        alarm2Text.setText(prefs.getString("alarm2", ""))
        switch2.isChecked = prefs.getBoolean("switch2_state", false)

        alarm3Text.setText(prefs.getString("alarm3", ""))
        switch3.isChecked = prefs.getBoolean("switch3_state", false)

        alarm4Text.setText(prefs.getString("alarm4", ""))
        switch4.isChecked = prefs.getBoolean("switch4_state", false)

        alarm5Text.setText(prefs.getString("alarm5", ""))
        switch5.isChecked = prefs.getBoolean("switch5_state", false)
    }

    private fun saveCardViewVisibility() {
        val prefs = getSharedPreferences("AlarmPrefs", MODE_PRIVATE)
        val editor = prefs.edit()

        editor.putBoolean("cardView1", cardViews[0].visibility == View.VISIBLE)
        editor.putBoolean("cardView2", cardViews[1].visibility == View.VISIBLE)
        editor.putBoolean("cardView3", cardViews[2].visibility == View.VISIBLE)
        editor.putBoolean("cardView4", cardViews[3].visibility == View.VISIBLE)
        editor.putBoolean("cardView5", cardViews[4].visibility == View.VISIBLE)

        editor.apply()
    }

    private fun restoreCardViewVisibility() {
        val prefs = getSharedPreferences("AlarmPrefs", MODE_PRIVATE)

        cardViews[0].visibility = if (prefs.getBoolean("cardView1", false)) View.VISIBLE else View.GONE
        cardViews[1].visibility = if (prefs.getBoolean("cardView2", false)) View.VISIBLE else View.GONE
        cardViews[2].visibility = if (prefs.getBoolean("cardView3", false)) View.VISIBLE else View.GONE
        cardViews[3].visibility = if (prefs.getBoolean("cardView4", false)) View.VISIBLE else View.GONE
        cardViews[4].visibility = if (prefs.getBoolean("cardView5", false)) View.VISIBLE else View.GONE
    }

    private fun resetValuesForCardView(index: Int) {
        when (index) {
            0 -> {
                alarm1Text.setText("")
                switch1.isChecked = false
            }
            1 -> {
                alarm2Text.setText("")
                switch2.isChecked = false
            }
            2 -> {
                alarm3Text.setText("")
                switch3.isChecked = false
            }
            3 -> {
                alarm4Text.setText("")
                switch4.isChecked = false
            }
            4 -> {
                alarm5Text.setText("")
                switch5.isChecked = false
            }
        }
    }

    fun showToast(message: String) {
        Toast.makeText(this, message, Toast.LENGTH_SHORT).show()
    }
}





