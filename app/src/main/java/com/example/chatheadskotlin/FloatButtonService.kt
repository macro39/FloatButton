package com.example.chatheadskotlin

import android.app.Activity
import android.content.Context
import android.graphics.PixelFormat
import android.graphics.Point
import android.os.Build
import android.os.CountDownTimer
import android.view.*
import android.widget.ImageView
import android.widget.RelativeLayout
import android.widget.Toast
import kotlin.math.abs
import kotlin.math.cos
import kotlin.math.exp

/**
 * Created by Kamil Macek on 4. 11. 2019.
 */
class FloatButtonService(
    val context: Context
) {

    private lateinit var mLayoutInflater: LayoutInflater

    private lateinit var mFloatView: View
    private lateinit var acceptTaskView: View
    private lateinit var bubbleView: View
    private lateinit var welcomeView: View

    private lateinit var mWindowManager: WindowManager
    private lateinit var mDisplaySize: Point

    private var isOnRightSide = true

    private var initX: Int = 0
    private var initY: Int = 0

    private var marginX: Int = 0
    private var marginY: Int = 0

    private var isInStudy: Boolean = false

    fun onCreate() {
        //super.onCreate()

        mLayoutInflater = LayoutInflater.from(context)

        mFloatView = mLayoutInflater.inflate(R.layout.float_button, null)


        // check for android version
        var params = if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            WindowManager.LayoutParams(
                WindowManager.LayoutParams.WRAP_CONTENT,
                WindowManager.LayoutParams.WRAP_CONTENT,
                WindowManager.LayoutParams.TYPE_APPLICATION_OVERLAY,
                WindowManager.LayoutParams.FLAG_NOT_FOCUSABLE,
                PixelFormat.TRANSLUCENT
            )
        } else {
            WindowManager.LayoutParams(
                WindowManager.LayoutParams.WRAP_CONTENT,
                WindowManager.LayoutParams.WRAP_CONTENT,
                WindowManager.LayoutParams.TYPE_PHONE,
                WindowManager.LayoutParams.FLAG_NOT_FOCUSABLE,
                PixelFormat.TRANSLUCENT
            )
        }

        params.gravity = Gravity.TOP or Gravity.RIGHT
        params.x = 0
        params.y = 100

        mWindowManager = context.getSystemService(Context.WINDOW_SERVICE) as WindowManager
        mWindowManager.addView(mFloatView, params)
        // WindowManager Bad token exe... need to fix

        bubbleView = mFloatView.findViewById(R.id.bubble)
        //acceptTaskView = mFloatView.findViewById(R.id.expanded_container)

        // TODO this should be in method
        // find another xml layout and add to mFloatView
        acceptTaskView =
            mLayoutInflater.inflate(R.layout.accept_task, mFloatView as ViewGroup, false)

        (mFloatView as ViewGroup).addView(acceptTaskView)

        welcomeView =
            mLayoutInflater.inflate(R.layout.welcome_screen, mFloatView as ViewGroup, false)

        (mFloatView as ViewGroup).addView(welcomeView)

        val width = mWindowManager.defaultDisplay.width
        val height = mWindowManager.defaultDisplay.height

        mDisplaySize = Point()

        mDisplaySize.set(width, height)

        acceptTaskView.findViewById<ImageView>(R.id.no_button).setOnClickListener {
            //unlockScreen()
            bubbleView.visibility = View.VISIBLE
            acceptTaskView.visibility = View.GONE
        }

        acceptTaskView.findViewById<ImageView>(R.id.yes_button).setOnClickListener {
            isInStudy = true
            welcomeView.visibility = View.VISIBLE
            acceptTaskView.visibility = View.GONE
        }

        welcomeView.findViewById<ImageView>(R.id.close_welcome_screen).setOnClickListener {
            bubbleView.visibility = View.VISIBLE
            welcomeView.visibility = View.GONE
        }

        mFloatView.setOnTouchListener(object : View.OnTouchListener {
            var start: Long = 0
            var end: Long = 0

            override fun onTouch(view: View?, motionEvent: MotionEvent): Boolean {

                var layoutParams = mFloatView.layoutParams as WindowManager.LayoutParams

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

                        if ((destinationY + mFloatView.height) > mDisplaySize.y) {
                            destinationY = mDisplaySize.y - mFloatView.height
                        }

                        layoutParams.y = destinationY

                        changePosition(shiftX)

                        return true
                    }

                    MotionEvent.ACTION_MOVE -> {

                        layoutParams.x = shiftX + marginX - initX
                        layoutParams.y = shiftY + marginY - initY

                        if (isOnRightSide) {
                            layoutParams.x = mDisplaySize.x - shiftX
                        } else {
                            layoutParams.x = mDisplaySize.x - shiftX - mFloatView.width
                        }


                        layoutParams.y = shiftY - mFloatView.height


//                        if (shiftX > (mDisplaySize.x / 2)) {
//                            layoutParams.x = 0
//                        } else {
//                            layoutParams.x = mDisplaySize.x - mFloatView.width
//                        }
//
//                        layoutParams.y = shiftY - mFloatView.width

                        mWindowManager.updateViewLayout(mFloatView, layoutParams)

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
        if (isInStudy) {
            Toast.makeText(context, "HINTS", Toast.LENGTH_SHORT).show()
            return
        }

        if (isBubbleVisible()) {
//            lockScreen()
            bubbleView.visibility = View.GONE
            acceptTaskView.visibility = View.VISIBLE
        }
    }

    fun lockScreen() {
        val activity = context as Activity
        activity.window.setFlags(
            WindowManager.LayoutParams.FLAG_NOT_TOUCHABLE,
            WindowManager.LayoutParams.FLAG_NOT_TOUCHABLE
        )
    }

    fun unlockScreen() {
        val activity = context as Activity
        activity.window.clearFlags(WindowManager.LayoutParams.FLAG_NOT_TOUCHABLE)
    }

    fun isBubbleVisible(): Boolean {
        return mFloatView.findViewById<RelativeLayout>(R.id.bubble).visibility == View.VISIBLE
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
        val timer = object : CountDownTimer(500, 5) {
            var mParams = mFloatView.layoutParams as WindowManager.LayoutParams

            override fun onTick(millisUntilFinished: Long) {
                var step = (500 - millisUntilFinished) / 5

                mParams.x = mDisplaySize.x + (currentX * currentX * step).toInt() - mFloatView.width
                //mParams.x = mDisplaySize.x + (getMoveValue(step, currentX) - mParams.width).toInt()

                mWindowManager.updateViewLayout(mFloatView, mParams)
            }

            override fun onFinish() {
                mParams.x = mDisplaySize.x
                mWindowManager.updateViewLayout(mFloatView, mParams)
            }
        }

        timer.start()
    }

    fun moveLeft(currentX: Int) {
        var x = mDisplaySize.x - currentX

        val timer = object : CountDownTimer(500, 5) {
            var mParams = mFloatView.layoutParams as WindowManager.LayoutParams

            override fun onTick(millisUntilFinished: Long) {
                var step = (500 - millisUntilFinished) / 5

                mParams.x = 0 - (currentX * currentX * step).toInt()
                //mParams.x = 0 - getMoveValue(step, x).toInt()

                mWindowManager.updateViewLayout(mFloatView, mParams)
            }

            override fun onFinish() {
                mParams.x = 0

                mWindowManager.updateViewLayout(mFloatView, mParams)
            }
        }
        timer.start()
    }

    fun getMoveValue(step: Long, scale: Int): Double {
        val exp = -0.05 * step
        val cos = 0.08 * step
        return scale * exp(exp) * cos(cos)
    }


    fun onDestroy() {
        //super.onDestroy()

        mWindowManager.removeView(mFloatView)
    }

    /*
    TODO ak je otvorene nech sa neda hybat a je lockunty displej
     */
}
