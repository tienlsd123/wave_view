package com.bxt.waveview

import android.animation.ValueAnimator
import android.content.Context
import android.graphics.*
import android.util.AttributeSet
import android.view.View
import android.view.animation.LinearInterpolator
import androidx.annotation.ColorInt
import kotlin.math.sin

class WaveView(context: Context, attrs: AttributeSet) : View(context, attrs) {

    private var _process = 0.0f
    var process: Float
        get() = _process
        set(value) {
            ValueAnimator.ofFloat(_process, value).apply {
                duration = 500
                interpolator = LinearInterpolator()
                addUpdateListener { animation ->
                    _process = animation.animatedValue as Float
                }
            }.start()
        }

    var speed: Int = 4
    var waveStyle = WAVE_STYLE_1

    private var _waveAmplitude = 30f
    private var _waveFrequency = 0.005f
    private val waveLayerCount: Int = 3
    private val wavePaint = Paint()

    @ColorInt
    private var _waveColor: Int = resources.getColor(R.color.black_50, null)

    private var waveHeight = 0f
    private var waveAnimator: ValueAnimator? = null

    init {
        val attr = context.obtainStyledAttributes(attrs, R.styleable.WaveView, 0, 0)

        if (attr.hasValue(R.styleable.WaveView_waveColor)) {
            _waveColor = getColorWithAlpha(attr.getColor(R.styleable.WaveView_waveColor, _waveColor))
        }

        if (attr.hasValue(R.styleable.WaveView_waveProcess)) {
            _process = attr.getFloat(R.styleable.WaveView_waveProcess, _process)
        }
        if (attr.hasValue(R.styleable.WaveView_waveSpeed)) {
            speed = attr.getInteger(R.styleable.WaveView_waveSpeed, speed)
        }

        if (attr.hasValue(R.styleable.WaveView_waveStyle)) {
            waveStyle = attr.getInteger(R.styleable.WaveView_waveStyle, waveStyle)
        }

        attr.recycle()

        wavePaint.color = _waveColor
        wavePaint.style = Paint.Style.FILL
        startWaveAnimation()
    }

    override fun onDraw(canvas: Canvas) {
        super.onDraw(canvas)
        for (i in 0 until waveLayerCount) {
            drawWave(canvas, i)
        }
    }


    private fun drawWave(canvas: Canvas, index: Int) {
        var wh = (waveHeight + index) / 0.1f
        wh = when (waveStyle) {
            WAVE_STYLE_1 -> wh
            WAVE_STYLE_2 -> if (index % 2 == 0) -wh else wh
            else -> wh
        }

        val path = Path()
        val process = (100 - _process) / 100f
        path.moveTo(0f, height * process)
        for (x in 0 until width) {
            val y = (_waveAmplitude * sin(0.4 * Math.PI * _waveFrequency * x + wh) + (height * process))
            path.lineTo(x.toFloat(), y.toFloat())
        }
        path.lineTo(width.toFloat(), height.toFloat())
        path.lineTo(0f, height.toFloat())
        path.close()

        canvas.drawPath(path, wavePaint)
    }

    private fun startWaveAnimation() {
        waveAnimator = ValueAnimator.ofFloat(0f, (1 * Math.PI).toFloat()).apply {
            repeatCount = ValueAnimator.INFINITE
            duration = speed * 1000L
            interpolator = LinearInterpolator()
            addUpdateListener { animation ->
                val value = animation.animatedValue as Float
                waveHeight = value
                invalidate()
            }
        }
        waveAnimator?.start()
    }

    private fun getColorWithAlpha(color: Int): Int {
        val red = Color.red(color)
        val blue = Color.blue(color)
        val green = Color.green(color)
        return Color.argb(255 / 2, red, green, blue)
    }

    object ShapeType {
        const val TRIANGLE = 0
        const val CIRCLE = 1
        const val SQUARE = 2
        const val RECTANGLE = 3
    }

    companion object {
        const val WAVE_STYLE_1 = 1
        const val WAVE_STYLE_2 = 2
    }

}
