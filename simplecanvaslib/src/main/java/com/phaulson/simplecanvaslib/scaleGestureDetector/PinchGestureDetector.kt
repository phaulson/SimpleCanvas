package com.phaulson.simplecanvaslib.scaleGestureDetector

import android.graphics.Color
import android.graphics.Paint
import android.graphics.RadialGradient
import android.graphics.Shader
import android.util.Log
import com.phaulson.simplecanvaslib.customView.CanvasView
import kotlinx.android.synthetic.main.simple_canvas.view.*


internal class PinchGestureDetector(private val canvasView: CanvasView): CustomScaleGestureDetector.SimpleOnScaleGestureListener() {
    private val MAX_STROKE = 300F
    private val MIN_STROKE = 10F
    private val START_STROKE_VALUE = 50F

    private var factor = 1F
    private val paint = Paint()
    private var ended = false

    var circleColor = 0
        set(value) {
            field = value
            paint.color = field
        }

    private var strokeValue = 50F
        set(value) {
            field = value
            canvasView.strokeWidth = field
        }

    init {
        paint.isAntiAlias = true
        paint.isDither = true
        paint.style = Paint.Style.FILL_AND_STROKE
        paint.strokeJoin = Paint.Join.ROUND
        paint.strokeCap = Paint.Cap.ROUND
        paint.setShadowLayer(10.0f, 0.0f, 2.0f, Color.BLACK)
    }

    override fun onScaleBegin(detector: CustomScaleGestureDetector): Boolean {
        if(!ended) {
            canvasView.drawCircle(detector.focusX, detector.focusY, START_STROKE_VALUE * factor, paint, false)
        }
        else {
            ended = false
            return false
        }

        return true
    }

    override fun onScale(detector: CustomScaleGestureDetector): Boolean {
        val newStroke = START_STROKE_VALUE * (factor * detector.scaleFactor)

        if(!ended && newStroke in MIN_STROKE..MAX_STROKE) {
            factor *= detector.scaleFactor

            canvasView.erasePath(canvasView.pathCount - 1)

            strokeValue = newStroke

            canvasView.drawCircle(detector.focusX, detector.focusY, strokeValue / 3 * 2, paint, false)
        }

        return true
    }

    override fun onScaleEnd(detector: CustomScaleGestureDetector) {
        canvasView.erasePath(canvasView.pathCount - 1)
        ended = true
    }

    fun adjustStrokeValue(value: Float) {
        factor = value / START_STROKE_VALUE
    }
}