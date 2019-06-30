package com.phaulson.simplecanvaslib.colorSeekBar

import com.phaulson.simplecanvaslib.R
import android.annotation.TargetApi
import android.content.Context
import android.content.res.TypedArray
import android.graphics.Bitmap
import android.graphics.Canvas
import android.graphics.Color
import android.graphics.LinearGradient
import android.graphics.Paint
import android.graphics.RadialGradient
import android.graphics.Rect
import android.graphics.Shader
import android.os.Build
import android.support.annotation.ArrayRes
import android.support.v4.content.ContextCompat
import android.util.AttributeSet
import android.view.MotionEvent
import android.view.View

import java.util.ArrayList


internal class ColorSeekBar : View {
    private var mBackgroundColor = -0x1
    private var mColorSeeds = intArrayOf(
        -0x1000000,
        -0x66ff01,
        -0xffff01,
        -0xff0100,
        -0xff0001,
        -0x10000,
        -0xff01,
        -0x9a00,
        -0x100,
        -0x1,
        -0x1000000
    )
    private var c0: Int = 0
    private var c1: Int = 0
    var alphaValue: Int = 0
        private set
    private var mRed: Int = 0
    private var mGreen: Int = 0
    private var mBlue: Int = 0
    private var mx: Float = 0F
    private var my: Float = 0F
    private var mOnColorChangeLister: OnColorChangeListener? = null
    private var mContext: Context? = null
    private var mIsShowAlphaBar = false

    var isVertical: Boolean = false
        private set
    private var mMovingColorBar: Boolean = false
    private var mMovingAlphaBar: Boolean = false
    private var mTransparentBitmap: Bitmap? = null
    private var mColorRect: Rect? = null
    var thumbHeight = 20
        private set
    private var mThumbRadius: Float = 0.toFloat()
    var barHeight = 2
        private set
    private var mColorGradient: LinearGradient? = null
    private var mColorRectPaint: Paint? = null
    private var realLeft: Int = 0
    private var realRight: Int = 0
    private var realTop: Int = 0
    private var realBottom: Int = 0
    private var mBarWidth: Int = 0
    var maxValue: Int = 0
        private set
    private var mAlphaRect: Rect? = null
    private var mColorBarPosition: Int = 0
    private var mAlphaBarPosition: Int = 0
    var barMargin = 5
        private set
    private var mPaddingSize: Int = 0
    private var mViewWidth: Int = 0
    private var mViewHeight: Int = 0
    /***
     *
     * @param alphaMinPosition >=0 && < alphaMaxPosition
     */
    var alphaMinPosition = 0
        set(alphaMinPosition) {
            field = alphaMinPosition
            if (this.alphaMinPosition >= alphaMaxPosition) {
                field = alphaMaxPosition - 1
            } else if (this.alphaMinPosition < 0) {
                field = 0
            }

            if (mAlphaBarPosition < this.alphaMinPosition) {
                mAlphaBarPosition = this.alphaMinPosition
            }
            invalidate()
        }
    /***
     *
     * @param alphaMaxPosition <= 255 && > alphaMinPosition
     */
    var alphaMaxPosition = 255
        set(alphaMaxPosition) {
            field = alphaMaxPosition
            if (this.alphaMaxPosition > 255) {
                field = 255
            } else if (this.alphaMaxPosition <= alphaMinPosition) {
                field = alphaMinPosition + 1
            }

            if (mAlphaBarPosition > alphaMinPosition) {
                mAlphaBarPosition = this.alphaMaxPosition
            }
            invalidate()
        }
    private val mColors = ArrayList<Int>()
    private var mColorsToInvoke = -1
    private var mInit = false
    /**
     * @return
     */
    @get:Deprecated("use {@link #setOnInitDoneListener(OnInitDoneListener)} instead.")
    var isFirstDraw = true
        private set
    private var mOnInitDoneListener: OnInitDoneListener? = null

    /**
     * Set color, it must correspond to the value, if not , setColorBarPosition(0);
     *
     * @paam color
     */
    //            mColorsToInvoke = color;
    var color: Int
        get() = getColor(mIsShowAlphaBar)
        set(color) {
            val withoutAlphaColor = Color.rgb(Color.red(color), Color.green(color), Color.blue(color))

            if (mInit) {
                val value = mColors.indexOf(withoutAlphaColor)
                setColorBarPosition(value)
            } else {
                mColorsToInvoke = color
            }

        }

    var alphaBarPosition: Int
        get() = mAlphaBarPosition
        set(value) {
            this.mAlphaBarPosition = value
            setAlphaValue()
            invalidate()
        }

    val colors: List<Int>
        get() = mColors

    var isShowAlphaBar: Boolean
        get() = mIsShowAlphaBar
        set(show) {
            mIsShowAlphaBar = show
            refreshLayoutParams()
            invalidate()
            if (mOnColorChangeLister != null)
                mOnColorChangeLister!!.onColorChangeListener(mColorBarPosition, mAlphaBarPosition, color)
        }

    val colorBarValue: Float
        get() = mColorBarPosition.toFloat()

    constructor(context: Context) : super(context) {
        init(context, null, 0, 0)
    }

    constructor(context: Context, attrs: AttributeSet) : super(context, attrs) {
        init(context, attrs, 0, 0)
    }

    constructor(context: Context, attrs: AttributeSet, defStyleAttr: Int) : super(context, attrs, defStyleAttr) {
        init(context, attrs, defStyleAttr, 0)
    }

    @TargetApi(Build.VERSION_CODES.LOLLIPOP)
    constructor(context: Context, attrs: AttributeSet, defStyleAttr: Int, defStyleRes: Int) : super(
        context,
        attrs,
        defStyleAttr,
        defStyleRes
    ) {
        init(context, attrs, defStyleAttr, defStyleRes)
    }

    protected fun init(context: Context, attrs: AttributeSet?, defStyleAttr: Int, defStyleRes: Int) {
        applyStyle(context, attrs, defStyleAttr, defStyleRes)
    }

    fun applyStyle(resId: Int) {
        applyStyle(context, null, 0, resId)
    }

    override fun onMeasure(widthMeasureSpec: Int, heightMeasureSpec: Int) {
        super.onMeasure(widthMeasureSpec, heightMeasureSpec)
        //Logger.i("onMeasure")
        mViewWidth = widthMeasureSpec
        mViewHeight = heightMeasureSpec

        val widthSpeMode = View.MeasureSpec.getMode(widthMeasureSpec)
        val heightSpeMode = View.MeasureSpec.getMode(heightMeasureSpec)

        val barHeight = if (mIsShowAlphaBar) this.barHeight * 2 else this.barHeight
        val thumbHeight = if (mIsShowAlphaBar) this.thumbHeight * 2 else this.thumbHeight

        //Logger.i("widthSpeMode:")
        //Logger.spec(widthSpeMode)
        //Logger.i("heightSpeMode:")
        //Logger.spec(heightSpeMode)

        if (isVertical) {
            if (widthSpeMode == View.MeasureSpec.AT_MOST || widthSpeMode == View.MeasureSpec.UNSPECIFIED) {
                mViewWidth = thumbHeight + barHeight + barMargin
                setMeasuredDimension(mViewWidth, mViewHeight)
            }

        } else {
            if (widthSpeMode == View.MeasureSpec.AT_MOST || widthSpeMode == View.MeasureSpec.UNSPECIFIED) {
                mViewHeight = thumbHeight + barHeight + barMargin
                setMeasuredDimension(mViewWidth, mViewHeight)
            }
        }
    }

    protected fun applyStyle(context: Context, attrs: AttributeSet?, defStyleAttr: Int, defStyleRes: Int) {
        mContext = context
        //get attributes
        val a = context.obtainStyledAttributes(attrs, R.styleable.ColorSeekBar, defStyleAttr, defStyleRes)
        val colorsId = a.getResourceId(R.styleable.ColorSeekBar_colorSeeds, 0)
        maxValue = a.getInteger(R.styleable.ColorSeekBar_maxPosition, 100)
        mColorBarPosition = a.getInteger(R.styleable.ColorSeekBar_colorBarPosition, 0)
        mAlphaBarPosition = a.getInteger(R.styleable.ColorSeekBar_alphaBarPosition, alphaMinPosition)
        isVertical = a.getBoolean(R.styleable.ColorSeekBar_isVertical, false)
        mIsShowAlphaBar = a.getBoolean(R.styleable.ColorSeekBar_showAlphaBar, false)
        mBackgroundColor = a.getColor(R.styleable.ColorSeekBar_bgColor, Color.TRANSPARENT)
        barHeight = a.getDimension(R.styleable.ColorSeekBar_barHeight, dp2px(2f).toFloat()).toInt()
        thumbHeight = a.getDimension(R.styleable.ColorSeekBar_thumbHeight, dp2px(30f).toFloat()).toInt()
        barMargin = a.getDimension(R.styleable.ColorSeekBar_barMargin, dp2px(5f).toFloat()).toInt()
        a.recycle()

        if (colorsId != 0) mColorSeeds = getColorsById(colorsId)

        setBackgroundColor(mBackgroundColor)
    }

    /**
     * @param vertical
     */
    fun setVertical(vertical: Boolean) {
        isVertical = vertical
        refreshLayoutParams()
        invalidate()
    }

    /**
     * @param id
     * @return
     */
    private fun getColorsById(id: Int): IntArray {
        if (isInEditMode) {
            val s = mContext!!.resources.getStringArray(id)
            val colors = IntArray(s.size)
            for (j in s.indices) {
                colors[j] = Color.parseColor(s[j])
            }
            return colors
        } else {
            val typedArray = mContext!!.resources.obtainTypedArray(id)
            val colors = IntArray(typedArray.length())
            for (j in 0 until typedArray.length()) {
                colors[j] = typedArray.getColor(j, Color.BLACK)
            }
            typedArray.recycle()
            return colors
        }
    }

    private fun init() {
        //Logger.i("init")
        //init size
        mThumbRadius = (thumbHeight / 2).toFloat()
        mPaddingSize = mThumbRadius.toInt()
        val viewBottom = height - paddingBottom - mPaddingSize
        val viewRight = width - paddingRight - mPaddingSize
        //init l r t b
        realLeft = paddingLeft + mPaddingSize
        realRight = if (isVertical) viewBottom else viewRight
        realTop = paddingTop + mPaddingSize
        realBottom = if (isVertical) viewRight else viewBottom

        mBarWidth = realRight - realLeft

        //init rect
        mColorRect = Rect(realLeft, realTop, realRight, realTop + barHeight)

        //init paint
        mColorGradient =
            LinearGradient(0f, 0f, mColorRect!!.width().toFloat(), 0f, mColorSeeds, null, Shader.TileMode.MIRROR)
        mColorRectPaint = Paint()
        mColorRectPaint!!.shader = mColorGradient
        mColorRectPaint!!.isAntiAlias = true
        cacheColors()
        setAlphaValue()
    }

    override fun onSizeChanged(w: Int, h: Int, oldw: Int, oldh: Int) {
        super.onSizeChanged(w, h, oldw, oldh)
        //Logger.i("onSizeChanged")
        mTransparentBitmap = if (isVertical) {
            Bitmap.createBitmap(h, w, Bitmap.Config.ARGB_4444)
        } else {
            Bitmap.createBitmap(w, h, Bitmap.Config.ARGB_4444)
        }
        mTransparentBitmap!!.eraseColor(Color.TRANSPARENT)
        init()
        mInit = true
        if (mColorsToInvoke != -1) color = mColorsToInvoke
    }


    private fun cacheColors() {
        //if the view's size hasn't been initialized. do not cache.
        if (mBarWidth < 1) return
        mColors.clear()
        for (i in 0..maxValue) {
            mColors.add(pickColor(i))
        }
    }

    override fun onDraw(canvas: Canvas) {
        //Logger.i("onDraw")

        if (isVertical) {
            canvas.rotate(-90f)
            canvas.translate((-height).toFloat(), 0f)
            canvas.scale(-1f, 1f, (height / 2).toFloat(), (width / 2).toFloat())
        }

        val colorPosition = mColorBarPosition.toFloat() / maxValue * mBarWidth

        val colorPaint = Paint()
        colorPaint.isAntiAlias = true
        val color = getColor(false)
        val colorStartTransparent =
            Color.argb(alphaMaxPosition, Color.red(color), Color.green(color), Color.blue(color))
        val colorEndTransparent = Color.argb(alphaMinPosition, Color.red(color), Color.green(color), Color.blue(color))
        colorPaint.color = color
        val toAlpha = intArrayOf(colorStartTransparent, colorEndTransparent)
        //clear
        canvas.drawBitmap(mTransparentBitmap!!, 0f, 0f, null)

        //draw color bar
        canvas.drawRect(mColorRect!!, mColorRectPaint!!)
        //draw color bar thumb
        val thumbX = colorPosition + realLeft
        val thumbY = (mColorRect!!.top + mColorRect!!.height() / 2).toFloat()
        canvas.drawCircle(thumbX, thumbY, (barHeight / 2 + 5).toFloat(), colorPaint)

        //draw color bar thumb radial gradient shader
        //val thumbShader = RadialGradient(thumbX, thumbY, mThumbRadius, toAlpha, null, ShadeHelper.TileMode.MIRROR)
        val thumbGradientPaint = Paint()
        thumbGradientPaint.isAntiAlias = true
        //thumbGradientPaint.shader = thumbShader
        thumbGradientPaint.color = color
        canvas.drawCircle(thumbX, thumbY, (thumbHeight / 4).toFloat(), thumbGradientPaint)

        thumbGradientPaint.style = Paint.Style.STROKE
        thumbGradientPaint.color = ContextCompat.getColor(context, R.color.color_seek_bar_stroke)
        //thumbGradientPaint.color = Color.WHITE
        thumbGradientPaint.strokeWidth = 5F
        canvas.drawCircle(thumbX, thumbY, (thumbHeight / 4).toFloat(), thumbGradientPaint)

        if (mIsShowAlphaBar) {

            //init rect
            val top = (thumbHeight.toFloat() + mThumbRadius + barHeight.toFloat() + barMargin.toFloat()).toInt()
            mAlphaRect = Rect(realLeft, top, realRight, top + barHeight)
            //draw alpha bar
            val alphaBarPaint = Paint()
            alphaBarPaint.isAntiAlias = true
            val alphaBarShader =
                LinearGradient(0f, 0f, mAlphaRect!!.width().toFloat(), 0f, toAlpha, null, Shader.TileMode.MIRROR)
            alphaBarPaint.shader = alphaBarShader
            canvas.drawRect(mAlphaRect!!, alphaBarPaint)

            //draw alpha bar thumb
            val alphaPosition =
                (mAlphaBarPosition - alphaMinPosition).toFloat() / (alphaMaxPosition - alphaMinPosition) * mBarWidth
            val alphaThumbX = alphaPosition + realLeft
            val alphaThumbY = (mAlphaRect!!.top + mAlphaRect!!.height() / 2).toFloat()
            canvas.drawCircle(alphaThumbX, alphaThumbY, (barHeight / 2 + 5).toFloat(), colorPaint)

            //draw alpha bar thumb radial gradient shader
            val alphaThumbShader =
                RadialGradient(alphaThumbX, alphaThumbY, mThumbRadius, toAlpha, null, Shader.TileMode.MIRROR)
            val alphaThumbGradientPaint = Paint()
            alphaThumbGradientPaint.isAntiAlias = true
            alphaThumbGradientPaint.shader = alphaThumbShader
            canvas.drawCircle(alphaThumbX, alphaThumbY, (thumbHeight / 2).toFloat(), alphaThumbGradientPaint)
        }

        if (isFirstDraw) {

            if (mOnColorChangeLister != null) {
                mOnColorChangeLister!!.onColorChangeListener(mColorBarPosition, mAlphaBarPosition, color)
            }

            isFirstDraw = false

            if (mOnInitDoneListener != null) {
                mOnInitDoneListener!!.done()
            }
        }


        super.onDraw(canvas)
    }


    override fun onTouchEvent(event: MotionEvent): Boolean {
        mx = if (isVertical) event.y else event.x
        my = if (isVertical) event.x else event.y
        when (event.action) {
            MotionEvent.ACTION_DOWN -> if (isOnBar(mColorRect!!, mx, my)) {
                mMovingColorBar = true
            } else if (mIsShowAlphaBar) {
                if (isOnBar(mAlphaRect!!, mx, my)) {
                    mMovingAlphaBar = true
                }
            }
            MotionEvent.ACTION_MOVE -> {
                parent.requestDisallowInterceptTouchEvent(true)
                if (mMovingColorBar) {
                    val value = (mx - realLeft) / mBarWidth * maxValue
                    mColorBarPosition = value.toInt()
                    if (mColorBarPosition < 0) mColorBarPosition = 0
                    if (mColorBarPosition > maxValue) mColorBarPosition = maxValue
                } else if (mIsShowAlphaBar) {
                    if (mMovingAlphaBar) {
                        val value =
                            (mx - realLeft) / mBarWidth.toFloat() * (alphaMaxPosition - alphaMinPosition) + alphaMinPosition
                        mAlphaBarPosition = value.toInt()
                        if (mAlphaBarPosition < alphaMinPosition)
                            mAlphaBarPosition = alphaMinPosition
                        else if (mAlphaBarPosition > alphaMaxPosition) mAlphaBarPosition = alphaMaxPosition
                        setAlphaValue()
                    }
                }
                if (mOnColorChangeLister != null && (mMovingAlphaBar || mMovingColorBar)) {
                    mOnColorChangeLister!!.onColorChangeListener(mColorBarPosition, mAlphaBarPosition, color)
                }
                invalidate()
            }
            MotionEvent.ACTION_UP -> {
                mMovingColorBar = false
                mMovingAlphaBar = false
            }
        }
        return true
    }

    /**
     * @param r
     * @param mx1
     * @param my1
     * @return whether MotionEvent is performing on bar or not
     */
    private fun isOnBar(r: Rect, mx1: Float, my1: Float): Boolean {
        return r.left - mThumbRadius < mx1 && mx1 < r.right + mThumbRadius && r.top - mThumbRadius < my1 && my1 < r.bottom + mThumbRadius
    }


    /**
     * @param value
     * @return color
     */
    private fun pickColor(value: Int): Int {
        return pickColor(value.toFloat() / maxValue * mBarWidth)
    }

    /**
     * @param position
     * @return color
     */
    private fun pickColor(position: Float): Int {
        val unit = position / mBarWidth
        if (unit <= 0.0)
            return mColorSeeds[0]

        if (unit >= 1)
            return mColorSeeds[mColorSeeds.size - 1]

        var colorPosition = unit * (mColorSeeds.size - 1)
        val i = colorPosition.toInt()
        colorPosition -= i.toFloat()
        c0 = mColorSeeds[i]
        c1 = mColorSeeds[i + 1]
        //         mAlpha = mix(Color.alpha(c0), Color.alpha(c1), colorPosition);
        mRed = mix(Color.red(c0), Color.red(c1), colorPosition)
        mGreen = mix(Color.green(c0), Color.green(c1), colorPosition)
        mBlue = mix(Color.blue(c0), Color.blue(c1), colorPosition)
        return Color.rgb(mRed, mGreen, mBlue)
    }

    /**
     * @param start
     * @param end
     * @param position
     * @return
     */
    private fun mix(start: Int, end: Int, position: Float): Int {
        return start + Math.round(position * (end - start))
    }

    /**
     * @param withAlpha
     * @return
     */
    fun getColor(withAlpha: Boolean): Int {
        //pick mode
        if (mColorBarPosition >= mColors.size) {
            val color = pickColor(mColorBarPosition)
            return if (withAlpha) {
                color
            } else {
                Color.argb(alphaValue, Color.red(color), Color.green(color), Color.blue(color))
            }
        }

        //cache mode
        val color = mColors[mColorBarPosition]

        return if (withAlpha) {
            Color.argb(alphaValue, Color.red(color), Color.green(color), Color.blue(color))
        } else color
    }

    interface OnColorChangeListener {
        /**
         * @param colorBarPosition between 0-maxValue
         * @param alphaBarPosition between 0-255
         * @param color            return the color contains alpha value whether showAlphaBar is true or without alpha value
         */
        fun onColorChangeListener(colorBarPosition: Int, alphaBarPosition: Int, color: Int)
    }

    /**
     * @param onColorChangeListener
     */
    fun setOnColorChangeListener(onColorChangeListener: OnColorChangeListener) {
        this.mOnColorChangeLister = onColorChangeListener
    }


    fun dp2px(dpValue: Float): Int {
        val scale = mContext!!.resources.displayMetrics.density
        return (dpValue * scale + 0.5f).toInt()
    }

    /**
     * Set colors by resource id. The resource's type must be ArrayRes
     *
     * @param resId
     */
    fun setColorSeeds(@ArrayRes resId: Int) {
        setColorSeeds(getColorsById(resId))
    }

    fun setColorSeeds(colors: IntArray) {
        mColorSeeds = colors
        init()
        invalidate()
        if (mOnColorChangeLister != null)
            mOnColorChangeLister!!.onColorChangeListener(mColorBarPosition, mAlphaBarPosition, color)
    }

    /**
     * @param color
     * @return the color's position in the bar, if not in the bar ,return -1;
     */
    fun getColorIndexPosition(color: Int): Int {
        return mColors.indexOf(Color.argb(255, Color.red(color), Color.green(color), Color.blue(color)))
    }

    private fun refreshLayoutParams() {
        layoutParams = layoutParams
    }

    /**
     * @param dp
     */
    fun setBarHeight(dp: Float) {
        barHeight = dp2px(dp)
        refreshLayoutParams()
        invalidate()
    }

    /**
     * @param px
     */
    fun setBarHeightPx(px: Int) {
        barHeight = px
        refreshLayoutParams()
        invalidate()
    }

    private fun setAlphaValue() {
        alphaValue = 255 - mAlphaBarPosition
    }

    fun setMaxPosition(value: Int) {
        this.maxValue = value
        invalidate()
        cacheColors()
    }

    /**
     * set margin between bars
     *
     * @param mBarMargin
     */
    fun setBarMargin(mBarMargin: Float) {
        this.barMargin = dp2px(mBarMargin)
        refreshLayoutParams()
        invalidate()
    }

    /**
     * set margin between bars
     *
     * @param mBarMargin
     */
    fun setBarMarginPx(mBarMargin: Int) {
        this.barMargin = mBarMargin
        refreshLayoutParams()
        invalidate()
    }


    /**
     * Set the value of color bar, if out of bounds , it will be 0 or maxValue;
     * @param value
     */
    fun setColorBarPosition(value: Int) {
        this.mColorBarPosition = value
        mColorBarPosition = if (mColorBarPosition > maxValue) maxValue else mColorBarPosition
        mColorBarPosition = if (mColorBarPosition < 0) 0 else mColorBarPosition
        invalidate()
        if (mOnColorChangeLister != null)
            mOnColorChangeLister!!.onColorChangeListener(mColorBarPosition, mAlphaBarPosition, color)
    }

    fun setOnInitDoneListener(listener: OnInitDoneListener) {
        this.mOnInitDoneListener = listener
    }

    /**
     * set thumb's height by dpi
     *
     * @param dp
     */
    fun setThumbHeight(dp: Float) {
        this.thumbHeight = dp2px(dp)
        mThumbRadius = (thumbHeight / 2).toFloat()
        refreshLayoutParams()
        invalidate()
    }

    /**
     * set thumb's height by pixels
     *
     * @param px
     */
    fun setThumbHeightPx(px: Int) {
        this.thumbHeight = px
        mThumbRadius = (thumbHeight / 2).toFloat()
        refreshLayoutParams()
        invalidate()
    }

    interface OnInitDoneListener {
        fun done()
    }

}