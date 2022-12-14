package com.appbenefy.sueldazo.ui.login

import android.app.Activity
import android.content.Intent
import android.os.Bundle
import android.view.*
import android.widget.AdapterView
import android.widget.LinearLayout
import androidx.appcompat.app.AppCompatActivity
import com.google.firebase.firestore.FirebaseFirestore
import com.appbenefy.sueldazo.R
import com.appbenefy.sueldazo.database.DataBaseAdapter
import com.appbenefy.sueldazo.entity.app.Departamento
import com.appbenefy.sueldazo.entity.app.Pais
import com.appbenefy.sueldazo.entity.app.SocialUser
import com.appbenefy.sueldazo.entity.firebase.Usuario
import com.appbenefy.sueldazo.room.database.RoomDataBase
import com.appbenefy.sueldazo.room.entity.CountryUser
import com.appbenefy.sueldazo.ui.home.HomeActivity
import com.appbenefy.sueldazo.ui.home.adapter.CustomAdapter
import com.appbenefy.sueldazo.utils.Constants
import com.appbenefy.sueldazo.utils.UserType
import kotlinx.android.synthetic.main.activity_country_selection.*
import java.text.SimpleDateFormat
import java.util.*

class CitySelectionActivity : AppCompatActivity(), AdapterView.OnItemSelectedListener {

    private val adapter by lazy { DataBaseAdapter(this.applicationContext) }
    private val roomDataBase by lazy { RoomDataBase.getRoomDatabase(this@CitySelectionActivity) }
    private var db: FirebaseFirestore? = null
    private var pais: String? = null
    private var cod: String? = null
    private var abr: String? = null
    private var moneda: String? = null
    private var timeZone: String? = null
    private var country: List<Pais?>? = null
    private var username: String? = null
    private var data: SocialUser? = null
    private var store: Int = 3
    private val flags = intArrayOf(R.drawable.sv, R.drawable.bo, R.drawable.gt)

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        this.supportRequestWindowFeature(Window.FEATURE_NO_TITLE)
        setContentView(R.layout.activity_country_selection)

        // Firebase
        db = FirebaseFirestore.getInstance()

        // Initialize SQLite
        adapter.createDatabase()

        // Parameters
        val extras = intent.extras
        if (extras != null) {
            username = extras.getString("userName")
        }

        // Get Cities
        adapter.open()
        country = ArrayList()
        country = adapter.infoCountry as List<Pais?>
        val country = country?.filter { it?.abreviacion == "BO" }
        val deptos = adapter.getInfoDepto(country?.get(0)?.idPais?:0) as List<Departamento?>

        val nomDept = ArrayList<String>()
        for (i in deptos.indices) {
            nomDept.add(deptos[i]!!.nombre)
        }

        succesDisclaimer.text = "Hola ${username}, tus datos se validaron correctamente"

        val customAdapter = CustomAdapter(applicationContext, flags, nomDept)
        simpleSpinnerCountry.adapter = customAdapter
        adapter.close()
        simpleSpinnerCountry.onItemSelectedListener = this

        btn_Aceptar.setOnClickListener {
            Constants.TYPE_OF_USER = UserType.VERIFIED_USER
            val intent = Intent(this, HomeActivity::class.java)
            startActivity(intent)
        }

        closeSelectCountry.setOnClickListener {
            finish()
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

    override fun onItemSelected(arg0: AdapterView<*>?, arg1: View?, position: Int, id: Long) {
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

}