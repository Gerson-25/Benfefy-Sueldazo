package com.syntepro.sueldazo.ui.shop.ui.activities

import android.app.Activity
import android.content.Intent
import android.os.Bundle
import android.util.Log
import android.view.View
import android.webkit.WebChromeClient
import android.webkit.WebView
import android.webkit.WebViewClient
import androidx.appcompat.app.AppCompatActivity
import androidx.appcompat.widget.Toolbar
import com.syntepro.sueldazo.R
import com.syntepro.sueldazo.utils.Constants
import kotlinx.android.synthetic.main.activity_payment_order.*

class PaymentOrderActivity : AppCompatActivity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_payment_order)

        // Toolbar
        val myToolbar = findViewById<View>(R.id.back_toolbar) as Toolbar
        setSupportActionBar(myToolbar)
        supportActionBar!!.title = getString(R.string.purchase_order)
        supportActionBar!!.setDisplayHomeAsUpEnabled(true)
        supportActionBar!!.setDisplayShowHomeEnabled(true)

        // Extras
        val extras = intent.extras
        if (extras != null) {
            val url = extras.getString("url")
            showData(url)
        }

        // Configuration
        webView.settings.javaScriptEnabled = true
    }

    override fun onSupportNavigateUp(): Boolean {
        onBackPressed()
        return true
    }

    private fun showData(url: String?) {
        if (url.isNullOrEmpty()) return
        webView.loadUrl(url)
        val agent = "Mozilla/5.0 (Macintosh; Intel Mac OS X 10_11_3) " +
                "AppleWebKit/537.36 (KHTML, like Gecko) Chrome/48.0.2564.109 Safari/537.36"
        webView.webViewClient = WebViewClient()

        webView.webChromeClient = object : WebChromeClient() {
            override fun onProgressChanged(view: WebView, progress: Int) {
                view.url?.let {
                    Log.e("Actual URL", "${view.url}") // Here you get url
                    if (it == Constants.SUCCESS_TRANSACTION) {
                        val intent = Intent(this@PaymentOrderActivity, SuccessPaymentActivity::class.java)
                        startActivity(intent)
                        finish()
                    } else if (it == Constants.FAILED_TRANSACTION) {
                        val intent = Intent()
                        intent.putExtra("error", true)
                        setResult(Activity.RESULT_CANCELED, intent)
                        finish()
                    }
                }
            }
        }
        webView.settings.userAgentString = agent
    }

}