package com.syntepro.sueldazo.ui.coupon

import android.app.Activity
import android.content.Intent
import android.os.Bundle
import android.view.Gravity
import android.view.ViewGroup
import android.view.Window
import android.view.WindowManager
import android.widget.LinearLayout
import androidx.appcompat.app.AppCompatActivity
import com.syntepro.sueldazo.R
import kotlinx.android.synthetic.main.activity_q_r_amount.*

class QRAmountActivity : AppCompatActivity() {

    private var remaining: Int = 100

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        this.supportRequestWindowFeature(Window.FEATURE_NO_TITLE)
        setContentView(R.layout.activity_q_r_amount)
        this.setFinishOnTouchOutside(true)

        // Extras
        val extras = intent.extras
        if (extras != null) {
            val info = extras.getString("userQR","")!!
            remaining = extras.getInt("remaining")
            userQRId.text = info
        }

        amountField.setText("1")

        cancel.setOnClickListener { this.finish() }

        accept.setOnClickListener {
            when {
                amountField.text.isNullOrEmpty() -> numberLayout.error = "Campo Obligatorio."

                amountField.text.toString().toInt() > remaining -> {
                    numberLayout.error = "No posees la cantidad suficiente para canjear."
                }

                else -> {
                    val intent = Intent()
                    intent.putExtra("amount", amountField.text.toString().toInt())
                    setResult(Activity.RESULT_OK, intent)
                    this.finish()
                }
            }
        }
    }

    override fun onAttachedToWindow() {
        super.onAttachedToWindow()

        val view = window.decorView
        val lp = view.layoutParams as WindowManager.LayoutParams
        lp.gravity = Gravity.START or Gravity.CENTER
        lp.x = 0
        lp.y = 0
        lp.horizontalMargin = 0f
        lp.width = LinearLayout.LayoutParams.MATCH_PARENT
        lp.height = ViewGroup.LayoutParams.WRAP_CONTENT
        windowManager.updateViewLayout(view, lp)
    }

    override fun onDestroy() {
        super.onDestroy()
        setResult(Activity.RESULT_CANCELED)
    }

}
