package com.syntepro.sueldazo.utils

import android.graphics.*
import com.squareup.picasso.Transformation
import kotlin.math.min

internal class CircleTransform : Transformation {
    private val color: Int

    constructor(color: Int) {
        this.color = color
    }

    constructor() {
        this.color = Color.TRANSPARENT
    }

    override fun transform(source: Bitmap): Bitmap {
        val size = min(source.width, source.height)
        val x = (source.width - size) / 2
        val y = (source.height - size) / 2

        val squaredBitmap = Bitmap.createBitmap(source, x, y, size, size)
        if (squaredBitmap != source) {
            source.recycle()
        }

        val bitmap = Bitmap.createBitmap(size, size, source.config)

        val canvas = Canvas(bitmap)
        val paint = Paint()
        val shader = BitmapShader(squaredBitmap, Shader.TileMode.CLAMP, Shader.TileMode.CLAMP)
        paint.shader = shader
        paint.isAntiAlias = true

        val r = size / 2f

        canvas.drawCircle(r, r, r, paint)
        val paintc = Paint()
        paintc.color = color
        paintc.style = Paint.Style.STROKE
        paintc.isAntiAlias = true
        paintc.strokeWidth = 2f
        canvas.drawCircle(r, r, r, paintc)
        squaredBitmap.recycle()
        return bitmap
    }

    override fun key(): String {
        return "circle"
    }
}