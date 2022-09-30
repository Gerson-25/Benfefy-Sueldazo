package com.syntepro.appbeneficiosbolivia.ui.login

import android.app.DatePickerDialog
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
import com.anotherdev.firebase.auth.FirebaseAuth
import com.anotherdev.firebase.auth.FirebaseAuthRest
import com.anotherdev.firebase.auth.FirebaseUser
import com.github.rtoshiro.util.format.SimpleMaskFormatter
import com.github.rtoshiro.util.format.text.MaskTextWatcher
import com.google.firebase.FirebaseApp
import com.syntepro.appbeneficiosbolivia.R
import com.syntepro.appbeneficiosbolivia.database.DataBaseAdapter
import com.syntepro.appbeneficiosbolivia.entity.app.Departamento
import com.syntepro.appbeneficiosbolivia.entity.app.Pais
import com.syntepro.appbeneficiosbolivia.entity.app.Provincia
import com.syntepro.appbeneficiosbolivia.entity.firebase.Usuario
import com.syntepro.appbeneficiosbolivia.room.database.RoomDataBase
import com.syntepro.appbeneficiosbolivia.room.entity.CountryUser
import com.syntepro.appbeneficiosbolivia.ui.home.adapter.CustomAdapter
import com.syntepro.appbeneficiosbolivia.utils.Constants
import com.syntepro.appbeneficiosbolivia.utils.Functions
import com.syntepro.appbeneficiosbolivia.utils.Functions.Companion.getCountryAbbreviation
import com.syntepro.appbeneficiosbolivia.utils.Functions.Companion.showError
import com.syntepro.appbeneficiosbolivia.utils.Functions.Companion.showWarning
import com.syntepro.appbeneficiosbolivia.utils.Helpers
import kotlinx.android.synthetic.hms.activity_sign_up.*
import java.text.DateFormat
import java.text.SimpleDateFormat
import java.util.*
import java.util.regex.Pattern

class SignUpActivity : AppCompatActivity(), View.OnClickListener, AdapterView.OnItemSelectedListener {

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

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_sign_up)

        // Init DataBase
        adapter.createDatabase()

        // Firebase
        val app = FirebaseApp.getInstance()
        mAuth = FirebaseAuthRest.getInstance(app)

        tl_pass.editText!!.addTextChangedListener(object : TextWatcher {
            override fun beforeTextChanged(s: CharSequence, start: Int, count: Int, after: Int) {  }
            override fun onTextChanged(s: CharSequence, start: Int, before: Int, count: Int) {  }

            @RequiresApi(api = Build.VERSION_CODES.N)
            override fun afterTextChanged(s: Editable) {
                contRegex = 0
                val pass = tl_pass.editText!!.text.toString()
                if (pass.matches(".*[a-z].*".toRegex())) contRegex += 1
                if (pass.matches(".*[A-Z].*".toRegex())) contRegex += 1
                if (pass.matches(".*[0-9].*".toRegex())) contRegex += 1
                if (pass.matches(".*[$&+,:;=?@#`~!%^*()_|].*".toRegex())) contRegex += 1
                if (pass.length >= 8) contRegex += 1
                when {
                    contRegex <= 2 -> {
                        progressBarPass.progress = 33
                        progressBarPass.progressTintList = ColorStateList.valueOf(resources.getColor(R.color.red))
                        debil.text = getString(R.string.debil_pass)
                        medio.text = ""
                        fuerte.text = ""
                    }
                    contRegex == 5 -> {
                        progressBarPass.progress = 100
                        progressBarPass.progressTintList = ColorStateList.valueOf(resources.getColor(R.color.green))
                        debil.text = ""
                        medio.text = ""
                        fuerte.text = getString(R.string.fuerte_pass)
                    }
                    else -> {
                        progressBarPass.progress = 67
                        progressBarPass.progressTintList = ColorStateList.valueOf(resources.getColor(R.color.yellow))
                        debil.text = ""
                        medio.text = getString(R.string.medio_pass)
                        fuerte.text = ""
                    }
                }
            }
        })

        // Get Countries
        adapter.open()
        val country = adapter.infoCountry as List<Pais?>
        for (i in country.indices) {
            nomCountry.add(country[i]!!.nombre)
        }
        val customAdapter = CustomAdapter(this, flags, nomCountry)
        simpleSpinnerCountry.adapter = customAdapter
        adapter.close()

        // Phone Mask
        val smfT = SimpleMaskFormatter("NNNN-NNNN")
        val mtwT = MaskTextWatcher(edt_telefono, smfT)
        edt_telefono.addTextChangedListener(mtwT)
        edt_telefono.hint = "xxxx-xxxx"

        dateButtonId.setOnClickListener {
            openCalendar()
        }

        dateField.setOnClickListener {
            openCalendar()
        }

        // Gender Spinner
        val g = arrayOf(getString(R.string.male), getString(R.string.female))
        sp_genero.adapter = ArrayAdapter(this, R.layout.spinner_item, g)

        // Spinner
        val s = arrayOf(getString(R.string.soltero), getString(R.string.comprometido), getString(R.string.casado), getString(R.string.separado), getString(R.string.divorciado), getString(R.string.viudo))
        sp_estado.adapter = ArrayAdapter(this, R.layout.spinner_item, s)
        simpleSpinnerCountry.onItemSelectedListener = this
        button.setOnClickListener(this)
    }

    override fun onDestroy() {
        RoomDataBase.destroyInstance()
        super.onDestroy()
    }

    override fun onBackPressed() {
        finish()
        super.onBackPressed()
    }

    private fun updateLabel() {
        dateField.setText(Helpers.dateToStrReg(mCalendar.time, DateFormat.DATE_FIELD))
        mDateSelected = mCalendar.time
    }

    override fun onItemSelected(arg0: AdapterView<*>?, arg1: View, position: Int, id: Long) {
        arg0?.let {
            adapter.open()
            val abbreviation = getCountryAbbreviation(nomCountry[it.selectedItemPosition])
            val country = adapter.getCountryInfoAbr (abbreviation)
            val prfBO = "+" + country.codigoArea
            txt_prefijo.text = prfBO
            text_depto.text = country.depto
            txt_prov.text = country.muni
            pais = country.nombre
            cod = country.codigoArea
            abr = country.abreviacion
            moneda = country.moneda
            timeZone = country.timeZone
            val d = adapter.getInfoDepto(country.idPais) as List<Departamento?>
            getAllDeptos(d)
            adapter.close()
        }
    }

    override fun onNothingSelected(arg0: AdapterView<*>?) {
        // TODO Auto-generated method stub
    }

    // Validate Name
    private fun validateName(): Boolean {
        val name = tl_nombre.editText?.text.toString()
        return if (name.isEmpty()) {
            tl_nombre.error = getString(R.string.c_required)
            false
        } else if (!NAME_PATTERN.matcher(name).matches()) {
            tl_nombre.error = getString(R.string.f_invalid)
            false
        } else {
            tl_nombre.error = null
            true
        }
    }

    // Validate Last Name
    private fun validateLastName(): Boolean {
        val name = tl_apellido.editText?.text.toString()
        return if (name.isEmpty()) {
            tl_apellido.error = getString(R.string.c_required)
            false
        } else if (!NAME_PATTERN.matcher(name).matches()) {
            tl_apellido.error = getString(R.string.f_invalid)
            false
        } else {
            tl_apellido.error = null
            true
        }
    }

    // Validate Email
    private fun validateEmail(): Boolean {
        val email = tl_correo.editText?.text.toString().trim { it <= ' ' }
        return if (email.isEmpty()) {
            tl_correo.error = getString(R.string.c_required)
            false
        } else if (!Patterns.EMAIL_ADDRESS.matcher(email).matches()) {
            tl_correo.error = getString(R.string.e_invalid)
            false
        } else {
            tl_correo.error = null
            true
        }
    }

    // Validate Password
    private fun validatePassword(): Boolean {
        val passwordInput = tl_pass.editText?.text.toString().trim { it <= ' ' }
        return when {
            passwordInput.isEmpty() -> {
                tl_pass!!.error = getString(R.string.c_required)
                false
            }
            passwordInput.length < 6 -> {
                tl_pass!!.error = getString(R.string.min_characters)
                false
            }
            else -> {
                tl_pass!!.error = null
                true
            }
        }
    }

    private fun getAllDeptos(depto: List<Departamento?>) {
        val nomDept = ArrayList<String>()
        for (i in depto.indices) {
            nomDept.add(depto[i]!!.nombre)
        }
        sp_depto.adapter = ArrayAdapter(this@SignUpActivity, R.layout.spinner_item, nomDept)
        sp_depto.onItemSelectedListener = object : AdapterView.OnItemSelectedListener {
            override fun onItemSelected(adapterView: AdapterView<*>?, view: View, position: Int, id: Long) {
                getAllProvince(depto[position]!!.idDepartamento)
            }

            override fun onNothingSelected(parent: AdapterView<*>?) {  }
        }
    }

    private fun getAllProvince(idDepto: Int) {
        adapter.open()
        val prov = adapter.getInfoPrv(idDepto) as List<Provincia?>
        val nomProv = ArrayList<String>()
        for (i in prov.indices) { nomProv.add(prov[i]!!.nombre) }
        sp_prov.adapter = ArrayAdapter(this@SignUpActivity, R.layout.spinner_item, nomProv)
        adapter.close()
    }

    // Click Listener on Screen
    override fun onClick(v: View) {
        progress_circular.visibility = View.VISIBLE
        button.visibility = View.GONE
        when {
            !validateName() or !validateLastName() or !validateEmail() or !validatePassword() -> {
                progress_circular.visibility = View.GONE
                button.visibility = View.VISIBLE
            }
            tl_pass.editText?.text.toString() != tl_confPass.editText?.text.toString() -> {
                tl_pass.error = getString(R.string.pass_match)
                progress_circular.visibility = View.GONE
                button.visibility = View.VISIBLE
            }
            else -> {
                userRecord()
                progress_circular.visibility = View.VISIBLE
                button.visibility = View.GONE
            }
        }
    }

    // Create user with Authentication of Firebase
    private fun userRecord() {
        val email = tl_correo.editText?.text.toString()
        val password = tl_pass.editText?.text.toString()
        try {
            mAuth?.createUserWithEmailAndPassword(email, password)
                    ?.subscribe(
                            {
                                // Sign in success, update UI with the signed-in user's information
                                val user: FirebaseUser = mAuth?.currentUser!!
                                addUser(user.uid, user.email)
                            },
                            { throwable ->
                                // If sign in fails, display a message to the user.
                                val error = throwable.message
                                if (error == "No address associated with hostname") {
                                    this@SignUpActivity.runOnUiThread {
                                        tl_correo.error = getString(R.string.usuario_existente)
                                        edt_email.setText("")
                                        showWarning(this, getString(R.string.usuario_existente))
                                        progress_circular.visibility = View.GONE
                                        button.visibility = View.VISIBLE
                                    }
                                } else {
                                    this@SignUpActivity.runOnUiThread {
                                        edt_nombre.setText("")
                                        edt_apellido.setText("")
                                        edt_telefono.setText("")
                                        dateField.setText("")
                                        edt_email.setText("")
                                        edt_pass.setText("")
                                        edt_confPass.setText("")
                                        showError(this, getString(R.string.usuario_existente))
                                        progress_circular.visibility = View.GONE
                                        button.visibility = View.VISIBLE
                                    }
                                }
                            }
                    )
        }catch (e: Exception) {
            this@SignUpActivity.runOnUiThread {
                showWarning(this, getString(R.string.usuario_existente))
                progress_circular.visibility = View.GONE
                button.visibility = View.VISIBLE
            }
        }
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

    // Add user to collection on Firebase
    private fun addUser(userId: String?, userEmail: String?) {
        userId?.let {
            val usr = Usuario()
            usr.nombre = tl_nombre.editText?.text.toString()
            usr.apellido = tl_apellido.editText?.text.toString()
            usr.fechaNac = format.format(mDateSelected ?: Date())
            usr.genero = sp_genero.selectedItem.toString()
            usr.correo = userEmail
            usr.provincia = sp_prov.selectedItem.toString()
            usr.departamento = sp_depto.selectedItem.toString()
            usr.tyc = "1"
            val date = SimpleDateFormat("dd/MM/yyyy HH:mm:ss", Locale.getDefault()).format(Calendar.getInstance().time)
            usr.fechaTyc = date
            usr.estadoCivil = sp_estado.selectedItem.toString()
            usr.numeroTel = edt_telefono.text.toString().replace("-", "")
            usr.imagenPerfil = "https://firebasestorage.googleapis.com/v0/b/beneficios-1b534.appspot.com/o/imagesProfile%2FdefaultImage.png?alt=media&token=2ee39312-4687-4d16-89ca-e929a5cf3722"
            usr.pais = pais
            usr.codigoArea = cod
            usr.abreviacion = abr
            usr.moneda = moneda
            UserData.saveFirebaseUser(this@SignUpActivity, usr, adapter, userId, Constants.APP_GALLERY) { message: String?, result: Boolean ->
                if (result) updateUI()
                else {
                    showError(this, message)
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
        } ?: run { showError(this, "Error al agregar el usuario") }
    }

    // Update UI
    private fun updateUI() {
        Constants.userProfile?.let {
            if (Constants.HUAWEI_TOKEN.isNotEmpty()) {
                Functions.updateDeviceToken(
                        it.idUserFirebase ?: "",
                        it.idUser ?: "",
                        Constants.HUAWEI_TOKEN,
                        Constants.APP_GALLERY
                )
            }
        }
        val intent = Intent(this, ConditionsActivity::class.java)
        intent.putExtra("provenance", 0)
        startActivity(intent)
        finish()
    }

    companion object {
        private val NAME_PATTERN = Pattern.compile("^[a-zA-Z][0-9a-zA-Z .,'´ñ-]*$")
    }

}