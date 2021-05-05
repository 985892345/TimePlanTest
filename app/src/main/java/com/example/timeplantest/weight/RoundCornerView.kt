package com.example.timeplantest.weight

import android.annotation.SuppressLint
import android.content.Context
import android.graphics.Canvas
import android.graphics.Paint
import android.util.AttributeSet
import android.view.MotionEvent
import android.view.View

/**
 *@author 985892345
 *@email 2767465918@qq.com
 *@data 2021/5/3
 *@description
 */
class RoundCornerView(context: Context, attrs: AttributeSet) : View(context, attrs) {

    fun setColor(color: Int) {
        post {
            mPaint.color = color
            invalidate()
        }
    }

    fun getColor(): Int {
        return mPaint.color
    }

    fun setOnClick(onClick: ((color: Int) -> Unit)) {
        mOnClick = onClick
    }

    private val mPaint = Paint()
    private val mRadius = 10
    private var mOnClick: ((color: Int) -> Unit)? = null

    init {
        mPaint.isAntiAlias = true
        mPaint.strokeWidth = 10f
        mPaint.color = 0xFFDCCC48.toInt()
        mPaint.style = Paint.Style.FILL
        isClickable = true
    }

    override fun onDraw(canvas: Canvas) {
        canvas.drawRoundRect(
            0f,
            0f,
            width.toFloat(),
            height.toFloat(),
            mRadius.toFloat(),
            mRadius.toFloat(),
            mPaint
        )
    }

    override fun onMeasure(widthMeasureSpec: Int, heightMeasureSpec: Int) {
        var newWidthMeasureSpec = widthMeasureSpec
        var newHheightMeasureSpec = heightMeasureSpec
        when (MeasureSpec.getMode(newWidthMeasureSpec)) {
            MeasureSpec.UNSPECIFIED, MeasureSpec.AT_MOST -> newWidthMeasureSpec =
                MeasureSpec.makeMeasureSpec(50, MeasureSpec.EXACTLY)
            MeasureSpec.EXACTLY -> {
            }
        }
        when (MeasureSpec.getMode(newHheightMeasureSpec)) {
            MeasureSpec.UNSPECIFIED, MeasureSpec.AT_MOST -> newHheightMeasureSpec =
                MeasureSpec.makeMeasureSpec(50, MeasureSpec.EXACTLY)
            MeasureSpec.EXACTLY -> {
            }
        }
        super.onMeasure(newWidthMeasureSpec, newHheightMeasureSpec)
    }

    @SuppressLint("ClickableViewAccessibility")
    override fun onTouchEvent(event: MotionEvent): Boolean {
        if (event.action == MotionEvent.ACTION_UP) {
            mOnClick?.invoke(getColor())
        }
        return super.onTouchEvent(event)
    }
}