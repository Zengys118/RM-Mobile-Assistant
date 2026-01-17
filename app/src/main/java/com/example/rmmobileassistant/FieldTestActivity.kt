package com.example.rmmobileassistant

import android.os.Bundle
import android.view.MotionEvent
import android.view.View
import android.view.WindowManager
import androidx.appcompat.app.AppCompatActivity

class FieldTestActivity : AppCompatActivity() {

    private lateinit var view: FieldTestView
    private lateinit var steps: MutableList<Step>
    private var idx: Int = 0

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        window.addFlags(WindowManager.LayoutParams.FLAG_KEEP_SCREEN_ON)
        val lp = window.attributes
        lp.screenBrightness = 1.0f
        window.attributes = lp

        window.decorView.systemUiVisibility =
            View.SYSTEM_UI_FLAG_IMMERSIVE_STICKY or
                    View.SYSTEM_UI_FLAG_FULLSCREEN or
                    View.SYSTEM_UI_FLAG_HIDE_NAVIGATION

        steps = StepsRepo.loadSteps(this)
        if (steps.isEmpty()) steps = mutableListOf(Step("EMPTY", "Go to Edit Steps"))

        view = FieldTestView(this)
        setContentView(view)

        render()

        // 触摸：左半屏 prev，右半屏 next
        view.setOnTouchListener { _, event ->
            if (event.action == MotionEvent.ACTION_UP) {
                val isRight = event.x >= (view.width / 2f)
                if (isRight) next() else prev()
            }
            true
        }

        // 长按：开关分区提示（简单但实用）
        view.setOnLongClickListener {
            view.showZonesHint = !view.showZonesHint
            true
        }
    }

    override fun onResume() {
        super.onResume()
        // 从编辑器返回后刷新步骤
        steps = StepsRepo.loadSteps(this)
        if (idx >= steps.size) idx = steps.lastIndex.coerceAtLeast(0)
        render()
    }

    private fun next() {
        if (steps.isEmpty()) return
        idx = (idx + 1) % steps.size   // “旋转”：循环
        render()
    }

    private fun prev() {
        if (steps.isEmpty()) return
        idx = (idx - 1 + steps.size) % steps.size
        render()
    }

    private fun render() {
        val total = steps.size.coerceAtLeast(1)
        val safeIdx = idx.coerceIn(0, total - 1)
        view.step = steps[safeIdx]
        view.indexText = "${safeIdx + 1}/$total"
    }
}
