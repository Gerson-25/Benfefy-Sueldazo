package com.syntepro.appbeneficiosbolivia.ui.shop.ui.activities

import android.content.Intent
import android.os.Bundle
import com.syntepro.appbeneficiosbolivia.R
import com.syntepro.appbeneficiosbolivia.base.BaseActivity
import com.syntepro.appbeneficiosbolivia.ui.home.HomeActivity
import kotlinx.android.synthetic.main.activity_success_payment.*

class SuccessPaymentActivity : BaseActivity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_success_payment)

        accept.setOnClickListener {
            val intent = Intent(this, HomeActivity::class.java)
            intent.flags = Intent.FLAG_ACTIVITY_CLEAR_TOP
            intent.putExtra("paid", true)
            startActivity(intent)
        }
    }

    override fun onBackPressed() { accept.performClick() }

}