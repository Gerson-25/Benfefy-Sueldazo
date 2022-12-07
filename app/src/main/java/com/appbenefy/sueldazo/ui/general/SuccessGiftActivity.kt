package com.appbenefy.sueldazo.ui.general

import android.app.Activity
import android.content.Intent
import android.os.Bundle
import android.view.WindowManager
import com.appbenefy.sueldazo.R
import com.appbenefy.sueldazo.base.BaseActivity
import com.appbenefy.sueldazo.ui.home.HomeActivity
import kotlinx.android.synthetic.main.activity_success_gift.*

class SuccessGiftActivity : BaseActivity() {

    private var giftId: String? = ""
    private var clientName: String? = ""
    private var giftCode: String? = ""

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        this.window.setFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN, WindowManager.LayoutParams.FLAG_FULLSCREEN)
        setContentView(R.layout.activity_success_gift)

        val extras = intent.extras
        if (extras != null) {
            giftId = extras.getString("giftId")
            clientName = extras.getString("clientName")
            giftCode = extras.getString("giftCode")
            val clientDescription = "${clientName?.substringBefore(" ")} ${getString(R.string.addressee_gift)}"
            giftDescription.text = clientDescription
        }

    }

    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        super.onActivityResult(requestCode, resultCode, data)
        if (requestCode == 842 && resultCode == Activity.RESULT_OK) {
            val intent = Intent(this, HomeActivity::class.java)
            intent.flags = Intent.FLAG_ACTIVITY_CLEAR_TOP
            startActivity(intent)
        }
    }


}