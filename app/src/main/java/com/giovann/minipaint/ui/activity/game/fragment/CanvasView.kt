package com.giovann.minipaint.ui.activity.game.fragment

import android.content.Context
import android.graphics.Bitmap
import android.graphics.Canvas
import android.graphics.Paint
import android.graphics.Path
import android.view.MotionEvent
import android.view.View
import androidx.core.content.res.ResourcesCompat
import com.giovann.minipaint.R
import com.giovann.minipaint.model.MovementCoordinate
import com.giovann.minipaint.utils.Constants

class CanvasView(context: Context) : View(context) {

    interface CanvasListener {
        fun sendMovementData(data: MutableList<MovementCoordinate>)
    }

    private lateinit var extraCanvas: Canvas
    private lateinit var extraBitmap: Bitmap
    private val backgroundColor = ResourcesCompat.getColor(resources, R.color.colorBackground, null)
    private val drawColor = ResourcesCompat.getColor(resources, R.color.colorPaint, null)

    private val data = mutableListOf<MovementCoordinate>()

    private var path = Path()
    private var motionTouchEventX = 0f
    private var motionTouchEventY = 0f
    private var currentX = 0f
    private var currentY = 0f

    private val paint = Paint().apply {
        color = drawColor
        isAntiAlias = true
        isDither = true
        style = Paint.Style.STROKE
        strokeJoin = Paint.Join.ROUND
        strokeCap = Paint.Cap.ROUND
        strokeWidth = Constants.STROKE_WIDTH
    }

    override fun onSizeChanged(w: Int, h: Int, oldw: Int, oldh: Int) {
        super.onSizeChanged(w, h, oldw, oldh)

        initializeBitmap()
    }

    override fun onDraw(canvas: Canvas) {
        super.onDraw(canvas)

        canvas.drawBitmap(extraBitmap, 0f, 0f, null)
    }

    override fun onTouchEvent(event: MotionEvent): Boolean {
        motionTouchEventX = event.x
        motionTouchEventY = event.y

        when (event.action) {
            MotionEvent.ACTION_DOWN -> touchStart()
            MotionEvent.ACTION_MOVE -> touchMove()
            MotionEvent.ACTION_UP -> touchUp()
        }

        return true
    }

    private fun touchStart() {
        path.reset()
        path.moveTo(motionTouchEventX, motionTouchEventY)
        currentX = motionTouchEventX
        currentY = motionTouchEventY
    }

    private fun touchMove() {
        path.quadTo(currentX, currentY, (motionTouchEventX + currentX) / 2, (motionTouchEventY + currentY) / 2)

        data.add(
            MovementCoordinate(currentX, motionTouchEventX, currentY, motionTouchEventY)
        )

        currentX = motionTouchEventX
        currentY = motionTouchEventY
        extraCanvas.drawPath(path, paint)
        invalidate()
    }

    private fun touchUp() {
        mListener!!.sendMovementData(data)
        data.clear()

        path.reset()
    }

    private fun initializeBitmap() {
        if (::extraBitmap.isInitialized) extraBitmap.recycle()
        extraBitmap = Bitmap.createBitmap(width, height, Bitmap.Config.ARGB_8888)
        extraCanvas = Canvas(extraBitmap)
        extraCanvas.drawColor(backgroundColor)
    }

    fun syncDrawing(input: List<MovementCoordinate>) {
        path.reset()
        path.moveTo(input[0].currentX, input[0].currentY)
        input.forEach {
            path.quadTo(it.currentX, it.currentY, (it.endX + it.currentX) / 2, (it.endY + it.currentY) / 2)
            extraCanvas.drawPath(path, paint)
        }
        path.reset()
        invalidate()
    }

    fun clearCanvas() {
        initializeBitmap()
        invalidate()
    }

    companion object {
        private var mListener: CanvasListener? = null
        fun newInstance(listener: CanvasListener, context: Context): CanvasView {
            mListener = listener
            return CanvasView(context)
        }
    }
}