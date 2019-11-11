package com.example.chatheadskotlin

import android.app.Service
import android.content.Context
import android.content.Intent
import android.graphics.PixelFormat
import android.graphics.Point
import android.os.Build
import android.os.CountDownTimer
import android.os.Handler
import android.os.IBinder
import android.view.*
import android.widget.Toast
import kotlin.math.abs
import kotlin.math.cos
import kotlin.math.exp

/**
 * Created by Kamil Macek on 4. 11. 2019.
 */
class FloatButtonService: Service() {
    private lateinit var mFloatButton: View
    private lateinit var mWindowManager: WindowManager
    private lateinit var mDisplaySize: Point

    private var isOnRightSide = true

    private var initX: Int = 0
    private var initY: Int = 0

    private var marginX: Int = 0
    private var marginY: Int = 0

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

        mDisplaySize = Point()

        mDisplaySize.set(width, height)

        mFloatButton.setOnTouchListener(object: View.OnTouchListener {
            var start: Long = 0
            var end: Long = 0

            override fun onTouch(view: View?, motionEvent: MotionEvent): Boolean {

                var layoutParams = mFloatButton.layoutParams as WindowManager.LayoutParams

                var shiftX = motionEvent.rawX.toInt()
                var shiftY = motionEvent.rawY.toInt()

                var destinationX: Int
                var destinationY: Int

                when (motionEvent.action) {

                    MotionEvent.ACTION_DOWN -> {
                        start = System.currentTimeMillis()

                        initX = shiftX
                        initY = shiftY

                        marginX = layoutParams.x
                        marginY = layoutParams.y

                        return true
                    }

                    MotionEvent.ACTION_UP -> {

                        var diffX = shiftX - initX
                        var diffY = shiftY - initY

                        if (abs(diffX) < 10 && abs(diffY) < 10) {
                            end = System.currentTimeMillis()

                            if ((end - start) < 300) {
                                floatButtonClicked()
                            }
                        }

                        destinationY = marginY + diffY

                        if (destinationY < 0) {
                            destinationY = 0
                        }

                        if ((destinationY + mFloatButton.height) > mDisplaySize.y) {
                            destinationY = mDisplaySize.y - mFloatButton.height
                        }

                        layoutParams.y = destinationY

                        changePosition(shiftX)

                        return true
                    }

                    MotionEvent.ACTION_MOVE -> {

//                        layoutParams.x = shiftX + marginX - initX
//                        layoutParams.y = shiftY + marginY - initY

                        if (isOnRightSide) {
                            layoutParams.x = mDisplaySize.x - shiftX
                        } else {
                            layoutParams.x = mDisplaySize.x - shiftX - mFloatButton.width
                        }


                        layoutParams.y = shiftY - mFloatButton.height

                        mWindowManager.updateViewLayout(mFloatButton, layoutParams)

                        return true
                    }

                    else -> {
                        return false
                    }
                }
            }
        })
    }

    fun floatButtonClicked() {

        var toast =  Toast.makeText(this, "Clicked", Toast.LENGTH_SHORT)
        toast.show()
    }

    fun changePosition(currentX: Int) {
        if (currentX > (mDisplaySize.x / 2)) {
            isOnRightSide = false
            moveLeft(currentX)
        } else {
            isOnRightSide = true
            moveRight(currentX)
        }
    }

    fun moveRight(currentX: Int) {
        val timer = object: CountDownTimer(500, 5) {
            var mParams = mFloatButton.layoutParams as WindowManager.LayoutParams

            override fun onTick(millisUntilFinished: Long) {
                var step = (500 - millisUntilFinished) / 5

                mParams.x = mDisplaySize.x + (currentX * currentX * step).toInt() - mFloatButton.width
                //mParams.x = mDisplaySize.x + (getMoveValue(step, currentX) - mParams.width).toInt()

                mWindowManager.updateViewLayout(mFloatButton, mParams)
            }

            override fun onFinish() {
                mParams.x = mDisplaySize.x
                mWindowManager.updateViewLayout(mFloatButton, mParams)
            }
        }

        timer.start()
    }

    fun moveLeft(currentX: Int) {
        var x = mDisplaySize.x - currentX

        val timer = object: CountDownTimer(500, 5) {
            var mParams = mFloatButton.layoutParams as WindowManager.LayoutParams

            override fun onTick(millisUntilFinished: Long) {
                var step = (500 - millisUntilFinished) / 5

                mParams.x = 0 - (currentX * currentX * step).toInt()
                //mParams.x = 0 - getMoveValue(step, x).toInt()

                mWindowManager.updateViewLayout(mFloatButton, mParams)
            }

            override fun onFinish() {
                mParams.x = 0

                mWindowManager.updateViewLayout(mFloatButton, mParams)
            }
        }
        timer.start()
    }

    fun getMoveValue(step: Long, scale: Int): Double {
        val exp = -0.05 * step
        val cos = 0.08 * step
        return scale * exp(exp) * cos(cos)
    }



    override fun onDestroy() {
        super.onDestroy()

        mWindowManager.removeView(mFloatButton)
    }
}
