package com.example.chatheadskotlin

import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.widget.Button

class ConsentActivity : AppCompatActivity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_consent)

        val buttonYes = findViewById<Button>(R.id.button_consent_yes)

        buttonYes.setOnClickListener {
            val intent = Intent(this, ScreeningQuestionnaireActivity::class.java)
            startActivity(intent)
            finish()
        }

        val buttonNo = findViewById<Button>(R.id.button_consent_no)

        buttonNo.setOnClickListener {
            finish()
        }

    }

    override fun onBackPressed() {
    }
}
