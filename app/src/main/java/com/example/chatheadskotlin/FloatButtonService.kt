package com.example.chatheadskotlin

import android.app.Service
import android.content.Context
import android.content.Intent
import android.graphics.PixelFormat
import android.graphics.Point
import android.os.Build
import android.os.CountDownTimer
import android.os.IBinder
import android.view.*
import android.widget.Toast
import kotlin.math.cos
import kotlin.math.exp

/**
 * Created by Kamil Macek on 4. 11. 2019.
 */
class FloatButtonService: Service() {
    private lateinit var mFloatButton: View
    private lateinit var mWindowManager: WindowManager
    private lateinit var displaySize: Point

    private var isOnRightSide = true;

    override fun onBind(p0: Intent?): IBinder? {
        return null
    }

    override fun onCreate() {
        super.onCreate()

        mFloatButton = LayoutInflater.from(this).inflate(R.layout.float_button, null)


        // check for android version
        var params = if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            WindowManager.LayoutParams(
                WindowManager.LayoutParams.WRAP_CONTENT,
                WindowManager.LayoutParams.WRAP_CONTENT,
                WindowManager.LayoutParams.TYPE_APPLICATION_OVERLAY,
                WindowManager.LayoutParams.FLAG_NOT_FOCUSABLE,
                PixelFormat.TRANSLUCENT)
        } else {
            WindowManager.LayoutParams(
                WindowManager.LayoutParams.WRAP_CONTENT,
                WindowManager.LayoutParams.WRAP_CONTENT,
                WindowManager.LayoutParams.TYPE_PHONE,
                WindowManager.LayoutParams.FLAG_NOT_FOCUSABLE,
                PixelFormat.TRANSLUCENT)
        }

        params.gravity = Gravity.TOP or Gravity.RIGHT
        params.x = 0
        params.y = 100

        mWindowManager = getSystemService(Context.WINDOW_SERVICE) as WindowManager
        mWindowManager.addView(mFloatButton, params)

        val width = mWindowManager.defaultDisplay.width
        val height = mWindowManager.defaultDisplay.height

        displaySize = Point()

        displaySize.set(width, height)

//        mFloatButton.setOnClickListener {
//            var toast =  Toast.makeText(this, "Fungujem", Toast.LENGTH_LONG)
//            toast.show()
//        }

        mFloatButton.setOnTouchListener { view, motionEvent ->
            var lastAction = 0
            var initialX = 0
            var initialY = 0
            var initialTouchX = 0.0f
            var initialTouchY = 0.0f

            var screenWidth = mWindowManager.defaultDisplay.width
            var screenHight = mWindowManager.defaultDisplay.height

            when (motionEvent.action) {
//                MotionEvent.ACTION_DOWN -> {
//                    true
//                }
//
//                MotionEvent.ACTION_UP -> {
//                    true
//                }

                MotionEvent.ACTION_MOVE -> {

                    if (motionEvent.rawX.toInt() < (screenWidth / 2)) {
                        isOnRightSide = true
                        //moveRight(motionEvent.rawX.toLong())
                        //params.x = motionEvent.rawX.toInt()
                        params.x = displaySize.x
                    } else {
                        isOnRightSide = false
                        //moveLeft(motionEvent.rawX.toLong())
                        //params.x = (screenWidth / 2) + motionEvent.rawX.toInt()
                        params.x = 0
                    }

//                    params.x = screenWidth - motionEvent.rawX.toInt()
                    params.y = motionEvent.rawY.toInt()

                    mWindowManager.updateViewLayout(mFloatButton, params)
//                    lastAction = motionEvent.action

                    true
                }

                else -> {
                    false
                }
            }
        }
    }

    fun moveRight(currentX: Long) {
        val timer = object: CountDownTimer(500, 5) {
            var mParams = mFloatButton.layoutParams

            override fun onTick(millisUntilFinished: Long) {
                var step = (500 - millisUntilFinished) / 5
                mParams.width = displaySize.x + (getMoveValue(step, currentX) - mParams.width).toInt()
                mWindowManager.updateViewLayout(mFloatButton, mParams)
            }

            override fun onFinish() {
                mParams.width = (displaySize.x - mFloatButton.x).toInt()
                mWindowManager.updateViewLayout(mFloatButton, mParams)
            }
        }

        timer.start()
    }

    fun moveLeft(currentX: Long) {
        var x = displaySize.x - currentX

        val timer = object: CountDownTimer(500, 5) {
            var mParams = mFloatButton.layoutParams

            override fun onTick(millisUntilFinished: Long) {
                var step = (500 - millisUntilFinished) / 5

                mParams.width = 0 - (currentX * currentX * step).toInt()

                mWindowManager.updateViewLayout(mFloatButton, mParams)
            }

            override fun onFinish() {
                mParams.height = 0

                mWindowManager.updateViewLayout(mFloatButton, mParams)
            }
        }
        timer.start()
    }

    fun getMoveValue(step: Long, scale: Long): Double {
        val exp = -0.05 * step
        val cos = 0.08 * step
        return scale * exp(exp) * cos(cos)
    }



    override fun onDestroy() {
        super.onDestroy()

        mWindowManager.removeView(mFloatButton)
    }
}
