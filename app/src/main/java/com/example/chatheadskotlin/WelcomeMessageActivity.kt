package com.example.chatheadskotlin

import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.os.Handler


class WelcomeMessageActivity : AppCompatActivity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_welcome_message)

        val handler = Handler()

        handler.postDelayed( {
            val intent = Intent(this, InstructionActivity::class.java)
            startActivity(intent)
            finish()
        }, 3000)


    }
}
