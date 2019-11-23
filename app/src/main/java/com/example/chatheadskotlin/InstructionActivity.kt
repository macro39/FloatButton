package com.example.chatheadskotlin

import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.widget.Button

class InstructionActivity : AppCompatActivity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_instruction)

        val buttonNext = findViewById<Button>(R.id.button_instruction_next)

        buttonNext.setOnClickListener {
            val intent = Intent(this, ThankYouMessageActivity::class.java)
            startActivity(intent)
            finish()
        }
    }
}
