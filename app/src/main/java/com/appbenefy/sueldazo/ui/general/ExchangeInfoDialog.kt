package com.appbenefy.sueldazo.ui.general

import android.app.Activity
import android.content.Intent
import android.os.Bundle
import android.view.WindowManager
import com.appbenefy.sueldazo.R
import com.appbenefy.sueldazo.base.BaseActivity
import com.appbenefy.sueldazo.ui.coupon.ui.activities.RatingActivity
import kotlinx.android.synthetic.main.exchange_info_dialog.*

class ExchangeInfoDialog: BaseActivity() {

    private var code: String? = ""
    private var id: String? = ""
    private var type: Int = 0

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        this.window.setFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN, WindowManager.LayoutParams.FLAG_FULLSCREEN)
        setContentView(R.layout.exchange_info_dialog)

        val extras = intent.extras
        if (extras != null) {
            code = extras.getString("qrCode")
            id = extras.getString("productId")
            type = extras.getString("productType", "0").toInt()
        }

        close.setOnClickListener {
            val intent = Intent(this@ExchangeInfoDialog, RatingActivity::class.java)
            intent.putExtra("qrCode", code)
            intent.putExtra("couponId", id)
            intent.putExtra("productType", type)
            startActivityForResult(intent, 924)
        }

        accept.setOnClickListener {
//            callIntent<RatingActivity> (924) {
//                this.putExtra("qrCode", code)
//                this.putExtra("couponId", id)
//            }
            val intent = Intent(this@ExchangeInfoDialog, RatingActivity::class.java)
            intent.putExtra("qrCode", code)
            intent.putExtra("couponId", id)
            intent.putExtra("productType", type)
            startActivityForResult(intent, 924)
        }

    }

    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        super.onActivityResult(requestCode, resultCode, data)
        if (requestCode == 924 && resultCode == Activity.RESULT_OK) {
            val intent = Intent()
            setResult(Activity.RESULT_OK, intent)
            finish()
        }
    }

    override fun onBackPressed() {
        super.onBackPressed()
        val intent = Intent(this@ExchangeInfoDialog, RatingActivity::class.java)
        intent.putExtra("qrCode", code)
        intent.putExtra("couponId", id)
        intent.putExtra("type", type)
        startActivityForResult(intent, 924)
    }

}
