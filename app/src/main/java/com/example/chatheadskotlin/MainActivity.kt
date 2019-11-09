package com.example.chatheadskotlin

import android.app.Activity
import android.content.Intent
import android.net.Uri
import android.os.Build
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.provider.Settings
import android.widget.Toast
import androidx.appcompat.widget.AppCompatButton
import androidx.constraintlayout.widget.ConstraintLayout

class MainActivity : AppCompatActivity() {

    var CODE_DRAW_OVER_OTHER_APP_PERMISSION = 2084

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)

        if(Build.VERSION.SDK_INT >= Build.VERSION_CODES.M && !Settings.canDrawOverlays(this)) {

            var intent = Intent(Settings.ACTION_MANAGE_OVERLAY_PERMISSION,
                    Uri.parse("package:" + getPackageName()));
            startActivityForResult(intent, CODE_DRAW_OVER_OTHER_APP_PERMISSION)
        } else {
            initializeView()
        }
    }


    fun initializeView() {
        findViewById<AppCompatButton>(R.id.add_header).setOnClickListener {
            startService(Intent(this, FloatButtonService::class.java))
        }
        findViewById<AppCompatButton>(R.id.remove_header).setOnClickListener {
            stopService(Intent(this, FloatButtonService::class.java))
        }
    }

    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        if(requestCode == CODE_DRAW_OVER_OTHER_APP_PERMISSION) {

            if(Settings.canDrawOverlays(this)) {
                initializeView()
            } else {
                Toast.makeText(this,
                    "Draw over other app permission not available. Closing the application",
                    Toast.LENGTH_SHORT).show()
                finish()
            }
        } else {
            super.onActivityResult(requestCode, resultCode, data)
        }
    }
}
