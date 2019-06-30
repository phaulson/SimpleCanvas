package com.phaulson.simplecanvaslib.customView

import android.animation.ObjectAnimator
import android.animation.PropertyValuesHolder
import android.animation.ValueAnimator
import android.content.Context
import android.content.res.ColorStateList
import android.content.res.Configuration
import android.graphics.Paint
import android.graphics.Path
import android.graphics.drawable.GradientDrawable
import android.support.v4.content.ContextCompat
import android.support.v4.widget.ImageViewCompat
import android.util.AttributeSet
import android.view.MotionEvent
import android.view.View
import android.widget.LinearLayout
import android.widget.RelativeLayout
import com.phaulson.simplecanvaslib.R
import com.phaulson.simplecanvaslib.CanvasTouchListener
import com.phaulson.simplecanvaslib.colorSeekBar.ColorSeekBar.OnColorChangeListener
import com.phaulson.simplecanvaslib.enums.BarPosition
import com.phaulson.simplecanvaslib.enums.DrawState
import kotlinx.android.synthetic.main.simple_canvas.view.*


class SimpleCanvas(context: Context, attrs: AttributeSet): RelativeLayout(context, attrs), CanvasTouchListener {
    val pathCount: Int
        get() = canvas_view.pathCount

    val paths: Map<Path, Paint>
        get() = canvas_view.paths

    var imageSrc = 0
        set(value) {
            field = value
            image.setImageResource(field)
        }

    var strokeColor: Int
        get() = canvas_view.strokeColor
        set(value) {
            canvas_view.strokeColor = value
            if(firstTime) adjustColorOnFirstTime = false
        }

    var strokeWidth: Float
        get() = canvas_view.strokeWidth
        set(value) {
            canvas_view.strokeWidth = value
        }

    var bgColor = 0
        set(value) {
            field = value
            val back = GradientDrawable()
            back.setColor(bgColor)
            background = back
        }

    var adjustStrokeWidthOnPinch: Boolean
        get() = canvas_view.adjustStrokeWidthOnPinch
        set(value) {
            canvas_view.adjustStrokeWidthOnPinch = value
        }

    var showColorSeekBar = true
        set(value) {
            field = value
            colorSeekBarContainer.visibility = if(value) View.VISIBLE else View.GONE
            adjustButtonBarPosition(buttonBarPosition)
        }

    var showUndoButton = true
        set(value) {
            field = value
            undo.visibility = if(value) View.VISIBLE else View.GONE
        }

    var showClearButton = true
        set(value) {
            field = value
            trash.visibility = if(value) View.VISIBLE else View.GONE
        }

    var showDrawingErasingToggle = true
        set(value) {
            field = value
            drawing_erasing.visibility = if(value) View.VISIBLE else View.GONE
        }

    var showErasingMark: Boolean
        get() = canvas_view.showErasingMark
        set(value) {
            canvas_view.showErasingMark = value
        }

    var undoButtonColor = 0
        set(value) {
            field = value
            ImageViewCompat.setImageTintList(undo, ColorStateList.valueOf(value));
        }

    var clearButtonColor = 0
        set(value) {
            field = value
            ImageViewCompat.setImageTintList(trash, ColorStateList.valueOf(value));
        }

    var drawingErasingToggleColor = 0
        set(value) {
            field = value
            ImageViewCompat.setImageTintList(drawing_erasing, ColorStateList.valueOf(value));
        }

    var colorSeekBarPosition = BarPosition.VERTICAL_TOP_RIGHT
        set(value) {
            field = value
            adjustColorSeekBarPosition(value)
            adjustButtonBarPosition(buttonBarPosition)
        }

    var buttonBarPosition = BarPosition.VERTICAL_TOP_RIGHT
        set(value) {
            field = value
            adjustButtonBarPosition(value)
        }

    var drawState: DrawState
        get() = canvas_view.drawState
        set(value) {
            canvas_view.drawState = value
        }

    var hideColorSeekBarOnTouch = true
    var hideButtonBarOnTouch = true

    private var firstTime = true
    private var adjustColorOnFirstTime = true

    init {
        inflate(context, R.layout.simple_canvas, this)

        //For some reason it won't work without it
        colorSeekBarPlaceHolder.setOnColorChangeListener(object : OnColorChangeListener {
            override fun onColorChangeListener(colorBarPosition: Int, alphaBarPosition: Int, color: Int) {
                //Toast.makeText(context, "lel", Toast.LENGTH_SHORT).show()
            }
        })

        context.theme.obtainStyledAttributes(attrs, R.styleable.simple_canvas, 0, 0)
            .apply {
                try {
                    imageSrc = getResourceId(R.styleable.simple_canvas_image_src, 0)
                    strokeColor = getColor(R.styleable.simple_canvas_stroke_color, -1)
                    strokeWidth = getFloat(R.styleable.simple_canvas_stroke_width, 50F)
                    bgColor = getColor(R.styleable.simple_canvas_bg_color, 0)
                    colorSeekBar.setColorSeeds(getResourceId(R.styleable.simple_canvas_color_seek_bar_colors, R.array.color_500))

                    adjustStrokeWidthOnPinch = getBoolean(R.styleable.simple_canvas_adjust_stroke_width_on_pinch, true)
                    showColorSeekBar = getBoolean(R.styleable.simple_canvas_show_color_seek_bar, true)
                    showUndoButton = getBoolean(R.styleable.simple_canvas_show_undo_button, true)
                    showClearButton = getBoolean(R.styleable.simple_canvas_show_clear_button, true)
                    showDrawingErasingToggle = getBoolean(R.styleable.simple_canvas_show_drawing_erasing_toggle, true)
                    showErasingMark = getBoolean(R.styleable.simple_canvas_show_erasing_mark, true)

                    undoButtonColor = getColor(R.styleable.simple_canvas_undo_button_color, ContextCompat.getColor(context, android.R.color.holo_blue_light))
                    clearButtonColor = getColor(R.styleable.simple_canvas_clear_button_color, ContextCompat.getColor(context, android.R.color.holo_blue_light))
                    drawingErasingToggleColor = getColor(R.styleable.simple_canvas_drawing_erasing_toggle_color, ContextCompat.getColor(context, android.R.color.holo_blue_light))

                    hideColorSeekBarOnTouch = getBoolean(R.styleable.simple_canvas_hide_color_seek_bar_on_touch, true)
                    hideButtonBarOnTouch = getBoolean(R.styleable.simple_canvas_hide_button_bar_on_touch, true)

                    setColorSeekBarPosition(getInt(R.styleable.simple_canvas_color_seek_bar_position, 0))
                    setButtonBarPosition(getInt(R.styleable.simple_canvas_button_bar_position, 4))
                }
                finally {
                    recycle()
                }
            }

        colorSeekBar.setOnColorChangeListener(object : OnColorChangeListener {
            override fun onColorChangeListener(colorBarPosition: Int, alphaBarPosition: Int, color: Int) {
                if(!firstTime || adjustColorOnFirstTime)
                    strokeColor = color
                firstTime = false
            }
        })

        addButtonEvents()

        canvas_view.canvasTouchListener = this
    }

    override fun onConfigurationChanged(newConfig: Configuration) {
        super.onConfigurationChanged(newConfig)
        adjustColorSeekBarPosition(colorSeekBarPosition)
        adjustButtonBarPosition(buttonBarPosition)
    }

    override fun onCanvasTouch(event: MotionEvent) {
        when(event.action and MotionEvent.ACTION_MASK) {
            MotionEvent.ACTION_DOWN, MotionEvent.ACTION_POINTER_DOWN -> {
                if(hideColorSeekBarOnTouch) colorSeekBarContainer.visibility = View.INVISIBLE
                if(hideButtonBarOnTouch) buttonContainer.visibility = View.INVISIBLE
            }
            MotionEvent.ACTION_UP, MotionEvent.ACTION_POINTER_UP -> {
                colorSeekBarContainer.visibility = View.VISIBLE
                buttonContainer.visibility = View.VISIBLE
            }
        }
    }

    fun undo() {
        canvas_view.undo()
    }

    fun clear() {
        canvas_view.clear()
    }

    fun drawPath(path: Path, paint: Paint, considerWhenUndo: Boolean = true) {
        canvas_view.drawPath(path, paint, considerWhenUndo)
    }

    fun drawCircle(x: Float, y: Float, radius: Float, paint: Paint, considerWhenUndo: Boolean = true) {
        canvas_view.drawCircle(x, y, radius, paint, considerWhenUndo)
    }

    fun erasePath(index: Int) {
        canvas_view.erasePath(index)
    }

    fun setColorSeekBarColors(res: Int) {
        colorSeekBar.setColorSeeds(res)
    }

    fun setColorSeekBarColors(res: IntArray) {
        colorSeekBar.setColorSeeds(res)
    }

    private fun setColorSeekBarPosition(pos: Int) {
        when(pos) {
            0 -> colorSeekBarPosition = BarPosition.VERTICAL_TOP_RIGHT
            1 -> colorSeekBarPosition = BarPosition.VERTICAL_BOTTOM_RIGHT
            2 -> colorSeekBarPosition = BarPosition.VERTICAL_TOP_LEFT
            3 -> colorSeekBarPosition = BarPosition.VERTICAL_BOTTOM_LEFT
            4 -> colorSeekBarPosition = BarPosition.HORIZONTAL_TOP_RIGHT
            5 -> colorSeekBarPosition = BarPosition.HORIZONTAL_BOTTOM_RIGHT
            6 -> colorSeekBarPosition = BarPosition.HORIZONTAL_TOP_LEFT
            7 -> colorSeekBarPosition = BarPosition.HORIZONTAL_BOTTOM_LEFT
        }
    }

    private fun setButtonBarPosition(pos: Int) {
        when(pos) {
            0 -> buttonBarPosition = BarPosition.VERTICAL_TOP_RIGHT
            1 -> buttonBarPosition = BarPosition.VERTICAL_BOTTOM_RIGHT
            2 -> buttonBarPosition = BarPosition.VERTICAL_TOP_LEFT
            3 -> buttonBarPosition = BarPosition.VERTICAL_BOTTOM_LEFT
            4 -> buttonBarPosition = BarPosition.HORIZONTAL_TOP_RIGHT
            5 -> buttonBarPosition = BarPosition.HORIZONTAL_BOTTOM_RIGHT
            6 -> buttonBarPosition = BarPosition.HORIZONTAL_TOP_LEFT
            7 -> buttonBarPosition = BarPosition.HORIZONTAL_BOTTOM_LEFT
        }
    }

    private fun adjustColorSeekBarPosition(position: BarPosition) {
        val params = colorSeekBarContainer.layoutParams as LayoutParams
        params.removeRule(ALIGN_PARENT_START)
        params.removeRule(ALIGN_PARENT_END)
        params.removeRule(ALIGN_PARENT_TOP)
        params.removeRule(ALIGN_PARENT_BOTTOM)

        colorSeekBarContainer.removeAllViews()

        when(position) {
            BarPosition.VERTICAL_TOP_RIGHT -> {
                colorSeekBarContainer.orientation = LinearLayout.VERTICAL
                colorSeekBar.layoutParams.height = 0
                colorSeekBar.layoutParams.width = LayoutParams.WRAP_CONTENT

                colorSeekBarContainer.addView(colorSeekBar)

                if(resources.configuration.orientation == Configuration.ORIENTATION_PORTRAIT) {
                    colorSeekBarPlaceHolder.layoutParams.width = LayoutParams.WRAP_CONTENT
                    colorSeekBarPlaceHolder.layoutParams.height = 0
                    colorSeekBarContainer.addView(colorSeekBarPlaceHolder)
                }

                colorSeekBar.setVertical(true)
                params.addRule(ALIGN_PARENT_END)
            }
            BarPosition.VERTICAL_BOTTOM_RIGHT -> {
                colorSeekBarContainer.orientation = LinearLayout.VERTICAL

                colorSeekBar.layoutParams.width = LayoutParams.WRAP_CONTENT
                colorSeekBar.layoutParams.height = 0

                if(resources.configuration.orientation == Configuration.ORIENTATION_PORTRAIT) {
                    colorSeekBarPlaceHolder.layoutParams.width = LayoutParams.WRAP_CONTENT
                    colorSeekBarPlaceHolder.layoutParams.height = 0
                    colorSeekBarContainer.addView(colorSeekBarPlaceHolder)
                }

                colorSeekBarContainer.addView(colorSeekBar)

                colorSeekBar.setVertical(true)
                params.addRule(ALIGN_PARENT_END)
            }
            BarPosition.VERTICAL_TOP_LEFT -> {
                colorSeekBarContainer.orientation = LinearLayout.VERTICAL

                colorSeekBar.layoutParams.width = LayoutParams.WRAP_CONTENT
                colorSeekBar.layoutParams.height = 0

                colorSeekBarContainer.addView(colorSeekBar)

                if(resources.configuration.orientation == Configuration.ORIENTATION_PORTRAIT) {
                    colorSeekBarPlaceHolder.layoutParams.width = LayoutParams.WRAP_CONTENT
                    colorSeekBarPlaceHolder.layoutParams.height = 0
                    colorSeekBarContainer.addView(colorSeekBarPlaceHolder)
                }

                colorSeekBar.setVertical(true)
                params.addRule(ALIGN_PARENT_START)
            }
            BarPosition.VERTICAL_BOTTOM_LEFT -> {
                colorSeekBarContainer.orientation = LinearLayout.VERTICAL

                colorSeekBar.layoutParams.width = LayoutParams.WRAP_CONTENT
                colorSeekBar.layoutParams.height = 0

                if(resources.configuration.orientation == Configuration.ORIENTATION_PORTRAIT) {
                    colorSeekBarPlaceHolder.layoutParams.width = LayoutParams.WRAP_CONTENT
                    colorSeekBarPlaceHolder.layoutParams.height = 0
                    colorSeekBarContainer.addView(colorSeekBarPlaceHolder)
                }

                colorSeekBarContainer.addView(colorSeekBar)

                colorSeekBar.setVertical(true)
                params.addRule(ALIGN_PARENT_START)
            }
            BarPosition.HORIZONTAL_TOP_RIGHT -> {
                colorSeekBarContainer.orientation = LinearLayout.HORIZONTAL

                colorSeekBar.layoutParams.width = 0
                colorSeekBar.layoutParams.height = 150

                if(resources.configuration.orientation == Configuration.ORIENTATION_LANDSCAPE) {
                    colorSeekBarPlaceHolder.layoutParams.width = 0
                    colorSeekBarPlaceHolder.layoutParams.height = 150
                    colorSeekBarContainer.addView(colorSeekBarPlaceHolder)
                }

                colorSeekBarContainer.addView(colorSeekBar)

                colorSeekBar.setVertical(false)
                params.addRule(ALIGN_PARENT_TOP)
            }
            BarPosition.HORIZONTAL_BOTTOM_RIGHT -> {
                colorSeekBarContainer.orientation = LinearLayout.HORIZONTAL

                colorSeekBar.layoutParams.width = 0
                colorSeekBar.layoutParams.height = 150

                if(resources.configuration.orientation == Configuration.ORIENTATION_LANDSCAPE) {
                    colorSeekBarPlaceHolder.layoutParams.width = 0
                    colorSeekBarPlaceHolder.layoutParams.height = 150
                    colorSeekBarContainer.addView(colorSeekBarPlaceHolder)
                }

                colorSeekBarContainer.addView(colorSeekBar)

                colorSeekBar.setVertical(false)
                params.addRule(ALIGN_PARENT_BOTTOM)
            }
            BarPosition.HORIZONTAL_TOP_LEFT -> {
                colorSeekBarContainer.orientation = LinearLayout.HORIZONTAL

                colorSeekBar.layoutParams.width = 0
                colorSeekBar.layoutParams.height = 150

                colorSeekBarContainer.addView(colorSeekBar)

                if(resources.configuration.orientation == Configuration.ORIENTATION_LANDSCAPE) {
                    colorSeekBarPlaceHolder.layoutParams.width = 0
                    colorSeekBarPlaceHolder.layoutParams.height = 150
                    colorSeekBarContainer.addView(colorSeekBarPlaceHolder)
                }

                colorSeekBar.setVertical(false)
                params.addRule(ALIGN_PARENT_TOP)
            }
            BarPosition.HORIZONTAL_BOTTOM_LEFT -> {
                colorSeekBarContainer.orientation = LinearLayout.HORIZONTAL

                colorSeekBar.layoutParams.width = 0
                colorSeekBar.layoutParams.height = 150

                colorSeekBarContainer.addView(colorSeekBar)

                if(resources.configuration.orientation == Configuration.ORIENTATION_LANDSCAPE) {
                    colorSeekBarPlaceHolder.layoutParams.width = 0
                    colorSeekBarPlaceHolder.layoutParams.height = 150
                    colorSeekBarContainer.addView(colorSeekBarPlaceHolder)
                }

                colorSeekBar.setVertical(false)
                params.addRule(ALIGN_PARENT_BOTTOM)
            }
        }
        colorSeekBarContainer.layoutParams = params
    }

    private fun adjustButtonBarPosition(position: BarPosition) {
        val params = buttonContainer.layoutParams as LayoutParams
        params.removeRule(ALIGN_PARENT_START)
        params.removeRule(ALIGN_PARENT_END)
        params.removeRule(ALIGN_PARENT_TOP)
        params.removeRule(ALIGN_PARENT_BOTTOM)
        params.removeRule(START_OF)
        params.removeRule(END_OF)
        params.removeRule(BELOW)
        params.removeRule(ABOVE)

        when(position) {
            BarPosition.VERTICAL_TOP_RIGHT, BarPosition.HORIZONTAL_TOP_RIGHT -> {
                if(position == BarPosition.VERTICAL_TOP_RIGHT)
                    buttonContainer.orientation = LinearLayout.VERTICAL
                else buttonContainer.orientation = LinearLayout.HORIZONTAL

                if(showColorSeekBar &&
                    (colorSeekBarPosition == BarPosition.VERTICAL_TOP_RIGHT ||
                            colorSeekBarPosition == BarPosition.VERTICAL_BOTTOM_RIGHT &&
                            resources.configuration.orientation == Configuration.ORIENTATION_LANDSCAPE)) {
                    params.addRule(START_OF, colorSeekBarContainer.id)
                }
                else params.addRule(ALIGN_PARENT_END)

                if(showColorSeekBar &&
                    (colorSeekBarPosition == BarPosition.HORIZONTAL_TOP_RIGHT) ||
                        colorSeekBarPosition == BarPosition.HORIZONTAL_TOP_LEFT &&
                        resources.configuration.orientation == Configuration.ORIENTATION_PORTRAIT) {
                    params.addRule(BELOW, colorSeekBarContainer.id)
                }
                else params.addRule(ALIGN_PARENT_TOP)
            }
            BarPosition.VERTICAL_BOTTOM_RIGHT, BarPosition.HORIZONTAL_BOTTOM_RIGHT -> {
                if(position == BarPosition.VERTICAL_BOTTOM_RIGHT)
                    buttonContainer.orientation = LinearLayout.VERTICAL
                else buttonContainer.orientation = LinearLayout.HORIZONTAL

                if(showColorSeekBar &&
                    (colorSeekBarPosition == BarPosition.VERTICAL_BOTTOM_RIGHT ||
                            colorSeekBarPosition == BarPosition.VERTICAL_TOP_RIGHT &&
                            resources.configuration.orientation == Configuration.ORIENTATION_LANDSCAPE)) {
                    params.addRule(START_OF, colorSeekBarContainer.id)
                }
                else params.addRule(ALIGN_PARENT_END)

                if(showColorSeekBar &&
                    (colorSeekBarPosition == BarPosition.HORIZONTAL_BOTTOM_RIGHT) ||
                    colorSeekBarPosition == BarPosition.HORIZONTAL_BOTTOM_LEFT &&
                    resources.configuration.orientation == Configuration.ORIENTATION_PORTRAIT) {
                    params.addRule(ABOVE, colorSeekBarContainer.id)
                }
                else params.addRule(ALIGN_PARENT_BOTTOM)
            }
            BarPosition.VERTICAL_TOP_LEFT, BarPosition.HORIZONTAL_TOP_LEFT -> {
                if(position == BarPosition.VERTICAL_TOP_LEFT)
                    buttonContainer.orientation = LinearLayout.VERTICAL
                else buttonContainer.orientation = LinearLayout.HORIZONTAL

                if(showColorSeekBar &&
                    (colorSeekBarPosition == BarPosition.VERTICAL_TOP_LEFT ||
                            colorSeekBarPosition == BarPosition.VERTICAL_BOTTOM_LEFT &&
                            resources.configuration.orientation == Configuration.ORIENTATION_LANDSCAPE)) {
                    params.addRule(END_OF, colorSeekBarContainer.id)
                }
                else params.addRule(ALIGN_PARENT_START)

                if(showColorSeekBar &&
                    (colorSeekBarPosition == BarPosition.HORIZONTAL_TOP_LEFT) ||
                    colorSeekBarPosition == BarPosition.HORIZONTAL_TOP_RIGHT &&
                    resources.configuration.orientation == Configuration.ORIENTATION_PORTRAIT) {
                    params.addRule(BELOW, colorSeekBarContainer.id)
                }
                else params.addRule(ALIGN_PARENT_TOP)
            }
            BarPosition.VERTICAL_BOTTOM_LEFT, BarPosition.HORIZONTAL_BOTTOM_LEFT -> {
                if(position == BarPosition.VERTICAL_BOTTOM_LEFT)
                    buttonContainer.orientation = LinearLayout.VERTICAL
                else buttonContainer.orientation = LinearLayout.HORIZONTAL

                if(showColorSeekBar &&
                    (colorSeekBarPosition == BarPosition.VERTICAL_BOTTOM_LEFT ||
                            colorSeekBarPosition == BarPosition.VERTICAL_TOP_LEFT &&
                            resources.configuration.orientation == Configuration.ORIENTATION_LANDSCAPE)) {
                    params.addRule(END_OF, colorSeekBarContainer.id)
                }
                else params.addRule(ALIGN_PARENT_START)

                if(showColorSeekBar &&
                    (colorSeekBarPosition == BarPosition.HORIZONTAL_BOTTOM_LEFT) ||
                    colorSeekBarPosition == BarPosition.HORIZONTAL_BOTTOM_RIGHT &&
                    resources.configuration.orientation == Configuration.ORIENTATION_PORTRAIT) {
                    params.addRule(ABOVE, colorSeekBarContainer.id)
                }
                else params.addRule(ALIGN_PARENT_BOTTOM)
            }
        }
        buttonContainer.layoutParams = params
    }

    private fun addButtonEvents() {
        undo.setOnClickListener {
            pulseAnimation(it)
            undo()
        }

        trash.setOnClickListener {
            pulseAnimation(it)
            clear()
        }

        drawing_erasing.setOnClickListener {
            pulseAnimation(it)
            drawState = if(drawState == DrawState.DRAWING) {
                drawing_erasing.setImageResource(R.drawable.rubber)
                DrawState.ERASING
            } else {
                drawing_erasing.setImageResource(R.drawable.pen)
                DrawState.DRAWING
            }
        }
    }

    private fun pulseAnimation(view: View) {
        val scaleDown = ObjectAnimator.ofPropertyValuesHolder(
            view,
            PropertyValuesHolder.ofFloat("scaleX", 1.2f),
            PropertyValuesHolder.ofFloat("scaleY", 1.2f)
        )
        scaleDown.duration = 100

        scaleDown.repeatCount = 1
        scaleDown.repeatMode = ValueAnimator.REVERSE

        scaleDown.start()
    }
}