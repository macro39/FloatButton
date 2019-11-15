package com.example.chatheadskotlin

import android.content.Intent
import android.net.Uri
import android.os.Build
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.provider.Settings
import android.widget.Toast
import androidx.appcompat.widget.AppCompatButton

class MainActivity : AppCompatActivity() {

    var CODE_DRAW_OVER_OTHER_APP_PERMISSION = 2084
    lateinit var floatButtonService: FloatButtonService

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)

        floatButtonService = FloatButtonService(this)
    }


    fun initializeView() {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M && !Settings.canDrawOverlays(this)) {

            var intent = Intent(
                Settings.ACTION_MANAGE_OVERLAY_PERMISSION,
                Uri.parse("package:" + getPackageName())
            );
            startActivityForResult(intent, CODE_DRAW_OVER_OTHER_APP_PERMISSION)
        }

        floatButtonService.onCreate()
    }

    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        if (requestCode == CODE_DRAW_OVER_OTHER_APP_PERMISSION) {

            if (Settings.canDrawOverlays(this)) {
                floatButtonService.onCreate()
            } else {
                Toast.makeText(
                    this,
                    "Draw over other app permission not available. Closing the application",
                    Toast.LENGTH_SHORT
                ).show()
                finish()
            }
        } else {
            super.onActivityResult(requestCode, resultCode, data)
        }
    }

    override fun onDestroy() {
        super.onDestroy()

        floatButtonService.onDestroy()
    }

    override fun onStop() {
        super.onStop()

        floatButtonService.onDestroy()
    }

    override fun onStart() {
        super.onStart()

        initializeView()
    }
}
