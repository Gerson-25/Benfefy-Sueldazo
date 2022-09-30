package com.syntepro.appbeneficiosbolivia.ui.lealtad

import android.os.Bundle
import android.view.Gravity
import android.view.ViewGroup
import android.view.Window
import android.view.WindowManager
import android.widget.Button
import android.widget.LinearLayout
import android.widget.TextView
import androidx.appcompat.app.AppCompatActivity
import com.syntepro.appbeneficiosbolivia.R
import com.syntepro.appbeneficiosbolivia.base.BaseActivity
import kotlinx.android.synthetic.main.activity_finish_dialog.*

class FinishDialog : BaseActivity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        this.supportRequestWindowFeature(Window.FEATURE_NO_TITLE)
        setContentView(R.layout.activity_finish_dialog)

        // Extras
        val extras = intent.extras
        if (extras != null) { planNameId.text = extras.getString("planName") }

        closeButtonId.setOnClickListener { finish() }
    }

    companion object {
        const val FINISH_DIALOG_ID  = 502
    }
}
