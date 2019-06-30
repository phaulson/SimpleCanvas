package com.phaulson.simplecanvaslib.customView

import android.content.Context
import android.graphics.*
import android.util.AttributeSet
import android.view.MotionEvent
import android.view.View
import com.phaulson.simplecanvaslib.CanvasTouchListener
import com.phaulson.simplecanvaslib.data.DrawItem
import com.phaulson.simplecanvaslib.data.UndoSegment
import com.phaulson.simplecanvaslib.enums.DrawState
import com.phaulson.simplecanvaslib.enums.PathState
import com.phaulson.simplecanvaslib.enums.TouchState
import com.phaulson.simplecanvaslib.scaleGestureDetector.CustomScaleGestureDetector
import com.phaulson.simplecanvaslib.scaleGestureDetector.PinchGestureDetector
import kotlin.math.abs

internal class CanvasView(context: Context, attrs: AttributeSet): View(context, attrs) {
    private val TOUCH_TOLERANCE = 5

    private val drawItems = mutableListOf<DrawItem>()
    private val undoSegments = mutableListOf<UndoSegment>()

    private var bitmap = Bitmap.createBitmap(1, 1, Bitmap.Config.ARGB_8888)
    private var canvas = Canvas(bitmap)

    private val pinchGestureDetector = PinchGestureDetector(this)
    private val scaleGestureDetector = CustomScaleGestureDetector(context, pinchGestureDetector)
    private var currentTouchState = TouchState.NONE

    lateinit var canvasTouchListener: CanvasTouchListener

    var adjustStrokeWidthOnPinch = true
    var showErasingMark = true

    var strokeColor = Color.WHITE
        set(value) {
            field = value
            pinchGestureDetector.circleColor =
                if(drawState == DrawState.DRAWING) value
                else Color.WHITE
        }

    var strokeWidth = 50F
        set(value) {
            field = value
            pinchGestureDetector.adjustStrokeValue(value)
        }

    var drawState = DrawState.DRAWING
        set(value) {
            field = value
            pinchGestureDetector.circleColor =
                if(value == DrawState.DRAWING) strokeColor
                else Color.WHITE
        }

    val pathCount: Int
        get() = drawItems.size

    val paths: Map<Path, Paint>
        get() = drawItems.map { it.path to it.paint  }.toMap()

    init {
        setLayerType(LAYER_TYPE_SOFTWARE, null)
        pinchGestureDetector.circleColor = strokeColor
        pinchGestureDetector.adjustStrokeValue(strokeWidth)
    }

    override fun onSizeChanged(w: Int, h: Int, oldw: Int, oldh: Int) {
        super.onSizeChanged(w, h, oldw, oldh)

        bitmap = Bitmap.createBitmap(w, h, Bitmap.Config.ARGB_8888)
        canvas = Canvas(bitmap)
    }

    override fun onDraw(canvas: Canvas) {
        super.onDraw(canvas)

        drawItems.forEach {
            canvas.drawPath(it.path, it.paint)
        }
    }

    override fun onTouchEvent(event: MotionEvent): Boolean {
        val x = event.x
        val y = event.y

        when(event.action and MotionEvent.ACTION_MASK) {
            MotionEvent.ACTION_DOWN -> {
                if(currentTouchState == TouchState.NONE) {
                    touchStart(x, y)

                    canvasTouchListener.onCanvasTouch(event)
                }
            }
            MotionEvent.ACTION_MOVE -> {
                if(currentTouchState == TouchState.NONE || currentTouchState == TouchState.DRAWING)
                    touchMove(x, y)
                else if(adjustStrokeWidthOnPinch && currentTouchState == TouchState.PINCHING) {
                        scaleGestureDetector.onTouchEvent(event)
                }
            }
            MotionEvent.ACTION_UP -> {
                if(currentTouchState == TouchState.NONE || currentTouchState == TouchState.DRAWING) {
                    touchEnd()

                    canvasTouchListener.onCanvasTouch(event)
                }
                currentTouchState = TouchState.NONE
            }
            MotionEvent.ACTION_POINTER_DOWN -> {
                if(adjustStrokeWidthOnPinch && currentTouchState != TouchState.DRAWING) {
                    scaleGestureDetector.onTouchEvent(event)
                    currentTouchState = TouchState.PINCHING
                    canvasTouchListener.onCanvasTouch(event)

                    if(drawState == DrawState.ERASING && showErasingMark) erasePath(pathCount - 1)
                }
            }
            MotionEvent.ACTION_POINTER_UP -> {
                if(adjustStrokeWidthOnPinch && currentTouchState == TouchState.PINCHING) {
                    scaleGestureDetector.onTouchEvent(event)
                    currentTouchState = TouchState.AFTER_PINCH

                    canvasTouchListener.onCanvasTouch(event)
                }
            }
        }

        invalidate()

        return true
    }

    private fun touchStart(x: Float, y: Float) {
        val paint = Paint()
        paint.isAntiAlias = true
        paint.isDither = true
        paint.color = strokeColor
        paint.style = Paint.Style.STROKE
        paint.strokeJoin = Paint.Join.ROUND
        paint.strokeCap = Paint.Cap.ROUND
        paint.strokeWidth = strokeWidth

        if(drawState == DrawState.ERASING) paint.xfermode = PorterDuffXfermode(PorterDuff.Mode.CLEAR)

        val path = Path()
        path.moveTo(x, y)

        drawItems.add(DrawItem(path, paint, x, y))

        if(drawState == DrawState.ERASING && showErasingMark) drawErasingCircle(x, y)
    }

    private fun touchMove(x: Float, y: Float) {
        if(drawItems.isEmpty()) return

        val drawItem = if(drawState == DrawState.DRAWING || !showErasingMark) drawItems.last() else drawItems[pathCount - 2]
        val dX = abs(x - drawItem.x)
        val dY = abs(y - drawItem.y)

        if (dX >= TOUCH_TOLERANCE || dY >= TOUCH_TOLERANCE) {
            drawItem.path.quadTo(drawItem.x, drawItem.y, (x + drawItem.x) / 2, (y + drawItem.y) / 2)
            drawItem.x = x
            drawItem.y = y
            currentTouchState = TouchState.DRAWING

            if(drawState == DrawState.ERASING && showErasingMark) {
                erasePath(pathCount - 1)
                drawErasingCircle(x, y)
            }
        }
    }

    private fun touchEnd() {
        if(drawItems.isEmpty()) return
        if(drawState == DrawState.ERASING && showErasingMark) erasePath(pathCount - 1)

        val drawItem = drawItems.last()
        val undoSegment = UndoSegment(PathState.DRAWN, drawItem)
        undoSegments.add(undoSegment)

        drawItem.path.lineTo(drawItem.x, drawItem.y)
        canvas.drawPath(drawItem.path, drawItem.paint)
    }

    fun undo() {
        if(undoSegments.isEmpty()) return

        val undoSegment = undoSegments.last()
        when(undoSegment.pathState) {
            PathState.DRAWN -> {
                undoSegments.removeAt(undoSegments.size - 1)
                drawItems.removeIf { it.path == undoSegment.items[0].path }
            }
            PathState.CLEARED -> {
                undoSegments.removeAt(undoSegments.size - 1)
                undoSegment.items.forEach {
                    drawItems.add(it)
                }
            }
        }

        invalidate()
    }

    fun clear() {
        val undoSegment = UndoSegment(PathState.CLEARED)

        drawItems.forEach {
            undoSegment.items.add(it)
        }
        undoSegments.add(undoSegment)

        drawItems.clear()
        invalidate()
    }

    fun drawPath(path: Path, paint: Paint, considerWhenUndo: Boolean) {
        val drawItem = DrawItem(path, paint, 0F, 0F)
        drawItems.add(drawItem)

        if(considerWhenUndo) {
            val undoSegment = UndoSegment(PathState.DRAWN, drawItem)
            undoSegments.add(undoSegment)
        }

        canvas.drawPath(path, paint)
        invalidate()
    }

    fun drawCircle(x: Float, y: Float, radius: Float, paint: Paint, considerWhenUndo: Boolean) {
        val path = Path()
        path.addCircle(x, y, radius, Path.Direction.CW)

        drawPath(path, paint, considerWhenUndo)
    }

    fun erasePath(index: Int) {
        val item = drawItems.removeAt(index)
        undoSegments.removeIf { it.pathState == PathState.DRAWN && it.items[0].path == item.path }

        invalidate()
    }

    private fun drawErasingCircle(x: Float, y: Float) {
        val radius = strokeWidth / 2
        val erasingPaint = Paint()
        erasingPaint.isAntiAlias = true
        erasingPaint.isDither = true
        erasingPaint.strokeJoin = Paint.Join.ROUND
        erasingPaint.strokeCap = Paint.Cap.ROUND
        erasingPaint.style = Paint.Style.FILL
        erasingPaint.color = Color.WHITE
        erasingPaint.setShadowLayer(10.0f, 0.0f, 2.0f, Color.BLACK)

        drawCircle(x, y, radius, erasingPaint, false)
    }
}