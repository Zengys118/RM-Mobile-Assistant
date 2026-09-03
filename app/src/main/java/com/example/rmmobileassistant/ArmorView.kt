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

    private val leftLightPath = Path()
    private val rightLightPath = Path()
    private val digitPath = Path()

    override fun onDraw(canvas: Canvas) {
        super.onDraw(canvas)
        canvas.drawColor(Color.BLACK)

        val w = width.toFloat()
        val h = height.toFloat()
        val cx = w / 2f
        val cy = h / 2f

        val baseColor = when (team) {
            Team.RED -> Color.rgb(245, 0, 22)
            Team.BLUE -> Color.rgb(0, 96, 230)
        }

        val dm = resources.displayMetrics
        val pxPerMmX = dm.xdpi / 25.4f
        val pxPerMmY = dm.ydpi / 25.4f

        val distMm = 135f
        val barHMm = 63f

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
        val lightHPx = barHPx * (51f / 63f)
        val lightW = barW * (6.5f / 9f)
        // The colored light strip has asymmetric end bevels: the outside
        // bevel is long, while the bevel facing the center is short.
        val lightLongBevel = (lightHPx * 0.13f).coerceAtLeast(1f)
        val lightShortBevel = (lightHPx * 0.052f).coerceAtLeast(1f)

        val lightTop = cy - lightHPx / 2f
        val lightBot = cy + lightHPx / 2f

        val leftCenterX = cx - distPx / 2f
        val rightCenterX = cx + distPx / 2f

        buildBarPath(
            leftLightPath,
            leftCenterX,
            lightTop,
            lightBot,
            lightW,
            innerOnRight = true,
            longBevel = lightLongBevel,
            shortBevel = lightShortBevel
        )
        buildBarPath(
            rightLightPath,
            rightCenterX,
            lightTop,
            lightBot,
            lightW,
            innerOnRight = false,
            longBevel = lightLongBevel,
            shortBevel = lightShortBevel
        )

        paint.style = Paint.Style.FILL
        paint.color = baseColor
        canvas.drawPath(leftLightPath, paint)
        canvas.drawPath(rightLightPath, paint)

        buildThree(digitPath, cx, cy, barHPx * 0.92f)
        canvas.drawPath(digitPath, textPaint)
    }

    private fun buildBarPath(
        p: Path,
        cx: Float,
        top: Float,
        bot: Float,
        width: Float,
        innerOnRight: Boolean,
        longBevel: Float,
        shortBevel: Float
    ) {
        val halfW = width / 2f
        val long = longBevel.coerceAtLeast(1f)
        val short = shortBevel.coerceAtLeast(1f)
        val leftBevel = if (innerOnRight) short else long
        val rightBevel = if (innerOnRight) long else short
        // DXF bevels are approximately 45 degrees, so their horizontal
        // projection is shorter than their vertical run.
        val leftInset = leftBevel * 0.7f
        val rightInset = rightBevel * 0.7f
        val xLeft = cx - halfW
        val xRight = cx + halfW
        p.reset()
        p.moveTo(xLeft, top + leftBevel)
        p.lineTo(xLeft + leftInset, top)
        p.lineTo(xRight - rightInset, top)
        p.lineTo(xRight, top + rightBevel)
        p.lineTo(xRight, bot - rightBevel)
        p.lineTo(xRight - rightInset, bot)
        p.lineTo(xLeft + leftInset, bot)
        p.lineTo(xLeft, bot - leftBevel)
        p.close()
    }

    // Straight-edged outline based on the official RM 3 decal.
    private fun buildThree(p: Path, cx: Float, cy: Float, height: Float) {
        val points = arrayOf(
            189f to 37f, 156f to 43f, 131f to 53f, 131f to 103f,
            168f to 87f, 216f to 87f, 231f to 94f, 240f to 104f,
            245f to 122f, 244f to 136f, 237f to 151f, 221f to 163f,
            197f to 169f, 155f to 170f, 155f to 218f, 200f to 218f,
            236f to 229f, 254f to 250f, 256f to 276f, 252f to 288f,
            241f to 301f, 227f to 309f, 206f to 314f, 181f to 314f,
            158f to 310f, 120f to 292f, 120f to 347f, 163f to 360f,
            214f to 362f, 260f to 352f, 293f to 332f, 304f to 320f,
            315f to 299f, 320f to 277f, 320f to 257f, 310f to 229f,
            293f to 210f, 269f to 197f, 247f to 192f, 281f to 174f,
            301f to 150f, 307f to 132f, 308f to 102f, 300f to 78f,
            286f to 61f, 259f to 45f, 225f to 37f
        )
        val scaleY = height / 325f
        val scaleX = scaleY
        fun x(value: Float) = cx + (value - 220f) * scaleX
        fun y(value: Float) = cy + (value - 199.5f) * scaleY

        p.reset()
        p.moveTo(x(points[0].first), y(points[0].second))
        for (i in 1 until points.size) {
            p.lineTo(x(points[i].first), y(points[i].second))
        }
        p.close()
    }
}
