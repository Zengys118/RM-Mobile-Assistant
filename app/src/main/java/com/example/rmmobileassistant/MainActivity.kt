package com.example.rmmobileassistant

import android.content.Intent
import android.os.Bundle
import android.view.View
import androidx.appcompat.app.AppCompatActivity
import androidx.cardview.widget.CardView
import androidx.core.view.ViewCompat
import androidx.core.view.WindowInsetsCompat

class MainActivity : AppCompatActivity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)
        enableImmersiveSwipe()
        applyInsetsPadding(R.id.root)
        findViewById<CardView>(R.id.cardArmor).setOnClickListener {
            startActivity(Intent(this, ArmorActivity::class.java))
        }

        findViewById<CardView>(R.id.cardFieldTest).setOnClickListener {
            startActivity(Intent(this, FieldTestActivity::class.java))
        }

        findViewById<CardView>(R.id.cardEditor).setOnClickListener {
            startActivity(Intent(this, StepEditorActivity::class.java))
        }
    }
    override fun onResume() {
        super.onResume()
        enableImmersiveSwipe()
    }

    override fun onWindowFocusChanged(hasFocus: Boolean) {
        super.onWindowFocusChanged(hasFocus)
        if (hasFocus) enableImmersiveSwipe()
    }

    private fun enableImmersiveSwipe() {
        window.setDecorFitsSystemWindows(false)
        val controller = window.insetsController
        controller?.hide(
            android.view.WindowInsets.Type.statusBars() or
                    android.view.WindowInsets.Type.navigationBars()
        )
        controller?.systemBarsBehavior =
            android.view.WindowInsetsController.BEHAVIOR_SHOW_TRANSIENT_BARS_BY_SWIPE

        @Suppress("DEPRECATION")
        window.decorView.systemUiVisibility =
            View.SYSTEM_UI_FLAG_LAYOUT_STABLE or
                    View.SYSTEM_UI_FLAG_LAYOUT_HIDE_NAVIGATION or
                    View.SYSTEM_UI_FLAG_LAYOUT_FULLSCREEN or
                    View.SYSTEM_UI_FLAG_HIDE_NAVIGATION or
                    View.SYSTEM_UI_FLAG_FULLSCREEN or
                    View.SYSTEM_UI_FLAG_IMMERSIVE_STICKY
    }

    private fun applyInsetsPadding(rootId: Int) {
        val root = findViewById<View>(rootId)
        ViewCompat.setOnApplyWindowInsetsListener(root) { v, insets ->
            val sb = insets.getInsets(WindowInsetsCompat.Type.systemBars())

            val extraTop = (20 * resources.displayMetrics.density).toInt() // 10dp，可改 8/12
            v.setPadding(
                sb.left,
                sb.top + extraTop,   // 👈 这里加
                sb.right,
                sb.bottom
            )
            insets
        }
    }

}
