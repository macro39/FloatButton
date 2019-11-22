package com.example.chatheadskotlin

import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.view.View
import android.widget.Button

class GlobalMessageActivity : AppCompatActivity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_global_message)

        val buttonYes = findViewById<Button>(R.id.button_global_message_yes)

        buttonYes.setOnClickListener {
            val intent = Intent(this, ConsentActivity::class.java)
            startActivity(intent)
            finish()
        }

        val buttonNo = findViewById<Button>(R.id.button_global_message_no)

        buttonNo.setOnClickListener {
            finish()
        }

        val buttonLater = findViewById<Button>(R.id.button_global_message_later)

        buttonLater.setOnClickListener {
            // TODO
            finish()
        }

    }

    override fun onBackPressed() {
    }
}
