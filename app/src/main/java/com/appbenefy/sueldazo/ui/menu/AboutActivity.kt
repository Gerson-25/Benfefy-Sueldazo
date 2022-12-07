package com.appbenefy.sueldazo.ui.menu

import android.content.pm.PackageInfo
import android.os.Bundle
import android.text.Html
import android.view.View
import androidx.appcompat.app.AppCompatActivity
import androidx.appcompat.widget.Toolbar
import com.appbenefy.sueldazo.R
import kotlinx.android.synthetic.main.activity_about.*

class AboutActivity : AppCompatActivity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_about)

        // Toolbar
        val myToolbar = findViewById<View>(R.id.back_toolbar) as Toolbar
        setSupportActionBar(myToolbar)
        supportActionBar!!.setDisplayHomeAsUpEnabled(true)
        supportActionBar!!.setDisplayShowHomeEnabled(true)
        supportActionBar!!.title = resources.getString(R.string.acerca_de)

        versionId.text = "v " + getVersionName()

        /**
         * Gerson Aquino 28JUN2021
         *
         * source string was fixed to get string from
         * string resources and fix the <b> tag
         *
         */

        val sourceString = getString(R.string.about_first) + "<b> " + getString(R.string.about_second) + "</b> "
        aboutId.text = Html.fromHtml(sourceString)
    }

    override fun onSupportNavigateUp(): Boolean {
        onBackPressed()
        return true
    }

    override fun onBackPressed() {
        this.finish()
        super.onBackPressed()
    }

    private fun getVersionName(): String {
        val pInfo: PackageInfo = this@AboutActivity.packageManager.getPackageInfo(packageName, 0)
        return pInfo.versionName
    }


}