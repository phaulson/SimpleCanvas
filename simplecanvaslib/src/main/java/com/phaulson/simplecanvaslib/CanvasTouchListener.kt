package com.phaulson.simplecanvaslib

import android.view.MotionEvent

interface CanvasTouchListener {
    fun onCanvasTouch(event: MotionEvent)
}