package com.syntepro.appbeneficiosbolivia.ui.login

import android.content.Intent
import android.os.Bundle
import android.view.View
import androidx.appcompat.app.AppCompatActivity
import androidx.appcompat.widget.Toolbar
import com.syntepro.appbeneficiosbolivia.R
import com.syntepro.appbeneficiosbolivia.ui.extras.IntroActivity
import com.syntepro.appbeneficiosbolivia.utils.Functions.Companion.userTimeZone
import kotlinx.android.synthetic.main.activity_conditions.*
import java.io.ByteArrayOutputStream
import java.io.IOException
import java.text.SimpleDateFormat
import java.util.*

class ConditionsActivity : AppCompatActivity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_conditions)

        // Toolbar
        val myToolbar = findViewById<View>(R.id.back_toolbar) as Toolbar
        setSupportActionBar(myToolbar)
        supportActionBar!!.setDisplayHomeAsUpEnabled(true)
        supportActionBar!!.setDisplayShowHomeEnabled(true)
        supportActionBar!!.title = resources.getString(R.string.terminos_y_condiciones_menu)

        // Extras
        val extras = intent.extras
        if (extras != null) {
            val provenance = extras.getInt("provenance")
            if (provenance == 1) checkbox.visibility = View.GONE
            else checkbox.setOnCheckedChangeListener { _, isChecked -> next.visibility = if (isChecked) View.VISIBLE else View.GONE }
        }

        // Get User Timezone
        val tz = TimeZone.getTimeZone(userTimeZone)
        val c = Calendar.getInstance(tz)
        val date = SimpleDateFormat("dd/MM/yyyy HH:mm:ss", Locale.getDefault()).format(c.timeInMillis)

        val conditions = readFileFromRawDirectory()
        conditionsId.text = conditions

        next.setOnClickListener { updateData(date) }
    }

    override fun onSupportNavigateUp(): Boolean {
        onBackPressed()
        return true
    }

    private fun readFileFromRawDirectory(): String? {
        val iStream = applicationContext.resources.openRawResource(R.raw.terminos_bo)
        var byteStream: ByteArrayOutputStream? = null
        try {
            val buffer = ByteArray(iStream.available())
            iStream.read(buffer)
            byteStream = ByteArrayOutputStream()
            byteStream.write(buffer)
            byteStream.close()
            iStream.close()
        } catch (e: IOException) { e.printStackTrace() }
        return Objects.requireNonNull(byteStream).toString()
    }

    private fun updateData(date: String) {
        val intent = Intent(this@ConditionsActivity, IntroActivity::class.java)
        startActivity(intent)
    }

}