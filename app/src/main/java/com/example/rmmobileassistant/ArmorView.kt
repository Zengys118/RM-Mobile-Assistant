package com.example.rmmobileassistant

import android.content.Context
import android.graphics.*
import android.util.AttributeSet
import android.view.View

class ArmorView @JvmOverloads constructor(
    context: Context, attrs: AttributeSet? = null
) : View(context, attrs) {

    enum class Team { RED, BLUE }

    var team: Team = Team.BLUE
        set(value) { field = value; invalidate() }

    var glow: Float = 1.0f
        set(value) { field = value; invalidate() }

    private val paint = Paint(Paint.ANTI_ALIAS_FLAG)
    private val textPaint = Paint(Paint.ANTI_ALIAS_FLAG).apply {
        color = Color.WHITE
        textAlign = Paint.Align.CENTER
        typeface = Typeface.create(Typeface.DEFAULT, Typeface.BOLD)
        isSubpixelText = true
    }

    private val leftPath = Path()
    private val rightPath = Path()

    override fun onDraw(canvas: Canvas) {
        super.onDraw(canvas)
        canvas.drawColor(Color.BLACK)

        val w = width.toFloat()
        val h = height.toFloat()
        val cx = w / 2f
        val cy = h / 2f

        val baseColor = when (team) {
            Team.RED -> Color.rgb(255, 60, 60)
            Team.BLUE -> Color.rgb(60, 170, 255)
        }

        val dm = resources.displayMetrics
        val pxPerMmX = dm.xdpi / 25.4f
        val pxPerMmY = dm.ydpi / 25.4f

        val distMm = 135f
        val barHMm = 57f

        var distPx = distMm * pxPerMmX
        var barHPx = barHMm * pxPerMmY

        val marginPx = 16f * dm.density
        val barWGuess = barHPx * 0.15f
        val needW = distPx + 2f * barWGuess + marginPx * 2f
        val needH = barHPx + marginPx * 2f
        val scale = minOf(1f, w / needW, h / needH)
        distPx *= scale
        barHPx *= scale

        val barW = barHPx * 0.15f
        val roofH = (barHPx * 0.054f).coerceAtLeast(1f)

        val top = cy - barHPx / 2f
        val bot = cy + barHPx / 2f

        val leftCenterX = cx - distPx / 2f
        val rightCenterX = cx + distPx / 2f

        val lx0 = leftCenterX - barW / 2f
        val lx1 = leftCenterX + barW / 2f
        val rx0 = rightCenterX - barW / 2f
        val rx1 = rightCenterX + barW / 2f

        buildHex(leftPath, lx0, lx1, top, bot, roofH)
        buildHex(rightPath, rx0, rx1, top, bot, roofH)

        paint.style = Paint.Style.FILL
        paint.color = baseColor
        paint.alpha = 255
        paint.maskFilter = BlurMaskFilter(barW * 2.0f, BlurMaskFilter.Blur.NORMAL)
        canvas.drawPath(leftPath, paint)
        canvas.drawPath(rightPath, paint)

        paint.maskFilter = null
        paint.alpha = 255
        canvas.drawPath(leftPath, paint)
        canvas.drawPath(rightPath, paint)

        textPaint.textSize = barHPx * 1.0f
        val fm = textPaint.fontMetrics
        val textY = cy - (fm.ascent + fm.descent) / 2f
        canvas.drawText("3", cx, textY, textPaint)
    }

    private fun buildHex(p: Path, x0: Float, x1: Float, top: Float, bot: Float, roofH: Float) {
        val cx = (x0 + x1) / 2f
        val rh = roofH.coerceAtLeast(1f)
        p.reset()
        p.moveTo(cx, top)
        p.lineTo(x1, top + rh)
        p.lineTo(x1, bot - rh)
        p.lineTo(cx, bot)
        p.lineTo(x0, bot - rh)
        p.lineTo(x0, top + rh)
        p.close()
    }
}
