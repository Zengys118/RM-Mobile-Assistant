package com.example.rmmobileassistant

import android.os.Bundle
import android.view.View
import android.view.WindowManager
import androidx.appcompat.app.AppCompatActivity

class ArmorActivity : AppCompatActivity() {

    private lateinit var armorView: ArmorView

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

        armorView = ArmorView(this)
        setContentView(armorView)

        armorView.glow = 1.0f

        armorView.setOnClickListener {
            armorView.team =
                if (armorView.team == ArmorView.Team.BLUE) ArmorView.Team.RED else ArmorView.Team.BLUE
        }
    }
}
