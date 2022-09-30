package com.syntepro.appbeneficiosbolivia.ui.menu

import android.os.Bundle
import android.view.View
import android.widget.ArrayAdapter
import androidx.appcompat.app.AppCompatActivity
import androidx.appcompat.widget.Toolbar
import com.syntepro.appbeneficiosbolivia.R
import com.syntepro.appbeneficiosbolivia.room.database.RoomDataBase
import com.syntepro.appbeneficiosbolivia.room.entity.UserConfiguration
import com.syntepro.appbeneficiosbolivia.utils.Functions.Companion.showInformation
import kotlinx.android.synthetic.main.activity_configuracion.*
import java.util.*

class ConfigurationActivity : AppCompatActivity(), View.OnClickListener {

    private val roomDataBase by lazy { RoomDataBase.getRoomDatabase(this) }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_configuracion)

        // Toolbar
        val myToolbar = findViewById<Toolbar>(R.id.mainToolbar)
        setSupportActionBar(myToolbar)
        supportActionBar!!.setDisplayHomeAsUpEnabled(true)
        supportActionBar!!.setDisplayShowHomeEnabled(true)
        supportActionBar!!.title = resources.getString(R.string.configuracion_conf)

        // User Configuration
        val userConfiguration = roomDataBase.accessDao().configuration

        // Switch Coupon
        switchNotiCupon.isChecked = userConfiguration.isNuevoCupon

        // Switch Route
        switchRuta.isChecked = userConfiguration.isRuta

        // Switch Vigencia
        switchVenc.isChecked = userConfiguration.isProximos

        // Spinner NumberNotifications
        val number = arrayOf("1", "2", "3", "4", "5", "6", "7", "8", "9", "10")
        spinnerNoti.adapter = ArrayAdapter(this, R.layout.spinner_item, number)
        val notifications = userConfiguration.numNotificaciones
        spinnerNoti.setSelection(notifications - 1)

        //Spinner Language
        val language = arrayOf("Español")
        spinnerIdioma.adapter = ArrayAdapter(this, R.layout.spinner_item, language)
        infoCupon.setOnClickListener(this)
        infoRuta.setOnClickListener(this)
        infoVenc.setOnClickListener(this)
    }

    override fun onDestroy() {
        RoomDataBase.destroyInstance()
        super.onDestroy()
    }

    override fun onSupportNavigateUp(): Boolean {
        onBackPressed()
        return super.onSupportNavigateUp()
    }

    override fun onBackPressed() {
        super.onBackPressed()
        val ids = TimeZone.getAvailableIDs()
        val tz = TimeZone.getTimeZone(ids[144])
        val c = Calendar.getInstance(tz)
        roomDataBase!!.accessDao().dropConfiguration()
        val userConfiguration = UserConfiguration()
        userConfiguration.id = 1
        userConfiguration.fechaModificacion = c.time
        userConfiguration.isNuevoCupon = switchNotiCupon!!.isChecked
        userConfiguration.isRuta = switchRuta!!.isChecked
        userConfiguration.isProximos = switchVenc!!.isChecked
        val number = spinnerNoti!!.selectedItemPosition
        userConfiguration.numNotificaciones = number + 1
        userConfiguration.idioma = "es"
        roomDataBase!!.accessDao().addUserConfiguration(userConfiguration)
        finish()
    }

    override fun onClick(v: View) {
        when (v.id) {
            R.id.infoCupon -> showAlertDialog(getString(R.string.permitir_notificaciones), getString(R.string.infoCupon))
            R.id.infoRuta -> showAlertDialog(getString(R.string.permitir_notificaciones), getString(R.string.infoRuta))
            R.id.infoVenc -> showAlertDialog(getString(R.string.permitir_notificaciones), getString(R.string.infoVenc))
        }
    }

    private fun showAlertDialog(title: String, message: String) {
        showInformation(this, title, message)
    }
}