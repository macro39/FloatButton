package com.example.chatheadskotlin

import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.widget.Button

class ScreeningQuestionnaireActivity : AppCompatActivity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_screening_questionnaire)

        val buttonNext = findViewById<Button>(R.id.button_screening_questionnaire_next)

        buttonNext.setOnClickListener {
            val intent = Intent(this, WelcomeMessageActivity::class.java)
            startActivity(intent)
            finish()
        }

    }

    override fun onBackPressed() {
    }
}
