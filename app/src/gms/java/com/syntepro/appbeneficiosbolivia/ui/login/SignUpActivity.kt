package com.syntepro.appbeneficiosbolivia.ui.login

import android.app.DatePickerDialog
import android.app.NotificationManager
import android.content.Intent
import android.content.res.ColorStateList
import android.os.Build
import android.os.Bundle
import android.text.Editable
import android.text.TextWatcher
import android.util.Patterns
import android.view.View
import android.widget.AdapterView
import android.widget.ArrayAdapter
import androidx.annotation.RequiresApi
import androidx.appcompat.app.AppCompatActivity
import androidx.core.widget.addTextChangedListener
import com.github.rtoshiro.util.format.SimpleMaskFormatter
import com.github.rtoshiro.util.format.text.MaskTextWatcher
import com.google.android.gms.tasks.Task
import com.google.firebase.auth.AuthResult
import com.google.firebase.auth.FirebaseAuth
import com.syntepro.appbeneficiosbolivia.R
import com.syntepro.appbeneficiosbolivia.database.DataBaseAdapter
import com.syntepro.appbeneficiosbolivia.entity.app.Departamento
import com.syntepro.appbeneficiosbolivia.entity.app.Pais
import com.syntepro.appbeneficiosbolivia.entity.app.Provincia
import com.syntepro.appbeneficiosbolivia.entity.firebase.Usuario
import com.syntepro.appbeneficiosbolivia.entity.service.User
import com.syntepro.appbeneficiosbolivia.room.database.RoomDataBase
import com.syntepro.appbeneficiosbolivia.room.entity.CountryUser
import com.syntepro.appbeneficiosbolivia.ui.home.HomeActivity
import com.syntepro.appbeneficiosbolivia.ui.home.adapter.CustomAdapter
import com.syntepro.appbeneficiosbolivia.utils.Constants
import com.syntepro.appbeneficiosbolivia.utils.Functions.Companion.getCountryAbbreviation
import com.syntepro.appbeneficiosbolivia.utils.Functions.Companion.showError
import com.syntepro.appbeneficiosbolivia.utils.Functions.Companion.showWarning
import com.syntepro.appbeneficiosbolivia.utils.Helpers
import kotlinx.android.synthetic.gms.activity_reset_password.*
import kotlinx.android.synthetic.gms.activity_sign_up.*
import kotlinx.android.synthetic.gms.activity_sign_up.progress_circular
import java.text.DateFormat
import java.text.SimpleDateFormat
import java.util.*
import java.util.regex.Pattern

class SignUpActivity : AppCompatActivity() {

    private val adapter by lazy { DataBaseAdapter(this.applicationContext) }
    private val roomDataBase by lazy { RoomDataBase.getRoomDatabase(this@SignUpActivity) }
    private var pais: String? = null
    private var cod: String? = null
    private var abr: String? = null
    private var moneda: String? = null
    private var timeZone: String? = null
    private var mAuth: FirebaseAuth? = null
    private var contRegex = 0
    private val mCalendar = Calendar.getInstance()
    private var mDateSelected: Date? = Date()
    private val nomCountry = ArrayList<String>()
    private val flags = intArrayOf(R.drawable.sv, R.drawable.bo, R.drawable.gt)
    private val format = SimpleDateFormat("dd/MM/yyyy", Locale.getDefault())
    var isAnonymousUser = false
    var document = ""

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_sign_up)

        // Init DataBase
        adapter.createDatabase()

        val extras = intent.extras
        if (extras != null) {
            isAnonymousUser = extras.getBoolean("isAnonymousUser", false)
            document = extras.getString("documentId", "")
        }

        // Get Countries
        adapter.open()
        val country = adapter.infoCountry as List<Pais?>
        for (i in country.indices) {
            nomCountry.add(country[i]!!.nombre)
        }
        val customAdapter = CustomAdapter(this, flags, nomCountry)
        adapter.close()

        // Phone MaskH
        val smfT = SimpleMaskFormatter("NNNN-NNNN")
        val mtwT = MaskTextWatcher(edt_telefono, smfT)
        edt_telefono.addTextChangedListener(mtwT)
        edt_telefono.hint = "xxxx-xxxx"

        dateField.setOnClickListener {
            openCalendar()
        }

        setAnonymousFields(isAnonymousUser)

        button.setOnClickListener {
            val formIsValid = if (isAnonymousUser) validateAnonymousForm() else validateForm()
            if (formIsValid){
                if (isAnonymousUser) {
                    saveAnonymousData()
                    val intent = Intent(applicationContext, HomeActivity::class.java)
                    startActivity(intent)
                } else {
                    saveData()
                    val name = tl_nombre.editText?.text.toString() + tl_apellido.editText?.text.toString()
                    val intent = Intent(applicationContext, CitySelectionActivity::class.java)
                    intent.putExtra("userName", name)
                    startActivity(intent)
                }
            } else {
                showError(this, "Completa el formulario de registro con los datos requeridos.", "UPS, FALTA INFORMACIÓN")
            }
        }

    }

    override fun onDestroy() {
        RoomDataBase.destroyInstance()
        super.onDestroy()
    }

    override fun onBackPressed() {
        finish()
        super.onBackPressed()
    }

    private fun validateAnonymousForm(): Boolean{
        return validateEmail() && validateName() && validateLastName()
    }

    private fun validateForm(): Boolean {
        return validateEmail() && validateName() && validateLastName() &&validateBirthday()
    }
    /**
     * @author Gerson Aquino 19OCT2022
     *
     * This function hide fields that are not needed when user
     * is login anonymously.
     *
     * @property isAnonymous define is the user will hace to fill.
     */
    private fun setAnonymousFields(isAnonymous: Boolean){
        if (isAnonymous){
            birthdayContainer.visibility = View.GONE
            phoneContainer.visibility = View.GONE
        }
    }

    private fun updateLabel() {
        dateField.setText(Helpers.dateToStrReg(mCalendar.time, DateFormat.DATE_FIELD))
        mDateSelected = mCalendar.time
    }

    // Validate Name
    private fun validateName(): Boolean {
        val name = tl_nombre.editText?.text.toString()
        return name.isNotEmpty() && NAME_PATTERN.matcher(name).matches()
    }

    // Validate Last Name
    private fun validateLastName(): Boolean {
        val name = tl_apellido.editText?.text.toString()
        return name.isNotEmpty() && NAME_PATTERN.matcher(name).matches()
    }

    // Validate Email
    private fun validateBirthday(): Boolean {
        val birthday = dateLayout.editText?.text.toString()
        return birthday.isNotEmpty()
    }

    private fun validateEmail(): Boolean{
        val email  = tl_correo.editText?.text.toString().trim { it <= ' ' }
        return email.isNotEmpty() && Patterns.EMAIL_ADDRESS.matcher(email).matches()
    }

    private fun openCalendar() {
        DatePickerDialog(
            this, { _, year, monthOfYear, dayOfMonth ->
            mCalendar.set(Calendar.YEAR, year)
            mCalendar.set(Calendar.MONTH, monthOfYear)
            mCalendar.set(Calendar.DAY_OF_MONTH, dayOfMonth)
            updateLabel()
        }, mCalendar.get(Calendar.YEAR), mCalendar.get(Calendar.MONTH), mCalendar.get(Calendar.DAY_OF_MONTH)
        ).show()
    }

    private fun saveData(){
        val userProfile = with(User()) {
            names = tl_nombre.editText?.text.toString()
            lastNames = tl_apellido.editText?.text.toString()
            birthDate = format.format(mDateSelected ?: Date())
            email = edt_email.text.toString()
            phone = edt_telefono.text.toString().replace("-", "")
            idDocument = document
            this
        }
        Constants.userProfile = userProfile
    }

    private fun saveAnonymousData(){
        val userProfile = with(User()) {
            names = tl_nombre.editText?.text.toString()
            lastNames = tl_apellido.editText?.text.toString()
            email = edt_email.text.toString()
            this
        }
        Constants.userProfile = userProfile
    }
    // Add user to collection on Firebase
    private fun addUser(userId: String?, userEmail: String?) {
        userId?.let {
            val usr = Usuario()
            usr.nombre = tl_nombre.editText?.text.toString()
            usr.apellido = tl_apellido.editText?.text.toString()
            usr.fechaNac = format.format(mDateSelected ?: Date())
            usr.correo = userEmail
            usr.tyc = "1"
            val date = SimpleDateFormat("dd/MM/yyyy HH:mm:ss", Locale.getDefault()).format(Calendar.getInstance().time)
            usr.fechaTyc = date
            usr.numeroTel = edt_telefono.text.toString().replace("-", "")
            usr.imagenPerfil = "https://firebasestorage.googleapis.com/v0/b/beneficios-1b534.appspot.com/o/imagesProfile%2FdefaultImage.png?alt=media&token=2ee39312-4687-4d16-89ca-e929a5cf3722"
            usr.pais = pais
            usr.codigoArea = cod
            usr.abreviacion = abr
            usr.moneda = moneda
            UserData.saveFirebaseUser(this@SignUpActivity, usr, adapter, userId, Constants.PLAY_STORE) { message: String?, result: Boolean ->
                if (result) updateUI()
                else {
                    showError(this, message, "")
                    progress_circular.visibility = View.GONE
                    button.visibility = View.VISIBLE
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
        } ?: run { showError(this, "Error al agregar el usuario", "") }
    }

    // Update UI
    private fun updateUI() {
        val intent = Intent(this, ConditionsActivity::class.java)
        intent.putExtra("provenance", 0)
        startActivity(intent)
        finish()
    }

    companion object {
        private val NAME_PATTERN = Pattern.compile("^[a-zA-Z][0-9a-zA-Z .,'´ñ-]*$")
    }
}
