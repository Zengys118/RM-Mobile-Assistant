package com.example.rmmobileassistant

import android.content.Context
import android.graphics.*
import android.util.AttributeSet
import android.view.View
import kotlin.math.min

class FieldTestView @JvmOverloads constructor(
    context: Context, attrs: AttributeSet? = null
) : View(context, attrs) {

    var step: Step = Step("FIELD TEST", "Tap right = next, left = prev")
        set(value) { field = value; invalidate() }

    var indexText: String = "1/1"
        set(value) { field = value; invalidate() }

    // 只是视觉提示：左/右半区
    var showZonesHint: Boolean = true
        set(value) { field = value; invalidate() }

    private val bgPaint = Paint(Paint.ANTI_ALIAS_FLAG).apply { color = Color.BLACK }
    private val titlePaint = Paint(Paint.ANTI_ALIAS_FLAG).apply {
        color = Color.WHITE
        typeface = Typeface.create(Typeface.DEFAULT, Typeface.BOLD)
        textAlign = Paint.Align.CENTER
        isSubpixelText = true
    }
    private val descPaint = Paint(Paint.ANTI_ALIAS_FLAG).apply {
        color = Color.LTGRAY
        typeface = Typeface.create(Typeface.DEFAULT, Typeface.NORMAL)
        textAlign = Paint.Align.CENTER
        isSubpixelText = true
    }
    private val smallPaint = Paint(Paint.ANTI_ALIAS_FLAG).apply {
        color = Color.GRAY
        textAlign = Paint.Align.RIGHT
        isSubpixelText = true
    }
    private val zonePaint = Paint(Paint.ANTI_ALIAS_FLAG).apply {
        color = Color.WHITE
        alpha = 18
        style = Paint.Style.STROKE
        strokeWidth = 2f * resources.displayMetrics.density
    }

    override fun onDraw(canvas: Canvas) {
        super.onDraw(canvas)
        canvas.drawRect(0f, 0f, width.toFloat(), height.toFloat(), bgPaint)

        val w = width.toFloat()
        val h = height.toFloat()
        val cx = w / 2f
        val cy = h / 2f

        // 标题大字：按屏幕宽度估一个字号（很粗暴但很稳）
        val maxTitleSize = min(w, h) * 0.16f
        titlePaint.textSize = maxTitleSize

        // 描述小字
        descPaint.textSize = maxTitleSize * 0.28f

        // 右上角 index
        smallPaint.textSize = maxTitleSize * 0.22f
        canvas.drawText(indexText, w - 16f * resources.displayMetrics.density, 32f * resources.displayMetrics.density, smallPaint)

        // 标题垂直居中（基线）
        val fmT = titlePaint.fontMetrics
        val titleY = cy - (fmT.ascent + fmT.descent) / 2f - maxTitleSize * 0.10f

        canvas.drawText(step.title, cx, titleY, titlePaint)

        // 描述：在标题下面
        val fmD = descPaint.fontMetrics
        val descY = titleY + maxTitleSize * 0.55f - (fmD.ascent + fmD.descent) / 2f
        if (step.desc.isNotBlank()) {
            canvas.drawText(step.desc, cx, descY, descPaint)
        }

        // 轻微分区提示（可关）
        if (showZonesHint) {
            val mid = w / 2f
            val pad = 10f * resources.displayMetrics.density
            canvas.drawRect(pad, pad, mid - pad, h - pad, zonePaint)
            canvas.drawRect(mid + pad, pad, w - pad, h - pad, zonePaint)

            // 左右文字提示（非常淡）
            val hintPaint = Paint(Paint.ANTI_ALIAS_FLAG).apply {
                color = Color.WHITE
                alpha = 35
                textAlign = Paint.Align.CENTER
                textSize = maxTitleSize * 0.18f
            }
            canvas.drawText("PREV", w * 0.25f, h - 24f * resources.displayMetrics.density, hintPaint)
            canvas.drawText("NEXT", w * 0.75f, h - 24f * resources.displayMetrics.density, hintPaint)
        }
    }
}
