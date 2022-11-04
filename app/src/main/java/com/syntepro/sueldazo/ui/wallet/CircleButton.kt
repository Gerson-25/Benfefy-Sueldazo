package com.syntepro.sueldazo.ui.wallet

import android.content.Context
import android.graphics.Canvas
import android.graphics.Paint
import android.graphics.RectF
import android.graphics.Typeface
import android.text.TextPaint
import android.util.AttributeSet
import android.view.View
import android.view.animation.Animation
import android.view.animation.Transformation
import com.syntepro.sueldazo.R

class CircleButton(context: Context, attrs: AttributeSet?): View(context, attrs) {

    private var paint: Paint? = null
    private var rect: RectF? = null
    private var fillPaint: Paint? = null
    private var fillRect: RectF? = null
    private var fillColor = 0
    private var color = 0
    private var angle = 0f
    private var startAngle = 0f
    private var animation: CircleAngleAnimation? = null
    private var strokeColor = 0
    private var text: String? = null
    private var textColor = 0
    private var textSize = 0f
    private var textPaint: TextPaint? = null
    private var textStyle = 0

    init {
        val typedArray = context.obtainStyledAttributes(attrs, R.styleable.CircleButton)
        startAngle = typedArray.getFloat(R.styleable.CircleButton_startAngle, 0f)
        val offsetAngle = typedArray.getFloat(R.styleable.CircleButton_offsetAngle, 0f)
        color = typedArray.getColor(R.styleable.CircleButton_color, context.resources.getColor(R.color.colorPrimary))
        val strokeWidth = typedArray.getFloat(R.styleable.CircleButton_circleStrokeWidth, 20f)
        val circleSize = typedArray.getDimension(R.styleable.CircleButton_cicleSize, 100f)
        fillColor = typedArray.getColor(R.styleable.CircleButton_fillColor, 0)
        strokeColor = typedArray.getColor(R.styleable.CircleButton_strokeColor, 0)
        text = typedArray.getString(R.styleable.CircleButton_text)
        textColor = typedArray.getColor(R.styleable.CircleButton_textColor, context.resources.getColor(R.color.colorPrimary))
        textSize = typedArray.getDimension(R.styleable.CircleButton_textSize, 10f)
        textStyle = typedArray.getInt(R.styleable.CircleButton_textStyle, 0)
        if (strokeColor == 0) strokeColor = fillColor
        typedArray.recycle()
        paint = Paint()
        paint!!.isAntiAlias = true
        paint!!.style = Paint.Style.STROKE
        paint!!.strokeWidth = strokeWidth
        paint!!.color = strokeColor
        rect = RectF(strokeWidth, strokeWidth, circleSize - strokeWidth, circleSize - strokeWidth)
        fillPaint = Paint()
        fillPaint!!.isAntiAlias = true
        fillPaint!!.style = Paint.Style.FILL
        fillPaint!!.color = fillColor
        fillRect = RectF(strokeWidth, strokeWidth, circleSize - strokeWidth, circleSize - strokeWidth)
        textPaint = TextPaint()
        textPaint!!.isAntiAlias = true
        textPaint!!.textSize = textSize
        textPaint!!.color = textColor
        if (textStyle == 1) textPaint!!.typeface = Typeface.create(Typeface.DEFAULT, Typeface.BOLD) else if (textStyle == 2) textPaint!!.typeface = Typeface.create(Typeface.DEFAULT, Typeface.ITALIC)
        angle = 360f
    }


    fun getAngle(): Float {
        return angle
    }

    fun setAngle(angle: Float) {
        this.angle = angle
    }

    fun getColor(): Int {
        return color
    }

    fun setColor(color: Int) {
        this.color = color
    }

    fun getRect(): RectF? {
        return rect
    }

    fun setRect(rect: RectF?) {
        this.rect = rect
    }

    fun getPaint(): Paint? {
        return paint
    }

    fun setPaint(paint: Paint?) {
        this.paint = paint
    }

    fun getStartAngle(): Float {
        return startAngle
    }

    fun setStartAngle(startAngle: Float) {
        this.startAngle = startAngle
    }

    fun getFillColor(): Int {
        return fillColor
    }

    fun setFillColor(fillColor: Int) {
        this.fillColor = fillColor
        fillPaint!!.color = fillColor
        requestLayout()
    }

    fun getStrokeColor(): Int {
        return strokeColor
    }

    fun setStrokeColor(strokeColor: Int) {
        this.strokeColor = strokeColor
        paint!!.color = strokeColor
        requestLayout()
    }

    fun getText(): String? {
        return text
    }

    fun setText(text: String?) {
        this.text = text
    }

    fun getTextColor(): Int {
        return textColor
    }

    fun setTextColor(textColor: Int) {
        this.textColor = textColor
    }

    fun getTextSize(): Float {
        return textSize
    }

    fun setTextSize(textSize: Float) {
        this.textSize = textSize
    }

    fun getTextPaint(): TextPaint? {
        return textPaint
    }

    fun setTextPaint(textPaint: TextPaint?) {
        this.textPaint = textPaint
    }

    fun getTextStyle(): Int {
        return textStyle
    }

    fun setTextStyle(textStyle: Int) {
        this.textStyle = textStyle
    }

    override fun onDraw(canvas: Canvas) {
        super.onDraw(canvas)
        if (fillColor != 0) {
            canvas.drawArc(rect!!, 0f, 360f, false, fillPaint!!)
        }
        textPaint!!.textAlign = Paint.Align.CENTER
        canvas.drawArc(rect!!, startAngle, angle, false, paint!!)
        val xPos = width / 2
        val yPos = (height / 2 - (textPaint!!.descent() + textPaint!!.ascent()) / 2).toInt()
        canvas.drawText(text!!, xPos.toFloat(), yPos.toFloat(), textPaint!!)
    }


    fun startAnimation() {
        animation = CircleAngleAnimation(this, 360)
        animation!!.duration = 1000
        animation!!.repeatCount = Animation.INFINITE
        this.startAnimation(animation)
    }

    fun stopAnimation() {
        if (animation != null) {
            animation!!.duration = 0
            animation!!.cancel()
        }
        paint!!.color = strokeColor
        this.angle = 360f
        requestLayout()
    }

    class CircleAngleAnimation(private val circle: CircleButton, newAngle: Int) : Animation() {
        private val oldAngle = 0f
        private val newAngle: Float = newAngle.toFloat()
        override fun applyTransformation(interpolatedTime: Float, transformation: Transformation) {
            val angle = oldAngle + (newAngle - oldAngle) * interpolatedTime
            circle.setAngle(angle)
            var ns: Float = circle.getStartAngle() + 1
            if (ns > 360) ns = 0f
            circle.setStartAngle(ns)
            circle.requestLayout()
        }

        init {
            //circle.getAngle();
            this.circle.getPaint()?.color = this.circle.getColor()
        }
    }
}