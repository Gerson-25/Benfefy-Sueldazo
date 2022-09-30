package com.syntepro.appbeneficiosbolivia.ui.login

import android.app.Activity
import android.content.Intent
import android.os.Bundle
import android.view.View
import android.widget.AdapterView
import androidx.appcompat.app.AppCompatActivity
import com.facebook.login.LoginManager
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.firestore.FirebaseFirestore
import com.syntepro.appbeneficiosbolivia.R
import com.syntepro.appbeneficiosbolivia.database.DataBaseAdapter
import com.syntepro.appbeneficiosbolivia.entity.app.Pais
import com.syntepro.appbeneficiosbolivia.entity.app.SocialUser
import com.syntepro.appbeneficiosbolivia.entity.firebase.Usuario
import com.syntepro.appbeneficiosbolivia.room.database.RoomDataBase
import com.syntepro.appbeneficiosbolivia.room.entity.CountryUser
import com.syntepro.appbeneficiosbolivia.ui.home.adapter.CustomAdapter
import com.syntepro.appbeneficiosbolivia.utils.Functions
import kotlinx.android.synthetic.main.activity_country_selection.*
import java.text.SimpleDateFormat
import java.util.*

class CountrySelectionActivity : AppCompatActivity(), AdapterView.OnItemSelectedListener {

    private val adapter by lazy { DataBaseAdapter(this.applicationContext) }
    private val roomDataBase by lazy { RoomDataBase.getRoomDatabase(this@CountrySelectionActivity) }
    private var db: FirebaseFirestore? = null
    private var pais: String? = null
    private var cod: String? = null
    private var abr: String? = null
    private var moneda: String? = null
    private var timeZone: String? = null
    private var country: List<Pais?>? = null
    private var userId: String? = null
    private var data: SocialUser? = null
    private var store: Int = 3
    private val flags = intArrayOf(R.drawable.sv, R.drawable.bo, R.drawable.gt)

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_country_selection)

        // Firebase
        db = FirebaseFirestore.getInstance()

        // Initialize SQLite
        adapter.createDatabase()

        // Parameters
        val extras = intent.extras
        if (extras != null) {
            userId = extras.getString("userId")
            data = extras.getSerializable("model") as SocialUser
            store = extras.getInt("registerStore")
        }

        // Get Countries
        adapter.open()
        country = ArrayList()
        country = adapter.infoCountry as List<Pais?>
        val nomCountry = ArrayList<String>()
        for (i in country!!.indices) { nomCountry.add(country!![i]?.nombre ?: "-") }
        val customAdapter = CustomAdapter(applicationContext, flags, nomCountry)
        simpleSpinnerCountry.adapter = customAdapter
        adapter.close()
        simpleSpinnerCountry.onItemSelectedListener = this

        btn_Aceptar.setOnClickListener {
            btn_Aceptar.text = ""
            progressBar.visibility = View.VISIBLE
            if (data?.email.isNullOrEmpty()) {
                val intent = Intent(this@CountrySelectionActivity, EmailConfirmationActivity::class.java)
                intent.putExtra("model", data)
                startActivityForResult(intent, 420)
            } else updateCountryUser()
        }
    }

    override fun onDestroy() {
        RoomDataBase.destroyInstance()
        super.onDestroy()
    }

    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        super.onActivityResult(requestCode, resultCode, data)
        if (requestCode == 420 && resultCode == Activity.RESULT_OK) {
            val updateData = data?.getSerializableExtra("model") as? SocialUser
            this.data = updateData
            updateCountryUser()
        } else {
            btn_Aceptar.text = getString(R.string.accept)
            progressBar.visibility = View.INVISIBLE
        }
    }

    override fun onItemSelected(arg0: AdapterView<*>?, arg1: View, position: Int, id: Long) {
        adapter.open()
        val countrySelectedSpinner = adapter.getCountryConfiguration(country!![position]!!.nombre)
        pais = countrySelectedSpinner.nombre
        cod = countrySelectedSpinner.codigoArea
        abr = countrySelectedSpinner.abreviacion
        moneda = countrySelectedSpinner.moneda
        timeZone = countrySelectedSpinner.timeZone
        adapter.close()
    }

    override fun onNothingSelected(arg0: AdapterView<*>?) {
        // TODO Auto-generated method stub
    }

    private fun updateCountryUser() {
        val calendar = Calendar.getInstance()
        val date = SimpleDateFormat("dd/MM/yyyy HH:mm:ss", Locale.getDefault()).format(calendar.timeInMillis)

        val usr = Usuario()
        usr.nombre = data?.name
        usr.correo = data?.email
        data?.phone?.let { usr.numeroTel = data?.phone?.replace("-", "") } ?: run { usr.numeroTel = "" }
        usr.imagenPerfil = data?.image
        usr.pais = pais
        usr.codigoArea = cod
        usr.abreviacion = abr
        usr.moneda = moneda
        usr.tyc = "1"
        usr.fechaTyc = date

        UserData.saveFirebaseUser(this@CountrySelectionActivity, usr, adapter, userId ?: "", store) { message: String, result: Boolean ->
            if (result) {
                val intent = Intent(applicationContext, ConditionsActivity::class.java)
                intent.putExtra("provenance", 0)
                startActivity(intent)
                finish()
            } else {
                LoginManager.getInstance().logOut()
                FirebaseAuth.getInstance().signOut()
                Functions.showWarning(this, message)
                btn_Aceptar.text = "Aceptar"
                progressBar.visibility = View.GONE
            }
        }

        val country = roomDataBase!!.accessDao().country
        if (country == null) {
            val ids = TimeZone.getAvailableIDs()
            val tz = TimeZone.getTimeZone(ids[144])
            val c = Calendar.getInstance(tz)
            val countryUser = CountryUser()
            countryUser.id = 1
            countryUser.fechaActualizacion = c.time
            countryUser.pais = pais
            countryUser.codArea = cod
            countryUser.abr = abr
            countryUser.moneda = moneda
            countryUser.timeZone = timeZone
            roomDataBase!!.accessDao().addCountryUser(countryUser)
        } else {
            roomDataBase!!.accessDao().dropCountry()
            val ids = TimeZone.getAvailableIDs()
            val tz = TimeZone.getTimeZone(ids[144])
            val c = Calendar.getInstance(tz)
            val countryUser = CountryUser()
            countryUser.id = 1
            countryUser.fechaActualizacion = c.time
            countryUser.pais = pais
            countryUser.codArea = cod
            countryUser.abr = abr
            countryUser.moneda = moneda
            countryUser.timeZone = timeZone
            roomDataBase!!.accessDao().addCountryUser(countryUser)
        }
    }

}